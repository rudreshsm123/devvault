package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.full;

/** Intermediate set C: backtracking, dynamic programming, greedy and stack problems. */
public final class MediumDpGreedyStack {

    private MediumDpGreedyStack() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty M = Difficulty.MEDIUM;

        full(l, "Combination Sum", "Backtracking", M, "backtracking,recursion,combinations",
                "Given distinct positive numbers and a target, return all unique combinations that sum to the target. A number may be used any number of times.",
                "Signal: choose a number, recurse with the reduced target, undo; start the loop at the same index so combinations are not repeated in a different order.",
                null,
                """
                static List<List<Integer>> combinationSum(int[] nums, int target) {
                    List<List<Integer>> out = new ArrayList<>();
                    Arrays.sort(nums);
                    backtrack(nums, target, 0, new ArrayList<>(), out);
                    return out;
                }
                static void backtrack(int[] nums, int remaining, int start, List<Integer> cur, List<List<Integer>> out) {
                    if (remaining == 0) { out.add(new ArrayList<>(cur)); return; }
                    for (int i = start; i < nums.length; i++) {
                        if (nums[i] > remaining) break;                 // sorted, so nothing further can fit
                        cur.add(nums[i]);
                        backtrack(nums, remaining - nums[i], i, cur, out);   // i, not i + 1: reuse allowed
                        cur.remove(cur.size() - 1);
                    }
                }""",
                "Sorting enables pruning: once a number exceeds the remaining target, all larger numbers do too. Passing i (not i + 1) allows reuse, while never looking back at smaller indexes avoids permutations of the same combination.",
                """
                remaining == 0 ==> A valid combination was found; save a copy.
                for (int i = start; ...) ==> Only consider numbers from the current index on, so [2,3] and [3,2] are not both generated.
                nums[i] > remaining then break ==> Pruning: the array is sorted, so no later number can fit either.
                backtrack(..., i, ...) ==> Same index again: the number may be reused.
                cur.remove(last) ==> Undo the choice.""",
                "Contrast with Subsets (i + 1) and Permutations (a used[] array). Say the complexity is exponential and why pruning matters.",
                "O(2^target) worst", "O(target)", "Passing i + 1 and disallowing reuse; forgetting to copy the list.");

        full(l, "Permutations", "Backtracking", M, "backtracking,recursion,permutations",
                "Return all permutations of distinct numbers.",
                "Signal: at each position try every unused number.",
                null,
                """
                static List<List<Integer>> permute(int[] nums) {
                    List<List<Integer>> out = new ArrayList<>();
                    backtrack(nums, new boolean[nums.length], new ArrayList<>(), out);
                    return out;
                }
                static void backtrack(int[] nums, boolean[] used, List<Integer> cur, List<List<Integer>> out) {
                    if (cur.size() == nums.length) { out.add(new ArrayList<>(cur)); return; }
                    for (int i = 0; i < nums.length; i++) {
                        if (used[i]) continue;
                        used[i] = true;
                        cur.add(nums[i]);
                        backtrack(nums, used, cur, out);
                        cur.remove(cur.size() - 1);
                        used[i] = false;
                    }
                }""",
                "The used array records which numbers are already in the current arrangement. Choose, explore, undo gives all n! orders.",
                """
                cur.size() == nums.length ==> A complete arrangement.
                used[i] ==> Skip numbers already placed in this arrangement.
                used[i] = true; cur.add(...) ==> Choose.
                backtrack(...) ==> Fill the next position.
                cur.remove(...); used[i] = false ==> Undo both parts of the choice.""",
                "State n! complexity. For duplicates, sort and skip equal numbers that were not used by the previous step.",
                "O(n x n!)", "O(n)", "Resetting only one of used and cur.");

