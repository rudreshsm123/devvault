package com.devvault.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginThrottleTest {

    private static final long MINUTE = 60_000L;
    private static final long WINDOW = 15 * MINUTE;

    private static LoginThrottle throttle() {
        return new LoginThrottle(5, 30, WINDOW);
    }

    private static void fail(LoginThrottle t, String ip, String user, int times, long at) {
        for (int i = 0; i < times; i++) {
            t.recordFailure(ip, user, at);
        }
    }

    @Test
    void notBlockedUntilTheLimitIsReached() {
        LoginThrottle t = throttle();

        fail(t, "1.1.1.1", "rajesh", 4, 0);
        assertThat(t.isBlocked("1.1.1.1", "rajesh", 1)).isFalse();

        fail(t, "1.1.1.1", "rajesh", 1, 2);
        assertThat(t.isBlocked("1.1.1.1", "rajesh", 3)).isTrue();
    }

    @Test
    void blockingOneUserFromOneAddressDoesNotBlockOtherUsersOrOtherAddresses() {
        LoginThrottle t = throttle();
        fail(t, "1.1.1.1", "rajesh", 5, 0);

        assertThat(t.isBlocked("1.1.1.1", "someone-else", 1)).isFalse();
        assertThat(t.isBlocked("2.2.2.2", "rajesh", 1)).isFalse();      // a stranger cannot lock the owner out elsewhere
    }

    @Test
    void usernamesAreComparedIgnoringCaseAndSurroundingSpaces() {
        LoginThrottle t = throttle();
        fail(t, "1.1.1.1", "Rajesh", 3, 0);
        fail(t, "1.1.1.1", "  rajesh ", 2, 1);

        assertThat(t.isBlocked("1.1.1.1", "RAJESH", 2)).isTrue();
    }

    @Test
    void oneAddressTryingManyUsernamesIsStoppedByTheAddressLimit() {
        LoginThrottle t = throttle();
        for (int i = 0; i < 30; i++) {
            t.recordFailure("9.9.9.9", "user" + i, i);
        }

        assertThat(t.isBlocked("9.9.9.9", "a-brand-new-name", 31)).isTrue();
        assertThat(t.isBlocked("8.8.8.8", "a-brand-new-name", 31)).isFalse();
    }

    @Test
    void failuresAgeOutOfTheWindow() {
        LoginThrottle t = throttle();
        fail(t, "1.1.1.1", "rajesh", 5, 0);

        assertThat(t.isBlocked("1.1.1.1", "rajesh", WINDOW - 1)).isTrue();
        assertThat(t.isBlocked("1.1.1.1", "rajesh", WINDOW + 1)).isFalse();
    }

    @Test
    void retryAfterCountsDownToTheOldestFailureLeavingTheWindow() {
        LoginThrottle t = throttle();
        fail(t, "1.1.1.1", "rajesh", 5, 0);

        assertThat(t.retryAfterSeconds("1.1.1.1", "rajesh", 5 * MINUTE)).isEqualTo(10 * 60);
        assertThat(t.retryAfterSeconds("1.1.1.1", "rajesh", WINDOW + 1)).isZero();
        assertThat(t.retryAfterSeconds("1.1.1.1", "nobody", 5 * MINUTE)).isZero();
    }

    @Test
    void aSuccessfulSignInClearsThatUsersFailures() {
        LoginThrottle t = throttle();
        fail(t, "1.1.1.1", "rajesh", 4, 0);

        t.recordSuccess("1.1.1.1", "rajesh");
        fail(t, "1.1.1.1", "rajesh", 4, 1);

        assertThat(t.isBlocked("1.1.1.1", "rajesh", 2)).isFalse();
    }

    @Test
    void aFloodOfDistinctUsernamesCannotGrowMemoryWithoutBound() {
        LoginThrottle t = new LoginThrottle(5, Integer.MAX_VALUE, WINDOW);
        for (int i = 0; i < LoginThrottle.MAX_KEYS + 5_000; i++) {
            t.recordFailure("7.7.7.7", "name" + i, 0);
        }

        // Still working, and an existing, already tracked username keeps being counted.
        t.recordFailure("7.7.7.7", "name0", 1);
        assertThat(t.isBlocked("7.7.7.7", "name0", 2)).isFalse();
    }
}
