package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.full;

/** Hard set C: arrays, heaps, intervals, greedy and range-query structures. */
public final class HardArraysHeaps {

    private HardArraysHeaps() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty H = Difficulty.HARD;

        full(l, "First Missing Positive", "Arrays", H, "arrays,in-place,cyclic-sort",
                "Find the smallest missing positive integer in an unsorted array in O(n) time and O(1) extra space.",
                "Signal: the answer is in 1..n+1, so place each number x at index x - 1 (cyclic sort) and then find the first slot that disagrees.",
                null,
                """
                static int firstMissingPositive(int[] a) {
                    int n = a.length;
                    for (int i = 0; i < n; i++) {
                        while (a[i] > 0 && a[i] <= n && a[a[i] - 1] != a[i]) {
                            int target = a[i] - 1;
                            int tmp = a[target];
                            a[target] = a[i];
                            a[i] = tmp;
                        }
                    }
                    for (int i = 0; i < n; i++) {
                        if (a[i] != i + 1) return i + 1;
                    }
                    return n + 1;
                }""",
                "With n numbers the answer cannot exceed n + 1. Use the array itself as a hash table: number x belongs at index x - 1. After swapping every number into its home (ignoring out-of-range values and duplicates), the first index holding the wrong value reveals the missing number.",
                """
                a[i] > 0 && a[i] <= n ==> Only numbers 1..n have a home; others cannot be the answer.
                a[a[i] - 1] != a[i] ==> Do not swap with a slot that already holds the right value (prevents an infinite loop on duplicates).
                swap into a[target] ==> Put the number at its home position.
                while (not for) ==> The value brought into position i may also need a home, so keep swapping until it is settled.
                first i with a[i] != i + 1 ==> The smallest missing positive; if none, the answer is n + 1.""",
                "Explain O(n) even though there is a while inside the for: each swap places at least one number permanently. Mention that a HashSet works but uses O(n) memory.",
                "O(n)", "O(1)", "Infinite loops on duplicate values; using a set and breaking the memory constraint.");

        full(l, "Candy (Two-Pass Greedy)", "Greedy", H, "greedy,arrays,two-pass",
                "Children stand in a line with ratings. Every child gets at least one candy, and a child with a higher rating than a neighbour gets more candy than that neighbour. Return the minimum total candies.",
                "Signal: satisfy the left-neighbour rule in a left-to-right pass, then the right-neighbour rule in a right-to-left pass, taking the max.",
                null,
                """
                static int candy(int[] ratings) {
                    int n = ratings.length;
                    int[] candies = new int[n];
                    Arrays.fill(candies, 1);
                    for (int i = 1; i < n; i++) {
                        if (ratings[i] > ratings[i - 1]) candies[i] = candies[i - 1] + 1;
                    }
                    for (int i = n - 2; i >= 0; i--) {
                        if (ratings[i] > ratings[i + 1]) candies[i] = Math.max(candies[i], candies[i + 1] + 1);
                    }
                    int total = 0;
                    for (int c : candies) total += c;
                    return total;
                }""",
                "Both neighbour rules are independent: the left pass ensures every child with a higher rating than the LEFT neighbour has more; the right pass ensures the same for the RIGHT neighbour. Taking the maximum satisfies both without giving extra candy.",
                """
                Arrays.fill(candies, 1) ==> Everyone starts with one.
                ratings[i] > ratings[i-1] ==> Left-to-right: climb one more than the left neighbour.
                Math.max(candies[i], candies[i+1] + 1) ==> Right-to-left: keep what the first pass gave if it is already enough.
                Sum ==> The minimum total, because each child gets exactly the smallest value that both passes demand.""",
                "A two-pass pattern (like Product of Array Except Self). An O(1) space slope-counting version exists as a follow-up.",
                "O(n)", "O(n)", "Doing only one pass; overwriting instead of taking the max in the second pass.");