        full(l, "Generate Parentheses", "Backtracking", M, "backtracking,strings,valid-parentheses",
                "Generate all valid strings of n pairs of parentheses.",
                "Signal: only add '(' while fewer than n are used and add ')' only when it would not exceed the opens.",
                """
                static List<String> generate(int n) {
                    List<String> out = new ArrayList<>();
                    brute("", 2 * n, out);
                    return out;
                }
                static void brute(String s, int len, List<String> out) {
                    if (s.length() == len) { if (valid(s)) out.add(s); return; }
                    brute(s + "(", len, out);
                    brute(s + ")", len, out);
                }
                static boolean valid(String s) {
                    int bal = 0;
                    for (char c : s.toCharArray()) { bal += c == '(' ? 1 : -1; if (bal < 0) return false; }
                    return bal == 0;
                }""",
                """
                static List<String> generate(int n) {
                    List<String> out = new ArrayList<>();
                    build(new StringBuilder(), 0, 0, n, out);
                    return out;
                }
                static void build(StringBuilder sb, int open, int close, int n, List<String> out) {
                    if (sb.length() == 2 * n) { out.add(sb.toString()); return; }
                    if (open < n) {
                        sb.append('(');
                        build(sb, open + 1, close, n, out);
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    if (close < open) {
                        sb.append(')');
                        build(sb, open, close + 1, n, out);
                        sb.deleteCharAt(sb.length() - 1);
                    }
                }""",
                "The brute force builds all 2^(2n) strings and filters. The guided version never creates an invalid prefix: a ')' is allowed only if there is an unmatched '('.",
                """
                open < n ==> We may still add an opening bracket.
                close < open ==> A closing bracket is legal only when an opening bracket is waiting for it.
                sb.append(...) / sb.deleteCharAt(...) ==> Choose and undo on a shared StringBuilder.
                sb.length() == 2 * n ==> All pairs placed; the string is valid by construction.""",
                "The count is the Catalan number. The point of the question is pruning invalid branches early.",
                "O(4^n / sqrt n)", "O(n)", "Generating all strings and filtering (too slow for n around 12).");

        full(l, "Letter Combinations of a Phone Number", "Backtracking", M, "backtracking,strings,mapping",
                "Given digits 2-9, return all letter combinations a phone keypad could produce.",
                "Signal: one choice per digit; the recursion depth equals the number of digits.",
                null,
                """
                private static final String[] KEYS = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

                static List<String> letterCombinations(String digits) {
                    List<String> out = new ArrayList<>();
                    if (digits.isEmpty()) return out;
                    build(digits, 0, new StringBuilder(), out);
                    return out;
                }
                static void build(String digits, int i, StringBuilder sb, List<String> out) {
                    if (i == digits.length()) { out.add(sb.toString()); return; }
                    for (char c : KEYS[digits.charAt(i) - '0'].toCharArray()) {
                        sb.append(c);
                        build(digits, i + 1, sb, out);
                        sb.deleteCharAt(sb.length() - 1);
                    }
                }""",
                "A lookup table maps each digit to its letters. For every digit we try each letter and move to the next digit; the answer count is the product of the number of letters per digit.",
                """
                KEYS[digit] ==> The letters on that key; indexes 0 and 1 are empty because they have no letters.
                digits.charAt(i) - '0' ==> Convert a digit character to an array index.
                i == digits.length() ==> One letter chosen for every digit: a finished combination.
                sb.append / deleteCharAt ==> Choose and undo.
                digits.isEmpty() ==> The empty input has no combinations (not one empty combination).""",
                "A gentle backtracking template. Handle the empty input explicitly.",
                "O(4^n)", "O(n)", "Returning [\"\"] for empty input.");

