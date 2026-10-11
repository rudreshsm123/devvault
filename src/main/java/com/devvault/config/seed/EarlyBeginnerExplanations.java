package com.devvault.config.seed;

import com.devvault.config.seed.HardProblemExplanations.Explanation;

import java.util.HashMap;
import java.util.Map;

/** Line-by-line explanations for the first batch of beginner problems. */
public final class EarlyBeginnerExplanations {

    private EarlyBeginnerExplanations() {
    }

    private static void e(Map<String, Explanation> m, String title, String lines, String tips) {
        m.put(title, new Explanation(lines.strip(), tips.strip()));
    }

    public static Map<String, Explanation> all() {
        Map<String, Explanation> m = new HashMap<>();

        e(m, "Reverse a String",
                """
                char[] chars = s.toCharArray() ==> Strings are immutable, so copy the characters into an array that can be changed.
                int left = 0, right = chars.length - 1 ==> One pointer at each end.
                chars[left++] = chars[right]; chars[right--] = tmp ==> Swap the two ends and move both pointers inward.
                while (left < right) ==> Stop when the pointers meet; the middle character stays where it is.
                new String(chars) ==> Build the result string once at the end.""",
                "Mention why += in a loop is O(n^2) and that StringBuilder.reverse() exists. Ask about Unicode surrogate pairs only if the interviewer goes there.");

        e(m, "Palindrome Check",
                """
                int i = 0, j = s.length() - 1 ==> Compare from the outside in.
                while (i < j && !Character.isLetterOrDigit(s.charAt(i))) i++ ==> Skip punctuation and spaces on the left.
                Character.toLowerCase(...) != ... ==> Compare ignoring case.
                i++; j-- ==> Move both pointers after a matching pair.
                return true ==> No mismatch found.""",
                "The two-pointer version uses O(1) extra memory; the clean-then-reverse version is easier to read. Say both and the trade-off.");

        e(m, "FizzBuzz",
                """
                for (int i = 1; i <= n; i++) ==> Numbers 1 to n inclusive.
                if (i % 3 == 0) s += "Fizz" ==> Each rule is independent and appends its word.
                if (i % 5 == 0) s += "Buzz" ==> A number divisible by both gets both words, which gives 'FizzBuzz' without a special case.
                s.isEmpty() ? String.valueOf(i) : s ==> If no rule matched, print the number itself.""",
                "The composable version makes adding a new rule trivial, which is the follow-up interviewers like to ask.");

        e(m, "Count Vowels in a Username",
                """
                Set.of('a', 'e', 'i', 'o', 'u') ==> A fixed set of vowels for quick membership tests.
                Character.toLowerCase(s.charAt(i)) ==> Normalise the case so 'A' counts too.
                vowels.contains(...) ==> One lookup instead of a long chain of ||.
                count++ ==> Increase for every vowel found.""",
                "Easy question; the interviewer is checking basic loop and character handling. Ask whether 'y' counts.");

        e(m, "Find Maximum in Array",
                """
                int best = a[0] ==> Start from a real element so negative arrays work.
                for (int i = 1; ...) ==> Index 0 is already in best.
                if (a[i] > best) best = a[i] ==> Keep the larger value.
                Arrays.stream(a).max().getAsInt() ==> The one-line alternative.""",
                "A running-maximum template used by many harder problems. Handle the empty array if asked.");

        e(m, "Sum of Digits",
                """
                n % 10 ==> The last digit.
                sum += n % 10 ==> Add it to the total.
                n /= 10 ==> Remove the last digit.
                while (n > 0) ==> Repeat until no digits are left.""",
                "Remember the pair n % 10 and n / 10, used in reverse integer, palindrome number and many more.");

        e(m, "Factorial",
                """
                if (n <= 1) return 1 ==> Base case: 0! and 1! are 1.
                n * factorial(n - 1) ==> Recursive step: a smaller version of the same problem.
                long result ==> Use long because 13! already overflows an int.
                for (int i = 2; i <= n; i++) result *= i ==> The iterative version, with no stack depth.""",
                "State the base case first and mention overflow. Ask what happens for negative input.");

        e(m, "Fibonacci Number",
                """
                if (n < 2) return n ==> F(0) = 0 and F(1) = 1.
                int prev = 0, cur = 1 ==> The last two Fibonacci numbers.
                int next = prev + cur ==> The next number is the sum of the previous two.
                prev = cur; cur = next ==> Slide the two-value window forward.""",
                "Show the exponential recursion, explain why it repeats work, then reduce it to O(n) time and O(1) space. This is the gateway to dynamic programming.");

        e(m, "Prime Number Check",
                """
                if (n < 2) return false ==> 0 and 1 are not prime.
                if (n % 2 == 0) return n == 2 ==> Even numbers are only prime if they are 2.
                (long) i * i <= n ==> Divisors come in pairs, so testing up to sqrt(n) is enough; the cast avoids overflow.
                i += 2 ==> Only odd divisors need testing.""",
                "Mention the sieve of Eratosthenes if many numbers must be tested.");

        e(m, "Remove Duplicates from Sorted Array",
                """
                int write = 1 ==> The next slot to fill; index 0 is always unique.
                a[read] != a[write - 1] ==> Sorted data: a new value differs from the last one written.
                a[write++] = a[read] ==> Copy the unique value forward.
                return write ==> The number of unique elements.""",
                "A standard slow/fast pointer example. Clarify that elements past the returned length do not matter.");

        e(m, "Contains Duplicate",
                """
                Set<Integer> seen ==> The numbers visited so far.
                seen.add(x) returns false ==> The value was already present, which means a duplicate.
                return true early ==> No need to look at the rest.
                return false ==> Reached the end without repeats.""",
                "Offer the sorting alternative (O(n log n), O(1) space) to show you understand the trade-off.");

        e(m, "Valid Anagram",
                """
                s.length() != t.length() ==> Different lengths can never be anagrams.
                int[] count = new int[26] ==> One counter per lowercase letter.
                count[s.charAt(i) - 'a']++ and count[t.charAt(i) - 'a']-- ==> Letters of s add, letters of t subtract.
                any count != 0 ==> The letter counts differ.""",
                "Ask whether input is lowercase only; for Unicode use a HashMap.");

        e(m, "First Unique Character",
                """
                int[] freq = new int[26] ==> Count every letter first.
                freq[c - 'a']++ ==> One pass to build the counts.
                second loop over the string ==> Walk in original order so the FIRST unique letter wins.
                freq[...] == 1 ==> Returns the index of the first letter that appears exactly once.""",
                "Two passes: count, then scan. Say the complexity is O(n) with O(1) memory for 26 letters.");

        e(m, "Merge Two Sorted Arrays",
                """
                int i = 0, j = 0, k = 0 ==> One pointer for each input and one for the output.
                a[i] <= b[j] ? a[i++] : b[j++] ==> Always take the smaller front element.
                while (i < a.length) ==> Copy the leftovers of a.
                while (j < b.length) ==> Copy the leftovers of b.""",
                "This is the merge step of merge sort. Mention the in-place variant that fills from the back.");

        e(m, "Move Zeroes",
                """
                int write = 0 ==> Where the next non-zero element goes.
                a[read] != 0 ==> Only non-zero values are moved forward.
                swap(a[write++], a[read]) ==> Swapping pushes zeros towards the end while keeping the order of the others.
                no extra array ==> O(1) memory.""",
                "Ask whether the order of non-zero elements must be preserved (it is). Count the writes to optimise.");

        e(m, "Best Time to Buy and Sell Stock",
                """
                minPrice = Math.min(minPrice, price) ==> The cheapest day seen so far.
                price - minPrice ==> The profit if we sell today and bought at the cheapest earlier price.
                best = Math.max(best, ...) ==> Keep the best profit.
                one pass ==> O(n) time and O(1) space.""",
                "State the brute force (all pairs) and then the one-pass idea. This leads to the stock problems family.");

        e(m, "Climbing Stairs",
                """
                if (n <= 2) return n ==> One way for one step, two ways for two steps.
                int a = 1, b = 2 ==> Ways to reach steps 1 and 2.
                int c = a + b ==> Steps n comes from n - 1 or n - 2.
                a = b; b = c ==> Keep only the last two values.""",
                "Recognise it as Fibonacci. Mention generalising to k steps with a sliding sum.");

        e(m, "Roman to Integer",
                """
                value(char) ==> A switch mapping each Roman symbol to its value.
                cur < next ==> A smaller symbol before a larger one is subtracted (IV = 4).
                total += cur < next ? -cur : cur ==> Subtract or add accordingly.
                i + 1 < s.length() ==> Guard the look-ahead at the last character.""",
                "State the subtraction rule and validate input if asked. Integer to Roman is the reverse follow-up.");

        e(m, "Longest Common Prefix",
                """
                String prefix = words[0] ==> The prefix can only shrink, so start with the first word.
                !w.startsWith(prefix) ==> The current prefix does not fit this word.
                prefix.substring(0, prefix.length() - 1) ==> Drop the last character and test again.
                empty array check ==> Return an empty string if there are no words.""",
                "Alternatives: vertical scanning and binary search on the length. Mention the edge cases.");

        e(m, "Binary Search (Find Index)",
                """
                lo = 0, hi = a.length - 1 ==> The current search range.
                mid = lo + (hi - lo) / 2 ==> Midpoint written to avoid integer overflow.
                a[mid] < target then lo = mid + 1 ==> The target must be on the right.
                else hi = mid - 1 ==> The target must be on the left.
                while (lo <= hi) ==> Stop when the range is empty: not found.""",
                "Practise writing it without bugs from memory; off-by-one errors are the usual failure. Test arrays of size 0, 1 and 2.");

        e(m, "Power of Two",
                """
                n > 0 ==> Zero and negatives are never powers of two.
                n & (n - 1) ==> Clears the lowest set bit.
                == 0 ==> A power of two has exactly one set bit, so clearing it leaves zero.""",
                "The bit trick is the expected answer; also explain it with an example such as 8 & 7.");

        e(m, "Single Number",
                """
                result ^= x ==> XOR every number into one accumulator.
                a ^ a = 0 ==> Numbers that appear twice cancel out.
                a ^ 0 = a ==> The unpaired number survives.
                no map needed ==> O(1) extra memory.""",
                "Explain XOR properties (commutative, associative). Follow-up: every number appears three times except one.");

        e(m, "Reverse Linked List",
                """
                prev = null, cur = head ==> prev is the reversed part so far.
                next = cur.next ==> Save the rest of the list before changing the link.
                cur.next = prev ==> Point the node backwards.
                prev = cur; cur = next ==> Advance both pointers.
                return prev ==> prev is the new head.""",
                "Draw three nodes and move the pointers. Mention the recursive version and its O(n) stack use.");

        e(m, "Maximum Depth of Binary Tree",
                """
                if (root == null) return 0 ==> An empty tree has depth 0.
                maxDepth(root.left) / maxDepth(root.right) ==> Ask each subtree for its depth.
                1 + Math.max(...) ==> The node itself plus the deeper child.
                BFS version with level counting ==> Count levels instead of using recursion.""",
                "A template for many tree problems: solve the children, combine the answers.");

        e(m, "Shopping Cart Total",
                """
                long unitPriceCents ==> Money is stored in whole cents, never in double.
                i.qty() < 0 ==> Validate input before calculating.
                sum += unitPriceCents * qty ==> Accumulate line totals using long to avoid overflow.
                stream mapToLong(...).sum() ==> The compact equivalent.""",
                "Money questions test precision awareness. Mention BigDecimal as the alternative.");

        e(m, "Email Format Validator",
                """
                indexOf('@') and lastIndexOf('@') ==> Exactly one @ must exist and not at the start.
                domain.lastIndexOf('.') ==> The domain needs a dot that is not first or last.
                Pattern.compile(...) as a static constant ==> Compile the regex once, not on every call.
                matcher(e).matches() ==> The whole string must match the pattern.""",
                "State that real validation means sending a confirmation email. Never write a regex that claims to cover the entire email specification.");

        e(m, "Word Frequency Counter (Blog Analytics)",
                """
                text.toLowerCase().split("[^a-z0-9']+") ==> Lower-case and split on anything that is not part of a word.
                counts.merge(w, 1, Integer::sum) ==> Increment a counter in one call.
                if (w.isEmpty()) continue ==> split can return an empty first token.
                TreeMap ==> Gives alphabetically sorted output.""",
                "Follow-up: top 10 words using a heap, and ignoring stop words.");

        e(m, "Password Strength Checker",
                """
                length >= 8 ==> One point for length.
                Character.isDigit / isUpperCase / non-alphanumeric ==> One point for each character class.
                List<Predicate<String>> rules ==> The rules as data, so new ones can be added without touching the loop.
                count of rules that pass ==> The final score from 0 to 4.""",
                "Mention hashing passwords with bcrypt/argon2 and that length matters more than complexity rules.");

        e(m, "Pagination Page Calculator",
                """
                (total + pageSize - 1) / pageSize ==> Integer ceiling division gives the number of pages.
                Math.min(Math.max(page, 1), pages) ==> Clamp user-supplied page numbers into the valid range.
                start = (page - 1) * pageSize ==> The offset of the first item on the page.
                Math.min(start + pageSize, total) ==> The last page may be shorter.""",
                "Mention keyset pagination for large tables and how Spring Data's Pageable does this for you.");

        e(m, "Days in Month (Calendar Widget)",
                """
                leap year rule ==> Divisible by 4, except centuries unless divisible by 400.
                case 4, 6, 9, 11 -> 30 ==> The 30-day months.
                case 2 -> leap ? 29 : 28 ==> February depends on the year.
                YearMonth.of(year, month).lengthOfMonth() ==> The java.time shortcut.""",
                "Prefer java.time in real code but know the leap year rule by heart.");

        e(m, "Page Views per User",
                """
                views.put(u, views.getOrDefault(u, 0) + 1) ==> Count events per user.
                compare counts, then compare ids ==> A deterministic tie-break.
                Collectors.groupingBy(..., counting()) ==> The stream version of the same grouping.
                max(...) ==> Pick the user with the largest count.""",
                "This is SQL GROUP BY in Java. Ask how ties must be handled before coding.");

        e(m, "Star Rating Average",
                """
                ratings.length == 0 ==> Avoid dividing by zero for a product with no reviews.
                (double) sum / ratings.length ==> Cast first so the average keeps its decimals.
                Math.round(x * 10) / 10.0 ==> Round to one decimal place.
                orElse(0.0) ==> Default for an empty stream.""",
                "Point out integer division and rounding only for display. A weighted (Bayesian) average is the advanced follow-up.");

        e(m, "Case-Insensitive Duplicate Usernames",
                """
                n.toLowerCase(Locale.ROOT) ==> Normalise the key; Locale.ROOT avoids the Turkish i problem.
                seen.add(key) returns false ==> The name clashes with an earlier one.
                dup.add(n) ==> Report the original spelling.
                UNIQUE index on lower(username) ==> The database must enforce it too, because checks in code race.""",
                "Interviewers like the point that application checks alone are not enough under concurrent sign-ups.");

        e(m, "URL Slug Generator",
                """
                title.toLowerCase(Locale.ROOT) ==> Slugs are lower case.
                replaceAll("[^a-z0-9]+", "-") ==> Any run of other characters becomes one hyphen.
                replaceAll("^-|-$", "") ==> Trim leading and trailing hyphens.
                uniqueness ==> Real sites append an id or counter when two titles produce the same slug.""",
                "Mention Unicode titles and how to transliterate them, and keeping slugs stable after publishing.");

        e(m, "Majority Element",
                """
                candidate, count ==> Boyer-Moore voting state.
                if (count == 0) candidate = x ==> Adopt a new candidate when the old one is cancelled out.
                count += (x == candidate) ? 1 : -1 ==> Matching votes add, different ones cancel.
                return candidate ==> The majority element survives all cancellations.""",
                "Explain the cancellation argument. If a majority is not guaranteed, verify the candidate with a second pass.");

        e(m, "Check Sorted Array",
                """
                for (int i = 1; ...) ==> Compare each element with its predecessor.
                a[i - 1] > a[i] ==> A decrease means the array is not sorted.
                return true ==> No violation found.
                early exit ==> Stops at the first problem.""",
                "Easy, but mention that <= allows duplicates (non-decreasing order).");

        return m;
    }
}
