package com.devvault.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterTest {

    private static final long HOUR = 60 * 60 * 1000L;

    @Test
    void allowsUpToTheLimitThenBlocksUntilTheWindowPasses() {
        RateLimiter limiter = new RateLimiter(2);

        assertThat(limiter.tryAcquire("a", 0)).isTrue();
        assertThat(limiter.tryAcquire("a", 1)).isTrue();
        assertThat(limiter.tryAcquire("a", 2)).isFalse();
        assertThat(limiter.tryAcquire("a", HOUR + 1)).isTrue();        // the first request has left the window
    }

    @Test
    void usersAreCountedSeparately() {
        RateLimiter limiter = new RateLimiter(1);

        assertThat(limiter.tryAcquire("a", 0)).isTrue();
        assertThat(limiter.tryAcquire("b", 0)).isTrue();
        assertThat(limiter.tryAcquire("a", 1)).isFalse();
    }
}