        full(l, "Unique Paths", "Dynamic Programming", M, "dp,grid,combinatorics",
                "A robot moves only right or down on an m x n grid from the top-left to the bottom-right. How many distinct paths exist?",
                "Signal: paths(r, c) = paths(r-1, c) + paths(r, c-1).",
                """
                static int uniquePaths(int m, int n) {
                    if (m == 1 || n == 1) return 1;
                    return uniquePaths(m - 1, n) + uniquePaths(m, n - 1);     // exponential
                }""",
                """
                static int uniquePaths(int m, int n) {
                    int[] row = new int[n];
                    Arrays.fill(row, 1);
                    for (int r = 1; r < m; r++)
                        for (int c = 1; c < n; c++)
                            row[c] += row[c - 1];            // from above (old row[c]) + from the left
                    return row[n - 1];
                }""",
                "Any cell is reached from the cell above or from the left. A single array keeps the current row: row[c] still holds the value from above until we add the value from the left.",
                """
                Arrays.fill(row, 1) ==> The first row has exactly one way to reach each cell (all moves right).
                row[c] += row[c - 1] ==> New value = value from above (what was already in row[c]) + value from the left.
                return row[n - 1] ==> The bottom-right cell.
                Math alternative ==> C(m + n - 2, m - 1): choose which moves are 'down'.""",
                "A 2D table first, then compress to one row. Mention the combinatorics formula for bonus points.",
                "O(m x n)", "O(n)", "Starting the loops at 0 and overwriting the base row.");

        full(l, "Minimum Path Sum", "Dynamic Programming", M, "dp,grid",
                "Find the path from the top-left to the bottom-right of a grid of non-negative numbers (moving right or down) with the smallest sum.",
                "Signal: cost(r, c) = grid[r][c] + min(cost from above, cost from the left).",
                null,
                """
                static int minPathSum(int[][] g) {
                    int R = g.length, C = g[0].length;
                    int[][] dp = new int[R][C];
                    dp[0][0] = g[0][0];
                    for (int c = 1; c < C; c++) dp[0][c] = dp[0][c - 1] + g[0][c];
                    for (int r = 1; r < R; r++) dp[r][0] = dp[r - 1][0] + g[r][0];
                    for (int r = 1; r < R; r++)
                        for (int c = 1; c < C; c++)
                            dp[r][c] = g[r][c] + Math.min(dp[r - 1][c], dp[r][c - 1]);
                    return dp[R - 1][C - 1];
                }""",
                "dp[r][c] holds the cheapest cost to reach that cell. The first row and column have only one way in. Every other cell takes the cheaper of its two predecessors.",
                """
                dp[0][0] = g[0][0] ==> The start costs its own value.
                first row / first column ==> Only one direction is possible, so costs simply accumulate.
                g[r][c] + Math.min(above, left) ==> Pay for this cell plus the better way of arriving.
                dp[R-1][C-1] ==> The answer.""",
                "Easy to extend: print the path (store parents) or use a single row to save memory.",
                "O(R x C)", "O(R x C)", "Choosing greedily the smaller neighbour at each step (not optimal).");

        full(l, "Longest Common Subsequence", "Dynamic Programming", M, "dp,strings,2d-dp",
                "Return the length of the longest subsequence common to two strings (characters in the same order, not necessarily adjacent).",
                "Signal: dp[i][j] over prefixes: matching characters extend the diagonal, otherwise take the better of dropping one character.",
                null,
                """
                static int lcs(String a, String b) {
                    int[][] dp = new int[a.length() + 1][b.length() + 1];
                    for (int i = 1; i <= a.length(); i++) {
                        for (int j = 1; j <= b.length(); j++) {
                            if (a.charAt(i - 1) == b.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1] + 1;
                            else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                        }
                    }
                    return dp[a.length()][b.length()];
                }""",
                "dp[i][j] is the LCS length of the first i characters of a and the first j of b. A match adds one to the diagonal; a mismatch inherits the best of skipping a character from either string.",
                """
                new int[a.length() + 1][b.length() + 1] ==> An extra row and column of zeros represent 'empty prefix'.
                a.charAt(i - 1) == b.charAt(j - 1) ==> The last characters match, so they are part of the subsequence.
                dp[i - 1][j - 1] + 1 ==> Extend the best subsequence of both shorter prefixes.
                Math.max(dp[i - 1][j], dp[i][j - 1]) ==> No match: drop the last character of a or of b, whichever is better.""",
                "Foundation for edit distance, diff tools and DNA alignment. Know how to reconstruct the actual sequence by walking back through the table.",
                "O(n x m)", "O(n x m)", "Confusing subsequence with substring.");

