package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.full;

/** Intermediate set A: arrays, matrices and strings. */
public final class MediumArraysStrings {

    private MediumArraysStrings() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty M = Difficulty.MEDIUM;

        full(l, "Sort Colors (Dutch National Flag)", "Two Pointers", M, "arrays,two-pointers,partition",
                "An array contains only 0, 1 and 2. Sort it in place in one pass without using a library sort.",
                "Signal: three categories and in-place -> three pointers (low, mid, high).",
                """
                static void sortColors(int[] a) {
                    int[] count = new int[3];
                    for (int x : a) count[x]++;
                    int i = 0;
                    for (int color = 0; color < 3; color++)
                        while (count[color]-- > 0) a[i++] = color;
                }""",
                """
                static void sortColors(int[] a) {
                    int low = 0, mid = 0, high = a.length - 1;
                    while (mid <= high) {
                        if (a[mid] == 0) swap(a, low++, mid++);
                        else if (a[mid] == 1) mid++;
                        else swap(a, mid, high--);
                    }
                }
                static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }""",
                "Counting needs two passes. The Dutch flag algorithm keeps four zones: [0..low) zeros, [low..mid) ones, [mid..high] unknown, (high..end] twos, and shrinks the unknown zone each step.",
                """
                low, mid, high ==> low marks the end of the zeros, mid is the element being examined, high marks the start of the twos.
                a[mid] == 0 -> swap(low++, mid++) ==> Send a zero to the front zone; the value swapped into mid is known to be a 1, so both pointers advance.
                a[mid] == 1 -> mid++ ==> A one is already in the right zone.
                a[mid] == 2 -> swap(mid, high--) ==> Send a two to the back; do NOT advance mid because the swapped-in value has not been checked yet.
                while (mid <= high) ==> Stop when the unknown zone is empty.""",
                "Explain the loop invariant (what each zone contains). The bug magnet is not advancing mid after swapping with high. It generalises to quicksort partitioning.",
                "O(n)", "O(1)", "Advancing mid after swapping with high; using <= vs < on the loop condition.");

        full(l, "Next Permutation", "Arrays", M, "arrays,permutation,in-place",
                "Rearrange numbers into the next lexicographically greater permutation. If none exists, rearrange into the smallest (sorted ascending).",
                "Signal: find the rightmost 'rise', swap with the next larger value to its right, then reverse the tail.",
                null,
                """
                static void nextPermutation(int[] a) {
                    int i = a.length - 2;
                    while (i >= 0 && a[i] >= a[i + 1]) i--;       // find the first drop from the right
                    if (i >= 0) {
                        int j = a.length - 1;
                        while (a[j] <= a[i]) j--;                 // next larger value to the right
                        swap(a, i, j);
                    }
                    reverse(a, i + 1, a.length - 1);              // smallest arrangement of the tail
                }
                static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }
                static void reverse(int[] a, int lo, int hi) { while (lo < hi) swap(a, lo++, hi--); }""",
                "The suffix after position i is in descending order, i.e. already the largest arrangement. To get the next permutation, bump a[i] to the next larger value from that suffix and make the suffix as small as possible (ascending = reversed).",
                """
                while (i >= 0 && a[i] >= a[i + 1]) i-- ==> Walk left while the sequence is non-increasing; the first element that is smaller than its right neighbour is the pivot.
                while (a[j] <= a[i]) j-- ==> The smallest value in the tail that is still bigger than the pivot (the tail is descending, so search from the right).
                swap(a, i, j) ==> Increase the pivot by the smallest possible amount.
                reverse(a, i + 1, end) ==> The tail was descending; reversing makes it ascending, the smallest possible suffix.
                i == -1 ==> The whole array was descending (largest permutation); reversing everything gives the smallest.""",
                "Usually asked as 'explain it before coding'. Walk through [1,3,2] -> [2,1,3]. Know the relationship with generating all permutations in order.",
                "O(n)", "O(1)", "Using > instead of >= (duplicates); forgetting the reverse step.");

