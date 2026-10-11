package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.full;

/** Hard set A: dynamic programming, strings and string algorithms. */
public final class HardDpStrings {

    private HardDpStrings() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty H = Difficulty.HARD;

        full(l, "Shortest Palindrome (KMP Failure Function)", "Strings", H, "strings,kmp,prefix-function",
                "You may add characters only in FRONT of a string. Return the shortest palindrome you can create.",
                "Signal: find the longest palindromic PREFIX; the rest, reversed, goes in front. KMP's prefix function finds it in linear time.",
                null,
                """
                static String shortestPalindrome(String s) {
                    String combined = s + "#" + new StringBuilder(s).reverse();
                    int[] fail = new int[combined.length()];
                    for (int i = 1; i < combined.length(); i++) {
                        int k = fail[i - 1];
                        while (k > 0 && combined.charAt(i) != combined.charAt(k)) k = fail[k - 1];
                        if (combined.charAt(i) == combined.charAt(k)) k++;
                        fail[i] = k;
                    }
                    int palPrefix = fail[combined.length() - 1];                 // length of the longest palindromic prefix
                    return new StringBuilder(s.substring(palPrefix)).reverse() + s;
                }""",
                "A prefix of s that is a palindrome equals its own reverse, which is a suffix of reverse(s). Concatenating s + '#' + reverse(s) turns 'longest palindromic prefix' into 'longest prefix that is also a suffix', exactly what the KMP failure array stores. The separator prevents matches from crossing the boundary.",
                """
                s + "#" + reverse(s) ==> The separator '#' (a character not in s) stops the prefix and suffix from overlapping.
                fail[i] ==> Length of the longest proper prefix of combined[0..i] that is also a suffix of it.
                while (k > 0 && mismatch) k = fail[k - 1] ==> On a mismatch fall back to the next shorter border instead of starting over; this keeps the loop linear.
                fail[last] ==> The longest prefix of s that equals a suffix of reverse(s), i.e. the longest palindromic prefix.
                reverse(s.substring(palPrefix)) + s ==> Mirror the leftover tail and put it in front.""",
                "Offer the O(n^2) check-every-prefix solution first, then KMP. Rolling hash is an alternative. Explain the border/failure function clearly: it is reused in strStr and pattern matching.",
                "O(n)", "O(n)", "Forgetting the separator (false matches across the join).");

        full(l, "Implement strStr with KMP", "Strings", H, "strings,kmp,pattern-matching",
                "Return the first index where needle appears inside haystack, or -1, in O(n + m) time.",
                "Signal: precompute how far the pattern can fall back on a mismatch (the prefix table), so the text pointer never moves backwards.",
                null,
                """
                static int strStr(String haystack, String needle) {
                    if (needle.isEmpty()) return 0;
                    int[] fail = new int[needle.length()];
                    for (int i = 1, k = 0; i < needle.length(); i++) {
                        while (k > 0 && needle.charAt(i) != needle.charAt(k)) k = fail[k - 1];
                        if (needle.charAt(i) == needle.charAt(k)) k++;
                        fail[i] = k;
                    }
                    for (int i = 0, k = 0; i < haystack.length(); i++) {
                        while (k > 0 && haystack.charAt(i) != needle.charAt(k)) k = fail[k - 1];
                        if (haystack.charAt(i) == needle.charAt(k)) k++;
                        if (k == needle.length()) return i - k + 1;
                    }
                    return -1;
                }""",
                "The naive search restarts the comparison after every mismatch (O(n*m)). KMP remembers how many characters of the pattern still match as a prefix and reuses them, so the text index only moves forward.",
                """
                fail[] ==> For each position of the needle: the length of the longest border (prefix that is also a suffix).
                k ==> How many needle characters currently match the end of the text read so far.
                while (k > 0 && mismatch) k = fail[k - 1] ==> Shift the pattern so its longest still-valid prefix lines up; no characters of the text are re-read.
                k == needle.length() ==> Full match found; the start index is i - k + 1.
                needle.isEmpty() ==> By convention the empty needle is found at index 0.""",
                "In practice String.indexOf is fine, but KMP shows you understand linear string matching. Contrast with Rabin-Karp (hashing) and Boyer-Moore.",
                "O(n + m)", "O(m)", "Resetting the text pointer on mismatch (turns it into the naive algorithm).");

