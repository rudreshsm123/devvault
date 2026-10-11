package com.devvault.drill;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.devvault.drill.Gen.arrayLiteral;
import static com.devvault.drill.Gen.pick;
import static com.devvault.drill.Gen.program;
import static com.devvault.drill.Gen.programWith;
import static com.devvault.drill.Gen.randomArray;
import static com.devvault.drill.Gen.ri;
import static com.devvault.drill.Gen.shuffled;

/** Algorithm and data-structure questions with an exactly computed answer. */
final class AlgoFamilies {

    private AlgoFamilies() {
    }

    static List<Family> all() {
        List<Family> l = new ArrayList<>();
        l.add(new Family("big-o", "Complexity", "Time complexity of code", "complexity", 3, AlgoFamilies::bigO));
        l.add(new Family("binary-steps", "Searching", "Binary search steps", "algorithms", 3, AlgoFamilies::binarySteps));
        l.add(new Family("bubble-pass", "Sorting", "Bubble sort passes", "algorithms", 3, AlgoFamilies::bubblePass));
        l.add(new Family("sort-swaps", "Sorting", "Swaps needed to sort", "algorithms", 2, AlgoFamilies::sortSwaps));
        l.add(new Family("prefix-sum", "Arrays", "Range sums", "arrays", 3, AlgoFamilies::prefixSum));
        l.add(new Family("window-max", "Arrays", "Best window of k", "arrays", 3, AlgoFamilies::windowMax));
        l.add(new Family("kadane", "Arrays", "Maximum subarray sum", "arrays", 3, AlgoFamilies::kadane));
        l.add(new Family("pair-count", "Arrays", "Pairs with a target sum", "arrays", 3, AlgoFamilies::pairCount));
        l.add(new Family("matrix", "Arrays", "Matrix reading", "arrays", 3, AlgoFamilies::matrix));
        l.add(new Family("bst-order", "Trees", "Tree traversals", "trees", 3, AlgoFamilies::bstOrder));
        l.add(new Family("graph-bfs", "Graphs", "BFS visiting order", "graphs", 3, AlgoFamilies::graphBfs));
        l.add(new Family("components", "Graphs", "Connected components", "graphs", 3, AlgoFamilies::components));
        l.add(new Family("dp-coins", "Dynamic Programming", "Fewest coins", "dp", 3, AlgoFamilies::coins));
        l.add(new Family("dp-lis", "Dynamic Programming", "Longest increasing subsequence", "dp", 3, AlgoFamilies::lis));
        l.add(new Family("dp-lcs", "Dynamic Programming", "Longest common subsequence", "dp", 3, AlgoFamilies::lcs));
        l.add(new Family("dp-knapsack", "Dynamic Programming", "0/1 knapsack", "dp", 2, AlgoFamilies::knapsack));
        l.add(new Family("dp-stairs", "Dynamic Programming", "Counting ways", "dp", 3, AlgoFamilies::stairs));
        l.add(new Family("call-count", "Recursion", "Counting recursive calls", "recursion", 2, AlgoFamilies::callCount));
        l.add(new Family("josephus", "Recursion", "Josephus circle", "recursion", 2, AlgoFamilies::josephus));
        l.add(new Family("gcd-lcm", "Math", "GCD and LCM", "math", 3, AlgoFamilies::gcdLcm));
        l.add(new Family("primes", "Math", "Primes and divisors", "math", 3, AlgoFamilies::primes));
        l.add(new Family("bits-count", "Bit Manipulation", "Counting bits", "bits", 3, AlgoFamilies::bitsCount));
        l.add(new Family("mod-arith", "Math", "Modular arithmetic", "math", 3, AlgoFamilies::modArith));
        l.add(new Family("combinatorics", "Math", "Counting and combinations", "math", 2, AlgoFamilies::combinatorics));
        l.add(new Family("radix", "Number Systems", "Binary, octal and hex", "bits", 3, AlgoFamilies::radix));
        l.add(new Family("hashing", "Hashing", "Hash buckets", "collections", 2, AlgoFamilies::hashing));
        return l;
    }

    // ---------------------------------------------------------------- complexity

    private static final List<String> BIG_O = List.of("O(1)", "O(log n)", "O(n)", "O(n log n)", "O(n^2)", "O(2^n)", "O(sqrt n)");

