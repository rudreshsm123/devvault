package com.devvault.config.seed;

import com.devvault.config.seed.HardProblemExplanations.Explanation;

import java.util.HashMap;
import java.util.Map;

/** Line-by-line explanations for the first batch of intermediate problems. */
public final class EarlyIntermediateExplanations {

    private EarlyIntermediateExplanations() {
    }

    private static void e(Map<String, Explanation> m, String title, String lines, String tips) {
        m.put(title, new Explanation(lines.strip(), tips.strip()));
    }

    public static Map<String, Explanation> all() {
        Map<String, Explanation> m = new HashMap<>();

        e(m, "Group Anagrams",
                """
                char[] c = w.toCharArray(); Arrays.sort(c) ==> Sorting the letters gives every anagram the same canonical key.
                new String(c) ==> Convert the sorted array back to a String; an array itself would not work as a map key.
                map.computeIfAbsent(key, k -> new ArrayList<>()).add(w) ==> Create the group on first use and add the word.
                new ArrayList<>(map.values()) ==> The groups are the answer.""",
                "Alternative key: a 26-count signature, which is O(k) per word instead of O(k log k).");

        e(m, "Longest Substring Without Repeating Characters",
                """
                Map<Character, Integer> lastSeen ==> The most recent index of each character.
                lastSeen.get(c) >= left ==> The earlier copy is inside the current window, so it is a real conflict.
                left = lastSeen.get(c) + 1 ==> Jump the left edge just past the earlier copy.
                best = Math.max(best, right - left + 1) ==> Window length with both ends included.""",
                "A textbook sliding window. State that each index is visited once, so it is O(n).");

        e(m, "Product of Array Except Self",
                """
                out[i] = out[i - 1] * a[i - 1] ==> First pass: product of everything to the LEFT of i.
                int right = 1 ==> Running product of everything to the right.
                out[i] *= right ==> Second pass: multiply in the right-side product.
                right *= a[i] ==> Include the current element for the next position.""",
                "Division is forbidden because of zeros. Show the two-pass idea and the O(1) extra space trick of reusing the output array.");

        e(m, "3Sum",
                """
                Arrays.sort(a) ==> Sorting enables two pointers and easy duplicate skipping.
                i > 0 && a[i] == a[i - 1] continue ==> Skip duplicate anchors.
                lo = i + 1, hi = a.length - 1 ==> Two pointers search the rest for the pair that completes the sum.
                sum < 0 then lo++, else hi-- ==> Move the pointer that can fix the sum.
                skip equal values after a hit ==> Avoids duplicate triplets.""",
                "Build on Two Sum II. Explain why sorting costs less than the O(n^2) loop it enables.");

        e(m, "Container With Most Water",
                """
                lo = 0, hi = h.length - 1 ==> Start with the widest container.
                (hi - lo) * Math.min(h[lo], h[hi]) ==> Area is width times the shorter wall.
                if (h[lo] < h[hi]) lo++ else hi-- ==> Move the shorter wall; moving the taller one cannot increase the area.
                best = Math.max(...) ==> Keep the best area seen.""",
                "The justification of the greedy move is the point of the question. Say it clearly.");

        e(m, "Trending Hashtags (Top K Frequent)",
                """
                count.merge(t, 1, Integer::sum) ==> Frequency table.
                PriorityQueue (min-heap by count) ==> Holds the best k candidates; the weakest is on top.
                if (heap.size() > k) heap.poll() ==> Evict the weakest when over capacity.
                out.add(0, heap.poll().getKey()) ==> Polling yields weakest first, so insert at the front.""",
                "Complexity O(n + m log k). Bucket sort by frequency gives O(n) as a follow-up.");

        e(m, "Binary Tree Level Order Traversal",
                """
                Queue<TreeNode> q ==> BFS queue.
                int size = q.size() ==> Freeze the number of nodes on the current level.
                for (i < size) ==> Process exactly one level per outer loop.
                add left and right children ==> They form the next level.""",
                "Template for every BFS-by-level problem (zigzag, right side view, averages).");

        e(m, "Validate Binary Search Tree",
                """
                isValid(n, lo, hi) ==> Each node must lie strictly between the bounds inherited from its ancestors.
                n.val <= lo || n.val >= hi ==> Out of range: not a BST.
                isValid(n.left, lo, n.val) ==> Left subtree values must be smaller than this node.
                isValid(n.right, n.val, hi) ==> Right subtree values must be larger.
                Long.MIN_VALUE / MAX_VALUE ==> Safe initial bounds even for Integer.MIN_VALUE nodes.""",
                "The classic trap is comparing only with the parent. Mention the in-order alternative.");

        e(m, "Number of Islands",
                """
                loop over every cell ==> An island is found at the first unvisited land cell.
                sink(g, r, c) ==> Flood fill that marks the whole island as visited by turning land into water.
                count++ ==> One island per flood fill started.
                four direction recursion ==> Up, down, left, right neighbours.""",
                "Mention modifying the input versus a visited array, and Union-Find or BFS to avoid deep recursion.");

        e(m, "Coin Change",
                """
                dp[a] ==> The fewest coins needed to make amount a.
                Arrays.fill(dp, amount + 1) ==> A value larger than any possible answer stands for impossible.
                dp[0] = 0 ==> Zero coins make zero.
                dp[a] = Math.min(dp[a], dp[a - c] + 1) ==> Try every coin as the last coin.
                dp[amount] > amount ? -1 ==> If still 'impossible' return -1.""",
                "Why greedy fails (coins 1, 3, 4 and amount 6). This is unbounded knapsack.");

        e(m, "House Robber",
                """
                skip, take ==> Best total if the previous house was skipped or robbed.
                next = Math.max(take, skip + x) ==> Either keep the best so far, or rob this house on top of the one before.
                skip = take; take = next ==> Roll the two variables forward.
                return take ==> The best total at the end.""",
                "Write the recurrence first, then reduce the table to two variables.");

        e(m, "Longest Increasing Subsequence",
                """
                tails[i] ==> The smallest possible last value of an increasing subsequence of length i + 1.
                Arrays.binarySearch(tails, 0, size, x) ==> Find where x fits.
                i < 0 then i = -(i + 1) ==> Convert 'not found' into the insertion point.
                tails[i] = x; if (i == size) size++ ==> Replace a tail, or extend the longest subsequence.""",
                "Mention O(n^2) DP first, then the O(n log n) method. tails is not the actual subsequence.");

        e(m, "Rotate Array",
                """
                k %= a.length ==> Rotating by n or more wraps around.
                reverse(a, 0, n - 1) ==> Reverse everything.
                reverse(a, 0, k - 1) ==> Restore the order of the first k elements.
                reverse(a, k, n - 1) ==> Restore the rest.""",
                "Three reversals give an in-place solution; the extra-array version uses index (i + k) % n.");

        e(m, "Search in Rotated Sorted Array",
                """
                a[lo] <= a[mid] ==> The left half is sorted.
                a[lo] <= t && t < a[mid] ==> The target lies in the sorted left half, so search it.
                otherwise search right ==> The target must be in the other half.
                else (right half sorted) ==> The mirrored decision.""",
                "Binary search only needs one half to be sorted each step. Discuss duplicates (O(n) worst case).");

        e(m, "Kth Largest Element",
                """
                PriorityQueue<Integer> heap ==> A min-heap of the k largest values.
                heap.add(x) ==> Candidate.
                if (heap.size() > k) heap.poll() ==> Drop the smallest.
                heap.peek() ==> The smallest of the k largest is the kth largest.""",
                "Mention quickselect (average O(n)) as the optimal-time alternative.");

        e(m, "Min Stack",
                """
                Deque<int[]> st ==> Each entry stores {value, minimum so far}.
                Math.min(x, st.peek()[1]) ==> The new minimum for the stack including x.
                getMin() returns st.peek()[1] ==> O(1) because the minimum travels with the stack.
                pop() ==> Removing an entry restores the previous minimum automatically.""",
                "A design question about trading memory for O(1) queries. The two-stack variant is equivalent.");

        e(m, "Search Autocomplete (Trie)",
                """
                Map<Character, Node> next ==> Each node maps a letter to its child.
                computeIfAbsent(c, k -> new Node()) ==> Create a path while inserting.
                boolean end ==> Marks that a word finishes here.
                collect(node, path, out) ==> DFS from the prefix node gathers every word below it.""",
                "Compare with scanning a list. Mention limiting results and ranking by popularity.");

        e(m, "URL Shortener (Base62 Encoder)",
                """
                nextId++ ==> A unique number for every URL.
                n % 62 ==> Converts a number to base 62 digit by digit.
                A.indexOf(c) ==> Decoding multiplies the running value by 62 and adds each digit.
                sb.reverse() ==> The digits were produced least significant first.""",
                "Cover collisions, expiry, analytics and scaling in the follow-up discussion.");

        e(m, "API Rate Limiter (Sliding Window)",
                """
                Map<String, Deque<Long>> hits ==> A queue of request timestamps per user.
                while (now - q.peekFirst() >= windowMs) q.pollFirst() ==> Drop requests outside the window.
                q.size() >= max return false ==> Over the limit: reject.
                q.addLast(now) ==> Record this request.""",
                "Compare fixed window, sliding window and token bucket. Distributed version: Redis.");

        e(m, "Coupon / Discount Engine",
                """
                interface Coupon { long apply(long totalCents); } ==> One abstraction for all discount kinds.
                record Percent / Flat / Capped ==> Each rule is a small class implementing the interface.
                Math.max(0, ...) ==> A discount can never make the total negative.
                Instant.now().isAfter(expiry) ==> Expired coupons are rejected before applying.""",
                "This is the Strategy pattern and the Open/Closed principle in practice.");

        e(m, "Merge Guest Cart After Login",
                """
                new HashMap<>(saved) ==> Start from the saved cart.
                guest.forEach((sku, qty) -> out.merge(sku, qty, Integer::sum)) ==> Add guest quantities to existing lines.
                out.replaceAll(... Math.min(qty, stock)) ==> Cap by the available stock.
                out.values().removeIf(q -> q <= 0) ==> Remove empty lines.""",
                "Mention doing this in one transaction on the server and handling price changes.");

        e(m, "Hotel Booking Overlap (Meeting Rooms II)",
                """
                Arrays.sort by start ==> Process bookings in time order.
                PriorityQueue<Integer> endTimes ==> The end time of every room currently occupied; the earliest on top.
                endTimes.peek() <= s[0] ==> That room is free before this booking starts, so reuse it.
                endTimes.add(s[1]) ==> Occupy a room until the booking ends.
                endTimes.size() ==> The number of rooms needed.""",
                "The sweep-line version (sort starts and ends separately) is the alternative.");

        e(m, "News Feed: Merge Sorted Timelines",
                """
                PriorityQueue<int[]> heap ==> Holds {timeline index, position} ordered by the post's time.
                heap.poll() ==> The newest unread post across all timelines.
                heap.add({t, pos + 1}) ==> Refill from the same timeline.
                out.size() < k ==> Stop after the first page.""",
                "Relate to merge k sorted lists and mention fan-out on write versus on read.");

        e(m, "Flatten Nested Comments Thread",
                """
                walk(c, depth + 1, out) ==> Recursion visits replies one level deeper.
                out.add(new Row(id, text, depth)) ==> Parent first, then its replies: pre-order.
                explicit stack version ==> Push children in reverse so the first reply is processed first.
                depth ==> The indentation level for the UI.""",
                "Mention depth limits in the UI and recursive SQL (WITH RECURSIVE).");

        e(m, "Session Store with Expiry",
                """
                Entry(userId, expiresAt) ==> Each session carries its own deadline.
                e.expiresAt() <= now ==> Expired: remove and return null (lazy eviction).
                map.put(token, new Entry(..., now + ttl)) ==> Sliding expiry refreshes on activity.
                sweep(now) removeIf ==> A periodic clean-up of sessions nobody used again.""",
                "Use cryptographically random tokens. In production the store is Redis with EXPIRE.");

        e(m, "Game Leaderboard (Top N)",
                """
                best.merge(p, s, Math::max) ==> Keep each player's best score.
                TreeSet ordered by score descending then name ==> A sorted view maintained incrementally.
                ranking.remove(Map.entry(old, p)) ==> Remove the stale entry before inserting the new score.
                iterate until n ==> Take the first n entries.""",
                "Mention Redis sorted sets as the production answer.");

        e(m, "Friend Suggestions (Mutual Friends)",
                """
                for f in friends, for fof in friends of f ==> Two-hop neighbours.
                !fof.equals(me) && !mine.contains(fof) ==> Skip yourself and existing friends.
                score.merge(fof, 1, Integer::sum) ==> The number of mutual friends.
                sort by score descending, then name ==> Deterministic ranking.""",
                "Only two hops are needed. At scale this becomes a graph-database or batch job.");

        e(m, "Prevent Inventory Oversell",
                """
                AtomicInteger stock ==> A lock-free thread-safe counter.
                stock.get() then check cur < qty ==> Read the current value and refuse if there is not enough.
                compareAndSet(cur, cur - qty) ==> Succeeds only if nobody changed the value in between; otherwise retry.
                SQL: UPDATE ... WHERE stock >= ? ==> The database version of the same atomic check.""",
                "Explain the check-then-act race and that a JVM lock does not protect several servers.");

        e(m, "Normalize URL Path (Breadcrumbs)",
                """
                path.split("/") ==> Break the path into segments.
                part.isEmpty() || part.equals(".") ==> Ignore empty and current-directory segments.
                part.equals("..") then stack.pollLast() ==> Go up one level (ignored at the root).
                "/" + String.join("/", stack) ==> Rebuild the canonical path.""",
                "Path traversal attacks use '..', which is why every file server normalises paths.");

        e(m, "De-duplicate Notifications",
                """
                key = user + type ==> One entry per user and notification type.
                nowSec - prev < 60 ==> Sent less than a minute ago: suppress.
                LinkedHashMap with accessOrder and removeEldestEntry ==> A bounded LRU map so memory cannot grow forever.
                separator character in the key ==> Prevents collisions like ('a:b','c') versus ('a','b:c').""",
                "Distributed systems use Redis SET with NX and EX for the same idea.");

        e(m, "Decode String Template",
                """
                Deque<Integer> counts ==> Repeat counts waiting for their closing bracket.
                Deque<StringBuilder> strings ==> The text built before each opening bracket.
                c == '[' ==> Save the current state and start a fresh builder.
                c == ']' ==> Repeat the inner text count times and append it to the saved text.
                k = k * 10 + digit ==> Counts can have several digits.""",
                "A nested-structure stack problem. Recursion is the other clean solution.");

        e(m, "Word Break (Search Tokenizer)",
                """
                boolean[] dp ==> dp[i] means the first i characters can be split into dictionary words.
                dp[0] = true ==> The empty prefix is valid.
                dp[j] && dict.contains(s.substring(j, i)) ==> A valid prefix followed by a dictionary word.
                break ==> One valid split is enough.""",
                "Plain recursion is exponential; memoisation or this table makes it O(n^2).");

        e(m, "Subarray Sum Equals K (Analytics Window)",
                """
                seen.put(0, 1) ==> The empty prefix, so subarrays starting at index 0 are counted.
                sum += x ==> Running prefix sum.
                seen.getOrDefault(sum - k, 0) ==> How many earlier prefixes make a subarray of sum k.
                seen.merge(sum, 1, Integer::sum) ==> Record this prefix.""",
                "Sliding window fails with negatives. Prefix sums plus a hash map is the general answer.");

        e(m, "Daily Temperatures (Next Greater)",
                """
                Deque<Integer> stack ==> Indices of days still waiting for a warmer day.
                t[i] > t[stack.peek()] ==> Today is warmer than the day on top.
                out[j] = i - j ==> The wait is the distance between the two days.
                stack.push(i) ==> Today now waits for a warmer day.""",
                "Monotonic stack: every index is pushed and popped once, so O(n).");

        e(m, "Paginated Search with Cursor",
                """
                filter createdAt < lastCreatedAt || (equal && id < lastId) ==> Items strictly after the cursor in the sort order.
                limit(size) ==> One page.
                (created_at, id) < (?, ?) in SQL ==> Uses an index and avoids scanning skipped rows.
                id as tie-breaker ==> Makes the order total, so no row is skipped or repeated.""",
                "Explain why OFFSET gets slower and unstable. APIs return the cursor in the response.");

        e(m, "Check Balanced HTML Tags",
                """
                Pattern "<(/?)([a-z][a-z0-9]*)>" ==> Matches opening and closing tags and captures the slash and the name.
                stack.push(name) for opening tags ==> Remember what is open.
                stack.pop().equals(name) for closing tags ==> The closing tag must match the latest opening tag.
                return stack.isEmpty() ==> Nothing may remain open.""",
                "It is valid parentheses with names. Browsers also handle void tags like <br> and optional end tags.");

        return m;
    }
}
