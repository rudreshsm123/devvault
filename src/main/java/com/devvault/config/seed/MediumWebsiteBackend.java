package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.full;

/** Intermediate set D: problems modelled on real website and backend features. */
public final class MediumWebsiteBackend {

    private MediumWebsiteBackend() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty M = Difficulty.MEDIUM;

        full(l, "Retry with Exponential Backoff", "Website: Backend Reliability", M, "website,backend,retry,resilience",
                "Call an unreliable service up to maxAttempts times. After each failure wait base * 2^attempt milliseconds (capped), with random jitter, and rethrow the last error if all attempts fail.",
                "Signal: transient failures (timeouts, 503) are retried with growing delays so a struggling server is not hammered.",
                """
                static <T> T retry(Callable<T> call, int maxAttempts) throws Exception {
                    Exception last = null;
                    for (int i = 0; i < maxAttempts; i++) {
                        try {
                            return call.call();
                        } catch (Exception e) {
                            last = e;
                            Thread.sleep(1000);                   // fixed delay: simple, but synchronises clients
                        }
                    }
                    throw last;
                }""",
                """
                static <T> T retry(Callable<T> call, int maxAttempts, long baseMs, long capMs) throws Exception {
                    Exception last = null;
                    for (int attempt = 0; attempt < maxAttempts; attempt++) {
                        try {
                            return call.call();
                        } catch (Exception e) {
                            last = e;
                            if (attempt == maxAttempts - 1) break;                       // no sleep after the last try
                            long delay = Math.min(capMs, baseMs * (1L << attempt));       // 100, 200, 400, ...
                            long jitter = ThreadLocalRandom.current().nextLong(delay / 2 + 1);
                            Thread.sleep(delay / 2 + jitter);                             // 'equal jitter'
                        }
                    }
                    throw last;
                }""",
                "A fixed delay makes all failed clients retry at the same moment, causing a 'thundering herd'. Exponential growth gives the server time to recover; jitter spreads the retries randomly; the cap prevents absurd waits.",
                """
                baseMs * (1L << attempt) ==> Doubles every attempt using a bit shift (use long to avoid overflow).
                Math.min(capMs, ...) ==> Never wait longer than the cap.
                nextLong(delay / 2 + 1) ==> Random jitter so clients do not retry in lockstep.
                attempt == maxAttempts - 1 ==> Do not sleep when there is nothing left to retry.
                throw last ==> The caller learns the real reason for failure.""",
                "Know which errors are retryable (timeouts, 5xx, 429) versus not (400, 404) and that operations must be idempotent to be retried safely. Mention Resilience4j / Spring Retry.",
                "O(maxAttempts)", "O(1)", "Retrying non-idempotent operations; no cap or jitter; swallowing the final exception.");

        full(l, "Circuit Breaker", "Website: Backend Reliability", M, "website,backend,state-machine,resilience",
                "Implement a circuit breaker with states CLOSED, OPEN and HALF_OPEN. After N consecutive failures it opens and rejects calls immediately; after a cool-down it allows one trial call.",
                "Signal: a small state machine that protects a failing dependency from more traffic.",
                null,
                """
                class CircuitBreaker {
                    enum State { CLOSED, OPEN, HALF_OPEN }

                    private State state = State.CLOSED;
                    private int failures = 0;
                    private long openedAt = 0;
                    private final int threshold;
                    private final long coolDownMs;

                    CircuitBreaker(int threshold, long coolDownMs) {
                        this.threshold = threshold;
                        this.coolDownMs = coolDownMs;
                    }

                    synchronized <T> T call(Callable<T> action, long now) throws Exception {
                        if (state == State.OPEN) {
                            if (now - openedAt < coolDownMs) throw new IllegalStateException("circuit open");
                            state = State.HALF_OPEN;                           // allow a trial
                        }
                        try {
                            T result = action.call();
                            failures = 0;
                            state = State.CLOSED;                               // success closes it again
                            return result;
                        } catch (Exception e) {
                            failures++;
                            if (state == State.HALF_OPEN || failures >= threshold) {
                                state = State.OPEN;
                                openedAt = now;
                            }
                            throw e;
                        }
                    }
                }""",
                "CLOSED lets calls through and counts failures. After enough failures it flips to OPEN and fails fast (no waiting on a dead service). After a cool-down one trial call (HALF_OPEN) decides: success closes the circuit, failure reopens it.",
                """
                enum State ==> The three states make the behaviour explicit and testable.
                state == OPEN && now - openedAt < coolDownMs ==> Fail fast without calling the dependency.
                state = HALF_OPEN ==> After the cool-down allow a probing call.
                failures = 0; state = CLOSED ==> Any success resets the counter.
                failures >= threshold or HALF_OPEN failure ==> Open (again) and remember the time.
                synchronized ==> The state is shared by many threads; protect it.""",
                "Pass the clock in (now) so it is testable. Describe where it sits relative to retries and timeouts. Name Resilience4j.",
                "O(1)", "O(1)", "Using the real clock inside (hard to test); letting many threads probe in HALF_OPEN.");

