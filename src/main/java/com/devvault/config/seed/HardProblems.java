package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.add;

/** 30 of the toughest problems: DP, graphs, design and real-system scale questions. */
public final class HardProblems {

    private HardProblems() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty H = Difficulty.HARD;

        add(l, "Edit Distance (Spell Checker)", "Dynamic Programming", H, "dp,strings,website,search",
                "Minimum insert/delete/replace operations to turn word1 into word2 - the idea behind 'did you mean...?' suggestions.",
                "Signal: two strings + minimum operations -> 2D DP over prefixes.",
                null,
                """
                static int editDistance(String a, String b) {
                    int[][] dp = new int[a.length() + 1][b.length() + 1];
                    for (int i = 0; i <= a.length(); i++) dp[i][0] = i;     // delete everything
                    for (int j = 0; j <= b.length(); j++) dp[0][j] = j;     // insert everything
                    for (int i = 1; i <= a.length(); i++) {
                        for (int j = 1; j <= b.length(); j++) {
                            if (a.charAt(i - 1) == b.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1];
                            else dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                        }
                    }
                    return dp[a.length()][b.length()];
                }""",
                "dp[i][j] = cost to convert the first i chars of a into the first j chars of b. Diagonal = replace/match, up = delete, left = insert. Keep two rows if memory matters.",
                "O(n x m)", "O(n x m)", "Off-by-one between string index and dp index; wrong base rows.");

        add(l, "Regular Expression Matching (Route Patterns)", "Dynamic Programming", H, "dp,strings,regex,website",
                "Implement matching with '.' (any single char) and '*' (zero or more of the previous element) over the ENTIRE string.",
                "Signal: a pattern with choices at '*' -> DP over (string index, pattern index).",
                null,
                """
                static boolean isMatch(String s, String p) {
                    boolean[][] dp = new boolean[s.length() + 1][p.length() + 1];
                    dp[0][0] = true;
                    for (int j = 2; j <= p.length(); j++) if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 2];
                    for (int i = 1; i <= s.length(); i++) {
                        for (int j = 1; j <= p.length(); j++) {
                            char pc = p.charAt(j - 1);
                            if (pc == '*') {
                                dp[i][j] = dp[i][j - 2];                                   // zero copies
                                char prev = p.charAt(j - 2);
                                if (prev == '.' || prev == s.charAt(i - 1)) dp[i][j] |= dp[i - 1][j];   // one more copy
                            } else if (pc == '.' || pc == s.charAt(i - 1)) {
                                dp[i][j] = dp[i - 1][j - 1];
                            }
                        }
                    }
                    return dp[s.length()][p.length()];
                }""",
                "At '*' you either use zero copies (skip two pattern chars) or consume one more matching char and stay on the same pattern position.",
                "O(n x m)", "O(n x m)", "Treating '*' like a shell wildcard; partial matches.");

        add(l, "Wildcard Matching (URL Routes)", "Dynamic Programming", H, "dp,greedy,strings,website",
                "Match a string against a pattern where '?' matches any one char and '*' matches any sequence (including empty). Whole string must match.",
                "Signal: '*' can swallow variable length -> DP, or a greedy two-pointer with backtracking to the last star.",
                null,
                """
                static boolean isMatch(String s, String p) {
                    int i = 0, j = 0, star = -1, mark = 0;
                    while (i < s.length()) {
                        if (j < p.length() && (p.charAt(j) == '?' || p.charAt(j) == s.charAt(i))) { i++; j++; }
                        else if (j < p.length() && p.charAt(j) == '*') { star = j++; mark = i; }
                        else if (star != -1) { j = star + 1; i = ++mark; }      // let the star eat one more char
                        else return false;
                    }
                    while (j < p.length() && p.charAt(j) == '*') j++;
                    return j == p.length();
                }""",
                "Remember the last '*' and where it started. On a mismatch, extend what the star swallows by one character and retry - no recursion needed.",
                "O(n x m) worst, O(n + m) typical", "O(1)", "Resetting both pointers instead of only the star's progress.");

        add(l, "Largest Rectangle in Histogram", "Stack", H, "monotonic-stack,arrays",
                "Given bar heights, find the area of the largest rectangle in the histogram.",
                "Signal: for each bar find the nearest smaller bar on each side -> monotonic increasing stack.",
                null,
                """
                static int largest(int[] h) {
                    Deque<Integer> stack = new ArrayDeque<>();
                    int best = 0;
                    for (int i = 0; i <= h.length; i++) {
                        int cur = i == h.length ? 0 : h[i];              // sentinel flushes the stack
                        while (!stack.isEmpty() && cur < h[stack.peek()]) {
                            int height = h[stack.pop()];
                            int left = stack.isEmpty() ? -1 : stack.peek();
                            best = Math.max(best, height * (i - left - 1));
                        }
                        stack.push(i);
                    }
                    return best;
                }""",
                "A bar's rectangle extends until a shorter bar on either side. The stack holds bars waiting for their right boundary; popping computes their area.",
                "O(n)", "O(n)", "Forgetting the final flush; width formula when the stack becomes empty.");

        add(l, "Merge k Sorted Lists", "Heap", H, "heap,linked-list,divide-and-conquer",
                "Merge k sorted linked lists into one sorted list.",
                "Signal: k sorted sources -> a min-heap of the current heads.",
                null,
                """
                static ListNode mergeK(ListNode[] lists) {
                    PriorityQueue<ListNode> heap = new PriorityQueue<>(Comparator.comparingInt(n -> n.val));
                    for (ListNode n : lists) if (n != null) heap.add(n);
                    ListNode dummy = new ListNode(0), tail = dummy;
                    while (!heap.isEmpty()) {
                        ListNode n = heap.poll();
                        tail.next = n;
                        tail = n;
                        if (n.next != null) heap.add(n.next);
                    }
                    return dummy.next;
                }""",
                "The heap holds at most k nodes, so each of the N nodes costs O(log k).",
                "O(N log k)", "O(k)", "Adding null heads to the heap.");

