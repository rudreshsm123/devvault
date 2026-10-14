package com.devvault.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Sliding-window limit per user, so one account cannot run up the AI bill. In memory, so it resets on restart. */
@Component
public class RateLimiter {

    private static final long WINDOW_MILLIS = 60 * 60 * 1000L;

    private final int maxPerHour;
    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    public RateLimiter(@Value("${devvault.ai.max-requests-per-hour:20}") int maxPerHour) {
        this.maxPerHour = maxPerHour;
    }

    /** @return true and records the request when the user is under the limit, false when they must wait */
    public boolean tryAcquire(String user, long now) {
        Deque<Long> times = hits.computeIfAbsent(user, u -> new ArrayDeque<>());
        synchronized (times) {
            while (!times.isEmpty() && times.peekFirst() <= now - WINDOW_MILLIS) {
                times.pollFirst();
            }
            if (times.size() >= maxPerHour) {
                return false;
            }
            times.addLast(now);
            return true;
        }
    }
}