    static Spec bigO(Random r, int tier) {
        String v = pick(r, "n", "size", "len", "count");
        String i = pick(r, "i", "k", "idx");
        String j = pick(r, "j", "m", "p");
        int c = ri(r, 2, 9);
        String body = pick(r, "total += " + i + ";", "ops++;", "arr[" + i + "] = " + i + " * " + c + ";", "count++;");
        int p = ri(r, 0, tier == 0 ? 3 : 8);
        String code;
        String answer;
        String why;
        switch (p) {
            case 0 -> { code = "for (int " + i + " = 0; " + i + " < " + v + "; " + i + "++) {\n    " + body + "\n}"; answer = "O(n)"; why = "One loop that runs " + v + " times."; }
            case 1 -> { code = "for (int " + i + " = 0; " + i + " < " + v + "; " + i + "++) {\n    for (int " + j + " = 0; " + j + " < " + v + "; " + j + "++) {\n        " + body + "\n    }\n}"; answer = "O(n^2)"; why = "A loop inside a loop: " + v + " x " + v + " runs."; }
            case 2 -> { code = "for (int " + i + " = 1; " + i + " < " + v + "; " + i + " *= " + c + ") {\n    " + body + "\n}"; answer = "O(log n)"; why = i + " is multiplied by " + c + " each time, so the loop runs about log(" + v + ") times."; }
            case 3 -> { code = body + "\n" + body.replace(i, i) + "\nreturn " + c + ";"; answer = "O(1)"; why = "A fixed number of statements, no matter how large " + v + " is."; }
            case 4 -> { code = "for (int " + i + " = 0; " + i + " < " + v + "; " + i + "++) {\n    for (int " + j + " = 1; " + j + " < " + v + "; " + j + " *= 2) {\n        " + body + "\n    }\n}"; answer = "O(n log n)"; why = "The outer loop runs n times and the inner loop doubles, so about log n times each."; }
            case 5 -> { code = "for (int " + i + " = 0; " + i + " < " + v + "; " + i + "++) {\n    " + body + "\n}\nfor (int " + j + " = 0; " + j + " < " + v + "; " + j + "++) {\n    " + body + "\n}"; answer = "O(n)"; why = "Two loops one after the other add up (n + n), which is still linear."; }
            case 6 -> { code = "for (int " + i + " = 0; " + i + " < " + v + "; " + i + "++) {\n    for (int " + j + " = 0; " + j + " < " + i + "; " + j + "++) {\n        " + body + "\n    }\n}"; answer = "O(n^2)"; why = "The inner loop runs 0 + 1 + 2 + ... + (n-1) times, about n^2 / 2."; }
            case 7 -> { code = "int " + i + " = 1;\nwhile (" + i + " * " + i + " <= " + v + ") {\n    " + body + "\n    " + i + "++;\n}"; answer = "O(sqrt n)"; why = i + " only has to reach the square root of " + v + "."; }
            default -> { code = "int f(int " + v + ") {\n    if (" + v + " <= 1) {\n        return " + v + ";\n    }\n    return f(" + v + " - 1) + f(" + v + " - 2);\n}"; answer = "O(2^n)"; why = "Every call makes two more calls, so the work doubles with each level."; }
        }
        return Spec.choice("Time complexity of code", "What is the time complexity (in terms of " + (p == 8 ? v : v) + ") of this code?", code, answer,
                shuffled(r, answer, BIG_O.subList(0, 6).stream().filter(x -> !x.equals(answer)).limit(4).toList()),
                why + " So the answer is " + answer + ".", false);
    }

    // ---------------------------------------------------------------- searching and sorting

    static Spec binarySteps(Random r, int tier) {
        int n = ri(r, 7, tier == 0 ? 12 : tier == 1 ? 20 : 31);
        int[] a = new int[n];
        int v = ri(r, 1, 5);
        for (int k = 0; k < n; k++) {
            a[k] = v;
            v += ri(r, 1, 6);
        }
        int target = r.nextInt(4) == 0 ? a[ri(r, 0, n - 1)] + 1 : a[ri(r, 0, n - 1)];
        int lo = 0, hi = n - 1, steps = 0;
        while (lo <= hi) {
            steps++;
            int mid = (lo + hi) >>> 1;
            if (a[mid] == target) break;
            if (a[mid] < target) lo = mid + 1; else hi = mid - 1;
        }
        String statements = "int[] a = " + arrayLiteral(a) + ";\nint target = " + target + ";\nint lo = 0, hi = a.length - 1, steps = 0;\nwhile (lo <= hi) {\n    steps++;\n    int mid = (lo + hi) >>> 1;\n    if (a[mid] == target) {\n        break;\n    }\n    if (a[mid] < target) {\n        lo = mid + 1;\n    } else {\n        hi = mid - 1;\n    }\n}\nSystem.out.println(steps);";
        return Spec.output("Binary search steps", "How many times does the loop body run?", program("", statements), String.valueOf(steps),
                "Each round compares the middle element and discards half of the range. Trace lo, hi and mid until the "
                        + "target is found or the range is empty: " + steps + " rounds.", true);
    }