        add(l, "Reverse Nodes in k-Group", "Linked List", H, "linked-list,pointers",
                "Reverse the nodes of a linked list k at a time; leftover nodes (< k) stay as they are.",
                "Signal: reverse a bounded segment repeatedly -> check k nodes exist, reverse, reconnect.",
                null,
                """
                static ListNode reverseKGroup(ListNode head, int k) {
                    ListNode dummy = new ListNode(0);
                    dummy.next = head;
                    ListNode groupPrev = dummy;
                    while (true) {
                        ListNode kth = groupPrev;
                        for (int i = 0; i < k && kth != null; i++) kth = kth.next;
                        if (kth == null) break;                           // fewer than k left
                        ListNode groupNext = kth.next;
                        ListNode prev = groupNext, cur = groupPrev.next;
                        while (cur != groupNext) {
                            ListNode next = cur.next;
                            cur.next = prev;
                            prev = cur;
                            cur = next;
                        }
                        ListNode oldFirst = groupPrev.next;
                        groupPrev.next = kth;
                        groupPrev = oldFirst;
                    }
                    return dummy.next;
                }""",
                "A dummy head removes special cases. Pre-pointing `prev` at groupNext makes the reversed segment link to the rest automatically.",
                "O(n)", "O(1)", "Reversing a final short group; losing the connection between groups.");

        add(l, "Word Search II (Trie + Backtracking)", "Trie", H, "trie,backtracking,grid",
                "Given a letter grid and a list of words, return all words that can be formed by adjacent cells (no cell reused in one word).",
                "Signal: many words searched in one grid -> build a trie of the words and DFS the grid once.",
                null,
                """
                static class Node { Node[] next = new Node[26]; String word; }

                static List<String> findWords(char[][] board, String[] words) {
                    Node root = new Node();
                    for (String w : words) {
                        Node n = root;
                        for (char c : w.toCharArray()) {
                            if (n.next[c - 'a'] == null) n.next[c - 'a'] = new Node();
                            n = n.next[c - 'a'];
                        }
                        n.word = w;
                    }
                    List<String> out = new ArrayList<>();
                    for (int r = 0; r < board.length; r++)
                        for (int c = 0; c < board[0].length; c++) dfs(board, r, c, root, out);
                    return out;
                }
                static void dfs(char[][] b, int r, int c, Node parent, List<String> out) {
                    if (r < 0 || c < 0 || r >= b.length || c >= b[0].length || b[r][c] == '#') return;
                    char ch = b[r][c];
                    Node n = parent.next[ch - 'a'];
                    if (n == null) return;                         // prune: no word has this prefix
                    if (n.word != null) { out.add(n.word); n.word = null; }   // avoid duplicates
                    b[r][c] = '#';
                    dfs(b, r + 1, c, n, out); dfs(b, r - 1, c, n, out); dfs(b, r, c + 1, n, out); dfs(b, r, c - 1, n, out);
                    b[r][c] = ch;
                }""",
                "Searching each word separately repeats the same grid walks. The trie lets one DFS test every word at once and prune dead prefixes immediately.",
                "O(cells x 4 x 3^(L-1))", "O(total word characters)", "Not restoring the cell after backtracking; returning duplicate words.");

        add(l, "Alien Dictionary (Topological Order)", "Graphs", H, "graph,topological-sort,strings",
                "Given words sorted by an unknown alphabet, derive a valid letter order, or \"\" if inconsistent.",
                "Signal: ordering constraints between items -> build a graph from adjacent words and topologically sort it.",
                null,
                """
                static String alienOrder(String[] words) {
                    Map<Character, Set<Character>> adj = new HashMap<>();
                    Map<Character, Integer> indeg = new HashMap<>();
                    for (String w : words) for (char c : w.toCharArray()) { adj.putIfAbsent(c, new HashSet<>()); indeg.putIfAbsent(c, 0); }
                    for (int i = 0; i + 1 < words.length; i++) {
                        String a = words[i], b = words[i + 1];
                        if (a.length() > b.length() && a.startsWith(b)) return "";     // invalid: prefix after longer word
                        for (int j = 0; j < Math.min(a.length(), b.length()); j++) {
                            if (a.charAt(j) != b.charAt(j)) {
                                if (adj.get(a.charAt(j)).add(b.charAt(j))) indeg.merge(b.charAt(j), 1, Integer::sum);
                                break;
                            }
                        }
                    }
                    Queue<Character> q = new ArrayDeque<>();
                    indeg.forEach((c, d) -> { if (d == 0) q.add(c); });
                    StringBuilder out = new StringBuilder();
                    while (!q.isEmpty()) {
                        char c = q.poll();
                        out.append(c);
                        for (char nx : adj.get(c)) if (indeg.merge(nx, -1, Integer::sum) == 0) q.add(nx);
                    }
                    return out.length() == indeg.size() ? out.toString() : "";     // cycle => impossible
                }""",
                "Only the first differing character of adjacent words gives an ordering rule. Kahn's algorithm outputs the order and exposes cycles.",
                "O(total characters)", "O(alphabet^2)", "Using non-adjacent words; missing the prefix edge case.");

        add(l, "Minimum Window Substring", "Sliding Window", H, "sliding-window,hash-map,strings",
                "Find the smallest substring of s that contains every character of t (with multiplicity).",
                "Signal: shortest window satisfying a condition -> expand right until valid, then shrink left while valid.",
                null,
                """
                static String minWindow(String s, String t) {
                    int[] need = new int[128];
                    for (char c : t.toCharArray()) need[c]++;
                    int missing = t.length(), left = 0, bestStart = 0, bestLen = Integer.MAX_VALUE;
                    for (int right = 0; right < s.length(); right++) {
                        if (need[s.charAt(right)]-- > 0) missing--;
                        while (missing == 0) {
                            if (right - left + 1 < bestLen) { bestLen = right - left + 1; bestStart = left; }
                            if (++need[s.charAt(left++)] > 0) missing++;
                        }
                    }
                    return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestStart, bestStart + bestLen);
                }""",
                "need[c] > 0 means that character is still required. Each character enters and leaves the window once.",
                "O(n + m)", "O(1)", "Not handling duplicates in t; shrinking without updating `missing`.");

