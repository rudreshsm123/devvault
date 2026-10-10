package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.add;

/** 35 intermediate problems: interview staples and the building blocks of real websites. */
public final class IntermediateProblems {

    private IntermediateProblems() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty M = Difficulty.MEDIUM;

        add(l, "Group Anagrams", "Hash Map", M, "strings,hash-map,sorting",
                "Group strings that are anagrams of each other. [\"eat\",\"tea\",\"tan\",\"ate\",\"nat\",\"bat\"] -> [[eat,tea,ate],[tan,nat],[bat]].",
                "Signal: 'same letters' -> compute a canonical key (sorted letters or a count signature) and group by it.",
                """
                static List<List<String>> group(String[] words) {
                    List<List<String>> groups = new ArrayList<>();
                    boolean[] used = new boolean[words.length];
                    for (int i = 0; i < words.length; i++) {
                        if (used[i]) continue;
                        List<String> g = new ArrayList<>(List.of(words[i]));
                        for (int j = i + 1; j < words.length; j++) {
                            if (!used[j] && sameLetters(words[i], words[j])) { g.add(words[j]); used[j] = true; }
                        }
                        groups.add(g);
                    }
                    return groups;
                }""",
                """
                static List<List<String>> group(String[] words) {
                    Map<String, List<String>> map = new HashMap<>();
                    for (String w : words) {
                        char[] c = w.toCharArray();
                        Arrays.sort(c);
                        map.computeIfAbsent(new String(c), k -> new ArrayList<>()).add(w);
                    }
                    return new ArrayList<>(map.values());
                }""",
                "Pairwise comparison is O(n^2 * k). A canonical key lets the hash map do the grouping in one pass.",
                "O(n * k log k)", "O(n * k)", "Using the word itself as key; forgetting that char[] has identity hashCode (convert to String).");

        add(l, "Longest Substring Without Repeating Characters", "Sliding Window", M, "strings,sliding-window,hash-map",
                "Return the length of the longest substring with all distinct characters. \"abcabcbb\" -> 3.",
                "Signal: 'longest/shortest contiguous range with a property' -> sliding window with two pointers.",
                """
                static int longest(String s) {
                    int best = 0;
                    for (int i = 0; i < s.length(); i++) {
                        Set<Character> seen = new HashSet<>();
                        for (int j = i; j < s.length() && seen.add(s.charAt(j)); j++) {
                            best = Math.max(best, j - i + 1);
                        }
                    }
                    return best;
                }""",
                """
                static int longest(String s) {
                    Map<Character, Integer> lastSeen = new HashMap<>();
                    int best = 0, left = 0;
                    for (int right = 0; right < s.length(); right++) {
                        char c = s.charAt(right);
                        if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) left = lastSeen.get(c) + 1;
                        lastSeen.put(c, right);
                        best = Math.max(best, right - left + 1);
                    }
                    return best;
                }""",
                "Restarting from every index is O(n^2). The window only ever moves forward: when a repeat appears, jump left past its previous position.",
                "O(n)", "O(alphabet)", "Moving left backwards (left = max(left, prev+1) is the safe form).");

        add(l, "Product of Array Except Self", "Arrays", M, "arrays,prefix-sum",
                "Return an array where out[i] is the product of all elements except nums[i], without using division, in O(n).",
                "Signal: 'everything except me' = (everything to my left) x (everything to my right) -> prefix and suffix products.",
                """
                static int[] product(int[] a) {
                    int[] out = new int[a.length];
                    for (int i = 0; i < a.length; i++) {
                        int p = 1;
                        for (int j = 0; j < a.length; j++) if (j != i) p *= a[j];
                        out[i] = p;
                    }
                    return out;
                }""",
                """
                static int[] product(int[] a) {
                    int n = a.length;
                    int[] out = new int[n];
                    out[0] = 1;
                    for (int i = 1; i < n; i++) out[i] = out[i - 1] * a[i - 1];   // left products
                    int right = 1;
                    for (int i = n - 1; i >= 0; i--) {                              // multiply right products
                        out[i] *= right;
                        right *= a[i];
                    }
                    return out;
                }""",
                "Two sweeps reuse the output array as storage so no extra arrays are needed.",
                "O(n)", "O(1) extra", "Dividing by the total product (breaks with zeros).");

        add(l, "3Sum", "Two Pointers", M, "arrays,sorting,two-pointers",
                "Return all unique triplets that sum to zero.",
                "Signal: k-sum -> sort, fix one number, then solve two-sum with two pointers.",
                """
                static Set<List<Integer>> threeSum(int[] a) {
                    Set<List<Integer>> out = new HashSet<>();
                    for (int i = 0; i < a.length; i++)
                        for (int j = i + 1; j < a.length; j++)
                            for (int k = j + 1; k < a.length; k++)
                                if (a[i] + a[j] + a[k] == 0) {
                                    List<Integer> t = Arrays.asList(a[i], a[j], a[k]);
                                    Collections.sort(t);
                                    out.add(t);
                                }
                    return out;
                }""",
                """
                static List<List<Integer>> threeSum(int[] a) {
                    Arrays.sort(a);
                    List<List<Integer>> out = new ArrayList<>();
                    for (int i = 0; i < a.length - 2; i++) {
                        if (i > 0 && a[i] == a[i - 1]) continue;       // skip duplicate anchors
                        int lo = i + 1, hi = a.length - 1;
                        while (lo < hi) {
                            int sum = a[i] + a[lo] + a[hi];
                            if (sum == 0) {
                                out.add(List.of(a[i], a[lo], a[hi]));
                                while (lo < hi && a[lo] == a[lo + 1]) lo++;
                                while (lo < hi && a[hi] == a[hi - 1]) hi--;
                                lo++; hi--;
                            } else if (sum < 0) lo++; else hi--;
                        }
                    }
                    return out;
                }""",
                "Sorting makes duplicates adjacent and lets two pointers prune: too small -> move lo right, too big -> move hi left.",
                "O(n^2)", "O(1) extra", "Not skipping duplicates; skipping before recording a hit.");