        full(l, "Sliding Window Median", "Heap", H, "heap,sliding-window,tree-map",
                "Return the median of every window of size k in an array.",
                "Signal: maintain the window in a sorted structure (or two heaps with lazy deletion).",
                null,
                """
                static double[] medianSlidingWindow(int[] nums, int k) {
                    double[] out = new double[nums.length - k + 1];
                    List<Integer> window = new ArrayList<>();
                    for (int i = 0; i < nums.length; i++) {
                        insertSorted(window, nums[i]);
                        if (i >= k) removeSorted(window, nums[i - k]);
                        if (i >= k - 1) {
                            out[i - k + 1] = k % 2 == 1
                                    ? window.get(k / 2)
                                    : ((double) window.get(k / 2 - 1) + window.get(k / 2)) / 2.0;
                        }
                    }
                    return out;
                }
                static void insertSorted(List<Integer> w, int x) {
                    int idx = Collections.binarySearch(w, x);
                    w.add(idx < 0 ? -(idx + 1) : idx, x);                          // keep the list sorted
                }
                static void removeSorted(List<Integer> w, int x) {
                    w.remove(Collections.binarySearch(w, x));
                }""",
                "A sorted list gives the median by index. Binary search finds the insert/remove position in O(log k); the actual shift costs O(k) but is a fast memmove for realistic k. For strict O(n log k) use two heaps with lazy deletion or an order-statistic tree.",
                """
                window ==> The k current values kept in sorted order.
                Collections.binarySearch(w, x) ==> Position of x, or -(insertion point) - 1 if absent.
                w.add(index, x) ==> Insert while preserving order.
                w.get(k / 2) ==> The median for odd k; for even k average the two middle elements.
                (double) a + b ==> Cast before adding to avoid int overflow.""",
                "State the trade-off between this simple solution and the two-heaps one. Overflow of int sums is a classic bug here.",
                "O(n x k)", "O(k)", "Averaging two ints with (a + b) / 2 and overflowing.");

        full(l, "IPO (Maximise Capital)", "Heap", H, "heap,greedy,sorting",
                "You can finish at most k projects. Each project needs a minimum capital and yields a profit that is added to your capital. Starting with w capital, maximise the final capital.",
                "Signal: among the projects you can currently afford, always take the most profitable one - two heaps (affordable projects by profit, remaining by cost).",
                null,
                """
                static int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
                    int n = profits.length;
                    Integer[] order = new Integer[n];
                    for (int i = 0; i < n; i++) order[i] = i;
                    Arrays.sort(order, Comparator.comparingInt(i -> capital[i]));         // by required capital

                    PriorityQueue<Integer> affordable = new PriorityQueue<>(Collections.reverseOrder());
                    int next = 0;
                    for (int round = 0; round < k; round++) {
                        while (next < n && capital[order[next]] <= w) affordable.add(profits[order[next++]]);
                        if (affordable.isEmpty()) break;                                  // nothing affordable
                        w += affordable.poll();
                    }
                    return w;
                }""",
                "Doing the most profitable affordable project first is always best, because it can only increase the set of affordable projects later. Sorting by capital and sweeping a pointer adds newly affordable projects to a max-heap in order.",
                """
                order sorted by capital ==> Projects become affordable in this order as w grows.
                while (capital[...] <= w) affordable.add(profit) ==> Move every project we can now afford into the max-heap.
                affordable.poll() ==> The best profit among them.
                w += profit ==> Capital grows, possibly unlocking more projects.
                affordable.isEmpty() then break ==> No further project can be started.""",
                "A classic greedy+heap pattern (also used in task scheduling). Justify the greedy choice.",
                "O(n log n)", "O(n)", "Choosing the cheapest instead of the most profitable affordable project.");

