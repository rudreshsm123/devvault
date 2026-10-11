package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.full;

/** Beginner set A: number and array fundamentals, each with a line-by-line explanation. */
public final class BasicsProblemsA {

    private BasicsProblemsA() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty E = Difficulty.EASY;

        full(l, "Check Even or Odd", "Math Basics", E, "math,modulo,beginner",
                "Return true if an integer is even.",
                "Signal: divisibility questions use the remainder operator %.",
                """
                static boolean isEven(int n) {
                    if (n / 2 * 2 == n) return true;
                    return false;
                }""",
                """
                static boolean isEven(int n) {
                    return n % 2 == 0;      // also works: (n & 1) == 0
                }""",
                "Every even number leaves remainder 0 when divided by 2. Bitwise: the last bit of an even number is 0.",
                """
                n % 2 ==> Remainder after dividing by 2. It is 0 for even numbers and 1 (or -1 for negatives) for odd numbers.
                == 0 ==> Compare the remainder to zero; checking == 1 would be wrong for negative odd numbers because -3 % 2 is -1.
                return ... ==> Returning the boolean expression directly avoids a pointless if/else.
                (n & 1) == 0 ==> Bitwise version: AND with 1 keeps only the last bit, which is 0 for every even number.""",
                "A warm-up. Mention negative numbers and that n % 2 == 1 is a bug for -3. The expected extra: the bitwise version.",
                "O(1)", "O(1)", "Testing n % 2 == 1 (fails for negative odd numbers).");

        full(l, "Swap Two Numbers", "Math Basics", E, "math,variables,beginner",
                "Swap the values of two int variables inside a method that works on an array of two elements.",
                "Signal: you need a temporary place to hold one value while you overwrite it.",
                """
                static void swap(int[] p) {
                    int temp = p[0];
                    p[0] = p[1];
                    p[1] = temp;
                }""",
                """
                static void swap(int[] p) {
                    p[0] = p[0] + p[1];
                    p[1] = p[0] - p[1];
                    p[0] = p[0] - p[1];     // arithmetic trick, can overflow
                    // XOR version: p[0] ^= p[1]; p[1] ^= p[0]; p[0] ^= p[1];
                }""",
                "The temporary variable is the clearest and safest solution. The arithmetic and XOR versions avoid the temp but are tricks, and the sum can overflow.",
                """
                int temp = p[0]; ==> Save the first value before it is overwritten; without this one value would be lost.
                p[0] = p[1]; ==> Copy the second value into the first slot.
                p[1] = temp; ==> Put the saved first value into the second slot. The swap is complete.
                int[] p ==> Java passes primitives by value, so a plain int parameter could not change the caller's variables; an array lets the method modify them.""",
                "Interviewers sometimes ask for the swap WITHOUT a temp variable. Give the temp version first, then the XOR trick, and mention it is rarely a good idea in real code.",
                "O(1)", "O(1)", "Writing p[0] = p[1]; p[1] = p[0]; (both end up equal).");

        full(l, "Largest of Three Numbers", "Math Basics", E, "math,conditions,beginner",
                "Return the largest of three integers.",
                "Signal: comparing a small fixed number of values - use Math.max or nested comparisons.",
                """
                static int largest(int a, int b, int c) {
                    if (a >= b && a >= c) return a;
                    if (b >= a && b >= c) return b;
                    return c;
                }""",
                """
                static int largest(int a, int b, int c) {
                    return Math.max(a, Math.max(b, c));
                }""",
                "The library call states the intent directly and cannot contain a comparison typo.",
                """
                a >= b && a >= c ==> a wins only if it is at least as large as both others; >= handles ties correctly.
                if (b >= a && b >= c) ==> Reached only if a did not win, so b is checked the same way.
                return c; ==> If neither a nor b is the largest, c must be.
                Math.max(a, Math.max(b, c)) ==> Compare b and c first, then compare that winner with a.""",
                "A relaxed question to check you use standard library helpers. A good extension: how would you find the max of n numbers? (loop with a running maximum).",
                "O(1)", "O(1)", "Using > instead of >= and returning the wrong value on ties.");