        add(l, "Container With Most Water", "Two Pointers", M, "arrays,two-pointers,greedy",
                "Given heights of vertical lines, choose two that, with the x-axis, hold the most water.",
                "Signal: maximise width x min(height) -> start widest and move the shorter wall inward.",
                """
                static int maxArea(int[] h) {
                    int best = 0;
                    for (int i = 0; i < h.length; i++)
                        for (int j = i + 1; j < h.length; j++)
                            best = Math.max(best, (j - i) * Math.min(h[i], h[j]));
                    return best;
                }""",
                """
                static int maxArea(int[] h) {
                    int lo = 0, hi = h.length - 1, best = 0;
                    while (lo < hi) {
                        best = Math.max(best, (hi - lo) * Math.min(h[lo], h[hi]));
                        if (h[lo] < h[hi]) lo++; else hi--;
                    }
                    return best;
                }""",
                "The shorter wall limits the area; moving the taller wall can only shrink the width without raising the limit, so always move the shorter one.",
                "O(n)", "O(1)", "Moving the taller pointer.");

        add(l, "Trending Hashtags (Top K Frequent)", "Website: Social Feed", M, "website,heap,hash-map,bucket-sort",
                "Given a stream of hashtags, return the k most frequent ones.",
                "Signal: 'top K' -> count, then a min-heap of size k (or bucket sort by frequency).",
                """
                static List<String> topK(List<String> tags, int k) {
                    Map<String, Integer> count = new HashMap<>();
                    for (String t : tags) count.merge(t, 1, Integer::sum);
                    List<String> keys = new ArrayList<>(count.keySet());
                    keys.sort((a, b) -> count.get(b) - count.get(a));
                    return keys.subList(0, Math.min(k, keys.size()));
                }""",
                """
                static List<String> topK(List<String> tags, int k) {
                    Map<String, Integer> count = new HashMap<>();
                    for (String t : tags) count.merge(t, 1, Integer::sum);
                    PriorityQueue<Map.Entry<String, Integer>> heap =
                            new PriorityQueue<>(Map.Entry.comparingByValue());      // min-heap
                    for (var e : count.entrySet()) {
                        heap.add(e);
                        if (heap.size() > k) heap.poll();                           // drop the smallest
                    }
                    List<String> out = new ArrayList<>();
                    while (!heap.isEmpty()) out.add(0, heap.poll().getKey());
                    return out;
                }""",
                "Sorting every distinct tag is O(m log m). A size-k heap keeps only the best k: O(m log k).",
                "O(n + m log k)", "O(m)", "Using a max-heap of everything; subtracting ints in a comparator (overflow).");

        add(l, "Binary Tree Level Order Traversal", "Trees", M, "tree,bfs,queue",
                "Return node values level by level, left to right.",
                "Signal: 'level by level' -> BFS with a queue, process queue.size() nodes per round.",
                """
                static List<List<Integer>> levels(TreeNode root) {
                    List<List<Integer>> out = new ArrayList<>();
                    dfs(root, 0, out);
                    return out;
                }
                static void dfs(TreeNode n, int depth, List<List<Integer>> out) {
                    if (n == null) return;
                    if (out.size() == depth) out.add(new ArrayList<>());
                    out.get(depth).add(n.val);
                    dfs(n.left, depth + 1, out);
                    dfs(n.right, depth + 1, out);
                }""",
                """
                static List<List<Integer>> levels(TreeNode root) {
                    List<List<Integer>> out = new ArrayList<>();
                    if (root == null) return out;
                    Queue<TreeNode> q = new ArrayDeque<>(List.of(root));
                    while (!q.isEmpty()) {
                        List<Integer> level = new ArrayList<>();
                        for (int i = q.size(); i > 0; i--) {
                            TreeNode n = q.poll();
                            level.add(n.val);
                            if (n.left != null) q.add(n.left);
                            if (n.right != null) q.add(n.right);
                        }
                        out.add(level);
                    }
                    return out;
                }""",
                "Freeze the level size before the inner loop; the queue grows while you process it.",
                "O(n)", "O(width)", "Reading q.size() inside the loop condition.");

        add(l, "Validate Binary Search Tree", "Trees", M, "tree,bst,dfs",
                "Return true if a binary tree is a valid BST (left subtree < node < right subtree for EVERY node).",
                "Signal: each node must stay inside a (min, max) range inherited from ancestors, not just beat its parent.",
                """
                static boolean isValid(TreeNode root) {
                    List<Integer> in = new ArrayList<>();
                    inorder(root, in);
                    for (int i = 1; i < in.size(); i++) if (in.get(i - 1) >= in.get(i)) return false;
                    return true;
                }""",
                """
                static boolean isValid(TreeNode n, long lo, long hi) {
                    if (n == null) return true;
                    if (n.val <= lo || n.val >= hi) return false;
                    return isValid(n.left, lo, n.val) && isValid(n.right, n.val, hi);
                }
                // call: isValid(root, Long.MIN_VALUE, Long.MAX_VALUE)""",
                "In-order traversal of a BST is strictly increasing. The range version checks the invariant directly and exits early.",
                "O(n)", "O(h)", "Only comparing a node with its direct children; using int bounds with Integer.MIN_VALUE nodes.");

        add(l, "Number of Islands", "Graphs", M, "grid,dfs,bfs,flood-fill",
                "Given a grid of '1' (land) and '0' (water), count the islands (connected up/down/left/right).",
                "Signal: connected components on a grid -> flood fill each unvisited land cell once.",
                """
                static int islands(char[][] g) {
                    boolean[][] seen = new boolean[g.length][g[0].length];
                    int count = 0;
                    for (int r = 0; r < g.length; r++)
                        for (int c = 0; c < g[0].length; c++)
                            if (g[r][c] == '1' && !seen[r][c]) { fill(g, seen, r, c); count++; }
                    return count;
                }""",
                """
                static int islands(char[][] g) {
                    int count = 0;
                    for (int r = 0; r < g.length; r++)
                        for (int c = 0; c < g[0].length; c++)
                            if (g[r][c] == '1') { sink(g, r, c); count++; }
                    return count;
                }
                static void sink(char[][] g, int r, int c) {
                    if (r < 0 || c < 0 || r >= g.length || c >= g[0].length || g[r][c] != '1') return;
                    g[r][c] = '0';                       // mark visited by sinking the land
                    sink(g, r + 1, c); sink(g, r - 1, c); sink(g, r, c + 1); sink(g, r, c - 1);
                }""",
                "Mutating the grid replaces the visited array. Use an explicit stack/queue if the grid is huge to avoid stack overflow.",
                "O(rows x cols)", "O(rows x cols) worst-case recursion", "Counting land cells instead of components; diagonal neighbours.");

