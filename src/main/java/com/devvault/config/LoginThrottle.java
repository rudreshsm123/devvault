package com.devvault.config;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Slows down password guessing. Failed sign-ins are counted in a sliding window, and further attempts are refused
 * (even with the right password) until old failures age out. Two limits apply:
 * <ul>
 *   <li>per address <b>and</b> username, so guessing one account is stopped quickly, while a stranger cannot lock
 *       the real owner out from a different address;</li>
 *   <li>per address alone, so one address cannot spray guesses over many usernames.</li>
 * </ul>
 * State is in memory (it resets on restart) and the class has no Spring dependency, so it is unit tested directly.
 */
public class LoginThrottle {

    static final int MAX_KEYS = 20_000;
    static final int MAX_USERNAME_CHARS = 100;

    private final int maxPerUser;
    private final int maxPerAddress;
    private final long windowMillis;
    private final Map<String, Deque<Long>> failures = new HashMap<>();

    public LoginThrottle(int maxPerUser, int maxPerAddress, long windowMillis) {
        this.maxPerUser = maxPerUser;
        this.maxPerAddress = maxPerAddress;
        this.windowMillis = windowMillis;
    }

    public synchronized boolean isBlocked(String address, String username, long now) {
        return count(userKey(address, username), now) >= maxPerUser || count(addressKey(address), now) >= maxPerAddress;
    }

    /** Seconds until the oldest counted failure leaves the window, or 0 when not blocked. */
    public synchronized long retryAfterSeconds(String address, String username, long now) {
        long wait = 0;
        if (count(userKey(address, username), now) >= maxPerUser) {
            wait = Math.max(wait, oldest(userKey(address, username)) + windowMillis - now);
        }
        if (count(addressKey(address), now) >= maxPerAddress) {
            wait = Math.max(wait, oldest(addressKey(address)) + windowMillis - now);
        }
        return wait <= 0 ? 0 : (wait + 999) / 1000;
    }

    public synchronized void recordFailure(String address, String username, long now) {
        if (failures.size() >= MAX_KEYS) {
            sweep(now);
        }
        add(addressKey(address), now);
        // If an attacker floods us with distinct usernames, stop tracking new ones rather than grow without bound;
        // the per-address limit above still applies.
        String userKey = userKey(address, username);
        if (failures.containsKey(userKey) || failures.size() < MAX_KEYS) {
            add(userKey, now);
        }
    }

    /** A successful sign-in clears that user's failures from this address (the address-wide count is kept). */
    public synchronized void recordSuccess(String address, String username) {
        failures.remove(userKey(address, username));
    }

    private int count(String key, long now) {
        Deque<Long> times = failures.get(key);
        if (times == null) {
            return 0;
        }
        while (!times.isEmpty() && times.peekFirst() <= now - windowMillis) {
            times.pollFirst();
        }
        if (times.isEmpty()) {
            failures.remove(key);
            return 0;
        }
        return times.size();
    }

    private long oldest(String key) {
        Deque<Long> times = failures.get(key);
        return times == null || times.isEmpty() ? 0 : times.peekFirst();
    }

    private void add(String key, long now) {
        failures.computeIfAbsent(key, k -> new ArrayDeque<>()).addLast(now);
    }

    private void sweep(long now) {
        Iterator<Map.Entry<String, Deque<Long>>> it = failures.entrySet().iterator();
        while (it.hasNext()) {
            Deque<Long> times = it.next().getValue();
            while (!times.isEmpty() && times.peekFirst() <= now - windowMillis) {
                times.pollFirst();
            }
            if (times.isEmpty()) {
                it.remove();
            }
        }
    }

    private static String addressKey(String address) {
        return "a|" + address;
    }

    private static String userKey(String address, String username) {
        String name = username == null ? "" : username.strip().toLowerCase(Locale.ROOT);
        if (name.length() > MAX_USERNAME_CHARS) {
            name = name.substring(0, MAX_USERNAME_CHARS);
        }
        return "u|" + address + "|" + name;
    }
}
