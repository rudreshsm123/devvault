package com.devvault.config.seed;

import com.devvault.entity.CodeSnippet;
import com.devvault.entity.SnippetLanguage;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.entity.SnippetLanguage.*;

/** Reusable snippets in many languages; each one carries a plain-English "meaning". */
public final class SnippetSeedData {

    private SnippetSeedData() {
    }

    private static void s(List<CodeSnippet> out, SnippetLanguage lang, String title, String description,
                          String code, String meaning, String tags) {
        CodeSnippet c = new CodeSnippet();
        c.setLanguage(lang);
        c.setTitle(title);
        c.setDescription(description);
        c.setCode(code.strip());
        c.setMeaning(meaning.strip());
        c.setTags(tags);
        out.add(c);
    }

    public static List<CodeSnippet> all() {
        List<CodeSnippet> l = new ArrayList<>();

        // ---------- JAVA ----------
        s(l, JAVA, "Read a file line by line", "Safely read a text file with try-with-resources.",
                """
                try (BufferedReader br = Files.newBufferedReader(Path.of("data.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }""",
                """
                try (...) opens the file and GUARANTEES it is closed when the block ends, even if an error happens.
                Files.newBufferedReader(...) opens data.txt for reading text efficiently.
                readLine() returns the next line, or null at the end - the while loop runs until there is nothing left.
                catch (IOException) handles problems such as 'file not found'.""",
                "java,io,files");

        s(l, JAVA, "Stream: filter, map, collect", "Transform a list in a pipeline.",
                """
                List<String> names = people.stream()
                        .filter(p -> p.getAge() >= 18)
                        .map(Person::getName)
                        .sorted()
                        .collect(Collectors.toList());""",
                """
                stream() turns the list into a pipeline of items.
                filter keeps only adults; map replaces each Person with just their name.
                sorted puts the names in alphabetical order; collect gathers them into a new list.
                The original list is never modified.""",
                "java,streams,lambda");

        s(l, JAVA, "Thread-safe singleton", "One shared instance created lazily and safely.",
                """
                public final class Config {
                    private Config() {}
                    private static class Holder { static final Config INSTANCE = new Config(); }
                    public static Config getInstance() { return Holder.INSTANCE; }
                }""",
                """
                The private constructor stops other code from calling new Config().
                The inner Holder class is only loaded the first time getInstance() runs, and the JVM guarantees class loading is thread-safe.
                So you get lazy creation + thread safety without locks.""",
                "java,design-pattern,singleton");

        s(l, JAVA, "Spring REST controller", "A minimal JSON endpoint.",
                """
                @RestController
                @RequestMapping("/api/notes")
                public class NoteController {
                    @GetMapping("/{id}")
                    public Note get(@PathVariable Long id) {
                        return service.getNoteById(id);
                    }
                }""",
                """
                @RestController: every method returns data (JSON) instead of an HTML page.
                @RequestMapping: all URLs in this class start with /api/notes.
                @GetMapping("/{id}"): handles GET /api/notes/5 and puts 5 in the id variable (@PathVariable).""",
                "java,spring,rest");

        // ---------- PYTHON ----------
        s(l, PYTHON, "List comprehension", "Build a list in one line.",
                "squares = [n * n for n in range(10) if n % 2 == 0]",
                """
                Read it as: 'for each n from 0 to 9, if n is even, put n*n in the list'.
                Result: [0, 4, 16, 36, 64].
                It replaces a for loop with an append.""",
                "python,list,basics");

        s(l, PYTHON, "Read JSON file", "Load JSON into a dict.",
                """
                import json

                with open("data.json", encoding="utf-8") as f:
                    data = json.load(f)
                print(data["name"])""",
                """
                 'with open(...)' opens the file and closes it automatically.
                json.load turns the JSON text into Python dicts/lists.
                After that data["name"] works like a normal dictionary lookup.""",
                "python,json,files");

        s(l, PYTHON, "Decorator that times a function", "Wrap a function to measure its run time.",
                """
                import time, functools

                def timed(fn):
                    @functools.wraps(fn)
                    def wrapper(*args, **kwargs):
                        start = time.perf_counter()
                        result = fn(*args, **kwargs)
                        print(f"{fn.__name__} took {time.perf_counter() - start:.4f}s")
                        return result
                    return wrapper

                @timed
                def work(): sum(range(10**6))""",
                """
                A decorator is a function that takes a function and returns an upgraded version of it.
                wrapper runs the original (fn) between two clock readings and prints the difference.
                @timed above 'work' is shorthand for work = timed(work).
                functools.wraps keeps the original name/docstring.""",
                "python,decorator,performance");

        s(l, PYTHON, "Count words with Counter", "Most common words in a text.",
                """
                from collections import Counter

                words = text.lower().split()
                print(Counter(words).most_common(3))""",
                """
                Counter is a dictionary that counts things automatically.
                most_common(3) returns the three most frequent items with their counts, e.g. [('the', 12), ('a', 7), ('to', 5)].""",
                "python,collections,counting");

        // ---------- JAVASCRIPT ----------
        s(l, JAVASCRIPT, "Fetch JSON with async/await", "Call an API and handle errors.",
                """
                async function loadUser(id) {
                  const res = await fetch(`/api/users/${id}`);
                  if (!res.ok) throw new Error(`HTTP ${res.status}`);
                  return res.json();
                }""",
                """
                async marks a function that can pause while waiting.
                await fetch(...) waits for the server response without freezing the page.
                res.ok is false for 404/500 - fetch does NOT throw for those, so you must check it yourself.
                res.json() parses the body as JSON.""",
                "javascript,fetch,async");

        s(l, JAVASCRIPT, "Debounce", "Run a function only after the user stops typing.",
                """
                function debounce(fn, delay = 300) {
                  let timer;
                  return (...args) => {
                    clearTimeout(timer);
                    timer = setTimeout(() => fn(...args), delay);
                  };
                }
                input.addEventListener('input', debounce(search, 400));""",
                """
                Every keystroke cancels the previous timer and starts a new one.
                Only when 400 ms pass with no keystroke does the real function run.
                Used for search boxes so you call the API once instead of on every letter.""",
                "javascript,performance,events");

        s(l, JAVASCRIPT, "Array map / filter / reduce", "The three array workhorses.",
                """
                const cart = [{p: 10, q: 2}, {p: 5, q: 1}];
                const total = cart
                  .filter(i => i.q > 0)
                  .map(i => i.p * i.q)
                  .reduce((sum, x) => sum + x, 0);   // 25""",
                """
                filter keeps matching items, map transforms each item, reduce folds everything into one value.
                The 0 at the end is the starting value of the sum.
                None of them change the original array.""",
                "javascript,array,functional");

        s(l, JAVASCRIPT, "Event delegation", "One listener for many dynamic elements.",
                """
                document.querySelector('#list').addEventListener('click', (e) => {
                  const btn = e.target.closest('button[data-id]');
                  if (!btn) return;
                  console.log('delete', btn.dataset.id);
                });""",
                """
                Instead of attaching a listener to every button, attach ONE to the parent.
                Clicks 'bubble up', so the parent hears them.
                closest(...) finds which button was clicked; works even for buttons added later.""",
                "javascript,dom,events");

        // ---------- TYPESCRIPT ----------
        s(l, TYPESCRIPT, "Typed API response", "Describe the shape of data.",
                """
                interface User { id: number; name: string; email?: string }

                async function getUser(id: number): Promise<User> {
                  const res = await fetch(`/api/users/${id}`);
                  return (await res.json()) as User;
                }""",
                """
                An interface is a contract: a User must have id and name; email is optional (?).
                Promise<User> says 'this function will eventually give you a User'.
                The editor now warns you if you use user.nme by mistake.""",
                "typescript,interface,types");

        s(l, TYPESCRIPT, "Union type + type guard", "Handle several shapes safely.",
                """
                type Shape = { kind: 'circle'; r: number } | { kind: 'rect'; w: number; h: number };

                function area(s: Shape): number {
                  switch (s.kind) {
                    case 'circle': return Math.PI * s.r ** 2;
                    case 'rect':   return s.w * s.h;
                  }
                }""",
                """
                A Shape is EITHER a circle OR a rectangle, told apart by the 'kind' field.
                Inside each case TypeScript knows exactly which fields exist, so s.r is allowed only for circles.
                If you add a new shape and forget a case, the compiler tells you.""",
                "typescript,union,switch");

        s(l, TYPESCRIPT, "Generic function", "Reusable code that keeps types.",
                """
                function first<T>(items: T[]): T | undefined {
                  return items[0];
                }
                const n = first([1, 2, 3]);        // number | undefined
                const s = first(['a', 'b']);       // string | undefined""",
                """
                <T> is a placeholder for 'whatever type you pass in'.
                first works for numbers, strings, users... and the result type matches the input type.
                | undefined is there because the array may be empty.""",
                "typescript,generics");

        // ---------- SQL ----------
        s(l, SQL, "INNER JOIN with GROUP BY", "Orders per customer.",
                """
                SELECT c.name, COUNT(o.id) AS orders, SUM(o.total) AS spent
                FROM customers c
                JOIN orders o ON o.customer_id = c.id
                GROUP BY c.id, c.name
                HAVING SUM(o.total) > 1000
                ORDER BY spent DESC;""",
                """
                JOIN connects each order to its customer using the matching id.
                GROUP BY collapses all rows of one customer into one result row.
                COUNT and SUM calculate per customer; HAVING filters AFTER grouping (WHERE filters before).
                ORDER BY shows the biggest spenders first.""",
                "sql,join,aggregate");

        s(l, SQL, "Second highest salary", "Classic interview query.",
                """
                SELECT MAX(salary) AS second_highest
                FROM employees
                WHERE salary < (SELECT MAX(salary) FROM employees);""",
                """
                The inner query finds the highest salary.
                The outer query then looks only at salaries lower than that and takes the maximum - which is the second highest.""",
                "sql,subquery,interview");

        s(l, SQL, "Window function: rank within group", "Top earner per department.",
                """
                SELECT * FROM (
                  SELECT name, dept, salary,
                         RANK() OVER (PARTITION BY dept ORDER BY salary DESC) AS rnk
                  FROM employees
                ) t
                WHERE rnk = 1;""",
                """
                PARTITION BY dept restarts the ranking for every department.
                ORDER BY salary DESC gives rank 1 to the highest paid.
                You wrap it in a subquery because you cannot filter on a window function in the same SELECT.""",
                "sql,window-function,rank");

        s(l, SQL, "Find duplicate emails", "Detect repeated values.",
                """
                SELECT email, COUNT(*) AS times
                FROM users
                GROUP BY email
                HAVING COUNT(*) > 1;""",
                """
                Group identical emails together and count each group.
                HAVING keeps only groups that appear more than once = the duplicates.""",
                "sql,duplicates,group-by");

        // ---------- HTML ----------
        s(l, HTML, "Accessible sign-up form", "Labels, types and validation.",
                """
                <form action="/signup" method="post">
                  <label for="email">Email</label>
                  <input id="email" name="email" type="email" required autocomplete="email">
                  <label for="pw">Password</label>
                  <input id="pw" name="password" type="password" minlength="8" required>
                  <button type="submit">Create account</button>
                </form>""",
                """
                label for=... is connected to the input id, so clicking the text focuses the box (and screen readers read it).
                type="email" gives mobile keyboards an @ key and basic validation; required stops empty submission.
                method="post" keeps the password out of the URL.""",
                "html,forms,accessibility");

        s(l, HTML, "Responsive page skeleton", "Basic HTML5 document.",
                """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>My Page</title>
                </head>
                <body>
                  <header></header><main></main><footer></footer>
                </body>
                </html>""",
                """
                DOCTYPE tells the browser to use modern standards mode.
                lang helps screen readers and translation tools.
                The viewport meta makes the page scale correctly on phones.
                header/main/footer are semantic tags that describe the page structure.""",
                "html,template,responsive");

        // ---------- CSS ----------
        s(l, CSS, "Center with Flexbox", "Horizontally and vertically.",
                """
                .center {
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  min-height: 100vh;
                }""",
                """
                display:flex turns the box into a flexible row.
                justify-content centers along the main (horizontal) axis; align-items centers across it (vertical).
                min-height:100vh makes the box as tall as the screen so vertical centering is visible.""",
                "css,flexbox,layout");

        s(l, CSS, "Responsive card grid", "As many columns as fit.",
                """
                .grid {
                  display: grid;
                  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
                  gap: 16px;
                }""",
                """
                Each card is at least 240px wide and shares leftover space equally (1fr).
                auto-fit creates as many columns as fit the screen: 4 on desktop, 1 on a phone - no media query needed.
                gap adds spacing between cards.""",
                "css,grid,responsive");

        s(l, CSS, "Dark mode with CSS variables", "Theme using prefers-color-scheme.",
                """
                :root { --bg: #ffffff; --text: #1a1a1a; }
                @media (prefers-color-scheme: dark) {
                  :root { --bg: #121212; --text: #eaeaea; }
                }
                body { background: var(--bg); color: var(--text); }""",
                """
                Colors are stored as variables (--bg, --text) and used with var(...).
                When the user's operating system is in dark mode, the media query swaps the variable values - the whole site recolors from one place.""",
                "css,variables,dark-mode");

        // ---------- C ----------
        s(l, C, "Swap two numbers with pointers", "Pass by reference in C.",
                """
                #include <stdio.h>

                void swap(int *a, int *b) {
                    int tmp = *a;
                    *a = *b;
                    *b = tmp;
                }
                int main(void) {
                    int x = 1, y = 2;
                    swap(&x, &y);
                    printf("%d %d\\n", x, y);   // 2 1
                }""",
                """
                C copies arguments, so to change the caller's variables you pass their ADDRESSES (&x).
                *a means 'the value stored at address a'.
                swap reads and writes through the pointers, so the original variables change.""",
                "c,pointers,basics");

        s(l, C, "Dynamic array with malloc/free", "Allocate and release memory.",
                """
                int n = 5;
                int *arr = malloc(n * sizeof *arr);
                if (!arr) { perror("malloc"); return 1; }
                for (int i = 0; i < n; i++) arr[i] = i * i;
                free(arr);""",
                """
                malloc asks the OS for n integers on the heap; sizeof *arr keeps the size correct if the type changes.
                Always check for NULL (out of memory).
                free gives the memory back - forgetting it is a memory leak.""",
                "c,memory,malloc");

        // ---------- C++ ----------
        s(l, CPP, "Sort a vector with a lambda", "Custom ordering.",
                """
                #include <algorithm>
                #include <vector>

                std::vector<std::pair<std::string,int>> v = {{"a",3},{"b",1},{"c",2}};
                std::sort(v.begin(), v.end(),
                          [](const auto& x, const auto& y) { return x.second < y.second; });""",
                """
                std::sort reorders the range from begin() to end().
                The lambda [](...) {...} is a tiny unnamed function that decides which element comes first - here, by the number.
                Result order: b, c, a.""",
                "cpp,stl,lambda");

        s(l, CPP, "Smart pointer (RAII)", "Automatic memory management.",
                """
                #include <memory>

                auto conn = std::make_unique<Connection>("db://local");
                conn->query("SELECT 1");
                // no delete needed""",
                """
                unique_ptr owns the object and deletes it automatically when it goes out of scope - even if an exception is thrown.
                make_unique creates it safely.
                This pattern (RAII) is why modern C++ rarely needs raw new/delete.""",
                "cpp,memory,raii");

        // ---------- C# ----------
        s(l, CSHARP, "LINQ query", "Filter and project a list.",
                """
                var names = people
                    .Where(p => p.Age >= 18)
                    .OrderBy(p => p.Name)
                    .Select(p => p.Name)
                    .ToList();""",
                """
                LINQ lets you query collections like a database.
                Where filters, OrderBy sorts, Select picks only the names, ToList runs the query and stores the result.""",
                "csharp,linq,collections");

        s(l, CSHARP, "Async HTTP call", "HttpClient with await.",
                """
                using var http = new HttpClient();
                string json = await http.GetStringAsync("https://api.example.com/items");
                Console.WriteLine(json.Length);""",
                """
                await pauses this method until the download finishes, without blocking the thread.
                'using var' disposes the HttpClient when the scope ends.
                (In real apps reuse one HttpClient via IHttpClientFactory.)""",
                "csharp,async,http");

        // ---------- GO ----------
        s(l, GO, "Goroutines and WaitGroup", "Run work concurrently.",
                """
                var wg sync.WaitGroup
                for i := 1; i <= 3; i++ {
                    wg.Add(1)
                    go func(n int) {
                        defer wg.Done()
                        fmt.Println("worker", n)
                    }(i)
                }
                wg.Wait()""",
                """
                'go func(...)' starts a lightweight concurrent task (goroutine).
                WaitGroup counts running tasks: Add(1) before starting, Done() when finished, Wait() blocks until the count is zero.
                n is passed as an argument so each goroutine gets its own copy.""",
                "go,concurrency,goroutine");

        s(l, GO, "HTTP JSON handler", "Tiny web server.",
                """
                http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
                    w.Header().Set("Content-Type", "application/json")
                    json.NewEncoder(w).Encode(map[string]string{"status": "ok"})
                })
                log.Fatal(http.ListenAndServe(":8080", nil))""",
                """
                HandleFunc registers what to run for the URL /health.
                The handler writes {"status":"ok"} as JSON to the response.
                ListenAndServe starts the server on port 8080; log.Fatal prints the error if it cannot start.""",
                "go,http,json");

        // ---------- RUST ----------
        s(l, RUST, "Ownership and borrowing", "Why Rust has no garbage collector.",
                """
                fn length(s: &String) -> usize { s.len() }

                fn main() {
                    let name = String::from("devvault");
                    let n = length(&name);   // borrow
                    println!("{name} has {n} chars");
                }""",
                """
                A value has exactly one owner. Passing &name LENDS it (a reference) instead of moving it away.
                Because we only borrowed, name is still usable afterwards.
                The compiler checks this at compile time, so there are no dangling pointers.""",
                "rust,ownership,borrowing");

        s(l, RUST, "Result and the ? operator", "Propagate errors.",
                """
                use std::fs;

                fn read_name() -> Result<String, std::io::Error> {
                    let text = fs::read_to_string("name.txt")?;
                    Ok(text.trim().to_string())
                }""",
                """
                Result is either Ok(value) or Err(error) - Rust has no exceptions.
                '?' means: if this failed, return the error to the caller immediately; otherwise unwrap the value and continue.""",
                "rust,error-handling,result");

        // ---------- KOTLIN ----------
        s(l, KOTLIN, "Data class and null safety", "Concise models.",
                """
                data class User(val id: Long, val name: String, val email: String? = null)

                val u = User(1, "Asha")
                println(u.email?.uppercase() ?: "no email")""",
                """
                data class automatically gives equals, hashCode, toString and copy.
                String? means the value may be null; ?. calls uppercase only if it is not null; ?: supplies a default.""",
                "kotlin,data-class,null-safety");

        s(l, KOTLIN, "Collections: groupBy", "Group items by a key.",
                """
                val byLength = listOf("a", "bb", "cc", "ddd").groupBy { it.length }
                // {1=[a], 2=[bb, cc], 3=[ddd]}""",
                """
                groupBy sorts items into buckets by the value of the lambda (here: string length).
                'it' is the current element.
                The result is a Map from key to list.""",
                "kotlin,collections");

        // ---------- PHP ----------
        s(l, PHP, "Prepared statement (PDO)", "Prevent SQL injection.",
                """
                $stmt = $pdo->prepare('SELECT * FROM users WHERE email = :email');
                $stmt->execute(['email' => $_POST['email']]);
                $user = $stmt->fetch(PDO::FETCH_ASSOC);""",
                """
                The query is sent with a placeholder (:email) and the value is sent separately, so user input can never change the SQL itself.
                Never build SQL by gluing strings together with $_POST data.""",
                "php,pdo,security");

        // ---------- RUBY ----------
        s(l, RUBY, "Map, select and hashes", "Idiomatic Ruby collections.",
                """
                scores = { "asha" => 82, "ravi" => 55, "meena" => 91 }
                passed = scores.select { |_, s| s >= 60 }.keys.map(&:capitalize)
                # => ["Asha", "Meena"]""",
                """
                select keeps pairs whose score is at least 60.
                keys takes just the names; map(&:capitalize) calls capitalize on each name.
                Blocks ({ |a, b| ... }) are Ruby's version of lambdas.""",
                "ruby,hash,blocks");

        // ---------- SWIFT ----------
        s(l, SWIFT, "Optional binding", "Safely unwrap values.",
                """
                let input: String? = readLine()
                if let text = input, let age = Int(text) {
                    print("Next year: \\(age + 1)")
                } else {
                    print("Not a number")
                }""",
                """
                String? may hold a value or nothing (nil).
                'if let' unwraps only when a value exists; both steps must succeed (text exists AND converts to Int) before the first branch runs.""",
                "swift,optionals");

        // ---------- BASH ----------
        s(l, BASH, "Find and delete old log files", "Housekeeping one-liner.",
                "find /var/log/myapp -name '*.log' -mtime +30 -print -delete",
                """
                find searches the folder for files ending in .log.
                -mtime +30 means 'modified more than 30 days ago'.
                -print shows each match; -delete removes it. Run without -delete first to preview.""",
                "bash,find,cleanup");

        s(l, BASH, "Loop over files with error handling", "Safe script header.",
                """
                #!/usr/bin/env bash
                set -euo pipefail

                for f in ./data/*.csv; do
                  echo "Processing $f"
                  wc -l "$f"
                done""",
                """
                set -e stops the script at the first error; -u treats unset variables as errors; pipefail makes a failure inside a pipe count.
                The quotes around "$f" protect file names containing spaces.""",
                "bash,scripting,safety");

        s(l, BASH, "Count requests per IP from a log", "Classic text pipeline.",
                "awk '{print $1}' access.log | sort | uniq -c | sort -nr | head -10",
                """
                awk prints the first column (the IP).
                sort groups identical IPs together; uniq -c counts each group.
                sort -nr orders by count, highest first; head -10 shows the top ten.""",
                "bash,awk,logs");

        // ---------- JSON ----------
        s(l, JSON, "REST error response", "A consistent error body.",
                """
                {
                  "status": 400,
                  "error": "Validation failed",
                  "details": [
                    { "field": "email", "message": "must be a valid email" }
                  ],
                  "timestamp": "2026-10-10T10:15:30Z"
                }""",
                """
                A good API error says what class of problem happened (status/error), which fields were wrong (details) and when.
                Keeping the same shape for every error lets front-end code handle them in one place.""",
                "json,api,errors");

        // ---------- YAML ----------
        s(l, YAML, "Docker Compose: app + MySQL", "Local dev stack.",
                """
                services:
                  db:
                    image: mysql:8
                    environment:
                      MYSQL_DATABASE: devvault
                      MYSQL_ROOT_PASSWORD: example
                    ports: ["3306:3306"]
                  app:
                    build: .
                    ports: ["8080:8080"]
                    depends_on: [db]""",
                """
                Defines two containers: db (MySQL) and app (built from the local Dockerfile).
                environment configures the database name and password; ports maps container ports to your machine.
                depends_on starts the database first. Start everything with 'docker compose up'.""",
                "yaml,docker,compose");

        s(l, YAML, "GitHub Actions: build and test", "CI pipeline.",
                """
                name: CI
                on: [push, pull_request]
                jobs:
                  test:
                    runs-on: ubuntu-latest
                    steps:
                      - uses: actions/checkout@v4
                      - uses: actions/setup-java@v4
                        with: { distribution: temurin, java-version: 21 }
                      - run: ./mvnw -B verify""",
                """
                'on' says when it runs: every push and pull request.
                The job checks out the code, installs Java 21, then runs the Maven build with tests.
                If any step fails, the commit shows a red cross.""",
                "yaml,ci,github-actions");

        return l;
    }
}