    static Spec bubblePass(Random r, int tier) {
        int n = ri(r, 5, tier == 0 ? 5 : 7);
        int[] a = randomArray(r, n, 1, 9);
        int passes = ri(r, 1, tier == 0 ? 1 : 3);
        String init = arrayLiteral(a);
        int[] b = a.clone();
        for (int p = 0; p < passes; p++) {
            for (int i = 0; i + 1 < n - p; i++) {
                if (b[i] > b[i + 1]) { int t = b[i]; b[i] = b[i + 1]; b[i + 1] = t; }
            }
        }
        String statements = "int[] a = " + init + ";\nfor (int pass = 0; pass < " + passes + "; pass++) {\n    for (int i = 0; i + 1 < a.length - pass; i++) {\n        if (a[i] > a[i + 1]) {\n            int t = a[i];\n            a[i] = a[i + 1];\n            a[i + 1] = t;\n        }\n    }\n}\nSystem.out.println(Arrays.toString(a));";
        return Spec.output("Bubble sort passes", "What does this program print?", program("import java.util.Arrays;", statements), Arrays.toString(b),
                "Each pass swaps neighbours that are out of order, which moves the largest remaining value to the end. After "
                        + passes + " pass(es): " + Arrays.toString(b) + ".", true);
    }

    static Spec sortSwaps(Random r, int tier) {
        int n = ri(r, 4, tier == 0 ? 5 : 7);
        int[] a = randomArray(r, n, 1, 20);
        int[] b = a.clone();
        int swaps = 0;
        for (int i = 0; i < n; i++) for (int j = 0; j + 1 < n - i; j++) if (b[j] > b[j + 1]) { int t = b[j]; b[j] = b[j + 1]; b[j + 1] = t; swaps++; }
        return Spec.number("Swaps needed to sort", "Bubble sort swaps two NEIGHBOURING elements whenever the left one is larger. "
                        + "How many swaps does it make to sort " + Arrays.toString(a) + " in ascending order?", "", swaps,
                "The number of swaps equals the number of inversions: pairs (i < j) where a[i] > a[j]. Here that is " + swaps + ".", false);
    }

    // ---------------------------------------------------------------- arrays

    static Spec prefixSum(Random r, int tier) {
        int n = ri(r, 6, 10);
        int[] a = randomArray(r, n, -5, 20);
        int lo = ri(r, 0, n - 3), hi = ri(r, lo + 1, n - 1);
        int s = 0;
        for (int i = lo; i <= hi; i++) s += a[i];
        String statements = "int[] a = " + arrayLiteral(a) + ";\nint sum = 0;\nfor (int i = " + lo + "; i <= " + hi + "; i++) {\n    sum += a[i];\n}\nSystem.out.println(sum);";
        return Spec.output("Range sums", "What does this program print?", program("", statements), String.valueOf(s),
                "Add the elements from index " + lo + " to " + hi + " (both included): " + s
                        + ". (With a prefix-sum array this is prefix[" + (hi + 1) + "] - prefix[" + lo + "].)", true);
    }

    static Spec windowMax(Random r, int tier) {
        int n = ri(r, 7, 11);
        int k = ri(r, 2, 4);
        int[] a = randomArray(r, n, -4, 15);
        int best = Integer.MIN_VALUE;
        for (int i = 0; i + k <= n; i++) {
            int s = 0;
            for (int j = i; j < i + k; j++) s += a[j];
            best = Math.max(best, s);
        }
        String statements = "int[] a = " + arrayLiteral(a) + ";\nint k = " + k + ";\nint best = Integer.MIN_VALUE;\nfor (int i = 0; i + k <= a.length; i++) {\n    int s = 0;\n    for (int j = i; j < i + k; j++) {\n        s += a[j];\n    }\n    best = Math.max(best, s);\n}\nSystem.out.println(best);";
        return Spec.output("Best window of k", "What does this program print?", program("", statements), String.valueOf(best),
                "It looks at every block of " + k + " neighbouring elements and keeps the largest sum: " + best + ".", true);
    }

    static Spec kadane(Random r, int tier) {
        int n = ri(r, 6, 10);
        int[] a = randomArray(r, n, -9, 9);
        int cur = a[0], best = a[0];
        for (int i = 1; i < n; i++) { cur = Math.max(a[i], cur + a[i]); best = Math.max(best, cur); }
        return Spec.number("Maximum subarray sum", "What is the largest sum of a non-empty block of neighbouring elements in "
                + Arrays.toString(a) + "?", "", best,
                "Kadane's algorithm: at each element either extend the running block or restart at that element, and remember "
                        + "the best total. The maximum is " + best + ".", false);
    }

    static Spec pairCount(Random r, int tier) {
        int n = ri(r, 5, 9);
        int[] a = randomArray(r, n, 1, 9);
        int target = ri(r, 6, 14);
        int c = 0;
        for (int i = 0; i < n; i++) for (int j = i + 1; j < n; j++) if (a[i] + a[j] == target) c++;
        String statements = "int[] a = " + arrayLiteral(a) + ";\nint target = " + target + ";\nint count = 0;\nfor (int i = 0; i < a.length; i++) {\n    for (int j = i + 1; j < a.length; j++) {\n        if (a[i] + a[j] == target) {\n            count++;\n        }\n    }\n}\nSystem.out.println(count);";
        return Spec.output("Pairs with a target sum", "What does this program print?", program("", statements), String.valueOf(c),
                "Count the pairs of positions i < j whose values add up to " + target + ": " + c + ".", true);
    }