        add(l, "Coin Change", "Dynamic Programming", M, "dp,unbounded-knapsack",
                "Given coin denominations and an amount, return the fewest coins that make the amount, or -1.",
                "Signal: min number of items to hit an exact total -> dp[amount] = 1 + min(dp[amount - coin]).",
                """
                static int coinChange(int[] coins, int amount) {
                    if (amount == 0) return 0;
                    int best = Integer.MAX_VALUE;
                    for (int c : coins) {
                        if (amount - c >= 0) {
                            int sub = coinChange(coins, amount - c);   // exponential without memo
                            if (sub >= 0) best = Math.min(best, sub + 1);
                        }
                    }
                    return best == Integer.MAX_VALUE ? -1 : best;
                }""",
                """
                static int coinChange(int[] coins, int amount) {
                    int[] dp = new int[amount + 1];
                    Arrays.fill(dp, amount + 1);                 // 'infinity'
                    dp[0] = 0;
                    for (int a = 1; a <= amount; a++)
                        for (int c : coins)
                            if (c <= a) dp[a] = Math.min(dp[a], dp[a - c] + 1);
                    return dp[amount] > amount ? -1 : dp[amount];
                }""",
                "Greedy (take the biggest coin) fails for coins {1,3,4} and amount 6. DP tries every last coin for every sub-amount.",
                "O(amount x coins)", "O(amount)", "Greedy approach; using Integer.MAX_VALUE then adding 1 (overflow).");

        add(l, "House Robber", "Dynamic Programming", M, "dp,1d-dp",
                "Each house has money; you cannot rob two adjacent houses. Maximise the haul.",
                "Signal: take-or-skip with a neighbour restriction -> best(i) = max(best(i-1), best(i-2) + a[i]).",
                """
                static int rob(int[] a, int i) {
                    if (i < 0) return 0;
                    return Math.max(rob(a, i - 1), rob(a, i - 2) + a[i]);
                }""",
                """
                static int rob(int[] a) {
                    int skip = 0, take = 0;
                    for (int x : a) {
                        int next = Math.max(take, skip + x);
                        skip = take;
                        take = next;
                    }
                    return take;
                }""",
                "Only the last two results matter, so two variables replace the whole table.",
                "O(n)", "O(1)", "Assuming you must alternate houses.");

        add(l, "Longest Increasing Subsequence", "Dynamic Programming", M, "dp,binary-search",
                "Return the length of the longest strictly increasing subsequence.",
                "Signal: dp[i] = best subsequence ending at i; the O(n log n) trick keeps the smallest possible tail per length.",
                """
                static int lis(int[] a) {
                    int[] dp = new int[a.length];
                    int best = 0;
                    for (int i = 0; i < a.length; i++) {
                        dp[i] = 1;
                        for (int j = 0; j < i; j++) if (a[j] < a[i]) dp[i] = Math.max(dp[i], dp[j] + 1);
                        best = Math.max(best, dp[i]);
                    }
                    return best;
                }""",
                """
                static int lis(int[] a) {
                    int[] tails = new int[a.length];
                    int size = 0;
                    for (int x : a) {
                        int i = Arrays.binarySearch(tails, 0, size, x);
                        if (i < 0) i = -(i + 1);          // insertion point
                        tails[i] = x;
                        if (i == size) size++;
                    }
                    return size;
                }""",
                "tails[k] is the smallest tail of any increasing subsequence of length k+1. Binary search finds where x belongs.",
                "O(n log n)", "O(n)", "Believing tails is the actual subsequence (it is not).");

        add(l, "Rotate Array", "Arrays", M, "arrays,reversal",
                "Rotate an array to the right by k steps in place.",
                "Signal: rotation = reverse all, then reverse the two parts.",
                """
                static void rotate(int[] a, int k) {
                    k %= a.length;
                    int[] copy = a.clone();
                    for (int i = 0; i < a.length; i++) a[(i + k) % a.length] = copy[i];
                }""",
                """
                static void rotate(int[] a, int k) {
                    k %= a.length;
                    reverse(a, 0, a.length - 1);
                    reverse(a, 0, k - 1);
                    reverse(a, k, a.length - 1);
                }
                static void reverse(int[] a, int i, int j) {
                    while (i < j) { int t = a[i]; a[i++] = a[j]; a[j--] = t; }
                }""",
                "Three reversals rotate in place with O(1) extra memory.",
                "O(n)", "O(1)", "Forgetting k %= n.");

        add(l, "Search in Rotated Sorted Array", "Binary Search", M, "binary-search,arrays",
                "A sorted array was rotated at an unknown pivot. Find the index of target in O(log n).",
                "Signal: at every mid, at least one half is still sorted -> decide which half contains the target.",
                """
                static int search(int[] a, int t) {
                    for (int i = 0; i < a.length; i++) if (a[i] == t) return i;
                    return -1;
                }""",
                """
                static int search(int[] a, int t) {
                    int lo = 0, hi = a.length - 1;
                    while (lo <= hi) {
                        int mid = (lo + hi) >>> 1;
                        if (a[mid] == t) return mid;
                        if (a[lo] <= a[mid]) {                         // left half sorted
                            if (a[lo] <= t && t < a[mid]) hi = mid - 1; else lo = mid + 1;
                        } else {                                       // right half sorted
                            if (a[mid] < t && t <= a[hi]) lo = mid + 1; else hi = mid - 1;
                        }
                    }
                    return -1;
                }""",
                "Binary search needs a decision rule per step, not necessarily a fully sorted array.",
                "O(log n)", "O(1)", "Using < instead of <= for a[lo] <= a[mid].");

        add(l, "Kth Largest Element", "Heap", M, "heap,quickselect",
                "Return the kth largest element of an unsorted array.",
                "Signal: only the k-th extreme matters -> size-k min-heap, no full sort.",
                """
                static int kthLargest(int[] a, int k) {
                    int[] copy = a.clone();
                    Arrays.sort(copy);
                    return copy[copy.length - k];
                }""",
                """
                static int kthLargest(int[] a, int k) {
                    PriorityQueue<Integer> heap = new PriorityQueue<>();
                    for (int x : a) {
                        heap.add(x);
                        if (heap.size() > k) heap.poll();
                    }
                    return heap.peek();
                }""",
                "The heap root is always the kth largest of what you have seen so far. Quickselect gives O(n) average.",
                "O(n log k)", "O(k)", "Using a max-heap and polling k times without need.");