        full(l, "Idempotency Key Store (Safe Payments)", "Website: Payments", M, "website,backend,idempotency,hash-map",
                "A mobile app may resend a 'pay' request after a timeout. Implement a store so the same idempotency key never charges twice: the second request returns the first response.",
                "Signal: remember the result per key; make 'check then act' atomic.",
                """
                class PaymentService {
                    private final Map<String, String> results = new HashMap<>();

                    String pay(String key, long cents) {
                        if (results.containsKey(key)) return results.get(key);   // race: two threads can both miss
                        String receipt = charge(cents);
                        results.put(key, receipt);
                        return receipt;
                    }
                    private String charge(long cents) { return "receipt-" + UUID.randomUUID(); }
                }""",
                """
                class PaymentService {
                    private final ConcurrentHashMap<String, CompletableFuture<String>> results = new ConcurrentHashMap<>();

                    String pay(String key, long cents) {
                        CompletableFuture<String> mine = new CompletableFuture<>();
                        CompletableFuture<String> existing = results.putIfAbsent(key, mine);   // atomic claim
                        if (existing != null) return existing.join();                          // duplicate: wait for first result
                        try {
                            String receipt = charge(cents);
                            mine.complete(receipt);
                            return receipt;
                        } catch (RuntimeException e) {
                            results.remove(key, mine);                                         // allow a retry after failure
                            mine.completeExceptionally(e);
                            throw e;
                        }
                    }
                    private String charge(long cents) { return "receipt-" + UUID.randomUUID(); }
                }""",
                "The simple version has a race: two simultaneous retries both see no entry and both charge. putIfAbsent is atomic, so exactly one request wins the right to charge, and duplicates wait for the same result.",
                """
                ConcurrentHashMap ==> A thread-safe map.
                putIfAbsent(key, mine) ==> Atomically claims the key; returns the existing value if someone else got there first.
                existing.join() ==> A duplicate request waits and returns the original receipt instead of charging again.
                results.remove(key, mine) ==> If the first attempt failed, remove the entry so the client can retry.
                Real systems ==> Persist keys in the database with a UNIQUE constraint and an expiry (for example 24 hours).""",
                "Payment APIs (Stripe, Razorpay) all use idempotency keys. Discuss atomicity, expiry, and what to do when the same key arrives with a different payload (reject with 422).",
                "O(1)", "O(keys)", "Check-then-act without atomicity; caching failures forever.");

        full(l, "Feature Flag Percentage Rollout", "Website: Release Engineering", M, "website,backend,hashing,feature-flags",
                "Roll out a feature to p percent of users. A given user must always get the same answer, and increasing p must never remove the feature from someone who already has it.",
                "Signal: hash the user id plus the flag name into a stable bucket 0-99 and compare it with p.",
                """
                static boolean isEnabled(String userId, int percent) {
                    return new Random().nextInt(100) < percent;      // wrong: the same user flips between visits
                }""",
                """
                static boolean isEnabled(String flagName, String userId, int percent) {
                    if (percent <= 0) return false;
                    if (percent >= 100) return true;
                    int bucket = Math.floorMod((flagName + ":" + userId).hashCode(), 100);
                    return bucket < percent;
                }""",
                "A deterministic hash maps each user to a fixed bucket, so the decision is stable and consistent across servers. Raising the percentage only adds buckets, so nobody loses the feature. Including the flag name gives different users per flag.",
                """
                (flagName + ":" + userId).hashCode() ==> A repeatable number for each user and flag pair (for production prefer a stronger hash such as murmur).
                Math.floorMod(h, 100) ==> A non-negative bucket 0..99 even if the hash is negative (% can return negatives).
                bucket < percent ==> Buckets 0..p-1 are enabled; increasing p adds more buckets.
                percent <= 0 / >= 100 ==> Fast paths for fully off or fully on.""",
                "Mention sticky assignment, gradual rollout, kill switches and A/B experiments. Be careful: String.hashCode is not uniform enough for serious experiments.",
                "O(len)", "O(1)", "Random per request; Math.abs(hash) % 100 (abs of Integer.MIN_VALUE is negative).");

        full(l, "Undo/Redo Editor History", "Website: Editor", M, "website,design,stack,deque",
                "Implement a text editor model with type(text), undo() and redo(). A new edit clears the redo history.",
                "Signal: two stacks - one for undo, one for redo - holding snapshots or commands.",
                null,
                """
                class Editor {
                    private final Deque<String> undo = new ArrayDeque<>();
                    private final Deque<String> redo = new ArrayDeque<>();
                    private String text = "";

                    void type(String s) {
                        undo.push(text);                    // remember the state BEFORE the change
                        text = text + s;
                        redo.clear();                       // a new edit invalidates the redo branch
                    }
                    void undo() {
                        if (undo.isEmpty()) return;
                        redo.push(text);
                        text = undo.pop();
                    }
                    void redo() {
                        if (redo.isEmpty()) return;
                        undo.push(text);
                        text = redo.pop();
                    }
                    String text() { return text; }
                }""",
                "Every change pushes the previous state on the undo stack. Undo moves the current state to the redo stack and restores the previous one. Typing something new makes the old redo states meaningless, so they are discarded.",
                """
                undo.push(text) ==> Save the state before modifying it.
                redo.clear() ==> Standard editor behaviour: a fresh edit ends the redo timeline.
                redo.push(text); text = undo.pop() ==> Undo: current state goes to redo, previous state returns.
                undo.push(text); text = redo.pop() ==> Redo is the mirror image.""",
                "Real editors store commands or diffs (the Command pattern) instead of whole strings to save memory. Mention it.",
                "O(n) per snapshot", "O(history)", "Forgetting to clear the redo stack.");

