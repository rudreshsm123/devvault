package com.devvault.service;

import java.util.concurrent.TimeUnit;

/**
 * Leitner-box spaced repetition. A right answer moves an item one box up, so it comes back after a longer gap;
 * a wrong answer sends it back to box 0, so it comes back tomorrow.
 */
public final class ReviewScheduler {

    /** Days until the next review for boxes 0 to 5. */
    private static final int[] INTERVAL_DAYS = {1, 2, 4, 8, 16, 32};

    public static final int MAX_BOX = INTERVAL_DAYS.length - 1;

    private ReviewScheduler() {
    }

    /** The box an item moves to after an answer. {@code currentBox} may be negative or too big; it is clamped. */
    public static int nextBox(int currentBox, boolean correct) {
        if (!correct) {
            return 0;
        }
        return Math.min(Math.max(currentBox, 0) + 1, MAX_BOX);
    }

    /** Epoch milliseconds when an item that now sits in {@code box} is due for review again. */
    public static long dueAt(int box, long now) {
        int clamped = Math.min(Math.max(box, 0), MAX_BOX);
        return now + TimeUnit.DAYS.toMillis(INTERVAL_DAYS[clamped]);
    }

    public static int intervalDays(int box) {
        return INTERVAL_DAYS[Math.min(Math.max(box, 0), MAX_BOX)];
    }
}
