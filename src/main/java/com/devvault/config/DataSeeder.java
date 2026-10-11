package com.devvault.config;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;
import com.devvault.entity.LearningResource;
import com.devvault.entity.ResourceType;
import com.devvault.repository.ProblemRepository;
import com.devvault.repository.ResourceRepository;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements SmartInitializingSingleton {

    private final ProblemRepository problemRepository;
    private final ResourceRepository resourceRepository;

    public DataSeeder(ProblemRepository problemRepository, ResourceRepository resourceRepository) {
        this.problemRepository = problemRepository;
        this.resourceRepository = resourceRepository;
    }

    /** Runs before the web server starts, so visitors never see a half-filled library. */
    @Override
    public void afterSingletonsInstantiated() {
        seedProblemIfMissing(medianOfTwoSortedArrays());
        seedProblemIfMissing(trappingRainWater());
        seedProblemIfMissing(nQueens());
        seedProblemIfMissing(longestValidParentheses());
        seedProblemIfMissing(wordLadder());
        seedProblemIfMissing(twoSum());
        seedProblemIfMissing(validParentheses());
        seedProblemIfMissing(maximumSubarray());
        seedProblemIfMissing(missingNumber());
        seedProblemIfMissing(linkedListCycle());
        seedProblemIfMissing(mergeIntervals());
        seedProblemIfMissing(lruCache());
        seedProblemIfMissing(serializeDeserializeBinaryTree());
        seedProblemIfMissing(slidingWindowMaximum());
        seedProblemIfMissing(courseSchedule());
        seedProblemIfMissing(outputIntegerCaching());
        seedProblemIfMissing(outputStringPool());
        seedProblemIfMissing(outputIncrementPrecedence());
        seedProblemIfMissing(outputFinallyOverridesReturn());
        seedProblemIfMissing(outputStaticInitOrder());
        seedProblemIfMissing(outputStringConcatenation());
        seedProblemIfMissing(bugOffByOneArray());
        seedProblemIfMissing(bugInfiniteLoop());
        seedProblemIfMissing(bugNullPointer());
        seedProblemIfMissing(bugIntegerDivision());
        seedProblemIfMissing(bugConcurrentModification());

        if (resourceRepository.count() == 0) {
            seedResources();
        }
    }

    private void seedProblemIfMissing(CodingProblem problem) {
        if (!problemRepository.existsByTitleIgnoreCase(problem.getTitle())) {
            problemRepository.save(problem);
        }
    }

    private CodingProblem twoSum() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Two Sum");
        p.setTopic("Hash Map");
        p.setDifficulty(Difficulty.EASY);
        p.setTags("arrays,hash-map,classic-first-question");
        p.setPatternHint("""
                Signal to watch for: "find a pair/combination that sums to / matches a target" over an \
                unsorted collection. Whenever you need to check "have I seen a complementary value \
                before?", that's a hash map, not a nested loop.""");
        p.setCommonMistakes("""
                Using the same element twice (forgetting the "not the same index" constraint); checking \
                containsKey(nums[i]) instead of containsKey(complement); inserting into the map before \
                checking for the complement, which can incorrectly match an element against itself.""");
        p.setCompanyTags("Classic first-round screening question across most large tech companies.");
        p.setDescription("""
                Given an array of integers nums and an integer target, return the indices of the two \
                numbers that add up to target. Exactly one valid answer exists; you may not use the \
                same element twice.""");
        p.setSolutionCode("""
                import java.util.HashMap;
                import java.util.Map;

                class Solution {
                    public int[] twoSum(int[] nums, int target) {
                        Map<Integer, Integer> seen = new HashMap<>();
                        for (int i = 0; i < nums.length; i++) {
                            int complement = target - nums[i];
                            if (seen.containsKey(complement)) {
                                return new int[] { seen.get(complement), i };
                            }
                            seen.put(nums[i], i);
                        }
                        throw new IllegalArgumentException("No two sum solution");
                    }
                }""");
        p.setExplanation("""
                The brute-force approach checks every pair (O(n^2)). The trick: instead of searching \
                forward for a partner, remember every number you've already walked past in a hash map. \
                Then, at each new number, just ask "have I already seen the value that would complete \
                this pair?" - an O(1) lookup instead of a nested loop. This turns O(n^2) into O(n) at \
                the cost of O(n) extra space.

                Interviewer's angle: this is usually the very first question in a technical screen, \
                specifically because the brute-force answer is obvious and the hash-map answer is not. \
                What interviewers are really scoring is whether you state the brute-force complexity \
                out loud first, then reach for the trade-off on your own, without being prompted.""");
        p.setTimeComplexity("O(n)");
        p.setSpaceComplexity("O(n)");
        return p;
    }

    private CodingProblem validParentheses() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Valid Parentheses");
        p.setTopic("Stack");
        p.setDifficulty(Difficulty.EASY);
        p.setTags("strings,stack,classic-first-question");
        p.setPatternHint("""
                Signal to watch for: anything about "nesting," "matching pairs," or "balanced" \
                structure (brackets, tags, nested function calls). Nesting always implies Last-In-\
                First-Out order, which means a stack.""");
        p.setCommonMistakes("""
                Not checking for an empty stack before popping (crashes or false positives); forgetting \
                to verify the stack is empty at the very end (unmatched opening brackets); comparing the \
                wrong bracket pair direction.""");
        p.setCompanyTags("Classic first-round screening question across most large tech companies.");
        p.setDescription("""
                Given a string containing only '(', ')', '{', '}', '[', ']', determine whether every \
                bracket is closed in the correct order.""");
        p.setSolutionCode("""
                import java.util.ArrayDeque;
                import java.util.Deque;
                import java.util.Map;

                class Solution {
                    public boolean isValid(String s) {
                        Deque<Character> stack = new ArrayDeque<>();
                        Map<Character, Character> pairs = Map.of(')', '(', ']', '[', '}', '{');

                        for (char c : s.toCharArray()) {
                            if (pairs.containsValue(c)) {
                                stack.push(c);
                            } else if (pairs.containsKey(c)) {
                                if (stack.isEmpty() || !stack.pop().equals(pairs.get(c))) {
                                    return false;
                                }
                            }
                        }
                        return stack.isEmpty();
                    }
                }""");
        p.setExplanation("""
                A stack is a Last-In-First-Out structure, which exactly mirrors how nested brackets \
                must close: the most recently opened bracket must be the next one closed. Push on every \
                opening bracket; on a closing bracket, pop and check it matches. Two failure modes to \
                remember: popping from an empty stack (an unmatched closer) and a non-empty stack left \
                over at the end (an unmatched opener).

                Interviewer's angle: almost every candidate gets the "happy path" right immediately. \
                What separates strong answers is whether you proactively mention and handle both edge \
                cases above without being asked - that is the real signal in this question.""");
        p.setTimeComplexity("O(n)");
        p.setSpaceComplexity("O(n)");
        return p;
    }

    private CodingProblem maximumSubarray() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Maximum Subarray (Kadane's Algorithm)");
        p.setTopic("Dynamic Programming");
        p.setDifficulty(Difficulty.MEDIUM);
        p.setTags("arrays,dynamic-programming,kadane");
        p.setPatternHint("""
                Signal to watch for: "contiguous subarray" + "maximum/minimum sum." Whenever the word \
                "contiguous" appears with an optimization goal, think Kadane's running-sum trick before \
                reaching for anything more complex.""");
        p.setCommonMistakes("""
                Initializing maxSoFar/currentMax to 0 instead of nums[0] (breaks on all-negative \
                arrays); resetting currentMax to 0 instead of to nums[i] when restarting; forgetting \
                the problem allows negative numbers, which is the entire point of the question.""");
        p.setCompanyTags("Common medium-tier DP warm-up question in technical phone screens.");
        p.setDescription("""
                Given an integer array nums, find the contiguous subarray with the largest sum and \
                return that sum.""");
        p.setSolutionCode("""
                class Solution {
                    public int maxSubArray(int[] nums) {
                        int maxSoFar = nums[0];
                        int currentMax = nums[0];

                        for (int i = 1; i < nums.length; i++) {
                            currentMax = Math.max(nums[i], currentMax + nums[i]);
                            maxSoFar = Math.max(maxSoFar, currentMax);
                        }
                        return maxSoFar;
                    }
                }""");
        p.setExplanation("""
                At every index, there are only two sensible choices: extend the best subarray ending at \
                the previous index by adding the current number, or abandon it and start a brand-new \
                subarray here. Kadane's trick is realizing the running sum should be discarded the \
                instant it goes negative, because no negative prefix can ever help a future subarray.

                Interviewer's angle: this question screens for dynamic-programming intuition even \
                though nothing about the problem statement looks like DP at first glance. A strong \
                candidate explicitly says "this is choosing between extend vs. restart at each step" \
                rather than just reciting the formula from memory.""");
        p.setTimeComplexity("O(n)");
        p.setSpaceComplexity("O(1)");
        return p;
    }

    private CodingProblem missingNumber() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Missing Number");
        p.setTopic("Math / Bit Manipulation");
        p.setDifficulty(Difficulty.EASY);
        p.setTags("math,bit-manipulation,aptitude");
        p.setPatternHint("""
                Signal to watch for: a range of consecutive numbers with exactly one missing or \
                duplicated. That pattern almost always has a math-formula or XOR shortcut that avoids \
                sorting or extra memory entirely.""");
        p.setCommonMistakes("""
                Using long/BigInteger unnecessarily (or conversely, overflowing int on very large n in \
                other languages - less of a concern in Java's int range here, but worth mentioning out \
                loud); sorting the array first when it isn't necessary at all.""");
        p.setCompanyTags("Common in aptitude/screening rounds before the technical interview.");
        p.setDescription("""
                Given an array containing n distinct numbers taken from the range [0, n], find the one \
                number in that range that is missing from the array.""");
        p.setSolutionCode("""
                class Solution {
                    public int missingNumber(int[] nums) {
                        int n = nums.length;
                        int expectedSum = n * (n + 1) / 2;
                        int actualSum = 0;
                        for (int num : nums) {
                            actualSum += num;
                        }
                        return expectedSum - actualSum;
                    }

                    // Alternative trick using XOR, in case the interviewer asks for a second approach:
                    public int missingNumberXor(int[] nums) {
                        int result = nums.length;
                        for (int i = 0; i < nums.length; i++) {
                            result ^= i ^ nums[i];
                        }
                        return result;
                    }
                }""");
        p.setExplanation("""
                The Gauss sum formula n*(n+1)/2 gives the sum of every integer from 0 to n if nothing \
                were missing. Subtract the array's actual sum and whatever is left over is the missing \
                number - no sorting, no extra memory. The XOR variant works because a number XORed with \
                itself cancels to zero, so XOR-ing every index and every array value together leaves \
                only the unmatched (missing) value standing.

                Interviewer's angle: this is a classic aptitude-round question used before the \
                technical round, specifically to test basic number-theory intuition under time \
                pressure. A common, telling follow-up: "can you solve it without the sum formula?" - \
                which is exactly why the XOR trick is included above.""");
        p.setTimeComplexity("O(n)");
        p.setSpaceComplexity("O(1)");
        return p;
    }

    private CodingProblem linkedListCycle() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Linked List Cycle Detection");
        p.setTopic("Two Pointers");
        p.setDifficulty(Difficulty.MEDIUM);
        p.setTags("linked-list,two-pointers,floyd");
        p.setPatternHint("""
                Signal to watch for: linked list + "does it loop / repeat / cycle back?" Any question \
                about detecting a cycle, finding the middle, or finding the Kth-from-end node on a \
                singly linked list is solved with two pointers moving at different speeds.""");
        p.setCommonMistakes("""
                Checking only fast != null and forgetting fast.next != null (causes a \
                NullPointerException on fast.next.next); comparing node values instead of node \
                references (duplicate values can exist without an actual cycle).""");
        p.setCompanyTags("Common medium-tier linked-list question testing pointer manipulation fluency.");
        p.setDescription("""
                Given the head of a singly linked list, determine whether the list contains a cycle \
                (some node's next pointer eventually loops back to an earlier node).""");
        p.setSolutionCode("""
                class ListNode {
                    int val;
                    ListNode next;
                    ListNode(int val) {
                        this.val = val;
                    }
                }

                class Solution {
                    public boolean hasCycle(ListNode head) {
                        ListNode slow = head;
                        ListNode fast = head;

                        while (fast != null && fast.next != null) {
                            slow = slow.next;
                            fast = fast.next.next;
                            if (slow == fast) {
                                return true;
                            }
                        }
                        return false;
                    }
                }""");
        p.setExplanation("""
                Known as Floyd's Tortoise and Hare: one pointer moves one node at a time, the other \
                moves two. If there is no cycle, the fast pointer simply reaches the end (null) first. \
                If there is a cycle, the fast pointer eventually "laps" the slow one from behind and \
                they land on the exact same node - which is only possible if a cycle exists.

                Interviewer's angle: this trick is nearly impossible to invent live under pressure, so \
                interviewers mainly use it to check whether you already know the named technique - and, \
                more importantly, whether you can explain *why* the two pointers must eventually \
                collide rather than just reciting the two-pointer setup from memory.""");
        p.setTimeComplexity("O(n)");
        p.setSpaceComplexity("O(1)");
        return p;
    }

    private CodingProblem mergeIntervals() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Merge Intervals");
        p.setTopic("Sorting / Greedy");
        p.setDifficulty(Difficulty.MEDIUM);
        p.setTags("arrays,sorting,greedy,intervals");
        p.setPatternHint("""
                Signal to watch for: the word "interval," "overlap," "schedule," or "meeting rooms." \
                Any time ranges with start/end pairs are involved, sort by start time first - almost \
                every interval problem becomes a single greedy pass after that.""");
        p.setCommonMistakes("""
                Forgetting to sort first (the merge logic silently produces wrong results on unsorted \
                input instead of crashing, which makes this bug easy to miss); comparing against the \
                wrong previous interval instead of the last one actually merged so far.""");
        p.setCompanyTags("Common medium-tier question; the interval family recurs often in scheduling-style interview problems.");
        p.setDescription("""
                Given an array of intervals where each interval is [start, end], merge all overlapping \
                intervals and return the resulting non-overlapping intervals, sorted by start time.""");
        p.setSolutionCode("""
                import java.util.ArrayList;
                import java.util.Arrays;
                import java.util.List;

                class Solution {
                    public int[][] merge(int[][] intervals) {
                        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
                        List<int[]> merged = new ArrayList<>();

                        for (int[] interval : intervals) {
                            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                                merged.add(interval);
                            } else {
                                merged.get(merged.size() - 1)[1] =
                                        Math.max(merged.get(merged.size() - 1)[1], interval[1]);
                            }
                        }
                        return merged.toArray(new int[merged.size()][]);
                    }
                }""");
        p.setExplanation("""
                Sorting by start time is the key pre-processing step: once sorted, any two intervals \
                that overlap are guaranteed to be adjacent to each other in the sorted order. That means \
                a single left-to-right pass is enough - at each interval, either it overlaps the most \
                recently merged interval (extend its end) or it starts a brand-new group.

                Interviewer's angle: nearly every "interval" family question (meeting rooms, insert \
                interval, employee free time) becomes tractable once you sort first. Interviewers use \
                this question to see whether sorting-as-preprocessing is already part of your instinct, \
                since trying to solve interval problems on unsorted data gets complicated fast.""");
        p.setTimeComplexity("O(n log n) for the sort, O(n) for the merge pass");
        p.setSpaceComplexity("O(n)");
        return p;
    }

    private CodingProblem medianOfTwoSortedArrays() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Median of Two Sorted Arrays");
        p.setTopic("Binary Search");
        p.setDifficulty(Difficulty.HARD);
        p.setTags("arrays,binary-search,divide-and-conquer");
        p.setPatternHint("""
                Signal to watch for: "sorted array(s)" + a required complexity better than O(n) (here \
                O(log n)). Any time a question explicitly demands sub-linear time on sorted data, binary \
                search on the answer (not just on values) is usually the intended approach.""");
        p.setCommonMistakes("""
                Trying to actually merge the arrays (meets correctness but fails the required \
                complexity); getting the partition boundary conditions wrong (off-by-one on \
                partition == 0 or partition == length); not handling odd vs. even combined length \
                separately for the final median calculation.""");
        p.setCompanyTags("Classic hard-tier binary-search question in senior/staff-level interview loops.");
        p.setDescription("""
                You are given two sorted integer arrays, nums1 (length m) and nums2 (length n). \
                Find the median of the combined, logically merged sorted array.

                A brute-force approach merges both arrays and reads the middle element(s) in O(m + n) \
                time. The harder, intended approach must run in O(log(min(m, n))) time by using binary \
                search instead of merging.""");
        p.setSolutionCode("""
                class Solution {
                    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
                        if (nums1.length > nums2.length) {
                            return findMedianSortedArrays(nums2, nums1);
                        }
                        int m = nums1.length, n = nums2.length;
                        int low = 0, high = m;

                        while (low <= high) {
                            int partition1 = (low + high) / 2;
                            int partition2 = (m + n + 1) / 2 - partition1;

                            int maxLeft1 = (partition1 == 0) ? Integer.MIN_VALUE : nums1[partition1 - 1];
                            int minRight1 = (partition1 == m) ? Integer.MAX_VALUE : nums1[partition1];

                            int maxLeft2 = (partition2 == 0) ? Integer.MIN_VALUE : nums2[partition2 - 1];
                            int minRight2 = (partition2 == n) ? Integer.MAX_VALUE : nums2[partition2];

                            if (maxLeft1 <= minRight2 && maxLeft2 <= minRight1) {
                                if ((m + n) % 2 == 0) {
                                    return (Math.max(maxLeft1, maxLeft2) + Math.min(minRight1, minRight2)) / 2.0;
                                }
                                return Math.max(maxLeft1, maxLeft2);
                            } else if (maxLeft1 > minRight2) {
                                high = partition1 - 1;
                            } else {
                                low = partition1 + 1;
                            }
                        }
                        throw new IllegalArgumentException("Input arrays are not sorted.");
                    }
                }""");
        p.setExplanation("""
                Binary search on the smaller array for a "partition" point such that every element to \
                the left of the partition (across both arrays combined) is <= every element to the \
                right. Each partition choice in nums1 determines the matching partition in nums2 via \
                the combined length, so only one binary search is needed. Once the partition satisfies \
                maxLeft1 <= minRight2 and maxLeft2 <= minRight1, the median is directly computable from \
                the four boundary values, with no merging required.""");
        p.setTimeComplexity("O(log(min(m, n)))");
        p.setSpaceComplexity("O(1)");
        return p;
    }

    private CodingProblem trappingRainWater() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Trapping Rain Water");
        p.setTopic("Two Pointers");
        p.setDifficulty(Difficulty.HARD);
        p.setTags("arrays,two-pointers,greedy");
        p.setPatternHint("""
                Signal to watch for: "water trapped between bars," or more generally, "the answer at \
                each position depends on the best value to its left AND to its right." That dual-\
                direction dependency is the signature of an inward two-pointer sweep.""");
        p.setCommonMistakes("""
                Precomputing both leftMax and rightMax arrays first (correct, but burns O(n) extra \
                space the two-pointer version avoids); moving the wrong pointer (the trick only works if \
                you advance the side with the smaller height); off-by-one at the array boundaries.""");
        p.setCompanyTags("Classic hard-tier two-pointer question in senior interview loops.");
        p.setDescription("""
                Given an array of non-negative integers representing an elevation map where the width \
                of each bar is 1, compute how much water it can trap after raining.""");
        p.setSolutionCode("""
                class Solution {
                    public int trap(int[] height) {
                        if (height == null || height.length == 0) {
                            return 0;
                        }
                        int left = 0, right = height.length - 1;
                        int leftMax = 0, rightMax = 0;
                        int water = 0;

                        while (left < right) {
                            if (height[left] <= height[right]) {
                                leftMax = Math.max(leftMax, height[left]);
                                water += leftMax - height[left];
                                left++;
                            } else {
                                rightMax = Math.max(rightMax, height[right]);
                                water += rightMax - height[right];
                                right--;
                            }
                        }
                        return water;
                    }
                }""");
        p.setExplanation("""
                The water trapped above any bar is bounded by the shorter of the tallest bar to its \
                left and the tallest bar to its right. Rather than precomputing both max arrays (which \
                costs O(n) extra space), walk two pointers inward from both ends. Whichever side \
                currently has the smaller height is the side whose running max is guaranteed to be the \
                limiting wall, so water trapped at that position can be resolved immediately before \
                advancing that pointer.""");
        p.setTimeComplexity("O(n)");
        p.setSpaceComplexity("O(1)");
        return p;
    }

    private CodingProblem nQueens() {
        CodingProblem p = new CodingProblem();
        p.setTitle("N-Queens");
        p.setTopic("Backtracking");
        p.setDifficulty(Difficulty.HARD);
        p.setTags("backtracking,recursion,matrix");
        p.setPatternHint("""
                Signal to watch for: "place/arrange items under constraints" + "return all valid \
                configurations." Whenever you need every valid arrangement (not just one), and invalid \
                partial states can be detected early, that's backtracking with pruning.""");
        p.setCommonMistakes("""
                Checking all three attack directions (row, column, diagonal) only at the end instead of \
                pruning early, which makes the search far slower than necessary; getting the diagonal \
                check formula wrong (it's an absolute-value comparison, not a sign comparison).""");
        p.setCompanyTags("Classic hard-tier backtracking question in senior/staff-level interview loops.");
        p.setDescription("""
                Place n chess queens on an n x n board so that no two queens attack each other (no \
                shared row, column, or diagonal). Return all distinct board configurations, each \
                represented as a list of strings where 'Q' marks a queen and '.' marks an empty cell.""");
        p.setSolutionCode("""
                import java.util.ArrayList;
                import java.util.Arrays;
                import java.util.List;

                class Solution {
                    public List<List<String>> solveNQueens(int n) {
                        List<List<String>> results = new ArrayList<>();
                        int[] queens = new int[n];
                        backtrack(0, n, queens, results);
                        return results;
                    }

                    private void backtrack(int row, int n, int[] queens, List<List<String>> results) {
                        if (row == n) {
                            results.add(buildBoard(queens, n));
                            return;
                        }
                        for (int col = 0; col < n; col++) {
                            if (isValid(queens, row, col)) {
                                queens[row] = col;
                                backtrack(row + 1, n, queens, results);
                            }
                        }
                    }

                    private boolean isValid(int[] queens, int row, int col) {
                        for (int r = 0; r < row; r++) {
                            int c = queens[r];
                            if (c == col || Math.abs(c - col) == Math.abs(r - row)) {
                                return false;
                            }
                        }
                        return true;
                    }

                    private List<String> buildBoard(int[] queens, int n) {
                        List<String> board = new ArrayList<>();
                        for (int row = 0; row < n; row++) {
                            char[] line = new char[n];
                            Arrays.fill(line, '.');
                            line[queens[row]] = 'Q';
                            board.add(new String(line));
                        }
                        return board;
                    }
                }""");
        p.setExplanation("""
                Place queens one row at a time. Before placing a queen in a column, isValid checks \
                every previously placed queen for a column clash or a diagonal clash (the classic trick: \
                two cells share a diagonal exactly when the absolute difference of their rows equals the \
                absolute difference of their columns). If no earlier queen conflicts, recurse into the \
                next row; if a full board is reached, record it. Backtracking naturally prunes invalid \
                branches early instead of generating and checking every permutation.""");
        p.setTimeComplexity("O(n!) in the worst case");
        p.setSpaceComplexity("O(n) for the recursion stack and queens array");
        return p;
    }

    private CodingProblem longestValidParentheses() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Longest Valid Parentheses");
        p.setTopic("Stack");
        p.setDifficulty(Difficulty.HARD);
        p.setTags("strings,stack");
        p.setPatternHint("""
                Signal to watch for: "longest valid/matching substring" rather than just "is it valid." \
                Whenever you need the *length* of the best matching run rather than a true/false answer, \
                track indices on the stack instead of characters, so you can measure distances.""");
        p.setCommonMistakes("""
                Pushing characters instead of indices (makes it impossible to measure substring length); \
                forgetting the -1 sentinel base case, which breaks the very first valid run in the \
                string; not re-pushing the current index as a new base after the stack empties.""");
        p.setCompanyTags("Hard-tier stack question, a step up from the classic Valid Parentheses warm-up.");
        p.setDescription("""
                Given a string containing only '(' and ')', find the length of the longest substring \
                that forms a valid (correctly matched) sequence of parentheses.""");
        p.setSolutionCode("""
                import java.util.ArrayDeque;
                import java.util.Deque;

                class Solution {
                    public int longestValidParentheses(String s) {
                        int maxLen = 0;
                        Deque<Integer> stack = new ArrayDeque<>();
                        stack.push(-1);

                        for (int i = 0; i < s.length(); i++) {
                            char c = s.charAt(i);
                            if (c == '(') {
                                stack.push(i);
                            } else {
                                stack.pop();
                                if (stack.isEmpty()) {
                                    stack.push(i);
                                } else {
                                    maxLen = Math.max(maxLen, i - stack.peek());
                                }
                            }
                        }
                        return maxLen;
                    }
                }""");
        p.setExplanation("""
                The stack holds indices, not characters. Pushing -1 first gives a sentinel "base" index \
                for when the very first characters form a valid run. On '(' push its index. On ')', pop \
                once to cancel the most recent unmatched '('; if the stack becomes empty, this ')' itself \
                becomes the new base (nothing before it can connect to anything after), otherwise the gap \
                between the current index and the new top of stack is the length of a valid run ending \
                here.""");
        p.setTimeComplexity("O(n)");
        p.setSpaceComplexity("O(n)");
        return p;
    }

    private CodingProblem wordLadder() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Word Ladder");
        p.setTopic("Breadth-First Search");
        p.setDifficulty(Difficulty.HARD);
        p.setTags("graphs,bfs,strings");
        p.setPatternHint("""
                Signal to watch for: "shortest transformation/sequence/path" between two states, where \
                each step has a defined set of valid "neighbor" moves. Any "shortest number of steps" \
                question on an unweighted, implicit graph is BFS, level by level.""");
        p.setCommonMistakes("""
                Using DFS instead of BFS (finds *a* path, not the *shortest* one); regenerating all \
                possible neighbor words by comparing against the entire word list instead of mutating \
                each character position (far slower); forgetting to mark words visited, causing \
                infinite re-queueing.""");
        p.setCompanyTags("Classic hard-tier graph/BFS question in senior interview loops.");
        p.setDescription("""
                Given a beginWord, an endWord, and a dictionary wordList, find the length of the \
                shortest transformation sequence from beginWord to endWord, changing exactly one letter \
                at a time, where every intermediate word must exist in wordList. Return 0 if no such \
                sequence exists.""");
        p.setSolutionCode("""
                import java.util.HashSet;
                import java.util.LinkedList;
                import java.util.List;
                import java.util.Queue;
                import java.util.Set;

                class Solution {
                    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
                        Set<String> dict = new HashSet<>(wordList);
                        if (!dict.contains(endWord)) {
                            return 0;
                        }

                        Queue<String> queue = new LinkedList<>();
                        queue.offer(beginWord);
                        Set<String> visited = new HashSet<>();
                        visited.add(beginWord);

                        int level = 1;
                        while (!queue.isEmpty()) {
                            int size = queue.size();
                            for (int i = 0; i < size; i++) {
                                String word = queue.poll();
                                if (word.equals(endWord)) {
                                    return level;
                                }
                                char[] chars = word.toCharArray();
                                for (int j = 0; j < chars.length; j++) {
                                    char original = chars[j];
                                    for (char c = 'a'; c <= 'z'; c++) {
                                        if (c == original) {
                                            continue;
                                        }
                                        chars[j] = c;
                                        String next = new String(chars);
                                        if (dict.contains(next) && !visited.contains(next)) {
                                            visited.add(next);
                                            queue.offer(next);
                                        }
                                    }
                                    chars[j] = original;
                                }
                            }
                            level++;
                        }
                        return 0;
                    }
                }""");
        p.setExplanation("""
                Treat each word as a node and an edge exists between two words that differ by exactly \
                one letter. The shortest transformation sequence is then just the shortest path in this \
                unweighted graph, which BFS finds level by level. Rather than precomputing all edges \
                (expensive), each word's neighbors are generated on the fly by trying every letter in \
                every position and checking dictionary membership.""");
        p.setTimeComplexity("O(N * L * 26), N = word count, L = word length");
        p.setSpaceComplexity("O(N * L)");
        return p;
    }

    private CodingProblem lruCache() {
        CodingProblem p = new CodingProblem();
        p.setTitle("LRU Cache (Design)");
        p.setTopic("Design / Hash Map + Doubly Linked List");
        p.setDifficulty(Difficulty.HARD);
        p.setTags("design,hash-map,linked-list,senior-level");
        p.setDescription("""
                Design a data structure for a Least Recently Used (LRU) cache that supports get(key) \
                and put(key, value), both in O(1) time. get returns the value or -1 if the key is \
                absent, and marks the key as recently used. put inserts or updates a key; if the cache \
                is at capacity, it must evict the least recently used entry before inserting.""");
        p.setSolutionCode("""
                import java.util.HashMap;
                import java.util.Map;

                class LRUCache {
                    private static class Node {
                        int key, value;
                        Node prev, next;
                        Node(int key, int value) {
                            this.key = key;
                            this.value = value;
                        }
                    }

                    private final int capacity;
                    private final Map<Integer, Node> cache = new HashMap<>();
                    private final Node head = new Node(0, 0);
                    private final Node tail = new Node(0, 0);

                    public LRUCache(int capacity) {
                        this.capacity = capacity;
                        head.next = tail;
                        tail.prev = head;
                    }

                    public int get(int key) {
                        Node node = cache.get(key);
                        if (node == null) {
                            return -1;
                        }
                        moveToFront(node);
                        return node.value;
                    }

                    public void put(int key, int value) {
                        Node node = cache.get(key);
                        if (node != null) {
                            node.value = value;
                            moveToFront(node);
                            return;
                        }
                        if (cache.size() == capacity) {
                            Node lru = tail.prev;
                            remove(lru);
                            cache.remove(lru.key);
                        }
                        Node newNode = new Node(key, value);
                        cache.put(key, newNode);
                        addToFront(newNode);
                    }

                    private void moveToFront(Node node) {
                        remove(node);
                        addToFront(node);
                    }

                    private void remove(Node node) {
                        node.prev.next = node.next;
                        node.next.prev = node.prev;
                    }

                    private void addToFront(Node node) {
                        node.next = head.next;
                        node.prev = head;
                        head.next.prev = node;
                        head.next = node;
                    }
                }""");
        p.setExplanation("""
                A HashMap alone gives O(1) lookup but no cheap way to track usage order. A doubly \
                linked list alone gives O(1) reordering but no cheap way to find a node by key. \
                Combining them solves both: the map finds any node instantly, and the list maintains \
                order so the most-recently-used node always sits right after a sentinel head, and the \
                least-recently-used node always sits right before a sentinel tail - making eviction a \
                simple "remove tail.prev."

                Interviewer's angle: this is one of the most common "design a data structure" \
                questions at the senior level, specifically because it requires combining two \
                structures rather than picking one off the shelf. Interviewers watch for whether you \
                reach for sentinel head/tail nodes (avoiding null-check edge cases) versus writing \
                fragile linked-list logic with lots of special cases.""");
        p.setTimeComplexity("O(1) for both get and put");
        p.setSpaceComplexity("O(capacity)");
        p.setPatternHint("""
                Signal to watch for: "design a cache/structure with O(1) operations" plus some notion \
                of ordering (recency, frequency, insertion order). That combination of "O(1) lookup" + \
                "O(1) reordering" is the signature of HashMap + Doubly Linked List.""");
        p.setCommonMistakes("""
                Forgetting to update links in BOTH directions when removing a node from the list; \
                forgetting that get() must also count as a "use" and move the node to the front, not \
                just put(); checking cache.size() == capacity at the wrong point relative to inserting \
                the new node; not using sentinel head/tail nodes, which forces awkward null checks.""");
        p.setCompanyTags("One of the most frequently cited senior/staff-level design questions at large tech companies.");
        return p;
    }

    private CodingProblem serializeDeserializeBinaryTree() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Serialize and Deserialize Binary Tree");
        p.setTopic("Trees / Depth-First Search");
        p.setDifficulty(Difficulty.HARD);
        p.setTags("trees,dfs,design,senior-level");
        p.setDescription("""
                Design an algorithm to serialize a binary tree into a string, and deserialize that \
                string back into a tree with the exact same structure and values.""");
        p.setSolutionCode("""
                import java.util.ArrayDeque;
                import java.util.Arrays;
                import java.util.Deque;

                class TreeNode {
                    int val;
                    TreeNode left, right;
                    TreeNode(int val) {
                        this.val = val;
                    }
                }

                class Codec {
                    public String serialize(TreeNode root) {
                        StringBuilder sb = new StringBuilder();
                        serializeHelper(root, sb);
                        return sb.toString();
                    }

                    private void serializeHelper(TreeNode node, StringBuilder sb) {
                        if (node == null) {
                            sb.append("#,");
                            return;
                        }
                        sb.append(node.val).append(",");
                        serializeHelper(node.left, sb);
                        serializeHelper(node.right, sb);
                    }

                    public TreeNode deserialize(String data) {
                        Deque<String> values = new ArrayDeque<>(Arrays.asList(data.split(",")));
                        return deserializeHelper(values);
                    }

                    private TreeNode deserializeHelper(Deque<String> values) {
                        String value = values.poll();
                        if (value.equals("#")) {
                            return null;
                        }
                        TreeNode node = new TreeNode(Integer.parseInt(value));
                        node.left = deserializeHelper(values);
                        node.right = deserializeHelper(values);
                        return node;
                    }
                }""");
        p.setExplanation("""
                A pre-order traversal (root, then left, then right) with an explicit "#" marker for \
                every null child fully encodes the tree's shape, not just its values. Because \
                deserialization walks the same tokens in the same root-left-right order, it can rebuild \
                the exact structure without needing brackets or indices - every null is explicitly \
                recorded, so there's never any ambiguity about where a subtree ends.

                Interviewer's angle: a frequent senior-level "design and code" hybrid question. The key \
                thing interviewers probe for is whether you understand *why* pre-order with null \
                markers is unambiguous, while plain in-order traversal is not enough information to \
                reconstruct an arbitrary tree.""");
        p.setTimeComplexity("O(n) for both serialize and deserialize");
        p.setSpaceComplexity("O(n)");
        p.setPatternHint("""
                Signal to watch for: "convert a structure to a string and back" where the structure's \
                shape itself matters, not just the values. Use a traversal order (pre-order is simplest) \
                plus explicit markers for missing children, so the encoding is unambiguous to reverse.""");
        p.setCommonMistakes("""
                Using in-order traversal (it cannot uniquely reconstruct a tree without extra \
                information); forgetting the null/sentinel marker, which makes the string ambiguous \
                about where branches end; off-by-one errors when splitting and consuming the \
                comma-separated token stream.""");
        p.setCompanyTags("Common senior-level whiteboard question combining tree traversal with string design.");
        return p;
    }

    private CodingProblem slidingWindowMaximum() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Sliding Window Maximum");
        p.setTopic("Monotonic Deque / Sliding Window");
        p.setDifficulty(Difficulty.HARD);
        p.setTags("arrays,deque,sliding-window,senior-level");
        p.setDescription("""
                Given an array nums and a window size k, return an array containing the maximum value \
                in every contiguous window of size k as the window slides from the start of the array \
                to the end.""");
        p.setSolutionCode("""
                import java.util.ArrayDeque;
                import java.util.Deque;

                class Solution {
                    public int[] maxSlidingWindow(int[] nums, int k) {
                        int n = nums.length;
                        int[] result = new int[n - k + 1];
                        Deque<Integer> deque = new ArrayDeque<>();

                        for (int i = 0; i < n; i++) {
                            while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                                deque.pollFirst();
                            }
                            while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
                                deque.pollLast();
                            }
                            deque.offerLast(i);
                            if (i >= k - 1) {
                                result[i - k + 1] = nums[deque.peekFirst()];
                            }
                        }
                        return result;
                    }
                }""");
        p.setExplanation("""
                The deque stores indices, kept in strictly decreasing order of their values. Any index \
                whose value is smaller than one already in the deque can never become the window \
                maximum before that larger value leaves the window, so it is safe to discard \
                immediately. This keeps the front of the deque as the current window's maximum at all \
                times, and every index enters and leaves the deque at most once.

                Interviewer's angle: candidates almost always suggest a max-heap first; the real signal \
                is recognizing why a heap falls short here - removing an *expired* (out-of-window) \
                element from a heap is not O(1) because it isn't necessarily at the top, whereas the \
                monotonic deque handles expiry from the front in O(1).""");
        p.setTimeComplexity("O(n)");
        p.setSpaceComplexity("O(k)");
        p.setPatternHint("""
                Signal to watch for: "maximum/minimum value in every sliding window of size k." This \
                exact phrase is the signature of a monotonic deque - not a heap, specifically because \
                old elements need to expire from one end efficiently.""");
        p.setCommonMistakes("""
                Reaching for a max-heap/PriorityQueue (works but can't cheaply remove expired entries); \
                forgetting to evict expired indices from the front before reading the current max; \
                starting to record results before the window is actually full (i >= k - 1).""");
        p.setCompanyTags("Common hard-tier array/deque question in senior-level interview loops.");
        return p;
    }

    private CodingProblem courseSchedule() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Course Schedule (Topological Sort)");
        p.setTopic("Graphs / Topological Sort");
        p.setDifficulty(Difficulty.HARD);
        p.setTags("graphs,topological-sort,senior-level");
        p.setDescription("""
                There are numCourses courses labeled 0 to numCourses - 1. Given a list of prerequisite \
                pairs [a, b] meaning course b must be completed before course a, determine whether it \
                is possible to finish all courses - equivalently, whether the prerequisite graph \
                contains no cycle.""");
        p.setSolutionCode("""
                import java.util.ArrayList;
                import java.util.LinkedList;
                import java.util.List;
                import java.util.Queue;

                class Solution {
                    public boolean canFinish(int numCourses, int[][] prerequisites) {
                        List<List<Integer>> graph = new ArrayList<>();
                        int[] inDegree = new int[numCourses];
                        for (int i = 0; i < numCourses; i++) {
                            graph.add(new ArrayList<>());
                        }
                        for (int[] pre : prerequisites) {
                            int course = pre[0], prereq = pre[1];
                            graph.get(prereq).add(course);
                            inDegree[course]++;
                        }

                        Queue<Integer> queue = new LinkedList<>();
                        for (int i = 0; i < numCourses; i++) {
                            if (inDegree[i] == 0) {
                                queue.offer(i);
                            }
                        }

                        int visited = 0;
                        while (!queue.isEmpty()) {
                            int course = queue.poll();
                            visited++;
                            for (int next : graph.get(course)) {
                                inDegree[next]--;
                                if (inDegree[next] == 0) {
                                    queue.offer(next);
                                }
                            }
                        }
                        return visited == numCourses;
                    }
                }""");
        p.setExplanation("""
                This is Kahn's Algorithm for topological sorting. Courses with zero prerequisites \
                (in-degree 0) can always be taken first; "completing" a course reduces the in-degree of \
                everything that depended on it, potentially freeing up new courses to take. If every \
                course is eventually reached and counted, the graph has no cycle; if some courses are \
                never reached, they are stuck in a cycle of mutual dependency and can never be \
                scheduled.

                Interviewer's angle: this question is conceptually identical to real dependency \
                resolution in build systems and package managers, which interviewers often point out as \
                a follow-up. The real signal is whether you reach for in-degree counting (Kahn's, BFS) \
                or DFS with a recursion-stack cycle check, rather than trying to simulate course-taking \
                directly.""");
        p.setTimeComplexity("O(V + E)");
        p.setSpaceComplexity("O(V + E)");
        p.setPatternHint("""
                Signal to watch for: "must complete X before Y," "task ordering," or "dependency \
                resolution." Any time there's a directed "must come before" relationship and you need \
                to know if a valid order exists, that's topological sort / cycle detection in a directed \
                graph.""");
        p.setCommonMistakes("""
                Building the edge direction backwards (course -> prerequisite instead of prerequisite \
                -> course), which silently inverts the whole algorithm; checking only whether the queue \
                became empty instead of checking visited == numCourses; forgetting that courses can \
                form several independent, disconnected dependency chains.""");
        p.setCompanyTags("Classic graph/dependency-resolution question, common in senior-level interview loops.");
        return p;
    }

    private CodingProblem outputIntegerCaching() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Output Prediction: Integer Caching Gotcha");
        p.setTopic("Output Prediction");
        p.setDifficulty(Difficulty.EASY);
        p.setTags("output-prediction,java-fundamentals,autoboxing,aptitude");
        p.setDescription("""
                What does the following Java code print?

                public class Main {
                    public static void main(String[] args) {
                        Integer a = 127;
                        Integer b = 127;
                        Integer c = 200;
                        Integer d = 200;
                        System.out.println(a == b);
                        System.out.println(c == d);
                    }
                }""");
        p.setSolutionCode("""
                true
                false""");
        p.setExplanation("""
                Java caches boxed Integer objects for values from -128 to 127 (via Integer.valueOf's
                internal cache) to avoid creating millions of tiny redundant objects for common small
                values. 'a' and 'b' are both autoboxed 127, so they point to the exact same cached
                object - '==' compares references, and it's true. 'c' and 'd' are 200, outside the
                cache range, so each autoboxing creates a brand-new Integer object - '==' is false even
                though .equals() would return true for both pairs.""");
        p.setPatternHint("""
                Signal to watch for: comparing boxed wrapper types (Integer, Long, etc.) with '=='
                instead of .equals(). Any time you see '==' on an object type rather than a primitive,
                ask whether it's actually comparing references, not values - and whether the value
                happens to fall inside Java's small-integer cache range.""");
        p.setCommonMistakes("""
                Assuming '==' on Integer always works like '==' on int (it doesn't - Integer is an
                object); assuming the cache range is unlimited (it's only -128 to 127 by default);
                using '==' on wrapper types in real code instead of .equals(), which causes rare,
                hard-to-reproduce bugs only for values outside the cache range.""");
        p.setCompanyTags("Extremely common Java fundamentals question in aptitude and campus placement rounds.");
        return p;
    }

    private CodingProblem outputStringPool() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Output Prediction: String Pool vs new String()");
        p.setTopic("Output Prediction");
        p.setDifficulty(Difficulty.EASY);
        p.setTags("output-prediction,java-fundamentals,strings,aptitude");
        p.setDescription("""
                What does the following Java code print?

                public class Main {
                    public static void main(String[] args) {
                        String s1 = "hello";
                        String s2 = "hello";
                        String s3 = new String("hello");
                        System.out.println(s1 == s2);
                        System.out.println(s1 == s3);
                        System.out.println(s1.equals(s3));
                    }
                }""");
        p.setSolutionCode("""
                true
                false
                true""");
        p.setExplanation("""
                String literals are automatically interned into a shared "string pool" - so s1 and s2,
                both written as the literal "hello", point to the exact same pooled object, making '=='
                true. new String("hello") explicitly forces a brand-new object on the heap, which is
                never automatically the same reference as the pooled literal, so s1 == s3 is false.
                .equals() compares actual character content, not references, so it's true regardless.""");
        p.setPatternHint("""
                Signal to watch for: string comparison using '==' instead of .equals(), especially
                combined with 'new String(...)'. String literals share pooled instances; explicitly
                constructed strings never automatically do.""");
        p.setCommonMistakes("""
                Assuming all equal-content strings are '==' equal (only true for literals from the
                pool); forgetting that 'new String(...)' deliberately opts out of pooling; using '=='
                for string comparison in real code, which works by accident most of the time and then
                fails mysteriously.""");
        p.setCompanyTags("Extremely common Java fundamentals question in aptitude and campus placement rounds.");
        return p;
    }

    private CodingProblem outputIncrementPrecedence() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Output Prediction: Pre/Post-Increment Precedence");
        p.setTopic("Output Prediction");
        p.setDifficulty(Difficulty.MEDIUM);
        p.setTags("output-prediction,java-fundamentals,operators,aptitude");
        p.setDescription("""
                What does the following Java code print?

                public class Main {
                    public static void main(String[] args) {
                        int x = 5;
                        int y = x++ + ++x;
                        System.out.println(x + " " + y);
                    }
                }""");
        p.setSolutionCode("""
                7 12""");
        p.setExplanation("""
                Walk it step by step. x starts at 5. 'x++' (post-increment) evaluates to the CURRENT
                value, 5, and only afterward bumps x to 6. Then '++x' (pre-increment) bumps x to 7
                first and evaluates to the NEW value, 7. So y = 5 + 7 = 12, and by the time printing
                happens, x has been incremented twice from its original 5, ending at 7.""");
        p.setPatternHint("""
                Signal to watch for: the same variable used with both ++ and a pre/post mix in a single
                expression. Always separate "what value does this sub-expression evaluate to" from
                "when does the variable's stored value actually change" - post-increment changes the
                variable immediately but returns the old value.""");
        p.setCommonMistakes("""
                Assuming x++ and ++x always behave identically (they only differ in return value, not
                side effect timing); evaluating left-to-right without tracking each increment
                individually; forgetting that both increments apply to the same underlying variable.""");
        p.setCompanyTags("Common operator-precedence trick question in aptitude rounds.");
        return p;
    }

    private CodingProblem outputFinallyOverridesReturn() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Output Prediction: finally Overriding a Return Value");
        p.setTopic("Output Prediction");
        p.setDifficulty(Difficulty.MEDIUM);
        p.setTags("output-prediction,java-fundamentals,exceptions,aptitude");
        p.setDescription("""
                What does the following Java code print?

                public class Main {
                    static int test() {
                        try {
                            return 1;
                        } finally {
                            return 2;
                        }
                    }

                    public static void main(String[] args) {
                        System.out.println(test());
                    }
                }""");
        p.setSolutionCode("""
                2""");
        p.setExplanation("""
                A 'finally' block always runs before a method actually returns - and if that 'finally'
                block itself contains a 'return', it silently discards whatever the try block was
                about to return. The try block's 'return 1' is never actually completed; 'finally's
                'return 2' wins.""");
        p.setPatternHint("""
                Signal to watch for: a 'return' statement appearing inside a 'finally' block. This is
                also a real-world code smell - interviewers use this question both as an output-
                prediction trick and as a chance to ask why this is considered bad practice.""");
        p.setCommonMistakes("""
                Assuming the first 'return' encountered "wins" (it doesn't if a later 'finally' also
                returns); not realizing 'finally' runs even when try returns normally (it's not just
                for exceptions); writing production code with a return inside finally, which silently
                swallows the original intent.""");
        p.setCompanyTags("Classic Java fundamentals trick question in technical/aptitude interviews.");
        return p;
    }

    private CodingProblem outputStaticInitOrder() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Output Prediction: Static Initialization Order");
        p.setTopic("Output Prediction");
        p.setDifficulty(Difficulty.MEDIUM);
        p.setTags("output-prediction,java-fundamentals,static,aptitude");
        p.setDescription("""
                What does the following Java code print, and in what order?

                public class Main {
                    static int a = init("a", 1);
                    static int b;

                    static {
                        b = init("static block", 2);
                    }

                    static int init(String name, int value) {
                        System.out.println("Initializing " + name);
                        return value;
                    }

                    public static void main(String[] args) {
                        System.out.println("main start");
                    }
                }""");
        p.setSolutionCode("""
                Initializing a
                Initializing static block
                main start""");
        p.setExplanation("""
                Static fields and static initializer blocks run in exact TEXTUAL ORDER, top to bottom,
                as they appear in the class - not "all fields first, then all static blocks." Here,
                'static int a = init(...)' appears before the static block, so it runs first; the
                static block runs second; and none of this happens inside main() at all - it all runs
                once, automatically, the first time the class is loaded, before main() is invoked.""");
        p.setPatternHint("""
                Signal to watch for: multiple static fields and static { } blocks mixed together in a
                class. The rule is always "top to bottom, in the order written" - never assume fields
                and blocks are grouped separately.""");
        p.setCommonMistakes("""
                Assuming all static field initializers run before any static block (order is purely
                textual, not grouped by kind); forgetting that static initialization happens once at
                class-loading time, before main(), not when an object is first created.""");
        p.setCompanyTags("Common Java fundamentals question testing class-loading understanding.");
        return p;
    }

    private CodingProblem outputStringConcatenation() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Output Prediction: String Concatenation Operator Order");
        p.setTopic("Output Prediction");
        p.setDifficulty(Difficulty.EASY);
        p.setTags("output-prediction,java-fundamentals,strings,aptitude");
        p.setDescription("""
                What does the following Java code print?

                public class Main {
                    public static void main(String[] args) {
                        System.out.println(1 + 2 + "3");
                        System.out.println("1" + 2 + 3);
                    }
                }""");
        p.setSolutionCode("""
                33
                123""");
        p.setExplanation("""
                The '+' operator is evaluated strictly left to right. Line 1: '1 + 2' happens first,
                and since both are plain ints, that's ordinary integer addition giving 3; THEN '3 +
                "3"' happens, and because one side is now a String, Java switches to string
                concatenation, producing "33". Line 2: '"1" + 2' happens first, and since one side is
                already a String, it's concatenation immediately, giving "12"; then '"12" + 3' appends
                again, giving "123".""");
        p.setPatternHint("""
                Signal to watch for: a chain of '+' operations mixing numbers and strings. Evaluate
                strictly left to right, and the moment a String appears anywhere in a given '+', every
                '+' after that point in the chain becomes concatenation - but anything purely numeric
                before the first String still adds normally.""");
        p.setCommonMistakes("""
                Assuming the entire expression is "number mode" or "string mode" uniformly; evaluating
                right-to-left instead of left-to-right; forgetting that once a String enters the chain,
                everything after it concatenates, not just that one '+'.""");
        p.setCompanyTags("Extremely common Java fundamentals question in aptitude rounds.");
        return p;
    }

    private CodingProblem bugOffByOneArray() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Find the Bug: Off-by-One Array Access");
        p.setTopic("Debugging");
        p.setDifficulty(Difficulty.EASY);
        p.setTags("find-the-bug,arrays,off-by-one,aptitude");
        p.setDescription("""
                The following code is supposed to sum all elements of an array, but it crashes. Find
                and fix the bug.

                public class Main {
                    public static void main(String[] args) {
                        int[] arr = {10, 20, 30, 40, 50};
                        int sum = 0;
                        for (int i = 0; i <= arr.length; i++) {
                            sum += arr[i];
                        }
                        System.out.println(sum);
                    }
                }""");
        p.setSolutionCode("""
                public class Main {
                    public static void main(String[] args) {
                        int[] arr = {10, 20, 30, 40, 50};
                        int sum = 0;
                        for (int i = 0; i < arr.length; i++) {
                            sum += arr[i];
                        }
                        System.out.println(sum);
                    }
                }""");
        p.setExplanation("""
                A 5-element array has valid indices 0 through 4 - 'arr.length' is 5, which is one past
                the last valid index. The loop condition 'i <= arr.length' allows i to reach 5, and
                'arr[5]' throws ArrayIndexOutOfBoundsException. The fix is changing '<=' to '<', so the
                loop stops at i = 4, the last valid index.""");
        p.setPatternHint("""
                Signal to watch for: a loop bound written as 'i <= collection.length' or
                'i <= collection.size()'. Array/list indices are always 0 to length-1, so any loop that
                should visit every element must use strict '<', not '<='.""");
        p.setCommonMistakes("""
                Using '<=' out of habit from math (where ranges are often inclusive on both ends);
                confusing 'length' (count of elements) with 'the last valid index' (which is length -
                1); not noticing the bug until it crashes on specific array sizes.""");
        p.setCompanyTags("Classic off-by-one debugging question in aptitude and screening rounds.");
        return p;
    }

    private CodingProblem bugInfiniteLoop() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Find the Bug: Infinite Loop From a Missing Update");
        p.setTopic("Debugging");
        p.setDifficulty(Difficulty.EASY);
        p.setTags("find-the-bug,loops,aptitude");
        p.setDescription("""
                The following code is supposed to print numbers 0 through 9, but it never finishes.
                Find and fix the bug.

                public class Main {
                    public static void main(String[] args) {
                        int i = 0;
                        while (i < 10) {
                            System.out.println(i);
                        }
                    }
                }""");
        p.setSolutionCode("""
                public class Main {
                    public static void main(String[] args) {
                        int i = 0;
                        while (i < 10) {
                            System.out.println(i);
                            i++;
                        }
                    }
                }""");
        p.setExplanation("""
                The loop condition checks 'i < 10', but nothing inside the loop body ever changes the
                value of 'i'. Since i stays 0 forever, the condition never becomes false, and the loop
                runs forever, printing 0 endlessly. The fix is adding 'i++;' inside the loop body so the
                condition can eventually become false.""");
        p.setPatternHint("""
                Signal to watch for: any loop (while/for) where you can't immediately point to the
                exact line that changes the condition variable. Before trusting any loop, explicitly
                find the line that moves it toward the exit condition.""");
        p.setCommonMistakes("""
                Assuming a for-loop-style update happens automatically in a while loop (it doesn't -
                while loops require you to write the update yourself); placing the update in the wrong
                branch of an if-statement so it only sometimes runs.""");
        p.setCompanyTags("Common debugging/aptitude question testing basic loop control understanding.");
        return p;
    }

    private CodingProblem bugNullPointer() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Find the Bug: NullPointerException From an Unchecked Return");
        p.setTopic("Debugging");
        p.setDifficulty(Difficulty.MEDIUM);
        p.setTags("find-the-bug,null-pointer,aptitude");
        p.setDescription("""
                The following code sometimes crashes with a NullPointerException. Find and fix the bug.

                public class Main {
                    static String getName(boolean found) {
                        if (found) {
                            return "Alice";
                        }
                        return null;
                    }

                    public static void main(String[] args) {
                        String name = getName(false);
                        System.out.println("Name length: " + name.length());
                    }
                }""");
        p.setSolutionCode("""
                public class Main {
                    static String getName(boolean found) {
                        if (found) {
                            return "Alice";
                        }
                        return null;
                    }

                    public static void main(String[] args) {
                        String name = getName(false);
                        if (name != null) {
                            System.out.println("Name length: " + name.length());
                        } else {
                            System.out.println("Name not found");
                        }
                    }
                }""");
        p.setExplanation("""
                'getName(false)' returns null by design. The caller then immediately calls
                'name.length()' without checking for null first - calling any method on a null
                reference throws NullPointerException. The fix is to check 'name != null' before
                dereferencing it, and handle the "not found" case explicitly instead of assuming the
                method always returns a usable value.""");
        p.setPatternHint("""
                Signal to watch for: a method whose return type can be null (either explicitly, by
                reading its code, or because the documentation says "returns null if not found"),
                immediately followed by a method call on that result with no null check in between.""");
        p.setCommonMistakes("""
                Assuming a method "probably" won't return null without actually checking its code;
                adding the null check far away from where the value is used, rather than right before
                first use; catching NullPointerException instead of preventing it with a check.""");
        p.setCompanyTags("Extremely common debugging question; NullPointerException is the single most frequent runtime error in real Java codebases.");
        return p;
    }

    private CodingProblem bugIntegerDivision() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Find the Bug: Integer Division Truncation");
        p.setTopic("Debugging");
        p.setDifficulty(Difficulty.EASY);
        p.setTags("find-the-bug,type-conversion,aptitude");
        p.setDescription("""
                The following code is supposed to compute and print an average of 3.5, but it prints
                3.0 instead. Find and fix the bug.

                public class Main {
                    public static void main(String[] args) {
                        int total = 7;
                        int count = 2;
                        double average = total / count;
                        System.out.println("Average: " + average);
                    }
                }""");
        p.setSolutionCode("""
                public class Main {
                    public static void main(String[] args) {
                        int total = 7;
                        int count = 2;
                        double average = (double) total / count;
                        System.out.println("Average: " + average);
                    }
                }""");
        p.setExplanation("""
                'total' and 'count' are both int, so 'total / count' performs INTEGER division first -
                7 / 2 truncates to 3, discarding the remainder entirely. Only after that truncated
                result (3) is computed does it get widened to a double (3.0) for assignment. Casting
                one operand to double BEFORE the division - '(double) total / count' - forces
                floating-point division from the start, correctly giving 3.5.""");
        p.setPatternHint("""
                Signal to watch for: dividing two int (or other integer-type) variables and assigning
                the result to a double. The division happens in integer arithmetic before the
                assignment ever sees a double - casting after the fact is always too late.""");
        p.setCommonMistakes("""
                Casting the final result instead of an operand before the division (e.g. '(double)
                (total / count)' still truncates first, since the cast happens after the division);
                assuming the target variable's type (double) influences how the division itself is
                performed (it does not).""");
        p.setCompanyTags("Classic type-conversion debugging question in aptitude rounds.");
        return p;
    }

    private CodingProblem bugConcurrentModification() {
        CodingProblem p = new CodingProblem();
        p.setTitle("Find the Bug: ConcurrentModificationException While Removing");
        p.setTopic("Debugging");
        p.setDifficulty(Difficulty.MEDIUM);
        p.setTags("find-the-bug,collections,aptitude");
        p.setDescription("""
                The following code is supposed to remove "Bob" from the list, but it throws a
                ConcurrentModificationException. Find and fix the bug.

                import java.util.ArrayList;
                import java.util.List;

                public class Main {
                    public static void main(String[] args) {
                        List<String> names = new ArrayList<>(List.of("Alice", "Bob", "Charlie"));
                        for (String name : names) {
                            if (name.equals("Bob")) {
                                names.remove(name);
                            }
                        }
                        System.out.println(names);
                    }
                }""");
        p.setSolutionCode("""
                import java.util.ArrayList;
                import java.util.List;

                public class Main {
                    public static void main(String[] args) {
                        List<String> names = new ArrayList<>(List.of("Alice", "Bob", "Charlie"));
                        names.removeIf(name -> name.equals("Bob"));
                        System.out.println(names);
                    }
                }""");
        p.setExplanation("""
                A for-each loop uses an Iterator internally, and that iterator tracks the list's
                "modification count" to detect concurrent changes. Calling 'names.remove(name)'
                directly on the list, while that same list is being iterated by the for-each loop's
                hidden iterator, invalidates the iterator mid-traversal - the next call to its
                hasNext()/next() detects the mismatch and throws ConcurrentModificationException. The
                fix is to use 'removeIf(...)', which safely removes matching elements without this
                conflict, or to call remove() through the Iterator's own remove() method instead of the
                list's.""");
        p.setPatternHint("""
                Signal to watch for: a for-each loop over a List/Map/Set that also calls remove() or
                add() directly on that same collection inside the loop body. Any structural
                modification of a collection while a for-each loop iterates it is unsafe.""");
        p.setCommonMistakes("""
                Not noticing the bug with small lists where only one match happens to be removed (it
                can sometimes appear to "work" depending on position, making the bug intermittent and
                confusing); reaching for a plain indexed for-loop as a fix without realizing it skips
                elements after a removal, instead of using removeIf or an explicit Iterator.""");
        p.setCompanyTags("Common real-world Java bug; frequently asked as a debugging question in interviews.");
        return p;
    }

    private void seedResources() {
        resourceRepository.save(resource(
                "Java SE 21 Official Documentation",
                "https://docs.oracle.com/en/java/javase/21/",
                "Oracle's official documentation hub for the Java SE 21 language and APIs.",
                "Java", ResourceType.DOCUMENTATION, "java,official"));

        resourceRepository.save(resource(
                "Spring Boot Reference Documentation",
                "https://docs.spring.io/spring-boot/index.html",
                "Official Spring Boot reference docs covering auto-configuration, starters, and core concepts.",
                "Spring Boot", ResourceType.DOCUMENTATION, "spring,official"));

        resourceRepository.save(resource(
                "Baeldung",
                "https://www.baeldung.com/",
                "A large, practical library of Java and Spring tutorials, widely used by working developers.",
                "Java", ResourceType.TUTORIAL, "java,spring,tutorial"));

        resourceRepository.save(resource(
                "Spring Boot GitHub Repository",
                "https://github.com/spring-projects/spring-boot",
                "The official Spring Boot source code repository.",
                "Spring Boot", ResourceType.GITHUB_REPO, "spring,github"));
    }

    private LearningResource resource(String title, String url, String description, String topic,
                                        ResourceType type, String tags) {
        LearningResource r = new LearningResource();
        r.setTitle(title);
        r.setUrl(url);
        r.setDescription(description);
        r.setTopic(topic);
        r.setResourceType(type);
        r.setTags(tags);
        return r;
    }
}