        add(l, "Sudoku Solver", "Backtracking", H, "backtracking,grid,constraint",
                "Fill the empty cells ('.') of a 9x9 Sudoku so every row, column and 3x3 box has digits 1-9 once.",
                "Signal: constraint satisfaction -> try a digit, recurse, undo on failure.",
                null,
                """
                static boolean solve(char[][] b) {
                    for (int r = 0; r < 9; r++) {
                        for (int c = 0; c < 9; c++) {
                            if (b[r][c] != '.') continue;
                            for (char d = '1'; d <= '9'; d++) {
                                if (ok(b, r, c, d)) {
                                    b[r][c] = d;
                                    if (solve(b)) return true;
                                    b[r][c] = '.';                   // backtrack
                                }
                            }
                            return false;                            // no digit fits -> dead end
                        }
                    }
                    return true;                                     // no empty cell left
                }
                static boolean ok(char[][] b, int r, int c, char d) {
                    for (int i = 0; i < 9; i++) {
                        if (b[r][i] == d || b[i][c] == d) return false;
                        if (b[3 * (r / 3) + i / 3][3 * (c / 3) + i % 3] == d) return false;
                    }
                    return true;
                }""",
                "Upgrade path: keep row/column/box bitmasks and always pick the empty cell with the fewest candidates (MRV heuristic).",
                "O(9^empty) worst", "O(1)", "Returning true too early; forgetting to undo.");

        add(l, "Maximal Rectangle", "Stack", H, "dp,stack,matrix",
                "Find the largest rectangle containing only 1s in a binary matrix.",
                "Signal: each row is a histogram of consecutive 1s -> reuse Largest Rectangle in Histogram.",
                null,
                """
                static int maximalRectangle(char[][] m) {
                    if (m.length == 0) return 0;
                    int[] heights = new int[m[0].length];
                    int best = 0;
                    for (char[] row : m) {
                        for (int c = 0; c < row.length; c++) heights[c] = row[c] == '1' ? heights[c] + 1 : 0;
                        best = Math.max(best, largestInHistogram(heights));
                    }
                    return best;
                }""",
                "Build heights row by row; the histogram routine does the hard part. Reduction to a known problem is the key skill.",
                "O(rows x cols)", "O(cols)", "Recomputing heights from scratch each row.");

        add(l, "Burst Balloons", "Dynamic Programming", H, "dp,interval-dp",
                "Bursting balloon i earns nums[i-1] * nums[i] * nums[i+1]. Burst them all in the best order for maximum coins.",
                "Signal: think about the LAST balloon to burst in a range - it splits the range into independent sides.",
                null,
                """
                static int maxCoins(int[] nums) {
                    int n = nums.length;
                    int[] a = new int[n + 2];
                    a[0] = a[n + 1] = 1;
                    for (int i = 0; i < n; i++) a[i + 1] = nums[i];
                    int[][] dp = new int[n + 2][n + 2];
                    for (int len = 1; len <= n; len++) {
                        for (int left = 1; left + len - 1 <= n; left++) {
                            int right = left + len - 1;
                            for (int k = left; k <= right; k++) {          // k = last balloon burst in [left,right]
                                dp[left][right] = Math.max(dp[left][right],
                                        dp[left][k - 1] + a[left - 1] * a[k] * a[right + 1] + dp[k + 1][right]);
                            }
                        }
                    }
                    return dp[1][n];
                }""",
                "Choosing the FIRST balloon creates dependent sub-problems. Choosing the LAST makes neighbours known (the range borders), so the halves are independent.",
                "O(n^3)", "O(n^2)", "Modelling it as 'first to burst'.");

        add(l, "Find Median from Data Stream (Live Stats)", "Heap", H, "heap,design,streaming,website",
                "Support addNum(x) and findMedian() on a growing stream - e.g. median response time on a live dashboard.",
                "Signal: median = boundary between the smaller half and larger half -> two heaps.",
                null,
                """
                class MedianFinder {
                    private final PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder());  // max-heap
                    private final PriorityQueue<Integer> high = new PriorityQueue<>();                           // min-heap

                    void addNum(int x) {
                        low.add(x);
                        high.add(low.poll());                    // keep every low value <= every high value
                        if (high.size() > low.size()) low.add(high.poll());   // low is never smaller
                    }
                    double findMedian() {
                        return low.size() > high.size() ? low.peek() : (low.peek() + high.peek()) / 2.0;
                    }
                }""",
                "Sorting after each insert is O(n log n). Two heaps give O(log n) insert and O(1) median.",
                "O(log n) add, O(1) median", "O(n)", "Integer division when averaging; unbalanced heaps.");

