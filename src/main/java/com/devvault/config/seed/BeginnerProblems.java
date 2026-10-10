package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.add;

/** 35 beginner problems: classic warm-ups plus small real-website features. */
public final class BeginnerProblems {

    private BeginnerProblems() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty E = Difficulty.EASY;

        add(l, "Reverse a String", "Strings", E, "strings,two-pointers,warm-up",
                "Given a string s, return it reversed. Example: \"hello\" -> \"olleh\".",
                "Signal: you need to read the data from both ends. Two indices moving toward each other is the reflex.",
                """
                static String reverse(String s) {
                    String result = "";
                    for (int i = s.length() - 1; i >= 0; i--) {
                        result += s.charAt(i);
                    }
                    return result;
                }""",
                """
                static String reverse(String s) {
                    char[] chars = s.toCharArray();
                    int left = 0, right = chars.length - 1;
                    while (left < right) {
                        char tmp = chars[left];
                        chars[left++] = chars[right];
                        chars[right--] = tmp;
                    }
                    return new String(chars);
                }""",
                "The beginner version appends to a String in a loop; Strings are immutable so every += copies the whole string (O(n^2)). The two-pointer version swaps characters in a char array in place, touching each character once.",
                "O(n)", "O(n)", "Using += on a String in a loop; off-by-one when the pointers meet (use left < right, not <=, it does not matter for the middle char).");

        add(l, "Palindrome Check", "Strings", E, "strings,two-pointers",
                "Return true if a string reads the same forwards and backwards, ignoring case and any non-alphanumeric characters. Example: \"A man, a plan, a canal: Panama\" -> true.",
                "Signal: symmetry. Compare the outside characters and walk inward.",
                """
                static boolean isPalindrome(String s) {
                    StringBuilder clean = new StringBuilder();
                    for (char c : s.toCharArray()) {
                        if (Character.isLetterOrDigit(c)) clean.append(Character.toLowerCase(c));
                    }
                    return clean.toString().equals(clean.reverse().toString());
                }""",
                """
                static boolean isPalindrome(String s) {
                    int i = 0, j = s.length() - 1;
                    while (i < j) {
                        while (i < j && !Character.isLetterOrDigit(s.charAt(i))) i++;
                        while (i < j && !Character.isLetterOrDigit(s.charAt(j))) j--;
                        if (Character.toLowerCase(s.charAt(i)) != Character.toLowerCase(s.charAt(j))) return false;
                        i++;
                        j--;
                    }
                    return true;
                }""",
                "Beginner: build a cleaned copy, compare with its reverse. Intermediate: skip junk characters on the fly with two pointers so no extra string is built.",
                "O(n)", "O(1) for two pointers (O(n) for the cleaned copy)", "Forgetting to lower-case; comparing with == instead of equals on strings; not bounding the inner skip loops with i < j.");

        add(l, "FizzBuzz", "Math", E, "math,loops,warm-up",
                "For numbers 1..n return a list: \"FizzBuzz\" if divisible by 3 and 5, \"Fizz\" if by 3, \"Buzz\" if by 5, otherwise the number as text.",
                "Signal: rules that depend on divisibility -> the modulo operator (%). Check the most specific rule first.",
                """
                static List<String> fizzBuzz(int n) {
                    List<String> out = new ArrayList<>();
                    for (int i = 1; i <= n; i++) {
                        if (i % 15 == 0) out.add("FizzBuzz");
                        else if (i % 3 == 0) out.add("Fizz");
                        else if (i % 5 == 0) out.add("Buzz");
                        else out.add(String.valueOf(i));
                    }
                    return out;
                }""",
                """
                static List<String> fizzBuzz(int n) {
                    List<String> out = new ArrayList<>();
                    for (int i = 1; i <= n; i++) {
                        String s = "";
                        if (i % 3 == 0) s += "Fizz";
                        if (i % 5 == 0) s += "Buzz";
                        out.add(s.isEmpty() ? String.valueOf(i) : s);
                    }
                    return out;
                }""",
                "The second version composes the answer from independent rules, so adding a 'Bazz' rule for 7 is one extra line instead of re-ordering a chain of if/else.",
                "O(n)", "O(n)", "Testing 3 and 5 before 15 so FizzBuzz is never reached.");

        add(l, "Count Vowels in a Username", "Strings", E, "strings,counting",
                "Given a username, return how many vowels (a, e, i, o, u, case-insensitive) it contains.",
                "Signal: counting characters that belong to a small fixed set -> a Set or a switch.",
                """
                static int countVowels(String s) {
                    int count = 0;
                    for (char c : s.toLowerCase().toCharArray()) {
                        if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') count++;
                    }
                    return count;
                }""",
                """
                static int countVowels(String s) {
                    Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u');
                    int count = 0;
                    for (int i = 0; i < s.length(); i++) {
                        if (vowels.contains(Character.toLowerCase(s.charAt(i)))) count++;
                    }
                    return count;
                }""",
                "Both are linear. The Set version is easier to extend (e.g. add accented vowels) and avoids a long chain of ||.",
                "O(n)", "O(1)", "Forgetting uppercase letters.");

