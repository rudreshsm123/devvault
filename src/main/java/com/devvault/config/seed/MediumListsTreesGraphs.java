package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.full;

/** Intermediate set B: linked lists, trees and graphs. */
public final class MediumListsTreesGraphs {

    private MediumListsTreesGraphs() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty M = Difficulty.MEDIUM;

        full(l, "Add Two Numbers (Linked Lists)", "Linked List", M, "linked-list,math,carry",
                "Two non-negative numbers are stored as linked lists with the digits in reverse order. Return their sum as a linked list.",
                "Signal: schoolbook addition from the least significant digit, with a carry.",
                null,
                """
                static ListNode addTwo(ListNode a, ListNode b) {
                    ListNode dummy = new ListNode(0), tail = dummy;
                    int carry = 0;
                    while (a != null || b != null || carry != 0) {
                        int sum = carry;
                        if (a != null) { sum += a.val; a = a.next; }
                        if (b != null) { sum += b.val; b = b.next; }
                        tail.next = new ListNode(sum % 10);
                        tail = tail.next;
                        carry = sum / 10;
                    }
                    return dummy.next;
                }""",
                "The reverse order is a gift: the head is already the ones digit, so we add exactly as on paper. The loop continues while a digit or a carry remains, which also handles lists of different lengths.",
                """
                dummy / tail ==> Build the result list with a fake head to avoid special cases.
                while (a != null || b != null || carry != 0) ==> Continue while any input digit or leftover carry exists (e.g. 99 + 1 produces a final carry digit).
                sum % 10 ==> The digit to store in this position.
                carry = sum / 10 ==> 0 or 1 carried to the next position.
                return dummy.next ==> Skip the fake head.""",
                "Ask whether the digits are reversed or forward; forward order needs stacks or reversal. Edge cases: different lengths and a final carry.",
                "O(max(n, m))", "O(max(n, m))", "Forgetting the last carry digit.");

        full(l, "Remove Nth Node From End of List", "Linked List", M, "linked-list,two-pointers",
                "Remove the nth node from the end of a singly linked list in one pass.",
                "Signal: keep two pointers n nodes apart; when the front one reaches the end, the back one is just before the target.",
                """
                static ListNode removeNth(ListNode head, int n) {
                    int length = 0;
                    for (ListNode x = head; x != null; x = x.next) length++;
                    ListNode dummy = new ListNode(0);
                    dummy.next = head;
                    ListNode cur = dummy;
                    for (int i = 0; i < length - n; i++) cur = cur.next;
                    cur.next = cur.next.next;
                    return dummy.next;
                }""",
                """
                static ListNode removeNth(ListNode head, int n) {
                    ListNode dummy = new ListNode(0);
                    dummy.next = head;
                    ListNode fast = dummy, slow = dummy;
                    for (int i = 0; i <= n; i++) fast = fast.next;     // gap of n + 1 nodes
                    while (fast != null) {
                        fast = fast.next;
                        slow = slow.next;
                    }
                    slow.next = slow.next.next;
                    return dummy.next;
                }""",
                "A fixed gap between two pointers measures distance from the end without knowing the length. The dummy node lets us remove the head with the same code.",
                """
                ListNode dummy ==> Handles the case where the head itself must be removed.
                for (i = 0; i <= n; i++) fast = fast.next ==> Advance fast n + 1 steps so that slow ends up one node BEFORE the target.
                while (fast != null) ==> Move both until fast falls off the end.
                slow.next = slow.next.next ==> Unlink the target node.""",
                "One-pass solutions are the point. Explain the n + 1 gap clearly (one before the target).",
                "O(n)", "O(1)", "Gap of n instead of n + 1 so you hold the target rather than its predecessor.");