        add(l, "Min Stack", "Stack", M, "design,stack",
                "Design a stack supporting push, pop, top and getMin all in O(1).",
                "Signal: an auxiliary structure that remembers the answer at each depth.",
                """
                class MinStack {
                    private final Deque<Integer> data = new ArrayDeque<>();
                    void push(int x) { data.push(x); }
                    int pop() { return data.pop(); }
                    int top() { return data.peek(); }
                    int getMin() { return Collections.min(data); }   // O(n)
                }""",
                """
                class MinStack {
                    private final Deque<int[]> st = new ArrayDeque<>();   // {value, minSoFar}
                    void push(int x) { st.push(new int[] { x, st.isEmpty() ? x : Math.min(x, st.peek()[1]) }); }
                    int pop() { return st.pop()[0]; }
                    int top() { return st.peek()[0]; }
                    int getMin() { return st.peek()[1]; }
                }""",
                "Store the running minimum next to each value: popping automatically restores the previous minimum.",
                "O(1) per op", "O(n)", "Keeping a single min variable that goes stale after a pop.");

        add(l, "Search Autocomplete (Trie)", "Website: Search Box", M, "website,trie,strings,design",
                "Implement insert(word) and startsWith(prefix) returning all words with the prefix - the engine behind a search-box dropdown.",
                "Signal: prefix queries -> a trie, where the path from the root spells the prefix.",
                """
                class Autocomplete {
                    private final List<String> words = new ArrayList<>();
                    void insert(String w) { words.add(w); }
                    List<String> suggest(String prefix) {
                        List<String> out = new ArrayList<>();
                        for (String w : words) if (w.startsWith(prefix)) out.add(w);
                        return out;
                    }
                }""",
                """
                class Autocomplete {
                    private static class Node { Map<Character, Node> next = new TreeMap<>(); boolean end; }
                    private final Node root = new Node();
                    void insert(String w) {
                        Node n = root;
                        for (char c : w.toCharArray()) n = n.next.computeIfAbsent(c, k -> new Node());
                        n.end = true;
                    }
                    List<String> suggest(String prefix) {
                        Node n = root;
                        for (char c : prefix.toCharArray()) { n = n.next.get(c); if (n == null) return List.of(); }
                        List<String> out = new ArrayList<>();
                        collect(n, new StringBuilder(prefix), out);
                        return out;
                    }
                    private void collect(Node n, StringBuilder path, List<String> out) {
                        if (n.end) out.add(path.toString());
                        for (var e : n.next.entrySet()) {
                            path.append(e.getKey());
                            collect(e.getValue(), path, out);
                            path.setLength(path.length() - 1);
                        }
                    }
                }""",
                "The list scan is O(total characters) for every keystroke. A trie jumps straight to the prefix node and only walks matches. Production systems also cap results and rank by popularity.",
                "O(prefix + matches)", "O(total characters)", "Forgetting the end-of-word flag so 'car' also returns for 'cart'.");

        add(l, "URL Shortener (Base62 Encoder)", "Website: URL Shortener", M, "website,design,base62,hash-map",
                "Design shorten(longUrl) -> short code and expand(code) -> longUrl using a database id and a Base62 alphabet.",
                "Signal: unique id -> base conversion. 62 chars ^ 7 = 3.5 trillion codes, no collisions, no hashing.",
                """
                class Shortener {
                    private final Map<String, String> codeToUrl = new HashMap<>();
                    private final Random rnd = new Random();
                    String shorten(String url) {
                        String code;
                        do { code = Integer.toString(rnd.nextInt(Integer.MAX_VALUE), 36); }
                        while (codeToUrl.containsKey(code));   // retry on collision
                        codeToUrl.put(code, url);
                        return code;
                    }
                    String expand(String code) { return codeToUrl.get(code); }
                }""",
                """
                class Shortener {
                    private static final String A = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    private final Map<Long, String> byId = new HashMap<>();
                    private long nextId = 100_000;

                    String shorten(String url) {
                        long id = nextId++;
                        byId.put(id, url);
                        StringBuilder sb = new StringBuilder();
                        for (long n = id; n > 0; n /= 62) sb.append(A.charAt((int) (n % 62)));
                        return sb.reverse().toString();
                    }
                    String expand(String code) {
                        long id = 0;
                        for (char c : code.toCharArray()) id = id * 62 + A.indexOf(c);
                        return byId.get(id);
                    }
                }""",
                "Random codes need collision retries and a uniqueness check. Encoding an auto-increment id in Base62 is collision-free and reversible. Real systems also add expiry, analytics and a cache in front of the DB.",
                "O(log62 id)", "O(urls)", "Using only a hash of the URL (collisions); sequential ids reveal volume - add an offset or shuffle the alphabet.");

        add(l, "API Rate Limiter (Sliding Window)", "Website: API Gateway", M, "website,design,queue,rate-limit",
                "Allow at most N requests per user in any W-second window. isAllowed(userId, timestamp) returns true/false.",
                "Signal: 'last W seconds' -> keep a deque of timestamps per user and drop the expired ones.",
                """
                class RateLimiter {
                    private final int max; private final long windowMs;
                    private final Map<String, Integer> fixedCount = new HashMap<>();
                    private final Map<String, Long> windowStart = new HashMap<>();
                    RateLimiter(int max, long windowMs) { this.max = max; this.windowMs = windowMs; }
                    boolean isAllowed(String user, long now) {            // fixed window: simple, but bursty at the edges
                        long start = windowStart.getOrDefault(user, now);
                        if (now - start >= windowMs) { start = now; fixedCount.put(user, 0); }
                        windowStart.put(user, start);
                        int c = fixedCount.merge(user, 1, Integer::sum);
                        return c <= max;
                    }
                }""",
                """
                class RateLimiter {
                    private final int max; private final long windowMs;
                    private final Map<String, Deque<Long>> hits = new HashMap<>();
                    RateLimiter(int max, long windowMs) { this.max = max; this.windowMs = windowMs; }
                    boolean isAllowed(String user, long now) {
                        Deque<Long> q = hits.computeIfAbsent(user, k -> new ArrayDeque<>());
                        while (!q.isEmpty() && now - q.peekFirst() >= windowMs) q.pollFirst();   // expire
                        if (q.size() >= max) return false;
                        q.addLast(now);
                        return true;
                    }
                }""",
                "A fixed window lets a user send 2N requests across a boundary. The sliding window deque is exact. At scale use Redis (sorted sets) or a token bucket.",
                "O(1) amortised", "O(users x N)", "Not synchronising access under concurrency; letting the map grow forever for idle users.");