        full(l, "Cache-Aside Loader with Hit Ratio", "Website: Caching", M, "website,backend,cache,design",
                "Implement a cache-aside helper: get(key) returns a cached value or loads it from a slow source, stores it with a TTL, and reports the hit ratio.",
                "Signal: check the cache first, load on a miss, store, and track counters.",
                null,
                """
                class CacheAside<K, V> {
                    private record Entry<V>(V value, long expiresAt) {}

                    private final Map<K, Entry<V>> cache = new ConcurrentHashMap<>();
                    private final Function<K, V> loader;
                    private final long ttlMs;
                    private final LongAdder hits = new LongAdder(), misses = new LongAdder();

                    CacheAside(Function<K, V> loader, long ttlMs) {
                        this.loader = loader;
                        this.ttlMs = ttlMs;
                    }

                    V get(K key, long now) {
                        Entry<V> e = cache.get(key);
                        if (e != null && e.expiresAt() > now) {
                            hits.increment();
                            return e.value();
                        }
                        misses.increment();
                        V value = loader.apply(key);                      // slow call to the database
                        cache.put(key, new Entry<>(value, now + ttlMs));
                        return value;
                    }

                    void invalidate(K key) { cache.remove(key); }         // call after the data changes

                    double hitRatio() {
                        long h = hits.sum(), total = h + misses.sum();
                        return total == 0 ? 0 : (double) h / total;
                    }
                }""",
                "In cache-aside the application owns the logic: read the cache, on a miss read the database and fill the cache; after a write, invalidate. The hit ratio shows whether the cache is worth its memory. LongAdder is faster than AtomicLong under many threads.",
                """
                Function<K, V> loader ==> The slow data source (a repository call) hidden behind a function.
                e.expiresAt() > now ==> A valid, unexpired entry is a hit.
                loader.apply(key) ==> Miss: load from the source of truth.
                cache.put(key, new Entry<>(value, now + ttlMs)) ==> Store with a time-to-live so stale data eventually disappears.
                invalidate(key) ==> The hardest part of caching: remove the entry when the underlying data changes.""",
                "Mention cache stampede (many threads loading the same key; use computeIfAbsent or a lock), TTL jitter, and write-through vs write-behind. Compare with Spring's @Cacheable.",
                "O(1)", "O(entries)", "Never invalidating; caching nulls without thought; cache stampede.");

        full(l, "Parse a URL Query String", "Website: HTTP Basics", M, "website,strings,parsing,url",
                "Parse \"?q=java%20dev&tag=a&tag=b&empty=\" into a map from key to a list of decoded values.",
                "Signal: split on & and =, decode percent-encoding, and allow repeated keys.",
                """
                static Map<String, String> parse(String query) {
                    Map<String, String> map = new HashMap<>();
                    for (String pair : query.split("&")) {
                        String[] kv = pair.split("=");
                        map.put(kv[0], kv.length > 1 ? kv[1] : "");      // loses repeated keys and breaks on '=' in values
                    }
                    return map;
                }""",
                """
                static Map<String, List<String>> parse(String query) {
                    Map<String, List<String>> map = new LinkedHashMap<>();
                    if (query.startsWith("?")) query = query.substring(1);
                    if (query.isEmpty()) return map;
                    for (String pair : query.split("&")) {
                        int eq = pair.indexOf('=');
                        String key = eq < 0 ? pair : pair.substring(0, eq);
                        String value = eq < 0 ? "" : pair.substring(eq + 1);
                        key = URLDecoder.decode(key, StandardCharsets.UTF_8);
                        value = URLDecoder.decode(value, StandardCharsets.UTF_8);
                        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                    }
                    return map;
                }""",
                "The simple version fails on repeated keys, values containing '=' and encoded characters. Splitting only on the FIRST '=' and decoding each part handles them. A list per key supports tag=a&tag=b.",
                """
                query.startsWith("?") ==> Allow both '?q=1' and 'q=1'.
                pair.indexOf('=') ==> Only the first '=' separates key from value (a value may contain '=').
                eq < 0 ? "" ==> A key without '=' has an empty value.
                URLDecoder.decode(..., UTF_8) ==> Converts %20 and + back into spaces and decodes Unicode.
                computeIfAbsent(key, ...).add(value) ==> Collect repeated keys in a list.""",
                "A frequent practical question: it tests edge-case thinking and awareness of URL encoding. Mention that Spring does this for you with @RequestParam.",
                "O(n)", "O(n)", "Using split(\"=\") and breaking on values with '='; forgetting to decode.");

        full(l, "Router with Path Parameters", "Website: Web Framework", M, "website,design,trie,strings",
                "Implement a router that registers patterns like /users/{id}/posts/{postId} and, for a request path, returns the handler name and the extracted parameters.",
                "Signal: split paths into segments; a segment in braces matches anything and captures it.",
                null,
                """
                class Router {
                    private record Route(String[] segments, String handler) {}
                    private final List<Route> routes = new ArrayList<>();

                    void add(String pattern, String handler) {
                        routes.add(new Route(pattern.substring(1).split("/"), handler));
                    }

                    Optional<Map<String, String>> match(String path, String[] handlerOut) {
                        String[] parts = path.substring(1).split("/");
                        for (Route r : routes) {
                            if (r.segments().length != parts.length) continue;
                            Map<String, String> params = new HashMap<>();
                            boolean ok = true;
                            for (int i = 0; i < parts.length && ok; i++) {
                                String seg = r.segments()[i];
                                if (seg.startsWith("{") && seg.endsWith("}")) {
                                    params.put(seg.substring(1, seg.length() - 1), parts[i]);
                                } else if (!seg.equals(parts[i])) {
                                    ok = false;
                                }
                            }
                            if (ok) { handlerOut[0] = r.handler(); return Optional.of(params); }
                        }
                        return Optional.empty();
                    }
                }""",
                "Comparing segment by segment avoids regular expressions and keeps matching predictable. Literal segments must be equal; {name} segments capture the value. A trie of segments makes lookup independent of the number of routes.",
                """
                pattern.substring(1).split("/") ==> '/users/{id}' becomes ['users', '{id}'].
                r.segments().length != parts.length ==> Different depth, cannot match.
                seg.startsWith("{") && seg.endsWith("}") ==> A parameter segment: store the actual value under the name inside the braces.
                !seg.equals(parts[i]) ==> A literal segment that differs: this route fails.
                Optional.of(params) ==> The extracted parameters, like Spring's @PathVariable values.""",
                "Show that you understand what a framework does before the controller method runs. Follow-ups: priority (literal beats parameter), wildcard routes, HTTP method matching.",
                "O(routes x segments)", "O(routes)", "Matching '/users/' differently from '/users'; treating a literal as a parameter.");

