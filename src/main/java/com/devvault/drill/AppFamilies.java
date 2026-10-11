package com.devvault.drill;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.devvault.drill.Gen.pick;
import static com.devvault.drill.Gen.ri;
import static com.devvault.drill.Gen.shuffled;

/** Interview-style application questions: SQL, HTTP, caching, dates, pagination, rate limiting, regex. */
final class AppFamilies {

    private AppFamilies() {
    }

    static List<Family> all() {
        List<Family> l = new ArrayList<>();
        l.add(new Family("lru-hits", "Caching", "LRU cache hits", "systems", 3, AppFamilies::lru));
        l.add(new Family("fifo-faults", "Caching", "FIFO page faults", "systems", 2, AppFamilies::fifo));
        l.add(new Family("pagination", "Web APIs", "Pagination maths", "web", 3, AppFamilies::pagination));
        l.add(new Family("rate-limit", "Systems Design", "Fixed-window rate limit", "systems", 3, AppFamilies::rateLimit));
        l.add(new Family("http-status", "Web APIs", "HTTP status codes", "web", 3, AppFamilies::httpStatus));
        l.add(new Family("sql-agg", "SQL", "SQL aggregate result", "sql", 3, AppFamilies::sqlAgg));
        l.add(new Family("date-math", "Dates", "Calendar arithmetic", "dates", 3, AppFamilies::dateMath));
        l.add(new Family("regex-count", "Strings", "Regex matches", "strings", 3, AppFamilies::regexCount));
        l.add(new Family("money", "Money", "Discount and tax maths", "math", 3, AppFamilies::money));
        l.add(new Family("scheduling", "Systems", "Round-robin scheduling", "systems", 2, AppFamilies::roundRobin));
        return l;
    }

    static Spec lru(Random r, int tier) {
        int cap = ri(r, 2, 3);
        int n = ri(r, 8, tier == 0 ? 9 : 14);
        int[] req = new int[n];
        for (int i = 0; i < n; i++) req[i] = ri(r, 1, cap + 2);
        LinkedHashSet<Integer> cache = new LinkedHashSet<>();
        int hits = 0;
        for (int x : req) {
            if (cache.remove(x)) hits++;
            else if (cache.size() == cap) cache.remove(cache.iterator().next());
            cache.add(x);
        }
        return Spec.number("LRU cache hits", "An LRU cache holds " + cap + " items and starts empty. Requests arrive for keys " + java.util.Arrays.toString(req)
                + ". When the cache is full the least recently USED key is evicted (both reads and inserts count as use). How many requests are hits?", "", hits,
                "Keep keys ordered by recency: a hit moves the key to the newest end, a miss inserts it and evicts the oldest if full. Total hits: " + hits + ".");
    }

    static Spec fifo(Random r, int tier) {
        int frames = ri(r, 3, 4);
        int n = ri(r, 9, 14);
        int[] req = new int[n];
        for (int i = 0; i < n; i++) req[i] = ri(r, 1, 6);
        java.util.ArrayDeque<Integer> mem = new java.util.ArrayDeque<>();
        int faults = 0;
        for (int x : req) {
            if (!mem.contains(x)) {
                faults++;
                if (mem.size() == frames) mem.poll();
                mem.add(x);
            }
        }
        return Spec.number("FIFO page faults", "Memory has " + frames + " frames (empty at the start) and uses FIFO replacement (the page that has been in memory longest is replaced; a hit does not change the order). "
                + "How many page faults for the page requests " + java.util.Arrays.toString(req) + "?", "", faults,
                "A fault happens when the page is not already in a frame. Simulate the queue step by step: " + faults + " faults.");
    }