        full(l, "Smallest Range Covering Elements from K Lists", "Heap", H, "heap,sorted-lists,sliding-window",
                "k sorted lists are given. Find the smallest range [a, b] that contains at least one number from each list.",
                "Signal: a min-heap holding one element from each list; the range is [heap min, current max]; advance the list of the minimum.",
                null,
                """
                static int[] smallestRange(List<List<Integer>> nums) {
                    PriorityQueue<int[]> heap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));   // {value, list, index}
                    int max = Integer.MIN_VALUE;
                    for (int i = 0; i < nums.size(); i++) {
                        int v = nums.get(i).get(0);
                        heap.add(new int[] {v, i, 0});
                        max = Math.max(max, v);
                    }
                    int bestLo = heap.peek()[0], bestHi = max;
                    while (true) {
                        int[] min = heap.poll();
                        if (max - min[0] < bestHi - bestLo) { bestLo = min[0]; bestHi = max; }
                        int nextIdx = min[2] + 1;
                        if (nextIdx == nums.get(min[1]).size()) break;                   // a list is exhausted: no more full ranges
                        int v = nums.get(min[1]).get(nextIdx);
                        heap.add(new int[] {v, min[1], nextIdx});
                        max = Math.max(max, v);
                    }
                    return new int[] {bestLo, bestHi};
                }""",
                "The heap always contains exactly one element from every list, so [min, max] covers all lists. To shrink the range the only useful move is to replace the current minimum with the next element from its own list. Stop when that list runs out.",
                """
                heap {value, list, index} ==> One current element per list; the smallest is on top.
                max ==> The largest value currently in the heap (tracked separately because the heap exposes only the min).
                max - min[0] < bestHi - bestLo ==> A strictly smaller range than the best found.
                nextIdx == size() ==> The list of the minimum has no more elements; any later range would miss that list.
                heap.add(next element) ==> Slide the window of candidates forward.""",
                "The same idea as merging k sorted lists. Explain why advancing the minimum is the only move that can help.",
                "O(N log k)", "O(k)", "Advancing the maximum or an arbitrary list.");

        full(l, "Employee Free Time", "Intervals", H, "intervals,sorting,heap",
                "Each employee has sorted, non-overlapping working intervals. Return the finite intervals during which ALL employees are free.",
                "Signal: flatten, sort and merge all busy intervals; the gaps between merged intervals are the answer.",
                null,
                """
                static List<int[]> employeeFreeTime(List<List<int[]>> schedules) {
                    List<int[]> all = new ArrayList<>();
                    for (List<int[]> s : schedules) all.addAll(s);
                    all.sort(Comparator.comparingInt(a -> a[0]));

                    List<int[]> free = new ArrayList<>();
                    int end = all.get(0)[1];
                    for (int i = 1; i < all.size(); i++) {
                        int[] cur = all.get(i);
                        if (cur[0] > end) free.add(new int[] {end, cur[0]});       // a gap nobody works
                        end = Math.max(end, cur[1]);
                    }
                    return free;
                }""",
                "Everyone is free exactly when no one is busy, so merge all busy intervals into a single timeline. Only gaps strictly between merged blocks are returned (time before the first and after the last shift is infinite, not 'finite free time').",
                """
                all.addAll(...) and sort by start ==> A single list ordered in time.
                end ==> The end of the merged busy block built so far.
                cur[0] > end ==> The next shift starts after the block ended: the gap [end, cur[0]] is free for everybody.
                end = Math.max(end, cur[1]) ==> Overlapping or nested shifts extend or keep the block.""",
                "Offer the k-way merge with a heap if the lists are huge. The sort-and-merge approach is the expected one.",
                "O(N log N)", "O(N)", "Reporting time before the first or after the last interval.");

