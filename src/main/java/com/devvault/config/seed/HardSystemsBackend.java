package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.full;

/** Hard set D: system-design style coding problems from real backends. */
public final class HardSystemsBackend {

    private HardSystemsBackend() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty H = Difficulty.HARD;

        full(l, "Consistent Hashing Ring", "Website: Distributed Systems", H, "website,distributed,hashing,tree-map",
                "Distribute keys across servers so that adding or removing a server moves only a small fraction of keys. Implement addServer, removeServer and getServer(key) with virtual nodes.",
                "Signal: place servers (many virtual copies each) on a hash ring and send a key to the first server clockwise from its hash.",
                null,
                """
                class ConsistentHashRing {
                    private final TreeMap<Integer, String> ring = new TreeMap<>();
                    private final int virtualNodes;

                    ConsistentHashRing(int virtualNodes) { this.virtualNodes = virtualNodes; }

                    void addServer(String server) {
                        for (int i = 0; i < virtualNodes; i++) ring.put(hash(server + "#" + i), server);
                    }

                    void removeServer(String server) {
                        for (int i = 0; i < virtualNodes; i++) ring.remove(hash(server + "#" + i));
                    }

                    String getServer(String key) {
                        if (ring.isEmpty()) return null;
                        Map.Entry<Integer, String> e = ring.ceilingEntry(hash(key));      // first point clockwise
                        return (e != null ? e : ring.firstEntry()).getValue();             // wrap around the ring
                    }

                    private int hash(String s) {
                        int h = s.hashCode();
                        h ^= (h >>> 16);                                                   // spread the bits
                        h *= 0x45d9f3b;
                        h ^= (h >>> 16);
                        return h;
                    }
                }""",
                "With 'hash(key) % numberOfServers' adding one server remaps almost every key, which empties caches. On a ring only the keys between the new server's points and their predecessors move (about 1/N of the keys). Virtual nodes spread each server around the ring so the load is even.",
                """
                TreeMap<Integer, String> ring ==> A sorted map of ring position to server name.
                hash(server + "#" + i) ==> Many virtual points per server give a smoother distribution than one point.
                ring.ceilingEntry(hash(key)) ==> The first server at or after the key's position, moving clockwise.
                ring.firstEntry() ==> If no point is larger, wrap around to the start of the ring.
                removeServer ==> Deletes only that server's points; its keys fall to the next servers.""",
                "Used by caches (Memcached clients), databases (Cassandra, DynamoDB) and CDNs. Mention replication (take the next N distinct servers) and a better hash function such as murmur.",
                "O(log n)", "O(servers x virtual nodes)", "Using modulo hashing; too few virtual nodes (uneven load).");

        full(l, "Bloom Filter", "Website: Distributed Systems", H, "website,probabilistic,bit-set,hashing",
                "Implement a Bloom filter: add(item) and mightContain(item). It may report false positives but never false negatives, using far less memory than a Set.",
                "Signal: a bit array and k hash functions; add sets k bits, lookup checks k bits.",
                null,
                """
                class BloomFilter {
                    private final BitSet bits;
                    private final int size, hashCount;

                    BloomFilter(int expectedItems, double falsePositiveRate) {
                        this.size = (int) Math.ceil(-expectedItems * Math.log(falsePositiveRate) / (Math.log(2) * Math.log(2)));
                        this.hashCount = Math.max(1, (int) Math.round((double) size / expectedItems * Math.log(2)));
                        this.bits = new BitSet(size);
                    }

                    void add(String item) {
                        int h1 = item.hashCode(), h2 = mix(h1);
                        for (int i = 0; i < hashCount; i++) bits.set(index(h1, h2, i));
                    }

                    boolean mightContain(String item) {
                        int h1 = item.hashCode(), h2 = mix(h1);
                        for (int i = 0; i < hashCount; i++) {
                            if (!bits.get(index(h1, h2, i))) return false;       // definitely not present
                        }
                        return true;                                              // probably present
                    }

                    private int index(int h1, int h2, int i) {
                        return Math.floorMod(h1 + i * h2, size);                  // double hashing simulates k hash functions
                    }
                    private int mix(int h) { h ^= h >>> 16; h *= 0x85ebca6b; h ^= h >>> 13; return h | 1; }
                }""",
                "Each item sets k bits. If any of its k bits is 0 the item was never added (no false negatives). If all are 1 it was probably added, but other items might have set those bits (a false positive). Sizing formulas from the expected item count and acceptable error give the number of bits m and hashes k. Double hashing creates k positions from two base hashes.",
                """
                size = -n ln(p) / (ln 2)^2 ==> Number of bits needed for n items at false positive rate p.
                hashCount = (m / n) ln 2 ==> The number of hash functions that minimises false positives.
                bits.set(index) ==> Marks the item's positions.
                if (!bits.get(...)) return false ==> A single zero bit proves absence.
                h1 + i * h2 ==> Kirsch-Mitzenmacher double hashing: one cheap way to derive many hash functions.""",
                "Typical uses: 'is this username taken?', skip disk lookups for missing keys (databases), malicious URL lists. Know that deletion is impossible (counting Bloom filters or cuckoo filters solve that).",
                "O(k)", "O(m bits)", "Treating 'true' as certain; sizing the filter too small.");

