package com.devvault.config.seed;

import com.devvault.config.seed.HardProblemExplanations.Explanation;

import java.util.HashMap;
import java.util.Map;

/** Line-by-line explanations for the original 26 starter problems (the ones created by DataSeeder). */
public final class StarterProblemExplanations {

    private StarterProblemExplanations() {
    }

    private static void e(Map<String, Explanation> m, String title, String lines, String tips) {
        m.put(title, new Explanation(lines.strip(), tips.strip()));
    }

    public static Map<String, Explanation> all() {
        Map<String, Explanation> m = new HashMap<>();

        e(m, "Two Sum",
                """
                Map<Integer, Integer> seen ==> Remembers every number already visited and the index where it appeared.
                int complement = target - nums[i] ==> The number that would finish a pair with the current one.
                seen.containsKey(complement) ==> A constant-time answer to 'did I already walk past the partner?'.
                return new int[] { seen.get(complement), i } ==> Earlier index first, current index second.
                seen.put(nums[i], i) ==> Stored AFTER the check so a number can never be paired with itself.""",
                "Say the brute force (two loops, O(n^2)) first, then the hash map improvement and the memory trade-off. Ask whether duplicates or negative numbers can appear.");

        e(m, "Valid Parentheses",
                """
                Deque<Character> stack ==> Holds the opening brackets that are still waiting for their partner.
                pairs.containsValue(c) ==> The character is an opener, so push it.
                pairs.containsKey(c) ==> The character is a closer, so it must match the most recent opener.
                stack.isEmpty() || !stack.pop().equals(pairs.get(c)) ==> Reject a closer with nothing open or with the wrong partner.
                return stack.isEmpty() ==> Any leftover opener means the string is invalid.""",
                "Mention the two failure cases (closer on an empty stack, leftover openers) before you are asked. A natural follow-up is Longest Valid Parentheses.");

        e(m, "Maximum Subarray (Kadane's Algorithm)",
                """
                int maxSoFar = nums[0] ==> Start from the first element, not 0, so all-negative arrays work.
                currentMax = Math.max(nums[i], currentMax + nums[i]) ==> Extend the previous subarray or start a new one at i, whichever is larger.
                maxSoFar = Math.max(maxSoFar, currentMax) ==> Remember the best sum seen anywhere.
                for (int i = 1; ...) ==> Index 0 is already used for initialisation.""",
                "Phrase it as 'extend or restart at every step'. Follow-ups: return the subarray itself, and the circular version.");

        e(m, "Missing Number",
                """
                int expectedSum = n * (n + 1) / 2 ==> The sum of 0..n if nothing were missing (Gauss formula).
                actualSum ==> The sum of what the array really contains.
                return expectedSum - actualSum ==> The difference is exactly the missing value.
                result ^= i ^ nums[i] ==> XOR version: every present number cancels with its index, leaving the missing one.
                int result = nums.length ==> The index n has no matching array element, so it starts in the XOR.""",
                "Give the sum solution, then the XOR one when asked for an alternative. Mention overflow with very large n for the sum approach.");

        e(m, "Linked List Cycle Detection",
                """
                slow = head, fast = head ==> Both pointers start together.
                fast != null && fast.next != null ==> Make sure the double step is safe; otherwise the list ended, so there is no cycle.
                slow = slow.next ==> One step per round.
                fast = fast.next.next ==> Two steps per round.
                slow == fast ==> Comparing references (not values) proves the fast pointer lapped the slow one inside a cycle.""",
                "Explain WHY they meet: inside a cycle the gap between them shrinks by one each round. A follow-up is finding where the cycle starts.");

        e(m, "Merge Intervals",
                """
                Arrays.sort(intervals, (a, b) -> a[0] - b[0]) ==> Sort by start time so overlapping intervals become neighbours.
                merged.isEmpty() || last end < interval[0] ==> No overlap with the last merged interval, so start a new group.
                else branch ==> Overlap: extend the end of the last merged interval.
                Math.max(lastEnd, interval[1]) ==> The new interval may be completely inside the old one, so keep the larger end.
                toArray(new int[merged.size()][]) ==> Convert the list back to a 2D array.""",
                "Sorting first is the whole idea. Discuss whether touching intervals like [1,2] and [2,3] should merge (here they do).");

        e(m, "Median of Two Sorted Arrays",
                """
                if (nums1.length > nums2.length) swap ==> Binary search the shorter array for speed.
                partition2 = (m + n + 1) / 2 - partition1 ==> The partition in the second array follows from the first so the left side holds half the elements.
                maxLeft / minRight with MIN/MAX sentinels ==> The boundary values around each cut, with sentinels when a cut is at an array edge.
                maxLeft1 <= minRight2 && maxLeft2 <= minRight1 ==> Correct partition: everything on the left is not larger than anything on the right.
                (m + n) % 2 == 0 ==> Even total: average the two middle values; odd total: the largest left value.""",
                "Say 'I binary search the partition, not a value'. Draw the two arrays with a vertical cut and check odd versus even totals.");

        e(m, "Trapping Rain Water",
                """
                left, right pointers ==> Start at both ends and walk inward.
                height[left] <= height[right] ==> The left wall is the lower one, so the water level at left depends only on leftMax.
                leftMax = Math.max(leftMax, height[left]) ==> The tallest wall seen from the left so far.
                water += leftMax - height[left] ==> Water above this bar equals the wall level minus the bar itself.
                else branch ==> The mirror image when the right side is lower.""",
                "Start from the O(n) prefix/suffix array version, then reduce memory with two pointers. Explain why moving the pointer at the lower side is safe.");

        e(m, "N-Queens",
                """
                int[] queens ==> queens[row] is the column of the queen placed in that row, so rows can never clash.
                backtrack(row + 1, ...) ==> Place one queen per row, going down the board.
                isValid(queens, row, col) ==> Check every earlier queen for the same column or the same diagonal.
                Math.abs(c - col) == Math.abs(r - row) ==> Two squares share a diagonal when column distance equals row distance.
                row == n ==> All queens placed: build and store the board.""",
                "Explain pruning: invalid placements are rejected immediately instead of generating all n^n boards. Mention bitmasks as the fast variant.");

        e(m, "Longest Valid Parentheses",
                """
                stack.push(-1) ==> A base index so the first valid run can measure its length.
                c == '(' -> stack.push(i) ==> Remember where each unmatched opener is.
                stack.pop() on ')' ==> Match this closer with the latest opener (or with the base marker).
                stack.isEmpty() then push(i) ==> No opener to match: this closer becomes the new base.
                i - stack.peek() ==> The length of the valid run ending at i.""",
                "Why indices instead of characters: so you can compute lengths. A two-pass counter solution with O(1) space is a well-known follow-up.");

        e(m, "Word Ladder",
                """
                dict = new HashSet<>(wordList) ==> O(1) dictionary lookups.
                queue + level ==> BFS: each level is one more letter changed.
                for (int j ...) for (char c = 'a'...) ==> Generate every neighbour by changing one letter at a time.
                dict.contains(next) && !visited.contains(next) ==> Only walk to real words we have not seen.
                word.equals(endWord) return level ==> The first time BFS reaches the target is the shortest path.""",
                "Why BFS and not DFS: only BFS guarantees the shortest path in an unweighted graph. Bidirectional BFS is the optimisation to mention.");

        e(m, "LRU Cache (Design)",
                """
                Map<Integer, Node> cache ==> O(1) access to any node by key.
                head / tail sentinel nodes ==> Fake ends so inserting and removing never need null checks.
                moveToFront(node) on get ==> A read counts as a use: the node becomes the most recent.
                tail.prev ==> The least recently used node, which is evicted when the cache is full.
                remove(node) + cache.remove(lru.key) ==> Evict from both structures.""",
                "Explain why a hash map alone or a list alone is not enough. Interviewers often ask you to code it from scratch, then to discuss LinkedHashMap and thread safety.");

        e(m, "Serialize and Deserialize Binary Tree",
                """
                sb.append("#,") for null ==> Explicit null markers make the shape unambiguous.
                sb.append(node.val).append(",") then left then right ==> Pre-order traversal: root, left subtree, right subtree.
                data.split(",") into a Deque ==> Tokens are consumed in the same order they were written.
                value.equals("#") return null ==> A null marker ends a branch.
                node.left = helper(values); node.right = helper(values) ==> Rebuild left before right, matching the pre-order.""",
                "Explain why in-order alone is not enough to rebuild a tree. Level-order with null markers (as LeetCode shows trees) is the alternative.");

        e(m, "Sliding Window Maximum",
                """
                Deque<Integer> deque ==> Stores INDICES whose values are in decreasing order; the front is the current maximum.
                deque.peekFirst() <= i - k ==> The front index left the window, so remove it.
                nums[deque.peekLast()] <= nums[i] ==> Smaller values behind a bigger new value can never be a maximum again; discard them.
                deque.offerLast(i) ==> Add the new index at the back.
                i >= k - 1 then result[...] = nums[deque.peekFirst()] ==> Once the first window is complete, the front is its maximum.""",
                "Explain why a heap is worse (cannot cheaply remove expired items). Each index is added and removed once, which proves O(n).");

        e(m, "Course Schedule (Topological Sort)",
                """
                graph.get(prereq).add(course) ==> Edge from prerequisite to the course that needs it.
                inDegree[course]++ ==> How many unfinished prerequisites each course still has.
                queue of in-degree 0 courses ==> Courses that can be taken right now.
                inDegree[next]-- ==> Finishing a course frees the ones that depended on it.
                visited == numCourses ==> If every course was taken there is no cycle.""",
                "Name the algorithm (Kahn's) and mention the DFS-with-colours alternative. Real build tools do the same dependency ordering.");

        e(m, "Output Prediction: Integer Caching Gotcha",
                """
                Integer a = 127; Integer b = 127; ==> Autoboxing calls Integer.valueOf, which returns cached objects for -128 to 127.
                a == b ==> Both variables reference the SAME cached object, so == is true.
                Integer c = 200; Integer d = 200; ==> 200 is outside the cache, so two different objects are created.
                c == d ==> == compares references here, so it is false even though the values are equal.
                c.equals(d) ==> The correct way to compare wrapper objects; it would be true.""",
                "Always say 'use equals for wrapper types'. Mention that the cache upper bound can be changed with a JVM option, so never rely on it.");

        e(m, "Output Prediction: String Pool vs new String()",
                """
                String s1 = "hello"; String s2 = "hello"; ==> Equal literals are interned into one shared pooled object.
                s1 == s2 ==> Same reference, so true.
                new String("hello") ==> Explicitly creates a separate object on the heap.
                s1 == s3 ==> Different references, so false.
                s1.equals(s3) ==> Compares the characters, so true.""",
                "A basic but constantly asked question. Mention intern() and why you should always compare strings with equals.");

        e(m, "Output Prediction: Pre/Post-Increment Precedence",
                """
                int x = 5 ==> Starting value.
                x++ ==> Post-increment: the expression yields 5, then x becomes 6.
                ++x ==> Pre-increment: x becomes 7 first, then the expression yields 7.
                y = 5 + 7 ==> 12.
                System.out.println(x + " " + y) ==> x is now 7, so it prints '7 12'.""",
                "Walk through it step by step out loud. State that such code is bad style and would not pass a code review.");

        e(m, "Output Prediction: finally Overriding a Return Value",
                """
                try { return 1; } ==> The return value 1 is computed and set aside.
                finally { return 2; } ==> The finally block runs before the method really returns and its own return replaces the earlier one.
                System.out.println(test()) ==> Prints 2.
                Real-world note ==> A return inside finally also swallows exceptions, which is why it is considered a serious code smell.""",
                "Good answers include why this is bad practice, plus what happens when finally modifies a returned variable (primitives are not affected).");

        e(m, "Output Prediction: Static Initialization Order",
                """
                static int a = init("a", 1) ==> The first static member in the text runs first when the class loads.
                static int b; ==> Declaration only, no code to run.
                static { b = init("static block", 2); } ==> Static blocks run in textual order too.
                main(...) ==> Runs only after class initialisation finished, so 'main start' is printed last.
                Output order ==> Initializing a, Initializing static block, main start.""",
                "State the rule: static initialisers run once, in the order written, when the class is first loaded. Instance initialisers and constructors are the follow-up.");

        e(m, "Output Prediction: String Concatenation Operator Order",
                """
                1 + 2 + "3" ==> + is evaluated left to right: 1 + 2 is integer addition (3), then 3 + "3" becomes the string "33".
                "1" + 2 + 3 ==> "1" + 2 is already string concatenation ("12"), then "12" + 3 gives "123".
                Rule ==> Once a String appears in the chain, every later + is concatenation.
                Fix for the second line ==> "1" + (2 + 3) would print 15.""",
                "Mention operator associativity (left to right) and use parentheses to show intent.");

        e(m, "Find the Bug: Off-by-One Array Access",
                """
                i <= arr.length ==> The bug: valid indexes are 0 to length - 1.
                arr[i] at i = 5 ==> Throws ArrayIndexOutOfBoundsException.
                i < arr.length ==> The fix: stop before the length.
                for-each loop ==> The even safer alternative: for (int x : arr) sum += x.""",
                "Show you check loop bounds with the first and last element. Mention using for-each when you do not need the index.");

        e(m, "Find the Bug: Infinite Loop From a Missing Update",
                """
                while (i < 10) ==> The condition depends on i.
                System.out.println(i) ==> The body never changes i, so the condition stays true forever.
                i++ ==> The fix: move the loop variable toward the exit condition.
                for (int i = 0; i < 10; i++) ==> A for loop keeps the update next to the condition, which avoids this mistake.""",
                "Habit to mention: before trusting a loop, find the line that makes the condition eventually false.");

        e(m, "Find the Bug: NullPointerException From an Unchecked Return",
                """
                return null ==> getName deliberately returns null when nothing is found.
                name.length() ==> Dereferencing a null reference throws NullPointerException.
                if (name != null) ==> The fix: check before using the value.
                Better design ==> Return Optional<String> so the caller is forced to think about the missing case.""",
                "Mention Optional, Objects.requireNonNull and defensive programming. Never fix it by catching NullPointerException.");

        e(m, "Find the Bug: Integer Division Truncation",
                """
                int total = 7; int count = 2; ==> Both operands are int.
                total / count ==> Integer division gives 3 and drops the .5.
                double average = ... ==> The int result 3 is only widened to 3.0 afterwards, too late.
                (double) total / count ==> Cast one operand BEFORE dividing so the division is floating point (3.5).""",
                "Explain that the type of the variable on the left does not change how the right side is calculated. (double) (total / count) is still wrong.");

        e(m, "Find the Bug: ConcurrentModificationException While Removing",
                """
                for (String name : names) ==> The for-each loop uses a hidden Iterator that tracks the list's modification count.
                names.remove(name) ==> Changing the list directly makes the iterator detect the change and throw on the next step.
                names.removeIf(name -> name.equals("Bob")) ==> The safe fix: the collection removes matches itself during one pass.
                Iterator.remove() ==> The other safe approach: remove through the iterator, not the list.""",
                "Explain fail-fast iterators and that the bug can appear only for some positions in the list. Mention CopyOnWriteArrayList for concurrent cases.");

        return m;
    }
}