        full(l, "CSV Import Validator with Error Report", "Website: Admin Import", M, "website,backend,parsing,validation",
                "An admin uploads a CSV of products (sku,name,price,stock). Validate every row and return a report of valid rows and per-row errors without stopping at the first error.",
                "Signal: validate each row independently and collect errors with the row number.",
                null,
                """
                record Product(String sku, String name, long priceCents, int stock) {}
                record RowError(int line, String message) {}
                record ImportResult(List<Product> valid, List<RowError> errors) {}

                static ImportResult importCsv(List<String> lines) {
                    List<Product> valid = new ArrayList<>();
                    List<RowError> errors = new ArrayList<>();
                    Set<String> skus = new HashSet<>();
                    for (int i = 1; i < lines.size(); i++) {                 // line 0 is the header
                        int lineNo = i + 1;
                        String[] c = lines.get(i).split(",", -1);
                        if (c.length != 4) { errors.add(new RowError(lineNo, "expected 4 columns")); continue; }
                        String sku = c[0].trim(), name = c[1].trim();
                        if (sku.isEmpty()) { errors.add(new RowError(lineNo, "sku is required")); continue; }
                        if (!skus.add(sku)) { errors.add(new RowError(lineNo, "duplicate sku " + sku)); continue; }
                        if (name.isEmpty()) { errors.add(new RowError(lineNo, "name is required")); continue; }
                        try {
                            long price = new BigDecimal(c[2].trim()).movePointRight(2).longValueExact();
                            int stock = Integer.parseInt(c[3].trim());
                            if (price < 0 || stock < 0) throw new NumberFormatException("negative");
                            valid.add(new Product(sku, name, price, stock));
                        } catch (ArithmeticException | NumberFormatException e) {
                            errors.add(new RowError(lineNo, "invalid price or stock"));
                        }
                    }
                    return new ImportResult(valid, errors);
                }""",
                "Good imports never fail silently or all-or-nothing without telling the user. Each row is validated alone, errors carry the line number, and duplicates are detected with a set. Prices are parsed with BigDecimal and stored as whole cents.",
                """
                split(",", -1) ==> The -1 keeps trailing empty columns so 'a,b,,' is four columns, not two.
                i + 1 ==> Human-friendly line numbers that match what a spreadsheet shows.
                skus.add(sku) ==> Detect duplicate SKUs inside the file; also check the database in real life.
                new BigDecimal(...).movePointRight(2).longValueExact() ==> Exact conversion to cents; throws for more than two decimals.
                catch (ArithmeticException | NumberFormatException e) ==> One handler for both kinds of bad numbers.
                ImportResult(valid, errors) ==> The caller can save the valid rows and show the error table.""",
                "Mention streaming huge files, a real CSV parser for quoted commas, transactions (all-or-nothing vs partial), and security (formula injection when re-exporting to Excel).",
                "O(n)", "O(n)", "Stopping at the first error; using split(\",\") that drops empty trailing cells; floating point prices.");

        full(l, "Token Bucket Rate Limiter", "Website: API Gateway", M, "website,backend,rate-limit,design",
                "Allow bursts but enforce an average rate: a bucket holds up to capacity tokens and refills at refillPerSecond. Each request consumes a token.",
                "Signal: compute the refill lazily from the elapsed time instead of using a timer thread.",
                null,
                """
                class TokenBucket {
                    private final double capacity, refillPerMs;
                    private double tokens;
                    private long lastRefill;

                    TokenBucket(double capacity, double refillPerSecond, long nowMs) {
                        this.capacity = capacity;
                        this.refillPerMs = refillPerSecond / 1000.0;
                        this.tokens = capacity;
                        this.lastRefill = nowMs;
                    }

                    synchronized boolean tryAcquire(long nowMs) {
                        tokens = Math.min(capacity, tokens + (nowMs - lastRefill) * refillPerMs);
                        lastRefill = nowMs;
                        if (tokens >= 1) {
                            tokens -= 1;
                            return true;
                        }
                        return false;
                    }
                }""",
                "Instead of a background thread adding tokens, each call adds the tokens earned since the previous call. The bucket size allows short bursts while the refill rate fixes the long-term average.",
                """
                tokens + elapsed * refillPerMs ==> Tokens earned since the last request.
                Math.min(capacity, ...) ==> The bucket cannot overflow; unused tokens beyond the capacity are lost.
                lastRefill = nowMs ==> Remember when we last calculated.
                tokens >= 1 ==> Enough for one request: consume it and allow.
                synchronized ==> Many request threads share one bucket per user.""",
                "Compare with fixed window and sliding window limiters. A distributed version uses Redis with a Lua script to make the read-modify-write atomic.",
                "O(1)", "O(1) per key", "Refilling with a timer thread per user; precision loss with integer tokens.");