        full(l, "Reorder List", "Linked List", M, "linked-list,reverse,merge",
                "Reorder L0 -> L1 -> ... -> Ln into L0 -> Ln -> L1 -> Ln-1 -> ... in place.",
                "Signal: find the middle, reverse the second half, then interleave the halves.",
                null,
                """
                static void reorder(ListNode head) {
                    if (head == null || head.next == null) return;
                    ListNode slow = head, fast = head;
                    while (fast.next != null && fast.next.next != null) {
                        slow = slow.next;
                        fast = fast.next.next;
                    }
                    ListNode second = reverse(slow.next);
                    slow.next = null;                                 // cut the list in two
                    ListNode first = head;
                    while (second != null) {
                        ListNode t1 = first.next, t2 = second.next;
                        first.next = second;
                        second.next = t1;
                        first = t1;
                        second = t2;
                    }
                }
                static ListNode reverse(ListNode h) {
                    ListNode prev = null;
                    while (h != null) { ListNode n = h.next; h.next = prev; prev = h; h = n; }
                    return prev;
                }""",
                "Three known techniques combined: slow/fast pointers (middle), reversal, and merging two lists alternately. Cutting the list before merging prevents cycles.",
                """
                slow / fast ==> After the loop slow is the end of the first half.
                reverse(slow.next) ==> The second half reversed so its nodes come out from the tail.
                slow.next = null ==> Detach the halves; otherwise the first half would still point into the second.
                first.next = second; second.next = t1 ==> Interleave: first, second, next of first, ...
                t1, t2 ==> Save the next pointers BEFORE rewiring.""",
                "Break the problem into three named steps out loud; each step is something you know.",
                "O(n)", "O(1)", "Forgetting slow.next = null which creates a cycle.");

        full(l, "Copy List with Random Pointer", "Linked List", M, "linked-list,hash-map,deep-copy",
                "Each node has next and random pointers. Create a deep copy of the list.",
                "Signal: you need a map from each original node to its copy so random pointers can be re-pointed.",
                """
                static Node copy(Node head) {
                    Map<Node, Node> map = new HashMap<>();
                    for (Node n = head; n != null; n = n.next) map.put(n, new Node(n.val));
                    for (Node n = head; n != null; n = n.next) {
                        map.get(n).next = map.get(n.next);
                        map.get(n).random = map.get(n.random);
                    }
                    return map.get(head);
                }""",
                """
                static Node copy(Node head) {
                    if (head == null) return null;
                    for (Node n = head; n != null; n = n.next.next) {      // 1) weave copies: A -> A' -> B -> B'
                        Node c = new Node(n.val);
                        c.next = n.next;
                        n.next = c;
                    }
                    for (Node n = head; n != null; n = n.next.next) {      // 2) set random pointers
                        if (n.random != null) n.next.random = n.random.next;
                    }
                    Node newHead = head.next;
                    for (Node n = head; n != null; n = n.next) {           // 3) unweave
                        Node c = n.next;
                        n.next = c.next;
                        if (c.next != null) c.next = c.next.next;
                    }
                    return newHead;
                }""",
                "The map version uses O(n) memory. The weaving trick places each copy right after its original, so the copy of n.random is simply n.random.next - no map needed.",
                """
                map.put(n, new Node(n.val)) ==> First pass: create all copies without any links.
                map.get(n).random = map.get(n.random) ==> Second pass: translate original pointers into copy pointers.
                n.next = c ==> Weaving: each copy is inserted directly after its original.
                n.next.random = n.random.next ==> The copy of the random target is the node right after the target.
                third loop ==> Restore the original list and extract the copy list.""",
                "Offer the map solution first, then the O(1) space improvement. Mention deep versus shallow copy.",
                "O(n)", "O(n) / O(1)", "Linking copies to original nodes instead of to other copies.");

        full(l, "Lowest Common Ancestor of a BST", "Trees", M, "tree,bst,iteration",
                "Given a binary search tree and two nodes, find their lowest common ancestor (the deepest node that has both as descendants).",
                "Signal: in a BST, the LCA is the first node whose value lies between the two targets.",
                """
                static TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
                    if (root == null || root == p || root == q) return root;
                    TreeNode left = lca(root.left, p, q);
                    TreeNode right = lca(root.right, p, q);
                    if (left != null && right != null) return root;
                    return left != null ? left : right;
                }""",
                """
                static TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
                    TreeNode cur = root;
                    while (cur != null) {
                        if (p.val < cur.val && q.val < cur.val) cur = cur.left;
                        else if (p.val > cur.val && q.val > cur.val) cur = cur.right;
                        else return cur;               // paths diverge (or cur equals one of them)
                    }
                    return null;
                }""",
                "The first version works for any binary tree. For a BST we can use ordering: if both values are smaller go left, if both are larger go right, otherwise the current node splits them.",
                """
                p.val < cur.val && q.val < cur.val ==> Both targets are in the left subtree.
                p.val > cur.val && q.val > cur.val ==> Both are in the right subtree.
                else return cur ==> One is on each side (or cur is one of them): this is the lowest node covering both.
                left != null && right != null ==> In the general tree version, a node that finds one target in each subtree is the LCA.""",
                "Always say whether you are exploiting the BST property. The general-tree solution is the follow-up.",
                "O(h)", "O(1)", "Using the BST shortcut on a non-BST.");

