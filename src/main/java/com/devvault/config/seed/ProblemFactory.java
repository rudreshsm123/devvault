package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.List;

/** Small helper so the seed lists stay readable. Each problem carries a beginner and an intermediate solution. */
final class ProblemFactory {

    private ProblemFactory() {
    }

    static void add(List<CodingProblem> out, String title, String topic, Difficulty difficulty, String tags,
                    String description, String patternHint, String beginnerCode, String intermediateCode,
                    String explanation, String time, String space, String mistakes) {
        CodingProblem p = new CodingProblem();
        p.setTitle(title);
        p.setTopic(topic);
        p.setDifficulty(difficulty);
        p.setTags(tags);
        p.setDescription(description.strip());
        p.setPatternHint(patternHint.strip());
        p.setExplanation(explanation.strip());
        p.setTimeComplexity(time);
        p.setSpaceComplexity(space);
        p.setCommonMistakes(mistakes.strip());
        p.setCompanyTags(topic.startsWith("Website")
                ? "Models a real feature of a production website; typical product-company interview exercise."
                : "Common interview question at product and service companies.");
        StringBuilder code = new StringBuilder();
        if (beginnerCode != null) {
            code.append("// ===== BEGINNER SOLUTION (simple and easy to reason about) =====\n")
                    .append(beginnerCode.strip()).append("\n\n");
        }
        if (intermediateCode != null) {
            code.append(beginnerCode == null
                    ? "// ===== OPTIMAL SOLUTION =====\n"
                    : "// ===== INTERMEDIATE SOLUTION (optimized) =====\n")
                    .append(intermediateCode.strip());
        }
        p.setSolutionCode(code.toString().strip());
        out.add(p);
    }
}