        full(l, "Longest Palindromic Subsequence", "Dynamic Programming", H, "dp,strings,interval-dp",
                "Return the length of the longest palindromic subsequence (not necessarily contiguous) of a string.",
                "Signal: interval DP - if the end characters match, wrap the best inner answer; otherwise drop one end.",
                null,
                """
                static int longestPalindromeSubseq(String s) {
                    int n = s.length();
                    int[][] dp = new int[n][n];
                    for (int i = n - 1; i >= 0; i--) {
                        dp[i][i] = 1;
                        for (int j = i + 1; j < n; j++) {
                            if (s.charAt(i) == s.charAt(j)) dp[i][j] = dp[i + 1][j - 1] + 2;
                            else dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                        }
                    }
                    return dp[0][n - 1];
                }""",
                "dp[i][j] is the answer for the substring s[i..j]. Equal ends contribute 2 plus the inside; otherwise the better of dropping the left or the right character. It equals the LCS of the string and its reverse.",
                """
                dp[i][i] = 1 ==> Any single character is a palindrome of length 1.
                for (i = n - 1; i >= 0; i--) ==> Compute shorter intervals first: dp[i][j] needs dp[i+1][*] which belong to larger i.
                s[i] == s[j] -> dp[i+1][j-1] + 2 ==> Both ends join the palindrome around the best inner subsequence.
                Math.max(dp[i+1][j], dp[i][j-1]) ==> Otherwise skip one end.
                dp[0][n-1] ==> The whole string.""",
                "State the equivalence with LCS(s, reverse(s)) as a sanity check. Compare subsequence versus substring.",
                "O(n^2)", "O(n^2)", "Filling the table in the wrong order.");

        full(l, "Best Time to Buy and Sell Stock III", "Dynamic Programming", H, "dp,state-machine,stocks",
                "At most two transactions (a buy followed by a sell, with no overlap) are allowed. Return the maximum profit.",
                "Signal: a small state machine - after first buy, after first sell, after second buy, after second sell.",
                null,
                """
                static int maxProfit(int[] prices) {
                    int buy1 = Integer.MIN_VALUE, sell1 = 0, buy2 = Integer.MIN_VALUE, sell2 = 0;
                    for (int p : prices) {
                        buy1 = Math.max(buy1, -p);              // best 'cash' after buying the first stock
                        sell1 = Math.max(sell1, buy1 + p);      // after selling it
                        buy2 = Math.max(buy2, sell1 - p);       // after buying the second with the profit so far
                        sell2 = Math.max(sell2, buy2 + p);      // after selling the second
                    }
                    return sell2;
                }""",
                "Each variable is the best possible cash balance in one state. Every price lets us either stay in a state or move to the next state. Updating in that order lets a buy and sell happen on the same day safely (which is allowed and gives zero profit).",
                """
                buy1 ==> Best balance while holding the first stock: we spent p, so -p.
                sell1 ==> Best balance after completing the first transaction.
                buy2 ==> Best balance while holding the second stock, funded by the first profit (sell1 - p).
                sell2 ==> Best balance after the second sale: the answer.
                return sell2 ==> At least as good as sell1 because a second transaction can be empty.""",
                "Generalise to k transactions with dp[k][state] arrays. The 'state machine' view is the key to the whole stock-problem family.",
                "O(n)", "O(1)", "Using one pass of max/min splits and mixing up the order of updates.");

        full(l, "Best Time to Buy and Sell Stock IV (k Transactions)", "Dynamic Programming", H, "dp,state-machine,stocks",
                "At most k transactions are allowed. Return the maximum profit.",
                "Signal: the previous problem generalised - keep buy[j] and sell[j] for every transaction count j.",
                null,
                """
                static int maxProfit(int k, int[] prices) {
                    int n = prices.length;
                    if (k >= n / 2) {                                   // unlimited transactions: take every rise
                        int profit = 0;
                        for (int i = 1; i < n; i++) profit += Math.max(0, prices[i] - prices[i - 1]);
                        return profit;
                    }
                    int[] buy = new int[k + 1], sell = new int[k + 1];
                    Arrays.fill(buy, Integer.MIN_VALUE);
                    for (int p : prices) {
                        for (int j = 1; j <= k; j++) {
                            buy[j] = Math.max(buy[j], sell[j - 1] - p);
                            sell[j] = Math.max(sell[j], buy[j] + p);
                        }
                    }
                    return sell[k];
                }""",
                "For k transactions the state machine repeats k times. If k is at least n / 2 the limit never matters, so the answer is simply the sum of all upward moves, which avoids huge arrays when k is large.",
                """
                k >= n / 2 ==> At most n / 2 buy-sell pairs fit in n days, so the restriction is irrelevant; use the simple greedy sum.
                buy[j] ==> Best balance while holding the stock of the j-th transaction.
                sell[j - 1] - p ==> Fund the j-th purchase with the profit after j - 1 completed transactions.
                buy[j] + p ==> Selling gives the balance after j transactions.
                return sell[k] ==> Best after at most k transactions.""",
                "Mention the whole family: cooldown, transaction fee, one transaction, unlimited. They are variations of the same states.",
                "O(n x k)", "O(k)", "Allocating O(n x k) memory for a huge k.");

