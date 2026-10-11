package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.full;

/** Hard set B: graphs, trees and data-structure design. */
public final class HardGraphsTreesDesign {

    private HardGraphsTreesDesign() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty H = Difficulty.HARD;

        full(l, "Word Ladder II (All Shortest Paths)", "Graphs", H, "graph,bfs,backtracking,strings",
                "Transform beginWord into endWord changing one letter at a time, with every intermediate word in a dictionary. Return ALL shortest transformation sequences.",
                "Signal: BFS to learn each word's distance from the start, then DFS backwards from the end following only edges that decrease the distance by one.",
                null,
                """
                static List<List<String>> findLadders(String begin, String end, List<String> wordList) {
                    Set<String> dict = new HashSet<>(wordList);
                    List<List<String>> out = new ArrayList<>();
                    if (!dict.contains(end)) return out;

                    Map<String, Integer> dist = new HashMap<>();
                    Map<String, List<String>> parents = new HashMap<>();
                    Queue<String> q = new ArrayDeque<>(List.of(begin));
                    dist.put(begin, 0);
                    boolean found = false;
                    while (!q.isEmpty() && !found) {
                        for (int i = q.size(); i > 0; i--) {
                            String w = q.poll();
                            char[] cs = w.toCharArray();
                            for (int p = 0; p < cs.length; p++) {
                                char orig = cs[p];
                                for (char c = 'a'; c <= 'z'; c++) {
                                    if (c == orig) continue;
                                    cs[p] = c;
                                    String next = new String(cs);
                                    if (!dict.contains(next)) continue;
                                    Integer d = dist.get(next);
                                    if (d == null) {                               // first time seen: one level deeper
                                        dist.put(next, dist.get(w) + 1);
                                        parents.computeIfAbsent(next, k -> new ArrayList<>()).add(w);
                                        q.add(next);
                                        if (next.equals(end)) found = true;
                                    } else if (d == dist.get(w) + 1) {             // another shortest way to reach next
                                        parents.get(next).add(w);
                                    }
                                }
                                cs[p] = orig;
                            }
                        }
                    }
                    if (found) build(end, begin, parents, new LinkedList<>(List.of(end)), out);
                    return out;
                }
                static void build(String word, String begin, Map<String, List<String>> parents,
                                  LinkedList<String> path, List<List<String>> out) {
                    if (word.equals(begin)) { out.add(new ArrayList<>(path)); return; }
                    for (String p : parents.getOrDefault(word, List.of())) {
                        path.addFirst(p);
                        build(p, begin, parents, path, out);
                        path.removeFirst();
                    }
                }""",
                "A plain DFS over all paths is exponential. BFS first records, for every word, all predecessors that lie exactly one level closer to the start. Walking those parent links backwards from the end then yields only shortest paths, with no wasted exploration.",
                """
                dist ==> The BFS level (number of changes) of each word.
                d == dist.get(w) + 1 ==> next was already discovered at the same level; w is another equally short way to reach it, so record the extra parent.
                found ==> Stop expanding once the level containing the end word is finished (longer paths are not needed).
                parents ==> A DAG of 'previous words on a shortest path'.
                build(...) ==> DFS from the end to the beginning along parent links; addFirst/removeFirst is the choose/undo step.""",
                "A hard BFS+backtracking combination. Always explain the two phases. Word Ladder I (just the length) is the warm-up.",
                "O(N x L x 26 + paths)", "O(N x L)", "Running a DFS from the start without distance pruning (timeouts).");

        full(l, "Connect All Cities at Minimum Cost (Kruskal)", "Graphs", H, "graph,mst,union-find,sorting",
                "Given roads (cityA, cityB, cost), return the minimum cost to connect all n cities, or -1 if impossible.",
                "Signal: minimum spanning tree - sort edges by cost and add an edge only if it joins two different components.",
                null,
                """
                static int minimumCost(int n, int[][] connections) {
                    Arrays.sort(connections, Comparator.comparingInt(e -> e[2]));
                    int[] parent = new int[n + 1];
                    for (int i = 0; i <= n; i++) parent[i] = i;
                    int cost = 0, edges = 0;
                    for (int[] e : connections) {
                        int a = find(parent, e[0]), b = find(parent, e[1]);
                        if (a != b) {
                            parent[a] = b;
                            cost += e[2];
                            if (++edges == n - 1) return cost;
                        }
                    }
                    return -1;
                }
                static int find(int[] p, int x) { while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; } return x; }""",
                "Kruskal's algorithm greedily takes the cheapest edge that does not create a cycle. A spanning tree on n cities has exactly n - 1 edges, so we can stop early; fewer edges means the graph is not connected.",
                """
                sort by cost ==> The cheapest roads are considered first.
                find(parent, x) ==> Which component a city currently belongs to.
                a != b ==> Adding the edge joins two separate groups, so it cannot form a cycle.
                ++edges == n - 1 ==> A spanning tree is complete.
                return -1 ==> The edges ran out before all cities were joined.""",
                "Contrast with Prim's algorithm (priority queue from a start node). Mention that MST is used for network design and clustering.",
                "O(E log E)", "O(n)", "Adding edges that close cycles; 0-indexed vs 1-indexed cities.");