        full(l, "Max Points on a Line", "Math", H, "math,hash-map,geometry,gcd",
                "Given points on a 2D plane, return the maximum number of points that lie on the same straight line.",
                "Signal: fix a point, group the other points by slope using an exact reduced fraction (dy/dx via gcd) to avoid floating point errors.",
                null,
                """
                static int maxPoints(int[][] p) {
                    int n = p.length;
                    if (n <= 2) return n;
                    int best = 0;
                    for (int i = 0; i < n; i++) {
                        Map<String, Integer> slopes = new HashMap<>();
                        int localBest = 0;
                        for (int j = i + 1; j < n; j++) {
                            int dx = p[j][0] - p[i][0], dy = p[j][1] - p[i][1];
                            int g = gcd(Math.abs(dx), Math.abs(dy));
                            dx /= g; dy /= g;
                            if (dx < 0 || (dx == 0 && dy < 0)) { dx = -dx; dy = -dy; }     // one canonical sign
                            int count = slopes.merge(dx + "/" + dy, 1, Integer::sum);
                            localBest = Math.max(localBest, count);
                        }
                        best = Math.max(best, localBest + 1);                              // +1 for point i itself
                    }
                    return best;
                }
                static int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }""",
                "Two other points are on one line with point i exactly when they have the same slope from i. Doubles can make different slopes equal (or equal slopes different), so the slope is stored as a reduced pair (dx, dy) with a normalised sign.",
                """
                dx, dy ==> The direction from point i to point j.
                gcd(|dx|, |dy|) and divide ==> 2/4 and 1/2 become the same key.
                sign normalisation ==> (1,-2) and (-1,2) describe the same line, so force dx to be non-negative.
                slopes.merge(key, 1, ...) ==> Count points per direction.
                localBest + 1 ==> The anchor point is also on the line.""",
                "The vertical line (dx = 0) is handled by the same code. Duplicate points would need an extra counter. Discuss why doubles are dangerous.",
                "O(n^2)", "O(n)", "Using double slopes; forgetting vertical lines; not counting the anchor.");

        full(l, "Count of Range Sum", "Divide and Conquer", H, "merge-sort,prefix-sum,divide-and-conquer",
                "Count the subarrays whose sum lies in [lower, upper].",
                "Signal: with prefix sums it becomes counting pairs (i < j) with lower <= prefix[j] - prefix[i] <= upper - done with merge sort.",
                null,
                """
                static int countRangeSum(int[] nums, int lower, int upper) {
                    long[] prefix = new long[nums.length + 1];
                    for (int i = 0; i < nums.length; i++) prefix[i + 1] = prefix[i] + nums[i];
                    return sort(prefix, 0, prefix.length - 1, lower, upper);
                }
                static int sort(long[] a, int lo, int hi, int lower, int upper) {
                    if (lo >= hi) return 0;
                    int mid = (lo + hi) >>> 1;
                    int count = sort(a, lo, mid, lower, upper) + sort(a, mid + 1, hi, lower, upper);
                    int l1 = mid + 1, l2 = mid + 1;
                    for (int i = lo; i <= mid; i++) {                       // both halves are sorted now
                        while (l1 <= hi && a[l1] - a[i] < lower) l1++;
                        while (l2 <= hi && a[l2] - a[i] <= upper) l2++;
                        count += l2 - l1;                                   // right-half values in the allowed window
                    }
                    long[] tmp = new long[hi - lo + 1];
                    int i = lo, j = mid + 1, k = 0;
                    while (i <= mid && j <= hi) tmp[k++] = a[i] <= a[j] ? a[i++] : a[j++];
                    while (i <= mid) tmp[k++] = a[i++];
                    while (j <= hi) tmp[k++] = a[j++];
                    System.arraycopy(tmp, 0, a, lo, tmp.length);
                    return count;
                }""",
                "The sum of nums[i..j-1] equals prefix[j] - prefix[i]. Counting pairs across the two halves of a merge sort is cheap: for each left value, the qualifying right values form a contiguous block in the sorted right half, found with two moving pointers.",
                """
                prefix[] as long ==> Sums can exceed int range.
                sort(left) + sort(right) ==> Pairs entirely inside a half are counted recursively.
                l1, l2 ==> For the current left value, a[l1..l2) are the right values with lower <= diff <= upper; both pointers only move forward because the left half is sorted too.
                count += l2 - l1 ==> Number of valid pairs for this left element.
                merge ==> Sort the range so the parent call can reuse the same two-pointer trick.""",
                "Related to 'Count of Smaller Numbers After Self' and 'Reverse Pairs'. A Fenwick tree with coordinate compression is the alternative.",
                "O(n log n)", "O(n)", "Using int for prefix sums (overflow).");