        full(l, "Shopping Cart with Tax and Rounding", "Website: E-commerce", M, "website,e-commerce,money,rounding",
                "Compute an order total from lines (price in cents, quantity, tax rate in basis points). Round tax per line half-up and never lose or gain a cent when splitting a discount across lines.",
                "Signal: all arithmetic on integer cents; distribute remainders deliberately.",
                null,
                """
                record Line(String sku, long unitCents, int qty, int taxBps) {}      // 1800 bps = 18%

                static long total(List<Line> lines, long orderDiscountCents) {
                    long subtotal = 0;
                    for (Line l : lines) subtotal += l.unitCents() * l.qty();
                    if (orderDiscountCents > subtotal) orderDiscountCents = subtotal;

                    long remaining = orderDiscountCents, taxTotal = 0, assigned = 0;
                    for (int i = 0; i < lines.size(); i++) {
                        Line l = lines.get(i);
                        long lineTotal = l.unitCents() * l.qty();
                        long share = (i == lines.size() - 1)
                                ? remaining                                           // last line absorbs the rounding remainder
                                : orderDiscountCents * lineTotal / subtotal;
                        remaining -= share;
                        long taxable = lineTotal - share;
                        taxTotal += (taxable * l.taxBps() + 5000) / 10000;           // half-up rounding of the tax
                        assigned += taxable;
                    }
                    return assigned + taxTotal;
                }""",
                "Splitting 100 cents of discount over three equal lines gives 33 + 33 + 33 = 99: one cent disappears. Giving the leftover to the last line keeps the sum exact. Tax is computed on the discounted amount and rounded half-up per line, as invoices require.",
                """
                taxBps ==> Basis points avoid floating point: 18 percent is 1800.
                orderDiscountCents * lineTotal / subtotal ==> Proportional share of the discount (integer division loses a remainder).
                i == lines.size() - 1 ? remaining ==> The last line takes whatever is left so the shares add up exactly.
                (taxable * bps + 5000) / 10000 ==> Integer half-up rounding of taxable * bps / 10000.
                assigned + taxTotal ==> Grand total in cents.""",
                "Money questions test precision awareness. State your rounding rule explicitly (half-up per line vs on the total) and that it must match the invoice rules of the country.",
                "O(n)", "O(1)", "Using double; rounding after summing so lines do not add up to the total.");

        full(l, "Low Stock Alerts (Smallest Stock First)", "Website: Inventory", M, "website,heap,inventory",
                "Given stock levels for many SKUs and a stream of sales, report the k SKUs with the lowest stock at any time.",
                "Signal: a TreeSet (sorted by stock then sku) or a heap with lazy deletion gives the k lowest quickly after updates.",
                """
                class Inventory {
                    private final Map<String, Integer> stock = new HashMap<>();

                    void set(String sku, int qty) { stock.put(sku, qty); }
                    void sell(String sku, int qty) { stock.merge(sku, -qty, Integer::sum); }

                    List<String> lowest(int k) {
                        return stock.entrySet().stream()
                                .sorted(Map.Entry.<String, Integer>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                                .limit(k).map(Map.Entry::getKey).toList();
                    }
                }""",
                """
                class Inventory {
                    private final Map<String, Integer> stock = new HashMap<>();
                    private final TreeSet<Map.Entry<Integer, String>> ordered = new TreeSet<>(
                            Map.Entry.<Integer, String>comparingByKey().thenComparing(Map.Entry.comparingByValue()));

                    void set(String sku, int qty) {
                        Integer old = stock.put(sku, qty);
                        if (old != null) ordered.remove(Map.entry(old, sku));
                        ordered.add(Map.entry(qty, sku));
                    }
                    void sell(String sku, int qty) { set(sku, stock.getOrDefault(sku, 0) - qty); }

                    List<String> lowest(int k) {
                        List<String> out = new ArrayList<>();
                        for (var e : ordered) { if (out.size() == k) break; out.add(e.getValue()); }
                        return out;
                    }
                }""",
                "Sorting all SKUs on every query is O(n log n). A TreeSet keeps them ordered incrementally: an update is O(log n) and reading the first k is O(k).",
                """
                stock ==> The current quantity per SKU for O(1) lookups.
                ordered (quantity, sku) ==> Sorted view; the SKU breaks ties so two SKUs with equal stock can both exist.
                ordered.remove(Map.entry(old, sku)) ==> Remove the stale pair before inserting the new one.
                for (var e : ordered) ==> Iteration is already in ascending stock order.""",
                "Mention the database equivalent: an index on stock plus ORDER BY stock LIMIT k, and event-driven alerts instead of polling.",
                "O(log n) update", "O(n)", "Leaving stale entries in the sorted structure.");

