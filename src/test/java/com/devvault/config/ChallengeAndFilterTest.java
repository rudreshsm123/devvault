package com.devvault.config;

import com.devvault.config.seed.BasicsProblemsA;
import com.devvault.config.seed.BasicsProblemsB;
import com.devvault.config.seed.BeginnerProblems;
import com.devvault.config.seed.ChallengeSeedData;
import com.devvault.config.seed.IntermediateProblems;
import com.devvault.config.seed.HardProblems;
import com.devvault.entity.CodeChallenge;
import com.devvault.entity.CodingProblem;
import com.devvault.repository.CodeChallengeRepository;
import com.devvault.service.ChallengeService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ChallengeAndFilterTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void everyChallengeBelongsToARealProblemAndHasWellFormedTests() {
        List<CodingProblem> problems = new ArrayList<>();
        problems.addAll(BeginnerProblems.all());
        problems.addAll(BasicsProblemsA.all());
        problems.addAll(BasicsProblemsB.all());
        problems.addAll(IntermediateProblems.all());
        problems.addAll(HardProblems.all());
        Set<String> titles = new HashSet<>();
        problems.forEach(p -> titles.add(p.getTitle().toLowerCase(Locale.ROOT)));
        // problems created by the very first seeder are not listed in the seed classes
        titles.addAll(List.of("two sum", "valid parentheses", "maximum subarray (kadane's algorithm)", "missing number",
                "merge intervals"));

        Set<String> functions = new HashSet<>();
        Set<String> challengeTitles = new HashSet<>();
        for (ChallengeSeedData.Def def : ChallengeSeedData.all()) {
            assertThat(titles).as("unknown problem " + def.problemTitle()).contains(def.problemTitle().toLowerCase(Locale.ROOT));
            assertThat(functions.add(def.functionName())).as("duplicate function " + def.functionName()).isTrue();
            assertThat(challengeTitles.add(def.problemTitle())).as("duplicate problem " + def.problemTitle()).isTrue();

            JsonNode tests = mapper.readTree(def.testsJson());
            assertThat(tests.isArray()).isTrue();
            assertThat(tests.size()).as(def.problemTitle()).isGreaterThanOrEqualTo(4);
            int params = def.javaParams().split(",").length;
            for (JsonNode test : tests) {
                assertThat(test.get("args").size()).as(def.problemTitle() + " argument count").isEqualTo(params);
                assertThat(test.has("expected")).isTrue();
            }
            assertThat(def.jsStarter()).startsWith("function " + def.functionName() + "(");
            assertThat(def.javaStarter()).contains("class Solution").contains(" " + def.functionName() + "(");
        }
    }

    @Test
    void challengeJsonForThePageContainsEverythingTheScriptNeeds() {
        ChallengeSeedData.Def def = ChallengeSeedData.all().get(0);
        ChallengeService service = new ChallengeService(mock(CodeChallengeRepository.class), mapper);
        CodeChallenge challenge = new CodeChallenge(7L, def.functionName(), def.jsStarter(), def.javaStarter(),
                mapper.readTree(def.testsJson()).toString());

        JsonNode json = mapper.readTree(service.clientJson(challenge, false, true));

        assertThat(json.get("problemId").asLong()).isEqualTo(7L);
        assertThat(json.get("functionName").asString()).isEqualTo("twoSum");
        assertThat(json.get("tests").isArray()).isTrue();
        assertThat(json.get("javaEnabled").asBoolean()).isFalse();
        assertThat(json.get("canSetStatus").asBoolean()).isTrue();
        assertThat(json.get("jsStarter").asString()).contains("function twoSum(nums, target)");
    }

    private MockHttpServletResponse run(String method, String uri) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        new ReadOnlyFilter().doFilter(request, response, chain);
        if (response.getStatus() == 200) {
            verify(chain).doFilter(any(), any());
        } else {
            verify(chain, never()).doFilter(any(), any());
        }
        return response;
    }

    @Test
    void readOnlyModeStillAllowsPracticeAccountsOwnProgressAndTheJudge() throws Exception {
        for (String uri : List.of("/problems/5/attempt", "/problems/5/attempt/clear", "/register",
                "/api/me/progress/toggle", "/api/me/progress/result", "/api/me/progress/import", "/api/judge/java", "/api/drills/5/check")) {
            assertThat(run("POST", uri).getStatus()).as(uri).isEqualTo(200);
        }
        assertThat(run("GET", "/problems").getStatus()).isEqualTo(200);
    }

    @Test
    void readOnlyModeBlocksEveryContentChange() throws Exception {
        for (String uri : List.of("/snippets", "/problems/5/status", "/problems/5/delete", "/notes/1/edit", "/resources")) {
            assertThat(run("POST", uri).getStatus()).as(uri).isEqualTo(403);
        }
        assertThat(run("DELETE", "/api/notes/1").getStatus()).isEqualTo(403);
        assertThat(run("PUT", "/api/problems/1").getStatus()).isEqualTo(403);
    }
}