        add(l, "Coupon / Discount Engine", "Website: E-commerce", M, "website,e-commerce,design,strategy",
                "Apply a coupon to a cart total: PERCENT (e.g. 10%), FLAT (e.g. 200 off), or BOGO-style cap. Never go below zero, apply at most one coupon, reject expired ones.",
                "Signal: several interchangeable rules -> a strategy interface instead of a growing if/else.",
                """
                static long apply(long totalCents, String type, long value, boolean expired) {
                    if (expired) throw new IllegalStateException("coupon expired");
                    long result = totalCents;
                    if (type.equals("PERCENT")) result = totalCents - totalCents * value / 100;
                    else if (type.equals("FLAT")) result = totalCents - value;
                    return Math.max(0, result);
                }""",
                """
                interface Coupon { long apply(long totalCents); }

                record Percent(int pct) implements Coupon {
                    public long apply(long t) { return t - t * pct / 100; }
                }
                record Flat(long cents) implements Coupon {
                    public long apply(long t) { return Math.max(0, t - cents); }
                }
                record Capped(Coupon inner, long maxDiscountCents) implements Coupon {
                    public long apply(long t) { return Math.max(t - maxDiscountCents, inner.apply(t)); }
                }

                static long checkout(long total, Coupon c, Instant expiry) {
                    if (Instant.now().isAfter(expiry)) throw new IllegalStateException("coupon expired");
                    return Math.max(0, c.apply(total));
                }""",
                "New coupon kinds become new classes - existing code is untouched (Open/Closed principle). Use integer cents and clamp at zero.",
                "O(1)", "O(1)", "Percent maths on doubles; discounting shipping/tax by accident; stacking coupons unintentionally.");

        add(l, "Merge Guest Cart After Login", "Website: E-commerce", M, "website,e-commerce,hash-map,merge",
                "A visitor adds items as a guest, then logs in and already has a saved cart. Merge both carts: quantities add up, capped at a per-item stock limit.",
                "Signal: merging two keyed collections -> Map.merge with a combining function.",
                """
                static Map<String, Integer> merge(Map<String, Integer> guest, Map<String, Integer> saved,
                                                  Map<String, Integer> stock) {
                    Map<String, Integer> out = new HashMap<>(saved);
                    for (var e : guest.entrySet()) {
                        int qty = out.containsKey(e.getKey()) ? out.get(e.getKey()) + e.getValue() : e.getValue();
                        out.put(e.getKey(), Math.min(qty, stock.getOrDefault(e.getKey(), 0)));
                    }
                    return out;
                }""",
                """
                static Map<String, Integer> merge(Map<String, Integer> guest, Map<String, Integer> saved,
                                                  Map<String, Integer> stock) {
                    Map<String, Integer> out = new HashMap<>(saved);
                    guest.forEach((sku, qty) -> out.merge(sku, qty, Integer::sum));
                    out.replaceAll((sku, qty) -> Math.min(qty, stock.getOrDefault(sku, 0)));
                    out.values().removeIf(q -> q <= 0);
                    return out;
                }""",
                "merge + replaceAll + removeIf separates 'combine', 'cap' and 'clean up' into clear steps.",
                "O(n + m)", "O(n + m)", "Overwriting the saved cart with the guest cart; ignoring stock limits.");

        add(l, "Hotel Booking Overlap (Meeting Rooms II)", "Website: Booking", M, "website,intervals,sorting,heap",
                "Given booking intervals [checkIn, checkOut), return the minimum number of rooms needed so that no two overlapping stays share a room.",
                "Signal: overlapping intervals -> sort by start and track the earliest ending stay with a min-heap (or sweep over start/end events).",
                """
                static int roomsNeeded(int[][] stays) {
                    int best = 0;
                    for (int[] a : stays) {
                        int overlap = 0;
                        for (int[] b : stays) if (b[0] <= a[0] && a[0] < b[1]) overlap++;
                        best = Math.max(best, overlap);
                    }
                    return best;
                }""",
                """
                static int roomsNeeded(int[][] stays) {
                    Arrays.sort(stays, Comparator.comparingInt(s -> s[0]));
                    PriorityQueue<Integer> endTimes = new PriorityQueue<>();
                    for (int[] s : stays) {
                        if (!endTimes.isEmpty() && endTimes.peek() <= s[0]) endTimes.poll();   // room freed
                        endTimes.add(s[1]);
                    }
                    return endTimes.size();
                }""",
                "The heap holds one end time per occupied room; if the earliest checkout is before the new check-in, reuse that room.",
                "O(n log n)", "O(n)", "Treating checkout == next check-in as an overlap.");

        add(l, "News Feed: Merge Sorted Timelines", "Website: Social Feed", M, "website,heap,merge,feed",
                "Each followed account has a timeline sorted newest-first. Return the newest k posts across all timelines.",
                "Signal: merge k sorted lists -> max-heap holding the head of each list.",
                """
                static List<Post> feed(List<List<Post>> timelines, int k) {
                    List<Post> all = new ArrayList<>();
                    timelines.forEach(all::addAll);
                    all.sort((a, b) -> Long.compare(b.time(), a.time()));
                    return all.subList(0, Math.min(k, all.size()));
                }""",
                """
                record Post(String author, long time, String text) {}

                static List<Post> feed(List<List<Post>> timelines, int k) {
                    PriorityQueue<int[]> heap = new PriorityQueue<>(
                            (a, b) -> Long.compare(timelines.get(b[0]).get(b[1]).time(),
                                                   timelines.get(a[0]).get(a[1]).time()));
                    for (int i = 0; i < timelines.size(); i++) if (!timelines.get(i).isEmpty()) heap.add(new int[] { i, 0 });
                    List<Post> out = new ArrayList<>();
                    while (!heap.isEmpty() && out.size() < k) {
                        int[] top = heap.poll();
                        out.add(timelines.get(top[0]).get(top[1]));
                        if (top[1] + 1 < timelines.get(top[0]).size()) heap.add(new int[] { top[0], top[1] + 1 });
                    }
                    return out;
                }""",
                "Flatten-and-sort touches every post of everyone you follow. The heap only touches about k + (number of followed accounts) posts.",
                "O((f + k) log f)", "O(f)", "Loading entire timelines when only the first page is needed.");