        full(l, "Count Digits in a Number", "Math Basics", E, "math,loops,digits",
                "Return how many digits a non-negative integer has. 0 has one digit.",
                "Signal: repeatedly dividing by 10 removes one digit each time.",
                """
                static int countDigits(int n) {
                    return String.valueOf(n).length();
                }""",
                """
                static int countDigits(int n) {
                    if (n == 0) return 1;
                    int count = 0;
                    while (n > 0) {
                        n /= 10;
                        count++;
                    }
                    return count;
                }""",
                "Converting to a String is simplest. The loop divides by 10 until nothing is left and counts how many divisions it took.",
                """
                if (n == 0) return 1; ==> Zero is a special case: the loop below would run zero times and wrongly return 0.
                while (n > 0) ==> Keep going while at least one digit remains.
                n /= 10; ==> Integer division removes the last digit (1234 becomes 123).
                count++; ==> One more digit was removed, so one more digit existed.
                return count; ==> After the loop n reached 0, so count holds the total number of digits.""",
                "Ask yourself about 0 and negative numbers before coding. The log10 formula (int) Math.log10(n) + 1 is a nice extra but fails for 0.",
                "O(log10 n)", "O(1)", "Forgetting the n == 0 case; not considering negative input.");

        full(l, "Reverse an Integer", "Math Basics", E, "math,digits,overflow",
                "Reverse the digits of an int: 123 becomes 321, -120 becomes -21. Return 0 if the result overflows.",
                "Signal: pop the last digit with % 10 and push it onto a result with * 10 + digit.",
                """
                static int reverse(int x) {
                    String s = new StringBuilder(String.valueOf(Math.abs((long) x))).reverse().toString();
                    long r = Long.parseLong(s) * (x < 0 ? -1 : 1);
                    return (r > Integer.MAX_VALUE || r < Integer.MIN_VALUE) ? 0 : (int) r;
                }""",
                """
                static int reverse(int x) {
                    int result = 0;
                    while (x != 0) {
                        int digit = x % 10;
                        x /= 10;
                        if (result > Integer.MAX_VALUE / 10 || result < Integer.MIN_VALUE / 10) return 0;
                        result = result * 10 + digit;
                    }
                    return result;
                }""",
                "Each loop pops one digit from x and appends it to result. The overflow check happens BEFORE multiplying by 10, because afterwards the damage is done.",
                """
                int digit = x % 10; ==> The last digit. Works for negatives too (the sign carries through), so no special handling is needed.
                x /= 10; ==> Drops the last digit; the loop ends when nothing is left.
                result > Integer.MAX_VALUE / 10 ==> If result is already above this, result * 10 would overflow. Checking first is the only safe moment.
                result = result * 10 + digit; ==> Shift the existing digits left one place and attach the new digit.
                return 0; ==> The problem statement defines an overflowing answer as 0.""",
                "The trap is overflow. Explain WHY you check before multiplying. Mention that Java ints are 32-bit, and that using long would be a valid alternative.",
                "O(log10 x)", "O(1)", "Checking overflow after the multiplication; mishandling negative numbers.");

        full(l, "Palindrome Number", "Math Basics", E, "math,palindrome,digits",
                "Return true if an integer reads the same forwards and backwards, without converting it to a String. Negative numbers are not palindromes.",
                "Signal: reverse only HALF of the number and compare to the other half.",
                """
                static boolean isPalindrome(int x) {
                    if (x < 0) return false;
                    String s = String.valueOf(x);
                    return new StringBuilder(s).reverse().toString().equals(s);
                }""",
                """
                static boolean isPalindrome(int x) {
                    if (x < 0 || (x % 10 == 0 && x != 0)) return false;
                    int reversedHalf = 0;
                    while (x > reversedHalf) {
                        reversedHalf = reversedHalf * 10 + x % 10;
                        x /= 10;
                    }
                    return x == reversedHalf || x == reversedHalf / 10;
                }""",
                "Reversing the whole number can overflow. Reversing only the second half until it meets the first half avoids that and halves the work.",
                """
                x < 0 ==> A minus sign can never match at the other end, so negatives are never palindromes.
                x % 10 == 0 && x != 0 ==> A number ending in 0 would have to start with 0, which is impossible (except 0 itself).
                while (x > reversedHalf) ==> Stop once the reversed half is as long as the remaining half: we have processed half of the digits.
                reversedHalf = reversedHalf * 10 + x % 10; ==> Move the last digit of x onto the reversed number.
                x == reversedHalf ==> Even number of digits (1221): both halves equal.
                x == reversedHalf / 10 ==> Odd number of digits (12321): drop the middle digit from the reversed half before comparing.""",
                "The follow-up 'without converting to string' is the real question. Show the half-reversal idea and the even/odd length handling.",
                "O(log10 n)", "O(1)", "Forgetting numbers that end in 0; mixing up the odd-length comparison.");