        full(l, "Spiral Matrix", "Matrix", M, "matrix,simulation,boundaries",
                "Return all elements of an m x n matrix in clockwise spiral order.",
                "Signal: shrink four boundaries (top, bottom, left, right) after each side is walked.",
                null,
                """
                static List<Integer> spiral(int[][] m) {
                    List<Integer> out = new ArrayList<>();
                    int top = 0, bottom = m.length - 1, left = 0, right = m[0].length - 1;
                    while (top <= bottom && left <= right) {
                        for (int c = left; c <= right; c++) out.add(m[top][c]);
                        top++;
                        for (int r = top; r <= bottom; r++) out.add(m[r][right]);
                        right--;
                        if (top <= bottom) {
                            for (int c = right; c >= left; c--) out.add(m[bottom][c]);
                            bottom--;
                        }
                        if (left <= right) {
                            for (int r = bottom; r >= top; r--) out.add(m[r][left]);
                            left++;
                        }
                    }
                    return out;
                }""",
                "Walk the top row, right column, bottom row and left column, shrinking the boundary after each. The two extra if checks stop a single remaining row or column from being read twice.",
                """
                top, bottom, left, right ==> The rectangle of cells not yet visited.
                for (c = left..right) out.add(m[top][c]); top++ ==> Walk the top side left to right, then remove that row.
                for (r = top..bottom) out.add(m[r][right]); right-- ==> Walk down the right side, then remove that column.
                if (top <= bottom) ==> Before walking the bottom side, make sure a row still exists (a single row would be repeated).
                if (left <= right) ==> Same safety check before walking up the left side.""",
                "Boundary problems are about the last layer: test 1xN, Nx1 and odd squares. Say what the invariant is.",
                "O(m x n)", "O(1) extra", "Duplicating the middle row/column in thin matrices.");

        full(l, "Rotate Image 90 Degrees", "Matrix", M, "matrix,in-place,transpose",
                "Rotate an n x n matrix 90 degrees clockwise in place.",
                "Signal: rotation = transpose, then reverse every row.",
                """
                static int[][] rotate(int[][] m) {
                    int n = m.length;
                    int[][] r = new int[n][n];
                    for (int i = 0; i < n; i++)
                        for (int j = 0; j < n; j++)
                            r[j][n - 1 - i] = m[i][j];
                    return r;
                }""",
                """
                static void rotate(int[][] m) {
                    int n = m.length;
                    for (int i = 0; i < n; i++)                       // transpose
                        for (int j = i + 1; j < n; j++) {
                            int t = m[i][j]; m[i][j] = m[j][i]; m[j][i] = t;
                        }
                    for (int[] row : m) {                             // reverse each row
                        for (int a = 0, b = n - 1; a < b; a++, b--) {
                            int t = row[a]; row[a] = row[b]; row[b] = t;
                        }
                    }
                }""",
                "Cell (i, j) moves to (j, n-1-i). That is a transpose (swap i and j) followed by a horizontal mirror (j becomes n-1-j). Doing it in two simple steps avoids tracking four-way swaps.",
                """
                for (j = i + 1; ...) swap m[i][j], m[j][i] ==> Transpose using only the upper triangle so each pair is swapped once.
                for (int[] row : m) ==> Process each row after transposing.
                a < b; a++, b-- ==> Two pointers from both ends reverse the row in place.
                r[j][n - 1 - i] = m[i][j] ==> The direct mapping used by the extra-memory version.""",
                "Counter-clockwise needs reverse rows first or reverse columns after transpose. Know both.",
                "O(n^2)", "O(1)", "Transposing the full matrix (swaps back to the original).");

