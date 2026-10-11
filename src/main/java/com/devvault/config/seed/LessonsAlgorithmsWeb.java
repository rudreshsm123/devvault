package com.devvault.config.seed;

import com.devvault.entity.Lesson;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.LessonFactory.add;

/** Chapter 5 (algorithm patterns), chapter 6 (SQL and databases) and chapter 7 (web fundamentals). */
public final class LessonsAlgorithmsWeb {

    private LessonsAlgorithmsWeb() {
    }

    public static List<Lesson> all() {
        List<Lesson> l = new ArrayList<>();
        String c5 = "Algorithm Patterns";
        String c5Intro = "Hundreds of interview problems are variations of about ten patterns. Learn to recognise the pattern from the wording and the solution almost writes itself. Each lesson shows one pattern and the signal that tells you to use it.";

        add(l, 5, c5, c5Intro, 1, "Binary Search and Its Variations",
                "Binary search is the standard answer whenever data is sorted or the answer lies in a monotonic range. It also hides inside 'minimum speed', 'smallest capacity' and 'first bad version' questions.",
                "Check the middle, discard the half that cannot contain the answer, repeat. The array must be sorted (or the yes/no condition must flip exactly once). Variations: first or last occurrence, insert position, and 'binary search on the answer' where you search a range of possible answers instead of an array.",
                "Java",
                """
                static int lowerBound(int[] a, int target) {
                    int lo = 0, hi = a.length;
                    while (lo < hi) {
                        int mid = lo + (hi - lo) / 2;
                        if (a[mid] < target) lo = mid + 1;
                        else hi = mid;
                    }
                    return lo;
                }

                static int minSpeed(int[] piles, int hours) {
                    int lo = 1, hi = 1_000_000_000;
                    while (lo < hi) {
                        int mid = lo + (hi - lo) / 2;
                        long needed = 0;
                        for (int p : piles) needed += (p + mid - 1) / mid;
                        if (needed <= hours) hi = mid; else lo = mid + 1;
                    }
                    return lo;
                }""",
                """
                int lo = 0, hi = a.length; ==> hi is one past the last index so the result can be 'insert at the very end'. The search range is [lo, hi).
                while (lo < hi) ==> Stop when the range is empty. Using < with hi = mid avoids infinite loops.
                int mid = lo + (hi - lo) / 2; ==> The overflow-safe midpoint.
                if (a[mid] < target) lo = mid + 1; ==> mid is too small, so the answer must be to its right.
                else hi = mid; ==> mid might be the answer (equal) or too big, so keep it and shrink from the right. Result: the first index with value >= target.
                needed += (p + mid - 1) / mid; ==> Ceiling division: hours needed for one pile at speed mid.
                if (needed <= hours) hi = mid; ==> Speed is fast enough, so try slower; otherwise we must go faster. 'Binary search on the answer'.""",
                "Say the invariant out loud ('the answer is always inside [lo, hi)'). Interviewers watch for infinite loops and off-by-one errors, so test with 0, 1 and 2 element arrays. Recognise the pattern: 'minimum X such that condition holds' means binary search.",
                "Using (lo + hi) / 2 with huge numbers; mixing the < and <= loop styles; searching an unsorted array.");

        add(l, 5, c5, null, 2, "Two Pointers",
                "Two pointers turns many O(n^2) array and string problems into O(n) with no extra memory. It is the most common pattern in beginner and intermediate rounds.",
                "Place two indexes in the array and move them according to a rule. Opposite ends: start at both ends of a sorted array and move inward (pair sum, palindrome, container with most water). Same direction: a slow pointer writes and a fast pointer reads (remove duplicates, move zeroes).",
                "Java",
                """
                static boolean pairWithSum(int[] sorted, int target) {
                    int left = 0, right = sorted.length - 1;
                    while (left < right) {
                        int sum = sorted[left] + sorted[right];
                        if (sum == target) return true;
                        if (sum < target) left++;
                        else right--;
                    }
                    return false;
                }

                static int removeDuplicates(int[] a) {
                    int write = 0;
                    for (int read = 0; read < a.length; read++) {
                        if (read == 0 || a[read] != a[read - 1]) {
                            a[write++] = a[read];
                        }
                    }
                    return write;
                }""",
                """
                int left = 0, right = sorted.length - 1; ==> One pointer at each end of a SORTED array. Sorting is what makes the next decision possible.
                int sum = sorted[left] + sorted[right]; ==> The current candidate pair.
                if (sum < target) left++; ==> The sum is too small; the only way to grow it is to move the smaller number to a bigger one.
                else right--; ==> The sum is too big; shrink it by moving the larger number down. Each step discards a whole row of pairs, so one pass is enough.
                int write = 0; ==> The slow pointer marks where the next unique value must go.
                a[read] != a[read - 1] ==> In a sorted array duplicates are neighbours, so a change means a new value.
                a[write++] = a[read]; ==> Copy it to the front and advance the writer. The array is modified in place with O(1) extra memory.""",
                "Signal words: 'sorted array', 'pair', 'in place', 'palindrome'. State why it is correct (each move discards impossible pairs) and compare to the hash-map alternative: the hash map needs O(n) memory, two pointers needs none but requires sorted input.",
                "Using two pointers on unsorted data; moving the wrong pointer; infinite loops because a pointer never moves.");

        add(l, 5, c5, null, 3, "Sliding Window",
                "Sliding window solves 'longest or shortest subarray/substring with a property' in O(n). It shows up in log analysis, rate limiting and string problems.",
                "Keep a window [left, right]. Expand right to include more, and when the window breaks a rule, move left until it is valid again. Each index enters and leaves the window once, so the total work is O(n) even though there is a loop inside the loop.",
                "Java",
                """
                static int longestUnique(String s) {
                    Map<Character, Integer> last = new HashMap<>();
                    int best = 0, left = 0;
                    for (int right = 0; right < s.length(); right++) {
                        char c = s.charAt(right);
                        if (last.containsKey(c) && last.get(c) >= left) {
                            left = last.get(c) + 1;
                        }
                        last.put(c, right);
                        best = Math.max(best, right - left + 1);
                    }
                    return best;
                }

                static int maxSumOfK(int[] a, int k) {
                    int sum = 0, best = Integer.MIN_VALUE;
                    for (int i = 0; i < a.length; i++) {
                        sum += a[i];
                        if (i >= k) sum -= a[i - k];
                        if (i >= k - 1) best = Math.max(best, sum);
                    }
                    return best;
                }""",
                """
                Map<Character, Integer> last ==> Remembers the most recent index of each character so we can jump the window past a repeat.
                for (int right = 0; ...) ==> right only moves forward: it is the end of the window.
                last.get(c) >= left ==> The earlier copy only matters if it is inside the current window.
                left = last.get(c) + 1; ==> Shrink the window to just after the previous copy so all characters are unique again.
                best = Math.max(best, right - left + 1); ==> Window length is right - left + 1 (both ends included).
                sum += a[i]; if (i >= k) sum -= a[i - k]; ==> Fixed-size window: add the new element and subtract the one that just left, instead of re-adding k numbers every time.
                i >= k - 1 ==> The first full window of size k is complete at index k-1.""",
                "Signal words: 'longest/shortest substring', 'subarray of size k', 'at most k distinct'. Explain why it is O(n) (each element enters and leaves once). Know the difference between fixed and variable windows.",
                "Resetting the window instead of sliding it; off-by-one in the window length; using it when negative numbers break monotonicity (use prefix sums instead).");

        add(l, 5, c5, null, 4, "Recursion and Backtracking",
                "Recursion is needed for trees, graphs and every 'generate all possibilities' problem (subsets, permutations, N-Queens). Backtracking is recursion with an undo step.",
                "A recursive function solves a problem by calling itself on a smaller version, and it must have a base case. Backtracking builds an answer step by step: choose an option, explore deeper, then undo the choice (backtrack) and try the next option. Always prune options that cannot work.",
                "Java",
                """
                static List<List<Integer>> subsets(int[] nums) {
                    List<List<Integer>> result = new ArrayList<>();
                    backtrack(nums, 0, new ArrayList<>(), result);
                    return result;
                }

                static void backtrack(int[] nums, int start, List<Integer> current,
                                      List<List<Integer>> result) {
                    result.add(new ArrayList<>(current));
                    for (int i = start; i < nums.length; i++) {
                        current.add(nums[i]);
                        backtrack(nums, i + 1, current, result);
                        current.remove(current.size() - 1);
                    }
                }""",
                """
                result.add(new ArrayList<>(current)); ==> Every partial path is itself a valid subset. We COPY the list, otherwise all stored answers would share one list that keeps changing.
                for (int i = start; ...) ==> Only consider elements after the last chosen one, which prevents picking {1,2} and {2,1} as two different subsets.
                current.add(nums[i]); ==> CHOOSE: put the element into the current partial answer.
                backtrack(nums, i + 1, current, result); ==> EXPLORE: solve the smaller problem with the remaining elements.
                current.remove(current.size() - 1); ==> UNDO: take the element back out so the next loop iteration starts from a clean state. This is the 'backtrack'.""",
                "Memorise the choose-explore-undo template. Be ready to state complexity (subsets: O(2^n), permutations: O(n!)) and to add pruning, as in N-Queens or Sudoku. Also be able to say when recursion should become iteration to avoid stack overflow.",
                "Forgetting to copy the current list; forgetting the undo step; duplicates in the input without sorting and skipping.");

        add(l, 5, c5, null, 5, "Dynamic Programming",
                "Dynamic programming (DP) is the topic most candidates fear and interviewers use to separate levels. The good news: it is just recursion that remembers its answers.",
                "Use DP when a problem asks for a count, minimum, maximum or yes/no and the same sub-problems repeat. Steps: (1) define the state in words (dp[i] = best answer for the first i items), (2) write the transition (how dp[i] comes from smaller states), (3) set base cases, (4) decide the order, (5) optimise memory.",
                "Java",
                """
                static int coinChange(int[] coins, int amount) {
                    int[] dp = new int[amount + 1];
                    Arrays.fill(dp, amount + 1);
                    dp[0] = 0;
                    for (int a = 1; a <= amount; a++) {
                        for (int coin : coins) {
                            if (coin <= a) {
                                dp[a] = Math.min(dp[a], dp[a - coin] + 1);
                            }
                        }
                    }
                    return dp[amount] > amount ? -1 : dp[amount];
                }

                static int climbStairs(int n) {
                    int a = 1, b = 1;
                    for (int i = 2; i <= n; i++) {
                        int c = a + b;
                        a = b;
                        b = c;
                    }
                    return b;
                }""",
                """
                int[] dp = new int[amount + 1]; ==> dp[a] means 'the fewest coins needed to make amount a'. Defining the state in a sentence is the most important DP step.
                Arrays.fill(dp, amount + 1); ==> A value larger than any real answer works as 'impossible' (infinity) without overflow risk.
                dp[0] = 0; ==> Base case: making amount 0 needs zero coins. Everything else is built from this.
                for (int a = 1; a <= amount; a++) ==> Compute smaller amounts first so the sub-answers we need already exist.
                dp[a] = Math.min(dp[a], dp[a - coin] + 1); ==> The transition: use one coin of this value plus the best way to make the remainder; keep the minimum over all coins.
                return dp[amount] > amount ? -1 : ... ==> If the value is still 'infinity' the amount cannot be formed.
                int a = 1, b = 1; ... c = a + b ==> Stairs: ways(n) = ways(n-1) + ways(n-2). Only the last two values are needed, so memory drops from O(n) to O(1).""",
                "Always begin with the recursive/brute-force idea, find the overlapping sub-problems, then add memoization, then convert to a table. Explain the state clearly: interviewers care more about your definition and transition than the code. Also know why greedy fails for coins {1,3,4}.",
                "Not defining the state; wrong base case; iterating in an order where needed values are not ready; confusing 'subsequence' and 'substring'.");

        add(l, 5, c5, null, 6, "Sorting Algorithms: Merge Sort and Quick Sort",
                "You rarely write a sort in a real job, but interviewers use sorting to test recursion, divide and conquer and complexity reasoning.",
                "Merge sort splits the array in half, sorts each half, then merges the two sorted halves: always O(n log n), needs O(n) extra space, and is stable. Quick sort picks a pivot and partitions smaller and larger values around it: O(n log n) on average, O(n^2) in the worst case, in place. Java sorts primitives with dual-pivot quicksort and objects with TimSort.",
                "Java",
                """
                static void mergeSort(int[] a, int lo, int hi) {
                    if (hi - lo < 1) return;
                    int mid = (lo + hi) >>> 1;
                    mergeSort(a, lo, mid);
                    mergeSort(a, mid + 1, hi);
                    merge(a, lo, mid, hi);
                }

                static void merge(int[] a, int lo, int mid, int hi) {
                    int[] tmp = new int[hi - lo + 1];
                    int i = lo, j = mid + 1, k = 0;
                    while (i <= mid && j <= hi) tmp[k++] = a[i] <= a[j] ? a[i++] : a[j++];
                    while (i <= mid) tmp[k++] = a[i++];
                    while (j <= hi) tmp[k++] = a[j++];
                    System.arraycopy(tmp, 0, a, lo, tmp.length);
                }""",
                """
                if (hi - lo < 1) return; ==> Base case: zero or one element is already sorted.
                mergeSort(a, lo, mid); mergeSort(a, mid + 1, hi); ==> Divide: sort each half independently. The recursion is about log n levels deep.
                int[] tmp = new int[hi - lo + 1]; ==> The temporary space merge needs, which is why merge sort costs O(n) memory.
                a[i] <= a[j] ? a[i++] : a[j++] ==> Always take the smaller front element. Using <= (not <) keeps equal elements in original order, making the sort stable.
                while (i <= mid) tmp[k++] = a[i++]; ==> One half ran out, so copy the leftovers of the other half; they are already sorted.
                System.arraycopy(tmp, 0, a, lo, tmp.length); ==> Write the merged result back into the original array.""",
                "Be able to compare sorts in a table: time, space, stable or not. Typical follow-ups: why is quicksort usually faster in practice (cache friendly, no extra array), how do you avoid the worst case (random pivot), and what is a stable sort and why does it matter?",
                "Off-by-one with mid + 1; allocating a new array at every level without need; forgetting that quicksort's worst case is sorted input with a bad pivot.");

        String c6 = "SQL and Databases";
        String c6Intro = "Nearly every real application stores data in a relational database, and SQL questions appear in almost every backend interview. You will learn to read, join, aggregate and protect data.";

        add(l, 6, c6, c6Intro, 1, "Tables, SELECT and WHERE",
                "SQL is the language of data. Even if you use JPA or an ORM, you must know what query it generates, why it is slow, and how to write a report by hand.",
                "A table has columns (fields) and rows (records). A primary key identifies each row. SELECT chooses columns, FROM chooses the table, WHERE filters rows, ORDER BY sorts, LIMIT restricts the count. SQL is declarative: you describe what you want, the database decides how to get it.",
                "SQL",
                """
                CREATE TABLE coding_problem (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    title VARCHAR(255) NOT NULL,
                    topic VARCHAR(100),
                    difficulty VARCHAR(10),
                    created_at DATETIME
                );

                INSERT INTO coding_problem (title, topic, difficulty)
                VALUES ('Two Sum', 'Hash Map', 'EASY');

                SELECT title, difficulty
                FROM coding_problem
                WHERE topic = 'Hash Map' AND difficulty <> 'HARD'
                ORDER BY title
                LIMIT 10;""",
                """
                id BIGINT PRIMARY KEY AUTO_INCREMENT ==> A unique identifier for each row; AUTO_INCREMENT makes the database generate it. JPA's GenerationType.IDENTITY relies on exactly this.
                title VARCHAR(255) NOT NULL ==> A text column with a maximum length; NOT NULL means a value is mandatory (the same rule as @NotBlank in Java).
                INSERT INTO ... VALUES (...) ==> Adds a row. Listing the columns explicitly keeps the statement safe when columns are added later.
                SELECT title, difficulty ==> Choose only the columns you need; SELECT * wastes bandwidth and breaks when the table changes.
                WHERE topic = 'Hash Map' AND difficulty <> 'HARD' ==> Row filter evaluated BEFORE sorting. <> means 'not equal'.
                ORDER BY title LIMIT 10 ==> Sort, then keep only the first 10 rows. Without ORDER BY the row order is not guaranteed.""",
                "Be ready to write queries on a whiteboard: filtering, sorting, limiting, NULL handling (use IS NULL, not = NULL) and LIKE patterns. Know what a primary key and a foreign key are.",
                "Using = NULL; SELECT * in production code; concatenating user input into SQL strings (SQL injection).");

        add(l, 6, c6, null, 2, "JOINs: Combining Tables",
                "Real data is split across tables to avoid duplication (customers, orders, items). JOIN questions are the most common SQL interview topic.",
                "A foreign key links a row to a row in another table. INNER JOIN returns only matching rows. LEFT JOIN returns all rows from the left table, with NULLs when there is no match (great for 'customers with no orders'). Join on the key columns, and alias tables to keep queries short.",
                "SQL",
                """
                SELECT c.name, o.id AS order_id, o.total
                FROM customers c
                INNER JOIN orders o ON o.customer_id = c.id
                WHERE o.total > 500;

                SELECT c.name
                FROM customers c
                LEFT JOIN orders o ON o.customer_id = c.id
                WHERE o.id IS NULL;""",
                """
                FROM customers c ==> 'c' is an alias, a short name used in the rest of the query.
                INNER JOIN orders o ON o.customer_id = c.id ==> Pair every order with its customer using the foreign key. Customers with no orders disappear from the result.
                o.id AS order_id ==> Renames a column in the output so two 'id' columns are not confused.
                LEFT JOIN orders o ON ... ==> Keep EVERY customer; where no order matches, the o.* columns are NULL.
                WHERE o.id IS NULL ==> Filters to the unmatched rows only: customers who never ordered. This is the classic anti-join.""",
                "Practise: INNER vs LEFT vs RIGHT vs FULL, anti-join, self join (employee and manager), joining three tables. Explain what happens with duplicates in a one-to-many join and why joins need indexes on the key columns.",
                "Joining without an ON condition (cartesian product); putting a LEFT JOIN filter in WHERE which turns it into an inner join; forgetting that a one-to-many join multiplies rows.");

        add(l, 6, c6, null, 3, "GROUP BY, HAVING and Aggregates",
                "Reports, dashboards and analytics all use aggregation: totals per customer, orders per day, the top 5 products.",
                "Aggregate functions (COUNT, SUM, AVG, MIN, MAX) collapse many rows into one value. GROUP BY makes one result row per group. WHERE filters rows before grouping; HAVING filters groups after aggregation. The order of evaluation is FROM, WHERE, GROUP BY, HAVING, SELECT, ORDER BY, LIMIT.",
                "SQL",
                """
                SELECT topic,
                       COUNT(*) AS problems,
                       AVG(CASE difficulty WHEN 'EASY' THEN 1
                                           WHEN 'MEDIUM' THEN 2 ELSE 3 END) AS avg_level
                FROM coding_problem
                WHERE topic IS NOT NULL
                GROUP BY topic
                HAVING COUNT(*) >= 5
                ORDER BY problems DESC;""",
                """
                COUNT(*) AS problems ==> Counts the rows in each group.
                CASE difficulty WHEN 'EASY' THEN 1 ... END ==> Converts text labels to numbers so they can be averaged. CASE is SQL's if/else.
                WHERE topic IS NOT NULL ==> Filters individual ROWS first, so rows without a topic never enter a group.
                GROUP BY topic ==> One output row per topic. Every selected column must be grouped or aggregated.
                HAVING COUNT(*) >= 5 ==> Filters GROUPS after counting; you cannot use WHERE here because the count does not exist yet.
                ORDER BY problems DESC ==> Sorting can use the alias because ORDER BY runs after SELECT.""",
                "Classic questions: second highest salary, duplicate emails, top N per group (window functions), customers who ordered every month. Know WHERE vs HAVING and the logical order of execution cold.",
                "Selecting a non-grouped column; using WHERE to filter an aggregate; COUNT(column) ignoring NULLs while COUNT(*) does not.");

        add(l, 6, c6, null, 4, "Indexes and Query Performance",
                "A query that takes 5 ms on test data can take 5 seconds with a million rows. Understanding indexes is what turns a SQL user into a backend engineer.",
                "An index is a sorted structure (usually a B-tree) on one or more columns. It lets the database jump straight to matching rows instead of scanning the whole table (a full table scan, O(n)). Costs: extra storage and slower writes. Composite indexes follow the leftmost-prefix rule. EXPLAIN shows whether an index is used.",
                "SQL",
                """
                CREATE INDEX idx_problem_topic ON coding_problem (topic);
                CREATE INDEX idx_problem_topic_diff ON coding_problem (topic, difficulty);

                EXPLAIN SELECT * FROM coding_problem
                WHERE topic = 'Graphs' AND difficulty = 'HARD';

                -- cannot use the index:
                SELECT * FROM coding_problem WHERE LOWER(topic) = 'graphs';
                SELECT * FROM coding_problem WHERE title LIKE '%sum';""",
                """
                CREATE INDEX idx_problem_topic ON coding_problem (topic) ==> Builds a sorted lookup structure for the topic column. Lookups drop from O(n) to O(log n).
                (topic, difficulty) ==> A composite index serves queries that filter on topic alone or on topic AND difficulty, but not on difficulty alone (leftmost-prefix rule).
                EXPLAIN SELECT ... ==> Does not run the query; it shows the plan. Look for 'type: ref' or 'range' (good) versus 'ALL' (full scan).
                WHERE LOWER(topic) = 'graphs' ==> Wrapping the column in a function hides it from the index. Store normalised data or add a functional index.
                LIKE '%sum' ==> A leading wildcard cannot use a B-tree index because the start of the value is unknown; 'sum%' can.""",
                "Expect: 'Why not index every column?', 'What is a covering index?', 'How do you find a slow query?', 'Explain the N+1 query problem in JPA'. Mention measuring with EXPLAIN before and after.",
                "Indexing low-selectivity columns (e.g. a boolean); functions on indexed columns; forgetting that every index slows INSERT and UPDATE.");

        add(l, 6, c6, null, 5, "Transactions and ACID",
                "Money transfers, stock reservations and bookings must never be half-done. Transactions are the reason databases can be trusted, and they are central to senior backend interviews.",
                "A transaction groups statements so they all succeed (COMMIT) or all fail (ROLLBACK). ACID: Atomicity (all or nothing), Consistency (rules hold), Isolation (concurrent transactions do not see each other's half-finished work), Durability (committed data survives a crash). In Spring, @Transactional on a service method gives you this.",
                "SQL",
                """
                START TRANSACTION;

                UPDATE account SET balance = balance - 500 WHERE id = 1 AND balance >= 500;
                UPDATE account SET balance = balance + 500 WHERE id = 2;

                COMMIT;

                -- Spring equivalent:
                -- @Transactional
                -- public void transfer(long from, long to, long amount) { ... }""",
                """
                START TRANSACTION; ==> Everything after this is tentative until COMMIT.
                SET balance = balance - 500 ==> Let the database do the arithmetic atomically instead of reading the balance into Java and writing it back (which races).
                AND balance >= 500 ==> The overdraft check lives inside the same statement, so two concurrent withdrawals cannot both pass.
                UPDATE account ... id = 2 ==> The second half of the transfer. If this fails and we ROLLBACK, the first update is undone too: atomicity.
                COMMIT; ==> Makes both changes permanent and visible to others at once.
                @Transactional ==> Spring opens a transaction before the method and commits when it returns normally, rolling back on an unchecked exception.""",
                "Be ready to name the four ACID properties with an example, explain isolation levels (read committed, repeatable read, serializable) and phenomena (dirty read, non-repeatable read, phantom read), and describe optimistic (version column) versus pessimistic (SELECT ... FOR UPDATE) locking.",
                "Doing read-modify-write in application code without locking; @Transactional on a private method (ignored by Spring); catching the exception inside the transaction so it never rolls back.");

        String c7 = "Web Fundamentals";
        String c7Intro = "Every backend developer must understand how the browser and server talk. Learn HTTP, the three front-end languages and what a REST API is, so you can build and explain a complete website.";

        add(l, 7, c7, c7Intro, 1, "How the Web Works: HTTP Request and Response",
                "'What happens when you type a URL and press Enter?' is the most asked system question. It ties networking, servers and browsers together.",
                "The browser resolves the domain to an IP address through DNS, opens a TCP connection (and TLS for https), sends an HTTP request (method, path, headers, optional body) and receives a response (status code, headers, body). Methods: GET reads, POST creates, PUT replaces, PATCH changes part, DELETE removes. Status codes: 2xx success, 3xx redirect, 4xx client error, 5xx server error.",
                "HTTP",
                """
                POST /api/problems HTTP/1.1
                Host: devvault.example.com
                Content-Type: application/json
                Accept: application/json

                {"title": "Two Sum", "difficulty": "EASY"}

                HTTP/1.1 201 Created
                Location: /api/problems/42
                Content-Type: application/json

                {"id": 42, "title": "Two Sum"}""",
                """
                POST /api/problems HTTP/1.1 ==> The request line: the method (create something), the path and the protocol version.
                Host: devvault.example.com ==> Tells the server which site is wanted; many sites share one IP address.
                Content-Type: application/json ==> Describes the format of the body we are sending, so the server knows how to parse it.
                Accept: application/json ==> Says which format we want back.
                {"title": ...} ==> The body: the data of the new problem.
                HTTP/1.1 201 Created ==> 201 means a new resource was created (200 would just mean OK).
                Location: /api/problems/42 ==> The address of the new resource, so the client can fetch it later.""",
                "Know the difference between GET and POST, PUT and PATCH, idempotent versus safe methods, common status codes (200, 201, 204, 301, 400, 401, 403, 404, 409, 500) and what cookies, sessions and HTTPS do. Draw the DNS, TCP, TLS, HTTP flow.",
                "Using GET for actions that change data; returning 200 with an error message in the body; confusing 401 (not logged in) and 403 (not allowed).");

        add(l, 7, c7, null, 2, "HTML: Structure and Forms",
                "HTML is the skeleton of every web page. Semantic HTML helps accessibility and search engines, and forms are how users send data to your backend.",
                "HTML uses tags to describe content: headings, paragraphs, lists, links, images and forms. Semantic tags (header, nav, main, section, footer) describe the purpose of each part. A form sends fields to a URL using GET or POST when submitted. Labels linked to inputs make forms usable with a keyboard and screen readers.",
                "HTML",
                """
                <main>
                  <h1>Add a problem</h1>
                  <form action="/problems" method="post">
                    <label for="title">Title</label>
                    <input id="title" name="title" type="text" required maxlength="255">

                    <label for="level">Difficulty</label>
                    <select id="level" name="difficulty">
                      <option value="EASY">Easy</option>
                      <option value="HARD">Hard</option>
                    </select>

                    <button type="submit">Save</button>
                  </form>
                </main>""",
                """
                <main> ==> A semantic tag that marks the main content of the page; there should be only one. Better for accessibility than a generic div.
                <form action="/problems" method="post"> ==> When submitted, the browser sends the fields to /problems using POST. This is what the Spring controller receives.
                <label for="title"> ==> Linked to the input with the same id. Clicking the label focuses the field, and screen readers announce it.
                name="title" ==> The key the server receives. Without a name the field is not sent at all.
                required maxlength="255" ==> Free browser validation. Never trust it alone: the server must validate again.
                <select> / <option value="EASY"> ==> The value sent is EASY; the visible text is Easy.
                <button type="submit"> ==> Triggers the form submission.""",
                "Interviewers ask about semantic HTML, accessibility basics (labels, alt text, keyboard focus), the difference between GET and POST forms, and why server-side validation is mandatory even when the browser validates.",
                "Forgetting the name attribute; using div for everything; missing alt text on images; trusting client-side validation.");

        add(l, 7, c7, null, 3, "CSS: Layout with Flexbox and Grid",
                "A site that looks professional gets attention before the code is ever read. Flexbox and Grid solve nearly every layout problem without hacks.",
                "CSS styles HTML through selectors. The box model: content, padding, border, margin. Flexbox lays items out in one dimension (a row or column); Grid in two. Media queries change the layout for different screen widths. CSS variables (custom properties) keep colours in one place.",
                "CSS",
                """
                :root {
                  --brand: #2563eb;
                }

                .cards {
                  display: grid;
                  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
                  gap: 16px;
                }

                .card {
                  background: white;
                  border-radius: 12px;
                  padding: 20px;
                  border-top: 4px solid var(--brand);
                }

                @media (max-width: 600px) {
                  .nav { flex-direction: column; }
                }""",
                """
                :root { --brand: #2563eb; } ==> Declares a variable available everywhere. Changing the brand colour later means editing one line.
                display: grid; ==> Turns the container into a grid; its children become grid items.
                repeat(auto-fit, minmax(240px, 1fr)) ==> As many columns as fit, each at least 240px wide, sharing leftover space equally. Responsive without a media query.
                gap: 16px; ==> Space between items, cleaner than margins on every child.
                padding: 20px; border-radius: 12px; ==> Space inside the box and rounded corners: the box model in action.
                var(--brand) ==> Reads the variable.
                @media (max-width: 600px) ==> Rules that only apply on narrow screens, such as phones.""",
                "Be ready to explain the box model, flexbox vs grid, specificity (why one rule beats another), position values, and a mobile-first approach. Recruiters notice visual polish and responsiveness in portfolio projects.",
                "Fixed pixel widths that break on phones; overusing !important; forgetting the viewport meta tag.");

        add(l, 7, c7, null, 4, "JavaScript and the Fetch API",
                "JavaScript makes pages interactive and is how the browser talks to your REST API without reloading the page.",
                "JavaScript runs in the browser. You select elements, react to events (click, input) and update the page (the DOM). fetch sends an HTTP request and returns a Promise; async/await lets you write asynchronous code in a readable top-to-bottom way. Always check response.ok because fetch does not throw on 404 or 500.",
                "JavaScript",
                """
                const form = document.querySelector('#search');
                const list = document.querySelector('#results');

                form.addEventListener('submit', async (event) => {
                  event.preventDefault();
                  const q = new FormData(form).get('q');

                  const res = await fetch('/api/problems?keyword=' + encodeURIComponent(q));
                  if (!res.ok) {
                    list.textContent = 'Something went wrong';
                    return;
                  }
                  const problems = await res.json();
                  list.innerHTML = '';
                  for (const p of problems) {
                    const li = document.createElement('li');
                    li.textContent = p.title;
                    list.appendChild(li);
                  }
                });""",
                """
                document.querySelector('#search') ==> Finds the first element matching the CSS selector. The result is stored in a const, which cannot be reassigned.
                addEventListener('submit', async (event) => {...}) ==> Runs our function whenever the form is submitted. async allows await inside.
                event.preventDefault(); ==> Stops the browser's normal full-page submit so we can handle it ourselves.
                encodeURIComponent(q) ==> Escapes special characters so the user's text cannot break or inject into the URL.
                await fetch(...) ==> Pauses this function (not the page) until the server answers.
                if (!res.ok) ==> fetch only fails on network errors; a 404 or 500 still resolves, so we must check the status ourselves.
                li.textContent = p.title; ==> Inserts text safely. Using innerHTML with user data would allow cross-site scripting (XSS).""",
                "Common questions: var/let/const, closures, the event loop, promises vs async/await, this keyword, == vs ===, and XSS prevention. For a Java role, show you can call your own REST API from the front end.",
                "Forgetting await; not handling errors; building HTML with innerHTML from user data; confusing = and ===.");

        add(l, 7, c7, null, 5, "Designing a REST API",
                "A clean API is a major sign of experience. Reviewers look at URL naming, status codes and error handling in take-home projects.",
                "REST models things (resources) as nouns in the URL and uses HTTP methods as the verbs. Use plural nouns: /problems, /problems/42. Return proper status codes, a consistent JSON error shape, and support filtering, sorting and pagination through query parameters. Keep the API stateless: each request carries everything the server needs.",
                "HTTP",
                """
                GET    /api/problems?topic=Graphs&difficulty=HARD&page=0&size=20
                GET    /api/problems/42
                POST   /api/problems            -> 201 Created + Location header
                PUT    /api/problems/42         -> 200 OK (replace)
                DELETE /api/problems/42         -> 204 No Content

                400 Bad Request
                {
                  "timestamp": "2026-10-11T10:15:30",
                  "status": 400,
                  "errors": { "title": "Title is required" }
                }""",
                """
                GET /api/problems?topic=Graphs&... ==> Reading a collection. Filters and paging belong in the query string, never in the path.
                GET /api/problems/42 ==> A single resource identified by its id; plural noun, no verbs like /getProblem.
                POST ... -> 201 Created ==> Creating returns 201 and the address of the new resource.
                PUT ... -> 200 OK ==> Replaces the whole resource. PATCH would change only some fields.
                DELETE ... -> 204 No Content ==> Success with nothing to return.
                400 Bad Request { errors: {...} } ==> Invalid input is the client's fault (4xx). The errors map names each failing field, so the front end can show the message beside it. This is the exact shape DevVault's GlobalExceptionHandler returns.""",
                "Be ready to discuss idempotency, versioning (/v1), pagination, authentication (JWT or sessions), rate limiting, HATEOAS (rarely required) and when to choose GraphQL or gRPC. Showing a consistent error format in your own project is a strong signal.",
                "Verbs in URLs; always returning 200; leaking stack traces to clients; no pagination on large lists.");

        return l;
    }
}