        full(l, "GCD of Two Numbers", "Math Basics", E, "math,euclid,recursion",
                "Return the greatest common divisor of two positive integers.",
                "Signal: gcd(a, b) = gcd(b, a % b) - the Euclidean algorithm.",
                """
                static int gcd(int a, int b) {
                    int best = 1;
                    for (int i = 1; i <= Math.min(a, b); i++) {
                        if (a % i == 0 && b % i == 0) best = i;
                    }
                    return best;
                }""",
                """
                static int gcd(int a, int b) {
                    while (b != 0) {
                        int rest = a % b;
                        a = b;
                        b = rest;
                    }
                    return a;
                }""",
                "The brute force tries every number up to min(a, b). Euclid replaces the pair with (b, a mod b), shrinking the numbers very quickly: O(log min(a, b)).",
                """
                while (b != 0) ==> When the second number reaches 0, the first number is the answer.
                int rest = a % b; ==> The remainder is smaller than b, so the numbers shrink every round. A common divisor of a and b also divides the remainder, so the GCD does not change.
                a = b; b = rest; ==> Slide the pair forward: (a, b) becomes (b, a % b).
                return a; ==> gcd(a, 0) = a.""",
                "Be ready to prove WHY it works in one sentence (a common divisor of a and b divides a % b). Follow-ups: LCM = a / gcd * b, and gcd of an array (fold the function).",
                "O(log min(a, b))", "O(1)", "Forgetting that LCM = a * b / gcd can overflow; divide first.");

        full(l, "LCM of Two Numbers", "Math Basics", E, "math,gcd,overflow",
                "Return the least common multiple of two positive integers.",
                "Signal: lcm(a, b) * gcd(a, b) = a * b. Reuse the GCD.",
                """
                static long lcm(int a, int b) {
                    long m = Math.max(a, b);
                    while (m % a != 0 || m % b != 0) m++;
                    return m;
                }""",
                """
                static long lcm(int a, int b) {
                    return (long) a / gcd(a, b) * b;      // divide first to avoid overflow
                }
                static int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }""",
                "The brute force counts upwards from the larger number and can run for millions of steps. The formula is instant because the GCD is fast.",
                """
                (long) a ==> Widen to long before multiplying so the result cannot overflow an int.
                / gcd(a, b) ==> Dividing first keeps the number small; a / gcd is always a whole number.
                * b ==> Multiplying last gives the smallest number divisible by both.
                b == 0 ? a : gcd(b, a % b) ==> The Euclidean algorithm written recursively in one line.""",
                "Interviewers look for reuse of GCD and the overflow awareness (divide before multiplying).",
                "O(log min(a, b))", "O(log) recursion", "Computing a * b / gcd and overflowing.");

        full(l, "Armstrong Number", "Math Basics", E, "math,digits,loops",
                "A number is an Armstrong number if the sum of its digits each raised to the power of the digit count equals the number. 153 = 1^3 + 5^3 + 3^3.",
                "Signal: you need the digit count first, then the digit-by-digit sum.",
                """
                static boolean isArmstrong(int n) {
                    String s = String.valueOf(n);
                    int power = s.length(), sum = 0;
                    for (char c : s.toCharArray()) {
                        sum += (int) Math.pow(c - '0', power);
                    }
                    return sum == n;
                }""",
                """
                static boolean isArmstrong(int n) {
                    int digits = String.valueOf(n).length();
                    int temp = n, sum = 0;
                    while (temp > 0) {
                        int d = temp % 10;
                        int p = 1;
                        for (int i = 0; i < digits; i++) p *= d;
                        sum += p;
                        temp /= 10;
                    }
                    return sum == n;
                }""",
                "Both versions first learn how many digits exist, then add each digit raised to that power. The second avoids floating point Math.pow.",
                """
                int digits = ... length() ==> The power depends on the number of digits, so it must be known first.
                int temp = n ==> Work on a copy because n is needed at the end for the comparison.
                int d = temp % 10; ==> Take the last digit.
                for (...) p *= d; ==> Integer power by repeated multiplication; avoids precision problems of Math.pow with doubles.
                temp /= 10; ==> Remove the processed digit.
                return sum == n; ==> Compare against the ORIGINAL number.""",
                "A loop-and-digits warm-up. Mention why you avoid Math.pow for exact integer results.",
                "O(d^2)", "O(1)", "Modifying n and then comparing against the changed value.");