        full(l, "Russian Doll Envelopes", "Dynamic Programming", H, "dp,sorting,lis,binary-search",
                "An envelope fits inside another only if both width and height are strictly smaller. Return the maximum number of nested envelopes.",
                "Signal: sort by width ascending (and height DESCENDING for equal widths), then find the longest increasing subsequence of heights.",
                null,
                """
                static int maxEnvelopes(int[][] envelopes) {
                    Arrays.sort(envelopes, (a, b) -> a[0] != b[0] ? a[0] - b[0] : b[1] - a[1]);
                    int[] tails = new int[envelopes.length];
                    int size = 0;
                    for (int[] e : envelopes) {
                        int h = e[1];
                        int i = Arrays.binarySearch(tails, 0, size, h);
                        if (i < 0) i = -(i + 1);
                        tails[i] = h;
                        if (i == size) size++;
                    }
                    return size;
                }""",
                "Sorting by width removes one dimension. Equal widths must never nest, so heights are sorted in descending order within the same width, which prevents two such envelopes from forming an increasing pair. What remains is a strict LIS on heights, solved in O(n log n) with binary search.",
                """
                a[0] != b[0] ? a[0] - b[0] : b[1] - a[1] ==> Width ascending; for equal widths, height descending.
                tails[] ==> tails[i] is the smallest possible last height of an increasing chain of length i + 1.
                Arrays.binarySearch(tails, 0, size, h) ==> Find where h fits.
                if (i < 0) i = -(i + 1) ==> Convert the 'not found' result into the insertion point.
                if (i == size) size++ ==> h extends the longest chain.""",
                "The descending-height trick is the insight interviewers look for. Mention that O(n^2) DP also works but times out for large n.",
                "O(n log n)", "O(n)", "Sorting heights ascending for equal widths (allows invalid nesting).");

        full(l, "Longest Increasing Path in a Matrix", "Dynamic Programming", H, "dp,dfs,memoization,grid",
                "Return the length of the longest strictly increasing path in a matrix, moving in four directions.",
                "Signal: DFS with memoisation; the graph of 'move to a bigger neighbour' has no cycles, so results can be cached.",
                null,
                """
                static int longestIncreasingPath(int[][] m) {
                    int R = m.length, C = m[0].length, best = 0;
                    int[][] memo = new int[R][C];
                    for (int r = 0; r < R; r++)
                        for (int c = 0; c < C; c++)
                            best = Math.max(best, dfs(m, memo, r, c));
                    return best;
                }
                static int dfs(int[][] m, int[][] memo, int r, int c) {
                    if (memo[r][c] != 0) return memo[r][c];
                    int best = 1;
                    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
                    for (int[] d : dirs) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr >= 0 && nc >= 0 && nr < m.length && nc < m[0].length && m[nr][nc] > m[r][c]) {
                            best = Math.max(best, 1 + dfs(m, memo, nr, nc));
                        }
                    }
                    return memo[r][c] = best;
                }""",
                "Because every step goes to a strictly larger value, a path can never revisit a cell, so no visited set is needed. memo[r][c] stores the longest path starting at that cell, so each cell is computed once.",
                """
                memo[r][c] != 0 ==> Already computed (the answer is at least 1, so 0 means 'not yet').
                m[nr][nc] > m[r][c] ==> Only move to a larger value; this makes the graph acyclic.
                1 + dfs(...) ==> One step plus the best continuation.
                return memo[r][c] = best ==> Store and return in one statement.
                outer loops ==> The best path may start anywhere.""",
                "An alternative is topological sort on the 'smaller to larger' DAG. Complexity discussion: each cell and edge once.",
                "O(R x C)", "O(R x C)", "Using a visited array that breaks memoisation; recursion depth on big grids.");

