package com.devvault.config.seed;

import com.devvault.entity.Lesson;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.LessonFactory.add;

/** Chapter 8 (Spring Boot backend), chapter 9 (testing, Git, Docker) and chapter 10 (design and interview playbook). */
public final class LessonsBackendCareer {

    private LessonsBackendCareer() {
    }

    public static List<Lesson> all() {
        List<Lesson> l = new ArrayList<>();
        String c8 = "Spring Boot Backend (This Very Project)";
        String c8Intro = "Spring Boot is what most Java companies use for backends. These lessons walk through the real code of DevVault, layer by layer, so you can explain every annotation and design choice in your own project.";

        add(l, 8, c8, c8Intro, 1, "The Spring Boot Application and Project Layout",
                "You must be able to explain how a Spring Boot app starts and why the packages are arranged the way they are. It is the first thing a reviewer opens.",
                "Spring Boot is Spring plus auto-configuration and an embedded web server. @SpringBootApplication starts component scanning from its package downwards, creating beans (objects managed by Spring) for every @Component, @Service, @Repository and @Controller. Dependencies are passed through constructors (dependency injection). The pom.xml lists starters, ready-made bundles of dependencies.",
                "Java",
                """
                @SpringBootApplication
                public class DevvaultApplication {
                    public static void main(String[] args) {
                        SpringApplication.run(DevvaultApplication.class, args);
                    }
                }

                // com.devvault
                //   controller/  web pages and REST endpoints
                //   service/     business rules
                //   repository/  database access (Spring Data JPA)
                //   entity/      classes mapped to tables
                //   exception/   custom errors and the global handler
                //   config/      seeders and filters""",
                """
                @SpringBootApplication ==> Three annotations in one: configuration, auto-configuration (guesses beans from the libraries on the classpath) and component scanning.
                SpringApplication.run(...) ==> Creates the application context (the container of beans), starts embedded Tomcat and begins serving requests.
                package com.devvault ==> Scanning starts here, so every sub-package is found automatically. A class outside this package would be ignored.
                controller / service / repository ==> A layered architecture: each layer talks only to the one below it, which keeps responsibilities separate and tests simple.
                entity/ ==> Plain classes with JPA annotations that describe the tables.
                config/ ==> Cross-cutting pieces: the data seeders and the read-only filter used on the public site.""",
                "Be ready to explain what auto-configuration does, what a bean is, the difference between @Component, @Service and @Repository (mostly semantic, plus exception translation for repositories) and how dependency injection helps testing.",
                "Putting the main class in a different package so scanning misses everything; business logic inside controllers; field injection with @Autowired instead of constructor injection.");

        add(l, 8, c8, null, 2, "Entities: Mapping a Class to a Table with JPA",
                "Persistence is the core of most backends. JPA lets you work with objects and still get a real relational schema, and interviewers expect you to know the annotations.",
                "An entity is a class mapped to a table. @Id marks the primary key, @GeneratedValue lets the database create it, @Column customises a column, and @Enumerated stores enums. Lifecycle callbacks (@PrePersist, @PreUpdate) run automatically before saving. Hibernate is the implementation that turns these annotations into SQL.",
                "Java",
                """
                @Entity
                @Table(name = "coding_problems")
                public class CodingProblem {

                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    private Long id;

                    @NotBlank(message = "Title is required")
                    @Column(nullable = false)
                    private String title;

                    @Column(columnDefinition = "TEXT")
                    private String solutionCode;

                    @Enumerated(EnumType.STRING)
                    private Difficulty difficulty;

                    @Column(nullable = false, updatable = false)
                    private LocalDateTime createdAt;

                    @PrePersist
                    protected void onCreate() {
                        this.createdAt = LocalDateTime.now();
                    }
                }""",
                """
                @Entity ==> Tells JPA this class is persistent: one object per table row. A public or protected no-argument constructor is required.
                @Table(name = "coding_problems") ==> The table name; otherwise it would be derived from the class name.
                @Id @GeneratedValue(strategy = IDENTITY) ==> The primary key, generated by the database auto-increment column.
                @NotBlank(message = ...) ==> Bean Validation: the message is returned to the user if the title is empty. It is checked in controllers marked @Valid.
                @Column(nullable = false) ==> Adds NOT NULL to the table so the database enforces the rule too, not only Java.
                @Column(columnDefinition = "TEXT") ==> Long text. A default String column is only 255 characters, too short for code and explanations.
                @Enumerated(EnumType.STRING) ==> Stores 'HARD' instead of the number 2. Reordering enum constants later cannot silently corrupt old rows.
                updatable = false ==> Once inserted, the creation time can never be changed by an UPDATE.
                @PrePersist ==> Called by JPA just before the first INSERT, so we never forget to set the timestamp.""",
                "Typical questions: difference between JPA, Hibernate and Spring Data; lazy vs eager loading; the N+1 problem; @OneToMany and @ManyToOne; why EnumType.STRING; what ddl-auto=update/validate/none mean and why production should use migrations (Flyway).",
                "Using EnumType.ORDINAL; exposing entities with lazy relations directly in JSON; relying on ddl-auto=update in production.");

        add(l, 8, c8, null, 3, "Repositories with Spring Data JPA",
                "Spring Data writes the data access code for you. Knowing when a method name is enough and when to use a query is a practical skill every Spring job tests.",
                "A repository is an interface that extends JpaRepository. You immediately get save, findById, findAll, delete and count. Method names such as existsByTitleIgnoreCase are turned into queries automatically. For optional filters, write a JPQL query with @Query and named parameters.",
                "Java",
                """
                public interface ProblemRepository extends JpaRepository<CodingProblem, Long> {

                    boolean existsByTitleIgnoreCase(String title);

                    List<CodingProblem> findByPracticeStatus(RevisionStatus status);

                    @Query(\"\"\"
                            SELECT p FROM CodingProblem p
                            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
                              AND (:difficulty IS NULL OR p.difficulty = :difficulty)
                            ORDER BY p.createdAt DESC
                            \"\"\")
                    List<CodingProblem> search(@Param("keyword") String keyword,
                                               @Param("difficulty") Difficulty difficulty);
                }""",
                """
                extends JpaRepository<CodingProblem, Long> ==> Generic parameters: the entity type and the type of its primary key. Spring creates the implementation at runtime; you never write it.
                existsByTitleIgnoreCase(String title) ==> A derived query: Spring parses the method name (exists, by, Title, IgnoreCase) and builds SELECT ... WHERE lower(title) = lower(?).
                findByPracticeStatus(...) ==> Returns every row with that status; the parameter name is matched to the property.
                @Query(...) ==> Use when the method name would become unreadable or you need optional filters.
                (:keyword IS NULL OR ...) ==> The 'optional filter' trick: when the caller passes null the condition is always true and is effectively skipped.
                JPQL uses CodingProblem and p.title ==> JPQL queries the entity model (class and field names), not the table and column names.
                @Param("keyword") ==> Binds the Java argument to the named parameter. Values are sent as parameters, never glued into the SQL, which prevents SQL injection.""",
                "Expect: JPQL vs native SQL, pagination with Pageable, the N+1 problem and how to fix it with JOIN FETCH, derived query limits, and projections. A strong answer mentions parameter binding as the protection against SQL injection.",
                "Concatenating user input into a query string; returning huge lists without paging; method names so long that nobody can read them.");

        add(l, 8, c8, null, 4, "The Service Layer: Business Rules",
                "Services hold the rules of the application, and they are what you unit test. Keeping logic out of controllers is a standard review comment.",
                "A service coordinates repositories and enforces rules: look things up, throw meaningful exceptions, copy fields on update and normalise input. It is annotated @Service so Spring manages it. Methods that change several rows together should be @Transactional.",
                "Java",
                """
                @Service
                public class ProblemService {

                    private final ProblemRepository problemRepository;

                    public ProblemService(ProblemRepository problemRepository) {
                        this.problemRepository = problemRepository;
                    }

                    public CodingProblem getProblemById(Long id) {
                        return problemRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Coding problem", id));
                    }

                    public CodingProblem updateProblem(Long id, CodingProblem updated) {
                        CodingProblem existing = getProblemById(id);
                        existing.setTitle(updated.getTitle());
                        existing.setDescription(updated.getDescription());
                        return problemRepository.save(existing);
                    }

                    private String blankToNull(String value) {
                        return (value == null || value.isBlank()) ? null : value;
                    }
                }""",
                """
                @Service ==> Marks the class as a Spring bean in the service layer.
                private final ProblemRepository problemRepository; ==> The dependency is final, so it can never be changed or left null after construction.
                public ProblemService(ProblemRepository ...) ==> Constructor injection. Spring passes the repository in; in a test you pass a mock. No @Autowired is needed with a single constructor.
                findById(id).orElseThrow(...) ==> findById returns an Optional. A missing row becomes a NotFoundException, which the global handler converts into a 404.
                CodingProblem existing = getProblemById(id); ==> Reuses the lookup so the not-found rule exists in exactly one place.
                existing.setTitle(updated.getTitle()); ==> Copy only the fields we allow to change. Saving the incoming object directly would let a client overwrite id or createdAt.
                blankToNull(...) ==> Normalises empty form fields to null so the repository query treats them as 'no filter'.""",
                "Be able to explain why the service layer exists, constructor vs field injection, what @Transactional does, and how you would unit test this class with Mockito (mock the repository, verify the calls).",
                "Business logic in the controller; saving the request object directly (mass assignment); swallowing exceptions in the service.");

        add(l, 8, c8, null, 5, "Controllers: REST Endpoints",
                "Controllers are the public face of your backend. Correct annotations, status codes and validation are the first things a reviewer checks.",
                "@RestController combines @Controller and @ResponseBody, so return values become JSON. @RequestMapping sets the base path; @GetMapping, @PostMapping, @PutMapping and @DeleteMapping handle the methods. @PathVariable reads a part of the URL, @RequestBody reads JSON, @Valid triggers validation. ResponseEntity lets you choose the status code and headers.",
                "Java",
                """
                @RestController
                @RequestMapping("/api/problems")
                public class ProblemController {

                    private final ProblemService problemService;

                    public ProblemController(ProblemService problemService) {
                        this.problemService = problemService;
                    }

                    @PostMapping
                    public ResponseEntity<CodingProblem> create(@Valid @RequestBody CodingProblem problem) {
                        CodingProblem saved = problemService.createProblem(problem);
                        return ResponseEntity.created(URI.create("/api/problems/" + saved.getId())).body(saved);
                    }

                    @GetMapping("/{id}")
                    public CodingProblem get(@PathVariable Long id) {
                        return problemService.getProblemById(id);
                    }

                    @DeleteMapping("/{id}")
                    public ResponseEntity<Void> delete(@PathVariable Long id) {
                        problemService.deleteProblem(id);
                        return ResponseEntity.noContent().build();
                    }
                }""",
                """
                @RestController ==> Every method returns data (converted to JSON by Jackson) rather than the name of an HTML template.
                @RequestMapping("/api/problems") ==> Common URL prefix for all methods in the class.
                @PostMapping ==> Handles POST /api/problems, i.e. creating a resource.
                @Valid @RequestBody CodingProblem problem ==> Parses the JSON body into an object and runs the Bean Validation rules. If a rule fails, Spring throws MethodArgumentNotValidException before our code runs.
                ResponseEntity.created(URI...).body(saved) ==> Status 201 with a Location header pointing to the new resource, which is the REST convention for creation.
                @GetMapping("/{id}") ==> The {id} part of the URL is bound to the method argument by @PathVariable.
                return ResponseEntity.noContent().build(); ==> 204: the delete succeeded and there is nothing to send back.""",
                "Expect: difference between @Controller and @RestController, @RequestParam vs @PathVariable vs @RequestBody, how you return correct status codes, and why entities should often be replaced by DTOs on public APIs (to avoid exposing internal fields and to version the contract).",
                "Returning entities that expose sensitive fields; always returning 200; doing database work in the controller.");

        add(l, 8, c8, null, 6, "Validation and Global Exception Handling",
                "Production apps never show a raw stack trace. A single place that converts exceptions into clean responses is a hallmark of a mature codebase.",
                "@RestControllerAdvice applies to every controller. Each @ExceptionHandler method maps an exception type to a response. Order matters conceptually from specific to general: NotFoundException becomes 404, validation errors become 400 with a field map, and a final catch-all returns 500 with a safe message while the real error is logged.",
                "Java",
                """
                @RestControllerAdvice
                public class GlobalExceptionHandler {

                    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

                    @ExceptionHandler(NotFoundException.class)
                    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
                        Map<String, Object> body = new HashMap<>();
                        body.put("status", 404);
                        body.put("message", ex.getMessage());
                        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
                    }

                    @ExceptionHandler(MethodArgumentNotValidException.class)
                    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
                        Map<String, String> fieldErrors = new HashMap<>();
                        for (FieldError e : ex.getBindingResult().getFieldErrors()) {
                            fieldErrors.put(e.getField(), e.getDefaultMessage());
                        }
                        return new ResponseEntity<>(Map.of("status", 400, "errors", fieldErrors), HttpStatus.BAD_REQUEST);
                    }

                    @ExceptionHandler(Exception.class)
                    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
                        log.error("Unhandled exception", ex);
                        return new ResponseEntity<>(Map.of("status", 500, "message", "An unexpected error occurred."),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }""",
                """
                @RestControllerAdvice ==> One class that intercepts exceptions thrown by any controller, so error handling is not repeated everywhere.
                @ExceptionHandler(NotFoundException.class) ==> Runs when a service throws NotFoundException. Spring picks the most specific matching handler.
                HttpStatus.NOT_FOUND ==> 404 tells the client the resource does not exist; it is a client error, not a server fault.
                for (FieldError e : ex.getBindingResult().getFieldErrors()) ==> Collects every failed validation rule into a map of field name to message, e.g. title: 'Title is required'.
                HttpStatus.BAD_REQUEST ==> 400: the client sent invalid data. The body lets the front end show each message beside its field.
                @ExceptionHandler(Exception.class) ==> The safety net for anything unexpected.
                log.error("Unhandled exception", ex) ==> The full stack trace goes to the server log for developers.
                "An unexpected error occurred." ==> The client gets a generic message only. Leaking internal details helps attackers.""",
                "A good answer covers: why centralise error handling, the shape of the error body, 4xx versus 5xx, logging versus returning details, and how you would add a correlation id for tracing. Mention ProblemDetail (RFC 7807) as the modern standard.",
                "Returning stack traces to clients; catching Exception inside every controller; using 200 for errors.");

        add(l, 8, c8, null, 7, "Thymeleaf: Server-Side HTML",
                "Server-side rendering gives fast pages and good SEO without a JavaScript build. Many companies still use it for internal tools and admin panels.",
                "Thymeleaf templates are normal HTML with th: attributes. The controller puts data into the Model and returns the template name. Expressions: ${...} reads a model value, @{...} builds a URL, th:text prints text (escaped), th:each loops, th:if shows or hides. th:text escapes HTML automatically, which protects against cross-site scripting.",
                "HTML",
                """
                @GetMapping("/problems")
                public String list(@RequestParam(required = false) String q, Model model) {
                    model.addAttribute("problems", problemService.search(q, null, null, null));
                    model.addAttribute("q", q);
                    return "problems/list";
                }

                <form method="get" th:action="@{/problems}">
                  <input type="text" name="q" th:value="${q}">
                </form>
                <tr th:each="p : ${problems}">
                  <td><a th:href="@{/problems/{id}(id=${p.id})}" th:text="${p.title}"></a></td>
                  <td th:text="${p.difficulty}"></td>
                </tr>
                <p th:if="${problems.isEmpty()}">No problems found.</p>""",
                """
                @RequestParam(required = false) String q ==> Reads ?q=... from the URL; required = false means a missing value is null instead of an error.
                model.addAttribute("problems", ...) ==> Puts data where the template can read it. The Model is the bridge from Java to HTML.
                return "problems/list"; ==> The name of the template file (templates/problems/list.html), not a URL.
                th:action="@{/problems}" ==> Builds the form URL and automatically adds the application's context path.
                th:value="${q}" ==> Pre-fills the input so the search text is not lost after submitting.
                th:each="p : ${problems}" ==> Repeats the row once per problem, like a for-each loop.
                @{/problems/{id}(id=${p.id})} ==> A URL template: {id} is replaced by the value after the semicolon.
                th:text="${p.title}" ==> Prints the value with HTML escaping, so a title containing <script> is shown as text and not executed.
                th:if="${problems.isEmpty()}" ==> Renders the element only when the condition is true (the empty-state message).""",
                "Be ready to compare server-side rendering with a single-page app (SEO, first load speed, complexity), explain what the Model is, and how Spring MVC finds the view. Mention that escaping by default prevents XSS.",
                "Using th:utext with user data; forgetting the model attribute so the page shows blanks; hard-coding URLs without @{...}.");

        add(l, 8, c8, null, 8, "Configuration and Profiles",
                "The same code must run on your laptop and in the cloud with different settings and secrets. Profiles and environment variables are how that is done.",
                "application.properties holds settings. A profile-specific file (application-cloud.properties) overrides it when that profile is active. Set the active profile with spring.profiles.active or the SPRING_PROFILES_ACTIVE environment variable. Secrets belong in ignored files or environment variables, never in Git. @Value or @ConfigurationProperties reads settings into code.",
                "Properties",
                """
                # application.properties
                spring.application.name=devvault
                spring.profiles.active=local
                spring.jpa.hibernate.ddl-auto=update

                # application-cloud.properties (public demo)
                server.port=${PORT:8080}
                spring.datasource.url=jdbc:h2:mem:devvault;MODE=MySQL
                spring.jpa.hibernate.ddl-auto=create
                devvault.readonly=true""",
                """
                spring.profiles.active=local ==> By default the 'local' profile is used, which adds the MySQL settings from an ignored file.
                ddl-auto=update ==> Hibernate adds missing tables and columns at startup. Convenient for development, but production should use migrations (Flyway or Liquibase).
                server.port=${PORT:8080} ==> Reads the PORT environment variable and falls back to 8080. Cloud hosts decide the port, so it must not be hard-coded.
                jdbc:h2:mem:devvault;MODE=MySQL ==> An in-memory H2 database that behaves like MySQL, so the public demo needs no paid database.
                ddl-auto=create ==> Rebuild the schema on every start because the demo database is recreated and re-seeded each time.
                devvault.readonly=true ==> A custom flag read by a filter and a model advice class to switch the site into read-only mode.""",
                "Expect: how to keep secrets out of Git, how profiles work, environment variables overriding properties, why you use different databases per environment, and how to externalise configuration for 12-factor apps.",
                "Committing passwords; hard-coding ports and URLs; using ddl-auto=create-drop against a real database.");

        String c9 = "Testing, Git and Deployment";
        String c9Intro = "Writing code is only part of the job. Teams expect automated tests, clean Git history and the ability to ship. This chapter covers the practical tools that make a project look professional.";

        add(l, 9, c9, c9Intro, 1, "Unit Testing with JUnit and Mockito",
                "Tests prove your code works and protect it when you change it. 'Do you write tests?' is a standard question, and a visible test suite sets a portfolio project apart.",
                "A unit test checks one small piece in isolation. JUnit 5 runs the tests (@Test) and AssertJ gives readable assertions. Mockito replaces dependencies (such as the repository) with fakes so the test needs no database. Structure each test as arrange, act, assert, and give it a name that describes the behaviour.",
                "Java",
                """
                @ExtendWith(MockitoExtension.class)
                class ProblemServiceTest {

                    @Mock
                    private ProblemRepository problemRepository;

                    @InjectMocks
                    private ProblemService problemService;

                    @Test
                    void getProblemById_throwsWhenMissing() {
                        when(problemRepository.findById(99L)).thenReturn(Optional.empty());

                        assertThatThrownBy(() -> problemService.getProblemById(99L))
                                .isInstanceOf(NotFoundException.class);
                    }

                    @Test
                    void createProblem_savesAndReturnsTheProblem() {
                        CodingProblem p = new CodingProblem();
                        p.setTitle("Two Sum");
                        when(problemRepository.save(p)).thenReturn(p);

                        CodingProblem result = problemService.createProblem(p);

                        assertThat(result.getTitle()).isEqualTo("Two Sum");
                        verify(problemRepository).save(p);
                    }
                }""",
                """
                @ExtendWith(MockitoExtension.class) ==> Turns on Mockito for this test class so the annotations below work.
                @Mock private ProblemRepository ==> A fake repository. It does nothing unless we tell it what to return, so no database is involved.
                @InjectMocks private ProblemService ==> Creates the real service and passes the mock into its constructor.
                when(...findById(99L)).thenReturn(Optional.empty()) ==> Arrange: program the fake so the row 'does not exist'.
                assertThatThrownBy(() -> ...).isInstanceOf(NotFoundException.class) ==> Act and assert: the call must throw the expected exception.
                verify(problemRepository).save(p) ==> Checks the interaction: the service really asked the repository to save.
                assertThat(result.getTitle()).isEqualTo("Two Sum") ==> A readable assertion on the returned value.""",
                "Know: unit vs integration vs end-to-end tests, the test pyramid, mock vs stub vs spy, what makes a test brittle, and what code coverage does and does not tell you. Mention Testcontainers for real-database integration tests.",
                "Testing getters and setters only; tests that depend on each other; mocking everything including the class under test.");

        add(l, 9, c9, null, 2, "Testing Controllers with MockMvc",
                "Controller tests verify URLs, status codes, JSON and validation without starting a real server, which is fast and catches wiring mistakes.",
                "MockMvc simulates HTTP requests against your controller. @WebMvcTest loads only the web layer, and you provide the service as a mock bean. You then assert on the status code and the JSON using jsonPath.",
                "Java",
                """
                @WebMvcTest(ProblemController.class)
                class ProblemControllerTest {

                    @Autowired
                    private MockMvc mockMvc;

                    @MockitoBean
                    private ProblemService problemService;

                    @Test
                    void createProblem_returns400WhenTitleMissing() throws Exception {
                        mockMvc.perform(post("/api/problems")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\\"description\\": \\"x\\"}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors.title").value("Title is required"));
                    }

                    @Test
                    void getProblem_returns404WhenMissing() throws Exception {
                        when(problemService.getProblemById(5L)).thenThrow(new NotFoundException("Coding problem", 5L));

                        mockMvc.perform(get("/api/problems/5"))
                                .andExpect(status().isNotFound());
                    }
                }""",
                """
                @WebMvcTest(ProblemController.class) ==> Starts only the MVC slice (controllers, JSON conversion, exception handlers), much faster than the whole application.
                @Autowired MockMvc ==> The tool that performs fake HTTP calls without a network.
                @MockitoBean ProblemService ==> Registers a mock service inside the Spring context so the controller has something to call.
                post("/api/problems").contentType(JSON).content(...) ==> Builds a POST request with a JSON body that is missing the title.
                status().isBadRequest() ==> The validation rule must reject it with 400.
                jsonPath("$.errors.title").value(...) ==> Reads into the JSON response: the error map must contain our message for the title field.
                thenThrow(new NotFoundException(...)) ==> Simulates the service failing; the global handler must turn it into a 404.""",
                "Be ready to explain test slices (@WebMvcTest, @DataJpaTest, @SpringBootTest), why mock the service in controller tests, and how to test security and validation. Show you test the failure paths, not only the happy path.",
                "Using @SpringBootTest for everything (slow); forgetting to test validation errors; asserting only the status code and not the body.");

        add(l, 9, c9, null, 3, "Git Workflow",
                "Git is how every team shares code. A clean history and the ability to resolve conflicts are expected from your first day.",
                "Git stores snapshots of your project as commits. A branch is a movable pointer, which lets you develop a feature in isolation. Typical flow: pull the latest main, create a feature branch, commit small logical changes with clear messages, push, open a pull request, review, merge. Never commit secrets or build output; list them in .gitignore.",
                "Bash",
                """
                git clone https://github.com/you/devvault.git
                git switch -c feature/search-by-topic
                git add src/main/java/com/devvault/repository/ProblemRepository.java
                git commit -m "Search problems by topic and tags"
                git fetch origin
                git rebase origin/main
                git push -u origin feature/search-by-topic
                git log --oneline --graph -10
                git restore --staged secrets.txt""",
                """
                git clone <url> ==> Copies the remote repository, with its full history, to your machine.
                git switch -c feature/search-by-topic ==> Creates and moves to a new branch so main stays stable while you work.
                git add <file> ==> Stages exactly the changes you want in the next commit; adding files one by one keeps commits focused.
                git commit -m "..." ==> Records the snapshot. A good message says what and why in the imperative mood.
                git fetch origin; git rebase origin/main ==> Replays your commits on top of the latest main so history stays linear and conflicts surface early.
                git push -u origin <branch> ==> Uploads the branch and remembers the link, so later a plain git push works.
                git log --oneline --graph ==> A compact picture of the history and branches.
                git restore --staged secrets.txt ==> Removes a file from the staging area before it is committed; never commit credentials.""",
                "Expect: merge vs rebase, what to do after an accidentally committed secret (rotate it, history rewriting is not enough), how to resolve a conflict, and what a good pull request looks like. Recruiters look at your GitHub: pinned repositories, README quality and commit messages.",
                "Huge commits with unrelated changes; committing .env files; force-pushing to a shared branch.");

        add(l, 9, c9, null, 4, "Docker: Packaging the App",
                "'It works on my machine' is solved by containers. Knowing Docker basics is now expected for backend roles.",
                "A Docker image contains your app and everything it needs to run. A multi-stage Dockerfile builds the app in one stage (with JDK and Maven) and copies only the finished jar into a small runtime stage (JRE), producing a smaller and safer image. A container is a running instance of an image. Cloud platforms run your image and provide the port through an environment variable.",
                "Dockerfile",
                """
                FROM maven:3.9-eclipse-temurin-21 AS build
                WORKDIR /app
                COPY pom.xml .
                RUN mvn -q -B dependency:go-offline
                COPY src ./src
                RUN mvn -q -B package -DskipTests

                FROM eclipse-temurin:21-jre
                WORKDIR /app
                COPY --from=build /app/target/devvault-0.0.1-SNAPSHOT.jar app.jar
                ENV SPRING_PROFILES_ACTIVE=cloud
                EXPOSE 8080
                ENTRYPOINT ["java", "-Xmx400m", "-jar", "app.jar"]""",
                """
                FROM maven:3.9-eclipse-temurin-21 AS build ==> The first stage has the full JDK and Maven, needed only to compile. AS build names it for later reference.
                COPY pom.xml . then RUN mvn dependency:go-offline ==> Copying the pom first lets Docker cache the downloaded dependencies; they are re-downloaded only when the pom changes.
                COPY src ./src; RUN mvn package ==> Source code changes often, so it is copied after the dependency layer to keep builds fast.
                FROM eclipse-temurin:21-jre ==> The second stage starts clean with only a JRE: a much smaller image with no build tools.
                COPY --from=build ... app.jar ==> Takes just the finished jar from the first stage.
                ENV SPRING_PROFILES_ACTIVE=cloud ==> Activates the cloud settings (in-memory database, read-only mode) when the container starts.
                -Xmx400m ==> Limits the Java heap so the app fits inside a small free-tier container.
                ENTRYPOINT [...] ==> The command run at start. The exec form (a JSON array) makes Java PID 1 so it receives stop signals correctly.""",
                "Be ready to explain image vs container, layers and caching, multi-stage builds, why not to run as root, how environment variables configure the app, and docker compose for app plus database.",
                "Copying the whole project before downloading dependencies (no cache); baking secrets into the image; using a full JDK image in production.");

        add(l, 9, c9, null, 5, "CI/CD and Deploying to the Cloud",
                "Shipping the app is what turns a project into a product. A live link on your CV is worth more than a screenshot.",
                "Continuous integration (CI) runs the build and tests on every push so broken code is caught early. Continuous deployment (CD) publishes the passing build automatically. Platforms such as Render deploy straight from a GitHub repository with a Dockerfile; every push to main triggers a new deploy. Free tiers sleep when idle, so the first request after a pause is slow.",
                "YAML",
                """
                name: CI
                on:
                  push:
                    branches: [main]
                  pull_request:

                jobs:
                  test:
                    runs-on: ubuntu-latest
                    steps:
                      - uses: actions/checkout@v4
                      - uses: actions/setup-java@v4
                        with:
                          distribution: temurin
                          java-version: 21
                          cache: maven
                      - run: ./mvnw -B verify""",
                """
                on: push: branches: [main]; pull_request ==> The workflow runs for every push to main and for every pull request, so problems are found before merging.
                runs-on: ubuntu-latest ==> A clean Linux virtual machine is created for each run; nothing from your laptop is involved.
                actions/checkout@v4 ==> Downloads your repository into the runner.
                actions/setup-java@v4 with temurin 21 ==> Installs the same Java version used locally, so 'works on my machine' differences disappear.
                cache: maven ==> Reuses downloaded dependencies between runs to save minutes.
                ./mvnw -B verify ==> Compiles, runs all tests and checks the packaging. A failing test turns the run red and blocks the merge.""",
                "Explain the difference between CI, continuous delivery and continuous deployment, blue-green and rolling deploys, rollbacks, and environment variables for secrets. Be ready to show the live link and describe what happens on git push.",
                "Skipping tests in CI; storing secrets in the workflow file instead of repository secrets; no way to roll back.");

        String c10 = "System Design and Interview Playbook";
        String c10Intro = "Technical skill gets you to the interview; communication gets you the offer. This final chapter covers how to think about scale, how to solve a problem out loud, and how to present your projects and yourself.";

        add(l, 10, c10, c10Intro, 1, "Scaling Basics: Cache, Load Balancer, Database",
                "Even for junior roles, interviewers ask 'what if a million users visit?'. A few core ideas answer most of those questions.",
                "Vertical scaling means a bigger machine; horizontal scaling means more machines behind a load balancer. Make the application stateless so any server can handle any request. Add a cache (Redis) for data that is read often and changes rarely. Add database read replicas for read-heavy workloads, indexes for slow queries, and a queue for slow background jobs. Always ask about read/write ratio and acceptable delay first.",
                "Text",
                """
                Users
                  |
                CDN (static files: css, js, images)
                  |
                Load balancer
                  |            |            |
                App server   App server   App server   (stateless)
                  |
                Cache (Redis)  <-- check first
                  |
                Database primary  --> read replicas
                  |
                Queue --> workers (emails, reports)""",
                """
                CDN ==> Serves static files from servers near the user, taking load and latency off your app.
                Load balancer ==> Spreads requests across servers and removes unhealthy ones, which also gives you zero-downtime deploys.
                App server (stateless) ==> No user data kept in memory between requests, so you can add or remove servers freely. Sessions go to Redis or a signed token.
                Cache (Redis) <-- check first ==> A cache hit answers in about a millisecond instead of hitting the database. Decide an expiry and how to invalidate.
                read replicas ==> Copies of the database that serve reads; writes still go to the primary. Beware of replication lag.
                Queue --> workers ==> Slow work (sending email) is put on a queue and handled later, so the user's request returns quickly.""",
                "Use a framework: clarify requirements, estimate load, draw the high-level design, then dig into the bottleneck and discuss trade-offs. The interviewer is grading your reasoning (consistency vs availability, cost vs speed), not a perfect answer.",
                "Jumping to technology names before understanding the requirements; forgetting that caches can serve stale data; ignoring failure cases.");

        add(l, 10, c10, null, 2, "Design Walkthrough: A URL Shortener",
                "It is the most common beginner system design question and shows a complete, small design from API to database to scaling.",
                "Requirements: create a short link, redirect quickly, optionally expire, count clicks. API: POST /shorten and GET /{code} (301 or 302 redirect). Storage: one table of code, long URL, created time. Code generation: encode an auto-increment id in Base62 so codes are short and collision free. Reads are much more frequent than writes, so cache hot codes in Redis. Analytics go to a queue so redirects stay fast.",
                "SQL",
                """
                CREATE TABLE short_url (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    code VARCHAR(10) NOT NULL UNIQUE,
                    long_url VARCHAR(2048) NOT NULL,
                    created_at DATETIME NOT NULL,
                    expires_at DATETIME NULL,
                    clicks BIGINT NOT NULL DEFAULT 0
                );

                -- redirect path (hot):  cache.get(code) -> else SELECT long_url ... WHERE code = ?
                -- code = base62(id)     7 chars = 62^7 = 3.5 trillion links""",
                """
                id BIGINT AUTO_INCREMENT ==> A unique number for every link. We turn this number into the short code, so two links can never get the same code.
                code VARCHAR(10) NOT NULL UNIQUE ==> The UNIQUE constraint creates an index, which is exactly what the redirect lookup uses.
                long_url VARCHAR(2048) ==> URLs can be long; 2048 is a common practical limit.
                expires_at DATETIME NULL ==> Optional expiry; NULL means the link never expires.
                clicks ==> A simple counter. At large scale you would update it asynchronously through a queue rather than on every redirect.
                cache.get(code) ==> The hot path: popular links are served from memory, protecting the database.
                base62(id) ==> Digits plus lower and upper case letters. 7 characters already cover trillions of links.""",
                "Cover: requirements, API, data model, code generation (hash vs counter vs random) and collisions, caching, 301 versus 302 (301 gets cached by browsers so you lose analytics), rate limiting and abuse prevention. Always finish with bottlenecks and how you would scale each one.",
                "Hashing the URL and ignoring collisions; forgetting expiry and abuse; using one database for hot reads at huge scale.");

        add(l, 10, c10, null, 3, "How to Solve a Coding Problem Out Loud",
                "Interviewers cannot read your mind. A clear process earns marks even when the final code has a small bug.",
                "Follow the same steps every time: (1) Restate the problem and ask clarifying questions about input size, duplicates, negatives and empty input. (2) Work through a small example by hand. (3) State a brute-force solution and its complexity. (4) Improve it by naming the pattern. (5) Explain the plan before writing code. (6) Code cleanly with good names. (7) Test with your example and edge cases. (8) State time and space complexity and discuss alternatives.",
                "Text",
                """
                1. Clarify    "Can the array contain negatives? Is it sorted? What if there is no answer?"
                2. Example    nums = [2, 7, 11, 15], target = 9  ->  [0, 1]
                3. Brute      two loops, check every pair: O(n^2) time, O(1) space
                4. Improve    "I keep numbers I have seen in a hash map, so each lookup is O(1)": O(n) time, O(n) space
                5. Plan       for each x: need = target - x; if need in map -> answer; else store x
                6. Code       (write it, narrating each line)
                7. Test       example, empty array, duplicates [3,3] target 6, no answer
                8. Analyse    O(n) time, O(n) space; if sorted I could use two pointers with O(1) space""",
                """
                Clarify ==> Asking about constraints is not a weakness; it shows you think about edge cases the way an engineer must.
                Example ==> Working one case by hand often reveals the pattern and gives you a test to run later.
                Brute ==> Always say the simple solution first. It proves you understand the problem and gives a baseline to improve.
                Improve ==> Name the idea (hash map, two pointers, sliding window) and the trade-off in plain words.
                Plan ==> Get agreement before coding. A wrong plan caught here costs seconds; caught in code it costs minutes.
                Test ==> Dry-run your own code on paper. Finding your own bug impresses interviewers far more than having them find it.
                Analyse ==> Finish with complexity and one alternative. It signals senior-level thinking.""",
                "This is the rubric most interviewers use: problem solving, communication, code quality, testing, complexity analysis. If you are stuck, say what you have tried and ask for a hint; interviewers expect it and hints rarely hurt a strong process.",
                "Silently coding for ten minutes; refusing to give a brute force; never testing; panicking instead of thinking aloud.");

        add(l, 10, c10, null, 4, "Behavioural Questions with the STAR Method",
                "Many offers are decided by the behavioural round. Companies want people who can work in a team, handle failure and learn quickly.",
                "STAR means Situation (context), Task (your responsibility), Action (what YOU did, step by step) and Result (measurable outcome and what you learned). Prepare 5 stories: a hard bug, a disagreement, a deadline, a mistake you owned, and something you learned fast. Keep each to about two minutes.",
                "Text",
                """
                Question: "Tell me about a time you fixed a difficult bug."

                S: In my project DevVault, searching problems returned a 500 error for some filters.
                T: I owned the search feature and needed to fix it before sharing the link.
                A: I reproduced it with a failing test, read the stack trace, and found a null
                   enum parameter that the query did not expect. I changed the JPQL to treat null
                   as 'no filter', added tests for each filter combination, and redeployed.
                R: All filters worked, the test suite grew by four tests, and I now write the
                   failing test first when debugging.""",
                """
                S: ==> One or two sentences of context. Interviewers do not need the whole project history.
                T: ==> Your personal responsibility, so they know what part was yours and not the team's.
                A: reproduced it with a failing test ==> Specific actions in the first person ('I'). Concrete verbs show real involvement.
                found a null enum parameter ==> A technical detail makes the story believable.
                added tests ==> Shows you prevent repeats, not only patch.
                R: ==> A measurable result plus a lesson. Ending with what you learned shows growth.""",
                "Typical prompts: biggest challenge, conflict with a teammate, a failure, leadership, why this company, strengths and weaknesses. Be honest, specific and positive about the lesson. Research the company and prepare two thoughtful questions to ask the interviewer.",
                "Saying 'we' for everything; vague answers without a result; blaming others; memorised speeches that sound fake.");

        add(l, 10, c10, null, 5, "Presenting Your Project to Recruiters",
                "A recruiter spends under a minute on a CV. A clear project story with a live link and a good README can be the difference between a call and silence.",
                "For each project show: what problem it solves, the tech stack, two or three features you are proud of, one hard technical decision and the trade-off, how to run it, and a live link. Put the same information in the GitHub README (screenshots, setup steps, architecture diagram). On your CV use one line per project with a verb and a result.",
                "Text",
                """
                CV line:
                DevVault (Java 21, Spring Boot, JPA, MySQL, Docker) - Built a full-stack interview-prep platform with
                330+ problems, chapter-wise lessons and line-by-line explanations; 54+ automated tests;
                deployed with Docker on Render. Live: https://devvault-dqkk.onrender.com

                README outline:
                1. One-line pitch + screenshot
                2. Features
                3. Tech stack and architecture diagram
                4. How to run locally (3 commands)
                5. Design decisions and trade-offs
                6. What I would improve next""",
                """
                DevVault (Java 21, Spring Boot, ...) ==> Put the technologies right after the name so a scanning recruiter sees the keywords immediately.
                Built a full-stack ... ==> Start with a strong verb and say what the product does for the user.
                330+ problems ... 54+ automated tests ==> Numbers make the claim concrete and checkable. Only claim what is true.
                Live: <link> ==> A working link is the strongest proof you can finish and ship.
                README: One-line pitch + screenshot ==> Most visitors decide in seconds whether to keep reading.
                How to run locally (3 commands) ==> Reviewers will not fight a setup. Easy setup shows professionalism.
                Design decisions and trade-offs ==> This is the section that proves you can think, not only copy tutorials.""",
                "Be ready to walk through the architecture, justify every technology choice, explain a bug you fixed and what you would do differently. Interviewers often open your repository live, so keep the code clean, remove dead code and never leave credentials in the history.",
                "A README with only 'Spring Boot project'; claiming skills you cannot explain; broken live links; copying a tutorial without understanding it.");

        return l;
    }
}