        full(l, "Path With Minimum Effort", "Graphs", H, "graph,dijkstra,binary-search,grid",
                "In a grid of heights you move up, down, left or right. The effort of a path is the maximum absolute height difference between consecutive cells. Return the minimum effort from the top-left to the bottom-right.",
                "Signal: Dijkstra where the 'distance' is the MAX edge on the path instead of the sum.",
                null,
                """
                static int minimumEffortPath(int[][] h) {
                    int R = h.length, C = h[0].length;
                    int[][] effort = new int[R][C];
                    for (int[] row : effort) Arrays.fill(row, Integer.MAX_VALUE);
                    effort[0][0] = 0;
                    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
                    pq.add(new int[] {0, 0, 0});                              // {effort, row, col}
                    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
                    while (!pq.isEmpty()) {
                        int[] cur = pq.poll();
                        int e = cur[0], r = cur[1], c = cur[2];
                        if (r == R - 1 && c == C - 1) return e;
                        if (e > effort[r][c]) continue;                      // stale queue entry
                        for (int[] d : dirs) {
                            int nr = r + d[0], nc = c + d[1];
                            if (nr < 0 || nc < 0 || nr >= R || nc >= C) continue;
                            int next = Math.max(e, Math.abs(h[nr][nc] - h[r][c]));
                            if (next < effort[nr][nc]) {
                                effort[nr][nc] = next;
                                pq.add(new int[] {next, nr, nc});
                            }
                        }
                    }
                    return 0;
                }""",
                "Dijkstra works for any 'monotone' path cost: the cost of a path never decreases as it grows. Here the cost is the largest step so far, so we always expand the cell with the smallest worst step first.",
                """
                effort[r][c] ==> The smallest possible 'largest step' on any path found so far to that cell.
                Math.max(e, abs(difference)) ==> The path cost combines with an edge by taking the maximum.
                if (e > effort[r][c]) continue ==> Skip outdated queue entries (a better route was already found).
                next < effort[nr][nc] ==> Only push when we improved the cell.
                first poll of the target ==> Its effort is final.""",
                "Alternative: binary search on the answer with BFS feasibility, or Union-Find sorting edges. Showing the 'Dijkstra with max' generalisation is a strong signal.",
                "O(RC log RC)", "O(RC)", "Summing differences instead of taking the maximum.");

        full(l, "Swim in Rising Water", "Graphs", H, "graph,dijkstra,binary-search,union-find",
                "At time t you can swim into any cell whose elevation is at most t. Starting at (0,0), what is the least time needed to reach (n-1,n-1)?",
                "Signal: the answer is the smallest possible maximum elevation along some path - again Dijkstra with a max cost.",
                null,
                """
                static int swimInWater(int[][] grid) {
                    int n = grid.length;
                    boolean[][] seen = new boolean[n][n];
                    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
                    pq.add(new int[] {grid[0][0], 0, 0});
                    seen[0][0] = true;
                    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
                    int time = 0;
                    while (!pq.isEmpty()) {
                        int[] cur = pq.poll();
                        time = Math.max(time, cur[0]);                       // we must wait until this cell is reachable
                        if (cur[1] == n - 1 && cur[2] == n - 1) return time;
                        for (int[] d : dirs) {
                            int nr = cur[1] + d[0], nc = cur[2] + d[1];
                            if (nr >= 0 && nc >= 0 && nr < n && nc < n && !seen[nr][nc]) {
                                seen[nr][nc] = true;
                                pq.add(new int[] {grid[nr][nc], nr, nc});
                            }
                        }
                    }
                    return time;
                }""",
                "The priority queue always opens the lowest accessible cell next, like a rising tide flooding cells in elevation order. The running maximum of the elevations opened is the time at which the target becomes reachable.",
                """
                pq ordered by elevation ==> The frontier cell that becomes swimmable earliest is processed first.
                time = Math.max(time, cur[0]) ==> The tide level never decreases; we may need to wait for this cell.
                seen[][] ==> Mark when added so each cell enters the queue once.
                return when reaching (n-1, n-1) ==> The tide level at that moment is the minimum possible.""",
                "Same family as 'Path With Minimum Effort' and 'Trapping Rain Water II'. Mention the binary search + BFS alternative.",
                "O(n^2 log n)", "O(n^2)", "Treating it as a sum-of-weights shortest path.");