        add(l, "Find Maximum in Array", "Arrays", E, "arrays,loops",
                "Return the largest value in a non-empty int array.",
                "Signal: 'best so far' -> keep one running variable while scanning once.",
                """
                static int max(int[] a) {
                    int best = a[0];
                    for (int i = 1; i < a.length; i++) {
                        if (a[i] > best) best = a[i];
                    }
                    return best;
                }""",
                """
                static int max(int[] a) {
                    return Arrays.stream(a).max().getAsInt();
                }""",
                "Initialise from the first element, not 0 - otherwise an all-negative array returns the wrong answer. The stream version states intent in one line.",
                "O(n)", "O(1)", "Initialising best = 0 (breaks for negative numbers); starting the loop at 0 and comparing an element with itself is harmless but wasteful.");

        add(l, "Sum of Digits", "Math", E, "math,loops",
                "Return the sum of the digits of a non-negative integer. 4821 -> 15.",
                "Signal: peel digits using n % 10 (last digit) and n / 10 (drop last digit).",
                """
                static int digitSum(int n) {
                    String s = String.valueOf(n);
                    int sum = 0;
                    for (char c : s.toCharArray()) sum += c - '0';
                    return sum;
                }""",
                """
                static int digitSum(int n) {
                    int sum = 0;
                    while (n > 0) {
                        sum += n % 10;
                        n /= 10;
                    }
                    return sum;
                }""",
                "The arithmetic version needs no String allocation. 'c - 48' trick in the first version converts a digit char to its numeric value.",
                "O(log10 n)", "O(1)", "Using n / 10 before n % 10 and losing the digit.");

        add(l, "Factorial", "Recursion", E, "recursion,math",
                "Return n! (n * (n-1) * ... * 1) for 0 <= n <= 20. 0! = 1.",
                "Signal: a problem defined in terms of a smaller version of itself -> recursion with a base case.",
                """
                static long factorial(int n) {
                    long result = 1;
                    for (int i = 2; i <= n; i++) result *= i;
                    return result;
                }""",
                """
                static long factorial(int n) {
                    if (n <= 1) return 1;        // base case stops the recursion
                    return n * factorial(n - 1); // smaller sub-problem
                }""",
                "Iteration is simplest and safest. Recursion mirrors the maths definition and teaches base case + recursive step. Use long: 21! overflows even a long.",
                "O(n)", "O(1) iterative / O(n) call stack recursive", "Missing base case (StackOverflowError); using int and overflowing at 13!.");

        add(l, "Fibonacci Number", "Dynamic Programming", E, "dp,recursion,memoization",
                "Return the nth Fibonacci number where F(0)=0, F(1)=1, F(n)=F(n-1)+F(n-2).",
                "Signal: the same sub-problem is computed many times -> remember results (memoization / DP).",
                """
                static int fib(int n) {
                    if (n < 2) return n;
                    return fib(n - 1) + fib(n - 2);   // exponential: recomputes everything
                }""",
                """
                static int fib(int n) {
                    if (n < 2) return n;
                    int prev = 0, cur = 1;
                    for (int i = 2; i <= n; i++) {
                        int next = prev + cur;
                        prev = cur;
                        cur = next;
                    }
                    return cur;
                }""",
                "Naive recursion is O(2^n) because fib(n-2) is recomputed inside fib(n-1). Keeping only the last two values turns it into O(n) time and O(1) space - the first taste of dynamic programming.",
                "O(n)", "O(1)", "Shipping the naive recursion to production; off-by-one in loop bound.");

        add(l, "Prime Number Check", "Math", E, "math,optimization",
                "Return true if n (n >= 2) is prime.",
                "Signal: divisors come in pairs, so you only need to test up to sqrt(n).",
                """
                static boolean isPrime(int n) {
                    for (int i = 2; i < n; i++) {
                        if (n % i == 0) return false;
                    }
                    return n >= 2;
                }""",
                """
                static boolean isPrime(int n) {
                    if (n < 2) return false;
                    if (n % 2 == 0) return n == 2;
                    for (int i = 3; (long) i * i <= n; i += 2) {
                        if (n % i == 0) return false;
                    }
                    return true;
                }""",
                "If n = a*b then one of a,b is <= sqrt(n), so testing to sqrt(n) is enough. Skipping even numbers halves the work again.",
                "O(sqrt n)", "O(1)", "Using i * i with int and overflowing for large n; treating 1 as prime.");