        full(l, "Set Matrix Zeroes", "Matrix", M, "matrix,in-place,marking",
                "If a matrix cell is 0, set its entire row and column to 0, in place.",
                "Signal: store 'which rows/columns need zeroing' without extra memory by using the first row and column as markers.",
                """
                static void setZeroes(int[][] m) {
                    int R = m.length, C = m[0].length;
                    boolean[] row = new boolean[R], col = new boolean[C];
                    for (int i = 0; i < R; i++)
                        for (int j = 0; j < C; j++)
                            if (m[i][j] == 0) { row[i] = true; col[j] = true; }
                    for (int i = 0; i < R; i++)
                        for (int j = 0; j < C; j++)
                            if (row[i] || col[j]) m[i][j] = 0;
                }""",
                """
                static void setZeroes(int[][] m) {
                    int R = m.length, C = m[0].length;
                    boolean firstRowZero = false, firstColZero = false;
                    for (int j = 0; j < C; j++) if (m[0][j] == 0) firstRowZero = true;
                    for (int i = 0; i < R; i++) if (m[i][0] == 0) firstColZero = true;
                    for (int i = 1; i < R; i++)
                        for (int j = 1; j < C; j++)
                            if (m[i][j] == 0) { m[i][0] = 0; m[0][j] = 0; }
                    for (int i = 1; i < R; i++)
                        for (int j = 1; j < C; j++)
                            if (m[i][0] == 0 || m[0][j] == 0) m[i][j] = 0;
                    if (firstRowZero) for (int j = 0; j < C; j++) m[0][j] = 0;
                    if (firstColZero) for (int i = 0; i < R; i++) m[i][0] = 0;
                }""",
                "Marker arrays cost O(m+n) space. Reusing the first row and column as the markers brings it to O(1), but you must remember separately whether the first row and column themselves contained a zero.",
                """
                firstRowZero / firstColZero ==> The first row and column double as marker storage, so their own zeros must be remembered before they are overwritten.
                m[i][0] = 0; m[0][j] = 0; ==> Mark that row i and column j must be cleared.
                if (m[i][0] == 0 || m[0][j] == 0) m[i][j] = 0 ==> Second pass: clear cells whose row or column is marked (skipping the marker row/column).
                final two ifs ==> Clear the first row/column last, otherwise the markers would be destroyed too early.""",
                "The order of operations is the trap. State clearly: record, mark, clear inner cells, clear first row and column.",
                "O(m x n)", "O(1)", "Zeroing as you discover zeros (cascades across the whole matrix).");

        full(l, "Find All Duplicates in an Array", "Arrays", M, "arrays,in-place,index-marking",
                "An array of n integers has values in 1..n and some appear twice. Return all values that appear twice in O(n) time and O(1) extra space.",
                "Signal: values are valid indexes, so mark 'seen' by negating the number at that index.",
                """
                static List<Integer> duplicates(int[] a) {
                    Set<Integer> seen = new HashSet<>();
                    List<Integer> out = new ArrayList<>();
                    for (int x : a) if (!seen.add(x)) out.add(x);
                    return out;
                }""",
                """
                static List<Integer> duplicates(int[] a) {
                    List<Integer> out = new ArrayList<>();
                    for (int i = 0; i < a.length; i++) {
                        int idx = Math.abs(a[i]) - 1;
                        if (a[idx] < 0) out.add(idx + 1);
                        else a[idx] = -a[idx];
                    }
                    return out;
                }""",
                "The sign of a[idx] is used as a free 'visited' flag for value idx + 1. If it is already negative, the value was seen before.",
                """
                Math.abs(a[i]) - 1 ==> The value (possibly already negated) converted to a 0-based index.
                a[idx] < 0 ==> Negative means we visited this value earlier: it is a duplicate.
                a[idx] = -a[idx] ==> First time: flip the sign to record the visit.
                out.add(idx + 1) ==> Convert back from index to the original value.""",
                "A 'use the input as storage' trick. State the precondition (values in 1..n) and that it modifies the input.",
                "O(n)", "O(1)", "Forgetting Math.abs when reading an element that was already negated.");

        full(l, "Longest Consecutive Sequence", "Hash Set", M, "arrays,hash-set,sequence",
                "Given an unsorted array, return the length of the longest run of consecutive integers (e.g. 100, 4, 200, 1, 3, 2 -> 4), in O(n).",
                "Signal: only start counting from a number whose predecessor is NOT in the set.",
                """
                static int longest(int[] a) {
                    if (a.length == 0) return 0;
                    int[] s = a.clone();
                    Arrays.sort(s);
                    int best = 1, run = 1;
                    for (int i = 1; i < s.length; i++) {
                        if (s[i] == s[i - 1]) continue;
                        run = (s[i] == s[i - 1] + 1) ? run + 1 : 1;
                        best = Math.max(best, run);
                    }
                    return best;
                }""",
                """
                static int longest(int[] a) {
                    Set<Integer> set = new HashSet<>();
                    for (int x : a) set.add(x);
                    int best = 0;
                    for (int x : set) {
                        if (!set.contains(x - 1)) {                // x starts a sequence
                            int len = 1;
                            while (set.contains(x + len)) len++;
                            best = Math.max(best, len);
                        }
                    }
                    return best;
                }""",
                "Sorting is O(n log n). With a set, each sequence is counted once, from its first element, so every number is visited at most twice overall.",
                """
                Set<Integer> set ==> O(1) membership test and duplicate removal.
                !set.contains(x - 1) ==> If x - 1 exists, x is in the middle of a sequence; skipping it prevents recounting and keeps the total work linear.
                while (set.contains(x + len)) len++ ==> Extend the run from its start.
                Math.max(best, len) ==> Record the best run.""",
                "The 'start only at the beginning of a run' idea is the whole trick. Be ready to prove O(n): each element is part of exactly one run.",
                "O(n)", "O(n)", "Counting from every element (O(n^2) in the worst case).");