        full(l, "Reconstruct Itinerary (Eulerian Path)", "Graphs", H, "graph,dfs,eulerian-path,hierholzer",
                "Given airline tickets [from, to], reconstruct the itinerary that starts at JFK and uses every ticket exactly once; choose the lexicographically smallest valid route.",
                "Signal: an Eulerian path - Hierholzer's algorithm with neighbours visited in sorted order, adding airports in post-order.",
                null,
                """
                static List<String> findItinerary(List<List<String>> tickets) {
                    Map<String, PriorityQueue<String>> graph = new HashMap<>();
                    for (List<String> t : tickets) {
                        graph.computeIfAbsent(t.get(0), k -> new PriorityQueue<>()).add(t.get(1));
                    }
                    LinkedList<String> route = new LinkedList<>();
                    dfs("JFK", graph, route);
                    return route;
                }
                static void dfs(String airport, Map<String, PriorityQueue<String>> graph, LinkedList<String> route) {
                    PriorityQueue<String> next = graph.get(airport);
                    while (next != null && !next.isEmpty()) {
                        dfs(next.poll(), graph, route);                // use the smallest remaining ticket first
                    }
                    route.addFirst(airport);                           // post-order: add after all outgoing tickets are used
                }""",
                "Plain DFS can get stuck in a dead end before using all tickets. Hierholzer's trick: keep following smallest tickets, and when an airport has no tickets left add it to the FRONT of the route. Dead ends end up at the back, which is exactly where the final stop belongs.",
                """
                PriorityQueue<String> per airport ==> Destinations sorted alphabetically, so poll() always gives the smallest unused ticket.
                dfs(next.poll(), ...) ==> Consume a ticket and travel along it.
                route.addFirst(airport) ==> Post-order insertion; builds the path in reverse as the recursion unwinds.
                No visited set ==> Edges (tickets) are consumed instead of nodes, because airports can be visited many times.""",
                "Knowing Hierholzer's algorithm is the whole point; also state the existence condition for an Eulerian path (degree rules).",
                "O(E log E)", "O(E)", "Marking airports visited; adding to the route in pre-order.");

        full(l, "Minimum Height Trees", "Graphs", H, "graph,topological-sort,tree,leaf-trimming",
                "For a tree with n nodes, rooting it at some node gives a tree with some height. Return all roots that give the minimum height.",
                "Signal: the best roots are the centre(s) of the tree - peel leaves layer by layer until one or two nodes remain.",
                null,
                """
                static List<Integer> findMinHeightTrees(int n, int[][] edges) {
                    if (n == 1) return List.of(0);
                    List<Set<Integer>> adj = new ArrayList<>();
                    for (int i = 0; i < n; i++) adj.add(new HashSet<>());
                    for (int[] e : edges) { adj.get(e[0]).add(e[1]); adj.get(e[1]).add(e[0]); }

                    List<Integer> leaves = new ArrayList<>();
                    for (int i = 0; i < n; i++) if (adj.get(i).size() == 1) leaves.add(i);

                    int remaining = n;
                    while (remaining > 2) {
                        remaining -= leaves.size();
                        List<Integer> newLeaves = new ArrayList<>();
                        for (int leaf : leaves) {
                            int neighbour = adj.get(leaf).iterator().next();
                            adj.get(neighbour).remove(leaf);
                            if (adj.get(neighbour).size() == 1) newLeaves.add(neighbour);
                        }
                        leaves = newLeaves;
                    }
                    return leaves;
                }""",
                "A longest path in a tree has a middle: one centre node if its length is even, two if odd. A root at the centre minimises the height. Removing all current leaves repeatedly converges on the centre, similar to a topological sort from the outside in.",
                """
                adj ==> Adjacency sets so that a leaf can be unlinked from its neighbour in O(1).
                leaves ==> Nodes with exactly one neighbour.
                remaining > 2 ==> A tree has at most two centres.
                adj.get(neighbour).remove(leaf) ==> Trim the leaf.
                size() == 1 ==> The neighbour just became a leaf for the next layer.""",
                "A neat 'center of a tree' technique. The brute force (BFS from every node) is O(n^2). Handle n = 1 and n = 2.",
                "O(n)", "O(n)", "Trying every root with BFS (too slow).");