        add(l, "Remove Duplicates from Sorted Array", "Two Pointers", E, "arrays,two-pointers,in-place",
                "Given a sorted int array, remove duplicates in place and return the count of unique elements. The first k slots must hold the unique values in order.",
                "Signal: 'in place' + 'sorted' -> a slow pointer writes, a fast pointer reads.",
                """
                static int removeDuplicates(int[] a) {
                    Set<Integer> seen = new LinkedHashSet<>();
                    for (int x : a) seen.add(x);
                    int i = 0;
                    for (int x : seen) a[i++] = x;
                    return seen.size();
                }""",
                """
                static int removeDuplicates(int[] a) {
                    if (a.length == 0) return 0;
                    int write = 1;
                    for (int read = 1; read < a.length; read++) {
                        if (a[read] != a[write - 1]) a[write++] = a[read];
                    }
                    return write;
                }""",
                "Because the array is sorted, duplicates sit next to each other: compare with the last element written. No extra memory is needed.",
                "O(n)", "O(1)", "Comparing a[read] with a[read-1] after you have already overwritten it; forgetting the empty array.");

        add(l, "Contains Duplicate", "Hash Set", E, "arrays,hash-set",
                "Return true if any value appears at least twice in the array.",
                "Signal: 'have I seen this before?' -> a HashSet gives O(1) answers.",
                """
                static boolean containsDuplicate(int[] a) {
                    for (int i = 0; i < a.length; i++)
                        for (int j = i + 1; j < a.length; j++)
                            if (a[i] == a[j]) return true;
                    return false;
                }""",
                """
                static boolean containsDuplicate(int[] a) {
                    Set<Integer> seen = new HashSet<>();
                    for (int x : a) {
                        if (!seen.add(x)) return true;   // add() returns false if already present
                    }
                    return false;
                }""",
                "Nested loops compare every pair (O(n^2)). A HashSet trades O(n) memory for O(1) lookups.",
                "O(n)", "O(n)", "Sorting is also valid (O(n log n), O(1) space) - mention the trade-off.");

        add(l, "Valid Anagram", "Hash Map", E, "strings,counting,hash-map",
                "Return true if t is an anagram of s (same letters, same counts). \"listen\" / \"silent\" -> true.",
                "Signal: 'same characters, different order' -> compare frequency counts or sorted forms.",
                """
                static boolean isAnagram(String s, String t) {
                    char[] a = s.toCharArray(), b = t.toCharArray();
                    Arrays.sort(a);
                    Arrays.sort(b);
                    return Arrays.equals(a, b);
                }""",
                """
                static boolean isAnagram(String s, String t) {
                    if (s.length() != t.length()) return false;
                    int[] count = new int[26];
                    for (int i = 0; i < s.length(); i++) {
                        count[s.charAt(i) - 'a']++;
                        count[t.charAt(i) - 'a']--;
                    }
                    for (int c : count) if (c != 0) return false;
                    return true;
                }""",
                "Sorting is O(n log n). A 26-slot counter array is O(n) and uses constant memory for lowercase letters.",
                "O(n)", "O(1)", "Not checking lengths first; assuming only lowercase letters when input may be Unicode (use a HashMap).");

        add(l, "First Unique Character", "Hash Map", E, "strings,hash-map,counting",
                "Return the index of the first character in the string that appears exactly once, or -1.",
                "Signal: 'first' + 'appears once' -> count first, then scan in original order.",
                """
                static int firstUniqChar(String s) {
                    for (int i = 0; i < s.length(); i++) {
                        if (s.indexOf(s.charAt(i)) == s.lastIndexOf(s.charAt(i))) return i;
                    }
                    return -1;
                }""",
                """
                static int firstUniqChar(String s) {
                    int[] freq = new int[26];
                    for (char c : s.toCharArray()) freq[c - 'a']++;
                    for (int i = 0; i < s.length(); i++) {
                        if (freq[s.charAt(i) - 'a'] == 1) return i;
                    }
                    return -1;
                }""",
                "Two passes: the first counts everything, the second finds the earliest character whose count is 1. The indexOf/lastIndexOf version is O(n^2).",
                "O(n)", "O(1)", "Returning the character instead of the index; scanning the frequency array instead of the string (loses order).");

        add(l, "Merge Two Sorted Arrays", "Two Pointers", E, "arrays,two-pointers,merge",
                "Merge two sorted int arrays into one sorted array.",
                "Signal: two sorted inputs -> one pointer per input, always take the smaller head (the heart of merge sort).",
                """
                static int[] merge(int[] a, int[] b) {
                    int[] all = new int[a.length + b.length];
                    System.arraycopy(a, 0, all, 0, a.length);
                    System.arraycopy(b, 0, all, a.length, b.length);
                    Arrays.sort(all);
                    return all;
                }""",
                """
                static int[] merge(int[] a, int[] b) {
                    int[] out = new int[a.length + b.length];
                    int i = 0, j = 0, k = 0;
                    while (i < a.length && j < b.length) out[k++] = a[i] <= b[j] ? a[i++] : b[j++];
                    while (i < a.length) out[k++] = a[i++];
                    while (j < b.length) out[k++] = b[j++];
                    return out;
                }""",
                "Concatenate + sort ignores the fact the inputs are already sorted (O(n log n)). Two pointers is O(n).",
                "O(n + m)", "O(n + m)", "Forgetting to copy the leftovers of the longer array.");