        full(l, "Binary Tree Right Side View", "Trees", M, "tree,bfs,level-order",
                "Return the values visible when looking at a binary tree from the right side (the last node of each level).",
                "Signal: level-order traversal; the last node of each level is visible.",
                """
                static List<Integer> rightSide(TreeNode root) {
                    List<Integer> out = new ArrayList<>();
                    dfs(root, 0, out);
                    return out;
                }
                static void dfs(TreeNode n, int depth, List<Integer> out) {
                    if (n == null) return;
                    if (depth == out.size()) out.add(n.val);     // first node seen at this depth (visiting right first)
                    dfs(n.right, depth + 1, out);
                    dfs(n.left, depth + 1, out);
                }""",
                """
                static List<Integer> rightSide(TreeNode root) {
                    List<Integer> out = new ArrayList<>();
                    if (root == null) return out;
                    Queue<TreeNode> q = new ArrayDeque<>(List.of(root));
                    while (!q.isEmpty()) {
                        int size = q.size();
                        for (int i = 0; i < size; i++) {
                            TreeNode n = q.poll();
                            if (i == size - 1) out.add(n.val);   // last in this level
                            if (n.left != null) q.add(n.left);
                            if (n.right != null) q.add(n.right);
                        }
                    }
                    return out;
                }""",
                "BFS processes one level at a time; the last node processed is the rightmost. DFS visiting right before left records the first node of each new depth.",
                """
                int size = q.size() ==> Freeze the count of nodes on this level before adding children.
                i == size - 1 ==> The rightmost node of the level.
                depth == out.size() ==> DFS version: if no value has been recorded for this depth yet, this is the first node seen there (and we visit right children first).""",
                "A level-order template question. Know both BFS and DFS versions.",
                "O(n)", "O(w) / O(h)", "Taking the right child only, which misses nodes hidden behind a missing right subtree.");

        full(l, "Kth Smallest Element in a BST", "Trees", M, "tree,bst,inorder",
                "Return the kth smallest value in a binary search tree.",
                "Signal: in-order traversal of a BST visits values in ascending order; stop after k.",
                """
                static int kthSmallest(TreeNode root, int k) {
                    List<Integer> all = new ArrayList<>();
                    inorder(root, all);
                    return all.get(k - 1);
                }
                static void inorder(TreeNode n, List<Integer> out) {
                    if (n == null) return;
                    inorder(n.left, out);
                    out.add(n.val);
                    inorder(n.right, out);
                }""",
                """
                static int kthSmallest(TreeNode root, int k) {
                    Deque<TreeNode> stack = new ArrayDeque<>();
                    TreeNode cur = root;
                    while (cur != null || !stack.isEmpty()) {
                        while (cur != null) { stack.push(cur); cur = cur.left; }
                        cur = stack.pop();
                        if (--k == 0) return cur.val;
                        cur = cur.right;
                    }
                    throw new IllegalArgumentException("k out of range");
                }""",
                "Collecting every value wastes time and memory. The iterative in-order walk can stop the moment the kth node is visited.",
                """
                stack.push(cur); cur = cur.left ==> Go to the smallest unvisited node, remembering the path.
                cur = stack.pop() ==> This is the next smallest value in sorted order.
                --k == 0 ==> We have visited k nodes; this is the answer, so return immediately.
                cur = cur.right ==> Continue with the right subtree.""",
                "Follow-up: the tree is modified often and k queries are frequent - store subtree sizes in each node for O(h) answers.",
                "O(h + k)", "O(h)", "Using pre-order or post-order (not sorted).");

        full(l, "Construct Binary Tree from Preorder and Inorder", "Trees", M, "tree,divide-and-conquer,hash-map",
                "Rebuild a binary tree from its preorder and inorder traversal arrays (all values distinct).",
                "Signal: preorder's first element is the root; its position in inorder splits left and right subtrees.",
                null,
                """
                static int pre = 0;
                static TreeNode build(int[] preorder, int[] inorder) {
                    Map<Integer, Integer> index = new HashMap<>();
                    for (int i = 0; i < inorder.length; i++) index.put(inorder[i], i);
                    pre = 0;
                    return build(preorder, index, 0, inorder.length - 1);
                }
                static TreeNode build(int[] preorder, Map<Integer, Integer> index, int lo, int hi) {
                    if (lo > hi) return null;
                    TreeNode root = new TreeNode(preorder[pre++]);
                    int mid = index.get(root.val);
                    root.left = build(preorder, index, lo, mid - 1);       // left first: preorder order
                    root.right = build(preorder, index, mid + 1, hi);
                    return root;
                }""",
                "Preorder tells the next root; inorder tells how many nodes belong to its left. The map gives the inorder position in O(1) instead of searching each time.",
                """
                Map<Integer, Integer> index ==> value -> position in inorder, so splitting is O(1).
                preorder[pre++] ==> The next unused preorder value is the root of the current range.
                mid = index.get(root.val) ==> Everything left of mid in inorder belongs to the left subtree.
                build left before right ==> Preorder lists the whole left subtree before the right one, so the shared counter stays in sync.
                lo > hi ==> Empty range: no subtree.""",
                "The order (left before right) matters because of the shared preorder pointer. Mention the postorder variant.",
                "O(n)", "O(n)", "Building the right subtree first.");