        full(l, "Partition Equal Subset Sum", "Dynamic Programming", M, "dp,knapsack,subset-sum",
                "Can an array of positive integers be split into two subsets with equal sums?",
                "Signal: it is a subset-sum question for target = total / 2 (0/1 knapsack).",
                null,
                """
                static boolean canPartition(int[] nums) {
                    int total = Arrays.stream(nums).sum();
                    if (total % 2 != 0) return false;
                    int target = total / 2;
                    boolean[] dp = new boolean[target + 1];
                    dp[0] = true;
                    for (int x : nums) {
                        for (int s = target; s >= x; s--) {      // backwards: each number used once
                            dp[s] = dp[s] || dp[s - x];
                        }
                    }
                    return dp[target];
                }""",
                "An odd total can never split evenly. Otherwise we only need to know if some subset reaches half the total. dp[s] says whether sum s is achievable; iterating sums downward prevents using the same number twice.",
                """
                total % 2 != 0 ==> Quick rejection for odd totals.
                dp[0] = true ==> The empty subset makes sum 0.
                for (s = target; s >= x; s--) ==> Going downward ensures dp[s - x] still refers to the state BEFORE including x.
                dp[s] = dp[s] || dp[s - x] ==> Sum s is reachable if it already was, or if s - x was reachable and we add x.""",
                "Explain why the loop goes backwards (0/1 knapsack) and forwards for unlimited use (coin change).",
                "O(n x target)", "O(target)", "Iterating the inner loop upwards, reusing a number.");

        full(l, "Decode Ways", "Dynamic Programming", M, "dp,strings",
                "A string of digits encodes letters (1 = A ... 26 = Z). Return the number of ways to decode it.",
                "Signal: like climbing stairs: one digit (1-9) or two digits (10-26) can end the string.",
                null,
                """
                static int numDecodings(String s) {
                    if (s.isEmpty() || s.charAt(0) == '0') return 0;
                    int prev2 = 1, prev1 = 1;                            // ways for the empty prefix and for the first character
                    for (int i = 2; i <= s.length(); i++) {
                        int cur = 0;
                        if (s.charAt(i - 1) != '0') cur += prev1;          // last digit alone
                        int two = Integer.parseInt(s.substring(i - 2, i));
                        if (two >= 10 && two <= 26) cur += prev2;          // last two digits together
                        prev2 = prev1;
                        prev1 = cur;
                    }
                    return prev1;
                }""",
                "The number of ways to decode i characters equals (ways for i - 1 if the last digit is 1-9) plus (ways for i - 2 if the last two digits form 10-26). A '0' can only be part of 10 or 20.",
                """
                s.charAt(0) == '0' ==> A leading zero cannot be decoded at all.
                prev2, prev1 ==> The answers for the prefixes two and one characters shorter; only these are needed.
                s.charAt(i - 1) != '0' ==> A single non-zero digit is a valid letter.
                two >= 10 && two <= 26 ==> A valid two-digit letter (excludes 05, 27 and so on).
                prev2 = prev1; prev1 = cur ==> Roll the window forward.""",
                "The zero handling is the whole difficulty. Test '0', '06', '10', '226' and '2101'.",
                "O(n)", "O(1)", "Treating '0' as a valid single digit.");

