package com.devvault.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InsightsServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 14);

    @Test
    void aTopicIsWeakOnlyAfterEnoughAnswersAndLowAccuracy() {
        assertThat(InsightsService.stat("Arrays", 2, 0).weak()).isFalse();      // too few answers to judge
        assertThat(InsightsService.stat("Arrays", 4, 2).weak()).isTrue();       // 50%
        assertThat(InsightsService.stat("Arrays", 10, 7).weak()).isFalse();     // exactly 70% is fine
        assertThat(InsightsService.stat("Arrays", 10, 6).accuracyPercent()).isEqualTo(60);
    }

    @Test
    void currentStreakCountsBackFromTodayAndSurvivesUntilTheDayEnds() {
        Map<LocalDate, Integer> days = Map.of(TODAY, 2, TODAY.minusDays(1), 1, TODAY.minusDays(2), 5);
        assertThat(InsightsService.streaks(days, TODAY)[0]).isEqualTo(3);

        Map<LocalDate, Integer> notYetToday = Map.of(TODAY.minusDays(1), 1, TODAY.minusDays(2), 1);
        assertThat(InsightsService.streaks(notYetToday, TODAY)[0]).isEqualTo(2);

        Map<LocalDate, Integer> broken = Map.of(TODAY.minusDays(3), 1);
        assertThat(InsightsService.streaks(broken, TODAY)[0]).isZero();
    }

    @Test
    void longestStreakFindsTheBestRun() {
        Map<LocalDate, Integer> days = Map.of(
                TODAY.minusDays(10), 1, TODAY.minusDays(9), 1, TODAY.minusDays(8), 1, TODAY.minusDays(7), 1,
                TODAY, 1);
        int[] streaks = InsightsService.streaks(days, TODAY);
        assertThat(streaks[0]).isEqualTo(1);
        assertThat(streaks[1]).isEqualTo(4);
    }
}