        full(l, "Reverse Pairs", "Divide and Conquer", H, "merge-sort,divide-and-conquer,counting",
                "Count pairs (i, j) with i < j and nums[i] > 2 * nums[j].",
                "Signal: count important pairs during merge sort before merging.",
                null,
                """
                static int reversePairs(int[] nums) {
                    return sort(nums, 0, nums.length - 1);
                }
                static int sort(int[] a, int lo, int hi) {
                    if (lo >= hi) return 0;
                    int mid = (lo + hi) >>> 1;
                    int count = sort(a, lo, mid) + sort(a, mid + 1, hi);
                    int j = mid + 1;
                    for (int i = lo; i <= mid; i++) {
                        while (j <= hi && (long) a[i] > 2L * a[j]) j++;      // all of a[mid+1 .. j-1] are small enough
                        count += j - (mid + 1);
                    }
                    Arrays.sort(a, lo, hi + 1);                               // simple merge step
                    return count;
                }""",
                "Because both halves are sorted, as i moves right the number of right-half elements j satisfying nums[i] > 2 * nums[j] can only grow, so j never moves backwards: a two-pointer scan counts all pairs in linear time per level.",
                """
                sort(left) + sort(right) ==> Pairs inside each half.
                (long) a[i] > 2L * a[j] ==> Use long: doubling an int can overflow.
                j keeps its position ==> Monotonic pointer thanks to sorted halves.
                count += j - (mid + 1) ==> The number of qualifying right elements for a[i].
                Arrays.sort(a, lo, hi + 1) ==> Restores sorted order for the parent (a hand-written merge is O(n) instead).""",
                "Discuss the Fenwick tree alternative. Overflow with 2 * a[j] is the classic trap.",
                "O(n log n)", "O(1) extra (plus sort)", "Computing 2 * nums[j] in int arithmetic.");

        full(l, "Split Array Largest Sum", "Binary Search", H, "binary-search,greedy,dp",
                "Split an array into k non-empty contiguous subarrays to minimise the largest subarray sum. Return that minimum.",
                "Signal: binary search on the ANSWER; for a candidate limit, greedily count how many subarrays are needed.",
                null,
                """
                static int splitArray(int[] nums, int k) {
                    int lo = 0, hi = 0;
                    for (int x : nums) { lo = Math.max(lo, x); hi += x; }       // answer is between max element and total sum
                    while (lo < hi) {
                        int mid = (lo + hi) >>> 1;
                        if (needed(nums, mid) <= k) hi = mid;                   // limit is large enough: try smaller
                        else lo = mid + 1;                                      // too small: need more pieces than allowed
                    }
                    return lo;
                }
                static int needed(int[] nums, int limit) {
                    int parts = 1, sum = 0;
                    for (int x : nums) {
                        if (sum + x > limit) { parts++; sum = x; }              // start a new subarray
                        else sum += x;
                    }
                    return parts;
                }""",
                "A larger limit never needs more parts, so feasibility is monotonic and binary search applies. For a given limit, packing greedily (extend each part as far as possible) gives the minimum number of parts.",
                """
                lo = max element ==> No part can be smaller than the biggest single number.
                hi = total sum ==> One part holds everything.
                needed(nums, mid) <= k ==> The limit works (we could always split further into exactly k parts).
                hi = mid ==> Feasible: search for something smaller.
                greedy packing in needed() ==> Add numbers until the limit would be exceeded, then start a new part.""",
                "Same pattern as 'Capacity to Ship Packages' and 'Koko Eating Bananas'. A DP O(k * n^2) solution also exists.",
                "O(n log S)", "O(1)", "Searching indexes rather than the answer range.");