        full(l, "Print a Multiplication Table", "Math Basics", E, "loops,strings,beginner",
                "Return the multiplication table of n from 1 to 10 as a list of strings like \"7 x 3 = 21\".",
                "Signal: a fixed-range for loop with a formatted string.",
                """
                static List<String> table(int n) {
                    List<String> rows = new ArrayList<>();
                    for (int i = 1; i <= 10; i++) {
                        rows.add(n + " x " + i + " = " + (n * i));
                    }
                    return rows;
                }""",
                """
                static List<String> table(int n) {
                    return IntStream.rangeClosed(1, 10)
                            .mapToObj(i -> String.format("%d x %d = %d", n, i, n * i))
                            .toList();
                }""",
                "The loop and the stream do the same work. String.format is easier to read once the expression has several parts.",
                """
                for (int i = 1; i <= 10; i++) ==> Starts at 1 and includes 10 because of <=.
                n + " x " + i ==> The + joins numbers and text; the first operand being an int is fine because the other side is a String.
                (n * i) ==> Parentheses are required: without them Java would concatenate the digits instead of multiplying.
                IntStream.rangeClosed(1, 10) ==> The numbers 1 to 10 inclusive as a stream.
                String.format("%d x %d = %d", ...) ==> %d is a placeholder for an integer, filled in order.""",
                "Easy. Show you know the operator precedence trap with string concatenation: \"x\" + 2 * 3 is x6 but \"x\" + 2 + 3 is x23.",
                "O(1)", "O(1)", "Forgetting parentheses around n * i; using < 10 and stopping at 9.");

        full(l, "Second Largest Element", "Arrays", E, "arrays,one-pass,beginner",
                "Return the second largest distinct value in an array, or -1 if there is none.",
                "Signal: track the best and the runner-up in one pass instead of sorting.",
                """
                static int secondLargest(int[] a) {
                    int[] copy = Arrays.stream(a).distinct().sorted().toArray();
                    return copy.length < 2 ? -1 : copy[copy.length - 2];
                }""",
                """
                static int secondLargest(int[] a) {
                    long first = Long.MIN_VALUE, second = Long.MIN_VALUE;
                    for (int x : a) {
                        if (x > first) {
                            second = first;
                            first = x;
                        } else if (x > second && x < first) {
                            second = x;
                        }
                    }
                    return second == Long.MIN_VALUE ? -1 : (int) second;
                }""",
                "Sorting is O(n log n). One pass keeps two variables and is O(n). The condition x < first makes sure duplicates of the maximum are not counted as the second largest.",
                """
                long first = Long.MIN_VALUE, second = Long.MIN_VALUE ==> A long sentinel smaller than any int, so even Integer.MIN_VALUE inputs work.
                if (x > first) ==> A new maximum: the old maximum becomes the runner-up.
                second = first; first = x; ==> The order matters: move first down BEFORE overwriting it.
                else if (x > second && x < first) ==> A value between the two: it becomes the runner-up. x < first excludes duplicates of the maximum.
                second == Long.MIN_VALUE ? -1 ==> If it never changed there was no second distinct value.""",
                "Common warm-up. The follow-up is the kth largest, where you switch to a heap or quickselect. Ask whether duplicates count.",
                "O(n)", "O(1)", "Treating a duplicate maximum as the second largest; initialising with 0.");

        full(l, "Count Frequency of Elements", "Hash Map", E, "arrays,hash-map,counting",
                "Return a map from each value in an int array to the number of times it appears.",
                "Signal: counting occurrences -> HashMap with merge.",
                """
                static Map<Integer, Integer> frequency(int[] a) {
                    Map<Integer, Integer> map = new HashMap<>();
                    for (int x : a) {
                        if (map.containsKey(x)) map.put(x, map.get(x) + 1);
                        else map.put(x, 1);
                    }
                    return map;
                }""",
                """
                static Map<Integer, Integer> frequency(int[] a) {
                    Map<Integer, Integer> map = new TreeMap<>();
                    for (int x : a) map.merge(x, 1, Integer::sum);
                    return map;
                }""",
                "merge removes the contains/get/put pattern. A TreeMap returns the keys in sorted order, useful for reports.",
                """
                Map<Integer, Integer> ==> Key = the value, value = how many times it was seen.
                map.merge(x, 1, Integer::sum) ==> If x is new store 1, otherwise add 1 to the existing count: the whole counting logic in one call.
                new TreeMap<>() ==> Keeps keys sorted, so printing the map gives an ordered frequency table.
                for (int x : a) ==> One pass over the input: O(n).""",
                "Counting is the base of many problems: anagrams, top K frequent, first unique character. Know merge and getOrDefault.",
                "O(n)", "O(unique)", "Using int keys with an int[] when values can be negative or huge.");

