package com.devvault.config.seed;

import java.util.HashMap;
import java.util.Map;

/**
 * Line-by-line explanations and interviewer guidance for the first batch of hard problems, which were
 * seeded before those two fields existed. ContentSeeder applies them to problems that still lack them.
 */
public final class HardProblemExplanations {

    /** lineByLine (one "fragment ==> reason" per line) and interviewTips for one problem. */
    public record Explanation(String lineByLine, String interviewTips) {
    }

    private HardProblemExplanations() {
    }

    private static void e(Map<String, Explanation> m, String title, String lines, String tips) {
        m.put(title, new Explanation(lines.strip(), tips.strip()));
    }

    public static Map<String, Explanation> all() {
        Map<String, Explanation> m = new HashMap<>();

        e(m, "Edit Distance (Spell Checker)",
                """
                dp[i][0] = i; dp[0][j] = j ==> Turning a prefix into the empty string costs i deletions, and building a prefix from nothing costs j insertions.
                a.charAt(i - 1) == b.charAt(j - 1) ==> The last characters already match, so the cost equals the cost of the shorter prefixes: dp[i - 1][j - 1].
                1 + Math.min(dp[i - 1][j - 1], ...) ==> Otherwise pay one operation and pick the cheapest of replace (diagonal), delete (up) or insert (left).
                dp[a.length()][b.length()] ==> The cost for the two complete strings.""",
                "Start with the recursive definition, then explain the three moves (insert, delete, replace) as the three neighbouring cells. Mention the space optimisation to two rows and the link to diff tools and 'did you mean' search.");

        e(m, "Regular Expression Matching (Route Patterns)",
                """
                dp[0][0] = true ==> An empty pattern matches an empty string.
                pattern '*' with dp[0][j - 2] ==> A star can repeat its previous element zero times, so patterns like a*b* can match the empty string.
                dp[i][j] = dp[i][j - 2] ==> Zero copies: skip the 'x*' pair entirely.
                prev == '.' || prev == s.charAt(i - 1) ==> The element before the star matches the current character, so one more copy is allowed.
                dp[i][j] |= dp[i - 1][j] ==> Consume one character of s and stay on the same star.
                pc == '.' || pc == s.charAt(i - 1) ==> A normal character or a dot: both strings advance diagonally.""",
                "Say out loud what dp[i][j] means (first i characters of s versus first j of p). The star has two choices - zero copies or one more - and that is the entire difficulty. Mention backtracking as the exponential alternative.");

        e(m, "Wildcard Matching (URL Routes)",
                """
                p.charAt(j) == '?' || equal characters ==> A single-character match: advance both pointers.
                p.charAt(j) == '*' ==> Remember where the star is (star) and where in s it started matching (mark).
                j = star + 1; i = ++mark ==> On a later mismatch, let the star swallow one more character of s and retry the rest of the pattern.
                else return false ==> No star to fall back to, so the mismatch is final.
                while (p.charAt(j) == '*') j++ ==> Trailing stars can match the empty string.""",
                "Show the DP version first, then the O(1)-space greedy with backtracking to the last star. Explain why only the LAST star needs remembering: earlier stars can be resolved greedily.");

        e(m, "Largest Rectangle in Histogram",
                """
                Deque<Integer> stack ==> Holds indices of bars in increasing height order; each waits for the first shorter bar on its right.
                cur < h[stack.peek()] ==> A shorter bar arrives, so the bar on the stack top can no longer extend right: its rectangle is final.
                height = h[stack.pop()] ==> The height of the rectangle being closed.
                left = stack.isEmpty() ? -1 : stack.peek() ==> The nearest shorter bar on the left (or the start).
                height * (i - left - 1) ==> Width runs between the two shorter bars.
                i == h.length with cur = 0 ==> A sentinel zero height flushes every remaining bar.""",
                "Explain that each bar is pushed and popped once (O(n)). The key sentence is: 'for every bar I need the nearest smaller bar on both sides, and a monotonic stack finds both in one pass.'");

        e(m, "Merge k Sorted Lists",
                """
                PriorityQueue<ListNode> heap ==> Holds the current head of each list, ordered by value; the smallest is always on top.
                heap.add(n) for each non-null list ==> Start with one node per list.
                tail.next = n; tail = n ==> Append the smallest node to the result by relinking, with no new nodes allocated.
                if (n.next != null) heap.add(n.next) ==> Refill the heap from the list we just took from.
                dummy ==> A fake head avoids special cases for the first node.""",
                "Give the complexity as O(N log k), not O(N log N). Mention divide and conquer (merge lists in pairs) as an equally good alternative with O(1) extra space.");

        e(m, "Reverse Nodes in k-Group",
                """
                dummy ==> A fake node in front so reversing the first group needs no special case.
                for (i < k && kth != null) kth = kth.next ==> Check that a full group of k nodes exists before touching it.
                groupNext = kth.next ==> The first node after the group; the reversed group must connect to it.
                prev = groupNext ==> Starting prev at groupNext makes the last reversed node point into the rest of the list automatically.
                cur.next = prev; prev = cur; cur = next ==> The standard reversal, run only until cur reaches groupNext.
                groupPrev = oldFirst ==> The old first node is now the group's tail and becomes the anchor for the next group.""",
                "Break it into 'find the group, reverse it, reconnect'. Draw pointers on the board. Leftover nodes (fewer than k) must stay in the original order.");

        e(m, "Word Search II (Trie + Backtracking)",
                """
                Node[] next = new Node[26] ==> A trie node: one child per letter.
                n.word = w ==> Store the complete word at its last node so we can output it directly when we reach it.
                parent.next[ch - 'a'] == null then return ==> Prune: no word in the list has this prefix, so stop exploring.
                out.add(n.word); n.word = null ==> Record a found word and clear it so it is never reported twice.
                b[r][c] = '#' ... b[r][c] = ch ==> Mark the cell used on this path, then restore it when backtracking.""",
                "The point is searching ALL words in one DFS instead of one DFS per word. Mention pruning exhausted trie branches for extra speed.");

        e(m, "Alien Dictionary (Topological Order)",
                """
                adj / indeg maps ==> A graph where an edge a -> b means letter a must come before letter b.
                a.length() > b.length() && a.startsWith(b) ==> A longer word before its own prefix is impossible, so the input is invalid.
                first differing character of adjacent words ==> Only that pair gives ordering information; later characters tell us nothing.
                adj.get(x).add(y) then indeg++ ==> Record each distinct edge once.
                queue of indegree-0 letters ==> Kahn's algorithm: emit letters with no unmet predecessor.
                out.length() == indeg.size() ==> If some letters were never emitted, the rules contain a cycle.""",
                "Name the technique: topological sort on a graph built from adjacent words. The two classic traps are the prefix case and cycles.");

        e(m, "Minimum Window Substring",
                """
                need[c]++ for each char of t ==> How many of each character the window must still supply.
                missing = t.length() ==> Total characters still required.
                need[s.charAt(right)]-- > 0 ==> The window gained a character we still needed, so missing decreases.
                while (missing == 0) ==> The window is valid: try to shrink it from the left.
                ++need[s.charAt(left++)] > 0 ==> Removing a needed character makes the window invalid again.
                bestStart / bestLen ==> The shortest valid window seen.""",
                "Describe it as expand until valid, then shrink while valid. A single array of counts plus one 'missing' counter keeps every step O(1).");

        e(m, "Sudoku Solver",
                """
                b[r][c] != '.' continue ==> Skip filled cells and find the next empty one.
                for (char d = '1'; d <= '9'; d++) ==> Try every digit in the empty cell.
                ok(b, r, c, d) ==> Check the digit is unused in the row, the column and the 3x3 box.
                b[r][c] = d; if (solve(b)) return true ==> Choose and recurse.
                b[r][c] = '.' ==> Undo when the choice leads to a dead end (backtracking).
                return false after the digit loop ==> No digit fits here, so the previous choice was wrong.""",
                "Mention optimisations: bitmasks for rows, columns and boxes and always choosing the cell with the fewest candidates. Explain why the simple version is still fast enough for 9x9.");

        e(m, "Maximal Rectangle",
                """
                heights[c] = row[c] == '1' ? heights[c] + 1 : 0 ==> Each row becomes a histogram: the height of consecutive 1s ending at this row.
                largestInHistogram(heights) ==> Reuse the largest rectangle in a histogram solution for every row.
                best = Math.max(best, ...) ==> The best rectangle may end at any row.""",
                "This is a reduction problem: say 'each row is a histogram' and reuse the stack solution. Complexity O(rows x cols).");

        e(m, "Burst Balloons",
                """
                a[0] = a[n + 1] = 1 ==> Virtual balloons of value 1 at both ends remove edge cases.
                dp[left][right] ==> The maximum coins from bursting everything strictly inside the range.
                for (len = 1..n) ==> Solve short ranges first because longer ones depend on them.
                k = last balloon burst in the range ==> Choosing the LAST one means its neighbours are the fixed borders left and right.
                dp[left][k - 1] + a[left - 1] * a[k] * a[right + 1] + dp[k + 1][right] ==> Left part + the final burst + right part.""",
                "The insight is to think in reverse: which balloon is burst LAST, not first. Say why this makes the two halves independent.");

        e(m, "Find Median from Data Stream (Live Stats)",
                """
                low (max-heap) ==> Holds the smaller half of the numbers; its top is the largest of the small ones.
                high (min-heap) ==> Holds the larger half; its top is the smallest of the large ones.
                low.add(x); high.add(low.poll()) ==> Insert through the low heap so every low value stays <= every high value.
                high.size() > low.size() ==> Rebalance so low is never smaller than high.
                low.peek() / (low.peek() + high.peek()) / 2.0 ==> Odd count: the top of low. Even count: the average of the two tops.""",
                "Explain O(log n) insert and O(1) median. Follow-ups: sliding window median, numbers within a small range (counting buckets).");

        e(m, "Design Twitter",
                """
                tweets: user -> list of {time, id} ==> Each user's own posts in the order they were posted.
                clock++ ==> A global counter gives a strict ordering without clock ties.
                following: user -> set of followees ==> Who a user follows.
                who.add(user) ==> A user's feed also includes their own tweets.
                last 10 tweets of each followee ==> Only the newest 10 of each person can possibly appear in a feed of 10.
                PriorityQueue by time descending ==> Merge those candidates and take the 10 newest.""",
                "Talk about fan-out on write (precompute feeds) versus fan-out on read (merge at request time) and why celebrities need the second. This is the classic news feed discussion.");

        e(m, "LRU Cache with TTL (Production Cache)",
                """
                LinkedHashMap(16, 0.75f, true) ==> accessOrder = true: every get moves an entry to the most recent end, so the first entry is the least recently used.
                Entry(value, expiresAt) ==> Each value carries its own deadline for the TTL.
                map.size() > capacity ==> Over capacity: remove the eldest (least recently used) entry.
                e.expiresAt() <= now ==> Expired entries are removed lazily when read.
                synchronized ==> The map is shared between threads; access order makes even reads mutate it.""",
                "Compare with Caffeine/Guava caches. Mention that expiry only on read leaves cold expired entries in memory, so production caches also run a cleanup or use a timer wheel.");

        e(m, "Search Typeahead System",
                """
                Node { kids, counts } ==> Each trie node stores every sentence that passes through it with its frequency.
                add(s, times) walks the characters ==> Update the counts on every node along the sentence's path.
                cur = cur.kids.get(c) ==> Move one step down the trie as the user types one character.
                sorted by count desc, then alphabetically ==> The ranking rule: most frequent first, ties in dictionary order.
                limit(3) ==> Only the top three suggestions are shown.
                c == '#' ==> The sentence is complete: save it and reset to the root.""",
                "State the trade-off: storing counts at every node is fast to query but uses more memory; real systems keep only the top K per node and update them asynchronously.");

        e(m, "The Skyline Problem",
                """
                events {x, -height} for starts and {x, height} for ends ==> Turn each building into two events. Starts are negative so they sort before ends at the same x.
                sort by x, then by the signed height ==> Process events left to right with starts first.
                TreeMap<Integer, Integer> heights ==> A multiset of the heights of the buildings currently covering x.
                heights.lastKey() ==> The tallest active building, which is the skyline height at this x.
                cur != prev ==> Only a change in the maximum height creates a key point.""",
                "Explain the sweep line idea and why the tie-breaking order matters. Mention the heap with lazy deletion as the other common implementation.");

        e(m, "Cheapest Flights Within K Stops",
                """
                cost[] with infinity ==> The cheapest known price to reach each city.
                for (round = 0..k) ==> Each round allows one more flight, so k stops means k + 1 flights.
                int[] next = cost.clone() ==> Update from the previous round's prices only, so one round never uses two flights.
                cost[f[0]] + f[2] < next[f[1]] ==> Relax the edge if it makes the destination cheaper.
                cost[dst] >= INF ? -1 ==> If the destination was never reached the answer is -1.""",
                "Explain why Dijkstra alone fails (cheapest path may use too many stops) and why Bellman-Ford limited to k + 1 rounds works.");

        e(m, "Network Delay Time (Dijkstra)",
                """
                Map<Integer, List<int[]>> g ==> Adjacency list of {neighbour, travel time}.
                dist[k] = 0 ==> The signal starts at node k.
                PriorityQueue by distance ==> Always expand the closest unprocessed node.
                if (cur[1] > dist[cur[0]]) continue ==> Skip stale queue entries instead of implementing decrease-key.
                nd < dist[e[0]] ==> A shorter path to the neighbour was found: update and enqueue.
                max over dist ==> The time until every node has the signal; any unreachable node means -1.""",
                "State that Dijkstra needs non-negative weights, complexity O(E log V), and why the first time a node is polled its distance is final.");

        e(m, "Critical Connections in a Network",
                """
                disc[u] ==> The time (order) at which node u was first discovered by the DFS.
                low[u] ==> The earliest discovery time reachable from u's subtree using at most one back edge.
                v == parent continue ==> Do not treat the edge we came from as a back edge.
                low[u] = Math.min(low[u], low[v]) ==> A child that can reach an earlier node lets u reach it too.
                low[v] > disc[u] ==> The child's subtree cannot reach u or anything above it except through this edge: it is a bridge.""",
                "This is Tarjan's bridge-finding algorithm. Be ready to explain low-link in one sentence and mention articulation points as the vertex version.");

        e(m, "Palindrome Partitioning II",
                """
                pal[start][end] ==> True if s[start..end] is a palindrome, filled as we go.
                s[start] == s[end] && (end - start < 2 || pal[start + 1][end - 1]) ==> The ends match and the inside is a palindrome (or too short to matter).
                cuts[end] = end ==> Worst case: cut between every pair of characters.
                start == 0 ? 0 : cuts[start - 1] + 1 ==> If the whole prefix is a palindrome no cut is needed; otherwise one cut after the best prefix.
                cuts[n - 1] ==> The answer for the whole string.""",
                "Two DP ideas combined: a palindrome table for O(1) checks and a cuts array. Say the complexity O(n^2) time and space.");

        e(m, "Distinct Subsequences",
                """
                dp[j] ==> The number of ways to build the first j characters of t from the part of s processed so far.
                dp[0] = 1 ==> The empty target can be formed in exactly one way: by choosing nothing.
                for (j = t.length(); j >= 1; j--) ==> Iterate backwards so each character of s is used at most once per target position.
                s.charAt(i - 1) == t.charAt(j - 1) ==> The characters match, so we may use this character of s.
                dp[j] += dp[j - 1] ==> Add the ways that existed without this character to the ways that use it.""",
                "Explain the 2D table first (dp[i][j]) and then the single-array compression. The backwards loop is the detail interviewers check.");

        e(m, "Interleaving String",
                """
                s1.length() + s2.length() != s3.length() ==> Lengths must add up, otherwise no interleaving exists.
                dp[i][j] ==> True if the first i characters of s1 and first j of s2 can form the first i + j of s3.
                dp[i - 1][j] && s1.charAt(i - 1) == s3.charAt(i + j - 1) ==> The last character of s3 came from s1.
                dp[i][j - 1] && s2.charAt(j - 1) == s3.charAt(i + j - 1) ==> Or it came from s2.
                dp[0][0] = true ==> Two empty prefixes build the empty string.""",
                "Greedy matching fails when both strings have the same next character, which is why DP over (i, j) is needed. Mention the O(n) memory rolling-array version.");

        e(m, "Count of Smaller Numbers After Self",
                """
                idx[] sorted instead of values ==> We sort indexes so each element's answer can be stored at its original position.
                recursive sort on both halves ==> Standard merge sort.
                rightSmaller++ when taking from the right half ==> A value from the right half placed before a left value is smaller and originally to its right.
                counts[idx[i]] += rightSmaller ==> Credit the left element with every smaller element that jumped ahead of it.
                System.arraycopy(tmp, ...) ==> Copy the merged order back for the parent level.""",
                "Name the family: counting inversions with merge sort. A Fenwick tree with coordinate compression is the other accepted answer.");

        e(m, "Maximum Profit in Job Scheduling (Freelance Marketplace)",
                """
                order sorted by end time ==> Process jobs in order of when they finish.
                endTimes[] ==> The sorted end times, used for binary search.
                upperBound(endTimes, i - 1, start[job]) ==> Count how many earlier jobs finish by the time this job starts: the compatible prefix.
                dp[i] = Math.max(dp[i - 1], dp[p] + profit[job]) ==> Either skip this job, or take it plus the best result using only compatible earlier jobs.
                dp[n] ==> The best profit over all jobs.""",
                "This is weighted interval scheduling. Explain why sorting by end time and binary searching the last compatible job gives O(n log n).");

        e(m, "Task Scheduler with Cooldown (Job Queue)",
                """
                freq[] ==> How many times each task appears.
                max ==> The highest frequency; those tasks decide the minimum length.
                maxCount ==> How many tasks share that highest frequency.
                (max - 1) * (n + 1) ==> The frames: each of the first max - 1 appearances is followed by n other slots.
                + maxCount ==> The final partial frame holds the tasks with the maximum frequency.
                Math.max(tasks.length, ...) ==> If there are enough different tasks no idle time is needed.""",
                "Show the formula by drawing the frames (A _ _ A _ _ A). Mention the heap simulation as a more general approach.");

        e(m, "Concurrent Seat Booking (No Double-Booking)",
                """
                enum State { FREE, HELD, SOLD } ==> A seat moves through explicit states; illegal moves are impossible to write by accident.
                ConcurrentHashMap<String, Seat> ==> A thread-safe lookup of seats.
                synchronized (s) ==> A lock per seat, so bookings for different seats never block each other.
                s.state == HELD && s.holdUntil <= now ==> A hold that expired is treated as free (lazy expiry).
                state != FREE return false ==> Someone else holds or owns the seat.
                confirm requires the same owner and an unexpired hold ==> Only the user who holds the seat can buy it, and only in time.""",
                "Explain that in a real system the same guarantee comes from a conditional database UPDATE (WHERE state = 'FREE'). Mention holds with a timeout and why a global lock does not scale.");

        e(m, "Text Justification (Layout Engine)",
                """
                while (j < words.length && len + words[j].length() + (j - i) <= width) ==> Greedily add words while the letters plus the minimum single spaces fit.
                gaps = j - i - 1 ==> Number of spaces between the chosen words.
                j == words.length || gaps == 0 ==> The last line and single-word lines are left-aligned and padded on the right.
                spaces = (width - len) / gaps ==> Equal share of the leftover space for every gap.
                extra = (width - len) % gaps ==> The remainder is given one by one to the leftmost gaps.""",
                "Implementation-heavy but not clever: state the greedy packing rule and the distribution rule before coding. Test one-word lines and the last line.");

        e(m, "Basic Calculator (Spreadsheet Formula)",
                """
                number = number * 10 + (c - '0') ==> Build multi-digit numbers digit by digit.
                result += sign * number ==> When an operator arrives, add the finished number with its sign.
                sign = c == '+' ? 1 : -1 ==> Remember the sign for the next number.
                stack.push(result); stack.push(sign) on '(' ==> Save the outer context before evaluating a bracketed group from scratch.
                result *= stack.pop(); result += stack.pop() on ')' ==> Apply the sign before the bracket, then add to the saved outer result.""",
                "Show you can handle nesting with a stack instead of building a tree. For * and / mention a second stack or the shunting-yard algorithm.");

        e(m, "Bus Routes (Fewest Buses)",
                """
                stopToBuses ==> For each stop the list of buses that visit it, so we can jump from a stop to every bus passing there.
                queue of stops, level by level ==> BFS where each level means one more bus ridden.
                usedBus[bus] ==> Once a bus has been boarded all its stops are enqueued, so never process that bus again.
                next == target return buses ==> The first time the target is reached the number of buses is minimal.
                seenStops ==> Avoid queueing a stop twice.""",
                "Highlight the modelling choice: BFS over buses/stops, counting buses not stops. Without marking whole buses as used the solution is too slow.");

        e(m, "Package Install Order (Dependency Resolver)",
                """
                state 1 = visiting, 2 = done ==> Three states: unvisited, in progress, finished.
                s == 1 ==> We reached a package that is still being resolved: a circular dependency.
                visit each dependency first ==> Post-order DFS ensures dependencies are installed before the package needing them.
                order.add(pkg) after the loop ==> A package is appended only after everything it depends on.
                path ==> Tracks the chain so the error message can show the circular path.""",
                "This is topological sort with cycle detection. Mention that npm, Maven and Gradle do the same and how they report cycles.");

        e(m, "Trapping Rain Water II (2D)",
                """
                border cells in a min-heap ==> Water can only escape over the lowest wall of the boundary, so the lowest border cell is processed first.
                seen[][] ==> Each cell is added once.
                cell[2] - h[r][c] ==> If the neighbour is lower than the current wall level, that difference is trapped water.
                Math.max(h[r][c], cell[2]) ==> The wall level for the neighbour is the higher of its own height and the level of the wall we came from.
                water += Math.max(0, ...) ==> Accumulate trapped water cell by cell.""",
                "Relate it to Dijkstra on 'water level'. Explain why the 1D two-pointer solution does not extend to two dimensions.");

        e(m, "Median of Two Sorted Arrays (Log Time)",
                """
                if (a.length > b.length) swap roles ==> Binary search the SHORTER array so the search is O(log min(m, n)).
                i = (lo + hi) / 2, j = half - i ==> Take i elements from a and j from b to form the left half of the merged order.
                aLeft <= bRight && bLeft <= aRight ==> Every element on the left is no larger than every element on the right: the partition is correct.
                Integer.MIN_VALUE / MAX_VALUE sentinels ==> Handle partitions at the very start or end of an array.
                max(aLeft, bLeft) ==> The median for an odd total; for an even total average it with min(aRight, bRight).""",
                "Say 'I binary search the partition, not a value'. Draw two arrays with a cut line. Expect follow-ups on odd versus even totals.");

        return m;
    }
}