        add(l, "Design Twitter", "Design", H, "design,heap,hash-map,website",
                "Support postTweet, follow, unfollow and getNewsFeed (10 most recent tweets from you and the people you follow).",
                "Signal: feed = k-way merge of followees' recent tweets (heap) - exactly what social sites do.",
                null,
                """
                class Twitter {
                    private int clock = 0;
                    private final Map<Integer, List<int[]>> tweets = new HashMap<>();     // user -> [time, id]
                    private final Map<Integer, Set<Integer>> following = new HashMap<>();

                    void postTweet(int user, int id) { tweets.computeIfAbsent(user, k -> new ArrayList<>()).add(new int[] { clock++, id }); }
                    void follow(int a, int b) { if (a != b) following.computeIfAbsent(a, k -> new HashSet<>()).add(b); }
                    void unfollow(int a, int b) { if (following.containsKey(a)) following.get(a).remove(b); }

                    List<Integer> getNewsFeed(int user) {
                        Set<Integer> who = new HashSet<>(following.getOrDefault(user, Set.of()));
                        who.add(user);
                        PriorityQueue<int[]> heap = new PriorityQueue<>((x, y) -> y[0] - x[0]);   // newest first
                        for (int u : who) {
                            List<int[]> t = tweets.getOrDefault(u, List.of());
                            for (int i = Math.max(0, t.size() - 10); i < t.size(); i++) heap.add(t.get(i));   // only last 10 matter
                        }
                        List<Integer> feed = new ArrayList<>();
                        while (!heap.isEmpty() && feed.size() < 10) feed.add(heap.poll()[1]);
                        return feed;
                    }
                }""",
                "Only each followee's latest 10 tweets can appear in the feed, so we never read more than 10 x followees. Large systems precompute feeds (fan-out on write) for normal users and merge on read for celebrities.",
                "O(F x 10 log(F x 10))", "O(tweets)", "Using wall-clock time (ties) instead of a monotonic counter; allowing self-follow.");

        add(l, "LRU Cache with TTL (Production Cache)", "Design", H, "design,linked-hash-map,cache,website",
                "Build a cache with capacity limit (evict least recently used) AND per-entry time-to-live, with O(1) get/put.",
                "Signal: LRU = hash map + recency order; TTL = store a deadline and check it lazily on get.",
                null,
                """
                class TtlLruCache<K, V> {
                    private record Entry<V>(V value, long expiresAt) {}
                    private final int capacity;
                    private final LinkedHashMap<K, Entry<V>> map = new LinkedHashMap<>(16, 0.75f, true);   // access order

                    TtlLruCache(int capacity) { this.capacity = capacity; }

                    synchronized void put(K key, V value, long ttlMs, long now) {
                        map.put(key, new Entry<>(value, now + ttlMs));
                        if (map.size() > capacity) {
                            Iterator<K> it = map.keySet().iterator();
                            it.next();
                            it.remove();                                   // eldest = least recently used
                        }
                    }
                    synchronized V get(K key, long now) {
                        Entry<V> e = map.get(key);                          // also refreshes recency
                        if (e == null) return null;
                        if (e.expiresAt() <= now) { map.remove(key); return null; }
                        return e.value();
                    }
                }""",
                "LinkedHashMap(accessOrder=true) gives O(1) LRU. Expiry is lazy; add a background sweeper for entries nobody reads again. Caches like Caffeine implement this far more efficiently.",
                "O(1)", "O(capacity)", "Forgetting thread safety; expiring only on read and never freeing cold keys.");

        add(l, "Search Typeahead System", "Design", H, "design,trie,heap,website",
                "Design autocomplete: after each typed character return the top 3 historical sentences with that prefix, ranked by frequency then alphabetically. '#' ends and records the sentence.",
                "Signal: prefix + ranking -> trie nodes that cache their top candidates.",
                null,
                """
                class Typeahead {
                    private static class Node { Map<Character, Node> kids = new HashMap<>(); Map<String, Integer> counts = new HashMap<>(); }
                    private final Node root = new Node();
                    private Node cur = root;
                    private final StringBuilder typed = new StringBuilder();

                    Typeahead(String[] sentences, int[] times) {
                        for (int i = 0; i < sentences.length; i++) add(sentences[i], times[i]);
                    }
                    private void add(String s, int times) {
                        Node n = root;
                        for (char c : s.toCharArray()) {
                            n = n.kids.computeIfAbsent(c, k -> new Node());
                            n.counts.merge(s, times, Integer::sum);
                        }
                    }
                    List<String> input(char c) {
                        if (c == '#') { add(typed.toString(), 1); typed.setLength(0); cur = root; return List.of(); }
                        typed.append(c);
                        cur = cur == null ? null : cur.kids.get(c);
                        if (cur == null) return List.of();
                        return cur.counts.entrySet().stream()
                                .sorted((a, b) -> a.getValue().equals(b.getValue())
                                        ? a.getKey().compareTo(b.getKey()) : b.getValue() - a.getValue())
                                .limit(3).map(Map.Entry::getKey).toList();
                    }
                }""",
                "Each node stores every sentence passing through it with a count, so a keystroke is a single child hop + small sort. At scale you would store only the top-k per node and update asynchronously.",
                "O(m log m) per char, m = matches at node", "O(total characters x sentences)", "Not resetting state on '#'; case where the prefix path no longer exists.");

        add(l, "The Skyline Problem", "Heap", H, "heap,sweep-line,divide-and-conquer",
                "Given buildings [left, right, height], output the key points of the city skyline.",
                "Signal: sweep line over x with a max-heap of active building heights.",
                null,
                """
                static List<List<Integer>> skyline(int[][] buildings) {
                    List<int[]> events = new ArrayList<>();
                    for (int[] b : buildings) { events.add(new int[] { b[0], -b[2] }); events.add(new int[] { b[1], b[2] }); }
                    events.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);   // starts (negative) before ends at same x
                    TreeMap<Integer, Integer> heights = new TreeMap<>();
                    heights.put(0, 1);
                    int prev = 0;
                    List<List<Integer>> out = new ArrayList<>();
                    for (int[] e : events) {
                        if (e[1] < 0) heights.merge(-e[1], 1, Integer::sum);
                        else if (heights.merge(e[1], -1, Integer::sum) == 0) heights.remove(e[1]);
                        int cur = heights.lastKey();
                        if (cur != prev) { out.add(List.of(e[0], cur)); prev = cur; }
                    }
                    return out;
                }""",
                "A key point appears exactly when the maximum active height changes. The sort order trick (start events negative) handles buildings touching at the same x.",
                "O(n log n)", "O(n)", "Wrong tie ordering at the same x; not removing heights when the count hits zero.");