        add(l, "Flatten Nested Comments Thread", "Website: Comments", M, "website,tree,dfs,recursion",
                "Comments form a tree (replies to replies). Return a flat list in display order with each comment's indentation depth.",
                "Signal: render a tree in reading order -> pre-order DFS carrying the depth.",
                """
                record Comment(int id, String text, List<Comment> replies) {}
                record Row(int id, String text, int depth) {}

                static List<Row> flatten(List<Comment> roots) {
                    List<Row> out = new ArrayList<>();
                    for (Comment c : roots) walk(c, 0, out);
                    return out;
                }
                static void walk(Comment c, int depth, List<Row> out) {
                    out.add(new Row(c.id(), c.text(), depth));
                    for (Comment r : c.replies()) walk(r, depth + 1, out);
                }""",
                """
                static List<Row> flatten(List<Comment> roots) {
                    List<Row> out = new ArrayList<>();
                    Deque<Object[]> stack = new ArrayDeque<>();
                    for (int i = roots.size() - 1; i >= 0; i--) stack.push(new Object[] { roots.get(i), 0 });
                    while (!stack.isEmpty()) {
                        Object[] top = stack.pop();
                        Comment c = (Comment) top[0];
                        int depth = (int) top[1];
                        out.add(new Row(c.id(), c.text(), depth));
                        for (int i = c.replies().size() - 1; i >= 0; i--) stack.push(new Object[] { c.replies().get(i), depth + 1 });
                    }
                    return out;
                }""",
                "The iterative version pushes children in reverse so the first reply is popped first. Use it when threads can be thousands deep (recursion would overflow).",
                "O(n)", "O(depth)", "Pushing children in forward order and reversing the display; unlimited depth in the UI.");

        add(l, "Session Store with Expiry", "Website: Login", M, "website,design,hash-map,ttl",
                "Implement a session store: create(userId) returns a token valid for ttl seconds; get(token, now) returns the user or null if expired; touch extends the expiry.",
                "Signal: time-based invalidation -> store the expiry time with each entry and check lazily on read (plus a periodic sweep).",
                """
                class Sessions {
                    private final Map<String, String> user = new HashMap<>();
                    private final Map<String, Long> expiry = new HashMap<>();
                    private final long ttl;
                    Sessions(long ttl) { this.ttl = ttl; }
                    String create(String userId, long now) {
                        String t = UUID.randomUUID().toString();
                        user.put(t, userId); expiry.put(t, now + ttl);
                        return t;
                    }
                    String get(String t, long now) {
                        Long e = expiry.get(t);
                        return (e == null || e <= now) ? null : user.get(t);
                    }
                }""",
                """
                class Sessions {
                    private record Entry(String userId, long expiresAt) {}
                    private final Map<String, Entry> map = new LinkedHashMap<>();
                    private final long ttl;
                    Sessions(long ttl) { this.ttl = ttl; }

                    String create(String userId, long now) {
                        String token = UUID.randomUUID().toString();   // use SecureRandom bytes in production
                        map.put(token, new Entry(userId, now + ttl));
                        return token;
                    }
                    String get(String token, long now) {
                        Entry e = map.get(token);
                        if (e == null) return null;
                        if (e.expiresAt() <= now) { map.remove(token); return null; }   // lazy eviction
                        map.put(token, new Entry(e.userId(), now + ttl));               // sliding expiry
                        return e.userId();
                    }
                    void sweep(long now) { map.values().removeIf(e -> e.expiresAt() <= now); }
                }""",
                "One map of immutable entries is easier to keep consistent than two parallel maps. Lazy eviction + periodic sweep keeps memory bounded. In production the store is Redis with EXPIRE.",
                "O(1) per op", "O(sessions)", "Predictable tokens; never evicting expired entries; comparing times with < instead of <=.");

        add(l, "Game Leaderboard (Top N)", "Website: Leaderboard", M, "website,design,heap,sorted-set",
                "Implement addScore(player, score) and top(n) returning the n highest players, where a player's best score counts.",
                "Signal: ranking with updates -> a map for lookups plus a sorted structure for order.",
                """
                class Leaderboard {
                    private final Map<String, Integer> best = new HashMap<>();
                    void addScore(String p, int s) { best.merge(p, s, Math::max); }
                    List<String> top(int n) {
                        return best.entrySet().stream()
                                .sorted((a, b) -> b.getValue() - a.getValue())
                                .limit(n).map(Map.Entry::getKey).toList();
                    }
                }""",
                """
                class Leaderboard {
                    private final Map<String, Integer> best = new HashMap<>();
                    private final TreeSet<Map.Entry<Integer, String>> ranking = new TreeSet<>(
                            Map.Entry.<Integer, String>comparingByKey().reversed()
                                    .thenComparing(Map.Entry.comparingByValue()));

                    void addScore(String p, int s) {
                        Integer old = best.get(p);
                        if (old != null && old >= s) return;
                        if (old != null) ranking.remove(Map.entry(old, p));
                        best.put(p, s);
                        ranking.add(Map.entry(s, p));
                    }
                    List<String> top(int n) {
                        List<String> out = new ArrayList<>();
                        for (var e : ranking) { if (out.size() == n) break; out.add(e.getValue()); }
                        return out;
                    }
                }""",
                "Sorting everything on each request is O(m log m). A TreeSet keeps the order maintained incrementally. Redis ZSET solves this in production.",
                "O(log m) add, O(n) top", "O(m)", "Forgetting to remove the old score entry.");