        full(l, "Maximum Sum of 3 Non-Overlapping Subarrays", "Dynamic Programming", H, "dp,sliding-window,arrays",
                "Find three non-overlapping subarrays of length k with the maximum total sum; return their starting indices (smallest lexicographically on ties).",
                "Signal: precompute window sums, then for the middle window combine the best window to its left and the best to its right.",
                null,
                """
                static int[] maxSumOfThreeSubarrays(int[] nums, int k) {
                    int n = nums.length, m = n - k + 1;
                    int[] w = new int[m];                                  // w[i] = sum of nums[i..i+k-1]
                    int sum = 0;
                    for (int i = 0; i < n; i++) {
                        sum += nums[i];
                        if (i >= k) sum -= nums[i - k];
                        if (i >= k - 1) w[i - k + 1] = sum;
                    }
                    int[] left = new int[m], right = new int[m];
                    int best = 0;
                    for (int i = 0; i < m; i++) {                          // best window ending at or before i
                        if (w[i] > w[best]) best = i;
                        left[i] = best;
                    }
                    best = m - 1;
                    for (int i = m - 1; i >= 0; i--) {                     // best window starting at or after i (earliest on ties)
                        if (w[i] >= w[best]) best = i;
                        right[i] = best;
                    }
                    int[] ans = {-1, -1, -1};
                    int max = 0;
                    for (int mid = k; mid < m - k; mid++) {
                        int a = left[mid - k], c = right[mid + k];
                        int total = w[a] + w[mid] + w[c];
                        if (total > max) { max = total; ans = new int[] {a, mid, c}; }
                    }
                    return ans;
                }""",
                "Treat each length-k window as one item with value w[i]. Choosing three non-overlapping windows means picking a middle one and the best window entirely before and entirely after it. Prefix-best arrays give those in O(1).",
                """
                w[i] ==> Sum of the window starting at i, computed with a sliding window in O(n).
                left[i] ==> Index of the best window among starts 0..i (first one on ties).
                right[i] ==> Index of the best window among starts i..m-1 (the earliest one on ties because of >=).
                mid from k to m - k - 1 ==> Leaves room for a whole window on each side.
                left[mid - k], right[mid + k] ==> The nearest allowed positions so the windows do not overlap.""",
                "Tie-breaking is the tricky part. First explain the decomposition into window sums; generalise to k windows with DP.",
                "O(n)", "O(n)", "Overlapping windows (off by k); wrong tie-breaking.");

        full(l, "Predict the Winner (Minimax DP)", "Dynamic Programming", H, "dp,game-theory,minimax",
                "Two players take turns picking a number from either end of an array, both playing optimally. Player 1 starts. Return true if player 1 can win (or tie).",
                "Signal: dp[i][j] = the best score DIFFERENCE the current player can achieve on subarray i..j.",
                null,
                """
                static boolean predictTheWinner(int[] nums) {
                    int n = nums.length;
                    int[][] dp = new int[n][n];
                    for (int i = 0; i < n; i++) dp[i][i] = nums[i];
                    for (int len = 2; len <= n; len++) {
                        for (int i = 0; i + len - 1 < n; i++) {
                            int j = i + len - 1;
                            dp[i][j] = Math.max(nums[i] - dp[i + 1][j], nums[j] - dp[i][j - 1]);
                        }
                    }
                    return dp[0][n - 1] >= 0;
                }""",
                "If I take nums[i], my opponent then plays optimally on i+1..j and gets a lead of dp[i+1][j]; my net lead is nums[i] - dp[i+1][j]. Storing the difference (not each player's score) makes both players use the same formula.",
                """
                dp[i][i] = nums[i] ==> With one number left, the current player takes it.
                nums[i] - dp[i + 1][j] ==> Pick the left end; subtract the opponent's best lead afterwards.
                nums[j] - dp[i][j - 1] ==> Pick the right end.
                Math.max(...) ==> The current player chooses the better option.
                dp[0][n - 1] >= 0 ==> Player 1 does not end behind.""",
                "Minimax with memoisation. The 'difference trick' removes the need to track whose turn it is.",
                "O(n^2)", "O(n^2)", "Tracking two scores separately and mixing up turns.");

