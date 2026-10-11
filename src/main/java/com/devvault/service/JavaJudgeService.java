package com.devvault.service;

import com.devvault.entity.CodeChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Compiles the learner's Java class and runs it against the challenge tests in a separate JVM process.
 *
 * <p>This executes code typed by whoever calls the endpoint, so it is switched OFF by default
 * ({@code devvault.judge.java.enabled=false}). Turn it on only for a copy that runs on your own computer.
 * Safeguards: size limit, at most two judgements at a time, a heap limit, a per-test and an overall timeout,
 * and the temporary folder is always deleted. A separate process is NOT a full sandbox (it can still read files
 * or use the network), which is why it must not be exposed to strangers.
 */
@Service
public class JavaJudgeService {

    public record TestResult(boolean ok, String input, String expected, String actual, String error, boolean timeout) {
    }

    /** status: ACCEPTED, WRONG_ANSWER, RUNTIME_ERROR, TIME_LIMIT, COMPILE_ERROR, DISABLED, BUSY or INVALID. */
    public record Verdict(String status, String message, int passed, int total, List<TestResult> results) {
        static Verdict of(String status, String message) {
            return new Verdict(status, message, 0, 0, List.of());
        }
    }

    static final int MAX_CODE_CHARS = 20_000;
    private static final long TOTAL_TIMEOUT_SECONDS = 10;

    private final boolean enabled;
    private final ObjectMapper mapper;
    private final Semaphore slots = new Semaphore(2);

    public JavaJudgeService(@Value("${devvault.judge.java.enabled:false}") boolean enabled, ObjectMapper mapper) {
        this.enabled = enabled;
        this.mapper = mapper;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Verdict judge(CodeChallenge challenge, String code) {
        if (!enabled) {
            return Verdict.of("DISABLED", "The Java judge is switched off on this server. Run DevVault on your own "
                    + "computer and set devvault.judge.java.enabled=true to use it.");
        }
        if (code == null || code.isBlank() || code.length() > MAX_CODE_CHARS) {
            return Verdict.of("INVALID", "Write your solution first (at most " + MAX_CODE_CHARS + " characters).");
        }
        if (!code.contains("class Solution")) {
            return Verdict.of("INVALID", "Your code must define a class named Solution, as in the starter code.");
        }
        if (!slots.tryAcquire()) {
            return Verdict.of("BUSY", "The judge is busy. Please try again in a few seconds.");
        }
        Path dir = null;
        try {
            dir = Files.createTempDirectory("devvault-judge");
            return run(challenge, code, dir);
        } catch (IOException e) {
            return Verdict.of("INVALID", "Could not prepare the judge: " + e.getMessage());
        } finally {
            slots.release();
            deleteQuietly(dir);
        }
    }

    private Verdict run(CodeChallenge challenge, String code, Path dir) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return Verdict.of("INVALID", "This Java installation has no compiler (a JDK is required to judge Java).");
        }
        Files.writeString(dir.resolve("Solution.java"), code, StandardCharsets.UTF_8);
        Files.writeString(dir.resolve("JudgeRunner.java"), runnerSource(), StandardCharsets.UTF_8);
        Files.writeString(dir.resolve("tests.json"), challenge.getTestsJson(), StandardCharsets.UTF_8);

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        boolean compiled;
        try (StandardJavaFileManager files = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
            var units = files.getJavaFileObjects(dir.resolve("Solution.java"), dir.resolve("JudgeRunner.java"));
            compiled = compiler.getTask(null, files, diagnostics,
                    List.of("-proc:none", "-nowarn", "-d", dir.toString()), null, units).call();
        }
        if (!compiled) {
            return Verdict.of("COMPILE_ERROR", compileMessages(diagnostics));
        }

        Process process = new ProcessBuilder(javaExecutable(), "-Xmx128m", "-Xss1m", "-XX:+UseSerialGC",
                "-XX:TieredStopAtLevel=1", "-Djava.awt.headless=true", "-cp", dir.toString(),
                "JudgeRunner", challenge.getFunctionName())
                .directory(dir.toFile())
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start();
        try {
            if (!process.waitFor(TOTAL_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return Verdict.of("TIME_LIMIT", "Time limit exceeded: the program ran for more than "
                        + TOTAL_TIMEOUT_SECONDS + " seconds.");
            }
        } catch (InterruptedException e) {
            process.destroyForcibly();
            Thread.currentThread().interrupt();
            return Verdict.of("INVALID", "The judge was interrupted.");
        }

        Path resultFile = dir.resolve("result.json");
        if (!Files.exists(resultFile)) {
            return Verdict.of("RUNTIME_ERROR", "The program stopped without a result "
                    + "(for example it ran out of memory or called System.exit).");
        }
        return verdict(challenge, mapper.readTree(Files.readString(resultFile)));
    }

    private Verdict verdict(CodeChallenge challenge, JsonNode result) {
        if (result.has("fatal")) {
            return Verdict.of("RUNTIME_ERROR", result.get("fatal").asString());
        }
        JsonNode tests = mapper.readTree(challenge.getTestsJson());
        JsonNode ran = result.get("results");
        List<TestResult> results = new ArrayList<>();
        int passed = 0;
        boolean timeout = false;
        String firstFailure = null;
        for (int i = 0; i < tests.size(); i++) {
            JsonNode test = tests.get(i);
            String input = test.get("args").toString();
            String expected = test.get("expected").toString();
            if (i >= ran.size()) {
                results.add(new TestResult(false, input, expected, "", "not run", false));
                continue;
            }
            JsonNode r = ran.get(i);
            boolean ok = r.get("ok").asBoolean();
            boolean timedOut = r.get("timeout").asBoolean();
            String error = r.get("error").asString();
            results.add(new TestResult(ok, input, expected, r.get("actual").asString(), error, timedOut));
            if (ok) {
                passed++;
            } else if (firstFailure == null) {
                firstFailure = timedOut ? "TIME_LIMIT" : error.isEmpty() ? "WRONG_ANSWER" : "RUNTIME_ERROR";
            }
            timeout |= timedOut;
        }
        String status = firstFailure == null ? "ACCEPTED" : firstFailure;
        return new Verdict(status, "", passed, tests.size(), results);
    }

    private static String compileMessages(DiagnosticCollector<JavaFileObject> diagnostics) {
        StringBuilder sb = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            if (d.getKind() == Diagnostic.Kind.ERROR) {
                sb.append("line ").append(d.getLineNumber()).append(": ").append(d.getMessage(null)).append('\n');
            }
        }
        return sb.toString().strip();
    }

    private static String javaExecutable() {
        return Path.of(System.getProperty("java.home"), "bin", "java").toString();
    }

    private static String runnerSource() {
        try (InputStream in = JavaJudgeService.class.getResourceAsStream("/judge/JudgeRunner.java.txt")) {
            if (in == null) {
                throw new IllegalStateException("judge/JudgeRunner.java.txt is missing");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void deleteQuietly(Path dir) {
        if (dir == null) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        } catch (IOException ignored) {
            // The temp folder is cleaned by the OS eventually.
        }
    }
}