        full(l, "Detect Near-Duplicate Posts", "Website: Moderation", M, "website,strings,sets,similarity",
                "A forum wants to flag a new post as a near-duplicate when its word-set overlap (Jaccard similarity) with a recent post is at least 0.8.",
                "Signal: turn each post into a set of normalised words and compare |A intersect B| / |A union B|.",
                """
                static boolean isNearDuplicate(String a, String b) {
                    return a.trim().equalsIgnoreCase(b.trim());          // only catches exact copies
                }""",
                """
                static Set<String> words(String text) {
                    Set<String> set = new HashSet<>();
                    for (String w : text.toLowerCase().split("[^a-z0-9]+")) if (!w.isEmpty()) set.add(w);
                    return set;
                }

                static double jaccard(Set<String> a, Set<String> b) {
                    if (a.isEmpty() && b.isEmpty()) return 1.0;
                    Set<String> inter = new HashSet<>(a);
                    inter.retainAll(b);
                    int union = a.size() + b.size() - inter.size();
                    return (double) inter.size() / union;
                }

                static boolean isNearDuplicate(String a, String b) {
                    return jaccard(words(a), words(b)) >= 0.8;
                }""",
                "Spammers change a few words, so exact comparison fails. Jaccard similarity measures overlap of the vocabularies: identical sets give 1.0 and unrelated texts near 0. At scale you precompute MinHash signatures to avoid pairwise comparisons.",
                """
                text.toLowerCase().split("[^a-z0-9]+") ==> Normalise: lower-case and split on anything that is not a letter or digit.
                inter.retainAll(b) ==> The set intersection.
                a.size() + b.size() - inter.size() ==> The union size without building the union set.
                >= 0.8 ==> A threshold chosen from data; tune it to balance false positives and false negatives.""",
                "Name MinHash / SimHash for large scale and explain why comparing every pair is O(n^2). Mention shingles (word n-grams) to respect word order.",
                "O(n + m)", "O(n + m)", "Dividing by zero for two empty posts; case-sensitive comparison.");

        full(l, "Search Ranking by Relevance Score", "Website: Search", M, "website,search,sorting,scoring",
                "Rank products for a query: title matches count 3 points per query word, description matches 1 point, and in-stock items get a +2 boost. Return the top 10, ties broken by lower price.",
                "Signal: compute a score per document, then sort with a multi-level comparator.",
                null,
                """
                record Product(String title, String description, long priceCents, boolean inStock) {}

                static List<Product> search(List<Product> catalog, String query) {
                    Set<String> terms = new HashSet<>(Arrays.asList(query.toLowerCase().split("\\\\s+")));
                    record Scored(Product p, int score) {}
                    List<Scored> scored = new ArrayList<>();
                    for (Product p : catalog) {
                        Set<String> title = new HashSet<>(Arrays.asList(p.title().toLowerCase().split("\\\\W+")));
                        Set<String> desc = new HashSet<>(Arrays.asList(p.description().toLowerCase().split("\\\\W+")));
                        int score = 0;
                        for (String t : terms) {
                            if (title.contains(t)) score += 3;
                            if (desc.contains(t)) score += 1;
                        }
                        if (score == 0) continue;                       // not relevant at all
                        if (p.inStock()) score += 2;
                        scored.add(new Scored(p, score));
                    }
                    scored.sort(Comparator.comparingInt(Scored::score).reversed()
                            .thenComparingLong(s -> s.p().priceCents()));
                    return scored.stream().limit(10).map(Scored::p).toList();
                }""",
                "A simple weighted score is the heart of every search ranking (before machine learning). Words are normalised and stored in sets for O(1) matching. Irrelevant items are filtered before the stock boost so an in-stock item with no match does not appear.",
                """
                new HashSet<>(Arrays.asList(...split)) ==> Unique, lower-case words for quick contains() checks.
                if (title.contains(t)) score += 3 ==> Title matches are worth more than description matches.
                if (score == 0) continue ==> Only boost items that matched at least once.
                comparingInt(score).reversed().thenComparingLong(price) ==> Highest score first; cheaper first on ties.
                limit(10) ==> The first results page.""",
                "Next steps to mention: an inverted index (term to documents), TF-IDF or BM25 scoring, typo tolerance and Elasticsearch. See the inverted index problem in the hard set.",
                "O(n x q)", "O(n)", "Boosting items that did not match; unstable tie-breaking.");

        full(l, "Order State Machine", "Website: E-commerce", M, "website,design,enum,state-machine",
                "An order moves through CREATED, PAID, SHIPPED, DELIVERED or CANCELLED. Only some transitions are legal (for example SHIPPED cannot go back to PAID). Implement a safe transition method.",
                "Signal: an enum with an explicit set of allowed next states; reject everything else.",
                null,
                """
                enum OrderStatus {
                    CREATED, PAID, SHIPPED, DELIVERED, CANCELLED;

                    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = new EnumMap<>(OrderStatus.class);
                    static {
                        ALLOWED.put(CREATED, EnumSet.of(PAID, CANCELLED));
                        ALLOWED.put(PAID, EnumSet.of(SHIPPED, CANCELLED));
                        ALLOWED.put(SHIPPED, EnumSet.of(DELIVERED));
                        ALLOWED.put(DELIVERED, EnumSet.noneOf(OrderStatus.class));
                        ALLOWED.put(CANCELLED, EnumSet.noneOf(OrderStatus.class));
                    }

                    boolean canMoveTo(OrderStatus next) {
                        return ALLOWED.get(this).contains(next);
                    }
                }

                class Order {
                    private OrderStatus status = OrderStatus.CREATED;

                    void moveTo(OrderStatus next) {
                        if (!status.canMoveTo(next)) {
                            throw new IllegalStateException("Cannot go from " + status + " to " + next);
                        }
                        status = next;
                    }
                }""",
                "Putting the rules in one table makes illegal states impossible instead of relying on if statements scattered through the code. EnumMap and EnumSet are fast, compact collections designed for enums.",
                """
                EnumMap<OrderStatus, Set<OrderStatus>> ==> A lookup of status -> the statuses allowed next.
                EnumSet.of(PAID, CANCELLED) ==> A created order can be paid or cancelled, nothing else.
                EnumSet.noneOf(...) ==> Terminal states: no way out.
                status.canMoveTo(next) ==> A single place checks every transition.
                throw new IllegalStateException(...) ==> A clear error that names both states, which the API can map to HTTP 409 Conflict.""",
                "Relate it to real systems: payment gateways, shipping workflows. Mention persisting a history of transitions and using optimistic locking to stop two requests moving the same order.",
                "O(1)", "O(states)", "Scattering transition checks across controllers; allowing cancellation after delivery.");

