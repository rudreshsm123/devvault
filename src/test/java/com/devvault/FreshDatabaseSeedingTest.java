package com.devvault;

import com.devvault.repository.CodeChallengeRepository;
import com.devvault.repository.LessonRepository;
import com.devvault.repository.ProblemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Boots the application exactly like the public demo does: an empty in-memory database that is filled by the
 * seeders. Guards against ordering mistakes (for example a challenge looking for a problem that is created later).
 */
@SpringBootTest
@ActiveProfiles("cloud")
class FreshDatabaseSeedingTest {

    @Autowired
    private ProblemRepository problems;

    @Autowired
    private CodeChallengeRepository challenges;

    @Autowired
    private LessonRepository lessons;

    @Test
    void anEmptyDatabaseEndsUpFullyPopulated() {
        assertThat(problems.count()).isEqualTo(331);
        assertThat(lessons.count()).isEqualTo(60);
        assertThat(challenges.count()).isEqualTo(30);
    }

    @Test
    void everyProblemHasALineByLineExplanation() {
        var missing = problems.findAll().stream()
                .filter(p -> p.getLineByLine() == null || p.getLineByLine().isBlank())
                .map(p -> p.getTitle())
                .toList();

        assertThat(missing).as("problems without a line-by-line explanation").isEmpty();
    }

    @Test
    void theOriginalStarterProblemsGotTheirChallengesToo() {
        for (String title : new String[] {"Two Sum", "Valid Parentheses", "Missing Number", "Merge Intervals",
                "Maximum Subarray (Kadane's Algorithm)"}) {
            Long id = problems.findFirstByTitleIgnoreCase(title).orElseThrow().getId();
            assertThat(challenges.findByProblemId(id)).as(title).isPresent();
        }
    }
}