        full(l, "Insert Interval", "Intervals", M, "intervals,arrays,merge",
                "Given sorted, non-overlapping intervals and a new interval, insert it and merge any overlaps.",
                "Signal: three phases - copy intervals that end before the new one, merge the overlapping ones, copy the rest.",
                null,
                """
                static int[][] insert(int[][] intervals, int[] add) {
                    List<int[]> out = new ArrayList<>();
                    int i = 0, n = intervals.length;
                    while (i < n && intervals[i][1] < add[0]) out.add(intervals[i++]);        // entirely before
                    while (i < n && intervals[i][0] <= add[1]) {                               // overlapping
                        add[0] = Math.min(add[0], intervals[i][0]);
                        add[1] = Math.max(add[1], intervals[i][1]);
                        i++;
                    }
                    out.add(add);
                    while (i < n) out.add(intervals[i++]);                                    // entirely after
                    return out.toArray(new int[0][]);
                }""",
                "Because the input is sorted and disjoint, the new interval can only overlap a contiguous block. Phase one skips what ends before it starts, phase two absorbs all overlaps, phase three copies the rest.",
                """
                intervals[i][1] < add[0] ==> The existing interval ends before the new one starts: no overlap, copy it unchanged.
                intervals[i][0] <= add[1] ==> The existing interval starts before the new one ends: they overlap (the first phase guarantees it also ends after the new start).
                add[0] = Math.min(...), add[1] = Math.max(...) ==> Grow the new interval to cover everything it touches.
                out.add(add) ==> Add the merged result once, after all overlaps are absorbed.
                while (i < n) out.add(intervals[i++]) ==> Everything left starts after the merged interval ends.""",
                "Draw timelines. The three-phase structure is the answer interviewers want; no sorting is needed because the input is already sorted.",
                "O(n)", "O(n)", "Using < instead of <= and failing to merge touching intervals (depending on the definition).");

        full(l, "Jump Game", "Greedy", M, "greedy,arrays,reachability",
                "Each element is the maximum jump length from that position. Starting at index 0, can you reach the last index?",
                "Signal: track the furthest index reachable so far; if you are ever past it, you are stuck.",
                null,
                """
                static boolean canJump(int[] nums) {
                    int furthest = 0;
                    for (int i = 0; i < nums.length; i++) {
                        if (i > furthest) return false;            // cannot even reach i
                        furthest = Math.max(furthest, i + nums[i]);
                        if (furthest >= nums.length - 1) return true;
                    }
                    return true;
                }""",
                "Trying every jump with recursion is exponential; DP is O(n^2). The greedy view needs only one number: the furthest reachable index. Reaching it means every index before it is reachable too.",
                """
                furthest ==> The largest index we know how to reach.
                i > furthest ==> We arrived at an index we cannot reach: the answer is false.
                i + nums[i] ==> The furthest index reachable by jumping from i.
                furthest >= nums.length - 1 ==> The end is within reach; stop early.""",
                "Explain why greedy is correct: the reachable set is always a prefix [0..furthest]. A follow-up is Jump Game II (minimum jumps).",
                "O(n)", "O(1)", "Treating nums[i] as an exact jump length rather than a maximum.");