    static Spec pagination(Random r, int tier) {
        int total = ri(r, 11, 995), size = pick(r, 5, 10, 12, 20, 25, 50);
        int p = ri(r, 0, 2);
        int pages = (total + size - 1) / size;
        if (p == 0) {
            return Spec.number("Pagination maths", "An API returns " + total + " records with a page size of " + size + ". How many pages are there?", "", pages,
                    "Round up: ceil(" + total + " / " + size + ") = " + pages + ".");
        } else if (p == 1) {
            int page = ri(r, 1, pages);
            int offset = (page - 1) * size;
            return Spec.number("Pagination maths", "With a page size of " + size + ", what OFFSET does a SQL query need to fetch page number " + page + " (pages start at 1)?", "", offset,
                    "offset = (page - 1) * pageSize = " + (page - 1) + " * " + size + " = " + offset + ".");
        } else {
            int page = pages;
            int count = total - (pages - 1) * size;
            return Spec.number("Pagination maths", "There are " + total + " records and the page size is " + size + ". How many records are on the last page (page " + page + ")?", "", count,
                    "All pages but the last are full: " + total + " - " + (pages - 1) + " * " + size + " = " + count + ".");
        }
    }

    static Spec rateLimit(Random r, int tier) {
        int limit = ri(r, 2, 5), window = pick(r, 5, 10, 60);
        int n = ri(r, 8, 12);
        int t = 0;
        int[] times = new int[n];
        for (int i = 0; i < n; i++) { t += ri(r, 0, window / 2 + 1); times[i] = t; }
        int allowed = 0, curWindow = -1, used = 0;
        for (int x : times) {
            int w = x / window;
            if (w != curWindow) { curWindow = w; used = 0; }
            if (used < limit) { used++; allowed++; }
        }
        return Spec.number("Fixed-window rate limit", "A rate limiter allows at most " + limit + " requests per fixed window of " + window + " seconds (windows are [0," + window + "), [" + window + "," + (2 * window)
                + "), ...). Requests arrive at seconds " + java.util.Arrays.toString(times) + ". How many are allowed?", "", allowed,
                "Group the requests by window (second / " + window + "). In each window only the first " + limit + " pass. Total allowed: " + allowed + ".");
    }

    private static final Map<Integer, String> STATUS = new LinkedHashMap<>();
    static {
        STATUS.put(200, "OK - the request succeeded");
        STATUS.put(201, "Created - a new resource was created");
        STATUS.put(204, "No Content - success with nothing to return");
        STATUS.put(301, "Moved Permanently - the URL has a new permanent address");
        STATUS.put(304, "Not Modified - the cached copy is still valid");
        STATUS.put(400, "Bad Request - the request is malformed or invalid");
        STATUS.put(401, "Unauthorized - the caller is not authenticated");
        STATUS.put(403, "Forbidden - authenticated but not allowed");
        STATUS.put(404, "Not Found - the resource does not exist");
        STATUS.put(405, "Method Not Allowed - wrong HTTP verb for this URL");
        STATUS.put(409, "Conflict - clashes with the current state, e.g. a duplicate");
        STATUS.put(422, "Unprocessable Entity - well-formed but fails validation");
        STATUS.put(429, "Too Many Requests - rate limit exceeded");
        STATUS.put(500, "Internal Server Error - unexpected failure on the server");
        STATUS.put(502, "Bad Gateway - an upstream server sent a bad response");
        STATUS.put(503, "Service Unavailable - the server is overloaded or down");
        STATUS.put(504, "Gateway Timeout - an upstream server did not answer in time");
    }

    static Spec httpStatus(Random r, int tier) {
        List<Integer> codes = new ArrayList<>(STATUS.keySet());
        int code = codes.get(r.nextInt(codes.size()));
        boolean forward = r.nextBoolean();
        String meaning = STATUS.get(code);
        String name = meaning.substring(0, meaning.indexOf(" - "));
        if (forward) {
            List<String> names = new ArrayList<>();
            for (int c : codes) if (c != code) names.add(STATUS.get(c).substring(0, STATUS.get(c).indexOf(" - ")));
            java.util.Collections.shuffle(names, r);
            return Spec.choice("HTTP status codes", "Which name goes with HTTP status " + code + "?", "", name, shuffled(r, name, names.subList(0, 3)),
                    code + " is " + meaning + ".", false);
        }
        List<String> options = new ArrayList<>();
        for (int c : codes) if (c != code) options.add(String.valueOf(c));
        java.util.Collections.shuffle(options, r);
        return Spec.choice("HTTP status codes", "Which status code means \"" + name + "\"?", "", String.valueOf(code), shuffled(r, String.valueOf(code), options.subList(0, 3)),
                code + " is " + meaning + ".", false);
    }