        full(l, "Binary Tree Maximum Path Sum", "Trees", H, "tree,dfs,dp",
                "A path may start and end at any node. Return the maximum sum of node values along any path.",
                "Signal: for each node compute the best downward path (return to parent) and the best bent path through the node (update a global).",
                null,
                """
                static int best;
                static int maxPathSum(TreeNode root) {
                    best = Integer.MIN_VALUE;
                    gain(root);
                    return best;
                }
                static int gain(TreeNode n) {
                    if (n == null) return 0;
                    int left = Math.max(0, gain(n.left));                 // ignore negative branches
                    int right = Math.max(0, gain(n.right));
                    best = Math.max(best, n.val + left + right);          // path bending at n
                    return n.val + Math.max(left, right);                 // a parent may extend only one side
                }""",
                "Two different quantities: the best path that bends at the node (uses both children) can only be the answer, never extended upward; the value returned to the parent must be a straight line down one side. Negative branches are dropped with max(0, ...).",
                """
                Math.max(0, gain(n.left)) ==> A branch with a negative sum only hurts, so take nothing instead.
                n.val + left + right ==> The best path whose highest node is n.
                best = Math.max(best, ...) ==> Update the global maximum.
                return n.val + Math.max(left, right) ==> Only one branch can continue to the parent.
                Integer.MIN_VALUE ==> All values may be negative, so start from the smallest possible.""",
                "Same pattern as Diameter of Binary Tree. Initialise best with MIN_VALUE, not 0.",
                "O(n)", "O(h)", "Initialising best with 0; returning the bent path to the parent.");

        full(l, "Vertical Order Traversal of a Binary Tree", "Trees", H, "tree,bfs,sorting,hash-map",
                "Report node values column by column (left to right). Within a column, sort nodes by row, and by value when in the same row and column.",
                "Signal: give each node coordinates (row, col) with BFS/DFS, then group by column and sort.",
                null,
                """
                static List<List<Integer>> verticalTraversal(TreeNode root) {
                    TreeMap<Integer, List<int[]>> columns = new TreeMap<>();      // col -> list of {row, value}
                    Deque<Object[]> q = new ArrayDeque<>();
                    q.add(new Object[] {root, 0, 0});
                    while (!q.isEmpty()) {
                        Object[] cur = q.poll();
                        TreeNode n = (TreeNode) cur[0];
                        int row = (int) cur[1], col = (int) cur[2];
                        columns.computeIfAbsent(col, k -> new ArrayList<>()).add(new int[] {row, n.val});
                        if (n.left != null) q.add(new Object[] {n.left, row + 1, col - 1});
                        if (n.right != null) q.add(new Object[] {n.right, row + 1, col + 1});
                    }
                    List<List<Integer>> out = new ArrayList<>();
                    for (List<int[]> nodes : columns.values()) {
                        nodes.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);
                        List<Integer> col = new ArrayList<>();
                        for (int[] x : nodes) col.add(x[1]);
                        out.add(col);
                    }
                    return out;
                }""",
                "Going to the left child decreases the column by 1 and going right increases it. A TreeMap keyed by column keeps columns in left-to-right order. Sorting each column by (row, value) applies the required tie-breaking.",
                """
                TreeMap<Integer, List<int[]>> ==> Columns automatically sorted by column index.
                {node, row, col} in the queue ==> Carry coordinates along with each node.
                col - 1 and col + 1 ==> Horizontal movement for left and right children.
                sort by row then value ==> Nodes at the same row and column are ordered by value.""",
                "Ask about tie-breaking rules before coding. Used in rendering and layout problems.",
                "O(n log n)", "O(n)", "Ordering by value only (ignoring row).");

        full(l, "Lowest Common Ancestor of a Binary Tree", "Trees", H, "tree,dfs,recursion",
                "Find the lowest common ancestor of two nodes in an ordinary binary tree (not necessarily a BST).",
                "Signal: return a node if it is p or q; if both sides return something, the current node is the LCA.",
                null,
                """
                static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
                    if (root == null || root == p || root == q) return root;
                    TreeNode left = lowestCommonAncestor(root.left, p, q);
                    TreeNode right = lowestCommonAncestor(root.right, p, q);
                    if (left != null && right != null) return root;       // p and q are in different subtrees
                    return left != null ? left : right;                   // both are on the same side (or one is the ancestor)
                }""",
                "Each call reports 'where did I find p or q in my subtree?'. If the two targets are found in different subtrees, the current node is where their paths meet. If only one side reports something, that value (p, q or already the LCA) is bubbled up.",
                """
                root == p || root == q ==> Found a target (or null): report it upward.
                left != null && right != null ==> One target in each subtree: this node is the lowest common ancestor.
                return left != null ? left : right ==> Pass up whatever was found; if both are in one subtree the LCA was already found below.""",
                "Be clear about the assumption that both nodes exist. If they might be missing, track a found count.",
                "O(n)", "O(h)", "Using BST comparisons on a general tree.");