        full(l, "Maximum Product Subarray", "Dynamic Programming", M, "dp,arrays,negatives",
                "Return the largest product of a contiguous subarray. The array may contain negatives and zeros.",
                "Signal: a very negative product can become the maximum after multiplying by another negative, so track both max and min.",
                """
                static int maxProduct(int[] a) {
                    int best = a[0];
                    for (int i = 0; i < a.length; i++) {
                        int p = 1;
                        for (int j = i; j < a.length; j++) {
                            p *= a[j];
                            best = Math.max(best, p);
                        }
                    }
                    return best;
                }""",
                """
                static int maxProduct(int[] a) {
                    int max = a[0], min = a[0], best = a[0];
                    for (int i = 1; i < a.length; i++) {
                        int x = a[i];
                        if (x < 0) { int t = max; max = min; min = t; }   // a negative flips ordering
                        max = Math.max(x, max * x);
                        min = Math.min(x, min * x);
                        best = Math.max(best, max);
                    }
                    return best;
                }""",
                "At each element keep the biggest and smallest product of a subarray ending here. Multiplying by a negative number turns the smallest into the biggest, so they swap roles.",
                """
                max, min ==> Best and worst product of a subarray that ends at the current index.
                if (x < 0) swap(max, min) ==> A negative multiplier reverses order, so the old minimum becomes the new maximum candidate.
                max = Math.max(x, max * x) ==> Either start fresh at x or extend the earlier product.
                min = Math.min(x, min * x) ==> The same for the minimum, needed for later negatives.
                best = Math.max(best, max) ==> Track the global answer.""",
                "Compare with Maximum Subarray (sum). The extra min variable is the insight to verbalise.",
                "O(n)", "O(1)", "Tracking only the maximum; resetting wrongly on zeros.");

        full(l, "Find Minimum in Rotated Sorted Array", "Binary Search", M, "binary-search,rotation",
                "A sorted array without duplicates was rotated. Return the minimum element in O(log n).",
                "Signal: compare mid with the right end to decide which half contains the pivot.",
                """
                static int findMin(int[] a) {
                    int min = a[0];
                    for (int x : a) min = Math.min(min, x);
                    return min;
                }""",
                """
                static int findMin(int[] a) {
                    int lo = 0, hi = a.length - 1;
                    while (lo < hi) {
                        int mid = (lo + hi) >>> 1;
                        if (a[mid] > a[hi]) lo = mid + 1;     // minimum is to the right of mid
                        else hi = mid;                        // mid could be the minimum
                    }
                    return a[lo];
                }""",
                "If a[mid] is bigger than a[hi], the drop (and the minimum) lies right of mid. Otherwise the right part is sorted so the minimum is at mid or to its left.",
                """
                lo < hi ==> We narrow the range until one element remains; no equality check is needed.
                a[mid] > a[hi] ==> The range from mid to hi contains the rotation point.
                lo = mid + 1 ==> mid itself cannot be the minimum because a larger value sits there.
                hi = mid ==> Keep mid; it might be the minimum.
                return a[lo] ==> Both pointers meet at the answer.""",
                "Comparing with the right end (not the left) avoids many corner cases. With duplicates the worst case degrades to O(n); mention it.",
                "O(log n)", "O(1)", "Comparing with a[lo] and mishandling the unrotated case.");

        full(l, "Find Peak Element", "Binary Search", M, "binary-search,arrays",
                "A peak is greater than its neighbours. Return the index of any peak in O(log n); a[-1] and a[n] are treated as negative infinity.",
                "Signal: follow the upward slope; a peak must exist in that direction.",
                """
                static int findPeak(int[] a) {
                    for (int i = 0; i < a.length; i++) {
                        boolean leftOk = i == 0 || a[i] > a[i - 1];
                        boolean rightOk = i == a.length - 1 || a[i] > a[i + 1];
                        if (leftOk && rightOk) return i;
                    }
                    return -1;
                }""",
                """
                static int findPeak(int[] a) {
                    int lo = 0, hi = a.length - 1;
                    while (lo < hi) {
                        int mid = (lo + hi) >>> 1;
                        if (a[mid] < a[mid + 1]) lo = mid + 1;    // slope goes up: a peak is to the right
                        else hi = mid;                            // slope goes down: a peak is at mid or to the left
                    }
                    return lo;
                }""",
                "If the next element is higher, walking right either ends at a peak or keeps climbing until the array end (a peak by the boundary rule). So a peak is guaranteed in the half you move toward.",
                """
                a[mid] < a[mid + 1] ==> Climbing to the right.
                lo = mid + 1 ==> A peak exists in (mid, hi]; mid itself is not a peak.
                else hi = mid ==> Descending (or flat top): a peak lies in [lo, mid].
                return lo ==> When the range has one element, it is a peak.""",
                "The surprising part is that binary search works on an unsorted array. Justify it with the climbing argument.",
                "O(log n)", "O(1)", "Reading mid + 1 beyond the array bounds.");