        full(l, "Distributed ID Generator (Snowflake)", "Website: Distributed Systems", H, "website,distributed,bit-manipulation,concurrency",
                "Generate unique, roughly time-ordered 64-bit ids on many servers without coordination: 41 bits of time, 10 bits of machine id, 12 bits of sequence.",
                "Signal: pack timestamp, machine id and a per-millisecond sequence into one long with bit shifts.",
                null,
                """
                class SnowflakeIdGenerator {
                    private static final long EPOCH = 1_700_000_000_000L;       // custom start date keeps the timestamp small
                    private static final int MACHINE_BITS = 10, SEQUENCE_BITS = 12;
                    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

                    private final long machineId;
                    private long lastTimestamp = -1;
                    private long sequence = 0;

                    SnowflakeIdGenerator(long machineId) {
                        if (machineId < 0 || machineId >= (1L << MACHINE_BITS)) throw new IllegalArgumentException("machineId");
                        this.machineId = machineId;
                    }

                    synchronized long nextId() {
                        long now = System.currentTimeMillis();
                        if (now < lastTimestamp) throw new IllegalStateException("clock moved backwards");
                        if (now == lastTimestamp) {
                            sequence = (sequence + 1) & MAX_SEQUENCE;               // up to 4096 ids per millisecond
                            if (sequence == 0) {                                    // exhausted: wait for the next millisecond
                                while ((now = System.currentTimeMillis()) <= lastTimestamp) { }
                            }
                        } else {
                            sequence = 0;
                        }
                        lastTimestamp = now;
                        return ((now - EPOCH) << (MACHINE_BITS + SEQUENCE_BITS)) | (machineId << SEQUENCE_BITS) | sequence;
                    }
                }""",
                "Auto-increment ids need one central database. A Snowflake id is built locally: the time part makes ids sortable by creation time, the machine part guarantees two machines never collide, and the sequence part separates ids created within the same millisecond.",
                """
                (now - EPOCH) << 22 ==> Timestamp in the highest bits, so larger ids are newer.
                machineId << 12 ==> The next 10 bits identify the server (up to 1024 servers).
                sequence ==> The lowest 12 bits count ids within one millisecond.
                (sequence + 1) & MAX_SEQUENCE ==> Wraps to 0 after 4095; then we must wait for the next millisecond.
                now < lastTimestamp ==> A clock that goes backwards could create duplicates, so refuse to generate.""",
                "Discuss clock skew, machine id assignment (ZooKeeper, config), UUID alternatives (random, 128-bit, not sortable) and ULIDs. This is how Twitter and Discord generate ids.",
                "O(1)", "O(1)", "Ignoring clock rollback; not synchronising the method.");