        full(l, "Notification Digest Batching", "Website: Notifications", M, "website,design,map,time",
                "Instead of sending 50 emails, send one digest. Collect events per user and flush a user's batch when it reaches maxSize events or the oldest event is older than maxAgeMs.",
                "Signal: per-user buffers with two flush conditions (size and age).",
                null,
                """
                class DigestBatcher {
                    private static class Batch { final List<String> events = new ArrayList<>(); long firstAt; }

                    private final Map<String, Batch> batches = new HashMap<>();
                    private final int maxSize;
                    private final long maxAgeMs;

                    DigestBatcher(int maxSize, long maxAgeMs) {
                        this.maxSize = maxSize;
                        this.maxAgeMs = maxAgeMs;
                    }

                    /** Returns the digest to send now, or an empty list if the batch should keep waiting. */
                    List<String> add(String user, String event, long now) {
                        Batch b = batches.computeIfAbsent(user, k -> new Batch());
                        if (b.events.isEmpty()) b.firstAt = now;
                        b.events.add(event);
                        if (b.events.size() >= maxSize) return flush(user);
                        return List.of();
                    }

                    /** Called periodically: flush every batch whose first event is too old. */
                    Map<String, List<String>> flushDue(long now) {
                        Map<String, List<String>> due = new HashMap<>();
                        for (var e : new ArrayList<>(batches.entrySet())) {
                            if (!e.getValue().events.isEmpty() && now - e.getValue().firstAt >= maxAgeMs) {
                                due.put(e.getKey(), flush(e.getKey()));
                            }
                        }
                        return due;
                    }

                    private List<String> flush(String user) {
                        Batch b = batches.remove(user);
                        return b == null ? List.of() : b.events;
                    }
                }""",
                "Two limits protect users: a size limit keeps messages from growing without bound and an age limit makes sure nobody waits forever. A scheduler calls flushDue every minute.",
                """
                Batch { events, firstAt } ==> The buffered events and the time of the oldest one.
                if (b.events.isEmpty()) b.firstAt = now ==> Start the age clock with the first event.
                b.events.size() >= maxSize ==> Size trigger: send now.
                now - firstAt >= maxAgeMs ==> Age trigger evaluated by a periodic job.
                batches.remove(user) ==> Flushing hands over the events and empties the buffer in one step.""",
                "Think about durability (events stored in a queue or database so a crash does not lose them) and per-user time zones for scheduled digests.",
                "O(1) per event", "O(pending events)", "Only a size limit (quiet users never get their email).");

        full(l, "Pagination Page Window (1 ... 4 5 6 ... 20)", "Website: Listing Page", M, "website,math,pagination,ui",
                "Given the current page, total pages and a window size, return the page links to show: always first and last, a window around the current page, and '...' where pages are skipped.",
                "Signal: compute the window boundaries, clamp them, then add the ellipsis markers.",
                null,
                """
                static List<String> pageWindow(int current, int total, int radius) {
                    List<String> out = new ArrayList<>();
                    int start = Math.max(1, current - radius);
                    int end = Math.min(total, current + radius);

                    if (start > 1) {
                        out.add("1");
                        if (start > 2) out.add("...");
                    }
                    for (int p = start; p <= end; p++) out.add(String.valueOf(p));
                    if (end < total) {
                        if (end < total - 1) out.add("...");
                        out.add(String.valueOf(total));
                    }
                    return out;
                }""",
                "The visible window is [current - radius, current + radius] clamped to the valid pages. The first and last pages are always reachable; an ellipsis is only shown when at least one page is hidden (a gap of exactly one page would just show that page).",
                """
                Math.max(1, current - radius) ==> The window cannot start before page 1.
                Math.min(total, current + radius) ==> Or end after the last page.
                if (start > 1) add "1" ==> Always keep a link to the first page.
                if (start > 2) add "..." ==> Show an ellipsis only if pages were skipped.
                if (end < total - 1) add "..." ==> The same check on the right side before adding the last page.""",
                "Test current = 1, current = total, total = 1 and a small total. Real UI libraries expose the same logic.",
                "O(radius)", "O(radius)", "Showing '...' that hides nothing; page 0.");

