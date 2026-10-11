package com.devvault.drill;

import java.util.List;

/** A generated, auto-graded practice question with a stable id (1 to 10,000). */
public record Drill(int id, String familyKey, String familyName, String topic, String group, String difficulty,
                    String title, String question, String code, Spec.Kind kind, String answer,
                    List<String> choices, String explanation, boolean runnable) {
}