        full(l, "Delayed Job Scheduler", "Website: Backend Jobs", H, "website,design,heap,concurrency",
                "Build a scheduler where schedule(task, delayMs) runs a task after its delay. Many tasks may be waiting; execution should use as few threads as possible.",
                "Signal: a priority queue ordered by due time and a worker that sleeps only until the earliest task.",
                null,
                """
                class Scheduler {
                    private record Job(long dueAt, Runnable task) {}

                    private final PriorityQueue<Job> queue = new PriorityQueue<>(Comparator.comparingLong(Job::dueAt));
                    private final Object lock = new Object();
                    private final Thread worker;
                    private volatile boolean running = true;

                    Scheduler() {
                        worker = new Thread(this::loop, "scheduler");
                        worker.start();
                    }

                    void schedule(Runnable task, long delayMs) {
                        synchronized (lock) {
                            queue.add(new Job(System.currentTimeMillis() + delayMs, task));
                            lock.notifyAll();                                      // wake the worker: the earliest job may have changed
                        }
                    }

                    private void loop() {
                        synchronized (lock) {
                            while (running) {
                                try {
                                    if (queue.isEmpty()) { lock.wait(); continue; }
                                    long wait = queue.peek().dueAt() - System.currentTimeMillis();
                                    if (wait > 0) { lock.wait(wait); continue; }
                                    Job job = queue.poll();
                                    new Thread(job.task()).start();                // run outside the lock (use a pool in production)
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return;
                                }
                            }
                        }
                    }

                    void shutdown() {
                        synchronized (lock) { running = false; lock.notifyAll(); }
                    }
                }""",
                "One thread is enough to watch many delayed jobs. The heap keeps the next due job on top, so the worker sleeps exactly until then. When a new job is scheduled that might be earlier than the current one, notifyAll wakes the worker to recompute its sleep time.",
                """
                PriorityQueue ordered by dueAt ==> The earliest job is always peek().
                lock.wait(wait) ==> Sleep until the first job is due (or until woken).
                lock.notifyAll() in schedule ==> A new earlier job requires re-evaluating how long to sleep.
                while (running) with wait/notify ==> The standard monitor pattern; always re-check conditions in a loop.
                new Thread(job.task()) ==> Do not run slow user code while holding the lock; production code uses a thread pool.""",
                "Know the JDK equivalent: ScheduledThreadPoolExecutor (and DelayQueue). Distributed schedulers add persistence and leader election (Quartz, Redis sorted sets).",
                "O(log n) schedule", "O(jobs)", "Busy-waiting with Thread.sleep(1); forgetting to wake the worker for earlier jobs.");

        full(l, "Pub/Sub Event Bus", "Website: Backend Messaging", H, "website,design,concurrency,observer",
                "Implement an in-memory event bus: subscribe(topic, handler), publish(topic, message). Slow subscribers must not block publishers, and one failing handler must not stop the others.",
                "Signal: observer pattern + a thread pool; isolate handler failures.",
                null,
                """
                class EventBus {
                    private final Map<String, List<Consumer<String>>> handlers = new ConcurrentHashMap<>();
                    private final ExecutorService pool = Executors.newFixedThreadPool(4);

                    void subscribe(String topic, Consumer<String> handler) {
                        handlers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(handler);
                    }

                    void publish(String topic, String message) {
                        for (Consumer<String> h : handlers.getOrDefault(topic, List.of())) {
                            pool.submit(() -> {
                                try {
                                    h.accept(message);
                                } catch (RuntimeException e) {
                                    System.err.println("handler failed on " + topic + ": " + e);   // isolate the failure
                                }
                            });
                        }
                    }

                    void shutdown() { pool.shutdown(); }
                }""",
                "Publishers and subscribers are decoupled: publish only submits work. CopyOnWriteArrayList lets threads iterate the subscriber list while others subscribe (reads are far more common than writes). Each delivery has its own try/catch so one bad subscriber cannot break the rest.",
                """
                ConcurrentHashMap + CopyOnWriteArrayList ==> Thread-safe registry optimised for many reads.
                computeIfAbsent(topic, ...) ==> Create the subscriber list lazily.
                pool.submit(...) ==> Delivery happens on worker threads, so a slow handler does not block publish().
                try { h.accept(...) } catch ==> A failure is logged and contained.
                Ordering ==> A pool does not guarantee per-subscriber order; use one single-thread executor per subscriber if order matters.""",
                "Discuss delivery guarantees (at-most-once vs at-least-once), back-pressure (bounded queues), ordering and persistence (Kafka, RabbitMQ). Spring's ApplicationEventPublisher is the same idea.",
                "O(subscribers)", "O(subscribers)", "Running handlers on the publisher's thread; one exception killing the loop.");