        add(l, "Move Zeroes", "Two Pointers", E, "arrays,two-pointers,in-place",
                "Move all 0s to the end of the array in place while keeping the order of non-zero elements.",
                "Signal: stable partition in place -> write pointer for 'good' elements.",
                """
                static void moveZeroes(int[] a) {
                    int[] copy = new int[a.length];
                    int k = 0;
                    for (int x : a) if (x != 0) copy[k++] = x;
                    System.arraycopy(copy, 0, a, 0, a.length);
                }""",
                """
                static void moveZeroes(int[] a) {
                    int write = 0;
                    for (int read = 0; read < a.length; read++) {
                        if (a[read] != 0) {
                            int tmp = a[write];
                            a[write++] = a[read];
                            a[read] = tmp;
                        }
                    }
                }""",
                "The swap version never allocates: everything left of 'write' is already non-zero and in order.",
                "O(n)", "O(1)", "Overwriting instead of swapping, then forgetting to fill zeros afterwards.");

        add(l, "Best Time to Buy and Sell Stock", "Greedy", E, "arrays,greedy,one-pass",
                "prices[i] is a stock price on day i. Buy once, sell later once. Return the max profit (0 if none).",
                "Signal: 'best difference where the smaller one comes first' -> track the minimum so far.",
                """
                static int maxProfit(int[] p) {
                    int best = 0;
                    for (int i = 0; i < p.length; i++)
                        for (int j = i + 1; j < p.length; j++)
                            best = Math.max(best, p[j] - p[i]);
                    return best;
                }""",
                """
                static int maxProfit(int[] p) {
                    int minPrice = Integer.MAX_VALUE, best = 0;
                    for (int price : p) {
                        minPrice = Math.min(minPrice, price);
                        best = Math.max(best, price - minPrice);
                    }
                    return best;
                }""",
                "At each day the best profit if you sell today is price - cheapest earlier price. One pass remembers the cheapest price seen.",
                "O(n)", "O(1)", "Selling before buying (using a global min that appears later).");

        add(l, "Climbing Stairs", "Dynamic Programming", E, "dp,fibonacci",
                "You can climb 1 or 2 steps at a time. How many distinct ways are there to reach step n?",
                "Signal: ways(n) = ways(n-1) + ways(n-2) - a Fibonacci in disguise.",
                """
                static int climb(int n) {
                    if (n <= 2) return n;
                    return climb(n - 1) + climb(n - 2);
                }""",
                """
                static int climb(int n) {
                    if (n <= 2) return n;
                    int a = 1, b = 2;
                    for (int i = 3; i <= n; i++) {
                        int c = a + b;
                        a = b;
                        b = c;
                    }
                    return b;
                }""",
                "To land on step n you came from n-1 (took 1 step) or n-2 (took 2 steps). Keep only the last two counts.",
                "O(n)", "O(1)", "Recursion without memoization (exponential).");

        add(l, "Roman to Integer", "Strings", E, "strings,hash-map,parsing",
                "Convert a Roman numeral (I, V, X, L, C, D, M) to an integer. \"MCMXCIV\" -> 1994.",
                "Signal: if a smaller symbol comes before a larger one it is subtracted (IV = 4).",
                """
                static int romanToInt(String s) {
                    String r = s.replace("IV", "IIII").replace("IX", "VIIII").replace("XL", "XXXX")
                            .replace("XC", "LXXXX").replace("CD", "CCCC").replace("CM", "DCCCC");
                    int total = 0;
                    for (char c : r.toCharArray()) total += value(c);
                    return total;
                }""",
                """
                static int romanToInt(String s) {
                    int total = 0;
                    for (int i = 0; i < s.length(); i++) {
                        int cur = value(s.charAt(i));
                        int next = i + 1 < s.length() ? value(s.charAt(i + 1)) : 0;
                        total += cur < next ? -cur : cur;
                    }
                    return total;
                }
                static int value(char c) {
                    return switch (c) {
                        case 'I' -> 1; case 'V' -> 5; case 'X' -> 10; case 'L' -> 50;
                        case 'C' -> 100; case 'D' -> 500; default -> 1000;
                    };
                }""",
                "Left-to-right: if the current symbol is smaller than the next, subtract it, otherwise add it.",
                "O(n)", "O(1)", "Treating every symbol as additive; reading past the end of the string.");