        full(l, "Rotate Array Left by One", "Arrays", E, "arrays,in-place",
                "Rotate an array left by one position in place: [1,2,3,4] becomes [2,3,4,1].",
                "Signal: save the first element, shift everything one step left, then put the saved element at the end.",
                """
                static void rotateLeft(int[] a) {
                    int[] copy = a.clone();
                    for (int i = 0; i < a.length; i++) {
                        a[i] = copy[(i + 1) % a.length];
                    }
                }""",
                """
                static void rotateLeft(int[] a) {
                    if (a.length < 2) return;
                    int first = a[0];
                    for (int i = 1; i < a.length; i++) {
                        a[i - 1] = a[i];
                    }
                    a[a.length - 1] = first;
                }""",
                "The in-place version needs just one extra variable instead of a full copy.",
                """
                if (a.length < 2) return; ==> Empty and one-element arrays are already 'rotated'.
                int first = a[0]; ==> The first element would be overwritten by the shift, so save it.
                a[i - 1] = a[i]; ==> Shift each element one slot to the left.
                a[a.length - 1] = first; ==> The saved element goes to the freed last slot.
                (i + 1) % a.length ==> The modulo trick that wraps the last index back to 0 in the copy version.""",
                "Extend to rotating by k: use three reversals or (i + k) % n. State the complexity of both approaches.",
                "O(n)", "O(1)", "Overwriting the first element before saving it.");

        full(l, "Remove Spaces from a String", "Strings", E, "strings,loops",
                "Return the string with all whitespace removed.",
                "Signal: build a new string with only the characters you want to keep.",
                """
                static String removeSpaces(String s) {
                    String result = "";
                    for (char c : s.toCharArray()) {
                        if (c != ' ') result += c;
                    }
                    return result;
                }""",
                """
                static String removeSpaces(String s) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < s.length(); i++) {
                        char c = s.charAt(i);
                        if (!Character.isWhitespace(c)) sb.append(c);
                    }
                    return sb.toString();
                    // or: s.replaceAll("\\\\s+", "")
                }""",
                "String += in a loop creates a new string each time (O(n^2)). StringBuilder appends in amortised O(1). Character.isWhitespace also covers tabs and newlines.",
                """
                new StringBuilder() ==> A mutable character buffer; appending does not copy the whole text each time.
                s.charAt(i) ==> Read one character by index.
                !Character.isWhitespace(c) ==> Keep everything that is not a space, tab or newline.
                sb.append(c) ==> Add the kept character at the end.
                sb.toString() ==> Convert the buffer back to an immutable String.""",
                "Know why StringBuilder exists and that a regex solution is acceptable but slower to write correctly.",
                "O(n)", "O(n)", "Concatenating strings with += inside a loop.");

        full(l, "Count Words in a Sentence", "Strings", E, "strings,split,parsing",
                "Return the number of words in a sentence. Words are separated by one or more spaces, and the text may start or end with spaces.",
                "Signal: trim, then split on a pattern for 'one or more spaces'.",
                """
                static int countWords(String s) {
                    int count = 0;
                    boolean inWord = false;
                    for (char c : s.toCharArray()) {
                        if (c != ' ' && !inWord) { count++; inWord = true; }
                        else if (c == ' ') inWord = false;
                    }
                    return count;
                }""",
                """
                static int countWords(String s) {
                    String t = s.trim();
                    return t.isEmpty() ? 0 : t.split("\\\\s+").length;
                }""",
                "The loop tracks whether we are inside a word and counts each time a new word starts. The split version is shorter but needs the empty-string check, because \"\".split gives one empty element.",
                """
                boolean inWord ==> A flag remembering whether the previous character belonged to a word.
                c != ' ' && !inWord ==> A non-space after a space (or at the start) marks the beginning of a new word, so count it once.
                else if (c == ' ') inWord = false; ==> A space ends the current word.
                s.trim() ==> Removes leading and trailing whitespace so split does not produce an empty first element.
                t.split("\\\\s+") ==> Regex: one or more whitespace characters count as one separator.""",
                "Watch for the empty-string and multiple-space cases; interviewers love them.",
                "O(n)", "O(1) / O(n)", "Using split(\" \") which gives empty strings for double spaces.");

