package com.devvault.config.seed;

import com.devvault.entity.Lesson;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.LessonFactory.add;

/** Chapter 3 (collections and streams) and chapter 4 (data structures). */
public final class LessonsCollections {

    private LessonsCollections() {
    }

    public static List<Lesson> all() {
        List<Lesson> l = new ArrayList<>();
        String c3 = "Collections and Streams";
        String c3Intro = "Arrays are fixed, so Java gives you ready-made, resizable containers: lists, sets and maps. Choosing the right one is half of solving a problem, and streams let you process them in a readable pipeline.";

        add(l, 3, c3, c3Intro, 1, "List and ArrayList",
                "A List is the default container for ordered data: search results, cart items, comments. Knowing how ArrayList works inside lets you answer performance questions correctly.",
                "ArrayList stores items in an internal array and grows it (about 1.5x) when full. Reading by index is O(1), adding at the end is O(1) on average, inserting or removing in the middle is O(n) because items shift. LinkedList is rarely better in practice. List.of() creates an unmodifiable list.",
                "Java",
                """
                List<String> tags = new ArrayList<>();
                tags.add("java");
                tags.add("spring");
                tags.add(0, "backend");
                String first = tags.get(0);
                tags.remove("spring");
                boolean has = tags.contains("java");
                int size = tags.size();

                for (String t : tags) {
                    System.out.println(t);
                }
                List<String> fixed = List.of("a", "b");""",
                """
                List<String> tags = new ArrayList<>(); ==> Declare with the interface type (List) and create the implementation (ArrayList). You can swap implementations later without touching the rest of the code. <> lets the compiler infer String.
                tags.add("java"); ==> Appends to the end. O(1) on average because there is spare capacity.
                tags.add(0, "backend"); ==> Inserts at index 0 and shifts every other item right: O(n).
                tags.get(0) ==> Direct index access: O(1).
                tags.remove("spring"); ==> Searches for the value (O(n)) and removes the first match, shifting the rest.
                tags.contains("java") ==> A linear scan: O(n). If you do this often, use a Set instead.
                for (String t : tags) ==> For-each is the safest way to read every element.
                List.of("a", "b") ==> Immutable list. Calling add on it throws UnsupportedOperationException.""",
                "Expect: ArrayList vs LinkedList, how capacity growth works, why you declare variables as List, and what happens if you remove items while iterating (ConcurrentModificationException - use an Iterator or removeIf).",
                "Removing inside a for-each loop; calling contains in a loop on a big list (O(n^2)); modifying a list returned by List.of.");

        add(l, 3, c3, null, 2, "Map and HashMap: Fast Lookup by Key",
                "Maps are the most useful structure in interviews and real code: counting words, caching, grouping orders by customer. A hash map turns many O(n) searches into O(1).",
                "A HashMap stores key-value pairs. It computes the key's hashCode, uses it to pick a bucket, then compares keys with equals. Average get and put are O(1). Keys must implement hashCode and equals consistently. TreeMap keeps keys sorted (O(log n)), LinkedHashMap keeps insertion order.",
                "Java",
                """
                Map<String, Integer> stock = new HashMap<>();
                stock.put("pen", 10);
                stock.put("book", 4);
                int pens = stock.getOrDefault("pen", 0);
                stock.merge("pen", 5, Integer::sum);
                stock.computeIfAbsent("bag", k -> 1);

                for (Map.Entry<String, Integer> e : stock.entrySet()) {
                    System.out.println(e.getKey() + " = " + e.getValue());
                }
                boolean known = stock.containsKey("pen");""",
                """
                Map<String, Integer> ==> Key type first (String), value type second (Integer). Primitives are not allowed, so Java boxes int into Integer.
                stock.put("pen", 10); ==> Stores the pair. Putting an existing key overwrites the value.
                getOrDefault("pen", 0) ==> Returns 0 instead of null when the key is missing, avoiding a NullPointerException.
                merge("pen", 5, Integer::sum) ==> If pen exists, combine old and new with sum; if not, store 5. The classic counting idiom in one line.
                computeIfAbsent("bag", k -> 1) ==> Only runs the lambda if bag is missing, then stores the result. Great for grouping into lists.
                stock.entrySet() ==> Gives key and value together, which is faster than looking up each key again.
                containsKey("pen") ==> O(1) check. HashMap allows one null key and null values.""",
                "A top-5 question: 'How does HashMap work internally?' Mention hashCode, buckets, equals, collisions (linked list, converted to a tree when long), load factor 0.75 and resizing. Also explain why mutable keys are dangerous.",
                "Using a key class without hashCode/equals; mutating a key after insertion; iterating a HashMap and expecting a stable order.");

        add(l, 3, c3, null, 3, "Set: Unique Values",
                "Whenever the question says unique, duplicate or 'have I seen this', a Set is the answer. It is the simplest big win in many interview problems.",
                "A Set holds no duplicates. HashSet is O(1) add/contains with no order. LinkedHashSet remembers insertion order. TreeSet keeps items sorted with O(log n) operations. add returns false when the item was already present, which is a free duplicate check.",
                "Java",
                """
                Set<String> seen = new HashSet<>();
                List<String> emails = List.of("a@x.com", "b@x.com", "a@x.com");
                List<String> duplicates = new ArrayList<>();

                for (String email : emails) {
                    if (!seen.add(email)) {
                        duplicates.add(email);
                    }
                }

                Set<Integer> a = new TreeSet<>(List.of(5, 1, 3));
                Set<Integer> b = Set.of(3, 4);
                a.retainAll(b);""",
                """
                Set<String> seen = new HashSet<>(); ==> The memory of what we have already met. Lookup is O(1).
                !seen.add(email) ==> add returns false if the value already existed, so one call both checks and inserts.
                duplicates.add(email); ==> Only runs for repeated values, giving us the duplicates in a single O(n) pass.
                new TreeSet<>(List.of(5, 1, 3)) ==> Same set but sorted: iterating gives 1, 3, 5.
                a.retainAll(b); ==> Keeps only elements present in both sets (intersection). addAll is union, removeAll is difference.""",
                "Expect 'find duplicates', 'intersection of two arrays' and 'longest consecutive sequence'. State the trade-off: a set costs O(n) extra memory but removes a nested loop.",
                "Using a List and contains for membership tests; relying on HashSet order; adding mutable objects whose hashCode changes.");

        add(l, 3, c3, null, 4, "Sorting with Comparable and Comparator",
                "Sorting is everywhere: leaderboards, product lists, search results. Writing a correct comparator is a frequent live-coding task.",
                "Comparable gives a class its natural order through compareTo. A Comparator defines an order from outside, so you can sort the same objects in many ways. compare returns negative, zero or positive. Comparator.comparing(...) with thenComparing(...) builds multi-level sorts. Java sorts objects with a stable merge-sort variant (TimSort).",
                "Java",
                """
                record Player(String name, int score, int age) { }

                List<Player> players = new ArrayList<>(List.of(
                        new Player("Asha", 90, 21),
                        new Player("Ravi", 90, 19),
                        new Player("Meena", 75, 22)));

                players.sort(Comparator
                        .comparingInt(Player::score).reversed()
                        .thenComparing(Player::name));

                players.sort((a, b) -> Integer.compare(a.age(), b.age()));""",
                """
                record Player(...) ==> A compact immutable data class with accessor methods score(), name(), age().
                new ArrayList<>(List.of(...)) ==> List.of is immutable; wrapping it in an ArrayList gives a list we are allowed to sort.
                Comparator.comparingInt(Player::score) ==> Sort by score. The method reference Player::score is shorthand for p -> p.score().
                .reversed() ==> Flips to highest score first.
                .thenComparing(Player::name) ==> Tie-breaker: when scores are equal, sort by name A to Z. Without this the order of ties is arbitrary.
                Integer.compare(a.age(), b.age()) ==> Safe comparison. Never write a.age() - b.age(): subtraction can overflow for large values.""",
                "Show you know sort is stable, that comparators must be consistent (if a<b then b>a), and how to sort by two fields. Many problems ('merge intervals', 'meeting rooms') start with 'sort by start time'.",
                "Using subtraction in a comparator; inconsistent comparators that throw 'Comparison method violates its general contract'; forgetting that List.of cannot be sorted.");

        add(l, 3, c3, null, 5, "Lambdas and Streams",
                "Streams express 'filter, transform, collect' in a few readable lines, and they are standard in modern Java code reviews and interviews.",
                "A lambda is a short anonymous function: (parameters) -> expression. A stream is a pipeline over a collection with intermediate steps (filter, map, sorted, distinct) and one terminal step (collect, count, forEach, reduce). Streams do not change the source and are lazy: nothing runs until the terminal operation.",
                "Java",
                """
                List<Integer> nums = List.of(5, 12, 7, 20, 12, 3);

                List<Integer> bigSquares = nums.stream()
                        .filter(n -> n > 5)
                        .distinct()
                        .map(n -> n * n)
                        .sorted()
                        .toList();

                int total = nums.stream().mapToInt(Integer::intValue).sum();
                long count = nums.stream().filter(n -> n % 2 == 0).count();
                Map<Boolean, List<Integer>> split =
                        nums.stream().collect(Collectors.partitioningBy(n -> n % 2 == 0));""",
                """
                nums.stream() ==> Opens a pipeline over the list; the list itself stays untouched.
                .filter(n -> n > 5) ==> Keeps only elements for which the lambda returns true.
                .distinct() ==> Removes duplicates using equals.
                .map(n -> n * n) ==> Transforms each element (squares it). The count stays the same.
                .sorted().toList() ==> Sorts ascending, then the terminal operation toList runs the whole pipeline and gives an immutable list.
                mapToInt(Integer::intValue).sum() ==> Primitive streams (IntStream) avoid boxing and provide sum, average, max.
                partitioningBy(...) ==> A collector that splits into two groups: true and false.""",
                "Be able to rewrite a for-loop as a stream and the reverse. Know map vs flatMap, filter vs takeWhile, and that parallel streams are not always faster. A good answer mentions readability versus debugging difficulty.",
                "Reusing a stream after a terminal operation; side effects inside map; using streams for tiny loops where a for loop is clearer.");

        add(l, 3, c3, null, 6, "Optional and Null Safety",
                "NullPointerException is the most common Java error in the world. Optional makes 'maybe no value' explicit in the method signature.",
                "Optional is a container that either holds a value or is empty. Use it as a return type, not for fields or parameters. map transforms the value if present, orElse supplies a default, orElseThrow raises an error. Spring Data's findById returns Optional.",
                "Java",
                """
                Optional<User> found = repository.findById(42L);

                String name = found.map(User::getName).orElse("Guest");

                User user = repository.findById(42L)
                        .orElseThrow(() -> new NotFoundException("User", 42L));

                found.ifPresent(u -> System.out.println(u.getName()));

                String safe = Objects.requireNonNullElse(input, "");""",
                """
                Optional<User> found ==> The return type warns the caller: there may be no user. The compiler cannot force it, but the intent is clear.
                found.map(User::getName) ==> If a user exists, take the name; if empty, stay empty. No null check needed.
                .orElse("Guest") ==> Fallback value when empty. Always evaluated, so use orElseGet for expensive defaults.
                .orElseThrow(() -> new NotFoundException(...)) ==> Turns 'missing' into a clear business error. This is exactly how this project's services handle a missing id.
                ifPresent(u -> ...) ==> Runs code only when a value exists.
                Objects.requireNonNullElse(input, "") ==> A one-liner default for plain nullable values.""",
                "Interviewers ask when NOT to use Optional (fields, method parameters, collections - return an empty list instead). Also expect 'how do you avoid NullPointerException?'",
                "Calling get() without checking; returning null from a method that returns Optional; wrapping everything in Optional.");

        String c4 = "Data Structures";
        String c4Intro = "A data structure is a way to organise data so the operations you need are fast. This chapter starts with complexity (the language of performance) then covers the structures behind almost every coding interview.";

        add(l, 4, c4, c4Intro, 1, "Big-O: Measuring Speed",
                "Interviewers always follow 'it works' with 'what is the complexity?'. Big-O lets you compare solutions without running them.",
                "Big-O describes how the work grows as the input size n grows, ignoring constants. O(1) constant, O(log n) halves each step (binary search), O(n) one pass, O(n log n) good sorting, O(n^2) nested loops, O(2^n) try every subset. Space complexity counts extra memory. Always say worst case unless asked otherwise.",
                "Java",
                """
                static int first(int[] a) { return a[0]; }

                static boolean contains(int[] a, int x) {
                    for (int v : a) if (v == x) return true;
                    return false;
                }

                static boolean hasPair(int[] a) {
                    for (int i = 0; i < a.length; i++)
                        for (int j = i + 1; j < a.length; j++)
                            if (a[i] == a[j]) return true;
                    return false;
                }

                static int binary(int[] sorted, int x) {
                    int lo = 0, hi = sorted.length - 1;
                    while (lo <= hi) {
                        int mid = (lo + hi) >>> 1;
                        if (sorted[mid] == x) return mid;
                        if (sorted[mid] < x) lo = mid + 1; else hi = mid - 1;
                    }
                    return -1;
                }""",
                """
                return a[0]; ==> One step no matter how long the array is: O(1).
                for (int v : a) ==> Visits each element once at worst: O(n). The early return does not change the worst case.
                for (...) for (...) ==> A loop inside a loop: about n*n/2 comparisons, so O(n^2). 1,000 items means about 500,000 steps; 1,000,000 items would take about 500 billion.
                int mid = (lo + hi) >>> 1; ==> Unsigned shift divides by two safely without overflow.
                lo = mid + 1 or hi = mid - 1 ==> Each step throws away half of what is left, so 1,000,000 items need about 20 steps: O(log n).""",
                "State time AND space complexity for every solution, and explain the trade-off (a HashSet makes hasPair O(n) at the cost of O(n) memory). Rule of thumb: n up to 10^5 needs O(n log n) or better; n up to 1000 allows O(n^2).",
                "Forgetting hidden costs (String concatenation in a loop, list.remove(0), contains on a List); counting only the loops you can see.");

        add(l, 4, c4, null, 2, "Stack and Queue",
                "Stacks power undo, browser back, expression parsing and recursion itself. Queues power task scheduling and breadth-first search. Both are interview staples.",
                "A stack is Last-In First-Out: push, pop, peek. A queue is First-In First-Out: offer, poll, peek. In Java use ArrayDeque for both (the old Stack class is outdated). All main operations are O(1). A deque lets you add and remove at both ends.",
                "Java",
                """
                Deque<Integer> stack = new ArrayDeque<>();
                stack.push(1);
                stack.push(2);
                int top = stack.pop();      // 2

                Queue<String> queue = new ArrayDeque<>();
                queue.offer("job-1");
                queue.offer("job-2");
                String next = queue.poll(); // job-1

                static boolean balanced(String s) {
                    Deque<Character> st = new ArrayDeque<>();
                    for (char c : s.toCharArray()) {
                        if (c == '(') st.push(c);
                        else if (c == ')') {
                            if (st.isEmpty()) return false;
                            st.pop();
                        }
                    }
                    return st.isEmpty();
                }""",
                """
                Deque<Integer> stack = new ArrayDeque<>(); ==> ArrayDeque is the recommended stack: faster than the legacy Stack class and not synchronised.
                stack.push(2); stack.pop(); ==> Last in, first out: the latest pushed item comes out first.
                Queue<String> queue = new ArrayDeque<>(); ==> Same class, used from the other end.
                queue.offer(...); queue.poll(); ==> offer adds at the back, poll removes from the front and returns null if empty (remove would throw).
                st.push(c) when '(' ==> Each opening bracket waits for its partner on the stack.
                if (st.isEmpty()) return false; ==> A closing bracket with nothing open means the text is invalid. Checking first prevents an exception.
                return st.isEmpty(); ==> At the end nothing should be left open, otherwise there was an unmatched '('.""",
                "Be ready to build a queue from two stacks, a min-stack, and to use a stack for valid parentheses, next greater element and DFS. Explain that the call stack is itself a stack, which is why deep recursion overflows.",
                "Using Stack/LinkedList out of habit; calling pop on an empty stack; forgetting the final empty check.");

        add(l, 4, c4, null, 3, "Linked List",
                "Linked lists teach pointer thinking, which underlies trees and graphs. They are a classic whiteboard topic even though ArrayList is used more in practice.",
                "A linked list is a chain of nodes, each holding a value and a reference to the next node. Insert or delete after a known node is O(1), but finding by position is O(n). Singly linked lists go one way; doubly linked lists also keep a prev pointer (LRU caches use them). Typical tricks: slow/fast pointers, dummy head.",
                "Java",
                """
                class ListNode {
                    int val;
                    ListNode next;
                    ListNode(int val) { this.val = val; }
                }

                static ListNode reverse(ListNode head) {
                    ListNode prev = null, cur = head;
                    while (cur != null) {
                        ListNode next = cur.next;
                        cur.next = prev;
                        prev = cur;
                        cur = next;
                    }
                    return prev;
                }

                static boolean hasCycle(ListNode head) {
                    ListNode slow = head, fast = head;
                    while (fast != null && fast.next != null) {
                        slow = slow.next;
                        fast = fast.next.next;
                        if (slow == fast) return true;
                    }
                    return false;
                }""",
                """
                ListNode next; ==> The link to the following node. null means the end of the list.
                ListNode prev = null, cur = head; ==> prev is the already reversed part (starts empty), cur is the node being processed.
                ListNode next = cur.next; ==> Save the rest of the list FIRST, because the next line destroys this link.
                cur.next = prev; ==> The actual reversal: point this node backwards.
                prev = cur; cur = next; ==> Move both pointers one step forward.
                return prev; ==> When cur falls off the end, prev is the new head.
                slow = slow.next; fast = fast.next.next; ==> Tortoise and hare: fast moves twice as fast. In a cycle it must eventually lap and meet the slow pointer.
                fast != null && fast.next != null ==> Both checks are needed to avoid a NullPointerException on the double step.""",
                "Practise reverse, merge two sorted lists, find the middle, detect and locate a cycle, and remove the nth node from the end. Interviewers watch your pointer discipline and whether you handle empty and one-node lists.",
                "Losing the rest of the list by overwriting next too early; null pointer on fast.next.next; forgetting to update the head.");

        add(l, 4, c4, null, 4, "Hashing in Practice: Two Sum",
                "The hash map trick of remembering what you have seen turns many O(n^2) problems into O(n). Two Sum is the most famous interview problem for exactly this reason.",
                "Instead of checking every pair, store each number in a map as you go. For the current number x, the partner you need is target - x. If it is already in the map, you are done. One pass, O(n) time, O(n) space.",
                "Java",
                """
                static int[] twoSum(int[] nums, int target) {
                    Map<Integer, Integer> seen = new HashMap<>();
                    for (int i = 0; i < nums.length; i++) {
                        int need = target - nums[i];
                        if (seen.containsKey(need)) {
                            return new int[] { seen.get(need), i };
                        }
                        seen.put(nums[i], i);
                    }
                    throw new IllegalArgumentException("no solution");
                }""",
                """
                Map<Integer, Integer> seen ==> Maps a value to the index where it appeared. This is the memory that replaces the inner loop.
                int need = target - nums[i]; ==> The number that would complete the pair with the current one.
                seen.containsKey(need) ==> O(1) check: did we already walk past the partner?
                return new int[] { seen.get(need), i }; ==> Earlier index first, current index second.
                seen.put(nums[i], i); ==> Added AFTER the check so a number can never pair with itself.
                throw new IllegalArgumentException(...) ==> Reached only if the input has no answer; throwing is clearer than returning a fake index.""",
                "First state the brute force (O(n^2)), then improve it. Interviewers want to hear you explain the trade-off (memory for speed) and ask about duplicates, negative numbers and sorted input (two pointers then).",
                "Inserting before checking; returning the values instead of the indexes; not handling the 'same element twice' rule.");

        add(l, 4, c4, null, 5, "Trees and Binary Search Trees",
                "Trees model folders, comments, HTML and databases indexes. Recursion on trees is the most common pattern in mid-level interviews.",
                "A tree is nodes with children; a binary tree has at most two (left, right). A binary search tree keeps smaller values left and larger right, so search is O(log n) when balanced. Traversals: in-order (sorted for a BST), pre-order, post-order (depth first) and level-order (breadth first, uses a queue).",
                "Java",
                """
                class TreeNode {
                    int val;
                    TreeNode left, right;
                    TreeNode(int val) { this.val = val; }
                }

                static TreeNode insert(TreeNode root, int v) {
                    if (root == null) return new TreeNode(v);
                    if (v < root.val) root.left = insert(root.left, v);
                    else root.right = insert(root.right, v);
                    return root;
                }

                static void inorder(TreeNode n, List<Integer> out) {
                    if (n == null) return;
                    inorder(n.left, out);
                    out.add(n.val);
                    inorder(n.right, out);
                }

                static int height(TreeNode n) {
                    return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right));
                }""",
                """
                TreeNode left, right; ==> Two child links. null means there is no child.
                if (root == null) return new TreeNode(v); ==> The base case: we found the empty spot where the new value belongs.
                if (v < root.val) root.left = insert(root.left, v); ==> Smaller values go to the left subtree. Assigning the result re-links the (possibly new) child.
                inorder(n.left, out); out.add(n.val); inorder(n.right, out); ==> Left, node, right. For a BST this visits values in sorted order.
                if (n == null) return; ==> Every recursive function needs a base case to stop; empty tree means nothing to do.
                1 + Math.max(height(n.left), height(n.right)) ==> Height of a node is one plus the taller child. A tree problem usually reduces to 'combine the answers of the two children'.""",
                "Be able to code traversals recursively and iteratively, validate a BST (use min/max bounds, not just parent comparison), find the lowest common ancestor and compute diameter. Mention that a skewed BST degrades to O(n) and balanced trees (AVL, red-black) fix it.",
                "Missing the base case; comparing a node only with its parent when validating a BST; forgetting that recursion depth can overflow on a skewed tree.");

        add(l, 4, c4, null, 6, "Heap and PriorityQueue",
                "Whenever you need 'the smallest/largest so far' repeatedly - top K, scheduling, median, shortest path - a heap is the right tool.",
                "A heap is a tree stored in an array where the root is always the smallest (min-heap) or largest (max-heap). Insert and remove-top are O(log n); peeking the top is O(1). Java's PriorityQueue is a min-heap; pass a reverse comparator for a max-heap. It does not iterate in sorted order.",
                "Java",
                """
                PriorityQueue<Integer> minHeap = new PriorityQueue<>();
                minHeap.add(5);
                minHeap.add(1);
                minHeap.add(3);
                int smallest = minHeap.poll();   // 1

                PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());

                static int kthLargest(int[] nums, int k) {
                    PriorityQueue<Integer> heap = new PriorityQueue<>();
                    for (int x : nums) {
                        heap.add(x);
                        if (heap.size() > k) heap.poll();
                    }
                    return heap.peek();
                }""",
                """
                new PriorityQueue<>() ==> A min-heap by default: the smallest element is always at the front.
                minHeap.poll() ==> Removes and returns the smallest in O(log n) (the heap re-organises itself).
                new PriorityQueue<>(Comparator.reverseOrder()) ==> Flips the order to make a max-heap.
                heap.add(x); if (heap.size() > k) heap.poll(); ==> Keep only the k largest numbers: whenever we exceed k, drop the smallest of them.
                return heap.peek(); ==> After the loop the smallest of the k largest is exactly the kth largest. Time O(n log k), memory O(k).""",
                "Typical tasks: top K frequent elements, merge K sorted lists, find median from a data stream (two heaps), task scheduling. State why a heap beats sorting (O(n log k) versus O(n log n) when k is small).",
                "Assuming PriorityQueue iterates in sorted order; using a min-heap when you need a max-heap; forgetting to cap the heap size.");

        add(l, 4, c4, null, 7, "Graphs: BFS and DFS",
                "Graphs model social networks, maps, dependencies and web links. Grid and dependency questions are graphs in disguise, and they appear in nearly every mid-to-senior interview.",
                "A graph is nodes (vertices) connected by edges. Store it as an adjacency list: a map from each node to its neighbours. DFS (depth first) goes as deep as possible using recursion or a stack. BFS (breadth first) uses a queue and finds the shortest path in an unweighted graph. Always track visited nodes to avoid infinite loops.",
                "Java",
                """
                Map<Integer, List<Integer>> graph = new HashMap<>();
                graph.computeIfAbsent(1, k -> new ArrayList<>()).add(2);
                graph.computeIfAbsent(2, k -> new ArrayList<>()).add(1);

                static int shortestPath(Map<Integer, List<Integer>> g, int start, int goal) {
                    Queue<Integer> queue = new ArrayDeque<>();
                    Set<Integer> visited = new HashSet<>();
                    queue.add(start);
                    visited.add(start);
                    int steps = 0;
                    while (!queue.isEmpty()) {
                        for (int i = queue.size(); i > 0; i--) {
                            int node = queue.poll();
                            if (node == goal) return steps;
                            for (int next : g.getOrDefault(node, List.of())) {
                                if (visited.add(next)) queue.add(next);
                            }
                        }
                        steps++;
                    }
                    return -1;
                }""",
                """
                Map<Integer, List<Integer>> graph ==> Adjacency list: for each node, the list of neighbours. Uses memory proportional to the number of edges.
                computeIfAbsent(1, k -> new ArrayList<>()).add(2) ==> Creates the neighbour list on first use, then adds an edge. The second line adds the reverse edge because the graph is undirected.
                Queue<Integer> queue ==> BFS explores all nodes at distance 1, then distance 2, and so on: that is what a queue gives.
                Set<Integer> visited ==> Marks nodes we have already queued so cycles cannot trap us in an infinite loop.
                for (int i = queue.size(); i > 0; i--) ==> Processes exactly one level at a time; the size is captured before the loop because the queue grows.
                if (visited.add(next)) queue.add(next); ==> add returns true only the first time, so every node is queued once.
                steps++; ==> After a whole level is processed we are one edge further away. Result: the minimum number of edges.""",
                "Be ready for number of islands, clone graph, course schedule (topological sort), shortest path in a grid and word ladder. Explain when to choose BFS (shortest path) versus DFS (explore everything, detect cycles) and give complexity O(V + E).",
                "Forgetting the visited set; marking visited when polling instead of when adding (duplicates in the queue); using DFS for shortest paths.");

        return l;
    }
}