        add(l, "Longest Common Prefix", "Strings", E, "strings,prefix",
                "Return the longest common prefix of an array of strings, or \"\" if there is none. [\"flower\",\"flow\",\"flight\"] -> \"fl\".",
                "Signal: the prefix can only shrink - start with the first word and trim it.",
                """
                static String lcp(String[] words) {
                    if (words.length == 0) return "";
                    StringBuilder prefix = new StringBuilder();
                    for (int i = 0; i < words[0].length(); i++) {
                        char c = words[0].charAt(i);
                        for (String w : words) {
                            if (i >= w.length() || w.charAt(i) != c) return prefix.toString();
                        }
                        prefix.append(c);
                    }
                    return prefix.toString();
                }""",
                """
                static String lcp(String[] words) {
                    if (words.length == 0) return "";
                    String prefix = words[0];
                    for (String w : words) {
                        while (!w.startsWith(prefix)) prefix = prefix.substring(0, prefix.length() - 1);
                    }
                    return prefix;
                }""",
                "Column scan stops at the first mismatch. Prefix trimming reuses startsWith and stops when the prefix is empty (\"\".startsWith is always true).",
                "O(total characters)", "O(1)", "Index out of bounds on short words.");

        add(l, "Binary Search (Find Index)", "Binary Search", E, "binary-search,sorted",
                "Given a sorted array and a target, return the index of the target or -1.",
                "Signal: sorted data + 'find' -> halve the search range every step.",
                """
                static int search(int[] a, int target) {
                    for (int i = 0; i < a.length; i++) if (a[i] == target) return i;
                    return -1;
                }""",
                """
                static int search(int[] a, int target) {
                    int lo = 0, hi = a.length - 1;
                    while (lo <= hi) {
                        int mid = lo + (hi - lo) / 2;      // avoids int overflow
                        if (a[mid] == target) return mid;
                        if (a[mid] < target) lo = mid + 1; else hi = mid - 1;
                    }
                    return -1;
                }""",
                "Each comparison discards half of the remaining range: 1,000,000 items need ~20 steps instead of 1,000,000.",
                "O(log n)", "O(1)", "(lo + hi) / 2 overflowing; using lo < hi and missing the last element; infinite loop from lo = mid.");

        add(l, "Power of Two", "Bit Manipulation", E, "bits,math",
                "Return true if n is a power of two (1, 2, 4, 8, ...).",
                "Signal: powers of two have exactly one set bit; n & (n - 1) clears the lowest set bit.",
                """
                static boolean isPowerOfTwo(int n) {
                    if (n < 1) return false;
                    while (n % 2 == 0) n /= 2;
                    return n == 1;
                }""",
                """
                static boolean isPowerOfTwo(int n) {
                    return n > 0 && (n & (n - 1)) == 0;
                }""",
                "8 = 1000b, 7 = 0111b, 8 & 7 = 0. For any other positive number the AND leaves some bits behind.",
                "O(1)", "O(1)", "Forgetting n > 0 (0 & -1 is 0).");

        add(l, "Single Number", "Bit Manipulation", E, "bits,xor",
                "Every element appears twice except one. Find the one that appears once.",
                "Signal: pairs cancel out -> XOR (a ^ a = 0, a ^ 0 = a).",
                """
                static int singleNumber(int[] a) {
                    Map<Integer, Integer> count = new HashMap<>();
                    for (int x : a) count.merge(x, 1, Integer::sum);
                    for (var e : count.entrySet()) if (e.getValue() == 1) return e.getKey();
                    return -1;
                }""",
                """
                static int singleNumber(int[] a) {
                    int result = 0;
                    for (int x : a) result ^= x;
                    return result;
                }""",
                "XOR is commutative, so all pairs annihilate and only the unpaired value remains - no map needed.",
                "O(n)", "O(1)", "Using sum tricks that overflow.");

        add(l, "Reverse Linked List", "Linked List", E, "linked-list,pointers",
                "Reverse a singly linked list and return the new head.",
                "Signal: re-wire next pointers one node at a time; keep prev, cur, next.",
                """
                static ListNode reverse(ListNode head) {
                    List<ListNode> nodes = new ArrayList<>();
                    for (ListNode n = head; n != null; n = n.next) nodes.add(n);
                    for (int i = nodes.size() - 1; i > 0; i--) nodes.get(i).next = nodes.get(i - 1);
                    if (!nodes.isEmpty()) nodes.get(0).next = null;
                    return nodes.isEmpty() ? null : nodes.get(nodes.size() - 1);
                }""",
                """
                static ListNode reverse(ListNode head) {
                    ListNode prev = null, cur = head;
                    while (cur != null) {
                        ListNode next = cur.next;  // save
                        cur.next = prev;           // flip
                        prev = cur;                // advance
                        cur = next;
                    }
                    return prev;
                }""",
                "Save next BEFORE overwriting cur.next, otherwise you lose the rest of the list.",
                "O(n)", "O(1)", "Losing the tail by not saving next; forgetting the original head must end with next = null.");