        full(l, "Path Sum II", "Trees", M, "tree,backtracking,dfs",
                "Return all root-to-leaf paths whose node values add up to a target.",
                "Signal: DFS with a running path; add on the way down, remove on the way back (backtracking).",
                null,
                """
                static List<List<Integer>> pathSum(TreeNode root, int target) {
                    List<List<Integer>> out = new ArrayList<>();
                    dfs(root, target, new ArrayList<>(), out);
                    return out;
                }
                static void dfs(TreeNode n, int remaining, List<Integer> path, List<List<Integer>> out) {
                    if (n == null) return;
                    path.add(n.val);
                    if (n.left == null && n.right == null && remaining == n.val) {
                        out.add(new ArrayList<>(path));                // copy: the path keeps changing
                    }
                    dfs(n.left, remaining - n.val, path, out);
                    dfs(n.right, remaining - n.val, path, out);
                    path.remove(path.size() - 1);                       // backtrack
                }""",
                "The shared path list is extended when entering a node and shortened when leaving it, so only O(h) memory is used. A copy is made only when a valid leaf is reached.",
                """
                path.add(n.val) ==> Choose: include this node in the current path.
                n.left == null && n.right == null ==> A leaf; only root-to-leaf paths count.
                new ArrayList<>(path) ==> Save a snapshot; the original list will be modified further.
                path.remove(path.size() - 1) ==> Undo the choice when returning to the parent.""",
                "Typical backtracking on a tree. Say why you copy the path.",
                "O(n)", "O(h)", "Storing the path itself instead of a copy.");

        full(l, "Diameter of Binary Tree", "Trees", M, "tree,dfs,height",
                "Return the length (in edges) of the longest path between any two nodes of a binary tree.",
                "Signal: the best path through a node is left height + right height; compute heights and update a global best on the way up.",
                """
                static int diameter(TreeNode root) {
                    if (root == null) return 0;
                    int through = height(root.left) + height(root.right);
                    return Math.max(through, Math.max(diameter(root.left), diameter(root.right)));
                }
                static int height(TreeNode n) {
                    return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right));
                }""",
                """
                static int best;
                static int diameter(TreeNode root) {
                    best = 0;
                    height(root);
                    return best;
                }
                static int height(TreeNode n) {
                    if (n == null) return 0;
                    int left = height(n.left), right = height(n.right);
                    best = Math.max(best, left + right);        // longest path bending at n
                    return 1 + Math.max(left, right);           // height reported to the parent
                }""",
                "The first version recomputes heights for every node (O(n^2) worst case). The second computes the height and the diameter in the same traversal.",
                """
                left + right ==> The path that goes down the left side, up to n, and down the right side.
                best = Math.max(best, left + right) ==> Update the global answer using the heights of both children.
                return 1 + Math.max(left, right) ==> A parent can only extend ONE of the two branches, so it receives the taller one.
                height(n.left) + height(n.right) ==> The naive per-node calculation.""",
                "The pattern 'return one thing to the parent while updating another thing globally' appears in many tree problems (max path sum, longest univalue path).",
                "O(n)", "O(h)", "Returning the diameter instead of the height from the recursion.");

        full(l, "Clone Graph", "Graphs", M, "graph,dfs,hash-map",
                "Return a deep copy of a connected undirected graph given one node.",
                "Signal: a map from original node to clone prevents infinite loops and shares clones.",
                null,
                """
                static Node cloneGraph(Node node) {
                    if (node == null) return null;
                    return clone(node, new HashMap<>());
                }
                static Node clone(Node n, Map<Node, Node> copies) {
                    if (copies.containsKey(n)) return copies.get(n);
                    Node copy = new Node(n.val);
                    copies.put(n, copy);                               // register BEFORE recursing
                    for (Node nb : n.neighbors) copy.neighbors.add(clone(nb, copies));
                    return copy;
                }""",
                "The map serves as both the 'visited' set and the lookup from original to clone. Registering the clone before visiting neighbours stops cycles from recursing forever.",
                """
                copies.containsKey(n) ==> Already cloned: reuse it, which also breaks cycles.
                copies.put(n, copy) ==> Must happen before the neighbour loop, otherwise a cycle returns to n and clones it again.
                copy.neighbors.add(clone(nb, copies)) ==> Rebuild every edge using clones.""",
                "Same technique as copying a list with random pointers. Offer a BFS version too.",
                "O(V + E)", "O(V)", "Registering the clone after recursing (infinite recursion).");