    static Spec sqlAgg(Random r, int tier) {
        String[] depts = {"IT", "HR", "Sales", "Ops"};
        int rows = ri(r, 5, 8);
        StringBuilder table = new StringBuilder("employees(name, dept, salary)\n");
        String[] dept = new String[rows];
        int[] sal = new int[rows];
        for (int i = 0; i < rows; i++) {
            dept[i] = depts[r.nextInt(depts.length)];
            sal[i] = ri(r, 3, 15) * 1000;
            table.append("('E").append(i + 1).append("', '").append(dept[i]).append("', ").append(sal[i]).append(")\n");
        }
        String d = dept[r.nextInt(rows)];
        int p = ri(r, 0, 3);
        long ans = 0;
        String query;
        int cnt = 0;
        long sum = 0, max = 0;
        for (int i = 0; i < rows; i++) if (dept[i].equals(d)) { cnt++; sum += sal[i]; max = Math.max(max, sal[i]); }
        int threshold = ri(r, 5, 10) * 1000;
        switch (p) {
            case 0 -> { query = "SELECT COUNT(*) FROM employees WHERE dept = '" + d + "';"; ans = cnt; }
            case 1 -> { query = "SELECT SUM(salary) FROM employees WHERE dept = '" + d + "';"; ans = sum; }
            case 2 -> { query = "SELECT MAX(salary) FROM employees WHERE dept = '" + d + "';"; ans = max; }
            default -> { query = "SELECT COUNT(*) FROM employees WHERE salary > " + threshold + ";"; for (int s : sal) if (s > threshold) ans++; }
        }
        return Spec.number("SQL aggregate result", "Given these rows:", table + "\n" + query, ans,
                "Filter the rows with the WHERE clause first, then apply the aggregate function to what is left: " + ans + ".");
    }

    static Spec dateMath(Random r, int tier) {
        LocalDate d = LocalDate.of(ri(r, 2020, 2027), ri(r, 1, 12), ri(r, 1, 28));
        int p = ri(r, 0, 2);
        if (p == 0) {
            DayOfWeek dow = d.getDayOfWeek();
            String ans = dow.toString().charAt(0) + dow.toString().substring(1).toLowerCase();
            List<String> days = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
            return Spec.choice("Calendar arithmetic", "Which day of the week is " + d + " (year-month-day)?", "", ans, days, "The calendar says " + d + " is a " + ans + ".", false);
        } else if (p == 1) {
            int add = ri(r, 10, 400);
            return Spec.output("Calendar arithmetic", "What date is " + add + " days after " + d + "? (answer as yyyy-mm-dd)", "", d.plusDays(add).toString(),
                    "Add " + add + " days, carrying over months and years: " + d.plusDays(add) + ".", false);
        } else {
            LocalDate e = d.plusDays(ri(r, 20, 700));
            long diff = java.time.temporal.ChronoUnit.DAYS.between(d, e);
            return Spec.number("Calendar arithmetic", "How many days are there from " + d + " to " + e + "?", "", diff,
                    "Count the days between the two dates (leap years included): " + diff + ".");
        }
    }