    static Spec matrix(Random r, int tier) {
        int n = ri(r, 3, 4);
        int[][] m = new int[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) m[i][j] = ri(r, 1, 9);
        StringBuilder init = new StringBuilder("int[][] m = {\n");
        for (int i = 0; i < n; i++) init.append("    ").append(arrayLiteral(m[i])).append(i < n - 1 ? ",\n" : "\n");
        init.append("};\n");
        int p = ri(r, 0, tier == 0 ? 1 : 3);
        String last;
        long answer;
        switch (p) {
            case 0 -> { int s = 0; for (int i = 0; i < n; i++) s += m[i][i]; answer = s; last = "int sum = 0;\nfor (int i = 0; i < m.length; i++) {\n    sum += m[i][i];\n}\nSystem.out.println(sum);"; }
            case 1 -> { int i = ri(r, 0, n - 1), j = ri(r, 0, n - 1); answer = m[j][i]; last = "System.out.println(m[" + j + "][" + i + "]);"; }
            case 2 -> { int s = 0; for (int i = 0; i < n; i++) s += m[i][n - 1 - i]; answer = s; last = "int sum = 0;\nfor (int i = 0; i < m.length; i++) {\n    sum += m[i][m.length - 1 - i];\n}\nSystem.out.println(sum);"; }
            default -> { int row = ri(r, 0, n - 1); int s = 0; for (int j = 0; j < n; j++) s += m[row][j] * (j + 1); answer = s; last = "int total = 0;\nfor (int j = 0; j < m[" + row + "].length; j++) {\n    total += m[" + row + "][j] * (j + 1);\n}\nSystem.out.println(total);"; }
        }
        return Spec.output("Matrix reading", "What does this program print?", program("", init + last), String.valueOf(answer),
                "m[row][column], both counted from 0. Follow the loop over the matrix and add as the code says: " + answer + ".", true);
    }

    // ---------------------------------------------------------------- trees and graphs

    private static final String BST_CLASS = "static class Node {\n    int val;\n    Node left, right;\n    Node(int val) {\n        this.val = val;\n    }\n}\n\n"
            + "static Node insert(Node root, int v) {\n    if (root == null) {\n        return new Node(v);\n    }\n    if (v < root.val) {\n        root.left = insert(root.left, v);\n    } else {\n        root.right = insert(root.right, v);\n    }\n    return root;\n}";

    private static final class Node {
        int val;
        Node left, right;
        Node(int v) { val = v; }
    }

    private static Node insert(Node root, int v) {
        if (root == null) return new Node(v);
        if (v < root.val) root.left = insert(root.left, v); else root.right = insert(root.right, v);
        return root;
    }