        add(l, "Cheapest Flights Within K Stops", "Graphs", H, "graph,bellman-ford,dijkstra,website",
                "Find the cheapest price from src to dst with at most k stops (flight search engine).",
                "Signal: shortest path with a hop limit -> Bellman-Ford limited to k+1 rounds (Dijkstra alone breaks).",
                null,
                """
                static int cheapest(int n, int[][] flights, int src, int dst, int k) {
                    int INF = Integer.MAX_VALUE / 2;
                    int[] cost = new int[n];
                    Arrays.fill(cost, INF);
                    cost[src] = 0;
                    for (int round = 0; round <= k; round++) {
                        int[] next = cost.clone();                            // use only last round's prices
                        for (int[] f : flights) {
                            if (cost[f[0]] + f[2] < next[f[1]]) next[f[1]] = cost[f[0]] + f[2];
                        }
                        cost = next;
                    }
                    return cost[dst] >= INF ? -1 : cost[dst];
                }""",
                "Each Bellman-Ford round adds at most one more flight to a route. Cloning the array prevents using two flights in a single round.",
                "O(k x E)", "O(V)", "Updating in place and exceeding the stop limit; Integer.MAX_VALUE overflow.");

        add(l, "Network Delay Time (Dijkstra)", "Graphs", H, "graph,dijkstra,heap,website",
                "A signal is sent from node k through a weighted directed network. How long until every node receives it? -1 if impossible (CDN propagation time).",
                "Signal: shortest paths from one source with non-negative weights -> Dijkstra with a priority queue.",
                null,
                """
                static int networkDelay(int[][] times, int n, int k) {
                    Map<Integer, List<int[]>> g = new HashMap<>();
                    for (int[] t : times) g.computeIfAbsent(t[0], x -> new ArrayList<>()).add(new int[] { t[1], t[2] });
                    int[] dist = new int[n + 1];
                    Arrays.fill(dist, Integer.MAX_VALUE);
                    dist[k] = 0;
                    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
                    pq.add(new int[] { k, 0 });
                    while (!pq.isEmpty()) {
                        int[] cur = pq.poll();
                        if (cur[1] > dist[cur[0]]) continue;                // stale entry
                        for (int[] e : g.getOrDefault(cur[0], List.of())) {
                            int nd = cur[1] + e[1];
                            if (nd < dist[e[0]]) { dist[e[0]] = nd; pq.add(new int[] { e[0], nd }); }
                        }
                    }
                    int max = 0;
                    for (int i = 1; i <= n; i++) { if (dist[i] == Integer.MAX_VALUE) return -1; max = Math.max(max, dist[i]); }
                    return max;
                }""",
                "The answer is the largest shortest-distance. Skip stale heap entries instead of implementing decrease-key.",
                "O(E log V)", "O(V + E)", "Using Dijkstra with negative weights; forgetting unreachable nodes.");

        add(l, "Critical Connections in a Network", "Graphs", H, "graph,tarjan,bridges,dfs",
                "Find all edges whose removal disconnects the network (single points of failure between data centers).",
                "Signal: bridges -> Tarjan's low-link DFS.",
                null,
                """
                static int timer = 0;
                static List<List<Integer>> bridges(int n, List<List<Integer>> connections) {
                    List<Integer>[] g = new List[n];
                    for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
                    for (List<Integer> c : connections) { g[c.get(0)].add(c.get(1)); g[c.get(1)].add(c.get(0)); }
                    int[] disc = new int[n], low = new int[n];
                    Arrays.fill(disc, -1);
                    List<List<Integer>> out = new ArrayList<>();
                    dfs(0, -1, g, disc, low, out);
                    return out;
                }
                static void dfs(int u, int parent, List<Integer>[] g, int[] disc, int[] low, List<List<Integer>> out) {
                    disc[u] = low[u] = timer++;
                    for (int v : g[u]) {
                        if (v == parent) continue;
                        if (disc[v] == -1) {
                            dfs(v, u, g, disc, low, out);
                            low[u] = Math.min(low[u], low[v]);
                            if (low[v] > disc[u]) out.add(List.of(u, v));      // no back-edge around this edge
                        } else low[u] = Math.min(low[u], disc[v]);
                    }
                }""",
                "low[v] is the earliest discovery time reachable from v's subtree. If it is still later than u's own time, the edge u-v is the only path.",
                "O(V + E)", "O(V + E)", "Treating the edge to the parent as a back edge; recursion depth on huge graphs.");

        add(l, "Palindrome Partitioning II", "Dynamic Programming", H, "dp,strings,palindrome",
                "Return the minimum cuts needed to split a string into palindromes.",
                "Signal: min cuts over prefixes + O(1) palindrome checks from a precomputed table.",
                null,
                """
                static int minCut(String s) {
                    int n = s.length();
                    boolean[][] pal = new boolean[n][n];
                    int[] cuts = new int[n];
                    for (int end = 0; end < n; end++) {
                        cuts[end] = end;                                       // worst case: cut between every char
                        for (int start = 0; start <= end; start++) {
                            if (s.charAt(start) == s.charAt(end) && (end - start < 2 || pal[start + 1][end - 1])) {
                                pal[start][end] = true;
                                cuts[end] = start == 0 ? 0 : Math.min(cuts[end], cuts[start - 1] + 1);
                            }
                        }
                    }
                    return cuts[n - 1];
                }""",
                "pal[i][j] is true when the ends match and the inside is a palindrome. cuts[end] = best over every palindrome that ends at `end`.",
                "O(n^2)", "O(n^2)", "Recomputing palindromes repeatedly (O(n^3)).");

