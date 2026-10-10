package com.devvault.config.seed;

import com.devvault.entity.LearningResource;
import com.devvault.entity.ResourceType;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.entity.ResourceType.*;

/** Well-known, long-lived learning links (official docs, free courses, reference repos). */
public final class ResourceSeedData {

    private ResourceSeedData() {
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

        // Java & Spring
        r(l, "Java SE 21 API Documentation", "https://docs.oracle.com/en/java/javase/21/docs/api/", DOCUMENTATION, "Java", "The official reference for every class in the JDK.", "java,api,reference");
        r(l, "The Java Tutorials (Oracle)", "https://docs.oracle.com/javase/tutorial/", TUTORIAL, "Java", "Free official step-by-step tutorials from basics to concurrency.", "java,beginner");
        r(l, "Spring Boot Reference", "https://docs.spring.io/spring-boot/index.html", DOCUMENTATION, "Spring", "Official Spring Boot documentation.", "spring,boot");
        r(l, "Spring Guides", "https://spring.io/guides", TUTORIAL, "Spring", "Short hands-on guides: REST, JPA, security, messaging.", "spring,guides");
        r(l, "Baeldung", "https://www.baeldung.com/", ARTICLE, "Java", "Practical Java and Spring articles with code.", "java,spring,articles");
        r(l, "Hibernate ORM Documentation", "https://hibernate.org/orm/documentation/", DOCUMENTATION, "JPA", "Reference and user guides for Hibernate.", "hibernate,jpa,orm");
        r(l, "Thymeleaf Documentation", "https://www.thymeleaf.org/documentation.html", DOCUMENTATION, "Thymeleaf", "Template engine used for this app's pages.", "thymeleaf,templates");
        r(l, "Maven Getting Started", "https://maven.apache.org/guides/getting-started/", TUTORIAL, "Build Tools", "Learn POM, lifecycle and dependency management.", "maven,build");
        r(l, "JUnit 5 User Guide", "https://junit.org/junit5/docs/current/user-guide/", DOCUMENTATION, "Testing", "Writing unit tests in Java.", "junit,testing");
        r(l, "Mockito Documentation", "https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html", DOCUMENTATION, "Testing", "Mocking framework for unit tests.", "mockito,testing");

        // DSA & interviews
        r(l, "NeetCode Roadmap", "https://neetcode.io/roadmap", TUTORIAL, "DSA", "Structured roadmap of the most important interview patterns.", "dsa,roadmap,interview");
        r(l, "LeetCode Problems", "https://leetcode.com/problemset/", OTHER, "DSA", "Largest set of coding interview practice problems.", "dsa,practice");
        r(l, "VisuAlgo", "https://visualgo.net/en", TUTORIAL, "DSA", "Animated visualisations of data structures and algorithms.", "dsa,visualization");
        r(l, "Big-O Cheat Sheet", "https://www.bigocheatsheet.com/", ARTICLE, "DSA", "Time and space complexity of common algorithms.", "big-o,complexity");
        r(l, "Data Structures and Algorithms (GeeksforGeeks)", "https://www.geeksforgeeks.org/data-structures/", TUTORIAL, "DSA", "Explanations and code for every classic structure.", "dsa,beginner");
        r(l, "Algorithms, 4th Edition (Princeton)", "https://algs4.cs.princeton.edu/home/", TUTORIAL, "DSA", "Companion site of the classic Sedgewick & Wayne textbook.", "algorithms,book");
        r(l, "TheAlgorithms/Java", "https://github.com/TheAlgorithms/Java", GITHUB_REPO, "DSA", "Hundreds of algorithms implemented in Java.", "algorithms,java,github");
        r(l, "Tech Interview Handbook", "https://www.techinterviewhandbook.org/", ARTICLE, "Interview", "Free curated interview preparation guide.", "interview,prep");
        r(l, "Coding Interview University", "https://github.com/jwasham/coding-interview-university", GITHUB_REPO, "Interview", "A complete multi-month computer science study plan.", "interview,study-plan");

        // System design
        r(l, "System Design Primer", "https://github.com/donnemartin/system-design-primer", GITHUB_REPO, "System Design", "Learn how to design large-scale systems.", "system-design,scalability");
        r(l, "Designing Data-Intensive Applications", "https://dataintensive.net/", ARTICLE, "System Design", "Book site for the best reference on distributed data systems.", "distributed,databases");
        r(l, "High Scalability", "http://highscalability.com/", ARTICLE, "System Design", "Real architectures of large websites.", "architecture,scalability");
        r(l, "Martin Fowler: Patterns of Enterprise Application Architecture", "https://martinfowler.com/eaaCatalog/", ARTICLE, "Architecture", "Catalog of enterprise design patterns.", "patterns,architecture");
        r(l, "Refactoring Guru: Design Patterns", "https://refactoring.guru/design-patterns", TUTORIAL, "Design Patterns", "Illustrated explanations of all GoF patterns with Java examples.", "patterns,oop");

        // Web
        r(l, "MDN Web Docs", "https://developer.mozilla.org/", DOCUMENTATION, "Web", "The reference for HTML, CSS and JavaScript.", "html,css,javascript");
        r(l, "web.dev Learn", "https://web.dev/learn", TUTORIAL, "Web", "Free courses on CSS, HTML, accessibility and performance.", "web,css,accessibility");
        r(l, "CSS-Tricks Flexbox Guide", "https://css-tricks.com/snippets/css/a-guide-to-flexbox/", ARTICLE, "CSS", "The visual guide to Flexbox.", "css,flexbox");
        r(l, "CSS-Tricks Grid Guide", "https://css-tricks.com/snippets/css/complete-guide-grid/", ARTICLE, "CSS", "The visual guide to CSS Grid.", "css,grid");
        r(l, "JavaScript.info", "https://javascript.info/", TUTORIAL, "JavaScript", "Modern JavaScript tutorial from basics to advanced.", "javascript,tutorial");
        r(l, "TypeScript Handbook", "https://www.typescriptlang.org/docs/handbook/intro.html", DOCUMENTATION, "TypeScript", "Official TypeScript guide.", "typescript");
        r(l, "React Documentation", "https://react.dev/learn", TUTORIAL, "React", "Official, interactive React tutorial.", "react,frontend");
        r(l, "Node.js Documentation", "https://nodejs.org/en/docs", DOCUMENTATION, "Node.js", "Official Node.js API docs and guides.", "node,javascript");
        r(l, "OWASP Top 10", "https://owasp.org/www-project-top-ten/", ARTICLE, "Security", "The ten most critical web application security risks.", "security,owasp");
        r(l, "HTTP Cats", "https://http.cat/", OTHER, "HTTP", "Memorable cat pictures for every HTTP status code.", "http,status-codes");

        // Databases
        r(l, "MySQL Reference Manual", "https://dev.mysql.com/doc/refman/8.4/en/", DOCUMENTATION, "MySQL", "Official MySQL documentation.", "mysql,sql");
        r(l, "SQLBolt", "https://sqlbolt.com/", TUTORIAL, "SQL", "Interactive SQL lessons for beginners.", "sql,beginner");
        r(l, "PostgreSQL Tutorial", "https://www.postgresqltutorial.com/", TUTORIAL, "SQL", "Step-by-step PostgreSQL and SQL tutorials.", "postgresql,sql");
        r(l, "Use The Index, Luke", "https://use-the-index-luke.com/", ARTICLE, "SQL", "Everything developers should know about SQL indexing.", "sql,indexes,performance");
        r(l, "Redis Documentation", "https://redis.io/docs/latest/", DOCUMENTATION, "Redis", "Caching, queues and leaderboards with Redis.", "redis,cache");
        r(l, "MongoDB University", "https://learn.mongodb.com/", TUTORIAL, "NoSQL", "Free official MongoDB courses.", "mongodb,nosql");

        // Tools & other languages
        r(l, "Pro Git Book", "https://git-scm.com/book/en/v2", TUTORIAL, "Git", "The free official Git book.", "git,version-control");
        r(l, "Learn Git Branching", "https://learngitbranching.js.org/", TUTORIAL, "Git", "Interactive visual Git practice.", "git,interactive");
        r(l, "Docker Get Started", "https://docs.docker.com/get-started/", TUTORIAL, "DevOps", "Official Docker introduction.", "docker,containers");
        r(l, "GitHub Actions Documentation", "https://docs.github.com/en/actions", DOCUMENTATION, "DevOps", "Automate build, test and deploy workflows.", "ci,github");
        r(l, "The Linux Command Line (book)", "https://linuxcommand.org/tlcl.php", TUTORIAL, "Linux", "Free book teaching the shell.", "linux,bash");
        r(l, "Python Official Tutorial", "https://docs.python.org/3/tutorial/", TUTORIAL, "Python", "The official Python tutorial.", "python,beginner");
        r(l, "Go Tour", "https://go.dev/tour/", TUTORIAL, "Go", "Interactive tour of the Go language.", "go,beginner");
        r(l, "The Rust Book", "https://doc.rust-lang.org/book/", TUTORIAL, "Rust", "The official guide to Rust.", "rust,ownership");
        r(l, "Kotlin Docs", "https://kotlinlang.org/docs/home.html", DOCUMENTATION, "Kotlin", "Official Kotlin documentation.", "kotlin");
        r(l, "C# Documentation", "https://learn.microsoft.com/en-us/dotnet/csharp/", DOCUMENTATION, "C#", "Microsoft's C# guides and reference.", "csharp,dotnet");
        r(l, "cppreference", "https://en.cppreference.com/w/", DOCUMENTATION, "C++", "The reference for C and C++ standard libraries.", "cpp,c,reference");
        r(l, "Postman Learning Center", "https://learning.postman.com/docs/", DOCUMENTATION, "APIs", "Test and document REST APIs.", "postman,api");

        // Videos
        r(l, "CS50: Introduction to Computer Science", "https://cs50.harvard.edu/x/", VIDEO, "CS Fundamentals", "Harvard's free intro course with lectures.", "cs50,beginner");
        r(l, "freeCodeCamp", "https://www.freecodecamp.org/", TUTORIAL, "Web", "Free full-length courses and certifications.", "web,python,javascript");
        r(l, "MIT OpenCourseWare: Introduction to Algorithms", "https://ocw.mit.edu/courses/6-006-introduction-to-algorithms-spring-2020/", VIDEO, "DSA", "MIT's algorithms lectures and notes.", "algorithms,lectures");

        return l;
    }
}