        full(l, "Design Add and Search Words (Trie with Wildcards)", "Trie", H, "design,trie,dfs",
                "Design a structure with addWord(word) and search(pattern), where '.' in the pattern matches any single letter.",
                "Signal: a trie where '.' makes the search branch into all children.",
                null,
                """
                class WordDictionary {
                    private static class Node { Node[] next = new Node[26]; boolean end; }
                    private final Node root = new Node();

                    void addWord(String word) {
                        Node n = root;
                        for (char c : word.toCharArray()) {
                            if (n.next[c - 'a'] == null) n.next[c - 'a'] = new Node();
                            n = n.next[c - 'a'];
                        }
                        n.end = true;
                    }

                    boolean search(String pattern) {
                        return dfs(pattern, 0, root);
                    }
                    private boolean dfs(String p, int i, Node n) {
                        if (i == p.length()) return n.end;
                        char c = p.charAt(i);
                        if (c != '.') {
                            Node child = n.next[c - 'a'];
                            return child != null && dfs(p, i + 1, child);
                        }
                        for (Node child : n.next) {                          // wildcard: try every branch
                            if (child != null && dfs(p, i + 1, child)) return true;
                        }
                        return false;
                    }
                }""",
                "A normal trie lookup follows one path. A wildcard expands the search into every existing child at that level; the other letters still follow a single branch. Worst case is exponential in the number of dots, but pruning by existing children keeps it practical.",
                """
                Node[] next = new Node[26] ==> One slot per letter; null means no such continuation.
                end ==> Marks that a whole word ends at this node.
                i == p.length() ==> The pattern is used up; it matches only if a word ends here.
                c != '.' ==> A normal letter: follow exactly one child.
                for (Node child : n.next) ==> For '.', try all existing children until one works.""",
                "Related to autocomplete and spell check. Discuss memory (arrays vs maps in trie nodes).",
                "O(L) add, O(26^dots x L) worst search", "O(total characters)", "Returning true when the pattern ends at a node that is not a word end.");

        full(l, "Design Underground System", "Design", H, "design,hash-map,averages",
                "Customers check in and out at stations. Implement checkIn(id, station, t), checkOut(id, station, t) and getAverageTime(start, end) for all completed trips between two stations.",
                "Signal: one map for open trips and one aggregate (sum, count) per station pair.",
                null,
                """
                class UndergroundSystem {
                    private record CheckIn(String station, int time) {}
                    private final Map<Integer, CheckIn> open = new HashMap<>();
                    private final Map<String, long[]> totals = new HashMap<>();          // "A->B" -> {sumTime, trips}

                    void checkIn(int id, String station, int t) {
                        open.put(id, new CheckIn(station, t));
                    }

                    void checkOut(int id, String station, int t) {
                        CheckIn in = open.remove(id);                                       // trip is finished
                        String key = in.station() + "->" + station;
                        long[] agg = totals.computeIfAbsent(key, k -> new long[2]);
                        agg[0] += t - in.time();
                        agg[1]++;
                    }

                    double getAverageTime(String start, String end) {
                        long[] agg = totals.get(start + "->" + end);
                        return (double) agg[0] / agg[1];
                    }
                }""",
                "Storing every trip would grow forever and make queries slow. Only the running sum and count per route are needed to answer an average in O(1). Removing the open trip at check-out frees memory.",
                """
                open ==> Passengers currently travelling: id -> where and when they entered.
                open.remove(id) ==> Removing at check-out both retrieves the entry and prevents leaks.
                totals[key] = {sum, count} ==> Enough information for an average without storing each trip.
                (double) agg[0] / agg[1] ==> Cast before dividing so the average keeps its decimals.""",
                "A design question testing choice of aggregates. Follow-ups: concurrency (use ConcurrentHashMap and atomic updates) and what to do with a missing check-in.",
                "O(1)", "O(open trips + routes)", "Keeping a list of all trips; integer division.");