        add(l, "Friend Suggestions (Mutual Friends)", "Website: Social Network", M, "website,graph,bfs,hash-map",
                "Given a friendship graph, suggest people for a user: friends-of-friends who are not already friends, ranked by number of mutual friends.",
                "Signal: 2-hop neighbourhood + counting -> map from candidate to mutual-friend count.",
                """
                static List<String> suggest(Map<String, Set<String>> g, String me) {
                    Map<String, Integer> score = new HashMap<>();
                    for (String friend : g.getOrDefault(me, Set.of()))
                        for (String fof : g.getOrDefault(friend, Set.of()))
                            if (!fof.equals(me) && !g.get(me).contains(fof)) score.merge(fof, 1, Integer::sum);
                    List<String> out = new ArrayList<>(score.keySet());
                    out.sort((a, b) -> score.get(b) - score.get(a));
                    return out;
                }""",
                """
                static List<String> suggest(Map<String, Set<String>> g, String me, int limit) {
                    Set<String> mine = g.getOrDefault(me, Set.of());
                    Map<String, Integer> score = new HashMap<>();
                    for (String f : mine)
                        for (String fof : g.getOrDefault(f, Set.of()))
                            if (!fof.equals(me) && !mine.contains(fof)) score.merge(fof, 1, Integer::sum);
                    return score.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                                    .thenComparing(Map.Entry.comparingByKey()))
                            .limit(limit).map(Map.Entry::getKey).toList();
                }""",
                "Only two hops are needed, so BFS beyond depth 2 is wasteful. Deterministic tie-breaking keeps UI stable between refreshes.",
                "O(sum of friend degrees)", "O(candidates)", "Suggesting yourself or existing friends; unstable ordering.");

        add(l, "Prevent Inventory Oversell", "Website: E-commerce", M, "website,concurrency,atomic,design",
                "Two customers click 'buy' on the last item at the same time. Design reserve(sku, qty) so stock never goes negative.",
                "Signal: check-then-act is a race condition. Make the check and the decrement a single atomic step.",
                """
                class Inventory {
                    private int stock;
                    Inventory(int stock) { this.stock = stock; }
                    boolean reserve(int qty) {
                        if (stock >= qty) {      // another thread can sneak in here!
                            stock -= qty;
                            return true;
                        }
                        return false;
                    }
                }""",
                """
                class Inventory {
                    private final AtomicInteger stock;
                    Inventory(int stock) { this.stock = new AtomicInteger(stock); }
                    boolean reserve(int qty) {
                        while (true) {
                            int cur = stock.get();
                            if (cur < qty) return false;
                            if (stock.compareAndSet(cur, cur - qty)) return true;   // retry if someone else changed it
                        }
                    }
                }
                // Database equivalent:
                // UPDATE inventory SET stock = stock - ? WHERE sku = ? AND stock >= ?   (check rows affected == 1)""",
                "compareAndSet only succeeds if the value is still what we read. Across several servers the guard must live in the database (conditional UPDATE or optimistic version column).",
                "O(1)", "O(1)", "Using synchronized on one server and assuming it protects a multi-server deployment; read-then-write in SQL.");

        add(l, "Normalize URL Path (Breadcrumbs)", "Website: Router", M, "website,stack,strings",
                "Simplify a URL path: collapse '//', resolve '.' and '..'. \"/a/./b/../../c/\" -> \"/c\".",
                "Signal: '..' undoes the previous directory -> stack of path segments.",
                """
                static String simplify(String path) {
                    String prev;
                    do {
                        prev = path;
                        path = path.replace("//", "/").replace("/./", "/");
                    } while (!path.equals(prev));
                    // ('..' handling would still be missing - the stack version is the complete answer)
                    return path;
                }""",
                """
                static String simplify(String path) {
                    Deque<String> stack = new ArrayDeque<>();
                    for (String part : path.split("/")) {
                        if (part.isEmpty() || part.equals(".")) continue;
                        if (part.equals("..")) stack.pollLast();      // go up one level (ignored at root)
                        else stack.addLast(part);
                    }
                    return "/" + String.join("/", stack);
                }""",
                "Never serve files from a path you have not normalised: '..' sequences are the classic path-traversal vulnerability.",
                "O(n)", "O(n)", "Popping from an empty stack at root; trailing slash in the result.");

        add(l, "De-duplicate Notifications", "Website: Notifications", M, "website,hash-map,design,time",
                "Show a notification only if an identical one (same user + type) was not shown in the last 60 seconds.",
                "Signal: remember the last-shown time per key and compare.",
                """
                class Notifier {
                    private final Map<String, Long> last = new HashMap<>();
                    boolean shouldSend(String user, String type, long nowSec) {
                        String key = user + ":" + type;
                        Long t = last.get(key);
                        if (t != null && nowSec - t < 60) return false;
                        last.put(key, nowSec);
                        return true;
                    }
                }""",
                """
                class Notifier {
                    private final Map<String, Long> last = new LinkedHashMap<>(16, 0.75f, true) {
                        protected boolean removeEldestEntry(Map.Entry<String, Long> e) { return size() > 100_000; }
                    };
                    boolean shouldSend(String user, String type, long nowSec) {
                        String key = user + '\\u0000' + type;           // separator that cannot appear in ids
                        Long prev = last.get(key);
                        if (prev != null && nowSec - prev < 60) return false;
                        last.put(key, nowSec);
                        return true;
                    }
                }""",
                "The intermediate version bounds memory with an access-ordered LinkedHashMap (an LRU) so old keys disappear automatically.",
                "O(1)", "O(keys)", "Key collisions like ('a:b','c') vs ('a','b:c'); unbounded map growth.");

        add(l, "Decode String Template", "Stack", M, "stack,strings,recursion",
                "Decode strings of the form k[encoded]: \"3[a2[c]]\" -> \"accaccacc\".",
                "Signal: nested repetition -> stack of (count, builder-so-far).",
                """
                static String decode(String s) {
                    String prev = "";
                    while (!s.equals(prev)) {
                        prev = s;
                        s = Pattern.compile("(\\\\d+)\\\\[([a-z]*)]").matcher(s)
                                .replaceAll(m -> m.group(2).repeat(Integer.parseInt(m.group(1))));
                    }
                    return s;
                }""",
                """
                static String decode(String s) {
                    Deque<Integer> counts = new ArrayDeque<>();
                    Deque<StringBuilder> strings = new ArrayDeque<>();
                    StringBuilder cur = new StringBuilder();
                    int k = 0;
                    for (char c : s.toCharArray()) {
                        if (Character.isDigit(c)) k = k * 10 + (c - '0');
                        else if (c == '[') { counts.push(k); strings.push(cur); cur = new StringBuilder(); k = 0; }
                        else if (c == ']') {
                            StringBuilder prev = strings.pop();
                            prev.append(String.valueOf(cur).repeat(counts.pop()));
                            cur = prev;
                        } else cur.append(c);
                    }
                    return cur.toString();
                }""",
                "On '[' save the state, on ']' pop it and append the repeated content. Multi-digit counts need k = k*10 + digit.",
                "O(output size)", "O(depth)", "Treating counts as a single digit.");