        full(l, "Gas Station", "Greedy", M, "greedy,circular,arrays",
                "Gas stations are on a circle; gas[i] fuel is added at station i and cost[i] is needed to reach the next one. Return the starting index that lets you complete the circuit, or -1.",
                "Signal: if the total gas is at least the total cost a solution exists, and the start is just after the point where the running tank was lowest.",
                null,
                """
                static int canCompleteCircuit(int[] gas, int[] cost) {
                    int total = 0, tank = 0, start = 0;
                    for (int i = 0; i < gas.length; i++) {
                        int gain = gas[i] - cost[i];
                        total += gain;
                        tank += gain;
                        if (tank < 0) {          // cannot reach i + 1 from any start in [start, i]
                            start = i + 1;
                            tank = 0;
                        }
                    }
                    return total >= 0 ? start : -1;
                }""",
                "If you run out of fuel on the way from start to i, then no station between start and i can be a valid start either (they would arrive with even less fuel), so jump the start past i. One pass decides.",
                """
                gain = gas[i] - cost[i] ==> Net fuel change at station i.
                total ==> Sum over the whole circle; negative means there is not enough fuel overall, so the answer is -1.
                tank < 0 ==> We cannot reach the next station from the current start.
                start = i + 1; tank = 0 ==> Restart after the failure point.
                return total >= 0 ? start : -1 ==> If enough fuel exists in total, the final candidate is guaranteed to work.""",
                "The proof sketch ('why can we skip all those starts') is what interviewers wait for.",
                "O(n)", "O(1)", "Trying every start (O(n^2)).");

        full(l, "Jump Game II", "Greedy", M, "greedy,bfs-levels,arrays",
                "Return the minimum number of jumps to reach the last index (it is always reachable).",
                "Signal: BFS in disguise - each jump covers a range of indexes; extend the range greedily.",
                null,
                """
                static int jump(int[] nums) {
                    int jumps = 0, currentEnd = 0, furthest = 0;
                    for (int i = 0; i < nums.length - 1; i++) {
                        furthest = Math.max(furthest, i + nums[i]);
                        if (i == currentEnd) {            // must jump now to go further
                            jumps++;
                            currentEnd = furthest;
                        }
                    }
                    return jumps;
                }""",
                "Indexes reachable with k jumps form a window. While scanning the window we record how far the next jump could reach. When the window ends we must take a jump, and the new window ends at that furthest point.",
                """
                furthest ==> The furthest index reachable with one more jump from anything seen so far.
                i == currentEnd ==> We reached the end of the range covered by the current number of jumps.
                jumps++; currentEnd = furthest ==> Take the jump that reaches the furthest.
                i < nums.length - 1 ==> We do not need to jump from the last index.""",
                "Explain it as BFS levels: each jump is one level. Compare with Jump Game I.",
                "O(n)", "O(1)", "Counting a jump at the last index.");

        full(l, "Non-overlapping Intervals", "Greedy", M, "greedy,intervals,sorting",
                "Return the minimum number of intervals to remove so that the rest do not overlap.",
                "Signal: sort by END time and always keep the interval that ends earliest.",
                null,
                """
                static int eraseOverlapIntervals(int[][] intervals) {
                    Arrays.sort(intervals, Comparator.comparingInt(a -> a[1]));
                    int removed = 0, lastEnd = Integer.MIN_VALUE;
                    for (int[] in : intervals) {
                        if (in[0] >= lastEnd) {
                            lastEnd = in[1];            // keep it
                        } else {
                            removed++;                  // overlaps with the kept one: drop it
                        }
                    }
                    return removed;
                }""",
                "Keeping the interval that finishes first leaves the most room for the rest. This is the classic activity-selection greedy proof: any solution can be changed to start with the earliest-finishing interval without losing anything.",
                """
                sort by a[1] ==> Order by end time, not start time.
                in[0] >= lastEnd ==> Starts after (or exactly when) the last kept interval ended: no overlap.
                lastEnd = in[1] ==> Keep it and update the boundary.
                removed++ ==> This interval overlaps a kept one; removing it costs one.""",
                "Be able to argue why sorting by start fails. Same technique: minimum arrows to burst balloons.",
                "O(n log n)", "O(1)", "Sorting by start time.");