        full(l, "Longest Palindromic Substring", "Strings", M, "strings,expand-around-center,dp",
                "Return the longest palindromic substring of a string.",
                "Signal: every palindrome has a centre; expand outward from each possible centre.",
                """
                static String longestPalindrome(String s) {
                    String best = "";
                    for (int i = 0; i < s.length(); i++)
                        for (int j = i; j < s.length(); j++) {
                            String t = s.substring(i, j + 1);
                            if (t.length() > best.length() && isPal(t)) best = t;
                        }
                    return best;
                }
                static boolean isPal(String t) {
                    for (int a = 0, b = t.length() - 1; a < b; a++, b--) if (t.charAt(a) != t.charAt(b)) return false;
                    return true;
                }""",
                """
                static String longestPalindrome(String s) {
                    int start = 0, end = 0;
                    for (int i = 0; i < s.length(); i++) {
                        int len = Math.max(expand(s, i, i), expand(s, i, i + 1));   // odd and even centres
                        if (len > end - start) {
                            start = i - (len - 1) / 2;
                            end = i + len / 2;
                        }
                    }
                    return s.substring(start, end + 1);
                }
                static int expand(String s, int l, int r) {
                    while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) { l--; r++; }
                    return r - l - 1;
                }""",
                "Checking all substrings is O(n^3). Expanding from 2n-1 centres is O(n^2) with no extra memory (Manacher's algorithm reaches O(n)).",
                """
                expand(s, i, i) ==> Odd-length palindromes centred on the character i.
                expand(s, i, i + 1) ==> Even-length palindromes centred between i and i + 1.
                while (l >= 0 && r < n && s[l] == s[r]) ==> Grow outward while the ends match and stay inside the string.
                return r - l - 1 ==> After the loop l and r are one step too far, so the length is r - l - 1.
                start = i - (len - 1) / 2 ==> Convert the centre and length back to the start index.""",
                "Mention both odd and even centres without being prompted, and name Manacher as the O(n) follow-up.",
                "O(n^2)", "O(1)", "Forgetting even-length palindromes.");

        full(l, "Count Palindromic Substrings", "Strings", M, "strings,expand-around-center",
                "Count how many substrings of a string are palindromes (positions count separately).",
                "Signal: the same centre-expansion idea, but count every successful expansion.",
                null,
                """
                static int countSubstrings(String s) {
                    int count = 0;
                    for (int centre = 0; centre < 2 * s.length() - 1; centre++) {
                        int l = centre / 2;
                        int r = l + centre % 2;           // even centres start with r = l + 1
                        while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
                            count++;
                            l--;
                            r++;
                        }
                    }
                    return count;
                }""",
                "There are 2n - 1 centres (n characters and n - 1 gaps). Each expansion step that still matches is one more palindrome, so counting during expansion gives the total.",
                """
                centre < 2 * s.length() - 1 ==> Characters and the gaps between them are all possible centres.
                l = centre / 2 ==> The left start index.
                r = l + centre % 2 ==> Odd centre: r = l (single char). Even centre: r = l + 1 (gap).
                count++ inside the while ==> Every time the ends still match, the current window is a new palindrome.""",
                "A natural follow-up to the longest palindromic substring. Easy to extend to a DP table if asked.",
                "O(n^2)", "O(1)", "Counting only distinct substrings by mistake.");

        full(l, "String to Integer (atoi)", "Strings", M, "strings,parsing,edge-cases",
                "Implement atoi: skip leading spaces, read an optional sign, read digits, stop at the first non-digit and clamp to the 32-bit integer range.",
                "Signal: a careful state-by-state parse, with overflow checked BEFORE it happens.",
                null,
                """
                static int myAtoi(String s) {
                    int i = 0, n = s.length();
                    while (i < n && s.charAt(i) == ' ') i++;
                    int sign = 1;
                    if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
                        sign = s.charAt(i) == '-' ? -1 : 1;
                        i++;
                    }
                    int result = 0;
                    while (i < n && Character.isDigit(s.charAt(i))) {
                        int d = s.charAt(i) - '0';
                        if (result > (Integer.MAX_VALUE - d) / 10) {
                            return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                        }
                        result = result * 10 + d;
                        i++;
                    }
                    return sign * result;
                }""",
                "The logic follows the specification step by step. The overflow test rearranges result * 10 + d > MAX into result > (MAX - d) / 10 so that the check itself cannot overflow.",
                """
                while (s.charAt(i) == ' ') i++ ==> Skip leading whitespace.
                sign handling ==> Accept at most one + or - immediately before the digits.
                Character.isDigit ==> Stop at the first non-digit; the rest of the string is ignored.
                result > (MAX - d) / 10 ==> Would result * 10 + d exceed Integer.MAX_VALUE? Tested without risking overflow.
                return sign == 1 ? MAX : MIN ==> Clamp as the specification requires.""",
                "This is an edge-case checklist problem: empty string, only spaces, sign only, leading zeros, letters after digits, overflow in both directions.",
                "O(n)", "O(1)", "Overflowing in the check; accepting more than one sign.");