        add(l, "Word Break (Search Tokenizer)", "Dynamic Programming", M, "dp,strings,website,search",
                "Given a string without spaces and a dictionary, can it be split into dictionary words? \"applepenapple\", [\"apple\",\"pen\"] -> true.",
                "Signal: prefix feasibility -> dp[i] = some j < i has dp[j] and s[j..i) in dictionary.",
                """
                static boolean wordBreak(String s, Set<String> dict) {
                    if (s.isEmpty()) return true;
                    for (int i = 1; i <= s.length(); i++) {
                        if (dict.contains(s.substring(0, i)) && wordBreak(s.substring(i), dict)) return true;
                    }
                    return false;
                }""",
                """
                static boolean wordBreak(String s, Set<String> dict) {
                    boolean[] dp = new boolean[s.length() + 1];
                    dp[0] = true;
                    for (int i = 1; i <= s.length(); i++) {
                        for (int j = 0; j < i; j++) {
                            if (dp[j] && dict.contains(s.substring(j, i))) { dp[i] = true; break; }
                        }
                    }
                    return dp[s.length()];
                }""",
                "Plain recursion recomputes the same suffixes exponentially. dp[i] caches whether the first i characters can be split.",
                "O(n^2)", "O(n)", "Forgetting dp[0] = true.");

        add(l, "Subarray Sum Equals K (Analytics Window)", "Hash Map", M, "arrays,prefix-sum,hash-map,website",
                "Count contiguous subarrays whose sum equals k (e.g. days in which total signups hit a target).",
                "Signal: subarray sum = prefix[j] - prefix[i]; look up how many earlier prefixes equal prefix - k.",
                """
                static int count(int[] a, int k) {
                    int c = 0;
                    for (int i = 0; i < a.length; i++) {
                        int sum = 0;
                        for (int j = i; j < a.length; j++) { sum += a[j]; if (sum == k) c++; }
                    }
                    return c;
                }""",
                """
                static int count(int[] a, int k) {
                    Map<Integer, Integer> seen = new HashMap<>();
                    seen.put(0, 1);                     // empty prefix
                    int sum = 0, c = 0;
                    for (int x : a) {
                        sum += x;
                        c += seen.getOrDefault(sum - k, 0);
                        seen.merge(sum, 1, Integer::sum);
                    }
                    return c;
                }""",
                "Sliding window fails with negative numbers; prefix sums + a frequency map work for any integers.",
                "O(n)", "O(n)", "Forgetting seen.put(0, 1).");

        add(l, "Daily Temperatures (Next Greater)", "Stack", M, "monotonic-stack,arrays",
                "For each day, how many days until a warmer temperature? 0 if none.",
                "Signal: 'next greater element' -> monotonic stack of indices.",
                """
                static int[] wait(int[] t) {
                    int[] out = new int[t.length];
                    for (int i = 0; i < t.length; i++)
                        for (int j = i + 1; j < t.length; j++)
                            if (t[j] > t[i]) { out[i] = j - i; break; }
                    return out;
                }""",
                """
                static int[] wait(int[] t) {
                    int[] out = new int[t.length];
                    Deque<Integer> stack = new ArrayDeque<>();      // indices with decreasing temps
                    for (int i = 0; i < t.length; i++) {
                        while (!stack.isEmpty() && t[i] > t[stack.peek()]) {
                            int j = stack.pop();
                            out[j] = i - j;
                        }
                        stack.push(i);
                    }
                    return out;
                }""",
                "Each index is pushed and popped once, so the nested-looking loops are O(n) total.",
                "O(n)", "O(n)", "Storing values instead of indices.");

        add(l, "Paginated Search with Cursor", "Website: Listing Page", M, "website,design,sorting,pagination",
                "OFFSET pagination gets slower on deep pages and repeats rows when data changes. Implement keyset (cursor) pagination: next page = items after the last (createdAt, id) seen.",
                "Signal: stable ordering + 'items after X' instead of 'skip N'.",
                """
                static List<Item> page(List<Item> sortedByTimeThenId, int offset, int size) {
                    int end = Math.min(offset + size, sortedByTimeThenId.size());
                    return sortedByTimeThenId.subList(Math.min(offset, end), end);   // OFFSET style
                }""",
                """
                record Item(long createdAt, long id, String title) {}

                static List<Item> pageAfter(List<Item> items, long lastCreatedAt, long lastId, int size) {
                    return items.stream()
                            .filter(i -> i.createdAt() < lastCreatedAt
                                      || (i.createdAt() == lastCreatedAt && i.id() < lastId))   // newest first
                            .limit(size)
                            .toList();
                }
                // SQL:  SELECT * FROM item
                //       WHERE (created_at, id) < (?, ?)
                //       ORDER BY created_at DESC, id DESC LIMIT ?     -- uses an index, no OFFSET scan""",
                "With OFFSET 100000 the database still reads and discards 100000 rows. Keyset pagination seeks straight to the position using an index. The id breaks ties between identical timestamps.",
                "O(page) with index", "O(page)", "Ordering by a non-unique column only (rows skipped/duplicated).");

        add(l, "Check Balanced HTML Tags", "Website: HTML Parser", M, "website,stack,parsing",
                "Given a simplified HTML string like \"<div><p></p></div>\", return true if every opening tag is closed in the right order.",
                "Signal: nesting -> stack; push on open tag, pop and compare on close tag.",
                """
                static boolean balanced(String html) {
                    String prev;
                    do {
                        prev = html;
                        html = html.replaceAll("<([a-z]+)></\\\\1>", "");
                    } while (!html.equals(prev));
                    return html.isEmpty();
                }""",
                """
                static boolean balanced(String html) {
                    Deque<String> stack = new ArrayDeque<>();
                    Matcher m = Pattern.compile("<(/?)([a-z][a-z0-9]*)>").matcher(html);
                    while (m.find()) {
                        boolean closing = !m.group(1).isEmpty();
                        String name = m.group(2);
                        if (!closing) stack.push(name);
                        else if (stack.isEmpty() || !stack.pop().equals(name)) return false;
                    }
                    return stack.isEmpty();
                }""",
                "Browsers are forgiving; validators are not. The stack catches both crossing tags (<b><i></b></i>) and unclosed tags.",
                "O(n)", "O(depth)", "Forgetting void tags such as <br> in a real parser.");

        return l;
    }
}