        full(l, "Key-Value Store with Write-Ahead Log", "Website: Storage", H, "website,storage,durability,files",
                "Build a key-value store that survives crashes: every put/delete is appended to a log file before it changes memory, and on startup the log is replayed to rebuild the data.",
                "Signal: write-ahead logging - durability comes from the append-only log; the in-memory map is only a cache of it.",
                null,
                """
                class WalStore {
                    private final Map<String, String> data = new HashMap<>();
                    private final Path logFile;
                    private BufferedWriter log;

                    WalStore(Path logFile) throws IOException {
                        this.logFile = logFile;
                        recover();
                        this.log = Files.newBufferedWriter(logFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    }

                    synchronized void put(String key, String value) throws IOException {
                        append("PUT\\t" + escape(key) + "\\t" + escape(value));      // 1) durable first
                        data.put(key, value);                                         // 2) then apply
                    }

                    synchronized void delete(String key) throws IOException {
                        append("DEL\\t" + escape(key));
                        data.remove(key);
                    }

                    synchronized String get(String key) { return data.get(key); }

                    private void append(String line) throws IOException {
                        log.write(line);
                        log.newLine();
                        log.flush();                                                  // production: also fsync (FileChannel.force)
                    }

                    private void recover() throws IOException {
                        if (!Files.exists(logFile)) return;
                        for (String line : Files.readAllLines(logFile)) {
                            String[] p = line.split("\\t", -1);
                            if (p[0].equals("PUT") && p.length == 3) data.put(unescape(p[1]), unescape(p[2]));
                            else if (p[0].equals("DEL") && p.length == 2) data.remove(unescape(p[1]));
                            // a torn last line (crash mid-write) simply fails the checks above and is ignored
                        }
                    }

                    private static String escape(String s) { return s.replace("\\\\", "\\\\\\\\").replace("\\t", "\\\\t").replace("\\n", "\\\\n"); }
                    private static String unescape(String s) {
                        return s.replace("\\\\n", "\\n").replace("\\\\t", "\\t").replace("\\\\\\\\", "\\\\");
                    }
                }""",
                "If the process crashes after changing memory but before saving, data is lost. Writing the log record FIRST guarantees that any operation the caller saw succeed can be replayed. Replaying the log on start rebuilds the exact state; a half-written last line is ignored. Real stores add checksums, log compaction and snapshots.",
                """
                append(...) before data.put ==> The write-ahead rule.
                BufferedWriter with CREATE and APPEND ==> The log only grows; existing records are never rewritten.
                log.flush() ==> Pushes bytes to the OS; true durability also needs fsync via FileChannel.force.
                recover() ==> Replays every record in order to rebuild the map.
                escape/unescape ==> Tabs and newlines in keys or values would otherwise corrupt the log format.""",
                "Relate it to database internals (WAL in PostgreSQL, redo log in MySQL), Redis AOF and Kafka. Mention compaction/snapshotting so the log does not grow forever.",
                "O(1) per op", "O(keys)", "Updating memory before the log; trusting flush() as a disk sync.");

        full(l, "Merge Log Streams from K Servers", "Website: Observability", H, "website,heap,streams,iterators",
                "Each of k servers produces log lines already sorted by timestamp. Produce a single time-ordered stream lazily, without loading everything into memory.",
                "Signal: a min-heap of the current line of each stream - an iterator-based k-way merge.",
                null,
                """
                class MergedLogs implements Iterator<String[]> {
                    private record Head(long time, String line, Iterator<String[]> source) {}

                    private final PriorityQueue<Head> heap = new PriorityQueue<>(Comparator.comparingLong(Head::time));

                    MergedLogs(List<Iterator<String[]>> streams) {
                        for (Iterator<String[]> it : streams) pushNext(it);
                    }

                    private void pushNext(Iterator<String[]> it) {
                        if (it.hasNext()) {
                            String[] entry = it.next();                           // {timestamp, line}
                            heap.add(new Head(Long.parseLong(entry[0]), entry[1], it));
                        }
                    }

                    @Override public boolean hasNext() { return !heap.isEmpty(); }

                    @Override public String[] next() {
                        Head h = heap.poll();                                    // the earliest line across all servers
                        pushNext(h.source());                                    // refill from the same server
                        return new String[] { String.valueOf(h.time()), h.line() };
                    }
                }""",
                "The heap never holds more than k lines (one per stream), so memory is O(k) no matter how large the logs are. After emitting the smallest line, only that stream's next line needs to be loaded.",
                """
                Head(time, line, source) ==> The current front line of one stream plus the iterator to get its next line.
                PriorityQueue by time ==> Always exposes the globally earliest line.
                pushNext(h.source()) ==> Replace the emitted line with the next one from the same server.
                implements Iterator ==> Lazy: nothing is read until the consumer asks.
                O(k) memory ==> Works for terabytes of logs.""",
                "A real-world version of Merge K Sorted Lists. Mention external merge sort and tools such as Kafka partitions with timestamps. Handle clock skew between servers.",
                "O(N log k)", "O(k)", "Reading all logs into memory and sorting.");