        add(l, "Distinct Subsequences", "Dynamic Programming", H, "dp,strings",
                "Count the distinct subsequences of s that equal t.",
                "Signal: matching with choices (use this char or skip it) -> dp over prefixes.",
                null,
                """
                static int numDistinct(String s, String t) {
                    long[] dp = new long[t.length() + 1];
                    dp[0] = 1;                                             // empty target matched once
                    for (int i = 1; i <= s.length(); i++) {
                        for (int j = t.length(); j >= 1; j--) {            // backwards so each s char is used once
                            if (s.charAt(i - 1) == t.charAt(j - 1)) dp[j] += dp[j - 1];
                        }
                    }
                    return (int) dp[t.length()];
                }""",
                "dp[j] = ways to build t[0..j) from the part of s seen so far. A matching character adds the ways that existed before it was available.",
                "O(n x m)", "O(m)", "Iterating j forwards (reuses the same char).");

        add(l, "Interleaving String", "Dynamic Programming", H, "dp,strings",
                "Is s3 formed by interleaving s1 and s2 while keeping each string's own order?",
                "Signal: two sources merged into one -> dp[i][j] = first i of s1 and j of s2 can build the first i+j of s3.",
                null,
                """
                static boolean isInterleave(String s1, String s2, String s3) {
                    if (s1.length() + s2.length() != s3.length()) return false;
                    boolean[][] dp = new boolean[s1.length() + 1][s2.length() + 1];
                    dp[0][0] = true;
                    for (int i = 0; i <= s1.length(); i++) {
                        for (int j = 0; j <= s2.length(); j++) {
                            if (i > 0 && dp[i - 1][j] && s1.charAt(i - 1) == s3.charAt(i + j - 1)) dp[i][j] = true;
                            if (j > 0 && dp[i][j - 1] && s2.charAt(j - 1) == s3.charAt(i + j - 1)) dp[i][j] = true;
                        }
                    }
                    return dp[s1.length()][s2.length()];
                }""",
                "The last character of s3 came from either s1 or s2; both possibilities are checked from earlier states.",
                "O(n x m)", "O(n x m)", "Greedy matching (fails when both strings share a char).");

        add(l, "Count of Smaller Numbers After Self", "Divide and Conquer", H, "merge-sort,fenwick,divide-and-conquer",
                "For each element, count the smaller elements to its right.",
                "Signal: pair counting by order -> merge sort (count while merging) or a Fenwick tree.",
                null,
                """
                static List<Integer> countSmaller(int[] nums) {
                    int n = nums.length;
                    int[] idx = new int[n], counts = new int[n];
                    for (int i = 0; i < n; i++) idx[i] = i;
                    sort(nums, idx, new int[n], 0, n - 1, counts);
                    List<Integer> out = new ArrayList<>();
                    for (int c : counts) out.add(c);
                    return out;
                }
                static void sort(int[] a, int[] idx, int[] tmp, int lo, int hi, int[] counts) {
                    if (lo >= hi) return;
                    int mid = (lo + hi) >>> 1;
                    sort(a, idx, tmp, lo, mid, counts);
                    sort(a, idx, tmp, mid + 1, hi, counts);
                    int i = lo, j = mid + 1, k = lo, rightSmaller = 0;
                    while (i <= mid || j <= hi) {
                        if (j > hi || (i <= mid && a[idx[i]] <= a[idx[j]])) {
                            counts[idx[i]] += rightSmaller;            // right-half elements already placed are smaller
                            tmp[k++] = idx[i++];
                        } else { rightSmaller++; tmp[k++] = idx[j++]; }
                    }
                    System.arraycopy(tmp, lo, idx, lo, hi - lo + 1);
                }""",
                "Sort indices, not values, so each element's answer can be attributed. Every time a right-half element is placed before a left-half one, it is smaller than it.",
                "O(n log n)", "O(n)", "Sorting values and losing the original positions.");

        add(l, "Maximum Profit in Job Scheduling (Freelance Marketplace)", "Dynamic Programming", H, "dp,binary-search,intervals,website",
                "Jobs have start, end and profit. Pick non-overlapping jobs to maximise total profit.",
                "Signal: weighted interval scheduling -> sort by end time, binary search the last compatible job.",
                null,
                """
                static int jobScheduling(int[] start, int[] end, int[] profit) {
                    int n = start.length;
                    Integer[] order = new Integer[n];
                    for (int i = 0; i < n; i++) order[i] = i;
                    Arrays.sort(order, Comparator.comparingInt(i -> end[i]));
                    int[] endTimes = new int[n];
                    int[] dp = new int[n + 1];                              // dp[i] = best using first i jobs by end time
                    for (int i = 0; i < n; i++) endTimes[i] = end[order[i]];
                    for (int i = 1; i <= n; i++) {
                        int job = order[i - 1];
                        int p = upperBound(endTimes, i - 1, start[job]);    // jobs ending <= start
                        dp[i] = Math.max(dp[i - 1], dp[p] + profit[job]);
                    }
                    return dp[n];
                }
                static int upperBound(int[] a, int hi, int x) {
                    int lo = 0;
                    while (lo < hi) { int mid = (lo + hi) >>> 1; if (a[mid] <= x) lo = mid + 1; else hi = mid; }
                    return lo;
                }""",
                "Either skip the job (dp[i-1]) or take it plus the best of everything that ends before it starts.",
                "O(n log n)", "O(n)", "Sorting by start time (breaks the dp ordering).");

        add(l, "Task Scheduler with Cooldown (Job Queue)", "Greedy", H, "greedy,heap,math,website",
                "Tasks are letters; the same task needs n idle intervals between runs. Return the minimum intervals to finish all tasks.",
                "Signal: the most frequent task determines the frame; fill gaps with the others.",
                null,
                """
                static int leastInterval(char[] tasks, int n) {
                    int[] freq = new int[26];
                    for (char t : tasks) freq[t - 'A']++;
                    int max = 0, maxCount = 0;
                    for (int f : freq) max = Math.max(max, f);
                    for (int f : freq) if (f == max) maxCount++;
                    // (max-1) full frames of size n+1, plus the final row of tasks with max frequency
                    return Math.max(tasks.length, (max - 1) * (n + 1) + maxCount);
                }""",
                "If other tasks are plentiful the frame is full and the answer is just tasks.length; otherwise idle slots are forced by the most frequent tasks.",
                "O(n)", "O(1)", "Simulating minute by minute (slow) or forgetting ties for the maximum.");