        add(l, "Maximum Depth of Binary Tree", "Trees", E, "tree,recursion,dfs",
                "Return the number of nodes along the longest path from the root to a leaf.",
                "Signal: a tree property = combine the same property of the children.",
                """
                static int maxDepth(TreeNode root) {
                    if (root == null) return 0;
                    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
                }""",
                """
                static int maxDepth(TreeNode root) {
                    if (root == null) return 0;
                    int depth = 0;
                    Queue<TreeNode> q = new ArrayDeque<>();
                    q.add(root);
                    while (!q.isEmpty()) {
                        depth++;
                        for (int i = q.size(); i > 0; i--) {
                            TreeNode n = q.poll();
                            if (n.left != null) q.add(n.left);
                            if (n.right != null) q.add(n.right);
                        }
                    }
                    return depth;
                }""",
                "Recursion is 3 lines. The BFS version counts levels and avoids deep recursion on skewed trees.",
                "O(n)", "O(h) recursive / O(width) BFS", "Returning 1 for null (off by one).");

        add(l, "Shopping Cart Total", "Website: E-commerce", E, "website,e-commerce,maps,money",
                "A cart is a list of (sku, unitPriceInCents, quantity). Return the total in cents. Reject negative quantities.",
                "Signal: money is never a double - use integer cents (or BigDecimal).",
                """
                record Item(String sku, long unitPriceCents, int qty) {}

                static long total(List<Item> cart) {
                    long sum = 0;
                    for (Item i : cart) {
                        if (i.qty() < 0) throw new IllegalArgumentException("negative qty: " + i.sku());
                        sum += i.unitPriceCents() * i.qty();
                    }
                    return sum;
                }""",
                """
                static long total(List<Item> cart) {
                    return cart.stream()
                            .peek(i -> { if (i.qty() < 0) throw new IllegalArgumentException(i.sku()); })
                            .mapToLong(i -> i.unitPriceCents() * i.qty())
                            .sum();
                }""",
                "Floating point cannot represent 0.10 exactly, so 3 x 0.10 != 0.30. Real shops store integer minor units (cents, paise).",
                "O(n)", "O(1)", "Using double for prices; int overflow on large carts (use long).");

        add(l, "Email Format Validator", "Website: Sign-up Form", E, "website,strings,validation",
                "Return true if a string looks like an email: exactly one '@', non-empty local part, and a domain containing a '.' that is not first/last.",
                "Signal: validating user input from a form - be strict about structure, lenient about what you cannot check (real validation = send a confirmation email).",
                """
                static boolean isValidEmail(String e) {
                    int at = e.indexOf('@');
                    if (at <= 0 || at != e.lastIndexOf('@')) return false;
                    String domain = e.substring(at + 1);
                    int dot = domain.lastIndexOf('.');
                    return dot > 0 && dot < domain.length() - 1;
                }""",
                """
                private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*\\\\.[A-Za-z]{2,}$");

                static boolean isValidEmail(String e) {
                    return e != null && EMAIL.matcher(e).matches();
                }""",
                "Manual checks teach the rules; a precompiled regex expresses them compactly. Never recompile the Pattern per call.",
                "O(n)", "O(1)", "Believing a regex can prove an address exists; compiling the Pattern inside the method on every request.");

        add(l, "Word Frequency Counter (Blog Analytics)", "Website: Blog", E, "website,hash-map,strings",
                "Given blog post text, return a map word -> count, case-insensitive, ignoring punctuation.",
                "Signal: counting occurrences -> HashMap<String,Integer> with merge().",
                """
                static Map<String, Integer> wordCount(String text) {
                    Map<String, Integer> counts = new HashMap<>();
                    for (String w : text.toLowerCase().split("[^a-z0-9']+")) {
                        if (w.isEmpty()) continue;
                        if (counts.containsKey(w)) counts.put(w, counts.get(w) + 1);
                        else counts.put(w, 1);
                    }
                    return counts;
                }""",
                """
                static Map<String, Integer> wordCount(String text) {
                    Map<String, Integer> counts = new TreeMap<>();   // sorted output
                    for (String w : text.toLowerCase().split("[^a-z0-9']+")) {
                        if (!w.isEmpty()) counts.merge(w, 1, Integer::sum);
                    }
                    return counts;
                }""",
                "merge(key, 1, Integer::sum) replaces the contains/get/put dance. Swap in a TreeMap if you want alphabetical output.",
                "O(n)", "O(unique words)", "Splitting on spaces only, so 'Hello,' and 'hello' count as different words.");

