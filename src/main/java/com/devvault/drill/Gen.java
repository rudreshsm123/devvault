package com.devvault.drill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Small helpers shared by the question generators. */
final class Gen {

    private Gen() {
    }

    /** Random integer in [lo, hi]. */
    static int ri(Random r, int lo, int hi) {
        return lo + r.nextInt(hi - lo + 1);
    }

    @SafeVarargs
    static <T> T pick(Random r, T... items) {
        return items[r.nextInt(items.length)];
    }

    /** A complete runnable program with the given statements inside main. */
    static String program(String imports, String statements) {
        return programWith(imports, "", statements);
    }

    /** A complete runnable program with extra members (helper methods or fields) above main. */
    static String programWith(String imports, String members, String statements) {
        StringBuilder sb = new StringBuilder();
        if (!imports.isEmpty()) {
            sb.append(imports).append("\n\n");
        }
        sb.append("public class Main {\n");
        if (!members.isEmpty()) {
            for (String line : members.split("\n")) {
                sb.append("    ").append(line).append('\n');
            }
            sb.append('\n');
        }
        sb.append("    public static void main(String[] args) {\n");
        for (String line : statements.split("\n")) {
            sb.append("        ").append(line).append('\n');
        }
        sb.append("    }\n}");
        return sb.toString();
    }

    static String join(int[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }

    static String arrayLiteral(int[] values) {
        return "{" + join(values) + "}";
    }

    static int[] randomArray(Random r, int n, int lo, int hi) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = ri(r, lo, hi);
        }
        return a;
    }

    /** The correct answer plus distractors, in a random order. */
    static List<String> shuffled(Random r, String correct, List<String> others) {
        List<String> all = new ArrayList<>(others);
        if (!all.contains(correct)) {
            all.add(correct);
        }
        Collections.shuffle(all, r);
        return all;
    }

    static final String[] WORDS = {"programming", "developer", "algorithm", "database", "interface", "recursion",
            "compiler", "variable", "function", "framework", "keyboard", "network", "backend", "frontend", "testing",
            "debugging", "package", "pointer", "session", "request", "response", "server", "cluster", "vault",
            "thread", "memory", "iterator", "abstract", "inherit", "polymorphism"};
}