        full(l, "Top K Heavy Hitters in a Stream (Misra-Gries)", "Website: Analytics", H, "website,streaming,algorithms,hash-map",
                "Find the items that occur more than n/k times in a huge stream (for example the most visited pages) using only O(k) memory and one pass.",
                "Signal: keep at most k - 1 counters; a new item that does not fit decrements ALL counters (the Misra-Gries summary).",
                null,
                """
                class HeavyHitters {
                    private final int capacity;                                   // k - 1 counters
                    private final Map<String, Integer> counters = new HashMap<>();

                    HeavyHitters(int k) { this.capacity = k - 1; }

                    void add(String item) {
                        if (counters.containsKey(item)) {
                            counters.merge(item, 1, Integer::sum);
                        } else if (counters.size() < capacity) {
                            counters.put(item, 1);
                        } else {
                            Iterator<Map.Entry<String, Integer>> it = counters.entrySet().iterator();
                            while (it.hasNext()) {                                // decrement everything; drop zeros
                                Map.Entry<String, Integer> e = it.next();
                                if (e.getValue() == 1) it.remove();
                                else e.setValue(e.getValue() - 1);
                            }
                        }
                    }

                    /** Candidates that may be frequent; a second pass over the data confirms the exact counts. */
                    Set<String> candidates() { return counters.keySet(); }
                }""",
                "Every time a new item causes a decrement of k distinct items, k occurrences are removed from the multiset. This can happen at most n/k times, so any item occurring more than n/k times must survive in the counters. The result is a candidate set that may include false positives, verified with a second pass if exact answers are needed.",
                """
                capacity = k - 1 ==> The number of counters, independent of the stream size.
                counters.containsKey(item) ==> Existing item: increase its count.
                counters.size() < capacity ==> Free slot: start counting the item.
                else decrement all ==> No room: remove one occurrence of every tracked item (and of the new item).
                candidates() ==> A superset of the true heavy hitters.""",
                "Alternatives: Count-Min Sketch (approximate counts) and a heap for exact top-k when memory allows. Know the 1D case: Boyer-Moore majority vote (k = 2).",
                "O(k) per item worst, O(1) amortised", "O(k)", "Believing the counters are exact counts.");

        full(l, "Real-time Leaderboard with Ranks (Fenwick Tree)", "Website: Leaderboard", H, "website,fenwick-tree,design,ranking",
                "Players have integer scores between 0 and MAX. Support updateScore(player, newScore) and rank(player) (1 = best, ties share a rank) in O(log MAX).",
                "Signal: a Fenwick tree over score values counting how many players have each score; rank = 1 + number of players with a higher score.",
                null,
                """
                class Leaderboard {
                    private final int max;
                    private final int[] tree;                                    // Fenwick tree over scores 0..max
                    private final Map<String, Integer> score = new HashMap<>();

                    Leaderboard(int maxScore) {
                        this.max = maxScore;
                        this.tree = new int[maxScore + 2];
                    }

                    void updateScore(String player, int newScore) {
                        Integer old = score.put(player, newScore);
                        if (old != null) add(old, -1);                           // remove from the old score bucket
                        add(newScore, +1);                                       // insert into the new one
                    }

                    int rank(String player) {
                        int s = score.get(player);
                        int atOrBelow = prefix(s);                              // players with score <= s
                        int total = prefix(max);
                        return total - atOrBelow + 1;                           // players strictly better, plus one
                    }

                    private void add(int scoreValue, int delta) {
                        for (int i = scoreValue + 1; i < tree.length; i += i & -i) tree[i] += delta;
                    }
                    private int prefix(int scoreValue) {
                        int sum = 0;
                        for (int i = scoreValue + 1; i > 0; i -= i & -i) sum += tree[i];
                        return sum;
                    }
                }""",
                "Sorting all players for every rank request is O(n log n). A Fenwick tree over score values answers 'how many players have a score at most s' in O(log MAX) and supports updates just as fast. Ranking by counts naturally gives tied players the same rank. This is the approach behind game leaderboards before Redis sorted sets.",
                """
                tree ==> Fenwick tree indexed by score (+1 because Fenwick trees are 1-indexed).
                updateScore ==> Move one player from the old bucket to the new bucket.
                prefix(s) ==> Count of players with score <= s.
                total - atOrBelow + 1 ==> The number of players with a strictly higher score, plus one.
                Map<String,Integer> score ==> Remember each player's current score.""",
                "Mention Redis ZSET (ZREVRANK) as the production answer, and what to do when scores are huge (coordinate compression or order-statistic trees).",
                "O(log MAX)", "O(MAX + players)", "Using list.sort per request; giving tied players different ranks.");