        full(l, "Dungeon Game", "Dynamic Programming", H, "dp,grid,reverse-dp",
                "A knight crosses a grid from the top-left to the bottom-right, moving right or down; cells add or subtract health. Return the minimum initial health needed so health never drops to 0 or below.",
                "Signal: DP from the END backwards, because the requirement at a cell depends on the future.",
                null,
                """
                static int calculateMinimumHP(int[][] d) {
                    int R = d.length, C = d[0].length;
                    int[][] need = new int[R + 1][C + 1];
                    for (int[] row : need) Arrays.fill(row, Integer.MAX_VALUE);
                    need[R][C - 1] = need[R - 1][C] = 1;                    // after the last cell we need at least 1 health left
                    for (int r = R - 1; r >= 0; r--) {
                        for (int c = C - 1; c >= 0; c--) {
                            int minNext = Math.min(need[r + 1][c], need[r][c + 1]);
                            need[r][c] = Math.max(1, minNext - d[r][c]);   // health must stay at least 1
                        }
                    }
                    return need[0][0];
                }""",
                "Going forwards you cannot tell whether the cheapest-so-far path is also the safest. Going backwards, need[r][c] is the health required when ENTERING the cell so that the rest of the trip is survivable.",
                """
                need[r][c] ==> The minimum health required before entering cell (r, c).
                need[R][C-1] = need[R-1][C] = 1 ==> Border sentinels so the last cell sees 'need 1' beyond it.
                minNext - d[r][c] ==> After this cell the health changes by d[r][c], so before it we need minNext minus that change.
                Math.max(1, ...) ==> Health must be at least 1 at all times (a big gain cannot make the requirement drop below 1).""",
                "Explain why a forward DP fails. Backward DP is the lesson. Follow-up: print the path.",
                "O(R x C)", "O(R x C)", "Forward DP and a greedy path; allowing health to reach 0.");

        full(l, "Frog Jump", "Dynamic Programming", H, "dp,hash-map,set",
                "Stones lie at increasing positions. A frog starts on the first stone and its first jump is exactly 1. If its last jump was k, the next jump is k-1, k or k+1. Can it reach the last stone?",
                "Signal: state = (stone, last jump size); store for each stone the set of jump sizes that can arrive there.",
                null,
                """
                static boolean canCross(int[] stones) {
                    Map<Integer, Set<Integer>> reach = new HashMap<>();
                    for (int s : stones) reach.put(s, new HashSet<>());
                    reach.get(stones[0]).add(0);
                    for (int s : stones) {
                        for (int k : reach.get(s)) {
                            for (int step = k - 1; step <= k + 1; step++) {
                                if (step > 0 && reach.containsKey(s + step)) reach.get(s + step).add(step);
                            }
                        }
                    }
                    return !reach.get(stones[stones.length - 1]).isEmpty();
                }""",
                "Each stone remembers every jump size with which the frog can land on it. From stone s with arrival jump k it can try k - 1, k and k + 1. Processing stones in increasing order guarantees all arrivals are recorded before the stone is expanded.",
                """
                reach.get(stones[0]).add(0) ==> We stand on the first stone with a 'last jump' of 0, so the first real jump is 0 + 1 = 1.
                for (int k : reach.get(s)) ==> Every way of arriving at s.
                step = k - 1 .. k + 1 ==> The allowed next jumps.
                step > 0 ==> Jumps must be forward.
                !reach.get(last).isEmpty() ==> The last stone was reachable by some jump size.""",
                "A memoised DFS on (position, k) is the other classic answer. State the state space size: n stones times O(n) jump sizes.",
                "O(n^2)", "O(n^2)", "Allowing a jump of 0 or backwards.");