        full(l, "Check Pangram", "Strings", E, "strings,set,alphabet",
                "A pangram contains every letter of the English alphabet at least once. Return true if the sentence is a pangram.",
                "Signal: a Set (or a boolean array of size 26) records which letters have been seen.",
                """
                static boolean isPangram(String s) {
                    String lower = s.toLowerCase();
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (lower.indexOf(c) < 0) return false;
                    }
                    return true;
                }""",
                """
                static boolean isPangram(String s) {
                    boolean[] seen = new boolean[26];
                    int count = 0;
                    for (char c : s.toLowerCase().toCharArray()) {
                        if (c >= 'a' && c <= 'z' && !seen[c - 'a']) {
                            seen[c - 'a'] = true;
                            if (++count == 26) return true;
                        }
                    }
                    return false;
                }""",
                "The first version scans the text up to 26 times. The array version does one pass and stops as soon as all 26 letters are found.",
                """
                boolean[] seen = new boolean[26] ==> One flag per letter; index 0 is 'a', index 25 is 'z'.
                c >= 'a' && c <= 'z' ==> Ignore spaces, digits and punctuation.
                c - 'a' ==> Characters are numbers; subtracting 'a' maps 'a'..'z' to 0..25.
                !seen[c - 'a'] ==> Only count a letter the first time we see it.
                if (++count == 26) return true; ==> Early exit: we do not need to read the rest of the text.""",
                "Typical follow-up: do it for any alphabet, or find the missing letters.",
                "O(n)", "O(1)", "Forgetting toLowerCase, so capital letters are missed.");

        full(l, "Toggle Case of a String", "Strings", E, "strings,characters",
                "Swap uppercase letters to lowercase and lowercase to uppercase: \"Hello World\" becomes \"hELLO wORLD\".",
                "Signal: transform character by character and keep non-letters unchanged.",
                """
                static String toggle(String s) {
                    String out = "";
                    for (char c : s.toCharArray()) {
                        if (Character.isUpperCase(c)) out += Character.toLowerCase(c);
                        else if (Character.isLowerCase(c)) out += Character.toUpperCase(c);
                        else out += c;
                    }
                    return out;
                }""",
                """
                static String toggle(String s) {
                    StringBuilder sb = new StringBuilder(s.length());
                    for (char c : s.toCharArray()) {
                        sb.append(Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c));
                    }
                    return sb.toString();
                }""",
                "toUpperCase on a non-letter returns it unchanged, so the ternary needs only one test. StringBuilder avoids quadratic copying.",
                """
                new StringBuilder(s.length()) ==> Pre-sizing the buffer avoids internal resizing.
                Character.isUpperCase(c) ==> Works for any alphabet, not just A to Z.
                ? toLowerCase(c) : toUpperCase(c) ==> Flip upper to lower; everything else is passed through toUpperCase, which leaves digits and punctuation unchanged.
                sb.append(...) ==> Add the converted character to the result.""",
                "A quick check of the Character helper methods. ASCII tricks (c ^ 32) work but break for non-ASCII text.",
                "O(n)", "O(n)", "Hard-coding ASCII ranges and breaking on non-English letters.");