        full(l, "Valid Sudoku", "Hash Set", M, "matrix,hash-set,validation",
                "Check if a 9x9 board (cells hold '1'-'9' or '.') is a valid partial Sudoku: no repeats in any row, column or 3x3 box.",
                "Signal: one set per row, per column and per box; the box index is (r / 3) * 3 + c / 3.",
                null,
                """
                static boolean isValidSudoku(char[][] b) {
                    Set<String> seen = new HashSet<>();
                    for (int r = 0; r < 9; r++) {
                        for (int c = 0; c < 9; c++) {
                            char ch = b[r][c];
                            if (ch == '.') continue;
                            if (!seen.add(ch + " in row " + r)
                                    || !seen.add(ch + " in col " + c)
                                    || !seen.add(ch + " in box " + (r / 3) + "-" + (c / 3))) {
                                return false;
                            }
                        }
                    }
                    return true;
                }""",
                "One pass over the 81 cells. Each digit generates three labelled facts; if any fact was already recorded, a rule was broken. add returns false for a repeat.",
                """
                Set<String> seen ==> A single set holding facts like '5 in row 0'.
                if (ch == '.') continue ==> Empty cells carry no constraint.
                seen.add(ch + " in row " + r) ==> Returns false if the digit was already in this row.
                (r / 3) + "-" + (c / 3) ==> Integer division groups the 81 cells into nine 3x3 boxes.
                || short-circuit ==> Stops at the first violation.""",
                "A clean way to verbalise it: 'every digit asserts three facts'. Follow-up: solve the Sudoku (backtracking).",
                "O(81) = O(1)", "O(1)", "Wrong box index; forgetting that '.' is allowed.");

        full(l, "Longest Repeating Character Replacement", "Sliding Window", M, "sliding-window,strings",
                "You may replace at most k characters. Return the length of the longest substring of identical letters you can obtain.",
                "Signal: a window is valid if (window size - count of the most frequent letter) <= k.",
                null,
                """
                static int characterReplacement(String s, int k) {
                    int[] count = new int[26];
                    int left = 0, maxFreq = 0, best = 0;
                    for (int right = 0; right < s.length(); right++) {
                        maxFreq = Math.max(maxFreq, ++count[s.charAt(right) - 'A']);
                        while ((right - left + 1) - maxFreq > k) {
                            count[s.charAt(left) - 'A']--;
                            left++;
                        }
                        best = Math.max(best, right - left + 1);
                    }
                    return best;
                }""",
                "Keep the most frequent letter in the window; everything else must be replaced. If more than k characters need replacing, shrink from the left. maxFreq is never decreased because a smaller value cannot produce a longer answer.",
                """
                count[...] ==> Frequency of each letter inside the window.
                maxFreq ==> The highest frequency seen in any window so far (an upper bound is enough).
                (right - left + 1) - maxFreq > k ==> Characters to replace exceed the allowance: the window is invalid.
                count[s.charAt(left)]--; left++ ==> Shrink the window from the left.
                best = Math.max(best, right - left + 1) ==> Record the longest valid window.""",
                "The subtle point (not recomputing maxFreq when shrinking) is a classic; explain why it is still correct.",
                "O(n)", "O(1)", "Recomputing the maximum by scanning 26 counts every time (still fine, but explain).");