    private static void inorder(Node n, List<Integer> out) { if (n != null) { inorder(n.left, out); out.add(n.val); inorder(n.right, out); } }
    private static void preorder(Node n, List<Integer> out) { if (n != null) { out.add(n.val); preorder(n.left, out); preorder(n.right, out); } }
    private static void postorder(Node n, List<Integer> out) { if (n != null) { postorder(n.left, out); postorder(n.right, out); out.add(n.val); } }
    private static int height(Node n) { return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right)); }

    static Spec bstOrder(Random r, int tier) {
        int n = ri(r, 5, tier == 0 ? 6 : 8);
        List<Integer> pool = new ArrayList<>();
        for (int i = 1; i <= 30; i++) pool.add(i);
        Collections.shuffle(pool, r);
        int[] values = new int[n];
        Node root = null;
        for (int i = 0; i < n; i++) { values[i] = pool.get(i); root = insert(root, values[i]); }
        int p = ri(r, 0, tier == 0 ? 1 : 3);
        List<Integer> out = new ArrayList<>();
        String call;
        String answer;
        switch (p) {
            case 0 -> { inorder(root, out); call = "inorder"; answer = out.toString(); }
            case 1 -> { preorder(root, out); call = "preorder"; answer = out.toString(); }
            case 2 -> { postorder(root, out); call = "postorder"; answer = out.toString(); }
            default -> { call = "height"; answer = String.valueOf(height(root)); }
        }
        String traversal = switch (call) {
            case "inorder" -> "static void inorder(Node n, List<Integer> out) {\n    if (n != null) {\n        inorder(n.left, out);\n        out.add(n.val);\n        inorder(n.right, out);\n    }\n}";
            case "preorder" -> "static void preorder(Node n, List<Integer> out) {\n    if (n != null) {\n        out.add(n.val);\n        preorder(n.left, out);\n        preorder(n.right, out);\n    }\n}";
            case "postorder" -> "static void postorder(Node n, List<Integer> out) {\n    if (n != null) {\n        postorder(n.left, out);\n        postorder(n.right, out);\n        out.add(n.val);\n    }\n}";
            default -> "static int height(Node n) {\n    return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right));\n}";
        };
        String statements = "int[] values = " + arrayLiteral(values) + ";\nNode root = null;\nfor (int v : values) {\n    root = insert(root, v);\n}\n"
                + (call.equals("height") ? "System.out.println(height(root));" : "List<Integer> out = new ArrayList<>();\n" + call + "(root, out);\nSystem.out.println(out);");
        String code = programWith("import java.util.ArrayList;\nimport java.util.List;", BST_CLASS + "\n\n" + traversal, statements);
        return Spec.output("Tree traversals", "What does this program print?", code, answer,
                "Insert the numbers one by one into the binary search tree (smaller goes left, otherwise right), then apply the "
                        + call + (call.equals("height") ? " (number of levels)" : " traversal") + ". Result: " + answer + ".", true);
    }

    static Spec graphBfs(Random r, int tier) {
        int n = ri(r, 5, tier == 0 ? 5 : 7);
        List<int[]> edges = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int i = 1; i < n; i++) {                       // a spanning tree keeps the graph connected
            int parent = ri(r, 0, i - 1);
            edges.add(new int[] {parent, i});
            seen.add(parent + "-" + i);
        }
        int extra = ri(r, 0, tier == 0 ? 0 : 2);
        for (int k = 0; k < extra; k++) {
            int a = ri(r, 0, n - 1), b = ri(r, 0, n - 1);
            if (a != b && !seen.contains(Math.min(a, b) + "-" + Math.max(a, b)) && !seen.contains(a + "-" + b) && !seen.contains(b + "-" + a)) {
                edges.add(new int[] {a, b});
                seen.add(a + "-" + b);
            }
        }
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) { adj.get(e[0]).add(e[1]); adj.get(e[1]).add(e[0]); }
        boolean[] visited = new boolean[n];
        Deque<Integer> q = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();
        q.add(0);
        visited[0] = true;
        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (int v : adj.get(u)) if (!visited[v]) { visited[v] = true; q.add(v); }
        }
        StringBuilder e = new StringBuilder("int[][] edges = {");
        for (int i = 0; i < edges.size(); i++) e.append(i > 0 ? ", " : "").append("{").append(edges.get(i)[0]).append(", ").append(edges.get(i)[1]).append("}");
        e.append("};\n");
        String statements = "int n = " + n + ";\n" + e + "List<List<Integer>> adj = new ArrayList<>();\nfor (int i = 0; i < n; i++) {\n    adj.add(new ArrayList<>());\n}\nfor (int[] edge : edges) {\n    adj.get(edge[0]).add(edge[1]);\n    adj.get(edge[1]).add(edge[0]);\n}\nboolean[] visited = new boolean[n];\nDeque<Integer> queue = new ArrayDeque<>();\nList<Integer> order = new ArrayList<>();\nqueue.add(0);\nvisited[0] = true;\nwhile (!queue.isEmpty()) {\n    int u = queue.poll();\n    order.add(u);\n    for (int v : adj.get(u)) {\n        if (!visited[v]) {\n            visited[v] = true;\n            queue.add(v);\n        }\n    }\n}\nSystem.out.println(order);";
        return Spec.output("BFS visiting order", "What does this program print?",
                program("import java.util.ArrayDeque;\nimport java.util.ArrayList;\nimport java.util.Deque;\nimport java.util.List;", statements), order.toString(),
                "Neighbours are stored in the order the edges are listed. Breadth-first search visits node 0, then all its neighbours, "
                        + "then theirs, and so on: " + order + ".", true);
    }

    static Spec components(Random r, int tier) {
        int n = ri(r, 6, 10);
        int m = ri(r, 2, n - 2);
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        StringBuilder edges = new StringBuilder();
        for (int k = 0; k < m; k++) {
            int a = ri(r, 0, n - 1), b = ri(r, 0, n - 1);
            edges.append(k > 0 ? ", " : "").append("(").append(a).append(", ").append(b).append(")");
            int ra = find(parent, a), rb = find(parent, b);
            if (ra != rb) parent[ra] = rb;
        }
        Set<Integer> roots = new HashSet<>();
        for (int i = 0; i < n; i++) roots.add(find(parent, i));
        return Spec.number("Connected components", "A network has " + n + " computers numbered 0 to " + (n - 1) + " and these cables: "
                + edges + ". How many separate groups of connected computers are there (a computer with no cable is a group on its own)?", "",
                roots.size(), "Start with " + n + " groups and merge two groups every time a cable joins computers that are in different "
                        + "groups. At the end " + roots.size() + " groups remain.", false);
    }

    private static int find(int[] p, int x) {
        while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; }
        return x;
    }

    // ---------------------------------------------------------------- dynamic programming

    static Spec coins(Random r, int tier) {
        int[][] sets = {{1, 2, 5}, {1, 3, 4}, {2, 5, 10}, {1, 5, 6}, {3, 7}, {4, 9}, {1, 5, 10, 25}};
        int[] coins = pick(r, sets);
        int amount = ri(r, 6, tier == 0 ? 12 : tier == 1 ? 25 : 40);
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int a = 1; a <= amount; a++) for (int c : coins) if (c <= a && dp[a - c] != Integer.MAX_VALUE) dp[a] = Math.min(dp[a], dp[a - c] + 1);
        int ans = dp[amount] == Integer.MAX_VALUE ? -1 : dp[amount];
        return Spec.number("Fewest coins", "You have an unlimited supply of coins worth " + Arrays.toString(coins) + ". What is the fewest number of coins "
                + "that add up to exactly " + amount + "? (Answer -1 if it is impossible.)", "", ans,
                "Dynamic programming: best[a] = 1 + the smallest best[a - coin] over all coins. Greedy (always the biggest coin) "
                        + "can fail. The fewest coins for " + amount + " is " + ans + ".", false);
    }

    static Spec lis(Random r, int tier) {
        int n = ri(r, 6, tier == 0 ? 7 : 10);
        int[] a = randomArray(r, n, 1, 20);
        int[] dp = new int[n];
        int best = 0;
        for (int i = 0; i < n; i++) { dp[i] = 1; for (int j = 0; j < i; j++) if (a[j] < a[i]) dp[i] = Math.max(dp[i], dp[j] + 1); best = Math.max(best, dp[i]); }
        return Spec.number("Longest increasing subsequence", "What is the length of the longest STRICTLY increasing subsequence of "
                + Arrays.toString(a) + "? (A subsequence keeps the order but may skip elements.)", "", best,
                "For each position, the best subsequence ending there is 1 + the best among earlier smaller elements. The longest has length " + best + ".", false);
    }

    static Spec lcs(Random r, int tier) {
        String alphabet = "ABCD";
        int n = ri(r, 4, tier == 0 ? 5 : 8), m = ri(r, 4, tier == 0 ? 5 : 8);
        StringBuilder a = new StringBuilder(), b = new StringBuilder();
        for (int i = 0; i < n; i++) a.append(alphabet.charAt(r.nextInt(4)));
        for (int i = 0; i < m; i++) b.append(alphabet.charAt(r.nextInt(4)));
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) for (int j = 1; j <= m; j++)
            dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1) ? dp[i - 1][j - 1] + 1 : Math.max(dp[i - 1][j], dp[i][j - 1]);
        return Spec.number("Longest common subsequence", "What is the length of the longest common subsequence of \"" + a + "\" and \"" + b
                + "\"? (Letters must appear in the same order in both strings but need not be next to each other.)", "", dp[n][m],
                "Fill a table: if the last letters match add 1 to the diagonal, otherwise take the better of dropping a letter from either string. Answer: " + dp[n][m] + ".", false);
    }

    static Spec knapsack(Random r, int tier) {
        int n = ri(r, 3, tier == 0 ? 4 : 5);
        int[] w = randomArray(r, n, 1, 6), v = randomArray(r, n, 2, 12);
        int cap = ri(r, 5, 12);
        int[] dp = new int[cap + 1];
        for (int i = 0; i < n; i++) for (int c = cap; c >= w[i]; c--) dp[c] = Math.max(dp[c], dp[c - w[i]] + v[i]);
        StringBuilder items = new StringBuilder();
        for (int i = 0; i < n; i++) items.append(i > 0 ? "; " : "").append("weight ").append(w[i]).append(" worth ").append(v[i]);
        return Spec.number("0/1 knapsack", "A bag holds a total weight of " + cap + ". Items (each can be used at most once): " + items
                + ". What is the largest total value you can carry?", "", dp[cap],
                "Classic 0/1 knapsack: best[c] = max(best[c], best[c - weight] + value), processing capacities from high to low so each item "
                        + "is used once. The best value is " + dp[cap] + ".", false);
    }

    static Spec stairs(Random r, int tier) {
        int n = ri(r, 4, tier == 0 ? 8 : 14);
        int[] steps = pick(r, new int[] {1, 2}, new int[] {1, 2, 3}, new int[] {1, 3}, new int[] {2, 3});
        long[] ways = new long[n + 1];
        ways[0] = 1;
        for (int i = 1; i <= n; i++) for (int s : steps) if (i >= s) ways[i] += ways[i - s];
        return Spec.number("Counting ways", "A staircase has " + n + " steps. You may climb " + Arrays.toString(steps).replace("[", "").replace("]", "")
                + " step(s) at a time. In how many different ways can you reach the top (the order of steps matters)?", "", ways[n],
                "ways[i] = the sum of ways[i - s] for every allowed step size s. Starting from ways[0] = 1 this gives " + ways[n] + " for " + n + " steps.", false);
    }

    // ---------------------------------------------------------------- recursion

    private static int calls;

    private static int fibCalls(int n) {
        calls++;
        return n < 2 ? n : fibCalls(n - 1) + fibCalls(n - 2);
    }

    static Spec callCount(Random r, int tier) {
        int n = ri(r, 3, tier == 0 ? 6 : 9);
        synchronized (AlgoFamilies.class) {
            calls = 0;
            fibCalls(n);
            int count = calls;
            String members = "static int count = 0;\n\nstatic int fib(int n) {\n    count++;\n    if (n < 2) {\n        return n;\n    }\n    return fib(n - 1) + fib(n - 2);\n}";
            String code = programWith("", members, "fib(" + n + ");\nSystem.out.println(count);");
            return Spec.output("Counting recursive calls", "What does this program print?", code, String.valueOf(count),
                    "Every call to fib counts once, including the calls for the base cases. calls(n) = 1 + calls(n-1) + calls(n-2), which gives "
                            + count + " calls for n = " + n + ".", true);
        }
    }

    static Spec josephus(Random r, int tier) {
        int n = ri(r, 5, tier == 0 ? 8 : 14), k = ri(r, 2, 4);
        List<Integer> people = new ArrayList<>();
        for (int i = 1; i <= n; i++) people.add(i);
        int idx = 0;
        while (people.size() > 1) { idx = (idx + k - 1) % people.size(); people.remove(idx); }
        return Spec.number("Josephus circle", n + " people numbered 1 to " + n + " stand in a circle. Starting at person 1 and counting "
                + k + " people (person 1 is the first you count), every " + ordinal(k) + " person is removed, then counting continues from the next person. "
                + "Which numbered person is the last one standing?", "", people.get(0),
                "Simulate it: keep a list, move the pointer k - 1 steps (wrapping around) and remove that person until one is left. The survivor is " + people.get(0) + ".", false);
    }

    private static String ordinal(int k) {
        return switch (k) { case 2 -> "2nd"; case 3 -> "3rd"; default -> k + "th"; };
    }

    // ---------------------------------------------------------------- math and bits

    static Spec gcdLcm(Random r, int tier) {
        int a = ri(r, 4, tier == 0 ? 40 : 150), b = ri(r, 4, tier == 0 ? 40 : 150);
        int x = a, y = b;
        while (y != 0) { int t = x % y; x = y; y = t; }
        boolean lcm = r.nextBoolean();
        long ans = lcm ? (long) a / x * b : x;
        return Spec.number(lcm ? "Least common multiple" : "Greatest common divisor", "What is the " + (lcm ? "least common multiple (LCM)" : "greatest common divisor (GCD)")
                + " of " + a + " and " + b + "?", "", ans,
                "GCD(" + a + ", " + b + ") = " + x + " (Euclid: keep replacing (a, b) by (b, a % b)). LCM = a / GCD * b = " + ((long) a / x * b) + ".", false);
    }

    static Spec primes(Random r, int tier) {
        int p = ri(r, 0, 3);
        switch (p) {
            case 0 -> {
                int n = ri(r, 20, tier == 0 ? 60 : 200);
                int c = 0;
                for (int i = 2; i <= n; i++) { boolean prime = true; for (int d = 2; d * d <= i; d++) if (i % d == 0) { prime = false; break; } if (prime) c++; }
                return Spec.number("Primes and divisors", "How many prime numbers are there from 1 to " + n + " (inclusive)?", "", c,
                        "Check each number up to " + n + " for divisors up to its square root. There are " + c + " primes.", false);
            }
            case 1 -> {
                int n = ri(r, 10, tier == 0 ? 99 : 999);
                boolean prime = n > 1;
                for (int d = 2; d * d <= n; d++) if (n % d == 0) { prime = false; break; }
                String ans = String.valueOf(prime);
                return Spec.choice("Primes and divisors", "Is " + n + " a prime number?", "", ans, List.of("true", "false"),
                        n + (prime ? " has no divisor between 2 and its square root, so it is prime." : " is divisible by a smaller number, so it is not prime."), false);
            }
            case 2 -> {
                int n = ri(r, 12, tier == 0 ? 100 : 5000);
                int c = 0;
                for (int d = 1; d <= n; d++) if (n % d == 0) c++;
                return Spec.number("Primes and divisors", "How many positive divisors does " + n + " have (including 1 and " + n + ")?", "", c,
                        "Count the numbers from 1 to " + n + " that divide it evenly: " + c + ".", false);
            }
            default -> {
                int n = ri(r, 20, tier == 0 ? 200 : 9999);
                int f = n;
                for (int d = 2; d * d <= n; d++) if (n % d == 0) { f = d; break; }
                return Spec.number("Primes and divisors", "What is the smallest prime factor of " + n + "?", "", f,
                        "Try 2, 3, 5, 7, ... until one divides " + n + ". The first one that does is " + f + ".", false);
            }
        }
    }

    static Spec bitsCount(Random r, int tier) {
        int n = ri(r, 5, tier == 0 ? 63 : 4095);
        int p = ri(r, 0, 2);
        String bin = Integer.toBinaryString(n);
        switch (p) {
            case 0 -> { return Spec.number("Counting bits", "How many 1 bits are in the binary form of " + n + " (" + bin + ")?", "", Integer.bitCount(n),
                    "Count the 1s in " + bin + ": " + Integer.bitCount(n) + "."); }
            case 1 -> { return Spec.number("Counting bits", "How many trailing zero bits does " + n + " have in binary (" + bin + ")?", "", Integer.numberOfTrailingZeros(n),
                    "Count the zeros at the right end of " + bin + ": " + Integer.numberOfTrailingZeros(n) + "."); }
            default -> { return Spec.number("Counting bits", "What is the largest power of two that is less than or equal to " + n + "?", "", Integer.highestOneBit(n),
                    "The highest set bit of " + bin + " has the value " + Integer.highestOneBit(n) + "."); }
        }
    }

    static Spec modArith(Random r, int tier) {
        int m = pick(r, 7, 9, 10, 11, 13, 17);
        if (r.nextBoolean()) {
            int a = ri(r, 10, tier == 0 ? 99 : 999), b = ri(r, 10, tier == 0 ? 99 : 999);
            return Spec.number("Modular arithmetic", "What is (" + a + " * " + b + ") mod " + m + "?", "", (long) a * b % m,
                    "Multiply first, then take the remainder: " + a + " * " + b + " = " + ((long) a * b) + " and its remainder after dividing by " + m + " is " + ((long) a * b % m) + ".", false);
        }
        int base = ri(r, 2, 9), exp = ri(r, 2, tier == 0 ? 6 : 12);
        long v = 1;
        for (int i = 0; i < exp; i++) v = v * base % m;
        return Spec.number("Modular arithmetic", "What is " + base + "^" + exp + " mod " + m + "?", "", v,
                "Multiply " + base + " by itself " + exp + " times, taking the remainder mod " + m + " after each step so the numbers stay small: " + v + ".", false);
    }

    static Spec combinatorics(Random r, int tier) {
        int n = ri(r, 4, 12), k = ri(r, 2, Math.min(5, n - 1));
        int p = ri(r, 0, 3);
        switch (p) {
            case 0 -> { long c = 1; for (int i = 1; i <= k; i++) c = c * (n - k + i) / i;
                return Spec.number("Counting and combinations", "In how many ways can you choose " + k + " people from a group of " + n + " (the order does not matter)?", "", c,
                        "This is C(" + n + ", " + k + ") = " + n + "! / (" + k + "! * " + (n - k) + "!) = " + c + ".", false); }
            case 1 -> { long c = 1; for (int i = 0; i < k; i++) c *= (n - i);
                return Spec.number("Counting and combinations", "In how many ways can you arrange " + k + " people in a row chosen from " + n + " people (the order matters)?", "", c,
                        "Multiply the choices for each seat: " + n + " * " + (n - 1) + " * ... for " + k + " seats = " + c + ".", false); }
            case 2 -> { return Spec.number("Counting and combinations", "How many different subsets (including the empty set) does a set of " + n + " distinct elements have?", "", 1L << n,
                    "Each element is either in or out, so there are 2^" + n + " = " + (1L << n) + " subsets."); }
            default -> { return Spec.number("Counting and combinations", n + " people each shake hands exactly once with every other person. How many handshakes happen?", "", (long) n * (n - 1) / 2,
                    "Every pair shakes once: C(" + n + ", 2) = " + n + " * " + (n - 1) + " / 2 = " + ((long) n * (n - 1) / 2) + "."); }
        }
    }

    static Spec radix(Random r, int tier) {
        int n = ri(r, 5, tier == 0 ? 63 : tier == 1 ? 255 : 4095);
        int p = ri(r, 0, 3);
        switch (p) {
            case 0 -> { return Spec.output("Binary, octal and hex", "Write " + n + " in binary (digits only, no prefix).", "", Integer.toBinaryString(n),
                    "Divide by 2 repeatedly and read the remainders from the last to the first: " + Integer.toBinaryString(n) + ".", false); }
            case 1 -> { String bin = Integer.toBinaryString(n); return Spec.number("Binary, octal and hex", "What is the decimal value of the binary number " + bin + "?", "", n,
                    "Add the powers of two for every 1 bit: " + n + "."); }
            case 2 -> { return Spec.output("Binary, octal and hex", "Write " + n + " in hexadecimal (lower case, no prefix).", "", Integer.toHexString(n),
                    "Group the binary form " + Integer.toBinaryString(n) + " in fours (from the right) and convert each group: " + Integer.toHexString(n) + ".", false); }
            default -> { String hex = Integer.toHexString(n); return Spec.number("Binary, octal and hex", "What is the decimal value of the hexadecimal number " + hex + "?", "", n,
                    "Each hex digit is worth 16 times the next one: " + n + "."); }
        }
    }

    static Spec hashing(Random r, int tier) {
        String key = pick(r, "cat", "dog", "bird", "fish", "lion", "bear", "wolf", "frog", "duck", "swan", "mouse", "horse");
        int buckets = pick(r, 5, 7, 8, 10, 11, 16);
        int h = 0;
        for (char c : key.toCharArray()) h += c;
        String statements = "String key = \"" + key + "\";\nint hash = 0;\nfor (char c : key.toCharArray()) {\n    hash += c;\n}\nSystem.out.println(hash % " + buckets + ");";
        return Spec.output("Hash buckets", "What does this program print?", program("", statements), String.valueOf(h % buckets),
                "The hash is the sum of the character codes (" + h + ") and the bucket is that number mod " + buckets + " = " + (h % buckets) + ".", true);
    }
}
