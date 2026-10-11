package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.full;

/** Seven extra problems that round the new library up to 200. */
public final class ExtraProblems {

    private ExtraProblems() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();

        full(l, "Implement a Queue Using Two Stacks", "Stack", Difficulty.EASY, "design,stack,queue,amortized",
                "Build a FIFO queue (push, pop, peek, empty) using only stack operations.",
                "Signal: one stack receives new items; a second stack reverses them lazily when the front is needed.",
                """
                class MyQueue {
                    private final Deque<Integer> in = new ArrayDeque<>();
                    void push(int x) { in.push(x); }
                    int pop() {
                        Deque<Integer> tmp = new ArrayDeque<>();
                        while (!in.isEmpty()) tmp.push(in.pop());          // reverse everything on EVERY pop: O(n)
                        int front = tmp.pop();
                        while (!tmp.isEmpty()) in.push(tmp.pop());
                        return front;
                    }
                }""",
                """
                class MyQueue {
                    private final Deque<Integer> in = new ArrayDeque<>(), out = new ArrayDeque<>();

                    void push(int x) { in.push(x); }

                    int pop() {
                        shift();
                        return out.pop();
                    }
                    int peek() {
                        shift();
                        return out.peek();
                    }
                    boolean empty() { return in.isEmpty() && out.isEmpty(); }

                    private void shift() {
                        if (out.isEmpty()) {                               // only refill when the output stack is empty
                            while (!in.isEmpty()) out.push(in.pop());
                        }
                    }
                }""",
                "Moving all elements from in to out reverses their order, so the oldest element ends up on top of out. Elements are moved only when out is empty, so each element is moved at most once: every operation is O(1) amortised.",
                """
                in ==> Receives every push; the newest element is on top.
                out ==> Holds elements in queue order; its top is the front of the queue.
                shift() only when out.isEmpty() ==> Moving earlier would break the order of items already in out.
                empty() ==> Both stacks must be empty.""",
                "A classic amortised analysis question: explain why the total cost of n operations is O(n). The reverse (stack from queues) is the follow-up.",
                "O(1) amortised", "O(n)", "Moving elements on every pop, or refilling out while it still has items.");

        full(l, "Meeting Rooms (Can Attend All?)", "Intervals", Difficulty.EASY, "intervals,sorting",
                "Given meeting time intervals, determine whether one person can attend all of them (no overlaps).",
                "Signal: sort by start time, then compare each meeting with the previous one.",
                """
                static boolean canAttendAll(int[][] meetings) {
                    for (int i = 0; i < meetings.length; i++)
                        for (int j = i + 1; j < meetings.length; j++)
                            if (meetings[i][0] < meetings[j][1] && meetings[j][0] < meetings[i][1]) return false;
                    return true;
                }""",
                """
                static boolean canAttendAll(int[][] meetings) {
                    Arrays.sort(meetings, Comparator.comparingInt(m -> m[0]));
                    for (int i = 1; i < meetings.length; i++) {
                        if (meetings[i][0] < meetings[i - 1][1]) return false;      // starts before the previous one ends
                    }
                    return true;
                }""",
                "After sorting, any overlap must involve neighbours, so one linear scan is enough. A meeting that starts exactly when the previous one ends is allowed.",
                """
                sort by start ==> Overlaps can then only occur between consecutive meetings.
                meetings[i][0] < meetings[i-1][1] ==> The next meeting starts before the previous ends.
                strict < ==> Touching meetings (end == start) do not conflict.
                nested loops (beginner) ==> Compare every pair; O(n^2).""",
                "The warm-up for 'Meeting Rooms II' (hotel booking) and 'Insert Interval'.",
                "O(n log n)", "O(1)", "Using <= and rejecting back-to-back meetings.");

