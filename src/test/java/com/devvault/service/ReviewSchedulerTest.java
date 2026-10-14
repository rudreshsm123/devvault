package com.devvault.service;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewSchedulerTest {

    @Test
    void rightAnswersClimbOneBoxAtATimeAndStopAtTheTop() {
        assertThat(ReviewScheduler.nextBox(0, true)).isEqualTo(1);
        assertThat(ReviewScheduler.nextBox(3, true)).isEqualTo(4);
        assertThat(ReviewScheduler.nextBox(ReviewScheduler.MAX_BOX, true)).isEqualTo(ReviewScheduler.MAX_BOX);
    }

    @Test
    void aWrongAnswerSendsTheItemBackToBoxZero() {
        assertThat(ReviewScheduler.nextBox(4, false)).isZero();
    }

    @Test
    void intervalsGrowAndAreClamped() {
        assertThat(ReviewScheduler.intervalDays(0)).isEqualTo(1);
        assertThat(ReviewScheduler.intervalDays(1)).isEqualTo(2);
        assertThat(ReviewScheduler.intervalDays(5)).isEqualTo(32);
        assertThat(ReviewScheduler.intervalDays(99)).isEqualTo(32);
        assertThat(ReviewScheduler.intervalDays(-3)).isEqualTo(1);
    }

    @Test
    void dueAtAddsTheBoxIntervalToNow() {
        long now = 1_000_000L;
        assertThat(ReviewScheduler.dueAt(2, now)).isEqualTo(now + TimeUnit.DAYS.toMillis(4));
    }
}