        add(l, "Concurrent Seat Booking (No Double-Booking)", "Website: Booking", H, "website,concurrency,design,database",
                "A cinema site lets thousands of users pick seats simultaneously. Design hold(seat, user, ttl), confirm(seat, user) and release so a seat is never sold twice and abandoned holds expire.",
                "Signal: shared mutable state + time -> atomic state transitions (FREE -> HELD -> SOLD) and a hold expiry.",
                null,
                """
                class SeatBooking {
                    enum State { FREE, HELD, SOLD }
                    private static final class Seat { State state = State.FREE; String owner; long holdUntil; }

                    private final Map<String, Seat> seats = new ConcurrentHashMap<>();

                    boolean hold(String seatId, String user, long now, long ttlMs) {
                        Seat s = seats.computeIfAbsent(seatId, k -> new Seat());
                        synchronized (s) {                                    // lock per seat, not one global lock
                            if (s.state == State.HELD && s.holdUntil <= now) s.state = State.FREE;   // lazy expiry
                            if (s.state != State.FREE) return false;
                            s.state = State.HELD; s.owner = user; s.holdUntil = now + ttlMs;
                            return true;
                        }
                    }
                    boolean confirm(String seatId, String user, long now) {
                        Seat s = seats.get(seatId);
                        if (s == null) return false;
                        synchronized (s) {
                            if (s.state != State.HELD || !user.equals(s.owner) || s.holdUntil <= now) return false;
                            s.state = State.SOLD;
                            return true;
                        }
                    }
                }
                // Database version: UPDATE seat SET state='HELD', owner=?, hold_until=?
                //                   WHERE id=? AND (state='FREE' OR (state='HELD' AND hold_until <= now()))
                // success only if rows_affected = 1""",
                "Per-seat locks keep unrelated bookings parallel. Across multiple servers the same guarantee comes from a conditional UPDATE or a unique constraint. Holds give users time to pay without blocking seats forever.",
                "O(1)", "O(seats)", "Check-then-act without a lock; a global lock that serialises the whole theatre; holds that never expire.");

        add(l, "Text Justification (Layout Engine)", "Strings", H, "strings,greedy,simulation,website",
                "Format words into lines of exactly maxWidth characters, fully justified (extra spaces distributed left-heavy); the last line is left-justified.",
                "Signal: greedy line packing, then distribute remaining spaces between gaps.",
                null,
                """
                static List<String> justify(String[] words, int width) {
                    List<String> lines = new ArrayList<>();
                    int i = 0;
                    while (i < words.length) {
                        int j = i, len = 0;
                        while (j < words.length && len + words[j].length() + (j - i) <= width) len += words[j++].length();
                        int gaps = j - i - 1;
                        StringBuilder line = new StringBuilder(words[i]);
                        if (j == words.length || gaps == 0) {                         // last line or single word
                            for (int k = i + 1; k < j; k++) line.append(' ').append(words[k]);
                            while (line.length() < width) line.append(' ');
                        } else {
                            int spaces = (width - len) / gaps, extra = (width - len) % gaps;
                            for (int k = i + 1; k < j; k++) {
                                line.append(" ".repeat(spaces + (k - i <= extra ? 1 : 0))).append(words[k]);
                            }
                        }
                        lines.add(line.toString());
                        i = j;
                    }
                    return lines;
                }""",
                "Pack as many words as fit with at least one space between, then spread (width - letters) spaces: equal share plus one extra to the leftmost gaps.",
                "O(total characters)", "O(width)", "Justifying the last line; dividing by zero for a one-word line.");

        add(l, "Basic Calculator (Spreadsheet Formula)", "Stack", H, "stack,parsing,expression,website",
                "Evaluate a string expression containing digits, + - operators, parentheses and spaces: \"(1+(4+5+2)-3)+(6+8)\" -> 23.",
                "Signal: parentheses flip/restore sign context -> stack of (result so far, sign).",
                null,
                """
                static int calculate(String s) {
                    Deque<Integer> stack = new ArrayDeque<>();
                    int result = 0, number = 0, sign = 1;
                    for (char c : s.toCharArray()) {
                        if (Character.isDigit(c)) number = number * 10 + (c - '0');
                        else if (c == '+' || c == '-') {
                            result += sign * number;
                            number = 0;
                            sign = c == '+' ? 1 : -1;
                        } else if (c == '(') {
                            stack.push(result);                    // save context
                            stack.push(sign);
                            result = 0; sign = 1;
                        } else if (c == ')') {
                            result += sign * number;
                            number = 0;
                            result *= stack.pop();                  // sign before the parenthesis
                            result += stack.pop();                  // result before the parenthesis
                        }
                    }
                    return result + sign * number;
                }""",
                "No need to build a full parse tree: push the running result and the sign when entering '(' and combine when leaving. Extend with * and / by adding a second stack or Dijkstra's shunting-yard algorithm.",
                "O(n)", "O(depth)", "Forgetting the last pending number; unary minus after '('.");