        full(l, "Permutation in String", "Sliding Window", M, "sliding-window,strings,anagram",
                "Return true if s2 contains a permutation of s1 as a substring.",
                "Signal: a fixed-size window of length s1.length() whose letter counts must equal s1's counts.",
                null,
                """
                static boolean checkInclusion(String s1, String s2) {
                    if (s1.length() > s2.length()) return false;
                    int[] need = new int[26], have = new int[26];
                    for (int i = 0; i < s1.length(); i++) {
                        need[s1.charAt(i) - 'a']++;
                        have[s2.charAt(i) - 'a']++;
                    }
                    if (Arrays.equals(need, have)) return true;
                    for (int i = s1.length(); i < s2.length(); i++) {
                        have[s2.charAt(i) - 'a']++;
                        have[s2.charAt(i - s1.length()) - 'a']--;
                        if (Arrays.equals(need, have)) return true;
                    }
                    return false;
                }""",
                "Slide a window of fixed length across s2, adding the new letter and removing the old one, and compare 26-slot count arrays (a constant-time comparison).",
                """
                need ==> The letter counts of s1: the target.
                have ==> The letter counts inside the current window of s2.
                have[new]++ and have[old]-- ==> Slide by one position in O(1).
                Arrays.equals(need, have) ==> Same counts means the window is a permutation of s1.""",
                "Same pattern as 'Find All Anagrams in a String'. Complexity: 26 * n which is O(n).",
                "O(n)", "O(1)", "Rebuilding the counts for every window (O(n * m)).");

        full(l, "Find All Anagrams in a String", "Sliding Window", M, "sliding-window,strings,anagram",
                "Return all start indices of substrings of s that are anagrams of p.",
                "Signal: the same fixed window as Permutation in String, but collect every match.",
                null,
                """
                static List<Integer> findAnagrams(String s, String p) {
                    List<Integer> out = new ArrayList<>();
                    if (p.length() > s.length()) return out;
                    int[] need = new int[26], have = new int[26];
                    for (char c : p.toCharArray()) need[c - 'a']++;
                    for (int i = 0; i < s.length(); i++) {
                        have[s.charAt(i) - 'a']++;
                        if (i >= p.length()) have[s.charAt(i - p.length()) - 'a']--;
                        if (Arrays.equals(need, have)) out.add(i - p.length() + 1);
                    }
                    return out;
                }""",
                "One loop does both the entering and leaving of letters; once the window is full its start index is i - len + 1.",
                """
                have[s.charAt(i) - 'a']++ ==> The letter entering on the right.
                i >= p.length() ==> The window is now longer than p, so a letter must leave on the left.
                have[s.charAt(i - p.length()) - 'a']-- ==> The leaving letter.
                out.add(i - p.length() + 1) ==> Convert the end index to the start index of the matching window.""",
                "Ask whether letters are lowercase only; otherwise use a HashMap or 128/256-size array.",
                "O(n)", "O(1)", "Off-by-one in the start index.");

        full(l, "Minimum Size Subarray Sum", "Sliding Window", M, "sliding-window,arrays,positive-numbers",
                "Given positive integers and a target, return the smallest length of a contiguous subarray whose sum is at least target, or 0.",
                "Signal: all numbers are positive, so a window can grow to increase the sum and shrink to decrease it.",
                """
                static int minSubArrayLen(int target, int[] a) {
                    int best = Integer.MAX_VALUE;
                    for (int i = 0; i < a.length; i++) {
                        int sum = 0;
                        for (int j = i; j < a.length; j++) {
                            sum += a[j];
                            if (sum >= target) { best = Math.min(best, j - i + 1); break; }
                        }
                    }
                    return best == Integer.MAX_VALUE ? 0 : best;
                }""",
                """
                static int minSubArrayLen(int target, int[] a) {
                    int left = 0, sum = 0, best = Integer.MAX_VALUE;
                    for (int right = 0; right < a.length; right++) {
                        sum += a[right];
                        while (sum >= target) {
                            best = Math.min(best, right - left + 1);
                            sum -= a[left++];
                        }
                    }
                    return best == Integer.MAX_VALUE ? 0 : best;
                }""",
                "Positivity makes the sum monotonic: adding an element never decreases it and removing one never increases it. Each element is added once and removed once.",
                """
                sum += a[right] ==> Grow the window to the right.
                while (sum >= target) ==> The window is valid; try to make it shorter.
                best = Math.min(best, right - left + 1) ==> Record the length of this valid window.
                sum -= a[left++] ==> Shrink from the left and look for an even shorter valid window.""",
                "If negatives are allowed, sliding window fails and you need prefix sums with a deque or binary search.",
                "O(n)", "O(1)", "Using this approach when the array contains negatives.");

        return l;
    }
}