        add(l, "Password Strength Checker", "Website: Sign-up Form", E, "website,strings,validation",
                "Score a password 0-4: +1 for length >= 8, +1 for a digit, +1 for an uppercase letter, +1 for a symbol.",
                "Signal: independent rules, each worth a point - evaluate each separately.",
                """
                static int strength(String pw) {
                    int score = 0;
                    boolean digit = false, upper = false, symbol = false;
                    for (char c : pw.toCharArray()) {
                        if (Character.isDigit(c)) digit = true;
                        else if (Character.isUpperCase(c)) upper = true;
                        else if (!Character.isLetter(c)) symbol = true;
                    }
                    if (pw.length() >= 8) score++;
                    if (digit) score++;
                    if (upper) score++;
                    if (symbol) score++;
                    return score;
                }""",
                """
                static int strength(String pw) {
                    List<Predicate<String>> rules = List.of(
                            p -> p.length() >= 8,
                            p -> p.chars().anyMatch(Character::isDigit),
                            p -> p.chars().anyMatch(Character::isUpperCase),
                            p -> p.chars().anyMatch(c -> !Character.isLetterOrDigit(c)));
                    return (int) rules.stream().filter(r -> r.test(pw)).count();
                }""",
                "Modelling rules as a list of predicates lets product teams add rules without touching the scoring loop.",
                "O(n)", "O(1)", "Logging or storing the plaintext password - never do that; hash with bcrypt/argon2.");

        add(l, "Pagination Page Calculator", "Website: Listing Page", E, "website,math,pagination",
                "Given totalItems, pageSize and a 1-based page number, return the [startIndex, endIndex) slice and the total page count. Clamp invalid pages.",
                "Signal: ceiling division: pages = (total + size - 1) / size.",
                """
                static int[] slice(int total, int pageSize, int page) {
                    int pages = (int) Math.ceil((double) total / pageSize);
                    if (page < 1) page = 1;
                    if (page > pages) page = pages;
                    int start = (page - 1) * pageSize;
                    int end = Math.min(start + pageSize, total);
                    return new int[] { start, end, pages };
                }""",
                """
                static int[] slice(int total, int pageSize, int page) {
                    int pages = Math.max(1, (total + pageSize - 1) / pageSize);   // integer ceiling
                    page = Math.min(Math.max(page, 1), pages);
                    int start = (page - 1) * pageSize;
                    return new int[] { start, Math.min(start + pageSize, total), pages };
                }""",
                "Integer ceiling division avoids floating point. Clamp user-supplied page numbers: ?page=-5 and ?page=99999 must not crash.",
                "O(1)", "O(1)", "Zero-based vs one-based page confusion; total = 0 giving 0 pages and a negative start.");

        add(l, "Days in Month (Calendar Widget)", "Website: Calendar", E, "website,math,dates",
                "Return the number of days in a given month (1-12) of a given year. Handle leap years.",
                "Signal: leap year = divisible by 4, except centuries unless divisible by 400.",
                """
                static int daysInMonth(int year, int month) {
                    boolean leap = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
                    return switch (month) {
                        case 2 -> leap ? 29 : 28;
                        case 4, 6, 9, 11 -> 30;
                        default -> 31;
                    };
                }""",
                """
                static int daysInMonth(int year, int month) {
                    return YearMonth.of(year, month).lengthOfMonth();   // java.time handles leap rules
                }""",
                "In real projects prefer java.time; writing it by hand once teaches why 1900 was not a leap year but 2000 was.",
                "O(1)", "O(1)", "Writing year % 4 == 0 only.");

        add(l, "Page Views per User", "Website: Analytics", E, "website,hash-map,analytics",
                "Given a list of (userId) page-view events return the user with the most views (ties: smallest id).",
                "Signal: group-and-count then take the max -> map + one scan.",
                """
                static String topUser(List<String> events) {
                    Map<String, Integer> views = new HashMap<>();
                    for (String u : events) views.put(u, views.getOrDefault(u, 0) + 1);
                    String best = null;
                    for (var e : views.entrySet()) {
                        if (best == null || e.getValue() > views.get(best)
                                || (e.getValue().equals(views.get(best)) && e.getKey().compareTo(best) < 0)) {
                            best = e.getKey();
                        }
                    }
                    return best;
                }""",
                """
                static String topUser(List<String> events) {
                    return events.stream()
                            .collect(Collectors.groupingBy(u -> u, Collectors.counting()))
                            .entrySet().stream()
                            .max(Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue)
                                    .thenComparing(Map.Entry::getKey, Comparator.reverseOrder()))
                            .map(Map.Entry::getKey).orElse(null);
                }""",
                "Counting is the same idea the real analytics pipelines use (GROUP BY user COUNT(*)). Tie-breaking must be explicit to be deterministic.",
                "O(n)", "O(users)", "Relying on HashMap iteration order for ties.");

