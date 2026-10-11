package com.devvault.config.seed;

import com.devvault.entity.LearningResource;
import com.devvault.entity.ResourceType;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.entity.ResourceType.*;

/** Study links that back the Practice Drills: algorithms, maths, SQL, systems, web and practice sites. */
public final class ResourceSeedDrills {

    private ResourceSeedDrills() {
    }

    private static void r(List<LearningResource> out, String title, String url, ResourceType type, String topic,
                          String description, String tags) {
        LearningResource x = new LearningResource();
        x.setTitle(title);
        x.setUrl(url);
        x.setResourceType(type);
        x.setTopic(topic);
        x.setDescription(description);
        x.setTags(tags);
        out.add(x);
    }

    public static List<LearningResource> all() {
        List<LearningResource> l = new ArrayList<>();

        // ---- Algorithms & data structures ----
        r(l, "VisuAlgo: Binary Search Tree", "https://visualgo.net/en/bst", OTHER, "DSA", "Step through BST insert, delete and traversals.", "bst,trees,visual");
        r(l, "VisuAlgo: Graph Data Structures", "https://visualgo.net/en/graphds", OTHER, "DSA", "See adjacency lists and matrices come to life.", "graphs,visual");
        r(l, "VisuAlgo: Hash Table", "https://visualgo.net/en/hashtable", OTHER, "DSA", "Collisions, buckets and probing visualised.", "hashing,visual");
        r(l, "USACO Guide: Prefix Sums", "https://usaco.guide/silver/prefix-sums", TUTORIAL, "DSA", "Range sum queries explained with practice problems.", "prefix-sum,arrays");
        r(l, "USACO Guide: Intro to DP", "https://usaco.guide/gold/intro-dp", TUTORIAL, "DSA", "Dynamic programming from first principles.", "dp,drills");
        r(l, "USACO Guide: Graph Traversal", "https://usaco.guide/silver/graph-traversal", TUTORIAL, "DSA", "BFS, DFS and connected components.", "graphs,bfs,dfs");
        r(l, "CP-Algorithms: Euclidean Algorithm", "https://cp-algorithms.com/algebra/euclid-algorithm.html", ARTICLE, "DSA", "GCD and LCM with proofs.", "gcd,lcm,math");
        r(l, "CP-Algorithms: Binary Exponentiation", "https://cp-algorithms.com/algebra/binary-exp.html", ARTICLE, "DSA", "Fast powers and modular exponentiation.", "modular,power,math");
        r(l, "CP-Algorithms: Sieve of Eratosthenes", "https://cp-algorithms.com/algebra/sieve-of-eratosthenes.html", ARTICLE, "DSA", "Find all primes up to n quickly.", "primes,sieve");
        r(l, "CP-Algorithms: Disjoint Set Union", "https://cp-algorithms.com/data_structures/disjoint_set_union.html", ARTICLE, "DSA", "Union-find for connected components.", "dsu,graphs");
        r(l, "CP-Algorithms: Longest Increasing Subsequence", "https://cp-algorithms.com/sequences/longest_increasing_subsequence.html", ARTICLE, "DSA", "LIS in O(n^2) and O(n log n).", "dp,lis");
        r(l, "Stanford Bit Twiddling Hacks", "https://graphics.stanford.edu/~seander/bithacks.html", ARTICLE, "DSA", "Classic bit manipulation tricks.", "bits,tricks");
        r(l, "Kadane's Algorithm (GeeksforGeeks)", "https://www.geeksforgeeks.org/largest-sum-contiguous-subarray/", ARTICLE, "DSA", "Maximum subarray sum in linear time.", "kadane,arrays");
        r(l, "Coin Change Problem (GeeksforGeeks)", "https://www.geeksforgeeks.org/coin-change-dp-7/", ARTICLE, "DSA", "Counting ways and fewest coins.", "dp,coins");
        r(l, "0/1 Knapsack (GeeksforGeeks)", "https://www.geeksforgeeks.org/0-1-knapsack-problem-dp-10/", ARTICLE, "DSA", "The classic knapsack DP table.", "dp,knapsack");
        r(l, "Longest Common Subsequence (GeeksforGeeks)", "https://www.geeksforgeeks.org/longest-common-subsequence-dp-4/", ARTICLE, "DSA", "LCS table walk-through.", "dp,lcs,strings");
        r(l, "Josephus Problem (Wikipedia)", "https://en.wikipedia.org/wiki/Josephus_problem", ARTICLE, "DSA", "History and formula for the circle elimination puzzle.", "josephus,recursion");
        r(l, "Tree Traversal (Wikipedia)", "https://en.wikipedia.org/wiki/Tree_traversal", ARTICLE, "DSA", "In-order, pre-order, post-order and level-order.", "trees,traversal");
        r(l, "Breadth-First Search (Wikipedia)", "https://en.wikipedia.org/wiki/Breadth-first_search", ARTICLE, "DSA", "BFS definition, pseudocode and uses.", "bfs,graphs");
        r(l, "Page Replacement Algorithms (Wikipedia)", "https://en.wikipedia.org/wiki/Page_replacement_algorithm", ARTICLE, "Systems", "FIFO, LRU and optimal replacement.", "lru,fifo,cache");
        r(l, "Round-robin Scheduling (Wikipedia)", "https://en.wikipedia.org/wiki/Round-robin_scheduling", ARTICLE, "Systems", "How time quanta share the CPU.", "scheduling,os");
        r(l, "Rate Limiting Strategies (Cloudflare)", "https://www.cloudflare.com/learning/bots/what-is-rate-limiting/", ARTICLE, "Systems", "What rate limiting is and why APIs need it.", "rate-limit,api");
        r(l, "Cache Replacement Policies (Wikipedia)", "https://en.wikipedia.org/wiki/Cache_replacement_policies", ARTICLE, "Systems", "LRU, LFU, FIFO and friends.", "cache,lru");

        // ---- Java output prediction ----
        r(l, "Java Puzzlers", "https://www.oreilly.com/library/view/java-puzzlers-traps/032133678X/", OTHER, "Java", "Famous book of tricky Java output questions.", "java,puzzlers,drills");
        r(l, "Java Integer Cache Explained (Baeldung)", "https://www.baeldung.com/java-integer-caching", ARTICLE, "Java", "Why == works for small Integer values only.", "java,integer,cache");
        r(l, "Java Operators (Oracle Tutorial)", "https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html", TUTORIAL, "Java", "Precedence, increments and short-circuit logic.", "java,operators");
        r(l, "Java switch Statements (Oracle Tutorial)", "https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html", TUTORIAL, "Java", "Fall-through and break explained.", "java,switch");
        r(l, "Java Primitive Data Types (Oracle)", "https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html", TUTORIAL, "Java", "Ranges, overflow and defaults of int, long, char.", "java,overflow");
        r(l, "StringBuilder API (Oracle)", "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StringBuilder.html", DOCUMENTATION, "Java", "Insert, delete and reverse operations.", "java,strings");
        r(l, "Java Math API (Oracle)", "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Math.html", DOCUMENTATION, "Java", "floor, round, abs and friends with exact rules.", "java,math");
        r(l, "Arrays API (Oracle)", "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Arrays.html", DOCUMENTATION, "Java", "toString, sort, fill, binarySearch.", "java,arrays");
        r(l, "List Interface (Oracle)", "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html", DOCUMENTATION, "Java", "remove(int) vs remove(Object) and more.", "java,collections");
        r(l, "Deque Interface (Oracle)", "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Deque.html", DOCUMENTATION, "Java", "Stack and queue operations in one interface.", "java,stack,queue");

        // ---- SQL ----
        r(l, "Select Star SQL", "https://selectstarsql.com/", TUTORIAL, "SQL", "An interactive book teaching SQL with real data.", "sql,book");
        r(l, "SQL Murder Mystery", "https://mystery.knightlab.com/", OTHER, "SQL", "Solve a crime by writing SQL queries.", "sql,game");

        // ---- Web, HTTP and dates ----
        r(l, "HTTP Status Codes (MDN)", "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status", DOCUMENTATION, "Web", "Every status code with usage notes.", "http,status,drills");
        r(l, "HTTP Methods (MDN)", "https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods", DOCUMENTATION, "Web", "GET, POST, PUT, PATCH and DELETE semantics.", "http,rest");
        r(l, "HTTP Caching (MDN)", "https://developer.mozilla.org/en-US/docs/Web/HTTP/Caching", DOCUMENTATION, "Web", "Cache-Control, ETag and 304 responses.", "http,cache");
        r(l, "httpstatuses.io", "https://httpstatuses.io/", OTHER, "Web", "Plain-English guide to HTTP status codes.", "http,status");
        r(l, "Pagination Best Practices (Slack Engineering)", "https://slack.engineering/evolving-api-pagination-at-slack/", ARTICLE, "Web", "Offset vs cursor pagination in a real API.", "pagination,api");
        r(l, "Java Date and Time (Oracle Tutorial)", "https://docs.oracle.com/javase/tutorial/datetime/", TUTORIAL, "Java", "LocalDate, Period and ChronoUnit.", "java,dates");
        r(l, "Falsehoods Programmers Believe About Time", "https://gist.github.com/timvisee/fcda9bbdff88d45cc9061606b4b923ca", ARTICLE, "Java", "Why date and time code is hard.", "dates,time");
        r(l, "Regex101", "https://regex101.com/", OTHER, "Strings", "Test regular expressions and see each match explained.", "regex,tool");
        r(l, "RegexOne", "https://regexone.com/", TUTORIAL, "Strings", "Learn regular expressions step by step.", "regex,tutorial");
        r(l, "Regular-Expressions.info", "https://www.regular-expressions.info/", TUTORIAL, "Strings", "The deepest reference on regular expressions.", "regex,reference");

        // ---- Practice sites ----
        r(l, "Codewars", "https://www.codewars.com/", OTHER, "Practice", "Kata of every size with a ranking system.", "practice,katas");
        r(l, "Exercism Java Track", "https://exercism.org/tracks/java", OTHER, "Practice", "Free Java exercises with mentor feedback.", "java,practice");
        r(l, "CodingBat Java", "https://codingbat.com/java", OTHER, "Practice", "Small Java warm-up problems with instant checks.", "java,warmup");
        r(l, "HackerRank 30 Days of Code", "https://www.hackerrank.com/domains/tutorials/30-days-of-code", OTHER, "Practice", "One small challenge a day for 30 days.", "practice,beginner");
        r(l, "Project Euler", "https://projecteuler.net/", OTHER, "Practice", "Maths-flavoured programming puzzles.", "math,practice");
        r(l, "Advent of Code", "https://adventofcode.com/", OTHER, "Practice", "Yearly puzzle calendar for every level.", "puzzles,practice");
        r(l, "InterviewBit", "https://www.interviewbit.com/", OTHER, "Interview", "Structured interview preparation path.", "interview,practice");
        return l;
    }
}
