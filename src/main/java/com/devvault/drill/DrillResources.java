package com.devvault.drill;

import java.util.List;
import java.util.Map;

/** Real study links for each drill group (shown under every drill as "Learn this"). */
public final class DrillResources {

    public record Link(String title, String url) {
    }

    private static final Map<String, List<Link>> BY_GROUP = Map.ofEntries(
            Map.entry("complexity", List.of(new Link("Big-O cheat sheet", "https://www.bigocheatsheet.com/"),
                    new Link("Big-O notation (Khan Academy)", "https://www.khanacademy.org/computing/computer-science/algorithms/asymptotic-notation/a/big-o-notation"))),
            Map.entry("algorithms", List.of(new Link("Binary search (Khan Academy)", "https://www.khanacademy.org/computing/computer-science/algorithms/binary-search/a/binary-search"),
                    new Link("Sorting algorithms visualised", "https://visualgo.net/en/sorting"))),
            Map.entry("arrays", List.of(new Link("Arrays and strings (LeetCode Explore)", "https://leetcode.com/explore/learn/card/array-and-string/"),
                    new Link("Prefix sums explained (USACO Guide)", "https://usaco.guide/silver/prefix-sums"))),
            Map.entry("trees", List.of(new Link("Binary search trees (VisuAlgo)", "https://visualgo.net/en/bst"),
                    new Link("Tree traversals (GeeksforGeeks)", "https://www.geeksforgeeks.org/tree-traversals-inorder-preorder-and-postorder/"))),
            Map.entry("graphs", List.of(new Link("Graph data structures (VisuAlgo)", "https://visualgo.net/en/graphds"),
                    new Link("BFS and DFS (USACO Guide)", "https://usaco.guide/silver/graph-traversal"))),
            Map.entry("dp", List.of(new Link("Intro to dynamic programming (USACO Guide)", "https://usaco.guide/gold/intro-dp"),
                    new Link("Dynamic programming patterns (LeetCode)", "https://leetcode.com/discuss/study-guide/458695/Dynamic-Programming-Patterns"))),
            Map.entry("recursion", List.of(new Link("Recursion (Khan Academy)", "https://www.khanacademy.org/computing/computer-science/algorithms/recursive-algorithms/a/recursion"))),
            Map.entry("math", List.of(new Link("Euclidean algorithm (CP-Algorithms)", "https://cp-algorithms.com/algebra/euclid-algorithm.html"),
                    new Link("Modular arithmetic (Khan Academy)", "https://www.khanacademy.org/computing/computer-science/cryptography/modarithmetic/a/what-is-modular-arithmetic"))),
            Map.entry("bits", List.of(new Link("Bit twiddling hacks (Stanford)", "https://graphics.stanford.edu/~seander/bithacks.html"))),
            Map.entry("collections", List.of(new Link("Java Collections tutorial (Oracle)", "https://docs.oracle.com/javase/tutorial/collections/"),
                    new Link("Hash tables (VisuAlgo)", "https://visualgo.net/en/hashtable"))),
            Map.entry("java", List.of(new Link("The Java Tutorials (Oracle)", "https://docs.oracle.com/javase/tutorial/"),
                    new Link("Java language specification", "https://docs.oracle.com/javase/specs/jls/se21/html/index.html"))),
            Map.entry("strings", List.of(new Link("Java String API", "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html"),
                    new Link("Interactive regular expressions tutorial (RegexOne)", "https://regexone.com/"))),
            Map.entry("systems", List.of(new Link("System design primer", "https://github.com/donnemartin/system-design-primer"),
                    new Link("Caching best practices (AWS)", "https://aws.amazon.com/caching/best-practices/"))),
            Map.entry("web", List.of(new Link("HTTP status codes (MDN)", "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status"),
                    new Link("REST API design guide (Microsoft)", "https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design"))),
            Map.entry("sql", List.of(new Link("Interactive SQL lessons (SQLBolt)", "https://sqlbolt.com/"),
                    new Link("SQL tutorial (W3Schools)", "https://www.w3schools.com/sql/"))),
            Map.entry("dates", List.of(new Link("Java Date and Time (Oracle)", "https://docs.oracle.com/javase/tutorial/datetime/"))));

    private DrillResources() {
    }

    public static List<Link> forGroup(String group) {
        return BY_GROUP.getOrDefault(group, BY_GROUP.get("java"));
    }
}