        add(l, "Star Rating Average", "Website: Reviews", E, "website,math,rounding",
                "Given an array of 1-5 star ratings, return the average rounded to one decimal place, or 0.0 when empty.",
                "Signal: guard the empty case and avoid integer division.",
                """
                static double average(int[] ratings) {
                    if (ratings.length == 0) return 0.0;
                    int sum = 0;
                    for (int r : ratings) sum += r;
                    return Math.round((double) sum / ratings.length * 10) / 10.0;
                }""",
                """
                static double average(int[] ratings) {
                    return Math.round(Arrays.stream(ratings).average().orElse(0.0) * 10) / 10.0;
                }""",
                "sum / length with two ints truncates (7/2 = 3). Cast one side to double first.",
                "O(n)", "O(1)", "Integer division; dividing by zero when there are no reviews yet.");

        add(l, "Case-Insensitive Duplicate Usernames", "Website: Sign-up Form", E, "website,hash-set,strings",
                "Given a list of requested usernames, return those that clash with an earlier one ignoring case (Rahul vs rahul).",
                "Signal: normalise the key (toLowerCase) before putting it in a set.",
                """
                static List<String> duplicates(List<String> names) {
                    List<String> dup = new ArrayList<>();
                    for (int i = 0; i < names.size(); i++)
                        for (int j = 0; j < i; j++)
                            if (names.get(i).equalsIgnoreCase(names.get(j))) { dup.add(names.get(i)); break; }
                    return dup;
                }""",
                """
                static List<String> duplicates(List<String> names) {
                    Set<String> seen = new HashSet<>();
                    List<String> dup = new ArrayList<>();
                    for (String n : names) {
                        if (!seen.add(n.toLowerCase(Locale.ROOT))) dup.add(n);
                    }
                    return dup;
                }""",
                "Put a UNIQUE index on lower(username) in the database too - application checks alone race under concurrent sign-ups.",
                "O(n)", "O(n)", "toLowerCase() without a Locale (Turkish dotless i); trusting only an application-level check.");

        add(l, "URL Slug Generator", "Website: Blog", E, "website,strings,seo",
                "Turn a post title into a URL slug: lowercase, spaces and punctuation become single hyphens, no leading/trailing hyphen. \"Hello, World! 2024\" -> \"hello-world-2024\".",
                "Signal: replace runs of non-alphanumerics with one separator, then trim.",
                """
                static String slugify(String title) {
                    StringBuilder sb = new StringBuilder();
                    boolean lastDash = true;
                    for (char c : title.toLowerCase().toCharArray()) {
                        if (Character.isLetterOrDigit(c)) { sb.append(c); lastDash = false; }
                        else if (!lastDash) { sb.append('-'); lastDash = true; }
                    }
                    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '-') sb.setLength(sb.length() - 1);
                    return sb.toString();
                }""",
                """
                static String slugify(String title) {
                    return title.toLowerCase(Locale.ROOT)
                            .replaceAll("[^a-z0-9]+", "-")
                            .replaceAll("^-|-$", "");
                }""",
                "A regex does the same in two lines. Real sites also append an id or check uniqueness because two posts can share a title.",
                "O(n)", "O(n)", "Leaving double hyphens; not trimming; non-ASCII titles producing empty slugs.");

        add(l, "Majority Element", "Hash Map", E, "arrays,voting,hash-map",
                "Return the element that appears more than n/2 times (it always exists).",
                "Signal: a strict majority survives pairwise cancellation -> Boyer-Moore voting.",
                """
                static int majority(int[] a) {
                    Map<Integer, Integer> c = new HashMap<>();
                    for (int x : a) {
                        c.merge(x, 1, Integer::sum);
                        if (c.get(x) > a.length / 2) return x;
                    }
                    return -1;
                }""",
                """
                static int majority(int[] a) {
                    int candidate = 0, count = 0;
                    for (int x : a) {
                        if (count == 0) candidate = x;
                        count += (x == candidate) ? 1 : -1;
                    }
                    return candidate;
                }""",
                "Every non-majority element can cancel at most one majority element, yet the majority has more than half the votes, so it survives.",
                "O(n)", "O(1)", "Using Boyer-Moore when a majority is not guaranteed without a verification pass.");

        add(l, "Check Sorted Array", "Arrays", E, "arrays,loops",
                "Return true if an int array is sorted in non-decreasing order.",
                "Signal: a global property = every adjacent pair satisfies a local rule.",
                """
                static boolean isSorted(int[] a) {
                    int[] copy = a.clone();
                    Arrays.sort(copy);
                    return Arrays.equals(a, copy);
                }""",
                """
                static boolean isSorted(int[] a) {
                    for (int i = 1; i < a.length; i++) {
                        if (a[i - 1] > a[i]) return false;
                    }
                    return true;
                }""",
                "Sorting a copy is O(n log n); checking neighbours is O(n) and exits at the first violation.",
                "O(n)", "O(1)", "Using > vs >= (duplicates are allowed in non-decreasing order).");

        return l;
    }
}