        full(l, "Design Hit Counter", "Design", H, "design,queue,sliding-window,design",
                "Design a counter that records hits with timestamps (seconds) and returns the number of hits in the last 300 seconds.",
                "Signal: a queue of timestamps where old ones are removed (or a circular array of 300 buckets).",
                null,
                """
                class HitCounter {
                    private final int[] times = new int[300];
                    private final int[] hits = new int[300];

                    void hit(int timestamp) {
                        int slot = timestamp % 300;
                        if (times[slot] != timestamp) {          // slot belongs to an older second: reset it
                            times[slot] = timestamp;
                            hits[slot] = 0;
                        }
                        hits[slot]++;
                    }

                    int getHits(int timestamp) {
                        int total = 0;
                        for (int i = 0; i < 300; i++) {
                            if (timestamp - times[i] < 300) total += hits[i];
                        }
                        return total;
                    }
                }""",
                "A queue would grow with traffic. A circular buffer of 300 one-second buckets has fixed memory: each second maps to a slot, and a slot whose stored timestamp is outdated is reused. Counting reads at most 300 buckets regardless of traffic.",
                """
                timestamp % 300 ==> The bucket for that second; the buffer wraps around every 5 minutes.
                times[slot] != timestamp ==> The bucket holds an old second: reset it before reuse.
                timestamp - times[i] < 300 ==> Only buckets from the last 300 seconds count.
                Fixed arrays ==> Memory does not depend on the number of hits.""",
                "Discuss the trade-off with a deque of timestamps (simple, memory proportional to hits) and how to handle many users (per-key counters, Redis).",
                "O(300) = O(1)", "O(300)", "An unbounded list of hits.");

        full(l, "All O(1) Data Structure", "Design", H, "design,linked-list,hash-map",
                "Support inc(key), dec(key), getMaxKey() and getMinKey(), all in O(1).",
                "Signal: a doubly linked list of buckets ordered by count, each holding a set of keys, plus a map from key to bucket.",
                null,
                """
                class AllOne {
                    private static class Bucket {
                        int count;
                        Set<String> keys = new LinkedHashSet<>();
                        Bucket prev, next;
                        Bucket(int count) { this.count = count; }
                    }
                    private final Bucket head = new Bucket(0), tail = new Bucket(0);    // sentinels
                    private final Map<String, Bucket> where = new HashMap<>();

                    AllOne() { head.next = tail; tail.prev = head; }

                    void inc(String key) {
                        Bucket cur = where.get(key);
                        Bucket target = (cur == null) ? head : cur;
                        int newCount = (cur == null) ? 1 : cur.count + 1;
                        if (target.next == tail || target.next.count != newCount) insertAfter(target, new Bucket(newCount));
                        Bucket dest = target.next;
                        dest.keys.add(key);
                        where.put(key, dest);
                        if (cur != null) removeKey(cur, key);
                    }

                    void dec(String key) {
                        Bucket cur = where.get(key);
                        if (cur == null) return;
                        if (cur.count == 1) {
                            where.remove(key);
                        } else {
                            Bucket target = cur.prev;
                            if (target == head || target.count != cur.count - 1) {
                                target = new Bucket(cur.count - 1);
                                insertAfter(cur.prev, target);
                            }
                            target.keys.add(key);
                            where.put(key, target);
                        }
                        removeKey(cur, key);
                    }

                    String getMaxKey() { return tail.prev == head ? "" : tail.prev.keys.iterator().next(); }
                    String getMinKey() { return head.next == tail ? "" : head.next.keys.iterator().next(); }

                    private void removeKey(Bucket b, String key) {
                        b.keys.remove(key);
                        if (b.keys.isEmpty()) { b.prev.next = b.next; b.next.prev = b.prev; }
                    }
                    private void insertAfter(Bucket node, Bucket nu) {
                        nu.prev = node; nu.next = node.next;
                        node.next.prev = nu; node.next = nu;
                    }
                }""",
                "Buckets are kept in ascending count order. Incrementing moves a key to the next bucket (creating it if the counts are not adjacent); decrementing moves it to the previous one. Because we only ever move to a neighbour, each operation is O(1), and min/max are the buckets next to the sentinels.",
                """
                head / tail sentinels ==> Avoid null checks at the ends of the bucket list.
                where ==> key -> the bucket currently holding it, for O(1) access.
                target.next.count != newCount ==> The neighbouring bucket has a different count, so a new bucket must be inserted.
                removeKey(...) ==> After moving a key, delete the old bucket if it became empty.
                tail.prev / head.next ==> The max and min buckets.""",
                "A hard design problem that combines a hash map with a custom linked list (like LRU/LFU). Draw the bucket list before coding.",
                "O(1) per op", "O(keys)", "Forgetting to remove empty buckets.");

