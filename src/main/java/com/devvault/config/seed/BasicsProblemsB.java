package com.devvault.config.seed;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.ProblemFactory.full;

/** Beginner set B: strings, arrays, matrices, small linked lists and trees, plus website-style helpers. */
public final class BasicsProblemsB {

    private BasicsProblemsB() {
    }

    public static List<CodingProblem> all() {
        List<CodingProblem> l = new ArrayList<>();
        Difficulty E = Difficulty.EASY;

        full(l, "Age Calculator from Birth Date", "Website: Widgets", E, "website,dates,java-time",
                "Given a birth date and today's date, return the age in complete years.",
                "Signal: subtract the years, then take one off if the birthday has not happened yet this year.",
                """
                static int age(LocalDate birth, LocalDate today) {
                    int age = today.getYear() - birth.getYear();
                    if (today.getMonthValue() < birth.getMonthValue()) age--;
                    else if (today.getMonthValue() == birth.getMonthValue()
                            && today.getDayOfMonth() < birth.getDayOfMonth()) age--;
                    return age;
                }""",
                """
                static int age(LocalDate birth, LocalDate today) {
                    if (birth.isAfter(today)) throw new IllegalArgumentException("birth date is in the future");
                    return Period.between(birth, today).getYears();
                }""",
                "java.time already knows about month lengths and leap years. Period.between returns years, months and days between two dates.",
                """
                birth.isAfter(today) ==> Reject impossible input early; an age cannot be negative.
                Period.between(birth, today) ==> Calculates the calendar difference, correctly handling leap years and month lengths.
                .getYears() ==> Only the complete years part of the period.
                today.getYear() - birth.getYear() ==> The manual version: the naive year difference is one too high until the birthday passes.""",
                "Show you prefer java.time over hand-written date maths and never use the old Date class. Mention passing 'today' as a parameter so the method is testable.",
                "O(1)", "O(1)", "Using only the year difference; reading the system clock inside the method (hard to test).");

        full(l, "Simple Interest and EMI Calculator", "Website: Widgets", E, "website,finance,math,bigdecimal",
                "Compute simple interest, and the monthly EMI for a loan: EMI = P * r * (1+r)^n / ((1+r)^n - 1), where r is the monthly rate.",
                "Signal: a formula problem - translate carefully, keep types as double, round only for display.",
                """
                static double simpleInterest(double principal, double ratePercent, double years) {
                    return principal * ratePercent * years / 100;
                }""",
                """
                static double emi(double principal, double annualPercent, int months) {
                    double r = annualPercent / 12 / 100;
                    if (r == 0) return principal / months;
                    double growth = Math.pow(1 + r, months);
                    return principal * r * growth / (growth - 1);
                }""",
                "EMI spreads the loan and its interest into equal monthly payments. The special case r == 0 avoids dividing by zero for interest-free loans.",
                """
                annualPercent / 12 / 100 ==> Converts '12 percent a year' into the monthly decimal rate 0.01.
                if (r == 0) ==> With zero interest the formula divides by zero, so just split the principal evenly.
                Math.pow(1 + r, months) ==> The compound growth factor over the whole loan period.
                principal * r * growth / (growth - 1) ==> The standard EMI formula.""",
                "The point is to read a formula and handle edge cases (zero rate, zero months). For real money use BigDecimal and a rounding mode.",
                "O(1)", "O(1)", "Percent vs decimal confusion; dividing by zero when the rate is 0.");

        full(l, "Grade Calculator", "Website: Widgets", E, "website,conditions,arrays",
                "Convert a mark from 0 to 100 into a grade: A (90+), B (80+), C (70+), D (60+), otherwise F. Reject marks outside 0-100.",
                "Signal: ordered thresholds - test from highest to lowest or search a lookup table.",
                """
                static char grade(int mark) {
                    if (mark < 0 || mark > 100) throw new IllegalArgumentException("mark out of range");
                    if (mark >= 90) return 'A';
                    if (mark >= 80) return 'B';
                    if (mark >= 70) return 'C';
                    if (mark >= 60) return 'D';
                    return 'F';
                }""",
                """
                private static final int[] CUTOFFS = {90, 80, 70, 60};
                private static final char[] GRADES = {'A', 'B', 'C', 'D'};

                static char grade(int mark) {
                    if (mark < 0 || mark > 100) throw new IllegalArgumentException("mark out of range");
                    for (int i = 0; i < CUTOFFS.length; i++) {
                        if (mark >= CUTOFFS[i]) return GRADES[i];
                    }
                    return 'F';
                }""",
                "The table version moves the policy (cutoffs) into data. Changing the grading scale no longer needs new if statements.",
                """
                mark < 0 || mark > 100 ==> Validate first (guard clause) so impossible marks never produce a grade.
                if (mark >= 90) return 'A'; ==> Highest threshold first: a mark of 95 must stop here, not fall into 'B'.
                CUTOFFS / GRADES ==> Two parallel arrays act as a lookup table; position i links a cutoff to its grade.
                return 'F'; ==> Everything below the last cutoff.""",
                "A discussion of data-driven rules vs hard-coded conditions earns extra credit. Order of conditions is the usual bug.",
                "O(1)", "O(1)", "Checking the lowest threshold first so everything becomes 'D'.");

        full(l, "Count Consonants and Digits", "Strings", E, "strings,characters,counting",
                "Return how many consonants and how many digits are in a string.",
                "Signal: classify each character once using Character helper methods.",
                """
                static int[] count(String s) {
                    int consonants = 0, digits = 0;
                    for (char c : s.toLowerCase().toCharArray()) {
                        if (c >= '0' && c <= '9') digits++;
                        else if (c >= 'a' && c <= 'z' && c != 'a' && c != 'e' && c != 'i' && c != 'o' && c != 'u') consonants++;
                    }
                    return new int[] { consonants, digits };
                }""",
                """
                static int[] count(String s) {
                    int consonants = 0, digits = 0;
                    for (char c : s.toLowerCase().toCharArray()) {
                        if (Character.isDigit(c)) digits++;
                        else if (Character.isLetter(c) && "aeiou".indexOf(c) < 0) consonants++;
                    }
                    return new int[] { consonants, digits };
                }""",
                "A consonant is a letter that is not a vowel, so test letter first and then 'not in aeiou'.",
                """
                s.toLowerCase() ==> Normalise so 'A' and 'a' are treated the same.
                Character.isDigit(c) ==> True for 0-9 (and digits of other scripts).
                Character.isLetter(c) ==> Excludes spaces and punctuation, which are neither consonants nor vowels.
                "aeiou".indexOf(c) < 0 ==> indexOf returns -1 when the character is absent: a quick 'is not a vowel' test.""",
                "Good for checking you know the Character class instead of hand-written ranges.",
                "O(n)", "O(1)", "Counting spaces and punctuation as consonants.");

        full(l, "Remove Duplicate Characters", "Strings", E, "strings,set,order",
                "Return the string with repeated characters removed, keeping the first occurrence: \"programming\" becomes \"progamin\".",
                "Signal: a LinkedHashSet remembers seen items while preserving the original order.",
                """
                static String dedupe(String s) {
                    String out = "";
                    for (char c : s.toCharArray()) {
                        if (out.indexOf(c) < 0) out += c;
                    }
                    return out;
                }""",
                """
                static String dedupe(String s) {
                    Set<Character> seen = new HashSet<>();
                    StringBuilder sb = new StringBuilder();
                    for (char c : s.toCharArray()) {
                        if (seen.add(c)) sb.append(c);
                    }
                    return sb.toString();
                }""",
                "indexOf on the growing result is a hidden loop (O(n^2)). A HashSet answers 'seen before?' in O(1).",
                """
                Set<Character> seen ==> The memory of characters already kept.
                seen.add(c) ==> Returns true only the first time c is added, so it checks and records in one step.
                sb.append(c) ==> Keep the first occurrence only, preserving original order.
                out.indexOf(c) < 0 ==> The slower alternative: search the result built so far.""",
                "Mention LinkedHashSet or the StringBuilder approach, and ask whether case matters.",
                "O(n)", "O(alphabet)", "Using a HashSet and then iterating it (order is lost).");

        full(l, "Most Frequent Character", "Hash Map", E, "strings,counting,hash-map",
                "Return the character that appears most often in a string (ties: the one that appears first).",
                "Signal: count with a map or array, then scan in original order to break ties.",
                """
                static char mostFrequent(String s) {
                    char best = s.charAt(0);
                    int bestCount = 0;
                    for (char c : s.toCharArray()) {
                        int count = 0;
                        for (char d : s.toCharArray()) if (d == c) count++;
                        if (count > bestCount) { bestCount = count; best = c; }
                    }
                    return best;
                }""",
                """
                static char mostFrequent(String s) {
                    Map<Character, Integer> count = new HashMap<>();
                    for (char c : s.toCharArray()) count.merge(c, 1, Integer::sum);
                    char best = s.charAt(0);
                    for (char c : s.toCharArray()) {
                        if (count.get(c) > count.get(best)) best = c;
                    }
                    return best;
                }""",
                "Counting everything first is O(n). The second scan goes through the string in order, so a tie keeps the earliest character because we only replace on a strictly higher count.",
                """
                count.merge(c, 1, Integer::sum) ==> Build the frequency table in one pass.
                char best = s.charAt(0); ==> Start with the first character as the champion.
                count.get(c) > count.get(best) ==> Strictly greater means ties never replace the earlier character.
                for (char c : s.toCharArray()) ==> Scanning the original string (not the map) preserves first-appearance order.""",
                "Tie-breaking is the real test. Ask the interviewer how ties should be treated before coding.",
                "O(n)", "O(alphabet)", "Iterating the HashMap and getting arbitrary tie order.");

        full(l, "String Compression (Run-Length)", "Strings", E, "strings,two-pointers,encoding",
                "Compress repeated characters: \"aaabbc\" becomes \"a3b2c1\". Return the original if the compressed form is not shorter.",
                "Signal: count a run of equal characters, then write the character and the count.",
                """
                static String compress(String s) {
                    String out = "";
                    int i = 0;
                    while (i < s.length()) {
                        int j = i;
                        while (j < s.length() && s.charAt(j) == s.charAt(i)) j++;
                        out += s.charAt(i) + "" + (j - i);
                        i = j;
                    }
                    return out.length() < s.length() ? out : s;
                }""",
                """
                static String compress(String s) {
                    StringBuilder sb = new StringBuilder();
                    int count = 1;
                    for (int i = 1; i <= s.length(); i++) {
                        if (i < s.length() && s.charAt(i) == s.charAt(i - 1)) {
                            count++;
                        } else {
                            sb.append(s.charAt(i - 1)).append(count);
                            count = 1;
                        }
                    }
                    return sb.length() < s.length() ? sb.toString() : s;
                }""",
                "The loop runs one step past the end (i <= length) so the last run is flushed by the same else branch, with no duplicated code after the loop.",
                """
                int count = 1; ==> The current run already contains one character.
                i <= s.length() ==> Deliberately goes one past the end so the final run is written.
                i < s.length() && s.charAt(i) == s.charAt(i - 1) ==> Same as the previous character: extend the run.
                sb.append(s.charAt(i - 1)).append(count) ==> The run ended: write the character and its length.
                sb.length() < s.length() ==> Only return the compressed form if it is actually shorter.""",
                "Test the empty string, single character and a string with no repeats. The 'flush at the end' trick is what interviewers look for.",
                "O(n)", "O(n)", "Forgetting the last run; crashing on the empty string.");

        full(l, "Valid Username Checker", "Website: Sign-up Form", E, "website,strings,validation",
                "A username is valid if it is 3-16 characters, starts with a letter and contains only letters, digits and underscores.",
                "Signal: validation rules - check them one at a time and fail fast.",
                """
                static boolean isValid(String u) {
                    if (u == null || u.length() < 3 || u.length() > 16) return false;
                    if (!Character.isLetter(u.charAt(0))) return false;
                    for (char c : u.toCharArray()) {
                        if (!Character.isLetterOrDigit(c) && c != '_') return false;
                    }
                    return true;
                }""",
                """
                private static final Pattern USERNAME = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{2,15}$");

                static boolean isValid(String u) {
                    return u != null && USERNAME.matcher(u).matches();
                }""",
                "The regex states all rules in one line and is compiled once. The manual version is easier to give custom error messages for each rule.",
                """
                u == null ==> Always handle null input first; real forms can send missing fields.
                Character.isLetter(u.charAt(0)) ==> First-character rule.
                !Character.isLetterOrDigit(c) && c != '_' ==> Reject any character outside the allowed set.
                [A-Za-z][A-Za-z0-9_]{2,15} ==> One letter plus 2 to 15 more allowed characters equals 3 to 16 in total.
                private static final Pattern ==> Compile once and reuse; compiling per request wastes CPU.""",
                "Mention that the database also needs a UNIQUE constraint and that checks happen on the server even if the browser validates.",
                "O(n)", "O(1)", "Forgetting null; compiling the regex on every call.");

        full(l, "Mask a Credit Card Number", "Website: Checkout", E, "website,strings,security",
                "Show only the last four digits of a card number, replacing earlier digits with '*', and keep spaces: \"4111 1111 1111 1234\" becomes \"**** **** **** 1234\".",
                "Signal: walk from the right, counting digits; mask everything after the fourth digit.",
                """
                static String mask(String card) {
                    int digits = 0;
                    StringBuilder out = new StringBuilder();
                    for (int i = card.length() - 1; i >= 0; i--) {
                        char c = card.charAt(i);
                        if (Character.isDigit(c)) {
                            out.append(++digits <= 4 ? c : '*');
                        } else {
                            out.append(c);
                        }
                    }
                    return out.reverse().toString();
                }""",
                """
                static String mask(String card) {
                    String digitsOnly = card.replaceAll("\\\\D", "");
                    if (digitsOnly.length() < 4) throw new IllegalArgumentException("too short");
                    String last4 = digitsOnly.substring(digitsOnly.length() - 4);
                    return "**** **** **** " + last4;
                }""",
                "The first version preserves the original formatting. The second always uses a fixed display format and is simpler. In real systems you should never store the full number at all.",
                """
                for (int i = card.length() - 1; i >= 0; i--) ==> Walk from the right so the last four digits are met first.
                ++digits <= 4 ? c : '*' ==> The first four digits seen from the right are kept; later ones are replaced.
                out.reverse() ==> We appended right to left, so flip the result back.
                replaceAll("\\\\D", "") ==> \\D means 'not a digit': removes spaces and dashes.
                "**** **** **** " + last4 ==> Fixed display format for receipts.""",
                "Security awareness is the point: PCI rules forbid logging or showing the full number, and the card number should come from a payment provider's tokens.",
                "O(n)", "O(n)", "Logging the original number; masking the last digits instead of the first.");

        full(l, "Extract Domain from an Email or URL", "Website: Parsing", E, "website,strings,parsing",
                "From \"asha@example.co.in\" return \"example.co.in\", and from \"https://www.example.com/path?x=1\" return \"www.example.com\".",
                "Signal: find the delimiters (@ or ://) and cut with substring.",
                """
                static String domainOfEmail(String email) {
                    int at = email.indexOf('@');
                    if (at < 0 || at == email.length() - 1) throw new IllegalArgumentException("not an email");
                    return email.substring(at + 1);
                }

                static String hostOfUrl(String url) {
                    int start = url.indexOf("://");
                    start = start < 0 ? 0 : start + 3;
                    int end = start;
                    while (end < url.length() && "/?#:".indexOf(url.charAt(end)) < 0) end++;
                    return url.substring(start, end);
                }""",
                """
                static String hostOfUrl(String url) {
                    return URI.create(url).getHost();
                }""",
                "Hand parsing teaches string indexes, but the standard URI class follows the real URL rules (ports, user info, IPv6) and should be used in production.",
                """
                email.indexOf('@') ==> Position of the separator, or -1 if missing.
                email.substring(at + 1) ==> Everything after the @.
                url.indexOf("://") ==> End of the scheme (https). start moves 3 characters past it.
                "/?#:".indexOf(url.charAt(end)) < 0 ==> Keep reading until a character that ends the host: path, query, fragment or port.
                URI.create(url).getHost() ==> The library handles the edge cases for you.""",
                "Good answer: do it by hand, then say 'in production I would use java.net.URI'. Handle invalid input.",
                "O(n)", "O(n)", "Not handling missing delimiters; cutting at the first dot.");

        full(l, "Title Case Converter", "Website: Blog", E, "website,strings,formatting",
                "Convert a headline to Title Case: \"the quick brown fox\" becomes \"The Quick Brown Fox\".",
                "Signal: split into words, fix the first letter of each, join back.",
                """
                static String titleCase(String s) {
                    StringBuilder sb = new StringBuilder();
                    boolean startOfWord = true;
                    for (char c : s.toCharArray()) {
                        if (Character.isWhitespace(c)) {
                            startOfWord = true;
                            sb.append(c);
                        } else if (startOfWord) {
                            sb.append(Character.toUpperCase(c));
                            startOfWord = false;
                        } else {
                            sb.append(Character.toLowerCase(c));
                        }
                    }
                    return sb.toString();
                }""",
                """
                static String titleCase(String s) {
                    return Arrays.stream(s.trim().split("\\\\s+"))
                            .map(w -> w.isEmpty() ? w : Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                            .collect(Collectors.joining(" "));
                }""",
                "The state machine keeps the original spacing; the stream version normalises spaces. Real title case also keeps small words (a, the, of) lowercase, which is a follow-up.",
                """
                boolean startOfWord ==> State: is the next letter the first of a word?
                Character.isWhitespace(c) ==> A space means the next letter starts a new word.
                toUpperCase(c) / toLowerCase(c) ==> Capitalise the first letter and lower the rest so 'hELLO' becomes 'Hello'.
                Collectors.joining(" ") ==> Joins the processed words back with single spaces.""",
                "Ask about acronyms and small words. A state-machine loop is a useful pattern for many parsing problems.",
                "O(n)", "O(n)", "Using split(\" \") and crashing on double spaces.");

        full(l, "Count Occurrences of a Word", "Strings", E, "strings,search,loops",
                "Count how many times a word appears in a text as a whole word, ignoring case.",
                "Signal: 'whole word' means word boundaries, so split instead of using contains.",
                """
                static int countWord(String text, String word) {
                    int count = 0;
                    String t = text.toLowerCase(), w = word.toLowerCase();
                    int idx = t.indexOf(w);
                    while (idx >= 0) {
                        count++;
                        idx = t.indexOf(w, idx + 1);       // may count 'cat' inside 'concatenate'
                    }
                    return count;
                }""",
                """
                static int countWord(String text, String word) {
                    int count = 0;
                    for (String token : text.toLowerCase().split("[^a-z0-9']+")) {
                        if (token.equals(word.toLowerCase())) count++;
                    }
                    return count;
                }""",
                "The first version counts substrings, so 'cat' matches inside 'concatenate'. Splitting on non-word characters counts only complete words.",
                """
                text.toLowerCase() ==> Case-insensitive comparison by normalising both sides.
                split("[^a-z0-9']+") ==> Splits on every run of characters that are not part of a word, removing punctuation too.
                token.equals(word.toLowerCase()) ==> Exact word match, not a substring match.
                t.indexOf(w, idx + 1) ==> Substring search starting after the previous hit (the flawed version).""",
                "Interviewers check whether you notice the substring vs whole-word difference. Ask for clarification first.",
                "O(n)", "O(n)", "Using contains/indexOf for whole-word matching.");

        full(l, "Intersection of Two Arrays", "Hash Set", E, "arrays,hash-set,intersection",
                "Return the distinct values that appear in both arrays.",
                "Signal: put one array in a set, then test the other against it.",
                """
                static List<Integer> intersection(int[] a, int[] b) {
                    List<Integer> out = new ArrayList<>();
                    for (int x : a) {
                        for (int y : b) {
                            if (x == y && !out.contains(x)) out.add(x);
                        }
                    }
                    return out;
                }""",
                """
                static List<Integer> intersection(int[] a, int[] b) {
                    Set<Integer> inA = new HashSet<>();
                    for (int x : a) inA.add(x);
                    Set<Integer> result = new HashSet<>();
                    for (int y : b) if (inA.contains(y)) result.add(y);
                    return new ArrayList<>(result);
                }""",
                "Nested loops cost O(n*m). Two passes with a HashSet cost O(n+m). The result set also removes duplicates automatically.",
                """
                Set<Integer> inA ==> Fast membership test for the first array.
                inA.contains(y) ==> O(1) check that replaces the inner loop.
                Set<Integer> result ==> A set so that a value appearing many times is reported once.
                new ArrayList<>(result) ==> Convert to the requested list type.""",
                "Follow-ups: sorted arrays (two pointers), very large arrays (sort and merge), and keeping duplicates (use counts).",
                "O(n + m)", "O(n)", "Returning duplicates; using List.contains inside the loop.");

        full(l, "Remove Element In Place", "Two Pointers", E, "arrays,two-pointers,in-place",
                "Remove all occurrences of a value from an array in place and return the new length.",
                "Signal: a write pointer collects the elements to keep.",
                """
                static int removeElement(int[] a, int val) {
                    int[] kept = new int[a.length];
                    int k = 0;
                    for (int x : a) if (x != val) kept[k++] = x;
                    System.arraycopy(kept, 0, a, 0, k);
                    return k;
                }""",
                """
                static int removeElement(int[] a, int val) {
                    int write = 0;
                    for (int read = 0; read < a.length; read++) {
                        if (a[read] != val) {
                            a[write++] = a[read];
                        }
                    }
                    return write;
                }""",
                "The read pointer visits every element; the write pointer only advances for elements we keep. No extra array is needed.",
                """
                int write = 0; ==> Where the next kept element must go; also the count of kept elements.
                a[read] != val ==> Keep everything that is not the unwanted value.
                a[write++] = a[read]; ==> Copy forward, then advance the write pointer.
                return write; ==> The new logical length; the elements after it are ignored.""",
                "A basic two-pointer drill. Mention stability (order kept) and the alternative of swapping with the last element when order does not matter.",
                "O(n)", "O(1)", "Shifting all elements on every removal (O(n^2)).");

        full(l, "Maximum Consecutive Ones", "Arrays", E, "arrays,counting,one-pass",
                "Given a binary array, return the longest run of 1s.",
                "Signal: keep a current run length and a best so far; reset on 0.",
                """
                static int longestRun(int[] a) {
                    int best = 0;
                    for (int i = 0; i < a.length; i++) {
                        int run = 0;
                        while (i + run < a.length && a[i + run] == 1) run++;
                        best = Math.max(best, run);
                    }
                    return best;
                }""",
                """
                static int longestRun(int[] a) {
                    int best = 0, run = 0;
                    for (int x : a) {
                        run = (x == 1) ? run + 1 : 0;
                        best = Math.max(best, run);
                    }
                    return best;
                }""",
                "The first version re-scans runs from every index. The one-pass version keeps a counter that grows on 1 and resets on 0.",
                """
                int best = 0, run = 0; ==> best is the answer so far, run the current streak.
                run = (x == 1) ? run + 1 : 0; ==> Extend the streak or reset it.
                best = Math.max(best, run); ==> Update after every step so a streak ending the array is counted.""",
                "Typical 'running counter' warm-up; extend it to 'at most one flip' with a sliding window.",
                "O(n)", "O(1)", "Updating best only when the streak breaks (misses a run at the array end).");

        full(l, "Plus One (Array Increment)", "Arrays", E, "arrays,math,carry",
                "A number is stored as an array of digits, most significant first. Add one: [1,2,9] becomes [1,3,0] and [9,9] becomes [1,0,0].",
                "Signal: add from the right and carry.",
                """
                static int[] plusOne(int[] digits) {
                    BigInteger n = new BigInteger(Arrays.stream(digits).mapToObj(String::valueOf).collect(Collectors.joining()));
                    String s = n.add(BigInteger.ONE).toString();
                    return s.chars().map(c -> c - '0').toArray();
                }""",
                """
                static int[] plusOne(int[] digits) {
                    for (int i = digits.length - 1; i >= 0; i--) {
                        if (digits[i] < 9) {
                            digits[i]++;
                            return digits;
                        }
                        digits[i] = 0;
                    }
                    int[] result = new int[digits.length + 1];
                    result[0] = 1;
                    return result;
                }""",
                "Only trailing 9s turn into 0 with a carry. If every digit was 9 the number grows by one digit, and the new array is all zeros except the leading 1.",
                """
                for (int i = digits.length - 1; ...) ==> Start with the least significant digit.
                if (digits[i] < 9) { digits[i]++; return digits; } ==> No carry: finish immediately.
                digits[i] = 0; ==> The digit was 9: it becomes 0 and the carry moves left.
                new int[digits.length + 1]; result[0] = 1; ==> All digits were 9, so we need an extra leading 1 (the new array is already filled with zeros).""",
                "A carry problem. Explain the 'all nines' case without being asked.",
                "O(n)", "O(1)", "Converting to int/long (overflow for long digit arrays).");

        full(l, "Matrix Transpose", "Matrix", E, "matrix,arrays,nested-loops",
                "Return the transpose of a matrix (rows become columns).",
                "Signal: element [i][j] moves to [j][i]; the result has swapped dimensions.",
                """
                static int[][] transpose(int[][] m) {
                    int rows = m.length, cols = m[0].length;
                    int[][] t = new int[cols][rows];
                    for (int i = 0; i < rows; i++)
                        for (int j = 0; j < cols; j++)
                            t[j][i] = m[i][j];
                    return t;
                }""",
                """
                static void transposeSquareInPlace(int[][] m) {
                    for (int i = 0; i < m.length; i++) {
                        for (int j = i + 1; j < m.length; j++) {
                            int tmp = m[i][j];
                            m[i][j] = m[j][i];
                            m[j][i] = tmp;
                        }
                    }
                }""",
                "A non-square matrix needs a new array with swapped dimensions. A square matrix can be done in place by swapping above-diagonal elements with their mirror.",
                """
                new int[cols][rows] ==> The new matrix has the dimensions swapped.
                t[j][i] = m[i][j]; ==> The defining rule of a transpose.
                for (int j = i + 1; ...) ==> Only visit the upper triangle; visiting both halves would swap everything twice and undo it.
                tmp swap ==> Exchange the mirrored pair.""",
                "Building block for 'rotate image' (transpose + reverse each row). Know the in-place square case.",
                "O(rows x cols)", "O(rows x cols) / O(1)", "Swapping the whole matrix twice; using the wrong result dimensions.");

        full(l, "Sum of Matrix Diagonals", "Matrix", E, "matrix,loops",
                "For a square matrix return the sum of the main diagonal plus the anti-diagonal, counting the centre cell once.",
                "Signal: the main diagonal is [i][i], the anti-diagonal is [i][n-1-i].",
                """
                static int diagonalSum(int[][] m) {
                    int sum = 0;
                    for (int i = 0; i < m.length; i++)
                        for (int j = 0; j < m.length; j++)
                            if (i == j || i + j == m.length - 1) sum += m[i][j];
                    return sum;
                }""",
                """
                static int diagonalSum(int[][] m) {
                    int n = m.length, sum = 0;
                    for (int i = 0; i < n; i++) {
                        sum += m[i][i] + m[i][n - 1 - i];
                    }
                    if (n % 2 == 1) sum -= m[n / 2][n / 2];   // centre counted twice
                    return sum;
                }""",
                "The nested loop checks every cell. We only need 2n cells, so one loop is enough; the centre of an odd-sized matrix lies on both diagonals and must be subtracted once.",
                """
                m[i][i] ==> Main diagonal: row equals column.
                m[i][n - 1 - i] ==> Anti-diagonal: the column counts down as the row counts up.
                n % 2 == 1 ==> Only odd sizes have a centre cell shared by both diagonals.
                sum -= m[n / 2][n / 2]; ==> Remove the double counted centre.""",
                "Index-pattern thinking. Test with 1x1, 2x2 and 3x3 matrices.",
                "O(n)", "O(1)", "Counting the centre twice.");

        full(l, "Count Negative Numbers in a Sorted Matrix", "Matrix", E, "matrix,staircase,search",
                "Each row and each column of a matrix is sorted in non-increasing order. Count the negative numbers.",
                "Signal: start at the top-right corner and walk a 'staircase'.",
                """
                static int countNegatives(int[][] g) {
                    int count = 0;
                    for (int[] row : g)
                        for (int x : row)
                            if (x < 0) count++;
                    return count;
                }""",
                """
                static int countNegatives(int[][] g) {
                    int rows = g.length, cols = g[0].length;
                    int r = 0, c = cols - 1, count = 0;
                    while (r < rows && c >= 0) {
                        if (g[r][c] < 0) {
                            count += rows - r;     // everything below in this column is negative too
                            c--;
                        } else {
                            r++;
                        }
                    }
                    return count;
                }""",
                "Because rows and columns are sorted, one comparison tells you about a whole column segment. Each step moves left or down, so at most rows + cols steps happen.",
                """
                int r = 0, c = cols - 1 ==> Top-right corner: the smallest in its row, largest in its column.
                g[r][c] < 0 ==> If this is negative then everything below it (same column) is negative as well.
                count += rows - r ==> Add all of those cells in one go.
                c-- ==> Move left to the next column.
                else r++ ==> This value is non-negative, and everything to its left in the row is even larger, so this row has no more negatives to find; move down to the next row.""",
                "The staircase search is also used for 'search a sorted 2D matrix'. Say why you start at the corner.",
                "O(rows + cols)", "O(1)", "Starting at the top-left where no pruning is possible.");

        full(l, "Days Between Two Dates", "Website: Calendar", E, "website,dates,java-time",
                "Return the number of days between two dates (for a booking site's nights calculator). Reject a check-out before check-in.",
                "Signal: use java.time and ChronoUnit instead of manual maths.",
                """
                static long nights(LocalDate in, LocalDate out) {
                    long days = 0;
                    for (LocalDate d = in; d.isBefore(out); d = d.plusDays(1)) days++;
                    return days;
                }""",
                """
                static long nights(LocalDate checkIn, LocalDate checkOut) {
                    if (!checkOut.isAfter(checkIn)) throw new IllegalArgumentException("check-out must be after check-in");
                    return ChronoUnit.DAYS.between(checkIn, checkOut);
                }""",
                "ChronoUnit.DAYS.between does the calendar maths in one call. The guard clause gives a clear error for impossible bookings.",
                """
                !checkOut.isAfter(checkIn) ==> Rejects same-day and reversed dates; a booking needs at least one night.
                ChronoUnit.DAYS.between(a, b) ==> Counts whole days from a up to (not including) b, which equals the number of nights.
                d.plusDays(1) ==> LocalDate is immutable: it returns a new date instead of changing d.""",
                "Show you know LocalDate is immutable and why you use LocalDate (no time zone) for calendar days.",
                "O(1)", "O(1)", "Using the legacy Date or Calendar classes; time zone confusion with LocalDateTime.");

        full(l, "Perfect Number Check", "Math Basics", E, "math,divisors,loops",
                "A perfect number equals the sum of its proper divisors (6 = 1 + 2 + 3). Return true if n is perfect.",
                "Signal: divisors come in pairs, so you only need to loop to sqrt(n).",
                """
                static boolean isPerfect(int n) {
                    if (n < 2) return false;
                    int sum = 0;
                    for (int i = 1; i < n; i++) if (n % i == 0) sum += i;
                    return sum == n;
                }""",
                """
                static boolean isPerfect(int n) {
                    if (n < 2) return false;
                    int sum = 1;                                   // 1 is always a proper divisor
                    for (int i = 2; (long) i * i <= n; i++) {
                        if (n % i == 0) {
                            sum += i;
                            if (i != n / i) sum += n / i;          // the paired divisor
                        }
                    }
                    return sum == n;
                }""",
                "If i divides n then so does n / i, so each loop hit finds two divisors. Checking i != n / i avoids counting a square root twice.",
                """
                int sum = 1; ==> 1 divides everything and is a proper divisor, but n itself is not included.
                (long) i * i <= n ==> Loop only to sqrt(n); the cast avoids overflow of i * i.
                sum += i; ==> The small divisor.
                if (i != n / i) sum += n / i; ==> The large partner divisor, but not twice when n is a perfect square.""",
                "A neat follow-up from 'sum of divisors'. Handle n < 2.",
                "O(sqrt n)", "O(1)", "Including n itself in the sum; double counting square roots.");

        full(l, "Middle of a Linked List", "Linked List", E, "linked-list,slow-fast-pointers",
                "Return the middle node of a singly linked list (the second middle if there are two).",
                "Signal: when a fast pointer moves two steps per slow step, slow is in the middle when fast reaches the end.",
                """
                static ListNode middle(ListNode head) {
                    int length = 0;
                    for (ListNode n = head; n != null; n = n.next) length++;
                    ListNode cur = head;
                    for (int i = 0; i < length / 2; i++) cur = cur.next;
                    return cur;
                }""",
                """
                static ListNode middle(ListNode head) {
                    ListNode slow = head, fast = head;
                    while (fast != null && fast.next != null) {
                        slow = slow.next;
                        fast = fast.next.next;
                    }
                    return slow;
                }""",
                "The two-pass version counts first. The fast and slow pointers find the middle in a single pass because fast travels twice the distance of slow.",
                """
                ListNode slow = head, fast = head; ==> Both start at the beginning.
                fast != null && fast.next != null ==> Stop when fast cannot take another double step; this check order prevents a NullPointerException.
                slow = slow.next; ==> One step.
                fast = fast.next.next; ==> Two steps.
                return slow; ==> When fast is at the end, slow has covered half the list.""",
                "The slow/fast pointer idea is reused for cycle detection and finding the nth node from the end.",
                "O(n)", "O(1)", "Dereferencing fast.next.next when fast.next is null.");

        full(l, "Merge Two Sorted Linked Lists", "Linked List", E, "linked-list,merge,dummy-node",
                "Merge two sorted linked lists into one sorted list by splicing the existing nodes.",
                "Signal: a dummy head node removes the 'which list starts?' special case.",
                """
                static ListNode merge(ListNode a, ListNode b) {
                    if (a == null) return b;
                    if (b == null) return a;
                    if (a.val <= b.val) {
                        a.next = merge(a.next, b);
                        return a;
                    }
                    b.next = merge(a, b.next);
                    return b;
                }""",
                """
                static ListNode merge(ListNode a, ListNode b) {
                    ListNode dummy = new ListNode(0), tail = dummy;
                    while (a != null && b != null) {
                        if (a.val <= b.val) { tail.next = a; a = a.next; }
                        else { tail.next = b; b = b.next; }
                        tail = tail.next;
                    }
                    tail.next = (a != null) ? a : b;
                    return dummy.next;
                }""",
                "The recursive version is short but uses O(n) stack. The iterative version uses a dummy node and a tail pointer to build the result in place.",
                """
                ListNode dummy = new ListNode(0) ==> A fake first node so we never have to special-case the head.
                tail ==> Always points at the last node of the merged list.
                a.val <= b.val ==> Take the smaller head; <= keeps the merge stable.
                tail.next = (a != null) ? a : b; ==> When one list ends, attach the rest of the other (already sorted) list in one step.
                return dummy.next; ==> Skip the fake node.""",
                "The dummy-node pattern appears in many list problems. Mention recursion depth as the weakness of the recursive version.",
                "O(n + m)", "O(1)", "Forgetting to attach the remaining nodes; losing the head.");

        full(l, "Binary Tree Inorder Traversal", "Trees", E, "tree,traversal,stack",
                "Return the values of a binary tree in in-order (left, node, right).",
                "Signal: depth-first traversal; recursion or an explicit stack.",
                """
                static void inorder(TreeNode n, List<Integer> out) {
                    if (n == null) return;
                    inorder(n.left, out);
                    out.add(n.val);
                    inorder(n.right, out);
                }""",
                """
                static List<Integer> inorder(TreeNode root) {
                    List<Integer> out = new ArrayList<>();
                    Deque<TreeNode> stack = new ArrayDeque<>();
                    TreeNode cur = root;
                    while (cur != null || !stack.isEmpty()) {
                        while (cur != null) {
                            stack.push(cur);
                            cur = cur.left;
                        }
                        cur = stack.pop();
                        out.add(cur.val);
                        cur = cur.right;
                    }
                    return out;
                }""",
                "The iterative version simulates the call stack: go as far left as possible pushing nodes, then pop, visit and move to the right child.",
                """
                while (cur != null) { stack.push(cur); cur = cur.left; } ==> Remember every ancestor on the way down the left side; we must return to them later.
                cur = stack.pop(); ==> The most recent left-most node: its left subtree is finished.
                out.add(cur.val); ==> Visit the node between its left and right subtrees.
                cur = cur.right; ==> Now process the right subtree the same way.
                while (cur != null || !stack.isEmpty()) ==> Continue while there is a current node or pending ancestors.""",
                "Be able to write recursive and iterative versions. For a BST, in-order gives sorted output.",
                "O(n)", "O(h)", "Forgetting to move to the right child after visiting.");

        full(l, "Symmetric Tree", "Trees", E, "tree,recursion,mirror",
                "Return true if a binary tree is a mirror image of itself around its centre.",
                "Signal: compare two subtrees in a mirrored way: left.left with right.right and left.right with right.left.",
                """
                static boolean isSymmetric(TreeNode root) {
                    if (root == null) return true;
                    Queue<TreeNode> q = new LinkedList<>();
                    q.add(root.left);
                    q.add(root.right);
                    while (!q.isEmpty()) {
                        TreeNode a = q.poll(), b = q.poll();
                        if (a == null && b == null) continue;
                        if (a == null || b == null || a.val != b.val) return false;
                        q.add(a.left);  q.add(b.right);
                        q.add(a.right); q.add(b.left);
                    }
                    return true;
                }""",
                """
                static boolean isSymmetric(TreeNode root) {
                    return root == null || mirror(root.left, root.right);
                }
                static boolean mirror(TreeNode a, TreeNode b) {
                    if (a == null && b == null) return true;
                    if (a == null || b == null || a.val != b.val) return false;
                    return mirror(a.left, b.right) && mirror(a.right, b.left);
                }""",
                "Two trees are mirrors when the values match and the left of one mirrors the right of the other. The queue version checks the same pairs level by level.",
                """
                a == null && b == null ==> Both empty at the same place: still symmetric.
                a == null || b == null ==> Only one is empty: the shapes differ.
                a.val != b.val ==> Same position, different values: not a mirror.
                mirror(a.left, b.right) && mirror(a.right, b.left) ==> The crossing comparison that defines a mirror.""",
                "A helper with two parameters is the key idea; many tree problems are 'compare two trees'.",
                "O(n)", "O(h)", "Comparing left with left instead of left with right.");

        return l;
    }
}