        full(l, "Pacific Atlantic Water Flow", "Graphs", M, "graph,grid,dfs,multi-source",
                "Water flows from a cell to a neighbour with equal or lower height. Return cells from which water can reach both the Pacific (top/left edges) and Atlantic (bottom/right edges).",
                "Signal: reverse the flow - search uphill from each ocean and intersect the two reachable sets.",
                null,
                """
                static List<List<Integer>> flow(int[][] h) {
                    int R = h.length, C = h[0].length;
                    boolean[][] pac = new boolean[R][C], atl = new boolean[R][C];
                    for (int r = 0; r < R; r++) { dfs(h, pac, r, 0); dfs(h, atl, r, C - 1); }
                    for (int c = 0; c < C; c++) { dfs(h, pac, 0, c); dfs(h, atl, R - 1, c); }
                    List<List<Integer>> out = new ArrayList<>();
                    for (int r = 0; r < R; r++)
                        for (int c = 0; c < C; c++)
                            if (pac[r][c] && atl[r][c]) out.add(List.of(r, c));
                    return out;
                }
                static void dfs(int[][] h, boolean[][] seen, int r, int c) {
                    seen[r][c] = true;
                    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
                    for (int[] d : dirs) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr < 0 || nc < 0 || nr >= h.length || nc >= h[0].length) continue;
                        if (seen[nr][nc] || h[nr][nc] < h[r][c]) continue;      // going uphill only
                        dfs(h, seen, nr, nc);
                    }
                }""",
                "Checking every cell with its own search is O(n^2 * m^2). Starting from the edges and moving to cells that are equal or higher visits each cell at most twice.",
                """
                dfs(h, pac, r, 0) ==> Start from every cell touching the Pacific edge.
                h[nr][nc] < h[r][c] ==> Reverse direction: we only step to neighbours that are the same or higher (water could flow down to us).
                seen[r][c] ==> Reachability from that ocean.
                pac[r][c] && atl[r][c] ==> Cells reachable from both oceans form the answer.""",
                "Reverse thinking (search from the targets) is the lesson. Grid DFS with direction arrays is a template to memorise.",
                "O(R x C)", "O(R x C)", "Starting from every cell; moving downhill in the reversed search.");

        full(l, "Rotting Oranges", "Graphs", M, "graph,grid,bfs,multi-source",
                "In a grid, 2 is a rotten orange, 1 a fresh one, 0 empty. Each minute rot spreads to 4-neighbours. Return the minutes until no fresh oranges remain, or -1.",
                "Signal: multi-source BFS - start with all rotten oranges in the queue together.",
                null,
                """
                static int orangesRotting(int[][] g) {
                    int R = g.length, C = g[0].length, fresh = 0;
                    Queue<int[]> q = new ArrayDeque<>();
                    for (int r = 0; r < R; r++)
                        for (int c = 0; c < C; c++) {
                            if (g[r][c] == 2) q.add(new int[] {r, c});
                            else if (g[r][c] == 1) fresh++;
                        }
                    int minutes = 0;
                    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
                    while (!q.isEmpty() && fresh > 0) {
                        minutes++;
                        for (int i = q.size(); i > 0; i--) {
                            int[] cur = q.poll();
                            for (int[] d : dirs) {
                                int nr = cur[0] + d[0], nc = cur[1] + d[1];
                                if (nr >= 0 && nc >= 0 && nr < R && nc < C && g[nr][nc] == 1) {
                                    g[nr][nc] = 2;
                                    fresh--;
                                    q.add(new int[] {nr, nc});
                                }
                            }
                        }
                    }
                    return fresh == 0 ? minutes : -1;
                }""",
                "All rotten oranges spread at the same time, so they all start in the queue. Each BFS level is one minute. Counting fresh oranges tells us whether everything rotted.",
                """
                q.add({r, c}) for every 2 ==> Several starting points at once (multi-source BFS).
                fresh ==> The number of fresh oranges remaining; also the success condition.
                for (i = q.size(); ...) ==> Process one whole minute at a time.
                g[nr][nc] = 2 ==> Mark as rotten immediately so it is not queued twice.
                return fresh == 0 ? minutes : -1 ==> Unreachable fresh oranges mean -1.""",
                "The pattern covers 'nearest X' problems: walls and gates, 01 matrix, and fire spreading.",
                "O(R x C)", "O(R x C)", "Counting minutes for the last level when nothing new rots.");