        full(l, "Substring with Concatenation of All Words", "Sliding Window", H, "sliding-window,hash-map,strings",
                "Given a string and words that all have the same length, return all start indices of substrings that are a concatenation of every word exactly once (any order).",
                "Signal: run the sliding window in WORD-sized steps for each of the wordLen starting offsets.",
                null,
                """
                static List<Integer> findSubstring(String s, String[] words) {
                    List<Integer> out = new ArrayList<>();
                    int wl = words[0].length(), total = words.length;
                    Map<String, Integer> need = new HashMap<>();
                    for (String w : words) need.merge(w, 1, Integer::sum);

                    for (int offset = 0; offset < wl; offset++) {
                        Map<String, Integer> have = new HashMap<>();
                        int left = offset, count = 0;
                        for (int right = offset; right + wl <= s.length(); right += wl) {
                            String w = s.substring(right, right + wl);
                            if (!need.containsKey(w)) {                         // reset: this word cannot be part of any match
                                have.clear();
                                count = 0;
                                left = right + wl;
                                continue;
                            }
                            have.merge(w, 1, Integer::sum);
                            count++;
                            while (have.get(w) > need.get(w)) {                 // too many of this word: shrink from the left
                                String lw = s.substring(left, left + wl);
                                have.merge(lw, -1, Integer::sum);
                                count--;
                                left += wl;
                            }
                            if (count == total) out.add(left);
                        }
                    }
                    return out;
                }""",
                "All words have equal length, so a valid substring is aligned on word boundaries relative to its start. Trying each of the wl offsets and sliding by whole words examines every alignment once, keeping counts of words in the window.",
                """
                need ==> How many times each word must appear.
                for (offset = 0; offset < wl; offset++) ==> The window can start at any character offset modulo the word length.
                right += wl ==> Move one WORD at a time.
                !need.containsKey(w) ==> A foreign word: clear the window and restart after it.
                while (have.get(w) > need.get(w)) ==> Too many copies: drop words from the left until valid.
                count == total ==> The window holds exactly all the words; record the start.""",
                "The naive approach re-checks every start index (O(n * m * wl)). Explain why the offset loop is enough.",
                "O(n x wl)", "O(m x wl)", "Sliding by 1 character instead of a word length.");

        full(l, "Maximum Frequency Stack", "Design", H, "design,stack,hash-map",
                "Design a stack with push(x) and pop(), where pop removes and returns the most FREQUENT element; ties are broken by the most recently pushed.",
                "Signal: one stack per frequency level plus a map of counts and the current maximum frequency.",
                null,
                """
                class FreqStack {
                    private final Map<Integer, Integer> freq = new HashMap<>();
                    private final Map<Integer, Deque<Integer>> groups = new HashMap<>();   // frequency -> stack of values
                    private int maxFreq = 0;

                    void push(int x) {
                        int f = freq.merge(x, 1, Integer::sum);
                        maxFreq = Math.max(maxFreq, f);
                        groups.computeIfAbsent(f, k -> new ArrayDeque<>()).push(x);
                    }

                    int pop() {
                        Deque<Integer> top = groups.get(maxFreq);
                        int x = top.pop();
                        freq.merge(x, -1, Integer::sum);
                        if (top.isEmpty()) {
                            groups.remove(maxFreq);
                            maxFreq--;
                        }
                        return x;
                    }
                }""",
                "A value pushed for the 3rd time sits in the stack for frequency 3 AND (from earlier pushes) in the stacks for 1 and 2. Popping from the highest group removes the most frequent element, and the stack order inside the group gives the tie-break by recency. Lower groups keep their copy, which is correct for the next pops.",
                """
                freq ==> Current count per value.
                groups[f] ==> Stack of values that reached frequency f, in push order.
                maxFreq ==> Which group to pop from.
                top.pop() ==> Most frequent, most recent value.
                if (top.isEmpty()) maxFreq-- ==> The next highest group is exactly one below, since groups are filled level by level.""",
                "A good O(1) design. Draw the groups for the sequence 5,7,5,7,4,5.",
                "O(1)", "O(n)", "Using a priority queue and ignoring the O(1) requirement.");