        full(l, "Partition Labels", "Greedy", M, "greedy,strings,two-pointers",
                "Split a string into as many parts as possible so that each letter appears in at most one part. Return the sizes of the parts.",
                "Signal: record each letter's last position and extend the current part to cover them.",
                null,
                """
                static List<Integer> partitionLabels(String s) {
                    int[] last = new int[26];
                    for (int i = 0; i < s.length(); i++) last[s.charAt(i) - 'a'] = i;
                    List<Integer> out = new ArrayList<>();
                    int start = 0, end = 0;
                    for (int i = 0; i < s.length(); i++) {
                        end = Math.max(end, last[s.charAt(i) - 'a']);
                        if (i == end) {
                            out.add(end - start + 1);
                            start = i + 1;
                        }
                    }
                    return out;
                }""",
                "The current part must reach at least the last occurrence of every letter it contains. When the scan reaches that furthest end, the part can be closed.",
                """
                last[] ==> The final index of each letter, computed in a first pass.
                end = Math.max(end, last[...]) ==> Letters inside the part force it to extend.
                i == end ==> Every letter seen so far ends here, so the part is complete.
                out.add(end - start + 1) ==> The size of the finished part.""",
                "Two linear passes. Relates to merging intervals, where each letter defines an interval from first to last occurrence.",
                "O(n)", "O(1)", "Closing a part before all letters' last positions are covered.");

        full(l, "Largest Number (Custom Sort)", "Sorting", M, "sorting,comparator,strings",
                "Arrange numbers so that their concatenation forms the largest possible number: [3, 30, 34, 5, 9] gives 9534330.",
                "Signal: a custom comparator - a goes before b if a+b is larger than b+a as strings.",
                null,
                """
                static String largestNumber(int[] nums) {
                    String[] s = Arrays.stream(nums).mapToObj(String::valueOf).toArray(String[]::new);
                    Arrays.sort(s, (a, b) -> (b + a).compareTo(a + b));
                    if (s[0].equals("0")) return "0";               // all zeros
                    return String.join("", s);
                }""",
                "Sorting by numeric value or by plain string order fails ('30' vs '3'). Comparing the two possible concatenations directly decides which order is better, and that relation is consistent (transitive), so sorting works.",
                """
                (b + a).compareTo(a + b) ==> If b followed by a is bigger, then b must come first; the comparator returns the order for descending concatenation.
                s[0].equals("0") ==> If the largest piece is 0 then everything is 0; avoid '000'.
                String.join("", s) ==> Concatenate in the sorted order.
                mapToObj(String::valueOf) ==> Convert numbers to strings once.""",
                "A great example of a custom comparator. Be ready to argue that the comparator is transitive.",
                "O(n log n x len)", "O(n)", "Using numeric comparison; returning '00'.");

        full(l, "Asteroid Collision", "Stack", M, "stack,simulation",
                "Asteroids move along a line: positive moves right, negative moves left, size is the absolute value. When two meet the smaller explodes (equal sizes both explode). Return the survivors.",
                "Signal: only a right-mover followed by a left-mover collides; use a stack of survivors.",
                null,
                """
                static int[] asteroidCollision(int[] asteroids) {
                    Deque<Integer> stack = new ArrayDeque<>();
                    for (int a : asteroids) {
                        boolean alive = true;
                        while (alive && a < 0 && !stack.isEmpty() && stack.peek() > 0) {
                            int top = stack.peek();
                            if (top < -a) stack.pop();              // top explodes, keep checking
                            else if (top == -a) { stack.pop(); alive = false; }   // both explode
                            else alive = false;                      // new asteroid explodes
                        }
                        if (alive) stack.push(a);
                    }
                    int[] out = new int[stack.size()];
                    for (int i = out.length - 1; i >= 0; i--) out[i] = stack.pop();
                    return out;
                }""",
                "The stack holds asteroids that are currently safe. A new left-moving asteroid fights the right-moving ones on top of the stack until it dies or the way is clear.",
                """
                a < 0 && stack.peek() > 0 ==> The only collision case: a left-mover meets a right-mover.
                top < -a ==> The stack asteroid is smaller: it explodes; keep fighting with the next one.
                top == -a ==> Equal size: both disappear.
                else alive = false ==> The stack asteroid is bigger; the new one explodes.
                if (alive) stack.push(a) ==> Survivors are pushed.""",
                "A 'simulate with a stack' problem; state the collision condition clearly before coding.",
                "O(n)", "O(n)", "Colliding two asteroids moving in the same direction.");