        full(l, "Shortest Path in a Binary Matrix", "Graphs", M, "graph,grid,bfs,8-directions",
                "Find the length of the shortest clear path from the top-left to the bottom-right in a binary grid, moving in 8 directions through 0 cells. Return -1 if none.",
                "Signal: unweighted shortest path on a grid -> BFS.",
                null,
                """
                static int shortestPath(int[][] g) {
                    int n = g.length;
                    if (g[0][0] == 1 || g[n - 1][n - 1] == 1) return -1;
                    Queue<int[]> q = new ArrayDeque<>();
                    q.add(new int[] {0, 0});
                    g[0][0] = 1;                                   // mark visited
                    int steps = 1;
                    while (!q.isEmpty()) {
                        for (int i = q.size(); i > 0; i--) {
                            int[] cur = q.poll();
                            if (cur[0] == n - 1 && cur[1] == n - 1) return steps;
                            for (int dr = -1; dr <= 1; dr++)
                                for (int dc = -1; dc <= 1; dc++) {
                                    int nr = cur[0] + dr, nc = cur[1] + dc;
                                    if (nr >= 0 && nc >= 0 && nr < n && nc < n && g[nr][nc] == 0) {
                                        g[nr][nc] = 1;
                                        q.add(new int[] {nr, nc});
                                    }
                                }
                        }
                        steps++;
                    }
                    return -1;
                }""",
                "BFS explores cells in order of distance, so the first time the target is polled the path length is minimal. Marking cells as visited when they are enqueued keeps each cell in the queue once.",
                """
                g[0][0] == 1 || g[n-1][n-1] == 1 ==> If the start or end is blocked there is no path.
                int steps = 1 ==> The path length counts cells, so the start cell counts as 1.
                dr, dc in -1..1 ==> Eight neighbours (the (0,0) offset is harmless because that cell is already marked).
                g[nr][nc] = 1 ==> Visit-marking on enqueue prevents duplicates and saves a separate visited array.""",
                "Mention A* for large grids. Marking on enqueue vs on dequeue is a common discussion point.",
                "O(n^2)", "O(n^2)", "Marking visited on dequeue, which lets a cell enter the queue many times.");

        full(l, "Graph Valid Tree", "Graphs", M, "graph,union-find,tree",
                "Given n nodes and a list of undirected edges, decide whether they form a valid tree (connected and no cycles).",
                "Signal: a tree on n nodes has exactly n - 1 edges and is connected. Union-Find detects cycles.",
                null,
                """
                static boolean validTree(int n, int[][] edges) {
                    if (edges.length != n - 1) return false;
                    int[] parent = new int[n];
                    for (int i = 0; i < n; i++) parent[i] = i;
                    for (int[] e : edges) {
                        int a = find(parent, e[0]), b = find(parent, e[1]);
                        if (a == b) return false;                    // joining two nodes already connected = cycle
                        parent[a] = b;
                    }
                    return true;
                }
                static int find(int[] parent, int x) {
                    while (parent[x] != x) {
                        parent[x] = parent[parent[x]];               // path halving
                        x = parent[x];
                    }
                    return x;
                }""",
                "With exactly n - 1 edges, 'no cycle' and 'connected' are equivalent, so only cycle detection is needed. Union-Find merges components and reports an edge that connects two nodes already in the same set.",
                """
                edges.length != n - 1 ==> Quick rejection: too few edges can not connect, too many must contain a cycle.
                find(parent, x) ==> The representative (root) of the set containing x.
                a == b ==> Both endpoints were already connected, so this edge closes a cycle.
                parent[a] = b ==> Union: merge the two sets.
                parent[x] = parent[parent[x]] ==> Path compression keeps the trees shallow for near-constant time.""",
                "Know Union-Find (DSU) well: find, union, path compression, union by rank. It appears in many graph questions.",
                "O(n alpha(n))", "O(n)", "Checking only for cycles and forgetting connectivity.");