        full(l, "Word Break II", "Dynamic Programming", H, "dp,backtracking,memoization,strings",
                "Given a string and a dictionary, return ALL ways to split the string into dictionary words (as sentences).",
                "Signal: DFS from each start index with memoisation of the sentences that can be built from that suffix.",
                null,
                """
                static List<String> wordBreak(String s, List<String> dict) {
                    return dfs(s, new HashSet<>(dict), 0, new HashMap<>());
                }
                static List<String> dfs(String s, Set<String> dict, int start, Map<Integer, List<String>> memo) {
                    if (memo.containsKey(start)) return memo.get(start);
                    List<String> result = new ArrayList<>();
                    if (start == s.length()) {
                        result.add("");
                        return result;
                    }
                    for (int end = start + 1; end <= s.length(); end++) {
                        String word = s.substring(start, end);
                        if (!dict.contains(word)) continue;
                        for (String rest : dfs(s, dict, end, memo)) {
                            result.add(rest.isEmpty() ? word : word + " " + rest);
                        }
                    }
                    memo.put(start, result);
                    return result;
                }""",
                "Without memoisation the same suffix is solved many times. memo[start] stores every sentence for the suffix beginning there. The result may still be exponentially large because the output itself is.",
                """
                memo ==> start index -> all sentences that can be built from s[start..].
                start == s.length() -> [""] ==> Reached the end: one valid (empty) completion, which lets the caller append nothing.
                dict.contains(word) ==> Only extend with real words.
                rest.isEmpty() ? word : word + " " + rest ==> Avoid a trailing space at the end of a sentence.""",
                "Do the Word Break I feasibility check first to prune impossible inputs (otherwise you waste time exploring dead ends). Output size dominates the complexity.",
                "O(n^2 + output)", "O(output)", "No pruning for strings like 'aaaa...b' that have no solution.");

        full(l, "Palindrome Pairs", "Strings", H, "strings,hash-map,palindrome",
                "Given distinct words, return all index pairs (i, j) such that words[i] + words[j] is a palindrome.",
                "Signal: split each word into prefix and suffix; if one part is a palindrome the reverse of the other part must exist as a word.",
                null,
                """
                static List<List<Integer>> palindromePairs(String[] words) {
                    Map<String, Integer> index = new HashMap<>();
                    for (int i = 0; i < words.length; i++) index.put(words[i], i);
                    List<List<Integer>> out = new ArrayList<>();
                    for (int i = 0; i < words.length; i++) {
                        String w = words[i];
                        for (int cut = 0; cut <= w.length(); cut++) {
                            String left = w.substring(0, cut), right = w.substring(cut);
                            if (isPal(left)) {                              // reverse(right) + w  is a palindrome
                                Integer j = index.get(new StringBuilder(right).reverse().toString());
                                if (j != null && j != i) out.add(List.of(j, i));
                            }
                            if (!right.isEmpty() && isPal(right)) {         // w + reverse(left) is a palindrome (skip cut = n: duplicate)
                                Integer j = index.get(new StringBuilder(left).reverse().toString());
                                if (j != null && j != i) out.add(List.of(i, j));
                            }
                        }
                    }
                    return out;
                }
                static boolean isPal(String s) {
                    for (int a = 0, b = s.length() - 1; a < b; a++, b--) if (s.charAt(a) != s.charAt(b)) return false;
                    return true;
                }""",
                "Comparing every pair is O(n^2 * k). If a word is split as left + right and 'left' is already a palindrome, then another word equal to reverse(right) placed in FRONT completes a palindrome; symmetrically for 'right'. A hash map finds that word in O(1).",
                """
                index ==> word -> its position for O(1) lookup of the needed partner.
                left + right ==> Every split point of the word, including the empty prefix and empty suffix.
                isPal(left) with reverse(right) in front ==> The outer parts mirror each other and the palindromic left part sits in the middle.
                !right.isEmpty() ==> Avoids producing the same pair twice when the split is at the end.
                j != i ==> A word cannot pair with itself.""",
                "Be careful with the empty string and duplicate pairs. A trie is the other standard solution. Complexity is O(n * k^2).",
                "O(n x k^2)", "O(n x k)", "Duplicate pairs from the empty prefix/suffix; pairing a word with itself.");

