package com.devvault.config.seed;

import com.devvault.entity.Difficulty;
import com.devvault.entity.Note;
import com.devvault.entity.RevisionStatus;

import java.util.ArrayList;
import java.util.List;

/** Cheat sheets and revision notes written for quick review before interviews. */
public final class NoteSeedData {

    private NoteSeedData() {
    }

    private static void n(List<Note> out, String title, String topic, String language, Difficulty difficulty,
                          boolean important, String tags, String content) {
        Note note = new Note();
        note.setTitle(title);
        note.setTopic(topic);
        note.setProgrammingLanguage(language);
        note.setDifficulty(difficulty);
        note.setImportant(important);
        note.setTags(tags);
        note.setRevisionStatus(RevisionStatus.NOT_STARTED);
        note.setContent(content.strip());
        out.add(note);
    }

    public static List<Note> all() {
        List<Note> l = new ArrayList<>();
        Difficulty E = Difficulty.EASY, M = Difficulty.MEDIUM, H = Difficulty.HARD;

        n(l, "Big-O Cheat Sheet: Data Structures", "Complexity", "Java", E, true, "big-o,data-structures,cheatsheet",
                """
                Operation costs (average case unless stated)

                Array / ArrayList      access O(1)   search O(n)   insert/delete middle O(n)   append O(1) amortised
                LinkedList             access O(n)   search O(n)   insert/delete at a known node O(1)
                HashMap / HashSet      get/put/contains O(1)   worst case O(n) (heavily colliding keys)
                TreeMap / TreeSet      get/put/remove O(log n), sorted order
                PriorityQueue (heap)   add O(log n)   peek O(1)   poll O(log n)   contains O(n)
                ArrayDeque             push/pop/offer/poll O(1) at both ends
                Stack (via Deque)      push/pop/peek O(1)
                Binary search tree     balanced O(log n), skewed O(n)
                Graph adjacency list   space O(V + E), BFS/DFS O(V + E)

                Sorting: merge sort O(n log n) stable, extra O(n) | quick sort O(n log n) avg, O(n^2) worst | heap sort O(n log n) in place
                Rule of thumb for the time limit: n = 10^5 needs O(n log n) or better; n = 1000 allows O(n^2); n = 20 allows O(2^n).""");

        n(l, "Big-O Cheat Sheet: Algorithm Patterns", "Complexity", "Java", M, true, "big-o,patterns,cheatsheet",
                """
                Pattern -> how to recognise it -> typical complexity

                Two pointers      sorted array, pairs, palindromes, in-place edits               O(n)
                Sliding window    longest/shortest subarray or substring with a property           O(n)
                Binary search     sorted data, or 'smallest X such that condition holds'            O(log n)
                Hash map/set      'seen before?', counting, grouping, two-sum                       O(n)
                Stack             nesting, matching brackets, next greater element                  O(n)
                BFS               shortest path in unweighted graph/grid, level order               O(V + E)
                DFS/backtracking  all combinations, permutations, subsets, paths                    exponential
                Dynamic programming  count/min/max with overlapping sub-problems                    polynomial
                Heap              top K, running median, merge K sorted lists, scheduling           O(n log k)
                Union-Find        connected components, cycle detection in undirected graphs        ~O(n)
                Topological sort  dependencies, ordering with prerequisites                         O(V + E)
                Trie              prefix search, autocomplete                                       O(length)""");

        n(l, "Java Collections: Which One to Pick", "Java Collections", "Java", E, true, "java,collections,interview",
                """
                Need an ordered list with index access             -> ArrayList
                Need fast insert/remove at both ends (queue/stack)  -> ArrayDeque
                Need unique values, no order                        -> HashSet
                Need unique values, keep insertion order            -> LinkedHashSet
                Need unique values, sorted                          -> TreeSet
                Need key-value pairs, no order                      -> HashMap
                Need key-value pairs, insertion/access order (LRU)  -> LinkedHashMap
                Need key-value pairs, sorted by key                 -> TreeMap
                Need smallest/largest element repeatedly            -> PriorityQueue
                Thread-safe map                                     -> ConcurrentHashMap
                Thread-safe list with rare writes                   -> CopyOnWriteArrayList
                Fixed set of enum keys                              -> EnumMap / EnumSet

                Tips: declare variables with the interface type (List, Map), prefer List.of / Map.of for constants, never modify a collection while iterating (use removeIf or an Iterator).""");

        n(l, "How HashMap Works Internally", "Java Collections", "Java", H, true, "java,hashmap,interview",
                """
                put(key, value):
                1. Compute hash = key.hashCode(), then spread the bits (h ^ (h >>> 16)).
                2. Bucket index = (n - 1) & hash, where n is the table size (always a power of two).
                3. Empty bucket -> store a node. Occupied -> compare keys with equals(); same key -> replace the value, different key -> append to the chain (collision).
                4. When a chain has 8+ nodes and the table has 64+ buckets, the chain becomes a red-black tree (O(log n) lookups).
                5. When size > capacity * loadFactor (default 0.75) the table doubles and entries are redistributed (rehash).

                get(key): hash, find the bucket, walk the chain/tree comparing equals().

                Rules for keys: equal objects MUST have equal hashCodes; keys should be immutable (changing a key after insertion makes it unreachable).

                Common interview questions: why capacity is a power of two, what happens with null keys (one allowed), HashMap vs Hashtable vs ConcurrentHashMap, why not use mutable keys.""");

        n(l, "OOP in Four Sentences (and Examples)", "OOP", "Java", E, true, "oop,java,interview",
                """
                Encapsulation: keep fields private and expose behaviour through methods, so an object can protect its own rules (a BankAccount never allows a negative balance).
                Abstraction: expose WHAT something does, hide HOW (the List interface hides whether it is an ArrayList or LinkedList).
                Inheritance: a class reuses and specialises another (EmailNotification extends Notification) - model 'is a' relationships only; prefer composition ('has a') otherwise.
                Polymorphism: code written against a parent type works with any child; the actual method run is chosen at runtime (overriding).

                Overriding vs overloading: overriding = same signature in a subclass (runtime); overloading = same name, different parameters in one class (compile time).
                Abstract class vs interface: abstract class can hold state and shared code (single inheritance); interface defines a contract (many allowed).""");

        n(l, "SOLID Principles with Examples", "Design Principles", "Java", M, true, "solid,design,interview",
                """
                S - Single Responsibility: a class has one reason to change. ProblemService handles problem rules; it does not render HTML or send emails.
                O - Open/Closed: open for extension, closed for modification. New coupon types are new classes implementing Coupon; the checkout code does not change.
                L - Liskov Substitution: a subclass must work wherever its parent is expected. A Square that breaks Rectangle's setWidth/setHeight contract violates it.
                I - Interface Segregation: many small interfaces beat one huge one. A Printer interface should not force a scanner to implement print().
                D - Dependency Inversion: depend on abstractions, not concrete classes. A service depends on the PaymentMethod interface; Spring injects the implementation.

                How to answer in an interview: name the principle, give a 2-line example from your own project, and say what goes wrong when it is violated (hard-to-test code, rippling changes).""");

        n(l, "Design Patterns You Should Know", "Design Patterns", "Java", M, true, "design-patterns,interview",
                """
                Creational: Singleton (one instance - holder idiom or enum), Factory (hide which class is created), Builder (many optional constructor arguments), Prototype (clone).
                Structural: Adapter (make an incompatible interface fit), Decorator (add behaviour by wrapping, e.g. BufferedReader), Facade (one simple entry to a complex subsystem), Proxy (stand-in; Spring AOP and @Transactional use proxies).
                Behavioural: Strategy (swap algorithms - coupons, pricing), Observer (publish/subscribe events), Command (undo/redo), Template Method (fixed skeleton, customisable steps), State (order status machine), Iterator (for-each).

                Where Spring uses them: Singleton (default bean scope), Factory (BeanFactory), Proxy (AOP, transactions), Template Method (JdbcTemplate), Observer (ApplicationEvent), Strategy (HandlerMapping, ViewResolver).""");

        n(l, "HTTP Status Codes Worth Memorising", "HTTP", "HTTP", E, true, "http,rest,status-codes",
                """
                200 OK                 request succeeded
                201 Created            resource created (send a Location header)
                204 No Content         success, nothing to return (DELETE)
                301 / 302              permanent / temporary redirect
                304 Not Modified       use your cached copy
                400 Bad Request        malformed or invalid input (validation errors)
                401 Unauthorized       not authenticated (no or bad credentials)
                403 Forbidden          authenticated but not allowed
                404 Not Found          resource does not exist
                405 Method Not Allowed wrong HTTP method for this URL
                409 Conflict           state conflict (duplicate, version mismatch)
                422 Unprocessable      well-formed but semantically invalid
                429 Too Many Requests  rate limit exceeded (send Retry-After)
                500 Internal Server Error  unexpected server failure
                502 / 503 / 504        bad gateway / unavailable / gateway timeout

                Method semantics: GET and DELETE and PUT are idempotent; POST is not. GET and HEAD are safe (no changes).""");

        n(l, "REST API Design Checklist", "REST", "HTTP", M, true, "rest,api,design",
                """
                1. Nouns, plural, lower-case: /api/problems, /api/problems/42/attempts (never /getProblem).
                2. Use the right method: GET read, POST create, PUT replace, PATCH partial update, DELETE remove.
                3. Return correct codes: 201 + Location on create, 204 on delete, 404 when missing, 400 for validation, 409 for conflicts.
                4. One consistent error body: timestamp, status, message, field errors.
                5. Filtering, sorting and paging through query parameters: ?topic=Graphs&sort=createdAt,desc&page=0&size=20.
                6. Version the API (/v1) or use headers, and never break existing clients silently.
                7. Validate every input on the server; never trust the client.
                8. Do not expose entities with internal fields - use DTOs.
                9. Authenticate (JWT or sessions), authorise per resource, use HTTPS, rate limit.
                10. Make writes idempotent where retries are possible (idempotency keys).
                11. Document with OpenAPI/Swagger and test with Postman collections.""");

        n(l, "SQL JOIN Cheat Sheet", "SQL", "SQL", E, true, "sql,join,cheatsheet",
                """
                INNER JOIN       only rows that match in both tables
                LEFT JOIN        all rows from the left table + matches (NULL when none)
                RIGHT JOIN       all rows from the right table + matches
                FULL OUTER JOIN  all rows from both (MySQL: UNION of LEFT and RIGHT)
                CROSS JOIN       every combination (cartesian product) - rarely wanted
                SELF JOIN        a table joined with itself (employee -> manager)

                Anti-join (rows with NO match):   SELECT c.* FROM customers c LEFT JOIN orders o ON o.customer_id = c.id WHERE o.id IS NULL;
                Top N per group:                  ROW_NUMBER() OVER (PARTITION BY dept ORDER BY salary DESC) <= 3
                Order of evaluation:              FROM -> WHERE -> GROUP BY -> HAVING -> SELECT -> ORDER BY -> LIMIT
                WHERE filters rows, HAVING filters groups.
                COUNT(*) counts rows, COUNT(col) ignores NULLs. Compare NULL with IS NULL, never = NULL.""");

        n(l, "SQL Interview Questions with Answers", "SQL", "SQL", M, true, "sql,interview,queries",
                """
                Second highest salary:
                  SELECT MAX(salary) FROM employees WHERE salary < (SELECT MAX(salary) FROM employees);

                Duplicate emails:
                  SELECT email, COUNT(*) FROM users GROUP BY email HAVING COUNT(*) > 1;

                Delete duplicates but keep the lowest id:
                  DELETE u1 FROM users u1 JOIN users u2 ON u1.email = u2.email AND u1.id > u2.id;

                Employees earning more than their manager:
                  SELECT e.name FROM employees e JOIN employees m ON e.manager_id = m.id WHERE e.salary > m.salary;

                Customers who never ordered:
                  SELECT name FROM customers WHERE id NOT IN (SELECT customer_id FROM orders WHERE customer_id IS NOT NULL);

                Running total:
                  SELECT day, SUM(amount) OVER (ORDER BY day) AS running_total FROM sales;

                Nth highest salary: DENSE_RANK() OVER (ORDER BY salary DESC) = N.""");

        n(l, "Database Normalisation in Plain English", "Databases", "SQL", M, false, "databases,normalization,design",
                """
                1NF: every column holds ONE value (no comma-separated lists) and every row is unique.
                2NF: no partial dependency - every non-key column depends on the WHOLE primary key (matters with composite keys).
                3NF: no transitive dependency - non-key columns depend only on the key, not on other non-key columns (store the city's name once in a cities table, not on every customer row).

                Why normalise: no duplicated facts, so updates cannot make copies disagree.
                When to denormalise: read-heavy reporting where joins are too slow - accept duplication for speed and keep it consistent in code.

                Keys: primary key (unique identity), foreign key (reference to another table), unique key, composite key (several columns).
                Relationships: one-to-many (customer -> orders), many-to-many (students <-> courses, needs a join table).""");

        n(l, "ACID, Isolation Levels and Locking", "Databases", "SQL", H, true, "databases,transactions,acid",
                """
                ACID: Atomicity (all or nothing), Consistency (rules always hold), Isolation (concurrent transactions do not interfere), Durability (committed data survives a crash).

                Problems without isolation:
                  Dirty read          reading another transaction's uncommitted change
                  Non-repeatable read the same row read twice gives different values
                  Phantom read        the same query returns different sets of rows

                Isolation levels (weakest to strongest): READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ (MySQL InnoDB default), SERIALIZABLE.

                Locking:
                  Pessimistic  SELECT ... FOR UPDATE blocks others (use under high contention)
                  Optimistic   version column checked on UPDATE; retry on conflict (JPA @Version)

                Deadlock: two transactions wait for each other. Avoid by locking rows in the same order and keeping transactions short.""");

        n(l, "Indexing Rules of Thumb", "Databases", "SQL", M, true, "sql,index,performance",
                """
                Index columns used in WHERE, JOIN ... ON and ORDER BY of frequent queries.
                A composite index (a, b, c) serves queries on a, on a+b and on a+b+c - NOT on b alone (leftmost-prefix rule).
                Put equality columns first, range columns last.
                Functions on the column (LOWER(col), col + 1) and a leading wildcard (LIKE '%x') prevent index use.
                Low-selectivity columns (boolean, status with 3 values) are poor single-column indexes.
                Every index slows INSERT/UPDATE/DELETE and uses space - do not index everything.
                Use EXPLAIN to check the plan: look for type ALL (full table scan) and large 'rows' estimates.
                A covering index contains every column the query needs, so the table is never touched.
                N+1 problem (JPA): one query for the list plus one per row - fix with JOIN FETCH or batch fetching.""");

        n(l, "Git Commands Cheat Sheet", "Git", "Bash", E, true, "git,cheatsheet,version-control",
                """
                Setup           git clone <url>          git init          git remote -v
                Daily           git status   git add <file>   git commit -m "message"   git push   git pull
                Branches        git switch -c feature/x   git switch main   git branch -d feature/x
                Inspect         git log --oneline --graph   git diff   git diff --staged   git blame <file>
                Undo            git restore <file>  (discard working changes)   git restore --staged <file>  (unstage)
                                git revert <commit> (safe undo with a new commit)   git reset --hard <commit> (DANGEROUS: discards work)
                Combine         git merge feature/x     git rebase main     git cherry-pick <commit>
                Temporary       git stash   git stash pop
                Tags/releases   git tag v1.0.0   git push --tags

                Good commit messages: imperative mood, short subject, explain WHY in the body.
                Never commit secrets (use .gitignore and environment variables); if you do, rotate the secret - deleting the commit is not enough.""");

        n(l, "Spring Boot Annotations Cheat Sheet", "Spring Boot", "Java", M, true, "spring,annotations,cheatsheet",
                """
                @SpringBootApplication  main class: configuration + auto-configuration + component scan
                @RestController         controller returning JSON      @Controller  returns a view name
                @RequestMapping / @GetMapping / @PostMapping / @PutMapping / @DeleteMapping
                @PathVariable (/problems/{id})   @RequestParam (?q=x)   @RequestBody (JSON)   @Valid (run validation)
                @Service                business logic bean             @Repository  data access bean
                @Component              any other bean                  @Configuration + @Bean  define beans manually
                @Autowired              inject a dependency (prefer constructor injection)
                @Transactional          wrap a method in a database transaction
                @Entity @Table @Id @GeneratedValue @Column @Enumerated @OneToMany @ManyToOne @Version
                @NotBlank @Size @Email @Min @Max @Pattern    (Bean Validation)
                @ControllerAdvice / @RestControllerAdvice + @ExceptionHandler    global error handling
                @Value("${prop}") / @ConfigurationProperties    read configuration
                @Profile("cloud")       bean only active in a profile
                @Test @WebMvcTest @DataJpaTest @SpringBootTest @MockitoBean @Mock @InjectMocks""");

        n(l, "Spring Request Lifecycle (What Happens on a Request)", "Spring Boot", "Java", H, true, "spring,mvc,interview",
                """
                1. The embedded Tomcat receives the HTTP request and hands it to the DispatcherServlet (the front controller).
                2. Servlet filters run first (security, our ReadOnlyFilter, CORS).
                3. HandlerMapping finds the controller method that matches the URL and HTTP method.
                4. Argument resolvers convert the request: @PathVariable, @RequestParam, @RequestBody (JSON -> object via Jackson), then @Valid runs Bean Validation.
                5. The controller calls a service, which uses a repository; Spring Data runs SQL through Hibernate and the connection pool (Hikari).
                6. The return value is converted: @RestController -> JSON via HttpMessageConverters; @Controller -> view name resolved by Thymeleaf into HTML.
                7. If an exception is thrown, @ExceptionHandler methods in @RestControllerAdvice build the error response.
                8. The response goes back through the filters to the client.

                Say it in an interview as: request -> filter -> DispatcherServlet -> controller -> service -> repository -> database, and back.""");

        n(l, "JPA and Hibernate Essentials", "Spring Boot", "Java", M, true, "jpa,hibernate,orm,interview",
                """
                Entity lifecycle: transient (new) -> managed (persisted, tracked) -> detached (session closed) -> removed.
                Dirty checking: changes to a managed entity are written automatically at commit - no save() needed inside a transaction.
                Fetch types: LAZY loads related data on first access (default for collections), EAGER loads immediately (default for @ManyToOne). Prefer LAZY.
                LazyInitializationException: touching a lazy relation outside a transaction/session. Fix with JOIN FETCH, DTO projections or @Transactional.
                N+1 problem: 1 query for parents + N queries for children. Fix with JOIN FETCH, @EntityGraph or batch size.
                Cascade: propagate operations (PERSIST, REMOVE) from parent to children; orphanRemoval deletes children removed from the collection.
                equals/hashCode: base them on a business key, not a generated id that is null before saving.
                ddl-auto: none / validate / update / create / create-drop - use none or validate with Flyway in production.
                Enum mapping: always EnumType.STRING.""");

        n(l, "Java Concurrency Basics", "Concurrency", "Java", H, true, "java,threads,concurrency,interview",
                """
                Thread vs Runnable: implement Runnable (or use an ExecutorService); do not extend Thread unless needed.
                Race condition: two threads read-modify-write shared data. Fix with synchronized, locks, atomics or immutable data.
                synchronized: one thread at a time per monitor; also guarantees visibility. volatile: visibility only (no atomicity).
                Atomic classes (AtomicInteger, LongAdder): lock-free counters using compare-and-set.
                ExecutorService: reuse a pool of threads - Executors.newFixedThreadPool(n); always shutdown().
                CompletableFuture: compose asynchronous tasks (supplyAsync, thenApply, thenCombine, join).
                Concurrent collections: ConcurrentHashMap, CopyOnWriteArrayList, BlockingQueue (producer/consumer).
                Deadlock needs four conditions (mutual exclusion, hold and wait, no pre-emption, circular wait) - break one, e.g. always acquire locks in the same order.
                Virtual threads (Java 21): cheap threads for blocking I/O code.
                Interview line: 'shared mutable state is the problem; I either avoid sharing, avoid mutating, or protect it'.""");

        n(l, "Java 8 to 21: Features Worth Knowing", "Java", "Java", M, false, "java,modern-java,features",
                """
                Java 8:  lambdas, streams, Optional, method references, default methods, java.time.
                Java 9:  List.of / Map.of, private interface methods, modules.
                Java 10: var for local variables.
                Java 14-17: switch expressions, text blocks (multi-line strings), records, pattern matching for instanceof, sealed classes.
                Java 21 (LTS): virtual threads, sequenced collections, record patterns, pattern matching for switch.

                Examples
                  var names = List.of("a", "b");
                  record Point(int x, int y) {}
                  String s = switch (day) { case SAT, SUN -> "weekend"; default -> "weekday"; };
                  if (obj instanceof String str && !str.isBlank()) { ... }

                Interviews usually ask: functional interfaces (Function, Predicate, Supplier, Consumer), stream pipeline vs loop, Optional usage rules, records vs classes.""");

        n(l, "String Handling in Java: Key Facts", "Java", "Java", E, true, "java,strings,interview",
                """
                Strings are immutable: every 'change' creates a new object. That is why s += x in a loop is O(n^2) - use StringBuilder (not thread-safe, fast) or StringBuffer (synchronised).
                String pool: literals with the same text share one object. new String("a") creates a separate object. == compares references; equals compares content - always use equals.
                intern() puts a string in the pool.
                Useful methods: length, charAt, substring, indexOf, contains, startsWith, split, trim/strip, replace, toCharArray, equalsIgnoreCase, compareTo, join, repeat, isBlank.
                Compare ignoring case with equalsIgnoreCase. Convert to number with Integer.parseInt (throws NumberFormatException).
                Why String is a good HashMap key: immutable and its hashCode is cached.
                Text blocks (Java 15+) give multi-line strings. Use String.format or formatted() for templates.""");

        n(l, "Exception Handling Best Practices", "Java", "Java", M, false, "java,exceptions,best-practices",
                """
                Checked exceptions (IOException, SQLException): the compiler forces you to handle or declare them - use for recoverable conditions the caller can act on.
                Unchecked exceptions (RuntimeException subclasses): programming errors or business rule violations - IllegalArgumentException, IllegalStateException, custom NotFoundException.
                Catch the most specific type; never leave a catch block empty; never catch Throwable.
                Preserve the cause: throw new ServiceException("message", e).
                Use try-with-resources for anything Closeable (files, connections).
                Do not use exceptions for normal control flow.
                Fail fast: validate arguments at the start of a method.
                In web apps translate exceptions to HTTP responses in ONE place (@RestControllerAdvice) and log details server-side only.
                Never return stack traces to users.""");

        n(l, "REST vs GraphQL vs gRPC", "Architecture", "HTTP", M, false, "rest,graphql,grpc,architecture",
                """
                REST     resources + HTTP verbs, JSON, cacheable, simple, great default. Weakness: over-fetching/under-fetching, many round trips.
                GraphQL  one endpoint; the client asks for exactly the fields it needs. Good for complex front ends. Weakness: caching, query cost control, more server complexity.
                gRPC     binary Protocol Buffers over HTTP/2, strongly typed contracts, streaming, very fast. Good for service-to-service calls. Weakness: not browser-friendly without a proxy.

                Choose REST for public APIs and CRUD apps, GraphQL for flexible client-driven UIs, gRPC for internal microservice traffic with strict performance needs.""");

        n(l, "Microservices vs Monolith: Honest Trade-offs", "Architecture", "Java", H, false, "architecture,microservices,monolith",
                """
                Monolith: one deployable unit. Easy to build, debug, test and deploy; one database transaction. Becomes hard to scale teams and parts independently. A well-structured modular monolith is the right start for most projects (DevVault is one).
                Microservices: many small services, each with its own data. Independent deployment and scaling; fits large organisations. Costs: network failures, distributed transactions (sagas), eventual consistency, observability, deployment complexity.

                Rule: do not split until you feel a concrete pain (team size, scaling hot spots, release coupling).
                Patterns you should name: API gateway, service discovery, circuit breaker, saga, event-driven messaging (Kafka/RabbitMQ), centralised logging and tracing.""");

        n(l, "Caching Strategies", "System Design", "Java", M, true, "caching,redis,system-design",
                """
                Cache-aside (lazy loading): app checks the cache, on a miss loads from the database and stores it. Most common.
                Read-through: the cache itself loads data on a miss.
                Write-through: write to cache and database together (consistent, slower writes).
                Write-behind: write to cache, flush to the database later (fast, risk of loss).
                Eviction: LRU (least recently used), LFU, TTL (time to live).
                Problems: stale data (set a TTL, invalidate on write), cache stampede (many requests reload the same key - use locking or request coalescing), cache penetration (requests for missing keys - cache the 'not found' result briefly or use a Bloom filter).
                What to cache: data read often and changed rarely, expensive computations, session data.
                Where: in-process (Caffeine), distributed (Redis, Memcached), CDN for static files, HTTP caching headers (ETag, Cache-Control).""");

        n(l, "System Design Interview Framework", "System Design", "Text", H, true, "system-design,interview,framework",
                """
                1. Clarify requirements: functional (what it does) and non-functional (scale, latency, availability, consistency).
                2. Estimate: users, requests per second, data size per day/year, read:write ratio.
                3. API: list the main endpoints and request/response shapes.
                4. Data model: tables/collections, keys, indexes; SQL vs NoSQL and why.
                5. High-level design: client, load balancer, app servers, cache, database, queue, CDN, object storage.
                6. Deep dive on the bottleneck: sharding, replication, caching, rate limiting, unique ids.
                7. Trade-offs and failure handling: what if a server/database/zone fails? consistency vs availability (CAP).
                8. Monitoring: metrics, logs, alerts.

                Practise on: URL shortener, rate limiter, news feed, chat app, notification system, file storage, typeahead, ride sharing, web crawler.""");

        n(l, "CAP Theorem and Consistency", "System Design", "Text", H, false, "system-design,cap,distributed",
                """
                In a distributed system with network Partitions you must choose between Consistency (every read sees the latest write) and Availability (every request gets a response).
                CP systems (e.g. ZooKeeper, HBase, MongoDB defaults) refuse some requests during a partition to stay consistent.
                AP systems (e.g. Cassandra, DynamoDB) keep answering but may return stale data (eventual consistency).
                Without partitions you can have both; PACELC adds the latency vs consistency trade-off during normal operation.

                Consistency models: strong, eventual, causal, read-your-writes.
                Practical advice: pick the consistency per feature - bank balance needs strong, a 'likes' counter can be eventual.""");

        n(l, "Docker Commands Cheat Sheet", "DevOps", "Bash", E, true, "docker,devops,cheatsheet",
                """
                docker build -t devvault .                 build an image from the Dockerfile
                docker run -d -p 8080:8080 --name dv devvault   start a container in the background and map the port
                docker ps / docker ps -a                   running / all containers
                docker logs -f dv                          follow the logs
                docker exec -it dv sh                      open a shell inside the container
                docker stop dv && docker rm dv             stop and remove
                docker images / docker rmi <image>         list / delete images
                docker compose up -d / down                start/stop a multi-container stack
                docker system prune                        remove unused data (be careful)

                Dockerfile tips: copy dependency files before source for layer caching, use multi-stage builds, run as non-root, set SPRING_PROFILES_ACTIVE and JVM memory flags through ENV, never bake secrets into images.""");

        n(l, "Linux Command Cheat Sheet for Developers", "DevOps", "Bash", E, false, "linux,bash,cheatsheet",
                """
                ls -la, cd, pwd, mkdir -p, rm -r, cp, mv, cat, less, head, tail -f
                grep -rn "text" .          search text recursively with line numbers
                find . -name "*.log"       find files
                chmod +x script.sh         make executable;   chown user file
                ps aux | grep java         find a process;   kill -9 <pid>
                lsof -i :8080 / ss -tulpn  who is using a port
                curl -i https://example.com    HTTP request with headers
                df -h / du -sh *           disk usage;   free -m memory;   top / htop CPU
                tar -czf a.tar.gz dir/     compress;   tar -xzf a.tar.gz   extract
                | pipe, > redirect output, >> append, 2>&1 merge errors, && run next if success
                Example: awk '{print $1}' access.log | sort | uniq -c | sort -nr | head   (top clients in a log)""");

        n(l, "Regex Cheat Sheet", "Strings", "Java", M, false, "regex,strings,cheatsheet",
                """
                .  any character         \\d digit   \\w word char   \\s whitespace      \\D \\W \\S the negations
                ^  start   $ end         \\b word boundary
                *  0 or more   +  1 or more   ?  0 or 1   {n}  exactly n   {n,m}  between n and m
                [abc] one of   [^abc] none of   [a-z] range   (a|b) either   (group)  capture   (?:group) no capture
                Lazy quantifiers: *? +?   Lookahead: (?=...)  (?!...)

                Java: Pattern p = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{2,15}$"); p.matcher(text).matches();
                In Java strings escape the backslash: "\\\\d+" means the regex \\d+.
                Compile a Pattern once and reuse it. Beware catastrophic backtracking with nested quantifiers like (a+)+.""");

        n(l, "Behavioural Interview: STAR Stories to Prepare", "Interview", "Text", E, true, "interview,behavioural,star",
                """
                Prepare 6 stories, 2 minutes each, using Situation - Task - Action - Result (+ what you learned):
                1. A hard bug you debugged (how you reproduced it, what you found, how you prevented repeats).
                2. A disagreement with a teammate and how you resolved it.
                3. A deadline or pressure situation and how you prioritised.
                4. A mistake you made and what you changed afterwards.
                5. Something you learned very quickly.
                6. A project you are proud of and the hardest technical decision in it.

                Rules: say 'I' (not 'we') for your own actions, include a number in the result (time saved, tests added, users reached), keep it honest, end with the lesson.
                Questions to ask them: how is success measured in the first 90 days, how does the team review code, what is the biggest technical challenge right now.""");

        n(l, "Resume Tips for Java Developers", "Career", "Text", E, true, "resume,career,recruiters",
                """
                One page for fewer than 5 years of experience. Order: summary (2 lines), skills, projects, experience, education.
                Put technologies in a scannable list: Java 21, Spring Boot, JPA/Hibernate, MySQL, REST, Docker, Git, JUnit/Mockito.
                Each project: name + stack, one line on what it does, 2-3 bullets starting with a verb and ending in a result ('Cut query time from 900 ms to 60 ms by adding a composite index').
                Add links: GitHub with a clear README and a LIVE demo link.
                Do not list skills you cannot discuss for five minutes.
                Recruiters search for keywords: match the job description honestly (Spring Boot, microservices, SQL, REST, CI/CD).
                Proofread, use consistent dates, export as PDF, name the file FirstName_LastName_Resume.pdf.""");

        n(l, "How to Present a Project in an Interview", "Career", "Text", M, true, "interview,project,career",
                """
                Use this 4-part structure (about three minutes):
                1. Problem: what it solves and for whom (one sentence).
                2. Solution and demo: show the live link and the two best features.
                3. Technical decisions: architecture (controller -> service -> repository), why Spring Boot, why MySQL locally and H2 for the free demo, how validation and errors are handled.
                4. Quality and next steps: tests (JUnit, Mockito, MockMvc), Docker deployment, and what you would improve (authentication, Flyway migrations, caching, integration tests).

                Prepare answers to: what was the hardest bug, what would you change, how would it scale to 1 million users, how do you prevent SQL injection and XSS, how do you test it.
                Always be ready to open the code and explain any file.""");

        n(l, "Common Java Interview Questions: One-Line Answers", "Java", "Java", M, true, "java,interview,quick-review",
                """
                JDK vs JRE vs JVM: JDK = tools + JRE; JRE = libraries + JVM; JVM executes bytecode.
                == vs equals(): == compares references (or primitives), equals() compares content.
                final / finally / finalize: constant or non-overridable / always-run block / deprecated cleanup hook.
                Stack vs heap: stack holds method frames and local variables; heap holds objects (garbage collected).
                Abstract class vs interface: state + partial code vs pure contract.
                ArrayList vs LinkedList: array-backed fast access vs node-based fast insertion at known positions.
                HashMap vs ConcurrentHashMap: not thread-safe vs thread-safe with fine-grained locking.
                Checked vs unchecked exception: must be handled vs optional.
                String vs StringBuilder: immutable vs mutable.
                Garbage collection: unreachable objects are reclaimed automatically; G1 is the default collector.
                Pass by value: Java always passes copies (of primitives or of references).
                Why is main static: the JVM must call it without creating an object.""");

        n(l, "Testing Pyramid and Practices", "Testing", "Java", M, false, "testing,junit,mockito,best-practices",
                """
                Pyramid: many fast unit tests, fewer integration tests, very few end-to-end tests.
                Unit test: one class in isolation, dependencies mocked (JUnit 5 + Mockito + AssertJ).
                Slice test: @WebMvcTest (controllers), @DataJpaTest (repositories) - load only the part you test.
                Integration test: @SpringBootTest with a real database (Testcontainers).
                Name tests by behaviour: getProblemById_throwsWhenMissing.
                Arrange - Act - Assert. One reason to fail per test.
                Test the failure paths: validation errors, not-found, conflicts.
                Do not mock what you do not own without care; do not test getters/setters.
                Keep tests independent and deterministic: inject the clock, avoid sleeping, no shared state.
                Coverage is a guide, not a goal: 80% of meaningful behaviour beats 100% of trivial lines.""");

        n(l, "Security Basics Every Backend Developer Must Know", "Security", "Java", M, true, "security,owasp,web",
                """
                SQL injection: never build SQL from strings; use parameterised queries / JPA parameters.
                XSS: escape output (Thymeleaf th:text does), never insert user data with innerHTML or th:utext; set a Content-Security-Policy.
                CSRF: for cookie-based sessions use CSRF tokens (Spring Security enables this by default).
                Authentication vs authorisation: who are you vs what may you do. Check permissions on the SERVER for every request.
                Passwords: hash with bcrypt/argon2 (never plain text, never fast hashes like MD5/SHA-1 alone).
                Secrets: environment variables or a vault, never in Git.
                HTTPS everywhere; secure, HttpOnly, SameSite cookies.
                Validate input, limit sizes, rate limit login and APIs.
                Dependencies: keep updated, scan for known vulnerabilities (OWASP Dependency-Check, Dependabot).
                Error messages: generic to the user, detailed in server logs.""");

        n(l, "Recursion Template and Common Mistakes", "Algorithms", "Java", M, true, "recursion,backtracking,templates",
                """
                Every recursive function needs: (1) a base case that stops, (2) a recursive step that moves toward it.

                Backtracking template:
                  void solve(state, choices, path, result) {
                      if (isComplete(state)) { result.add(copy(path)); return; }
                      for (choice : choices) {
                          if (!isValid(choice)) continue;     // prune
                          path.add(choice);                    // choose
                          solve(next state, ...);              // explore
                          path.remove(last);                   // undo
                      }
                  }

                Mistakes: forgetting to copy the path before adding to the result, missing the undo step, no base case (StackOverflowError), recomputing the same sub-problems (add memoisation -> dynamic programming).
                Complexity hints: subsets 2^n, permutations n!, combinations C(n, k).""");

        n(l, "Dynamic Programming Recipe", "Algorithms", "Java", H, true, "dp,recipe,templates",
                """
                1. Can the problem be split into smaller identical problems that repeat? Is it asking for count / min / max / true-false?
                2. Define the state in ONE sentence: dp[i] = best answer using the first i items; dp[i][j] = ... for prefixes i and j.
                3. Write the transition: how dp[i] comes from earlier states (take or skip, extend, match or mismatch).
                4. Set base cases (empty input, first element).
                5. Pick the order so dependencies are computed first (increasing i, or by interval length).
                6. Return the right cell, then reduce memory if only the last row/few values are needed.

                Families: 1D (climbing stairs, house robber), knapsack (subset sum, coin change), strings (LCS, edit distance), grid paths, intervals (burst balloons), state machines (stock problems), bitmask.
                Top-down = recursion + memo (easy to write); bottom-up = table (faster, no recursion depth issues).""");

        n(l, "Graph Algorithms Quick Reference", "Algorithms", "Java", H, true, "graphs,bfs,dfs,dijkstra,reference",
                """
                BFS (queue): shortest path in unweighted graphs, level order, multi-source spreading (rotting oranges). O(V + E).
                DFS (recursion/stack): explore all, connected components, cycle detection, topological sort, backtracking on graphs. O(V + E).
                Dijkstra (priority queue): shortest paths with NON-NEGATIVE weights. O(E log V).
                Bellman-Ford: handles negative weights, detects negative cycles, supports 'at most k edges'. O(VE).
                Floyd-Warshall: all-pairs shortest paths. O(V^3).
                Topological sort (Kahn: indegree queue; or DFS post-order): ordering of a DAG; leftover nodes mean a cycle.
                Union-Find: connected components, Kruskal's minimum spanning tree, redundant edge detection. ~O(alpha(n)).
                Prim: minimum spanning tree with a priority queue.
                Tarjan: bridges and articulation points, strongly connected components.

                Representation: adjacency list (sparse graphs, most problems), adjacency matrix (dense graphs, O(1) edge test), edge list (Kruskal, Bellman-Ford).
                Grids are implicit graphs: neighbours = 4 or 8 directions.""");

        n(l, "Bit Manipulation Tricks", "Algorithms", "Java", M, false, "bits,tricks,cheatsheet",
                """
                n & 1                 last bit (odd test)         n >> 1  divide by 2         n << 1  multiply by 2
                n & (n - 1)           clears the lowest set bit   (power of two test: n > 0 && (n & (n - 1)) == 0)
                n & -n                isolates the lowest set bit (used in Fenwick trees)
                a ^ a = 0, a ^ 0 = a  XOR cancels pairs           (single number problem)
                x | (1 << i)          set bit i     x & ~(1 << i) clear bit i     x ^ (1 << i) toggle bit i
                (x >> i) & 1          read bit i
                Integer.bitCount(x)   number of set bits          Integer.toBinaryString(x)
                Subsets of n items    loop mask from 0 to (1 << n) - 1; item i is in the subset when (mask >> i) & 1
                Beware: >> keeps the sign, >>> fills with zeros; ints are 32-bit, use 1L << i for longs.""");

        n(l, "Time Management Plan: 90 Days to Interview Ready", "Career", "Text", E, true, "plan,preparation,career",
                """
                Weeks 1-2: Java and OOP fundamentals - finish the Foundations and Object-Oriented chapters of the Learn path. Solve 2 beginner problems a day.
                Weeks 3-4: Collections, Big-O, arrays, strings, hash maps, two pointers, sliding window. 2 problems a day (beginner then intermediate).
                Weeks 5-6: Stack, queue, linked list, trees, heaps, binary search. Re-solve problems you missed.
                Weeks 7-8: Graphs, recursion/backtracking, dynamic programming. Start intermediate problems without hints (attempt first!).
                Weeks 9-10: SQL, Spring Boot, JPA, REST, testing. Rebuild a small feature in your own project and write tests.
                Weeks 11-12: System design basics, hard problems, mock interviews, resume and project pitch, behavioural stories.
                Daily habits: attempt before reading the solution, write the pattern in a note, revisit 'needs revision' problems every Sunday.
                Track progress with the practice status on each problem and the dashboard.""");

        n(l, "Code Review Checklist", "Engineering", "Java", M, false, "code-review,quality,engineering",
                """
                Correctness: does it solve the requirement, including edge cases (null, empty, large, negative, duplicates)?
                Readability: clear names, small methods, comments explain WHY not WHAT.
                Design: each class has one job; business rules are in services; no duplicated logic.
                Errors: exceptions are specific and handled in one place; resources closed; no swallowed exceptions.
                Security: input validated, queries parameterised, secrets not in code, permissions checked.
                Performance: no queries inside loops (N+1), no unnecessary copies, right data structure.
                Tests: new behaviour and failure paths are tested; tests are readable and independent.
                API: backwards compatible, consistent naming and status codes.
                Review style: ask questions, suggest alternatives, praise good work, separate must-fix from nice-to-have.""");

        n(l, "Drills Strategy: Predicting Output", "Practice", "Java", E, false, "drills,output,strategy",
                """
                Predict-the-output method
                1. Write down every variable and update it line by line like a trace table.
                2. Watch the traps: integer division, i++ vs ++i, == on objects, char + int, overflow, switch fall-through.
                3. Say the answer out loud before you press Check answer.
                4. If you were wrong, find the exact line where your table and the real value split, then retry a similar drill.""");

        n(l, "Drills Strategy: Dynamic Programming Questions", "Practice", "Java", M, false, "drills,dp",
                """
                Solving DP drills by hand
                - Define what table[i] means in one sentence before filling anything.
                - Write the base case (table[0]) first.
                - Fill from small to large; each cell uses only already-filled cells.
                - Coins: best[a] = 1 + min(best[a - coin]). LIS: dp[i] = 1 + max(dp[j]) for smaller a[j]. Knapsack: best[c] = max(best[c], best[c-w] + v).
                - Stairs/ways: ways[i] = sum of ways[i - step].""");

        n(l, "Quick Maths for Interviews", "Math", "Java", M, false, "math,gcd,modular,cheatsheet",
                """
                Formulas worth memorising
                - gcd(a, b) = gcd(b, a % b); lcm = a / gcd * b
                - C(n, k) = n! / (k! (n-k)!); subsets of n items = 2^n; handshakes = n(n-1)/2
                - (a * b) mod m = ((a mod m) * (b mod m)) mod m
                - Number of 1 bits: n & (n - 1) removes the lowest set bit
                - Highest power of two <= n: Integer.highestOneBit(n)
                - Sum 1..n = n(n+1)/2; trailing zeros of n! = n/5 + n/25 + ...""");

        n(l, "HTTP Status Codes Cheat Sheet", "Web APIs", "Java", E, false, "http,status,cheatsheet",
                """
                2xx success: 200 OK, 201 Created, 204 No Content
                3xx redirects: 301 Moved Permanently, 304 Not Modified
                4xx caller's fault: 400 Bad Request, 401 Not authenticated, 403 Not allowed, 404 Not Found, 405 Wrong method, 409 Conflict, 422 Validation failed, 429 Too many requests
                5xx server's fault: 500 Internal error, 502 Bad gateway, 503 Unavailable, 504 Gateway timeout
                Rule: 401 = who are you? 403 = I know you, but no.""");

        n(l, "Caching and Rate Limiting Basics", "Systems Design", "Java", M, false, "cache,lru,rate-limit",
                """
                LRU: evict the least recently used key; hits and inserts both refresh recency (LinkedHashMap with accessOrder, or HashMap + doubly linked list).
                FIFO: evict the oldest inserted; a hit does not change order.
                Fixed-window rate limit: counter per window (time / windowSize); simple but allows bursts at window edges.
                Sliding window / token bucket smooth that out.
                Always return 429 with a Retry-After header when the limit is hit.""");

        return l;
    }
}