        full(l, "Check if One String is a Rotation of Another", "Strings", E, "strings,rotation,contains",
                "Return true if string b is a rotation of string a: \"abcde\" and \"cdeab\" are rotations.",
                "Signal: every rotation of a is a substring of a + a.",
                """
                static boolean isRotation(String a, String b) {
                    if (a.length() != b.length()) return false;
                    for (int shift = 0; shift < a.length(); shift++) {
                        String rotated = a.substring(shift) + a.substring(0, shift);
                        if (rotated.equals(b)) return true;
                    }
                    return a.isEmpty();
                }""",
                """
                static boolean isRotation(String a, String b) {
                    return a.length() == b.length() && (a + a).contains(b);
                }""",
                "Doubling the string places every possible rotation inside it as a contiguous substring, so one contains call answers the question.",
                """
                a.length() == b.length() ==> Different lengths can never be rotations; this also prevents false positives like 'ab' in 'abab'.
                (a + a) ==> 'abcdeabcde' contains 'cdeab', 'deabc' and every other rotation.
                .contains(b) ==> A single substring search. Total cost is linear with a good algorithm and quadratic worst case with the naive one.
                substring(shift) + substring(0, shift) ==> The explicit rotation in the first version.""",
                "A neat trick question. Explain WHY a + a works; that is the answer the interviewer is waiting for.",
                "O(n) to O(n^2)", "O(n)", "Skipping the length check.");

        full(l, "Binary to Decimal Conversion", "Math Basics", E, "binary,math,parsing",
                "Convert a binary string such as \"1101\" to its decimal value (13).",
                "Signal: each step to the right multiplies the value by 2 and adds the next bit.",
                """
                static int toDecimal(String bin) {
                    return Integer.parseInt(bin, 2);
                }""",
                """
                static int toDecimal(String bin) {
                    int value = 0;
                    for (char c : bin.toCharArray()) {
                        value = value * 2 + (c - '0');
                    }
                    return value;
                }""",
                "Reading left to right, the number built so far doubles when a new bit arrives, and the bit is added. 1,1->3,0->6,1->13.",
                """
                int value = 0; ==> The value of the bits processed so far.
                value * 2 ==> Appending a bit on the right shifts all earlier bits one place, which doubles the value.
                (c - '0') ==> Converts the character '0' or '1' into the number 0 or 1.
                Integer.parseInt(bin, 2) ==> The library way; the second argument is the radix (base).""",
                "Know both. Follow-ups: octal and hex conversion, and handling invalid characters by throwing an exception.",
                "O(n)", "O(1)", "Reading the string right to left without the matching power of two.");

        full(l, "Decimal to Binary Conversion", "Math Basics", E, "binary,math,strings",
                "Convert a non-negative integer to its binary string: 13 becomes \"1101\".",
                "Signal: repeatedly divide by 2; the remainders are the bits from last to first.",
                """
                static String toBinary(int n) {
                    return Integer.toBinaryString(n);
                }""",
                """
                static String toBinary(int n) {
                    if (n == 0) return "0";
                    StringBuilder sb = new StringBuilder();
                    while (n > 0) {
                        sb.append(n % 2);
                        n /= 2;
                    }
                    return sb.reverse().toString();
                }""",
                "Each remainder is the lowest remaining bit, so the bits come out in reverse order and must be reversed at the end.",
                """
                if (n == 0) return "0"; ==> The loop would not run for 0 and would return an empty string.
                n % 2 ==> The lowest bit of n (0 or 1).
                n /= 2; ==> Shift right by one bit; same as n >>= 1 for positive numbers.
                sb.reverse() ==> The bits were collected least significant first, so flip them.
                Integer.toBinaryString(n) ==> The built-in alternative.""",
                "Simple but common. Mention negative numbers and two's complement if asked.",
                "O(log n)", "O(log n)", "Forgetting to reverse; returning an empty string for 0.");

        full(l, "Count Set Bits", "Bit Manipulation", E, "bits,loops",
                "Return the number of 1 bits in the binary representation of a non-negative int (the Hamming weight).",
                "Signal: n & (n - 1) clears the lowest set bit, so the number of times you can do that is the answer.",
                """
                static int countBits(int n) {
                    int count = 0;
                    while (n > 0) {
                        count += n % 2;
                        n /= 2;
                    }
                    return count;
                }""",
                """
                static int countBits(int n) {
                    int count = 0;
                    while (n != 0) {
                        n &= (n - 1);   // remove the lowest set bit
                        count++;
                    }
                    return count;
                }""",
                "The naive loop visits every bit. Brian Kernighan's trick visits only the 1 bits: n - 1 flips the lowest 1 and the zeros below it, so n & (n - 1) removes that single 1.",
                """
                n & (n - 1) ==> Example 1100 & 1011 = 1000: the lowest set bit disappeared.
                count++ ==> One set bit was removed, so count it.
                while (n != 0) ==> When no set bits remain n becomes 0.
                Integer.bitCount(n) ==> The built-in method that does this with a hardware instruction.""",
                "A classic bit trick. Explain the n & (n - 1) identity clearly; interviewers want the reasoning, not the memorised line.",
                "O(number of set bits)", "O(1)", "Using n > 0 for negative numbers (they would loop 0 times).");