        full(l, "Number of Connected Components", "Graphs", M, "graph,dfs,union-find",
                "Return the number of connected components in an undirected graph with n nodes and a list of edges.",
                "Signal: DFS/BFS from each unvisited node, or Union-Find where each union reduces the component count by one.",
                """
                static int components(int n, int[][] edges) {
                    List<List<Integer>> g = new ArrayList<>();
                    for (int i = 0; i < n; i++) g.add(new ArrayList<>());
                    for (int[] e : edges) { g.get(e[0]).add(e[1]); g.get(e[1]).add(e[0]); }
                    boolean[] seen = new boolean[n];
                    int count = 0;
                    for (int i = 0; i < n; i++) {
                        if (!seen[i]) { count++; dfs(g, seen, i); }
                    }
                    return count;
                }
                static void dfs(List<List<Integer>> g, boolean[] seen, int u) {
                    seen[u] = true;
                    for (int v : g.get(u)) if (!seen[v]) dfs(g, seen, v);
                }""",
                """
                static int components(int n, int[][] edges) {
                    int[] parent = new int[n];
                    for (int i = 0; i < n; i++) parent[i] = i;
                    int count = n;
                    for (int[] e : edges) {
                        int a = find(parent, e[0]), b = find(parent, e[1]);
                        if (a != b) { parent[a] = b; count--; }
                    }
                    return count;
                }
                static int find(int[] p, int x) { while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; } return x; }""",
                "Start with n components (every node alone). Each successful union merges two components into one, so the count drops by one.",
                """
                count = n ==> Initially every node is its own component.
                a != b ==> The edge connects two different components.
                parent[a] = b; count-- ==> Merge them; one fewer component.
                new unvisited node in the DFS version ==> Each new DFS start discovers one whole component.""",
                "Be ready to explain when you would prefer DSU (edges arrive over time) versus DFS.",
                "O(n + e)", "O(n)", "Counting edges instead of components.");

        full(l, "Redundant Connection", "Graphs", M, "graph,union-find,cycle",
                "A tree with n nodes had one extra edge added. Return the extra edge (the last one in the input that can be removed to restore the tree).",
                "Signal: process edges in order with Union-Find; the first edge whose endpoints are already connected closes the cycle.",
                null,
                """
                static int[] findRedundant(int[][] edges) {
                    int[] parent = new int[edges.length + 1];
                    for (int i = 0; i < parent.length; i++) parent[i] = i;
                    for (int[] e : edges) {
                        int a = find(parent, e[0]), b = find(parent, e[1]);
                        if (a == b) return e;
                        parent[a] = b;
                    }
                    return new int[0];
                }
                static int find(int[] p, int x) { while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; } return x; }""",
                "Edges form a tree until the extra one arrives. That edge connects two nodes that are already in the same set, so it is detected the moment it is processed.",
                """
                parent = new int[edges.length + 1] ==> Nodes are labelled 1..n and there are n edges, so n + 1 slots cover them.
                a == b ==> The endpoints are already connected: this edge creates the cycle.
                return e ==> The input order guarantees this is the last such edge.""",
                "Same DSU template as Graph Valid Tree. The directed-graph variant (Redundant Connection II) is hard.",
                "O(n alpha(n))", "O(n)", "Using DFS and mishandling the 'last edge' requirement.");

        full(l, "Accounts Merge (Merging User Profiles)", "Website: Accounts", M, "website,union-find,hash-map,strings",
                "Each account is a name plus several emails. Accounts that share any email belong to the same person. Merge them and return each person's sorted emails.",
                "Signal: emails are the glue - union accounts that share an email, then group emails by root.",
                null,
                """
                static List<List<String>> mergeAccounts(List<List<String>> accounts) {
                    int n = accounts.size();
                    int[] parent = new int[n];
                    for (int i = 0; i < n; i++) parent[i] = i;
                    Map<String, Integer> owner = new HashMap<>();
                    for (int i = 0; i < n; i++) {
                        for (int j = 1; j < accounts.get(i).size(); j++) {
                            String email = accounts.get(i).get(j);
                            if (owner.containsKey(email)) union(parent, i, owner.get(email));
                            else owner.put(email, i);
                        }
                    }
                    Map<Integer, TreeSet<String>> groups = new HashMap<>();
                    for (var e : owner.entrySet()) {
                        groups.computeIfAbsent(find(parent, e.getValue()), k -> new TreeSet<>()).add(e.getKey());
                    }
                    List<List<String>> out = new ArrayList<>();
                    for (var g : groups.entrySet()) {
                        List<String> row = new ArrayList<>();
                        row.add(accounts.get(g.getKey()).get(0));
                        row.addAll(g.getValue());
                        out.add(row);
                    }
                    return out;
                }
                static int find(int[] p, int x) { while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; } return x; }
                static void union(int[] p, int a, int b) { p[find(p, a)] = find(p, b); }""",
                "This is how real sites de-duplicate users. Each email remembers the first account that used it; a second account with the same email is unioned with that owner. Finally emails are grouped by the root account and sorted.",
                """
                owner (email -> account index) ==> The first account that claimed each email.
                union(parent, i, owner.get(email)) ==> A shared email proves both accounts are the same person.
                find(parent, e.getValue()) ==> The representative account of the person who owns the email.
                TreeSet<String> ==> Collects the emails of a merged person already sorted and without duplicates.
                row.add(accounts.get(root).get(0)) ==> The name comes from any account in the group (they share one person).""",
                "A very practical Union-Find example for e-commerce and CRM systems. Talk about identity resolution and the risk of wrongly merging people.",
                "O(N log N)", "O(N)", "Merging by name instead of by email; forgetting to sort.");