        full(l, "Car Pooling / My Calendar III", "Intervals", H, "intervals,tree-map,sweep-line",
                "Events are booked as [start, end). After each booking return the maximum number of simultaneous bookings (the 'k-booking') so far.",
                "Signal: a sweep over a sorted map of +1 at starts and -1 at ends; the running sum's maximum is the answer.",
                null,
                """
                class MyCalendarThree {
                    private final TreeMap<Integer, Integer> delta = new TreeMap<>();

                    int book(int start, int end) {
                        delta.merge(start, 1, Integer::sum);       // one more booking begins
                        delta.merge(end, -1, Integer::sum);        // one ends (at end, exclusive)
                        int running = 0, best = 0;
                        for (int change : delta.values()) {
                            running += change;
                            best = Math.max(best, running);
                        }
                        return best;
                    }
                }""",
                "Every booking adds +1 at its start and -1 at its end. Scanning the changes in time order and keeping a running total shows how many bookings overlap at each moment; the maximum is the answer. A segment tree makes each booking O(log n) instead of O(n).",
                """
                TreeMap<Integer, Integer> delta ==> time -> net change in active bookings, sorted by time.
                merge(start, 1, ...) ==> Start of a booking; several bookings can start at the same time.
                merge(end, -1, ...) ==> End is exclusive, so bookings that touch do not overlap.
                running ==> Active bookings after applying each change in time order.
                best ==> The highest overlap seen.""",
                "Start with this sweep line, then mention the segment tree / lazy propagation for faster queries. The same technique solves 'Meeting Rooms II' and 'Car Pooling'.",
                "O(n) per booking", "O(n)", "Treating the end as inclusive.");

        full(l, "Range Sum Query Mutable (Fenwick Tree)", "Design", H, "fenwick-tree,binary-indexed-tree,design",
                "Support update(index, value) and sumRange(left, right) on an array, both in O(log n).",
                "Signal: a Fenwick tree (binary indexed tree) stores partial sums indexed by the lowest set bit.",
                null,
                """
                class NumArray {
                    private final int[] nums;
                    private final int[] tree;                              // 1-indexed Fenwick tree

                    NumArray(int[] nums) {
                        this.nums = new int[nums.length];
                        this.tree = new int[nums.length + 1];
                        for (int i = 0; i < nums.length; i++) update(i, nums[i]);
                    }

                    void update(int index, int value) {
                        int delta = value - nums[index];
                        nums[index] = value;
                        for (int i = index + 1; i < tree.length; i += i & -i) tree[i] += delta;     // go up
                    }

                    int sumRange(int left, int right) {
                        return prefix(right + 1) - prefix(left);
                    }

                    private int prefix(int i) {
                        int sum = 0;
                        for (; i > 0; i -= i & -i) sum += tree[i];                                   // go down
                        return sum;
                    }
                }""",
                "tree[i] holds the sum of a block of length lowbit(i) ending at i. An update touches every block that contains the index (jumping up by adding the lowest set bit); a prefix query adds disjoint blocks (jumping down by removing the lowest set bit). Both take O(log n).",
                """
                i & -i ==> Isolates the lowest set bit (the block length of i).
                i += i & -i ==> Next block that covers this index.
                i -= i & -i ==> Next block to add when summing a prefix.
                update(index, value) stores a DELTA ==> Fenwick trees add differences, so we remember the old value in nums.
                prefix(right + 1) - prefix(left) ==> Range sum from two prefix sums.""",
                "Compare with a segment tree (more general, more code) and prefix sums (static arrays only). The i & -i trick is the thing to explain.",
                "O(log n)", "O(n)", "0-indexing the tree (it must be 1-indexed); forgetting to store the delta.");