        full(l, "Ticket Booking with Waitlist", "Website: Booking", H, "website,design,queue,concurrency",
                "An event has limited seats. book(user) confirms a seat if available, otherwise puts the user on a FIFO waitlist. cancel(user) frees a seat and automatically promotes the first waitlisted user.",
                "Signal: confirmed set + waitlist queue, with every state change in one synchronized block.",
                null,
                """
                class EventBooking {
                    enum Result { CONFIRMED, WAITLISTED, ALREADY_BOOKED }

                    private final int capacity;
                    private final Set<String> confirmed = new LinkedHashSet<>();
                    private final Deque<String> waitlist = new ArrayDeque<>();
                    private final Set<String> waiting = new HashSet<>();                 // O(1) membership for the queue

                    EventBooking(int capacity) { this.capacity = capacity; }

                    synchronized Result book(String user) {
                        if (confirmed.contains(user) || waiting.contains(user)) return Result.ALREADY_BOOKED;
                        if (confirmed.size() < capacity) {
                            confirmed.add(user);
                            return Result.CONFIRMED;
                        }
                        waitlist.addLast(user);
                        waiting.add(user);
                        return Result.WAITLISTED;
                    }

                    /** Returns the user promoted from the waitlist, or null. */
                    synchronized String cancel(String user) {
                        if (waiting.remove(user)) {                                       // cancelling while waiting
                            waitlist.remove(user);
                            return null;
                        }
                        if (!confirmed.remove(user)) return null;
                        String next = waitlist.pollFirst();
                        if (next != null) {
                            waiting.remove(next);
                            confirmed.add(next);
                        }
                        return next;
                    }
                }""",
                "Booking, cancelling and promoting must be one atomic step, otherwise two cancellations could promote the same person or a seat could be double-sold. The waiting set keeps duplicate checks O(1). In a real system the same rules are enforced in the database with a transaction and a unique (event, user) constraint.",
                """
                confirmed.size() < capacity ==> Seats left: confirm immediately.
                waitlist (FIFO) ==> Fairness: first come, first promoted.
                synchronized ==> All state changes happen together; no half-updated state is visible.
                cancel from waitlist ==> A waiting user can leave without affecting seats.
                waitlist.pollFirst() ==> Promote the longest-waiting user into the freed seat.""",
                "Discuss notifying the promoted user (an event), payment timeouts (hold a seat for N minutes) and the SQL version using SELECT ... FOR UPDATE SKIP LOCKED.",
                "O(1) (O(n) waitlist removal)", "O(users)", "Letting promotion and cancellation race; forgetting duplicate bookings.");