        full(l, "Evaluate Reverse Polish Notation", "Stack", M, "stack,parsing,expression",
                "Evaluate an expression in reverse Polish notation: [\"2\",\"1\",\"+\",\"3\",\"*\"] means (2 + 1) * 3 = 9.",
                "Signal: operands go onto a stack; an operator pops two, computes and pushes the result.",
                null,
                """
                static int evalRPN(String[] tokens) {
                    Deque<Integer> stack = new ArrayDeque<>();
                    for (String t : tokens) {
                        switch (t) {
                            case "+" -> stack.push(stack.pop() + stack.pop());
                            case "*" -> stack.push(stack.pop() * stack.pop());
                            case "-" -> { int b = stack.pop(), a = stack.pop(); stack.push(a - b); }
                            case "/" -> { int b = stack.pop(), a = stack.pop(); stack.push(a / b); }
                            default -> stack.push(Integer.parseInt(t));
                        }
                    }
                    return stack.pop();
                }""",
                "RPN needs no parentheses or precedence rules. The second popped value is the left operand, which matters for - and /.",
                """
                default -> stack.push(Integer.parseInt(t)) ==> A number is stored for later.
                int b = stack.pop(), a = stack.pop() ==> The FIRST pop is the right operand, the second is the left.
                stack.push(a - b) ==> Order matters for subtraction and division.
                return stack.pop() ==> A valid expression leaves exactly one value.""",
                "Follow-up: convert infix to postfix (shunting-yard) or write a full calculator.",
                "O(n)", "O(n)", "Swapping operand order in - and /.");

        full(l, "Online Stock Span", "Stack", M, "stack,monotonic-stack,design",
                "Design a class where next(price) returns the number of consecutive days (including today) the price was less than or equal to today's price.",
                "Signal: a monotonic stack of (price, span) pairs that merges smaller previous spans.",
                null,
                """
                class StockSpanner {
                    private final Deque<int[]> stack = new ArrayDeque<>();      // {price, span}

                    int next(int price) {
                        int span = 1;
                        while (!stack.isEmpty() && stack.peek()[0] <= price) {
                            span += stack.pop()[1];                              // absorb the earlier smaller days
                        }
                        stack.push(new int[] {price, span});
                        return span;
                    }
                }""",
                "Each stored pair already summarises many earlier days. When a higher price arrives it swallows those pairs, so every price is pushed and popped once (amortised O(1) per call).",
                """
                int span = 1 ==> Today always counts.
                stack.peek()[0] <= price ==> The earlier price is not higher than today's.
                span += stack.pop()[1] ==> Add all the days that earlier price already covered.
                stack.push({price, span}) ==> Store today's summary for future days.""",
                "A direct application of the monotonic stack; state the amortised complexity.",
                "O(1) amortised", "O(n)", "Storing only prices and rescanning the past.");

        full(l, "Car Fleet", "Stack", M, "stack,sorting,greedy",
                "Cars on a one-lane road head to a target. A faster car catches up and then travels at the slower car's speed (forming a fleet). Return the number of fleets that arrive.",
                "Signal: sort by starting position from the closest to the target and compare arrival times.",
                null,
                """
                static int carFleet(int target, int[] position, int[] speed) {
                    int n = position.length;
                    Integer[] order = new Integer[n];
                    for (int i = 0; i < n; i++) order[i] = i;
                    Arrays.sort(order, (a, b) -> position[b] - position[a]);     // closest to the target first
                    int fleets = 0;
                    double lastTime = 0;
                    for (int i : order) {
                        double time = (double) (target - position[i]) / speed[i];
                        if (time > lastTime) {                                    // cannot catch the fleet ahead
                            fleets++;
                            lastTime = time;
                        }
                    }
                    return fleets;
                }""",
                "A car whose arrival time is not greater than the fleet ahead will catch it and merge. Only a strictly later arrival forms a new fleet.",
                """
                sort by position descending ==> Process cars from the front of the road to the back.
                time = (target - position) / speed ==> Time to reach the target if unobstructed (use double).
                time > lastTime ==> It arrives later than the fleet ahead, so it never catches up: a new fleet.
                lastTime = time ==> The new leading fleet's arrival time.""",
                "Convince the interviewer that comparing arrival times is enough (no simulation needed).",
                "O(n log n)", "O(n)", "Integer division when computing time.");