    static Spec regexCount(Random r, int tier) {
        String[] texts = {"order-123 shipped, order-45 pending, order-6789 failed", "a1b22c333d4444", "call 555-1234 or 555-9876 now", "cat bat rat mat hat sat",
                "2024-01-05 and 2025-12-31 and 2026-07-04", "foo@bar.com, x@y.org; nobody", "aaa bb c dddd ee"};
        String[][] pats = {{"\\d+", "one or more digits"}, {"[a-z]at", "a letter followed by 'at'"}, {"\\b\\w{2}\\b", "a whole word of exactly 2 letters"}, {"\\d{3}", "exactly three digits in a row"},
                {"[a-z]+@[a-z]+\\.[a-z]+", "a simple email"}, {"[aeiou]", "a vowel"}};
        String text = pick(r, texts);
        String[] pat = pick(r, pats);
        Matcher m = Pattern.compile(pat[0]).matcher(text);
        int c = 0;
        while (m.find()) c++;
        return Spec.number("Regex matches", "How many times does the regular expression " + pat[0] + " (" + pat[1] + ") match in the text \"" + text + "\"? (Count non-overlapping matches from left to right.)", "", c,
                "Scan the text from left to right and count each separate match: " + c + ".");
    }

    static Spec money(Random r, int tier) {
        int price = ri(r, 20, 500) * 5, pct = pick(r, 5, 10, 12, 15, 18, 20, 25);
        int qty = ri(r, 1, 6);
        int p = ri(r, 0, 2);
        if (p == 0) {
            long cents = Math.round(price * (100 - pct) / 100.0 * 100);
            return Spec.output("Discount and tax maths", "An item costs " + price + ".00. After a " + pct + "% discount, what is the price (two decimals)?", "", String.format("%d.%02d", cents / 100, cents % 100),
                    price + " * (100 - " + pct + ") / 100 = " + String.format("%d.%02d", cents / 100, cents % 100) + ".", false);
        } else if (p == 1) {
            long cents = Math.round(price * qty * (100 + pct) / 100.0 * 100);
            return Spec.output("Discount and tax maths", qty + " items at " + price + ".00 each, plus " + pct + "% tax on the total. What is the final amount (two decimals)?", "", String.format("%d.%02d", cents / 100, cents % 100),
                    "Subtotal " + (price * qty) + ", add " + pct + "%: " + String.format("%d.%02d", cents / 100, cents % 100) + ".", false);
        } else {
            int years = ri(r, 1, 6);
            long value = price * 100L;
            for (int i = 0; i < years; i++) value = Math.round(value * (100 + pct) / 100.0);
            return Spec.output("Discount and tax maths", "Start with " + price + ".00 and grow it by " + pct + "% each year (compound), rounding to the nearest cent every year. What is the amount after " + years + " year(s)?", "",
                    String.format("%d.%02d", value / 100, value % 100), "Multiply by " + (100 + pct) + "/100 once per year and round each time: " + String.format("%d.%02d", value / 100, value % 100) + ".", false);
        }
    }

    static Spec roundRobin(Random r, int tier) {
        int n = ri(r, 3, 4), q = ri(r, 2, 4);
        int[] burst = new int[n];
        for (int i = 0; i < n; i++) burst[i] = ri(r, 2, 9);
        int target = r.nextInt(n);
        int[] rem = burst.clone();
        int time = 0, done = 0;
        int finish = 0;
        while (done < n) {
            for (int i = 0; i < n; i++) {
                if (rem[i] > 0) {
                    int run = Math.min(q, rem[i]);
                    time += run;
                    rem[i] -= run;
                    if (rem[i] == 0) { done++; if (i == target) finish = time; }
                }
            }
        }
        return Spec.number("Round-robin scheduling", n + " processes arrive together and need " + java.util.Arrays.toString(burst) + " time units. A round-robin scheduler with a quantum of " + q
                + " serves them in order P1, P2, ... and loops. At what time does process P" + (target + 1) + " finish?", "", finish,
                "Each turn runs a process for at most " + q + " units. Add up the time slices in order until P" + (target + 1) + " has used all of its time: " + finish + ".");
    }
}