        full(l, "Longest Duplicate Substring (Rolling Hash)", "Strings", H, "strings,binary-search,rolling-hash",
                "Return any longest substring that appears at least twice in a string.",
                "Signal: binary search on the LENGTH; for each length check duplicates quickly with a Rabin-Karp rolling hash.",
                null,
                """
                static String longestDupSubstring(String s) {
                    int lo = 1, hi = s.length() - 1;
                    String best = "";
                    while (lo <= hi) {
                        int mid = (lo + hi) >>> 1;
                        String dup = findDuplicate(s, mid);
                        if (dup != null) { best = dup; lo = mid + 1; }
                        else hi = mid - 1;
                    }
                    return best;
                }
                static String findDuplicate(String s, int len) {
                    final long MOD = 1_000_000_007L, BASE = 131;
                    long hash = 0, pow = 1;
                    for (int i = 0; i < len; i++) {
                        hash = (hash * BASE + s.charAt(i)) % MOD;
                        if (i > 0) pow = pow * BASE % MOD;                      // BASE^(len-1)
                    }
                    Map<Long, List<Integer>> seen = new HashMap<>();
                    seen.computeIfAbsent(hash, k -> new ArrayList<>()).add(0);
                    for (int i = 1; i + len <= s.length(); i++) {
                        hash = (hash - s.charAt(i - 1) * pow % MOD + MOD) % MOD;          // remove the leftmost char
                        hash = (hash * BASE + s.charAt(i + len - 1)) % MOD;               // add the new rightmost char
                        List<Integer> starts = seen.computeIfAbsent(hash, k -> new ArrayList<>());
                        for (int st : starts) {
                            if (s.regionMatches(st, s, i, len)) return s.substring(i, i + len);   // verify: hashes can collide
                        }
                        starts.add(i);
                    }
                    return null;
                }
                }""",
                "If a duplicate of length L exists then duplicates of every shorter length exist, so the property is monotonic and we can binary search the length. For a fixed length a rolling hash updates in O(1) per position (remove the left character, add the right one). A matching hash is verified with regionMatches because collisions are possible.",
                """
                lo, hi on the LENGTH ==> The answer length is searched, not an index.
                hash = hash * BASE + char ==> Polynomial hash of the first window.
                pow = BASE^(len-1) ==> Weight of the leftmost character, used to remove it.
                hash - s[i-1] * pow ==> Slide the window: drop the left character and shift everything.
                s.regionMatches(st, s, i, len) ==> Confirm real equality when two windows share a hash.
                MOD = 1_000_000_007 ==> A large prime modulus keeps collisions rare and keeps every product below 2^63.""",
                "Interviews usually accept the idea plus a clear description of rolling hashes; production code would use suffix arrays. Mention the birthday-paradox collision risk and verification.",
                "O(n log n)", "O(n)", "Skipping verification; negative hash values after subtraction.");

        full(l, "Scramble String", "Dynamic Programming", H, "dp,recursion,memoization,strings",
                "A string can be scrambled by recursively splitting it into two parts and optionally swapping them. Return true if s2 is a scramble of s1.",
                "Signal: try every split point; the two halves must be scrambles either in order or swapped. Memoise on (start1, start2, length).",
                null,
                """
                static boolean isScramble(String s1, String s2) {
                    return check(s1, s2, new HashMap<>());
                }
                static boolean check(String a, String b, Map<String, Boolean> memo) {
                    if (a.equals(b)) return true;
                    if (a.length() != b.length()) return false;
                    String key = a + "|" + b;
                    if (memo.containsKey(key)) return memo.get(key);
                    int[] count = new int[26];
                    for (int i = 0; i < a.length(); i++) { count[a.charAt(i) - 'a']++; count[b.charAt(i) - 'a']--; }
                    for (int c : count) if (c != 0) { memo.put(key, false); return false; }       // different letters: impossible
                    int n = a.length();
                    for (int i = 1; i < n; i++) {
                        boolean noSwap = check(a.substring(0, i), b.substring(0, i), memo)
                                      && check(a.substring(i), b.substring(i), memo);
                        boolean swap = check(a.substring(0, i), b.substring(n - i), memo)
                                    && check(a.substring(i), b.substring(0, n - i), memo);
                        if (noSwap || swap) { memo.put(key, true); return true; }
                    }
                    memo.put(key, false);
                    return false;
                }""",
                "At each split the left part of s1 corresponds either to the left part of s2 (no swap) or to the right part (swap). The letter-count check is a cheap pruning step that removes most impossible branches before recursion.",
                """
                a.equals(b) ==> Base case: equal strings are trivially scrambles.
                count[] difference ==> If the multisets of letters differ the answer is false immediately.
                noSwap ==> The first parts match each other and the second parts match each other.
                swap ==> The first part of a matches the LAST i characters of b and the rest matches the beginning.
                memo ==> Same pairs of substrings appear repeatedly; cache them.""",
                "Mention complexity O(n^4) states-by-work. A 3D boolean DP over (i, j, len) is the bottom-up version.",
                "O(n^4)", "O(n^3)", "Forgetting the letter-count pruning (exponential time).");

