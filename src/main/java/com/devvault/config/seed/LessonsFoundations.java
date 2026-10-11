package com.devvault.config.seed;

import com.devvault.entity.Lesson;

import java.util.ArrayList;
import java.util.List;

import static com.devvault.config.seed.LessonFactory.add;

/** Chapter 1 (programming foundations) and chapter 2 (object-oriented Java). */
public final class LessonsFoundations {

    private LessonsFoundations() {
    }

    public static List<Lesson> all() {
        List<Lesson> l = new ArrayList<>();
        String c1 = "Programming Foundations";
        String c1Intro = "Every developer starts here. You will learn how a program is written, stores data, makes decisions and repeats work. Everything later (collections, Spring, algorithms) is built from these pieces, so interviewers quietly check that you know them cold.";

        add(l, 1, c1, c1Intro, 1, "Your First Program: How Java Runs",
                "Before anything else you must know what a program is and how the computer turns your text into a running application. Interviewers ask 'what happens when you run a Java program?' to test whether you understand the basics, not just syntax.",
                "A Java file is plain text. The compiler (javac) turns it into bytecode (.class). The JVM (Java Virtual Machine) then runs that bytecode on any operating system. That is why Java is called 'write once, run anywhere'. Execution always starts in a method called main.",
                "Java",
                """
                public class Hello {
                    public static void main(String[] args) {
                        String name = "DevVault";
                        System.out.println("Welcome to " + name);
                    }
                }""",
                """
                public class Hello ==> Declares a class. In Java all code lives inside classes. The file must be named Hello.java because the class is public.
                public static void main(String[] args) ==> The exact entry point the JVM looks for. public: the JVM can call it. static: no object needed. void: returns nothing. args: command-line words.
                String name = "DevVault"; ==> Creates a variable named name that holds text. String is a type, the semicolon ends the statement.
                System.out.println(...) ==> Prints a line to the console. System.out is the standard output stream, println adds a new line.
                "Welcome to " + name ==> The + joins (concatenates) two strings into one new string.""",
                "Be ready to explain JDK vs JRE vs JVM: the JDK has the compiler and tools, the JRE has the libraries and JVM, the JVM executes bytecode. Also be able to explain why main is static.",
                "Forgetting the semicolon; naming the file differently from the public class; confusing = (assign) with == (compare).");

        add(l, 1, c1, null, 2, "Variables and Data Types",
                "Programs are about data. Choosing the right type decides how much memory you use, whether decimals stay exact and whether numbers overflow. Wrong types cause real bugs in billing and counting systems.",
                "A variable is a named box with a type. Primitive types (int, long, double, boolean, char) hold the value directly. Reference types (String, arrays, objects) hold an address that points to the object in memory. int holds about +-2.1 billion; use long beyond that. Never use double for money.",
                "Java",
                """
                int age = 21;
                long population = 8_000_000_000L;
                double price = 19.99;
                boolean isMember = true;
                char grade = 'A';
                String city = "Pune";
                final int MAX_TRIES = 3;
                var total = age + 1;""",
                """
                int age = 21; ==> int stores whole numbers up to about 2.1 billion. 21 is a literal value.
                long population = 8_000_000_000L; ==> The number is bigger than int allows, so we use long. The L suffix tells Java the literal is a long. Underscores only make it readable.
                double price = 19.99; ==> A 64-bit decimal number. Fast but not exact (0.1 + 0.2 is not 0.3), so for money use BigDecimal or whole cents.
                boolean isMember = true; ==> Only true or false. Used for conditions in if and while.
                char grade = 'A'; ==> One single character in single quotes. Strings use double quotes.
                String city = "Pune"; ==> A String is an object, not a primitive. It is immutable: operations create new strings.
                final int MAX_TRIES = 3; ==> final means the value can never change. Constants are written in UPPER_CASE by convention.
                var total = age + 1; ==> var lets the compiler infer the type (int). It is still strongly typed, just shorter to write.""",
                "Expect: 'difference between primitive and reference types', 'why is String immutable', 'int vs Integer'. Show you know that == compares references for objects and you must use equals for content.",
                "Using == to compare Strings; storing money in double; integer overflow (Integer.MAX_VALUE + 1 becomes negative).");

        add(l, 1, c1, null, 3, "Operators and Expressions",
                "Operators build every calculation, condition and loop. Integer division and modulo are favourite interview traps, and % is the key to FizzBuzz, even/odd, wrapping around arrays and hashing.",
                "Arithmetic: + - * / %. Comparison: == != < > <= >=. Logical: && (and), || (or), ! (not). Integer division throws away the decimal part: 7 / 2 is 3. The remainder operator % gives what is left over: 7 % 2 is 1. && and || short-circuit, meaning the right side is skipped when the answer is already known.",
                "Java",
                """
                int a = 7, b = 2;
                int q = a / b;            // 3
                double d = (double) a / b; // 3.5
                int r = a % b;            // 1
                boolean even = a % 2 == 0;
                boolean ok = b != 0 && a / b > 1;
                a += 5;
                int next = a++;""",
                """
                int q = a / b; ==> Both operands are int, so the result is int: 3. The .5 is silently dropped.
                (double) a / b ==> The cast turns a into 7.0 first, so the division is decimal and gives 3.5. Cast before dividing, not after.
                a % b ==> Remainder after division: 7 = 3*2 + 1. Used for even/odd, cycles and hashing.
                a % 2 == 0 ==> Standard even check. Works because every even number leaves remainder 0.
                b != 0 && a / b > 1 ==> Short-circuit: if b is 0 the left side is false and the division is never evaluated, which avoids a crash.
                a += 5; ==> Shorthand for a = a + 5.
                int next = a++; ==> Post-increment: next receives the OLD value, then a increases. ++a would increase first.""",
                "Classic questions: predict the output of i++ + ++i, explain short-circuit evaluation, and why 7/2 is 3. Say out loud that you would cast before dividing.",
                "Dividing two ints and expecting decimals; dividing by zero (ArithmeticException for ints); using & instead of &&.");

        add(l, 1, c1, null, 4, "Making Decisions: if, else and switch",
                "Software constantly chooses between paths: is the password right, is the stock available, which discount applies. Clean branching is also how you handle edge cases like null or empty input that interviewers love to test.",
                "if runs a block when a boolean condition is true; else if and else handle the other cases, checked top to bottom, and the first match wins. switch compares one value against many constants and is neater than a long chain. Modern switch expressions use -> and return a value.",
                "Java",
                """
                static String discount(int quantity, boolean member) {
                    if (quantity <= 0) {
                        return "Invalid quantity";
                    }
                    if (member && quantity >= 10) {
                        return "25%";
                    } else if (quantity >= 10) {
                        return "10%";
                    }
                    return "none";
                }

                static int daysIn(int month) {
                    return switch (month) {
                        case 2 -> 28;
                        case 4, 6, 9, 11 -> 30;
                        default -> 31;
                    };
                }""",
                """
                if (quantity <= 0) { return ... } ==> A guard clause: reject bad input first and return early. This keeps the rest of the method free from deep nesting.
                member && quantity >= 10 ==> Both conditions must be true. More specific rules (members) are tested before general ones, otherwise the general rule would always win.
                else if (quantity >= 10) ==> Reached only when the first condition failed, so this branch means non-member with 10 or more items.
                return "none"; ==> The default path when no rule matched. Every code path must return something.
                return switch (month) { ==> A switch expression: it produces a value that is returned directly.
                case 4, 6, 9, 11 -> 30; ==> Several constants can share one result, replacing four separate case lines.
                default -> 31; ==> Required in a switch expression so every possible month has an answer.""",
                "Interviewers watch for edge cases: do you check null, zero, negative values first? Mention guard clauses and that ordering conditions from most specific to most general avoids wrong branches.",
                "Using = instead of == inside a condition; forgetting break in an old-style switch (fall-through); putting a general condition before a specific one.");

        add(l, 1, c1, null, 5, "Loops: for, while and for-each",
                "Loops let a few lines process a million items. Almost every algorithm problem is a loop with a clever idea inside, and the loop bounds are where off-by-one bugs live.",
                "for is used when you know how many times to repeat. while repeats as long as a condition stays true. for-each walks through every element of an array or collection without an index. break leaves the loop; continue skips to the next round.",
                "Java",
                """
                int[] scores = {70, 85, 92, 60};
                int sum = 0;
                for (int i = 0; i < scores.length; i++) {
                    sum += scores[i];
                }
                double avg = (double) sum / scores.length;

                int best = Integer.MIN_VALUE;
                for (int s : scores) {
                    if (s > best) best = s;
                }

                int n = 27, steps = 0;
                while (n != 1) {
                    n = (n % 2 == 0) ? n / 2 : 3 * n + 1;
                    steps++;
                }""",
                """
                for (int i = 0; i < scores.length; i++) ==> Start at index 0, continue while i is smaller than the length, then add 1. Arrays are zero-based, so the last index is length - 1; using <= would crash.
                sum += scores[i]; ==> Accumulator pattern: keep a running total outside the loop and update it each round.
                (double) sum / scores.length ==> Cast so the average keeps its decimals.
                int best = Integer.MIN_VALUE; ==> Start with the smallest possible value so any real score replaces it. Starting at 0 would fail for negative numbers.
                for (int s : scores) ==> For-each: read-only walk over every element, no index to get wrong.
                while (n != 1) ==> Used when the number of rounds is unknown in advance. The loop MUST change n or it never ends.
                steps++; ==> Counts the rounds. Incrementing by one each time is the simplest loop counter.""",
                "Be able to state the time complexity of a loop (one loop is O(n), nested loops are O(n^2)). Interviewers also check that your loop ends and that your bounds are right at the first and last element.",
                "Off-by-one errors (<= instead of <); infinite loops because the variable never changes; modifying a list while looping over it.");

        add(l, 1, c1, null, 6, "Methods: Reusing and Organising Code",
                "Methods are how you stop copy-pasting. Small, well-named methods make code readable and testable, which is exactly what reviewers read for in a take-home or live coding round.",
                "A method has a name, parameters (inputs), a return type (output) and a body. Java passes arguments by value: primitives are copied, and for objects the reference is copied (so the method can change the object but cannot make your variable point somewhere else). Overloading means several methods with the same name but different parameters.",
                "Java",
                """
                public class Calc {
                    static int square(int x) {
                        return x * x;
                    }

                    static double average(int... numbers) {
                        if (numbers.length == 0) return 0;
                        int sum = 0;
                        for (int n : numbers) sum += n;
                        return (double) sum / numbers.length;
                    }

                    static void addBonus(int[] marks) {
                        for (int i = 0; i < marks.length; i++) marks[i] += 5;
                    }
                }""",
                """
                static int square(int x) ==> static: callable as Calc.square(4) without creating an object. int before the name is the return type, int x is the parameter.
                return x * x; ==> Sends the result back to the caller and ends the method.
                int... numbers ==> Varargs: the caller may pass any number of ints, and inside the method they arrive as an array.
                if (numbers.length == 0) return 0; ==> Handles the empty case so we never divide by zero.
                static void addBonus(int[] marks) ==> void means nothing is returned. The array reference is copied, but it points to the SAME array, so changes are visible to the caller.
                marks[i] += 5; ==> Modifies the caller's data. This is why methods that mutate their input should be documented.""",
                "Typical question: 'Is Java pass-by-value or pass-by-reference?' The correct answer is always pass-by-value (the value of a reference is copied). Also expect to explain method overloading versus overriding.",
                "Believing a method can reassign the caller's variable; very long methods that do many things; not handling empty input.");

        add(l, 1, c1, null, 7, "Arrays: Your First Data Structure",
                "Arrays are the base of lists, strings, matrices and heaps. Most beginner and intermediate interview problems are array problems, and they teach index thinking.",
                "An array stores a fixed number of items of the same type in consecutive memory, so reading arr[i] is instant (O(1)). The size cannot change after creation. Searching an unsorted array means checking items one by one (O(n)). 2D arrays are arrays of arrays.",
                "Java",
                """
                int[] nums = new int[5];
                nums[0] = 10;
                int[] primes = {2, 3, 5, 7, 11};
                int last = primes[primes.length - 1];

                int[][] grid = new int[3][4];
                grid[1][2] = 9;

                int[] copy = java.util.Arrays.copyOf(primes, 7);
                java.util.Arrays.sort(copy);""",
                """
                new int[5] ==> Allocates 5 ints, all automatically 0. Valid indexes are 0 to 4.
                nums[0] = 10; ==> Writing by index is O(1) because the address is start + index * size.
                {2, 3, 5, 7, 11} ==> Array literal: Java works out the length (5) for you.
                primes[primes.length - 1] ==> The last item. length is a field (no parentheses), unlike String.length().
                new int[3][4] ==> 3 rows and 4 columns. grid[1][2] means row 1, column 2.
                Arrays.copyOf(primes, 7) ==> Arrays have fixed size, so to grow you create a bigger copy. The two new slots are filled with 0.
                Arrays.sort(copy) ==> Sorts in place in O(n log n). Sorting first often turns a hard problem into an easy one.""",
                "Know the complexity: access O(1), search O(n), insert in the middle O(n), binary search on sorted O(log n). Be ready for follow-ups: how would you remove duplicates, rotate, or find the second largest in one pass?",
                "ArrayIndexOutOfBoundsException (index equals length); assuming arrays resize; comparing arrays with == instead of Arrays.equals.");

        String c2 = "Object-Oriented Java";
        String c2Intro = "Real applications are built from objects that model things like Customer, Order or Problem. This chapter covers the four pillars of OOP, interfaces, exceptions and generics - the topics behind most Java interview questions and every Spring project.";

        add(l, 2, c2, c2Intro, 1, "Classes and Objects",
                "A class is the blueprint and an object is the real thing built from it. Every Spring entity, service and controller is a class, so this is the vocabulary for the rest of the course.",
                "A class defines fields (data) and methods (behaviour). new creates an object in heap memory and returns a reference to it. Two variables can reference the same object. Instance members belong to each object; static members belong to the class and are shared.",
                "Java",
                """
                public class Problem {
                    private String title;
                    private int attempts;
                    private static int totalCreated = 0;

                    public Problem(String title) {
                        this.title = title;
                        totalCreated++;
                    }

                    public void attempt() {
                        attempts++;
                    }

                    public String getTitle() {
                        return title;
                    }
                }

                Problem p = new Problem("Two Sum");
                p.attempt();""",
                """
                private String title; ==> A field hidden from other classes. Only this class can touch it directly (encapsulation).
                private static int totalCreated ==> static: ONE copy shared by all Problem objects, useful for counters.
                public Problem(String title) ==> A constructor: same name as the class, no return type. It runs once when new is used and sets up a valid object.
                this.title = title; ==> this means 'the current object', which separates the field from the parameter with the same name.
                public void attempt() ==> A method that changes the object's own state (attempts).
                public String getTitle() ==> A getter: controlled read access to a private field.
                new Problem("Two Sum") ==> Allocates memory for a new object, runs the constructor and returns a reference stored in p.""",
                "Expect: 'what is the difference between a class and an object', 'what does static mean', 'what is the heap vs the stack'. In Spring interviews, explain that Spring creates and manages many of these objects for you (beans).",
                "Making every field public; forgetting that two variables can point to one object; using static for things that should differ per object.");

        add(l, 2, c2, null, 2, "Encapsulation, Constructors and Immutability",
                "Hiding internal data and guarding it with rules is how you keep an object always valid. Immutable objects are thread-safe and easy to reason about, which interviewers love to hear.",
                "Encapsulation means private fields plus public methods that enforce rules. Constructors can validate input so an invalid object can never exist. An immutable object (like String) cannot change after creation: fields are final and there are no setters. Java records give you immutable data classes in one line.",
                "Java",
                """
                public final class Money {
                    private final long cents;

                    public Money(long cents) {
                        if (cents < 0) throw new IllegalArgumentException("negative");
                        this.cents = cents;
                    }

                    public Money plus(Money other) {
                        return new Money(this.cents + other.cents);
                    }

                    public long getCents() { return cents; }
                }

                record Point(int x, int y) { }""",
                """
                public final class Money ==> final class: nobody can subclass it and break its guarantees.
                private final long cents; ==> final field: assigned exactly once in the constructor, never changed again. Whole cents avoid decimal errors.
                if (cents < 0) throw ... ==> The constructor refuses invalid data, so every Money object in the program is guaranteed to be valid.
                return new Money(...) ==> Operations return a NEW object instead of changing this one. That is what makes it immutable.
                public long getCents() ==> Read access only. There is deliberately no setter.
                record Point(int x, int y) ==> One line that generates the constructor, getters (x(), y()), equals, hashCode and toString.""",
                "Question: 'How do you create an immutable class?' Answer: final class, private final fields, no setters, defensive copies of mutable fields. Also be ready to explain why immutable objects are safe to share between threads.",
                "Adding a setter 'just in case'; returning a reference to an internal mutable list; skipping validation in the constructor.");

        add(l, 2, c2, null, 3, "Inheritance and Polymorphism",
                "Inheritance lets you reuse behaviour and polymorphism lets you write code that works with many types. Together they explain how frameworks plug in your classes, and they are the most asked OOP topic.",
                "A subclass extends a parent class and inherits its fields and methods. It can override a method to change the behaviour. Polymorphism means a parent-type variable can hold a child object and the child's version of the method runs at runtime (dynamic dispatch). Overriding = same signature in a subclass. Overloading = same name, different parameters in one class.",
                "Java",
                """
                class Notification {
                    void send(String msg) {
                        System.out.println("Generic: " + msg);
                    }
                }

                class EmailNotification extends Notification {
                    @Override
                    void send(String msg) {
                        System.out.println("Email: " + msg);
                    }
                }

                Notification n = new EmailNotification();
                n.send("Welcome");""",
                """
                class EmailNotification extends Notification ==> extends = 'is a'. An EmailNotification IS A Notification and inherits everything it has.
                @Override ==> Asks the compiler to check that the method really overrides something. A typo in the name becomes a compile error instead of a silent bug.
                void send(String msg) ==> Same name and parameters as the parent, so this replaces the parent's behaviour for email objects.
                Notification n = new EmailNotification(); ==> The variable type is the parent, the object is the child. This is polymorphism.
                n.send("Welcome"); ==> At runtime Java looks at the real object (EmailNotification) and runs its send, printing 'Email: Welcome'.""",
                "Know the difference between overriding and overloading, what super does, why Java has no multiple class inheritance, and the rule 'prefer composition over inheritance'. A common follow-up: what happens if a constructor calls an overridable method?",
                "Overusing inheritance when 'has a' (composition) fits; forgetting @Override; confusing the compile-time type with the runtime type.");

        add(l, 2, c2, null, 4, "Interfaces and Abstract Classes",
                "Interfaces are the way big systems stay flexible: code against a contract, swap the implementation later. Spring injects interface types everywhere, and testing with mocks depends on them.",
                "An interface lists methods a class promises to provide. A class can implement many interfaces. An abstract class can hold shared state and partial code but cannot be instantiated. Use an interface for a capability (Comparable, Runnable) and an abstract class when subclasses share real code. Interfaces may have default methods.",
                "Java",
                """
                interface PaymentMethod {
                    boolean pay(long cents);
                }

                class CardPayment implements PaymentMethod {
                    public boolean pay(long cents) {
                        return cents < 1_000_00;
                    }
                }

                class UpiPayment implements PaymentMethod {
                    public boolean pay(long cents) { return true; }
                }

                class Checkout {
                    private final PaymentMethod method;
                    Checkout(PaymentMethod method) { this.method = method; }
                    boolean complete(long total) { return method.pay(total); }
                }""",
                """
                interface PaymentMethod ==> A contract: anything that can pay must offer pay(long). It says WHAT, not HOW.
                boolean pay(long cents); ==> No body. Methods in an interface are public and abstract automatically.
                class CardPayment implements PaymentMethod ==> Promises to fulfil the contract; the compiler enforces that pay is implemented.
                private final PaymentMethod method; ==> Checkout depends on the interface, not on a specific class. That is loose coupling.
                Checkout(PaymentMethod method) ==> The dependency is passed in (dependency injection). Spring does exactly this for your services.
                method.pay(total) ==> Works for card, UPI or any future payment type without changing Checkout.""",
                "Expect: interface vs abstract class, what are default methods, what is the dependency inversion idea, and how mocking works. Showing constructor injection earns points because it is how Spring recommends wiring beans.",
                "Putting too much into one big interface; depending on concrete classes; forgetting that interface fields are constants.");

        add(l, 2, c2, null, 5, "Exceptions: Handling the Unexpected",
                "Files go missing, networks fail and users type nonsense. How you handle errors separates demo code from production code, and error handling is a standard interview topic.",
                "An exception is an object thrown when something goes wrong. Checked exceptions (IOException) must be caught or declared. Unchecked exceptions (RuntimeException such as NullPointerException) do not have to be. try runs risky code, catch handles a specific failure, finally always runs, and try-with-resources closes resources automatically.",
                "Java",
                """
                static int parseAge(String text) {
                    try {
                        int age = Integer.parseInt(text);
                        if (age < 0) {
                            throw new IllegalArgumentException("Age cannot be negative");
                        }
                        return age;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Not a number: " + text, e);
                    } finally {
                        System.out.println("parse attempted");
                    }
                }""",
                """
                try { ==> Wraps code that might fail. If an exception happens, Java jumps straight to a matching catch.
                Integer.parseInt(text) ==> Throws NumberFormatException for input like 'abc'.
                throw new IllegalArgumentException(...) ==> We detect a business rule violation ourselves and signal it with a clear message.
                catch (NumberFormatException e) ==> Catch the SPECIFIC exception, never a blanket Exception, so you do not hide unrelated bugs.
                new IllegalArgumentException("...", e) ==> Pass the original exception as the cause so the full stack trace is preserved.
                finally { ... } ==> Runs whether or not an exception happened. Used to release resources or log.""",
                "Be able to list checked vs unchecked, explain try-with-resources, and describe a global exception handler (Spring's @RestControllerAdvice) returning a consistent error response. Never swallow an exception silently.",
                "Empty catch blocks; catching Exception or Throwable everywhere; losing the original cause; using exceptions for normal control flow.");

        add(l, 2, c2, null, 6, "Generics and Enums",
                "Generics give type safety without duplicating code, and enums replace magic strings. Both appear in every collection and every domain model, including this project's Difficulty and ResourceType.",
                "Generics let a class or method work with a type chosen by the caller (List<String>), and the compiler checks it. They are erased at runtime (type erasure). An enum is a fixed set of named constants and can have fields and methods. JPA stores enums as text with @Enumerated(EnumType.STRING).",
                "Java",
                """
                class Box<T> {
                    private T value;
                    Box(T value) { this.value = value; }
                    T get() { return value; }
                }

                static <T extends Comparable<T>> T max(T a, T b) {
                    return a.compareTo(b) >= 0 ? a : b;
                }

                enum Difficulty {
                    EASY, MEDIUM, HARD
                }

                Difficulty d = Difficulty.HARD;""",
                """
                class Box<T> ==> T is a type parameter, a placeholder. Box<String> holds text, Box<Integer> holds numbers, with one class.
                T get() ==> Returns exactly the type chosen by the caller, so no casting is needed.
                <T extends Comparable<T>> ==> Restricts T to types that can be compared. Without it a.compareTo(b) would not compile.
                a.compareTo(b) >= 0 ? a : b ==> compareTo returns negative, zero or positive. The ternary picks the larger value.
                enum Difficulty { EASY, MEDIUM, HARD } ==> A fixed list of allowed values. Typos like 'HRAD' become compile errors instead of bad data.
                Difficulty.HARD ==> Enum constants are singletons, so == comparison is safe and fast.""",
                "Common questions: what is type erasure, what is a bounded type, why prefer enums over constants, and why store JPA enums as STRING (adding or reordering constants would corrupt ORDINAL numbers).",
                "Using raw types (List with no type); storing enums by ordinal in a database; trying to create new T() inside a generic class.");

        return l;
    }
}