        full(l, "Minimal JSON Validator", "Website: API", M, "website,stack,parsing,json",
                "Without a library, check that a string has structurally valid JSON brackets and quotes: balanced {} and [], strings closed, and escapes respected.",
                "Signal: scan once with a stack and a flag for 'inside a string'.",
                null,
                """
                static boolean isBalancedJson(String s) {
                    Deque<Character> stack = new ArrayDeque<>();
                    boolean inString = false;
                    for (int i = 0; i < s.length(); i++) {
                        char c = s.charAt(i);
                        if (inString) {
                            if (c == '\\\\') i++;                          // skip the escaped character
                            else if (c == '"') inString = false;
                            continue;
                        }
                        switch (c) {
                            case '"' -> inString = true;
                            case '{', '[' -> stack.push(c);
                            case '}' -> { if (stack.isEmpty() || stack.pop() != '{') return false; }
                            case ']' -> { if (stack.isEmpty() || stack.pop() != '[') return false; }
                            default -> { }
                        }
                    }
                    return !inString && stack.isEmpty();
                }""",
                "Brackets inside strings must be ignored, so the scanner tracks whether it is inside quotes and honours backslash escapes. Outside strings a stack pairs every closing bracket with the correct opener.",
                """
                inString ==> While true, brackets are plain text.
                c == '\\\\' then i++ ==> A backslash escapes the next character (for example an escaped quote), so skip it.
                case '"' -> inString = true ==> A quote outside a string begins one.
                stack.pop() != '{' ==> A '}' must close the most recent unmatched '{'.
                !inString && stack.isEmpty() ==> At the end no string and no bracket may remain open.""",
                "This is 'valid parentheses' with extra state. A full parser would also check commas, colons and values; mention recursive descent parsing.",
                "O(n)", "O(depth)", "Counting brackets inside strings; ignoring escape sequences.");

        full(l, "Calendar Free Slots Finder", "Website: Booking", M, "website,intervals,sorting,calendar",
                "Given a person's busy intervals (minutes since midnight) and a working day [start, end), return the free slots that are at least minLength long.",
                "Signal: sort and merge the busy intervals, then collect the gaps between them.",
                null,
                """
                static List<int[]> freeSlots(List<int[]> busy, int dayStart, int dayEnd, int minLength) {
                    List<int[]> sorted = new ArrayList<>(busy);
                    sorted.sort(Comparator.comparingInt(a -> a[0]));

                    List<int[]> free = new ArrayList<>();
                    int cursor = dayStart;                               // everything before cursor is accounted for
                    for (int[] b : sorted) {
                        if (b[0] - cursor >= minLength) free.add(new int[] {cursor, b[0]});
                        cursor = Math.max(cursor, b[1]);                 // handles overlapping meetings
                    }
                    if (dayEnd - cursor >= minLength) free.add(new int[] {cursor, dayEnd});
                    return free;
                }""",
                "Walk through the sorted busy intervals with a cursor marking the end of the busy time so far. The gap between the cursor and the next meeting is a free slot. Math.max handles meetings that overlap or are nested.",
                """
                sorted by start ==> Gaps can only be found in time order.
                cursor ==> The end of the busy time processed so far.
                b[0] - cursor >= minLength ==> The gap before this meeting is long enough to be useful.
                cursor = Math.max(cursor, b[1]) ==> A meeting inside a longer one must not move the cursor backwards.
                final if ==> The free time after the last meeting until the end of the day.""",
                "This is the heart of 'find a time that works for everyone': merge all participants' busy lists first. Mention time zones and storing times in UTC.",
                "O(n log n)", "O(n)", "Moving the cursor back with b[1] when intervals overlap.");

        full(l, "Referral Chain Depth", "Website: Growth", M, "website,tree,graph,dfs",
                "Users refer other users. Given pairs (referrer, newUser), return for each user how many levels deep they are in the referral tree (the first users who joined without a referrer have depth 0).",
                "Signal: it is a forest; compute depth with BFS from the roots, or memoised recursion up the parent chain.",
                null,
                """
                static Map<String, Integer> depths(List<String[]> referrals) {
                    Map<String, List<String>> children = new HashMap<>();
                    Set<String> hasParent = new HashSet<>();
                    Set<String> all = new LinkedHashSet<>();
                    for (String[] r : referrals) {
                        children.computeIfAbsent(r[0], k -> new ArrayList<>()).add(r[1]);
                        hasParent.add(r[1]);
                        all.add(r[0]);
                        all.add(r[1]);
                    }
                    Map<String, Integer> depth = new HashMap<>();
                    Deque<String> queue = new ArrayDeque<>();
                    for (String u : all) {
                        if (!hasParent.contains(u)) { depth.put(u, 0); queue.add(u); }        // roots
                    }
                    while (!queue.isEmpty()) {
                        String u = queue.poll();
                        for (String child : children.getOrDefault(u, List.of())) {
                            if (depth.putIfAbsent(child, depth.get(u) + 1) == null) queue.add(child);   // also protects against cycles
                        }
                    }
                    return depth;
                }""",
                "Roots are users who never appear as a referred user. BFS from all roots assigns each user the depth of its parent plus one. putIfAbsent both sets the depth and guards against bad data that contains a cycle.",
                """
                children ==> Adjacency list: referrer -> the users they brought in.
                hasParent ==> Users who were referred; everyone else is a root.
                depth.put(u, 0) for roots ==> The starting level of the BFS (multi-source).
                depth.putIfAbsent(child, depth.get(u) + 1) ==> Assign once; returns null only the first time, so each user is queued once.
                children.getOrDefault(u, List.of()) ==> Leaves have no children.""",
                "Real data can contain cycles or missing parents, so validate. Follow-ups: total referrals in a subtree, and SQL recursive CTEs (WITH RECURSIVE).",
                "O(n)", "O(n)", "Assuming a single root; infinite loops on cyclic bad data.");

        return l;
    }
}