        full(l, "Time Based Key-Value Store", "Design", M, "design,hash-map,binary-search",
                "Design a store where set(key, value, timestamp) saves a value and get(key, timestamp) returns the latest value saved at or before that time.",
                "Signal: per key keep timestamps in order and binary search for the floor.",
                null,
                """
                class TimeMap {
                    private final Map<String, TreeMap<Integer, String>> data = new HashMap<>();

                    void set(String key, String value, int timestamp) {
                        data.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
                    }

                    String get(String key, int timestamp) {
                        TreeMap<Integer, String> versions = data.get(key);
                        if (versions == null) return "";
                        Map.Entry<Integer, String> e = versions.floorEntry(timestamp);
                        return e == null ? "" : e.getValue();
                    }
                }""",
                "A TreeMap keeps timestamps sorted and floorEntry finds the greatest timestamp less than or equal to the query in O(log n). It is how versioned configuration and audit stores answer 'what was the value at time T'.",
                """
                Map<String, TreeMap<Integer, String>> ==> Key -> its history ordered by time.
                computeIfAbsent(key, k -> new TreeMap<>()) ==> Create the history on first write.
                versions.floorEntry(timestamp) ==> The latest entry at or before the requested time.
                e == null ? "" ==> No value existed yet at that time.""",
                "If timestamps always increase you can use an ArrayList and manual binary search. Discuss that choice.",
                "O(log n) get", "O(n)", "Using ceilingEntry instead of floorEntry.");

        full(l, "Top K Frequent Words", "Heap", M, "heap,hash-map,sorting,strings",
                "Return the k most frequent words, ordered by frequency (highest first) and alphabetically for ties.",
                "Signal: count with a map, then use a heap or a sort with a two-level comparator.",
                """
                static List<String> topKFrequent(String[] words, int k) {
                    Map<String, Integer> count = new HashMap<>();
                    for (String w : words) count.merge(w, 1, Integer::sum);
                    List<String> list = new ArrayList<>(count.keySet());
                    list.sort((a, b) -> count.get(a).equals(count.get(b)) ? a.compareTo(b) : count.get(b) - count.get(a));
                    return list.subList(0, k);
                }""",
                """
                static List<String> topKFrequent(String[] words, int k) {
                    Map<String, Integer> count = new HashMap<>();
                    for (String w : words) count.merge(w, 1, Integer::sum);
                    PriorityQueue<String> heap = new PriorityQueue<>((a, b) ->
                            count.get(a).equals(count.get(b)) ? b.compareTo(a) : count.get(a) - count.get(b));   // weakest on top
                    for (String w : count.keySet()) {
                        heap.add(w);
                        if (heap.size() > k) heap.poll();
                    }
                    LinkedList<String> out = new LinkedList<>();
                    while (!heap.isEmpty()) out.addFirst(heap.poll());
                    return out;
                }""",
                "The heap keeps only k words; its comparator ranks the WEAKEST word as the smallest so it is the one removed (lower frequency, or alphabetically later when tied).",
                """
                count.merge(w, 1, Integer::sum) ==> Frequency table.
                heap comparator ==> The smallest element in the heap must be the least desirable word: lower count first, then the alphabetically later word.
                if (heap.size() > k) heap.poll() ==> Evict the weakest so the heap holds the best k.
                out.addFirst(heap.poll()) ==> Polling yields weakest first, so insert at the front to reverse the order.""",
                "Tie-breaking direction is the usual error. Compare with the numeric Top K Frequent Elements.",
                "O(n log k)", "O(n)", "Wrong tie-break direction inside the heap.");

        return l;
    }
}
