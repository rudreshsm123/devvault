package com.devvault.config.seed;

import com.devvault.entity.LearningResource;
import com.devvault.entity.ResourceType;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.entity.ResourceType.*;

/** A second batch of long-lived learning links: docs, free courses, practice sites and reference repositories. */
public final class ResourceSeedMore {

    private ResourceSeedMore() {
    }

    private static void r(List<LearningResource> out, String title, String url, ResourceType type, String topic,
                          String description, String tags) {
        LearningResource x = new LearningResource();
        x.setTitle(title);
        x.setUrl(url);
        x.setResourceType(type);
        x.setTopic(topic);
        x.setDescription(description);
        x.setTags(tags);
        out.add(x);
    }

    public static List<LearningResource> all() {
        List<LearningResource> l = new ArrayList<>();

        // ---- Java & JVM ----
        r(l, "Java Language Specification", "https://docs.oracle.com/javase/specs/jls/se21/html/index.html", DOCUMENTATION, "Java", "The exact rules of the Java language.", "java,specification");
        r(l, "JVM Specification", "https://docs.oracle.com/javase/specs/jvms/se21/html/index.html", DOCUMENTATION, "Java", "How the JVM, bytecode and class loading really work.", "java,jvm");
        r(l, "Dev.java", "https://dev.java/learn/", TUTORIAL, "Java", "Oracle's modern Java learning hub.", "java,beginner");
        r(l, "Inside Java", "https://inside.java/", ARTICLE, "Java", "News and deep dives from the Java team.", "java,news");
        r(l, "Java Concurrency in Practice (book site)", "https://jcip.net/", ARTICLE, "Concurrency", "The classic book on correct multithreaded Java.", "java,concurrency,book");
        r(l, "Effective Java (overview)", "https://www.oreilly.com/library/view/effective-java/9780134686097/", ARTICLE, "Java", "Joshua Bloch's best-practice book for Java developers.", "java,best-practices,book");
        r(l, "Java Code Geeks", "https://www.javacodegeeks.com/", ARTICLE, "Java", "Tutorials and interview questions for Java developers.", "java,interview");
        r(l, "JavaTpoint Java Tutorial", "https://www.javatpoint.com/java-tutorial", TUTORIAL, "Java", "Beginner-friendly explanations of Java topics.", "java,beginner");
        r(l, "W3Schools Java", "https://www.w3schools.com/java/", TUTORIAL, "Java", "Short interactive lessons for Java basics.", "java,beginner");
        r(l, "Programiz Java", "https://www.programiz.com/java-programming", TUTORIAL, "Java", "Clear examples for Java fundamentals.", "java,beginner");
        r(l, "Jenkov Java Concurrency Tutorial", "https://jenkov.com/tutorials/java-concurrency/index.html", TUTORIAL, "Concurrency", "Threads, locks and executors explained simply.", "java,concurrency");
        r(l, "Google Guava", "https://github.com/google/guava", GITHUB_REPO, "Java", "Popular utility library: collections, caching, hashing.", "java,library");
        r(l, "Lombok", "https://projectlombok.org/", DOCUMENTATION, "Java", "Reduces boilerplate with annotations such as @Getter and @Builder.", "java,lombok");

        // ---- Spring ecosystem ----
        r(l, "Spring Framework Reference", "https://docs.spring.io/spring-framework/reference/index.html", DOCUMENTATION, "Spring", "The core container, MVC, data access and testing reference.", "spring,reference");
        r(l, "Spring Data JPA Reference", "https://docs.spring.io/spring-data/jpa/reference/", DOCUMENTATION, "Spring", "Repositories, derived queries and paging.", "spring,jpa");
        r(l, "Spring Security Reference", "https://docs.spring.io/spring-security/reference/index.html", DOCUMENTATION, "Spring", "Authentication, authorisation and common attack protection.", "spring,security");
        r(l, "Spring Initializr", "https://start.spring.io/", OTHER, "Spring", "Generate a Spring Boot project with the dependencies you choose.", "spring,tool");
        r(l, "Spring Boot Testing Guide", "https://docs.spring.io/spring-boot/reference/testing/index.html", DOCUMENTATION, "Testing", "Test slices, MockMvc and test containers.", "spring,testing");
        r(l, "Spring Boot Actuator", "https://docs.spring.io/spring-boot/reference/actuator/index.html", DOCUMENTATION, "Spring", "Health checks, metrics and monitoring endpoints.", "spring,monitoring");
        r(l, "Spring Petclinic", "https://github.com/spring-projects/spring-petclinic", GITHUB_REPO, "Spring", "The official sample Spring Boot application to study.", "spring,sample");
        r(l, "Hibernate ORM User Guide", "https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html", DOCUMENTATION, "JPA", "Complete reference for mappings, queries and performance.", "hibernate,jpa");
        r(l, "Vlad Mihalcea Blog", "https://vladmihalcea.com/", ARTICLE, "JPA", "Best practices for Hibernate, JPA and SQL performance.", "hibernate,performance");
        r(l, "Flyway Documentation", "https://documentation.red-gate.com/flyway", DOCUMENTATION, "Databases", "Versioned database migrations for production apps.", "flyway,migrations");
        r(l, "Testcontainers for Java", "https://java.testcontainers.org/", DOCUMENTATION, "Testing", "Run real databases in Docker during integration tests.", "testing,docker");
        r(l, "AssertJ", "https://assertj.github.io/doc/", DOCUMENTATION, "Testing", "Readable fluent assertions for Java tests.", "testing,assertj");
        r(l, "Resilience4j", "https://resilience4j.readme.io/docs/getting-started", DOCUMENTATION, "Resilience", "Circuit breaker, retry and rate limiter for Java.", "resilience,spring");
        r(l, "Baeldung Spring Tutorials", "https://www.baeldung.com/spring-tutorial", TUTORIAL, "Spring", "Step-by-step Spring articles with code.", "spring,tutorial");

        // ---- Data structures and algorithms ----
        r(l, "Grind 75", "https://www.techinterviewhandbook.org/grind75", OTHER, "DSA", "A customisable study plan built from the Blind 75.", "dsa,plan");
        r(l, "LeetCode Patterns Explore Cards", "https://leetcode.com/explore/", OTHER, "DSA", "Structured learning cards on arrays, trees, graphs and DP.", "leetcode,learning");
        r(l, "HackerRank Interview Preparation Kit", "https://www.hackerrank.com/interview/interview-preparation-kit", OTHER, "DSA", "Curated practice by topic with test cases.", "hackerrank,practice");
        r(l, "CodeChef Practice", "https://www.codechef.com/practice", OTHER, "DSA", "Problems sorted by difficulty and tag.", "competitive,practice");
        r(l, "Codeforces", "https://codeforces.com/", OTHER, "DSA", "Contests and a huge problem archive for competitive programming.", "competitive,contests");
        r(l, "AtCoder", "https://atcoder.jp/", OTHER, "DSA", "High-quality contests with excellent editorials.", "competitive,contests");
        r(l, "CSES Problem Set", "https://cses.fi/problemset/", OTHER, "DSA", "Carefully selected algorithm problems with a clear syllabus.", "dsa,practice");
        r(l, "cp-algorithms", "https://cp-algorithms.com/", ARTICLE, "DSA", "Detailed articles on algorithms and data structures.", "algorithms,reference");
        r(l, "Algorithms by Jeff Erickson", "https://jeffe.cs.illinois.edu/teaching/algorithms/", TUTORIAL, "DSA", "A free university algorithms textbook.", "algorithms,book");
        r(l, "Visualgo Sorting", "https://visualgo.net/en/sorting", TUTORIAL, "DSA", "Animated sorting algorithms step by step.", "sorting,visualization");
        r(l, "Data Structure Visualizations (USFCA)", "https://www.cs.usfca.edu/~galles/visualization/Algorithms.html", TUTORIAL, "DSA", "Interactive visualisations of trees, heaps, graphs and more.", "data-structures,visualization");
        r(l, "Python Tutor (Visualise Code)", "https://pythontutor.com/java.html", TUTORIAL, "Java", "Step through Java code and see memory as it runs.", "java,debugging,visualization");
        r(l, "Awesome Algorithms", "https://github.com/tayllan/awesome-algorithms", GITHUB_REPO, "DSA", "A curated list of algorithm resources.", "algorithms,list");
        r(l, "LeetCode Patterns (SeanPrashad)", "https://seanprashad.com/leetcode-patterns/", OTHER, "DSA", "Questions grouped by pattern and difficulty.", "leetcode,patterns");
        r(l, "Stanford CS106B Lecture Notes", "https://web.stanford.edu/class/cs106b/", TUTORIAL, "DSA", "Recursion, backtracking and data structures from Stanford.", "recursion,stanford");

        // ---- System design and architecture ----
        r(l, "ByteByteGo System Design", "https://bytebytego.com/", ARTICLE, "System Design", "Illustrated system design guides.", "system-design,diagrams");
        r(l, "System Design Interview Roadmap (Github)", "https://github.com/checkcheckzz/system-design-interview", GITHUB_REPO, "System Design", "Large collection of design problems and articles.", "system-design,list");
        r(l, "Awesome Scalability", "https://github.com/binhnguyennus/awesome-scalability", GITHUB_REPO, "System Design", "Scalability patterns and real-world architecture write-ups.", "scalability,architecture");
        r(l, "The Twelve-Factor App", "https://12factor.net/", ARTICLE, "Architecture", "Principles for building cloud-ready applications.", "cloud,architecture");
        r(l, "Microservices.io Patterns", "https://microservices.io/patterns/index.html", ARTICLE, "Architecture", "Catalogue of microservice patterns such as saga and API gateway.", "microservices,patterns");
        r(l, "Martin Fowler's Blog", "https://martinfowler.com/", ARTICLE, "Architecture", "Articles on refactoring, patterns and software design.", "design,refactoring");
        r(l, "Refactoring Guru: Refactoring Catalog", "https://refactoring.guru/refactoring", TUTORIAL, "Design Patterns", "Code smells and how to refactor them.", "refactoring,clean-code");
        r(l, "Clean Code Summary", "https://github.com/ryanmcdermott/clean-code-javascript", GITHUB_REPO, "Clean Code", "Clean code principles with examples (JavaScript, ideas apply to Java).", "clean-code,principles");
        r(l, "Google Engineering Practices", "https://google.github.io/eng-practices/", ARTICLE, "Engineering", "How Google reviews code and writes change lists.", "code-review,engineering");
        r(l, "Redis University", "https://university.redis.io/", TUTORIAL, "Redis", "Free official Redis courses.", "redis,caching");
        r(l, "Kafka Documentation", "https://kafka.apache.org/documentation/", DOCUMENTATION, "Messaging", "Event streaming concepts and reference.", "kafka,messaging");
        r(l, "RabbitMQ Tutorials", "https://www.rabbitmq.com/tutorials", TUTORIAL, "Messaging", "Hands-on message queue tutorials.", "rabbitmq,messaging");

        // ---- Databases ----
        r(l, "SQLZoo", "https://sqlzoo.net/", TUTORIAL, "SQL", "Interactive SQL exercises.", "sql,practice");
        r(l, "Mode SQL Tutorial", "https://mode.com/sql-tutorial/", TUTORIAL, "SQL", "SQL for analytics, from basics to window functions.", "sql,analytics");
        r(l, "LeetCode Database Problems", "https://leetcode.com/problemset/database/", OTHER, "SQL", "SQL interview questions graded by difficulty.", "sql,interview");
        r(l, "HackerRank SQL", "https://www.hackerrank.com/domains/sql", OTHER, "SQL", "SQL practice problems with instant feedback.", "sql,practice");
        r(l, "SQL Style Guide", "https://www.sqlstyle.guide/", ARTICLE, "SQL", "Conventions for readable SQL.", "sql,style");
        r(l, "PostgreSQL Documentation", "https://www.postgresql.org/docs/current/", DOCUMENTATION, "SQL", "The official PostgreSQL manual.", "postgresql,sql");
        r(l, "MongoDB Manual", "https://www.mongodb.com/docs/manual/", DOCUMENTATION, "NoSQL", "Official MongoDB documentation.", "mongodb,nosql");
        r(l, "Database Normalization Explained", "https://www.guru99.com/database-normalization.html", TUTORIAL, "Databases", "1NF, 2NF and 3NF with examples.", "database,normalization");
        r(l, "Designing Data-Intensive Applications Notes", "https://github.com/keyvanakbary/learning-notes/blob/master/designing-data-intensive-applications.md", ARTICLE, "Databases", "Chapter-by-chapter notes on the classic book.", "databases,distributed");
        r(l, "dbdiagram.io", "https://dbdiagram.io/", OTHER, "Databases", "Draw database schemas quickly and share them.", "database,design,tool");

        // ---- Web front end ----
        r(l, "The Odin Project", "https://www.theodinproject.com/", TUTORIAL, "Web", "A free full-stack curriculum.", "web,fullstack");
        r(l, "Frontend Mentor", "https://www.frontendmentor.io/", OTHER, "Web", "Real design challenges to build a portfolio.", "frontend,practice");
        r(l, "CSS Grid Garden", "https://cssgridgarden.com/", TUTORIAL, "CSS", "A game that teaches CSS Grid.", "css,grid,game");
        r(l, "Flexbox Froggy", "https://flexboxfroggy.com/", TUTORIAL, "CSS", "A game that teaches Flexbox.", "css,flexbox,game");
        r(l, "Can I Use", "https://caniuse.com/", OTHER, "Web", "Browser support tables for web features.", "web,compatibility");
        r(l, "HTML Living Standard", "https://html.spec.whatwg.org/multipage/", DOCUMENTATION, "Web", "The official HTML specification.", "html,specification");
        r(l, "MDN: HTTP Overview", "https://developer.mozilla.org/en-US/docs/Web/HTTP/Overview", TUTORIAL, "HTTP", "How HTTP works, with headers, methods and status codes.", "http,web");
        r(l, "MDN: Learn JavaScript", "https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Scripting", TUTORIAL, "JavaScript", "Structured JavaScript course from MDN.", "javascript,beginner");
        r(l, "You Don't Know JS Yet", "https://github.com/getify/You-Dont-Know-JS", GITHUB_REPO, "JavaScript", "Deep book series on how JavaScript really works.", "javascript,book");
        r(l, "React Roadmap (roadmap.sh)", "https://roadmap.sh/react", OTHER, "React", "A step-by-step learning path for React.", "react,roadmap");
        r(l, "Google Web Fundamentals: Accessibility", "https://web.dev/learn/accessibility", TUTORIAL, "Web", "Make sites usable for everyone.", "accessibility,web");
        r(l, "Lighthouse", "https://developer.chrome.com/docs/lighthouse/overview", DOCUMENTATION, "Web", "Audit performance, accessibility and SEO.", "performance,seo");
        r(l, "Coolors Palette Generator", "https://coolors.co/", OTHER, "Design", "Generate colour palettes for your site.", "design,colors");
        r(l, "Google Fonts", "https://fonts.google.com/", OTHER, "Design", "Free fonts for web projects.", "design,fonts");

        // ---- DevOps, tools, career ----
        r(l, "roadmap.sh Backend Roadmap", "https://roadmap.sh/backend", OTHER, "Career", "What to learn to become a backend developer.", "roadmap,backend");
        r(l, "roadmap.sh Java Roadmap", "https://roadmap.sh/java", OTHER, "Career", "A visual learning path for Java developers.", "roadmap,java");
        r(l, "roadmap.sh Spring Boot Roadmap", "https://roadmap.sh/spring-boot", OTHER, "Career", "A visual learning path for Spring Boot.", "roadmap,spring");
        r(l, "roadmap.sh System Design", "https://roadmap.sh/system-design", OTHER, "System Design", "A guided path through system design topics.", "roadmap,system-design");
        r(l, "Git Immersion", "https://gitimmersion.com/", TUTORIAL, "Git", "Learn Git by doing a guided lab.", "git,tutorial");
        r(l, "Oh My Git!", "https://ohmygit.org/", TUTORIAL, "Git", "A game to learn Git concepts.", "git,game");
        r(l, "GitHub Skills", "https://skills.github.com/", TUTORIAL, "Git", "Interactive courses on GitHub features.", "github,tutorial");
        r(l, "Conventional Commits", "https://www.conventionalcommits.org/en/v1.0.0/", ARTICLE, "Git", "A convention for readable commit messages.", "git,commits");
        r(l, "Docker Curriculum", "https://docker-curriculum.com/", TUTORIAL, "DevOps", "A beginner-friendly Docker tutorial.", "docker,tutorial");
        r(l, "Play with Docker", "https://labs.play-with-docker.com/", OTHER, "DevOps", "Practise Docker in the browser.", "docker,sandbox");
        r(l, "Kubernetes Basics", "https://kubernetes.io/docs/tutorials/kubernetes-basics/", TUTORIAL, "DevOps", "Official interactive Kubernetes tutorial.", "kubernetes,devops");
        r(l, "Render Docs", "https://render.com/docs", DOCUMENTATION, "DevOps", "Deploy web services, databases and static sites.", "deploy,render");
        r(l, "OWASP Cheat Sheet Series", "https://cheatsheetseries.owasp.org/", ARTICLE, "Security", "Concise security guidance for developers.", "security,owasp");
        r(l, "OWASP WebGoat", "https://github.com/WebGoat/WebGoat", GITHUB_REPO, "Security", "A deliberately insecure app for learning web security.", "security,practice");
        r(l, "JWT.io", "https://jwt.io/introduction", TUTORIAL, "Security", "What JSON Web Tokens are and how to inspect them.", "jwt,auth");
        r(l, "Postman Academy", "https://academy.postman.com/", TUTORIAL, "APIs", "Free courses on API testing and design.", "postman,api");
        r(l, "OpenAPI Specification", "https://swagger.io/specification/", DOCUMENTATION, "APIs", "How to describe REST APIs in a standard format.", "openapi,api");
        r(l, "RESTful API Design Best Practices (Microsoft)", "https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design", ARTICLE, "APIs", "Practical guidance for designing web APIs.", "rest,api,design");
        r(l, "Pramp: Mock Interviews", "https://www.pramp.com/", OTHER, "Interview", "Practise interviews with peers for free.", "mock-interview,practice");
        r(l, "Interviewing.io", "https://interviewing.io/", OTHER, "Interview", "Anonymous mock interviews and guides.", "mock-interview,practice");
        r(l, "Cracking the Coding Interview", "https://www.crackingthecodinginterview.com/", ARTICLE, "Interview", "The best-known interview preparation book.", "interview,book");
        r(l, "Levels.fyi", "https://www.levels.fyi/", OTHER, "Career", "Salary data and company levels.", "career,salary");
        r(l, "Glassdoor Interview Questions", "https://www.glassdoor.co.in/Interview/index.htm", OTHER, "Career", "Real interview experiences by company.", "interview,companies");
        r(l, "Naukri Campus / Jobs", "https://www.naukri.com/", OTHER, "Career", "Job portal popular in India.", "jobs,india");
        r(l, "LinkedIn Jobs", "https://www.linkedin.com/jobs/", OTHER, "Career", "Search jobs and connect with recruiters.", "jobs,networking");
        r(l, "Resume Worded Tips", "https://resumeworded.com/", ARTICLE, "Career", "Resume and LinkedIn improvement guides.", "resume,career");
        r(l, "MIT 6.006 Lectures (YouTube)", "https://www.youtube.com/playlist?list=PLUl4u3cNGP63EdVPNLG3ToM6LaEUuStEY", VIDEO, "DSA", "MIT Introduction to Algorithms video lectures.", "mit,algorithms,video");
        r(l, "freeCodeCamp YouTube Channel", "https://www.youtube.com/@freecodecamp", VIDEO, "Web", "Long free video courses on programming topics.", "video,courses");
        r(l, "Amigoscode YouTube", "https://www.youtube.com/@amigoscode", VIDEO, "Spring", "Spring Boot and Java full-stack courses.", "spring,video");
        r(l, "Java Brains YouTube", "https://www.youtube.com/@Java.Brains", VIDEO, "Spring", "Clear Java and Spring tutorials.", "java,spring,video");
        r(l, "NeetCode YouTube", "https://www.youtube.com/@NeetCode", VIDEO, "DSA", "Explanations for the most common interview problems.", "dsa,video");
        r(l, "Gaurav Sen System Design (YouTube)", "https://www.youtube.com/@gkcs", VIDEO, "System Design", "System design explained with diagrams.", "system-design,video");

        return l;
    }
}