        full(l, "Flood Fill (Paint Bucket Tool)", "Website: Image Editor", Difficulty.EASY, "website,grid,dfs,bfs",
                "Implement the paint-bucket tool: starting at pixel (r, c) change it and every connected pixel of the same colour (4-directional) to a new colour.",
                "Signal: DFS/BFS from the clicked pixel across pixels with the original colour.",
                """
                static int[][] floodFill(int[][] image, int r, int c, int newColor) {
                    int old = image[r][c];
                    if (old == newColor) return image;
                    fill(image, r, c, old, newColor);
                    return image;
                }
                static void fill(int[][] img, int r, int c, int old, int nu) {
                    if (r < 0 || c < 0 || r >= img.length || c >= img[0].length || img[r][c] != old) return;
                    img[r][c] = nu;
                    fill(img, r + 1, c, old, nu); fill(img, r - 1, c, old, nu);
                    fill(img, r, c + 1, old, nu); fill(img, r, c - 1, old, nu);
                }""",
                """
                static int[][] floodFill(int[][] image, int r, int c, int newColor) {
                    int old = image[r][c];
                    if (old == newColor) return image;
                    Deque<int[]> stack = new ArrayDeque<>();
                    stack.push(new int[] {r, c});
                    while (!stack.isEmpty()) {
                        int[] p = stack.pop();
                        if (p[0] < 0 || p[1] < 0 || p[0] >= image.length || p[1] >= image[0].length) continue;
                        if (image[p[0]][p[1]] != old) continue;
                        image[p[0]][p[1]] = newColor;
                        stack.push(new int[] {p[0] + 1, p[1]}); stack.push(new int[] {p[0] - 1, p[1]});
                        stack.push(new int[] {p[0], p[1] + 1}); stack.push(new int[] {p[0], p[1] - 1});
                    }
                    return image;
                }""",
                "Recolouring a pixel also marks it as visited, because it no longer has the old colour. The early return when old == newColor is essential: without it the fill would never terminate.",
                """
                old == newColor ==> Nothing to do; also prevents infinite recursion.
                img[r][c] != old ==> Boundary of the region (a different colour) or an already painted pixel.
                img[r][c] = nu ==> Paint and mark visited in one step.
                explicit stack ==> The iterative version avoids StackOverflowError on huge images.""",
                "Used in every image editor and in 'Number of Islands'. Mention the recursion depth problem for a 4000x4000 image.",
                "O(pixels)", "O(pixels)", "Forgetting the old == new check (infinite loop).");

        full(l, "Valid Palindrome II (Delete at Most One)", "Two Pointers", Difficulty.MEDIUM, "strings,two-pointers,palindrome",
                "Return true if a string can become a palindrome by deleting at most one character.",
                "Signal: two pointers from both ends; at the first mismatch try skipping the left OR the right character.",
                null,
                """
                static boolean validPalindrome(String s) {
                    int left = 0, right = s.length() - 1;
                    while (left < right) {
                        if (s.charAt(left) != s.charAt(right)) {
                            return isPal(s, left + 1, right) || isPal(s, left, right - 1);   // delete one of the two
                        }
                        left++;
                        right--;
                    }
                    return true;
                }
                static boolean isPal(String s, int l, int r) {
                    while (l < r) if (s.charAt(l++) != s.charAt(r--)) return false;
                    return true;
                }""",
                "Only one deletion is allowed, so the first mismatch decides the story: either the left or the right character must be removed. Checking both remaining ranges costs O(n) once, giving O(n) overall.",
                """
                s.charAt(left) != s.charAt(right) ==> The first place where the palindrome property breaks.
                isPal(s, left + 1, right) ==> Try deleting the left character.
                isPal(s, left, right - 1) ==> Try deleting the right character.
                || ==> Either deletion is enough.
                after the loop ==> No mismatch at all: already a palindrome.""",
                "Make sure you can explain why you do not need to try deleting characters elsewhere. Follow-up: at most k deletions (DP).",
                "O(n)", "O(1)", "Trying every possible deletion (O(n^2)).");