        full(l, "Shortest Common Supersequence", "Dynamic Programming", H, "dp,strings,lcs,reconstruction",
                "Return the shortest string that has both str1 and str2 as subsequences.",
                "Signal: build the LCS table, then walk it backwards writing characters, taking the shared characters only once.",
                null,
                """
                static String shortestCommonSupersequence(String a, String b) {
                    int n = a.length(), m = b.length();
                    int[][] dp = new int[n + 1][m + 1];
                    for (int i = 1; i <= n; i++)
                        for (int j = 1; j <= m; j++)
                            dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1)
                                    ? dp[i - 1][j - 1] + 1
                                    : Math.max(dp[i - 1][j], dp[i][j - 1]);
                    StringBuilder sb = new StringBuilder();
                    int i = n, j = m;
                    while (i > 0 && j > 0) {
                        if (a.charAt(i - 1) == b.charAt(j - 1)) { sb.append(a.charAt(i - 1)); i--; j--; }
                        else if (dp[i - 1][j] >= dp[i][j - 1]) sb.append(a.charAt(--i));
                        else sb.append(b.charAt(--j));
                    }
                    while (i > 0) sb.append(a.charAt(--i));
                    while (j > 0) sb.append(b.charAt(--j));
                    return sb.reverse().toString();
                }""",
                "The length of the shortest supersequence is n + m - LCS. To build it, follow the table from the end: a matching character is output once; otherwise output the character from the string whose removal keeps the LCS larger, then append any leftovers and reverse.",
                """
                dp[i][j] ==> LCS length of the first i characters of a and first j of b.
                a[i-1] == b[j-1] ==> A shared character appears once in the answer and we step diagonally.
                dp[i-1][j] >= dp[i][j-1] ==> Move up (output a's character) when that keeps the LCS at least as large; otherwise move left.
                while (i > 0) / while (j > 0) ==> Append the remaining prefix of whichever string is not finished.
                sb.reverse() ==> We built the answer from the end.""",
                "Know the relation SCS length = n + m - LCS. Reconstruction from a DP table is a skill interviewers like to see.",
                "O(n x m)", "O(n x m)", "Appending shared characters twice.");

        full(l, "Minimum Number of Refueling Stops", "Greedy", H, "greedy,heap,dp",
                "A car starts with startFuel and must reach a target distance. Stations are at given positions with a fuel amount. Return the fewest stops needed, or -1.",
                "Signal: drive as far as possible, remember the fuel of passed stations in a max-heap, and refuel from the biggest one only when you must.",
                null,
                """
                static int minRefuelStops(int target, int startFuel, int[][] stations) {
                    PriorityQueue<Integer> passed = new PriorityQueue<>(Collections.reverseOrder());
                    long reach = startFuel;
                    int stops = 0, i = 0;
                    while (reach < target) {
                        while (i < stations.length && stations[i][0] <= reach) passed.add(stations[i++][1]);
                        if (passed.isEmpty()) return -1;                    // stuck: no station in range
                        reach += passed.poll();                             // retroactively refuel at the best passed station
                        stops++;
                    }
                    return stops;
                }""",
                "Instead of deciding at each station, delay the decision: keep driving, collecting fuel amounts of stations we passed. When we run out, 'pretend' we stopped at the richest passed station. This greedy choice is optimal because any needed stop can be exchanged for the largest available one.",
                """
                reach ==> The farthest distance reachable with the fuel and stops used so far (long to avoid overflow).
                stations[i][0] <= reach ==> Stations we could have stopped at.
                passed (max-heap) ==> Fuel amounts of those stations, biggest on top.
                reach += passed.poll() ==> Use the largest tank when forced to refuel.
                passed.isEmpty() ==> Nothing in range: the target cannot be reached.""",
                "A DP over (stops, fuel) also works in O(n^2); the heap approach is the expected optimal answer. Explain the 'retroactive' greedy idea.",
                "O(n log n)", "O(n)", "Refuelling at every station or at the nearest one.");

        return l;
    }
}