        full(l, "Search Engine: Inverted Index with TF-IDF", "Website: Search", H, "website,search,indexing,scoring",
                "Build a tiny search engine: index documents, then search(query, k) returns the top k documents ranked by TF-IDF.",
                "Signal: an inverted index maps each term to the documents (and counts) containing it; TF-IDF rewards frequent-in-document and rare-in-corpus terms.",
                null,
                """
                class SearchEngine {
                    private final Map<String, Map<Integer, Integer>> index = new HashMap<>();   // term -> (docId -> term frequency)
                    private final Map<Integer, Integer> docLength = new HashMap<>();
                    private int docs = 0;

                    void addDocument(int id, String text) {
                        String[] terms = tokenize(text);
                        docLength.put(id, terms.length);
                        docs++;
                        for (String t : terms) index.computeIfAbsent(t, k -> new HashMap<>()).merge(id, 1, Integer::sum);
                    }

                    List<Integer> search(String query, int k) {
                        Map<Integer, Double> scores = new HashMap<>();
                        for (String term : tokenize(query)) {
                            Map<Integer, Integer> postings = index.get(term);
                            if (postings == null) continue;
                            double idf = Math.log((double) docs / postings.size()) + 1;          // rare terms weigh more
                            for (var e : postings.entrySet()) {
                                double tf = (double) e.getValue() / docLength.get(e.getKey());   // share of the document
                                scores.merge(e.getKey(), tf * idf, Double::sum);
                            }
                        }
                        return scores.entrySet().stream()
                                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                                .limit(k).map(Map.Entry::getKey).toList();
                    }

                    private String[] tokenize(String s) {
                        return Arrays.stream(s.toLowerCase().split("[^a-z0-9]+")).filter(w -> !w.isEmpty()).toArray(String[]::new);
                    }
                }""",
                "Scanning every document per query is O(total text). The inverted index jumps directly to the documents containing each query word. TF (term frequency) says how important the word is inside a document, IDF (inverse document frequency) says how informative the word is across the corpus: 'the' appears everywhere and scores low, 'kubernetes' is rare and scores high.",
                """
                index: term -> (doc -> count) ==> The inverted index; its entries are called postings.
                tf = count / docLength ==> Normalised so long documents do not win just by being long.
                idf = ln(N / df) + 1 ==> Rarer terms (small df) get a bigger weight.
                scores.merge(doc, tf * idf, Double::sum) ==> Sum the contribution of every query term per document.
                sorted(...).limit(k) ==> Rank and return the top results.""",
                "Name Lucene / Elasticsearch, BM25 (a better scoring function), stemming, stop words and sharding the index. A very strong project talking point.",
                "O(postings touched)", "O(total terms)", "Scoring every document; no length normalisation.");

        full(l, "Spell Checker Suggestions (Edit Distance 1)", "Website: Search", H, "website,strings,hash-set,search",
                "Given a dictionary and a misspelled word, return dictionary words within edit distance 1 (insert, delete, replace or swap two adjacent letters), most frequent first.",
                "Signal: generate all possible one-edit variants of the word and look each up in a hash map of word frequencies.",
                null,
                """
                static List<String> suggest(String word, Map<String, Integer> dictionary) {
                    if (dictionary.containsKey(word)) return List.of(word);
                    Set<String> candidates = new HashSet<>();
                    String letters = "abcdefghijklmnopqrstuvwxyz";
                    for (int i = 0; i <= word.length(); i++) {
                        String left = word.substring(0, i), right = word.substring(i);
                        if (!right.isEmpty()) candidates.add(left + right.substring(1));                       // deletion
                        if (right.length() > 1) candidates.add(left + right.charAt(1) + right.charAt(0) + right.substring(2));   // transposition
                        for (char c : letters.toCharArray()) {
                            if (!right.isEmpty()) candidates.add(left + c + right.substring(1));              // replacement
                            candidates.add(left + c + right);                                                   // insertion
                        }
                    }
                    return candidates.stream()
                            .filter(dictionary::containsKey)
                            .sorted(Comparator.comparingInt((String w) -> dictionary.get(w)).reversed())
                            .toList();
                }""",
                "For a word of length n there are only about 54n + 25 words at distance 1. Instead of comparing the typo with every dictionary word (edit distance DP for each), generate those variants and test membership in O(1). This is Peter Norvig's classic spell corrector.",
                """
                left / right split ==> Every position where an edit can happen.
                left + right.substring(1) ==> Deletion of one letter.
                left + right[1] + right[0] + rest ==> Transposition of two adjacent letters.
                left + c + right.substring(1) ==> Replacement with each letter.
                left + c + right ==> Insertion.
                sorted by dictionary frequency ==> The most common word is the most likely intended one.""",
                "Extend to distance 2 (apply the function twice) and discuss BK-trees and SymSpell for large dictionaries.",
                "O(26 x n x lookup)", "O(26 x n)", "Computing edit distance against the whole dictionary.");