        full(l, "Sum of Natural Numbers using Recursion", "Recursion", E, "recursion,base-case",
                "Return 1 + 2 + ... + n using recursion.",
                "Signal: sum(n) = n + sum(n - 1), with sum(0) = 0 as the base case.",
                """
                static int sum(int n) {
                    int total = 0;
                    for (int i = 1; i <= n; i++) total += i;
                    return total;
                }""",
                """
                static int sum(int n) {
                    if (n <= 0) return 0;          // base case
                    return n + sum(n - 1);         // smaller problem
                    // closed form: n * (n + 1) / 2
                }""",
                "Recursion needs a base case that stops it and a step that moves towards that base case. The closed form n(n+1)/2 is O(1).",
                """
                if (n <= 0) return 0; ==> Base case. Without it the method would call itself forever until a StackOverflowError.
                n + sum(n - 1) ==> The recursive step: solve a smaller problem and add the current number.
                n * (n + 1) / 2 ==> Gauss's formula: pair the first and last numbers; each pair sums to n + 1 and there are n / 2 pairs.""",
                "Recursion is explained here at its simplest. Interviewers often ask: what happens for n = 100000? (stack overflow; use the loop or formula).",
                "O(n) / O(1)", "O(n) stack / O(1)", "Missing base case; calling sum(n) instead of sum(n - 1).");

        full(l, "Temperature Converter", "Website: Widgets", E, "website,math,formatting",
                "Convert between Celsius and Fahrenheit and format the result with one decimal place, as a weather widget would.",
                "Signal: F = C * 9 / 5 + 32. Use doubles for the formula and format only at the end.",
                """
                static double toFahrenheit(double c) {
                    return c * 9 / 5 + 32;
                }
                static double toCelsius(double f) {
                    return (f - 32) * 5 / 9;
                }""",
                """
                static String display(double celsius) {
                    double f = celsius * 9.0 / 5.0 + 32;
                    return String.format("%.1f\\u00B0C = %.1f\\u00B0F", celsius, f);
                }""",
                "Keep full precision while calculating and round only when displaying. Writing 9 / 5 with two ints would give 1, not 1.8.",
                """
                celsius * 9.0 / 5.0 ==> Use decimal literals so the division keeps its fraction; 9 / 5 with ints is 1.
                + 32 ==> The offset between the two scales.
                String.format("%.1f", ...) ==> Formats a number with exactly one decimal place for display only.
                \\u00B0 ==> The degree sign written as a Unicode escape so source encoding can never corrupt it.""",
                "Small but shows precision awareness: integer division and rounding only at the presentation layer.",
                "O(1)", "O(1)", "Integer division (9 / 5); rounding too early.");

        full(l, "Tip Calculator", "Website: Widgets", E, "website,money,rounding",
                "Given a bill in cents, a tip percentage and the number of people, return how many cents each person pays, rounded UP so the restaurant is never short.",
                "Signal: do money maths in whole cents and round up with integer ceiling division.",
                """
                static long perPerson(long billCents, int tipPercent, int people) {
                    double total = billCents * (1 + tipPercent / 100.0);
                    return (long) Math.ceil(total / people);
                }""",
                """
                static long perPerson(long billCents, int tipPercent, int people) {
                    if (people <= 0) throw new IllegalArgumentException("people must be positive");
                    long tip = (billCents * tipPercent + 99) / 100;       // round the tip up
                    long total = billCents + tip;
                    return (total + people - 1) / people;                 // ceiling division
                }""",
                "Floating point can turn 0.1 + 0.2 into 0.30000000000000004. Integer cents and (x + d - 1) / d give exact rounding up.",
                """
                people <= 0 ==> Validate input: dividing by zero would crash the page.
                (billCents * tipPercent + 99) / 100 ==> Integer ceiling of billCents * tipPercent / 100.
                billCents + tip ==> Total in cents.
                (total + people - 1) / people ==> The standard integer ceiling division trick: adds just enough to round up.""",
                "Shows maturity with money. Be ready to explain why BigDecimal or integer minor units are used instead of double.",
                "O(1)", "O(1)", "Using double for money; forgetting to guard against zero people.");

        return l;
    }
}