        full(l, "Is Graph Bipartite", "Graphs", M, "graph,bfs,coloring",
                "Decide whether the nodes of a graph can be split into two groups so every edge connects the two groups (2-colourable).",
                "Signal: colour with two colours using BFS; if a neighbour has the same colour as you, it is impossible.",
                null,
                """
                static boolean isBipartite(int[][] graph) {
                    int n = graph.length;
                    int[] color = new int[n];                      // 0 = unseen, 1 and -1 = the two colours
                    for (int start = 0; start < n; start++) {
                        if (color[start] != 0) continue;
                        Queue<Integer> q = new ArrayDeque<>();
                        q.add(start);
                        color[start] = 1;
                        while (!q.isEmpty()) {
                            int u = q.poll();
                            for (int v : graph[u]) {
                                if (color[v] == 0) { color[v] = -color[u]; q.add(v); }
                                else if (color[v] == color[u]) return false;
                            }
                        }
                    }
                    return true;
                }""",
                "A graph is bipartite exactly when it has no odd-length cycle. Alternate colours along edges; the first conflict proves an odd cycle.",
                """
                color[] ==> 0 means not coloured yet; 1 and -1 are the two groups.
                for (start...) ==> The graph may be disconnected, so each component needs its own BFS.
                color[v] = -color[u] ==> Give the neighbour the opposite colour.
                color[v] == color[u] ==> Two adjacent nodes forced into the same group: impossible.""",
                "Mention the use cases: scheduling conflicts, matching problems. Remember disconnected graphs.",
                "O(V + E)", "O(V)", "Colouring only one component.");

        full(l, "Word Search", "Backtracking", M, "grid,backtracking,dfs",
                "Return true if a word can be formed in a grid by moving to adjacent cells (up, down, left, right) without reusing a cell.",
                "Signal: DFS with backtracking; temporarily mark the current cell as used.",
                null,
                """
                static boolean exist(char[][] b, String word) {
                    for (int r = 0; r < b.length; r++)
                        for (int c = 0; c < b[0].length; c++)
                            if (dfs(b, word, 0, r, c)) return true;
                    return false;
                }
                static boolean dfs(char[][] b, String w, int i, int r, int c) {
                    if (i == w.length()) return true;
                    if (r < 0 || c < 0 || r >= b.length || c >= b[0].length || b[r][c] != w.charAt(i)) return false;
                    char saved = b[r][c];
                    b[r][c] = '#';                                          // mark as used
                    boolean found = dfs(b, w, i + 1, r + 1, c) || dfs(b, w, i + 1, r - 1, c)
                                 || dfs(b, w, i + 1, r, c + 1) || dfs(b, w, i + 1, r, c - 1);
                    b[r][c] = saved;                                        // restore (backtrack)
                    return found;
                }""",
                "Each path is explored deeply and undone when it fails. Marking the cell in the board itself avoids an extra visited array, and restoring it lets other paths use the cell later.",
                """
                i == w.length() ==> All characters matched.
                b[r][c] != w.charAt(i) ==> Out of range or the wrong letter: this path fails immediately (pruning).
                b[r][c] = '#' ==> The cell cannot be reused within the same path.
                || chain ==> Try the four directions; stop at the first success.
                b[r][c] = saved ==> Undo so a different path can use this cell.""",
                "Complexity is O(R*C*4^L) in the worst case. Follow-up: many words at once (trie) - see Word Search II.",
                "O(R x C x 4^L)", "O(L)", "Forgetting to restore the cell after the search.");

        return l;
    }
}
