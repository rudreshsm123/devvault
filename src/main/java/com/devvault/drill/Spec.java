package com.devvault.drill;

import java.util.List;

/**
 * One generated question before it gets an id: the text, an optional code listing, the exact answer and an
 * explanation. {@code runnable} means {@code code} is a complete program whose output is the answer (the tests
 * compile and run those programs to prove the answers are right).
 */
public record Spec(String title, String question, String code, Kind kind, String answer, List<String> choices,
                   String explanation, boolean runnable) {

    /** How the learner answers. */
    public enum Kind { OUTPUT, NUMBER, CHOICE }

    /** The exact text the program prints (OUTPUT), compared ignoring extra white space. */
    public static Spec output(String title, String question, String code, String answer, String explanation,
                              boolean runnable) {
        return new Spec(title, question, code == null ? "" : code, Kind.OUTPUT, answer, List.of(), explanation, runnable);
    }

    /** A whole-number answer. */
    public static Spec number(String title, String question, String code, long answer, String explanation,
                              boolean runnable) {
        return new Spec(title, question, code == null ? "" : code, Kind.NUMBER, Long.toString(answer), List.of(),
                explanation, runnable);
    }

    public static Spec number(String title, String question, String code, long answer, String explanation) {
        return number(title, question, code, answer, explanation, false);
    }

    /** Pick one of the choices; the answer must be one of them. */
    public static Spec choice(String title, String question, String code, String answer, List<String> choices,
                              String explanation, boolean runnable) {
        if (!choices.contains(answer)) {
            throw new IllegalArgumentException("answer is not one of the choices: " + answer);
        }
        return new Spec(title, question, code == null ? "" : code, Kind.CHOICE, answer, List.copyOf(choices),
                explanation, runnable);
    }
}
