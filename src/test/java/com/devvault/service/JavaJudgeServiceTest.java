package com.devvault.service;

import com.devvault.config.seed.ChallengeSeedData;
import com.devvault.entity.CodeChallenge;
import com.devvault.service.JavaJudgeService.Verdict;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs the real Java judge (compiler plus a separate JVM). Every seeded challenge is checked with a reference
 * solution, which proves that the test cases themselves are correct.
 */
class JavaJudgeServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final JavaJudgeService judge = new JavaJudgeService(true, mapper);

    private static Map<String, String> references() throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        try (InputStream in = JavaJudgeServiceTest.class.getResourceAsStream("/judge/references.java.txt")) {
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            for (String block : text.split("(?m)^//// ")) {
                if (block.isBlank()) {
                    continue;
                }
                int nl = block.indexOf('\n');
                map.put(block.substring(0, nl).trim(), block.substring(nl + 1));
            }
        }
        return map;
    }

    private static CodeChallenge challenge(ChallengeSeedData.Def def) {
        return new CodeChallenge(1L, def.functionName(), def.jsStarter(), def.javaStarter(),
                new ObjectMapper().readTree(def.testsJson()).toString());
    }

    private static ChallengeSeedData.Def def(String function) {
        return ChallengeSeedData.all().stream().filter(d -> d.functionName().equals(function)).findFirst().orElseThrow();
    }

    private static void requireJdk() {
        Assumptions.assumeTrue(ToolProvider.getSystemJavaCompiler() != null, "a JDK is needed to run this test");
    }

    @Test
    void everySeededChallengeIsAcceptedWithItsReferenceSolution() throws IOException {
        requireJdk();
        Map<String, String> refs = references();
        List<ChallengeSeedData.Def> defs = ChallengeSeedData.all();
        assertThat(defs).hasSize(30);
        assertThat(refs.keySet()).containsExactlyInAnyOrderElementsOf(defs.stream().map(ChallengeSeedData.Def::functionName).toList());

        for (ChallengeSeedData.Def def : defs) {
            Verdict v = judge.judge(challenge(def), refs.get(def.functionName()));
            assertThat(v.status()).as(def.problemTitle() + " -> " + v.message() + " " + v.results()).isEqualTo("ACCEPTED");
            assertThat(v.passed()).isEqualTo(v.total());
        }
    }

    @Test
    void starterCodeCompilesAndFailsInsteadOfCrashing() {
        requireJdk();
        for (ChallengeSeedData.Def def : ChallengeSeedData.all()) {
            Verdict v = judge.judge(challenge(def), def.javaStarter());
            assertThat(v.status()).as(def.problemTitle() + " " + v.message()).isIn("WRONG_ANSWER", "ACCEPTED");
        }
    }

    @Test
    void wrongAnswerShowsWhichTestsFailed() {
        requireJdk();
        String code = "class Solution { public int digitSum(int n) { return n; } }";

        Verdict v = judge.judge(challenge(def("digitSum")), code);

        assertThat(v.status()).isEqualTo("WRONG_ANSWER");
        assertThat(v.passed()).isLessThan(v.total());
        assertThat(v.results().get(0).input()).isEqualTo("[4821]");
        assertThat(v.results().get(0).expected()).isEqualTo("15");
        assertThat(v.results().get(0).actual()).isEqualTo("4821");
    }

    @Test
    void compileErrorsAreReportedWithTheLineNumber() {
        requireJdk();
        Verdict v = judge.judge(challenge(def("digitSum")),
                "class Solution {\n  public int digitSum(int n) {\n    return n +;\n  }\n}");

        assertThat(v.status()).isEqualTo("COMPILE_ERROR");
        assertThat(v.message()).contains("line 3");
    }

    @Test
    void exceptionsInTheLearnersCodeBecomeRuntimeErrors() {
        requireJdk();
        Verdict v = judge.judge(challenge(def("findMax")),
                "class Solution { public int findMax(int[] nums) { return nums[nums.length]; } }");

        assertThat(v.status()).isEqualTo("RUNTIME_ERROR");
        assertThat(v.results().get(0).error()).contains("ArrayIndexOutOfBoundsException");
    }

    @Test
    void anEndlessLoopIsStoppedAsTimeLimitExceeded() {
        requireJdk();
        long started = System.currentTimeMillis();

        Verdict v = judge.judge(challenge(def("digitSum")),
                "class Solution { public int digitSum(int n) { while (true) { } } }");

        assertThat(v.status()).isEqualTo("TIME_LIMIT");
        assertThat(System.currentTimeMillis() - started).isLessThan(9_000);
    }

    @Test
    void printingInsideTheSolutionDoesNotBreakTheVerdict() throws IOException {
        requireJdk();
        String code = references().get("digitSum").replace("int s = 0;", "int s = 0; System.out.println(\"noise\");");

        assertThat(judge.judge(challenge(def("digitSum")), code).status()).isEqualTo("ACCEPTED");
    }

    @Test
    void calledMethodMustExistAndCodeMustDefineClassSolution() {
        requireJdk();
        assertThat(judge.judge(challenge(def("digitSum")), "class Solution { public int other(int n) { return n; } }").status())
                .isEqualTo("RUNTIME_ERROR");
        assertThat(judge.judge(challenge(def("digitSum")), "class Other { }").status()).isEqualTo("INVALID");
        assertThat(judge.judge(challenge(def("digitSum")), "  ").status()).isEqualTo("INVALID");
        assertThat(judge.judge(challenge(def("digitSum")), "class Solution {" + " ".repeat(JavaJudgeService.MAX_CODE_CHARS) + "}").status())
                .isEqualTo("INVALID");
    }

    @Test
    void whenSwitchedOffNothingIsCompiledOrRun() {
        JavaJudgeService off = new JavaJudgeService(false, mapper);

        Verdict v = off.judge(null, "class Solution { }");

        assertThat(v.status()).isEqualTo("DISABLED");
        assertThat(off.isEnabled()).isFalse();
    }
}