        full(l, "Expression Add Operators", "Backtracking", H, "backtracking,strings,math",
                "Insert +, - or * between the digits of a string so the expression evaluates to a target. Return all valid expressions (no numbers with leading zeros).",
                "Signal: backtracking that carries the current value AND the last multiplied term, so multiplication precedence can be undone.",
                null,
                """
                static List<String> addOperators(String num, int target) {
                    List<String> out = new ArrayList<>();
                    dfs(num, target, 0, "", 0, 0, out);
                    return out;
                }
                static void dfs(String num, int target, int pos, String expr, long value, long lastTerm, List<String> out) {
                    if (pos == num.length()) {
                        if (value == target) out.add(expr);
                        return;
                    }
                    for (int end = pos + 1; end <= num.length(); end++) {
                        if (end > pos + 1 && num.charAt(pos) == '0') break;           // no leading zeros
                        long cur = Long.parseLong(num.substring(pos, end));
                        String part = num.substring(pos, end);
                        if (pos == 0) {
                            dfs(num, target, end, part, cur, cur, out);
                        } else {
                            dfs(num, target, end, expr + "+" + part, value + cur, cur, out);
                            dfs(num, target, end, expr + "-" + part, value - cur, -cur, out);
                            dfs(num, target, end, expr + "*" + part, value - lastTerm + lastTerm * cur, lastTerm * cur, out);
                        }
                    }
                }""",
                "Multiplication binds tighter, so when we multiply we must undo the last added or subtracted term and replace it with term * cur: value - lastTerm + lastTerm * cur. Carrying lastTerm makes that correction possible without re-evaluating the whole expression.",
                """
                value ==> The result of the expression built so far.
                lastTerm ==> The most recent operand (with sign) that was added; the part that a following '*' will extend.
                value - lastTerm + lastTerm * cur ==> Replace the last term by its product with the new number.
                end > pos + 1 && num.charAt(pos) == '0' ==> A number such as '05' is not allowed; stop extending.
                pos == 0 ==> The first number has no operator in front of it.""",
                "Use long to avoid overflow with long digit strings. Complexity is exponential (4^n worst case).",
                "O(4^n)", "O(n)", "Evaluating the whole expression at the end (too slow) or ignoring precedence.");

        full(l, "Integer to English Words", "Strings", H, "strings,math,recursion",
                "Convert a non-negative integer to its English words: 1234567 becomes 'One Million Two Hundred Thirty Four Thousand Five Hundred Sixty Seven'.",
                "Signal: process the number in groups of three digits (thousands, millions, billions) with a helper for 0-999.",
                null,
                """
                private static final String[] BELOW_20 = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
                private static final String[] TENS = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
                private static final String[] SCALES = {"", "Thousand", "Million", "Billion"};

                static String numberToWords(int num) {
                    if (num == 0) return "Zero";
                    StringBuilder sb = new StringBuilder();
                    for (int scale = 0; num > 0; scale++, num /= 1000) {
                        int chunk = num % 1000;
                        if (chunk != 0) {
                            String words = below1000(chunk) + (SCALES[scale].isEmpty() ? "" : " " + SCALES[scale]);
                            sb.insert(0, words + (sb.length() > 0 ? " " : ""));
                        }
                    }
                    return sb.toString();
                }
                static String below1000(int n) {
                    if (n < 20) return BELOW_20[n];
                    if (n < 100) return TENS[n / 10] + (n % 10 != 0 ? " " + BELOW_20[n % 10] : "");
                    return BELOW_20[n / 100] + " Hundred" + (n % 100 != 0 ? " " + below1000(n % 100) : "");
                }""",
                "English reads numbers in repeating groups of three digits, each followed by a scale word. A small recursive helper handles 0-999, and the main loop peels groups from the right, prepending each so the final order is correct.",
                """
                BELOW_20 / TENS / SCALES ==> Lookup tables for the irregular names; they replace dozens of if statements.
                num % 1000 ==> The current group of three digits; num /= 1000 moves to the next group.
                chunk != 0 ==> Skip empty groups so 'One Million Five' has no 'Thousand'.
                sb.insert(0, ...) ==> We process from the least significant group, so new words go in front.
                below1000 ==> Handles hundreds, tens and the teens with recursion on the remainder.""",
                "Edge cases: 0, 10, 100, 1000, 1000010, Integer.MAX_VALUE. Tests whether you can organise a messy specification cleanly.",
                "O(digits)", "O(digits)", "Extra spaces; saying 'One Thousand' for an empty group.");

        return l;
    }
}