        full(l, "Find the Duplicate Number (Floyd's Cycle)", "Linked List", Difficulty.MEDIUM, "arrays,cycle-detection,two-pointers",
                "An array of n + 1 integers contains values from 1 to n and exactly one value repeats. Find it without modifying the array and using O(1) extra space.",
                "Signal: treat index -> value as a linked list; the duplicate is where the cycle begins (Floyd's tortoise and hare).",
                null,
                """
                static int findDuplicate(int[] nums) {
                    int slow = nums[0], fast = nums[0];
                    do {                                         // phase 1: find a meeting point inside the cycle
                        slow = nums[slow];
                        fast = nums[nums[fast]];
                    } while (slow != fast);

                    slow = nums[0];                              // phase 2: find the cycle entrance
                    while (slow != fast) {
                        slow = nums[slow];
                        fast = nums[fast];
                    }
                    return slow;
                }""",
                "With n + 1 values in the range 1..n, following i -> nums[i] must eventually repeat, creating a cycle. The entrance of the cycle is the value that two different indexes point to: the duplicate. Floyd's algorithm finds the entrance in O(n) time without extra memory.",
                """
                slow = nums[slow] ==> One step along the 'linked list'.
                fast = nums[nums[fast]] ==> Two steps; inside a cycle fast must catch slow.
                second loop from the start ==> The distance from the start to the entrance equals the distance from the meeting point to the entrance (modulo the cycle length), so moving both at the same speed makes them meet at the entrance.
                return slow ==> The duplicate number.""",
                "Alternatives with their limits: HashSet (extra memory), sorting (modifies input), binary search on value (O(n log n)). Floyd's version satisfies every constraint.",
                "O(n)", "O(1)", "Using this when the array may contain 0 or values above n.");

        full(l, "Kth Largest Element in a Stream", "Heap", Difficulty.MEDIUM, "heap,design,streaming",
                "Design a class that receives numbers one at a time and, after each add(val), returns the kth largest element seen so far.",
                "Signal: keep a MIN-heap of size k; its top is the kth largest.",
                null,
                """
                class KthLargest {
                    private final PriorityQueue<Integer> heap = new PriorityQueue<>();
                    private final int k;

                    KthLargest(int k, int[] initial) {
                        this.k = k;
                        for (int x : initial) add(x);
                    }

                    int add(int val) {
                        heap.add(val);
                        if (heap.size() > k) heap.poll();          // discard the smallest of the k + 1 candidates
                        return heap.peek();
                    }
                }""",
                "The heap holds only the k largest values seen so far, so the smallest of them (the top of a min-heap) is the kth largest. Each new number costs O(log k) and memory stays O(k) however long the stream is.",
                """
                PriorityQueue<Integer> ==> A min-heap by default.
                heap.size() > k ==> More than k values: the smallest cannot be among the k largest.
                heap.poll() ==> Removes it.
                heap.peek() ==> The kth largest right now.""",
                "A real-world pattern for leaderboards and monitoring dashboards. Compare with sorting on every insert (O(n log n)).",
                "O(log k) per add", "O(k)", "Using a max-heap, which would need O(n) memory and work.");

        full(l, "Subsets II (With Duplicates)", "Backtracking", Difficulty.MEDIUM, "backtracking,duplicates,sorting",
                "Given numbers that may contain duplicates, return all possible subsets without duplicate subsets.",
                "Signal: sort first, then skip a value equal to its predecessor in the same loop level.",
                null,
                """
                static List<List<Integer>> subsetsWithDup(int[] nums) {
                    Arrays.sort(nums);                                         // equal values become neighbours
                    List<List<Integer>> out = new ArrayList<>();
                    backtrack(nums, 0, new ArrayList<>(), out);
                    return out;
                }
                static void backtrack(int[] nums, int start, List<Integer> cur, List<List<Integer>> out) {
                    out.add(new ArrayList<>(cur));
                    for (int i = start; i < nums.length; i++) {
                        if (i > start && nums[i] == nums[i - 1]) continue;     // same value already tried at this position
                        cur.add(nums[i]);
                        backtrack(nums, i + 1, cur, out);
                        cur.remove(cur.size() - 1);
                    }
                }""",
                "Sorting groups equal numbers. If we already tried a value at a given recursion level, trying an identical value again would generate the same subsets. The condition i > start (not i > 0) still allows using the same value deeper in the recursion, which is how [2, 2] is produced.",
                """
                Arrays.sort(nums) ==> Required so duplicates are adjacent.
                i > start && nums[i] == nums[i - 1] ==> Skip a duplicate choice at the SAME level only.
                backtrack(..., i + 1, ...) ==> Each element is used at most once.
                out.add(new ArrayList<>(cur)) ==> Every partial list is a subset; save a copy.""",
                "The most common mistake is writing i > 0 instead of i > start. Compare with Subsets (no duplicates) and Combination Sum II.",
                "O(n x 2^n)", "O(n)", "Using i > 0 which wrongly removes valid subsets like [2, 2].");

        return l;
    }
}