        full(l, "LFU Cache", "Design", H, "design,hash-map,linked-hash-set",
                "Design a Least Frequently Used cache: get and put in O(1); when full, evict the least frequently used key, breaking ties by least recently used.",
                "Signal: map key -> value+frequency and map frequency -> ordered set of keys, plus the current minimum frequency.",
                null,
                """
                class LFUCache {
                    private final int capacity;
                    private int minFreq = 0;
                    private final Map<Integer, Integer> values = new HashMap<>();
                    private final Map<Integer, Integer> freq = new HashMap<>();
                    private final Map<Integer, LinkedHashSet<Integer>> byFreq = new HashMap<>();

                    LFUCache(int capacity) { this.capacity = capacity; }

                    int get(int key) {
                        if (!values.containsKey(key)) return -1;
                        touch(key);
                        return values.get(key);
                    }

                    void put(int key, int value) {
                        if (capacity == 0) return;
                        if (values.containsKey(key)) {
                            values.put(key, value);
                            touch(key);
                            return;
                        }
                        if (values.size() == capacity) {
                            LinkedHashSet<Integer> lowest = byFreq.get(minFreq);
                            int evict = lowest.iterator().next();                      // oldest among the least used
                            lowest.remove(evict);
                            values.remove(evict);
                            freq.remove(evict);
                        }
                        values.put(key, value);
                        freq.put(key, 1);
                        byFreq.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
                        minFreq = 1;
                    }

                    private void touch(int key) {
                        int f = freq.get(key);
                        freq.put(key, f + 1);
                        byFreq.get(f).remove(key);
                        if (byFreq.get(f).isEmpty()) {
                            byFreq.remove(f);
                            if (minFreq == f) minFreq++;                               // the old minimum bucket vanished
                        }
                        byFreq.computeIfAbsent(f + 1, k -> new LinkedHashSet<>()).add(key);
                    }
                }""",
                "byFreq groups keys by how often they were used; a LinkedHashSet keeps insertion order so the first element is the least recently used in that group. minFreq always points at the group to evict from, and is updated only in the two situations where it can change: a new key (becomes 1) or the minimum group emptying.",
                """
                values / freq ==> The data and the use counter of each key.
                byFreq ==> frequency -> keys in recency order (oldest first).
                minFreq ==> Avoids searching for the lowest frequency bucket.
                lowest.iterator().next() ==> Evict: lowest frequency, and the oldest key within it.
                touch(key) ==> Move the key from bucket f to f + 1 and fix minFreq if the old bucket emptied.""",
                "Compare with LRU (a linked hash map). Explain both tie rules out loud. This problem is a favourite 'hard design' question.",
                "O(1) per op", "O(capacity)", "Forgetting to update minFreq; wrong eviction within a tie.");

        full(l, "Design a Text Editor (Gap Buffer Idea)", "Design", H, "design,stack,deque,strings",
                "Implement a text editor with a cursor: addText, deleteText(k), cursorLeft(k) and cursorRight(k) (each returning the last up-to-10 characters left of the cursor).",
                "Signal: two stacks - text left of the cursor and text right of it. Moving the cursor transfers characters between them.",
                null,
                """
                class TextEditor {
                    private final StringBuilder left = new StringBuilder();            // text before the cursor
                    private final Deque<Character> right = new ArrayDeque<>();         // text after the cursor, nearest on top

                    void addText(String text) { left.append(text); }

                    int deleteText(int k) {
                        int deleted = Math.min(k, left.length());
                        left.setLength(left.length() - deleted);
                        return deleted;
                    }

                    String cursorLeft(int k) {
                        for (int i = 0; i < k && left.length() > 0; i++) {
                            right.push(left.charAt(left.length() - 1));
                            left.setLength(left.length() - 1);
                        }
                        return lastTen();
                    }

                    String cursorRight(int k) {
                        for (int i = 0; i < k && !right.isEmpty(); i++) left.append(right.pop());
                        return lastTen();
                    }

                    private String lastTen() {
                        return left.substring(Math.max(0, left.length() - 10));
                    }
                }""",
                "Editing happens around the cursor, so storing the text as two parts (before and after) makes insertion and deletion O(1) at the cursor, unlike a single array where you shift everything. Real editors use a gap buffer or a rope for the same reason.",
                """
                left ==> A StringBuilder holding everything before the cursor; appending and truncating the end is cheap.
                right ==> A stack of characters after the cursor; the nearest character is on top.
                cursorLeft ==> Move characters from the end of left onto the stack.
                cursorRight ==> Pop characters from the stack back onto left.
                left.substring(max(0, len - 10)) ==> The ten characters just left of the cursor.""",
                "Mention gap buffers (Emacs), ropes and piece tables (VS Code) as real implementations, and undo support with a command stack.",
                "O(k)", "O(n)", "Using one array and shifting on every insert.");