        add(l, "Bus Routes (Fewest Buses)", "Graphs", H, "graph,bfs,website",
                "routes[i] lists the stops bus i loops through. Find the minimum number of buses to ride from source to target (journey planner).",
                "Signal: model BUSES as graph nodes, not stops; BFS counts buses.",
                null,
                """
                static int numBusesToDestination(int[][] routes, int source, int target) {
                    if (source == target) return 0;
                    Map<Integer, List<Integer>> stopToBuses = new HashMap<>();
                    for (int b = 0; b < routes.length; b++)
                        for (int stop : routes[b]) stopToBuses.computeIfAbsent(stop, k -> new ArrayList<>()).add(b);
                    Queue<Integer> q = new ArrayDeque<>(List.of(source));
                    Set<Integer> seenStops = new HashSet<>(List.of(source));
                    boolean[] usedBus = new boolean[routes.length];
                    int buses = 0;
                    while (!q.isEmpty()) {
                        buses++;
                        for (int i = q.size(); i > 0; i--) {
                            int stop = q.poll();
                            for (int bus : stopToBuses.getOrDefault(stop, List.of())) {
                                if (usedBus[bus]) continue;
                                usedBus[bus] = true;
                                for (int next : routes[bus]) {
                                    if (next == target) return buses;
                                    if (seenStops.add(next)) q.add(next);
                                }
                            }
                        }
                    }
                    return -1;
                }""",
                "BFS levels = number of buses. Marking a whole bus as used avoids re-scanning its stops repeatedly (otherwise it degrades to O(n^2)).",
                "O(total stops)", "O(total stops)", "BFS over stops counting stops instead of buses.");

        add(l, "Package Install Order (Dependency Resolver)", "Graphs", H, "graph,topological-sort,dfs,website",
                "Given packages and 'A depends on B' pairs, return an installation order, or report a circular dependency (like npm/maven resolving a lockfile).",
                "Signal: dependencies -> topological sort; a cycle means no valid order.",
                null,
                """
                static List<String> installOrder(Map<String, List<String>> deps) {
                    List<String> order = new ArrayList<>();
                    Map<String, Integer> state = new HashMap<>();   // 1 = visiting, 2 = done
                    for (String pkg : deps.keySet()) visit(pkg, deps, state, order, new ArrayDeque<>());
                    return order;
                }
                static void visit(String pkg, Map<String, List<String>> deps, Map<String, Integer> state,
                                  List<String> order, Deque<String> path) {
                    Integer s = state.get(pkg);
                    if (s != null && s == 2) return;
                    path.addLast(pkg);
                    if (s != null && s == 1) throw new IllegalStateException("Circular dependency: " + path);
                    state.put(pkg, 1);
                    for (String d : deps.getOrDefault(pkg, List.of())) visit(d, deps, state, order, path);
                    state.put(pkg, 2);
                    order.add(pkg);                                  // all dependencies are already installed
                    path.removeLast();
                }""",
                "Post-order DFS emits a package only after all its dependencies. Seeing a node that is still 'visiting' proves a cycle, and the path gives a helpful error message.",
                "O(V + E)", "O(V)", "Not distinguishing 'visiting' from 'done' (false cycle reports on diamonds).");

        add(l, "Trapping Rain Water II (2D)", "Heap", H, "heap,bfs,grid",
                "Given a 2D elevation map, compute how much water it can trap after raining.",
                "Signal: water level is bounded by the lowest wall on the boundary -> flood from the border with a min-heap.",
                null,
                """
                static int trapRainWater(int[][] h) {
                    int m = h.length, n = h[0].length;
                    if (m < 3 || n < 3) return 0;
                    boolean[][] seen = new boolean[m][n];
                    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
                    for (int i = 0; i < m; i++)
                        for (int j = 0; j < n; j++)
                            if (i == 0 || j == 0 || i == m - 1 || j == n - 1) { pq.add(new int[] { i, j, h[i][j] }); seen[i][j] = true; }
                    int water = 0;
                    int[][] dirs = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
                    while (!pq.isEmpty()) {
                        int[] cell = pq.poll();                       // lowest wall of the current border
                        for (int[] d : dirs) {
                            int r = cell[0] + d[0], c = cell[1] + d[1];
                            if (r < 0 || c < 0 || r >= m || c >= n || seen[r][c]) continue;
                            seen[r][c] = true;
                            water += Math.max(0, cell[2] - h[r][c]);  // fills up to the border height
                            pq.add(new int[] { r, c, Math.max(h[r][c], cell[2]) });
                        }
                    }
                    return water;
                }""",
                "Water can only escape over the lowest point of the surrounding wall, so always process the lowest border cell first - like Dijkstra on 'water level'.",
                "O(mn log mn)", "O(mn)", "Using the 1D two-pointer method in 2D.");

        add(l, "Median of Two Sorted Arrays (Log Time)", "Binary Search", H, "binary-search,partition",
                "Find the median of two sorted arrays in O(log(min(m, n))).",
                "Signal: binary search the PARTITION point, not a value.",
                null,
                """
                static double findMedian(int[] a, int[] b) {
                    if (a.length > b.length) return findMedian(b, a);       // search the shorter array
                    int m = a.length, n = b.length, lo = 0, hi = m, half = (m + n + 1) / 2;
                    while (lo <= hi) {
                        int i = (lo + hi) / 2, j = half - i;
                        int aLeft = i == 0 ? Integer.MIN_VALUE : a[i - 1], aRight = i == m ? Integer.MAX_VALUE : a[i];
                        int bLeft = j == 0 ? Integer.MIN_VALUE : b[j - 1], bRight = j == n ? Integer.MAX_VALUE : b[j];
                        if (aLeft <= bRight && bLeft <= aRight) {
                            if ((m + n) % 2 == 1) return Math.max(aLeft, bLeft);
                            return (Math.max(aLeft, bLeft) + (double) Math.min(aRight, bRight)) / 2;
                        }
                        if (aLeft > bRight) hi = i - 1; else lo = i + 1;
                    }
                    throw new IllegalArgumentException();
                }""",
                "Pick i elements from a and j = half - i from b so that the left halves together contain half the data; valid when each left maximum <= the other side's right minimum.",
                "O(log min(m, n))", "O(1)", "Searching the longer array; overflow when summing the two middle values.");

        return l;
    }
}