        full(l, "Build System: Parallel Build Time (Critical Path)", "Website: DevOps", H, "website,graph,topological-sort,dp",
                "Modules have build times and dependencies (A needs B built first). With unlimited parallel workers, how long does the whole build take? Report an error on a dependency cycle.",
                "Signal: the longest path in a DAG (critical path) - compute earliest finish times in topological order.",
                null,
                """
                static int totalBuildTime(Map<String, Integer> time, Map<String, List<String>> dependsOn) {
                    Map<String, Integer> indegree = new HashMap<>();
                    Map<String, List<String>> dependents = new HashMap<>();
                    for (String m : time.keySet()) indegree.put(m, 0);
                    for (var e : dependsOn.entrySet()) {
                        for (String dep : e.getValue()) {
                            dependents.computeIfAbsent(dep, k -> new ArrayList<>()).add(e.getKey());
                            indegree.merge(e.getKey(), 1, Integer::sum);
                        }
                    }
                    Map<String, Integer> finish = new HashMap<>();
                    Deque<String> ready = new ArrayDeque<>();
                    for (var e : indegree.entrySet()) if (e.getValue() == 0) { ready.add(e.getKey()); finish.put(e.getKey(), time.get(e.getKey())); }

                    int processed = 0, longest = 0;
                    while (!ready.isEmpty()) {
                        String m = ready.poll();
                        processed++;
                        longest = Math.max(longest, finish.get(m));
                        for (String next : dependents.getOrDefault(m, List.of())) {
                            finish.merge(next, finish.get(m) + time.get(next), Math::max);     // starts after its slowest dependency
                            if (indegree.merge(next, -1, Integer::sum) == 0) ready.add(next);
                        }
                    }
                    if (processed != time.size()) throw new IllegalStateException("dependency cycle detected");
                    return longest;
                }""",
                "A module can start only when ALL its dependencies have finished, so its finish time is its own build time plus the maximum finish time among its dependencies. Kahn's algorithm processes modules in dependency order; modules never processed prove a cycle.",
                """
                indegree ==> How many unfinished dependencies each module still has.
                ready queue ==> Modules whose dependencies are all done.
                finish.merge(next, finish[m] + time[next], Math::max) ==> Keep the LATEST of all dependency finish times plus own time.
                longest ==> The end of the critical path: total build time with unlimited workers.
                processed != size ==> Some modules never became ready, so there is a cycle.""",
                "Same idea as Course Schedule, plus weights. With a limited number of workers the problem becomes scheduling (NP-hard in general). Maven, Gradle and Bazel do exactly this.",
                "O(V + E)", "O(V + E)", "Summing all build times; ignoring cycles.");

        full(l, "Optimistic Locking Simulation (Versioned Updates)", "Website: Databases", H, "website,concurrency,database,design",
                "Two users edit the same record concurrently. Implement update(id, expectedVersion, newValue) that succeeds only if the stored version still equals expectedVersion (like JPA @Version), and a helper that retries.",
                "Signal: compare-and-set on a version number instead of locking rows.",
                null,
                """
                class VersionedStore {
                    record Record(String value, long version) {}

                    private final ConcurrentHashMap<Integer, Record> rows = new ConcurrentHashMap<>();

                    Record read(int id) { return rows.get(id); }

                    void insert(int id, String value) { rows.put(id, new Record(value, 0)); }

                    /** Equivalent to: UPDATE t SET value = ?, version = version + 1 WHERE id = ? AND version = ? */
                    boolean update(int id, long expectedVersion, String newValue) {
                        Record current = rows.get(id);
                        if (current == null || current.version() != expectedVersion) return false;
                        Record next = new Record(newValue, expectedVersion + 1);
                        return rows.replace(id, current, next);                        // atomic compare-and-set
                    }

                    /** Read-modify-write with retries on conflict. */
                    void updateWithRetry(int id, UnaryOperator<String> change, int maxAttempts) {
                        for (int i = 0; i < maxAttempts; i++) {
                            Record r = read(id);
                            if (update(id, r.version(), change.apply(r.value()))) return;
                        }
                        throw new IllegalStateException("too many conflicting updates for id " + id);
                    }
                }""",
                "Pessimistic locks block other users while one edits. Optimistic locking lets everyone read freely and detects a conflict at write time: if the version changed, someone else saved first and the update is rejected (HTTP 409 or an OptimisticLockException). replace(key, old, new) makes the check-and-write atomic, exactly like the SQL WHERE version = ? clause.",
                """
                Record(value, version) ==> Each row carries a version counter.
                current.version() != expectedVersion ==> The row changed since the caller read it: reject.
                rows.replace(id, current, next) ==> Atomic: succeeds only if the map still holds the same record, so two racing updates cannot both win.
                updateWithRetry ==> Re-read the latest data and re-apply the change after a conflict.
                SQL comment ==> The database version is a conditional UPDATE; check the affected row count is 1.""",
                "Compare with pessimistic locking (SELECT FOR UPDATE), explain the lost-update problem and when each approach is appropriate (optimistic for low contention, pessimistic for high). Mention @Version in JPA.",
                "O(1)", "O(rows)", "Read-then-write without a version check (lost update).");

        return l;
    }
}