        full(l, "Design Parking Lot (Object-Oriented Design)", "Design", H, "design,oop,enum,interfaces",
                "Design a parking lot with different vehicle sizes and spot sizes. park(vehicle) returns a ticket or fails if full; leave(ticket) frees the spot and computes the fee.",
                "Signal: model the nouns as classes (Vehicle, Spot, Ticket), keep rules in one place, and think about extension points (new vehicle types, pricing).",
                null,
                """
                enum Size { SMALL, MEDIUM, LARGE }

                record Vehicle(String plate, Size size) {}
                record Ticket(String id, Spot spot, Vehicle vehicle, long enteredAt) {}

                class Spot {
                    final int number;
                    final Size size;
                    Vehicle occupant;
                    Spot(int number, Size size) { this.number = number; this.size = size; }
                    boolean fits(Vehicle v) { return occupant == null && size.ordinal() >= v.size().ordinal(); }
                }

                interface PricingPolicy { long feeCents(long minutesParked, Size size); }

                class ParkingLot {
                    private final List<Spot> spots;
                    private final PricingPolicy pricing;
                    private final Map<String, Ticket> active = new HashMap<>();

                    ParkingLot(List<Spot> spots, PricingPolicy pricing) {
                        this.spots = spots;
                        this.pricing = pricing;
                    }

                    Optional<Ticket> park(Vehicle v, long now) {
                        Spot best = null;
                        for (Spot s : spots) {                                   // smallest spot that fits wastes the least space
                            if (s.fits(v) && (best == null || s.size.ordinal() < best.size.ordinal())) best = s;
                        }
                        if (best == null) return Optional.empty();
                        best.occupant = v;
                        Ticket t = new Ticket(UUID.randomUUID().toString(), best, v, now);
                        active.put(t.id(), t);
                        return Optional.of(t);
                    }

                    long leave(String ticketId, long now) {
                        Ticket t = active.remove(ticketId);
                        if (t == null) throw new IllegalArgumentException("unknown ticket");
                        t.spot().occupant = null;
                        long minutes = Math.max(1, (now - t.enteredAt() + 59) / 60);
                        return pricing.feeCents(minutes, t.spot().size);
                    }
                }""",
                "The goal is clean responsibilities: Spot knows if a vehicle fits, ParkingLot knows how to allocate, PricingPolicy is a strategy so fees can change without touching parking logic. The linear search is fine to start; per-size free lists make allocation O(1) when the lot is big.",
                """
                size.ordinal() >= v.size().ordinal() ==> A bigger spot can hold a smaller vehicle, never the reverse.
                smallest fitting spot ==> A simple allocation strategy; mention others (nearest to the entrance).
                Optional<Ticket> ==> 'Lot full' is a normal outcome, not an exception.
                PricingPolicy (strategy) ==> New pricing rules become new classes.
                active map ==> Look up a ticket in O(1) when the car leaves.""",
                "OOD interviews score structure and extensibility more than code volume. Clarify requirements first, then name classes, relationships and one design pattern (strategy). Discuss concurrency for two cars taking the same spot.",
                "O(spots) park", "O(spots)", "One giant class; hard-coded prices; ignoring thread safety.");

        full(l, "Design Tic-Tac-Toe with O(1) Moves", "Design", H, "design,arrays,counting",
                "Design an n x n Tic-Tac-Toe where move(row, col, player) returns the winner (or 0), checking the winner in O(1) per move.",
                "Signal: keep counters per row, column and both diagonals instead of scanning the board.",
                null,
                """
                class TicTacToe {
                    private final int n;
                    private final int[] rows, cols;
                    private int diag, anti;

                    TicTacToe(int n) {
                        this.n = n;
                        rows = new int[n];
                        cols = new int[n];
                    }

                    int move(int row, int col, int player) {
                        int delta = (player == 1) ? 1 : -1;                  // player 1 adds, player 2 subtracts
                        rows[row] += delta;
                        cols[col] += delta;
                        if (row == col) diag += delta;
                        if (row + col == n - 1) anti += delta;
                        if (Math.abs(rows[row]) == n || Math.abs(cols[col]) == n
                                || Math.abs(diag) == n || Math.abs(anti) == n) {
                            return player;
                        }
                        return 0;
                    }
                }""",
                "A line is won when all n cells belong to one player. If player 1 adds +1 and player 2 adds -1 to the counter of every line the cell is on, a line counter reaches +n or -n exactly when one player owns all its cells. No board storage is even needed.",
                """
                rows[row] += delta ==> Each line has a running balance between the two players.
                row == col ==> The cell lies on the main diagonal.
                row + col == n - 1 ==> The cell lies on the anti-diagonal.
                Math.abs(counter) == n ==> One player filled the whole line.
                return player ==> The winner of this move; otherwise 0.""",
                "A neat 'replace scanning with counters' design. Compare with the O(n) per move version.",
                "O(1) per move", "O(n)", "Scanning the whole board after every move.");

        return l;
    }
}
