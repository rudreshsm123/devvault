package com.devvault.drill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ArrayDeque;
import java.util.Deque;

import static com.devvault.drill.Gen.pick;
import static com.devvault.drill.Gen.program;
import static com.devvault.drill.Gen.programWith;
import static com.devvault.drill.Gen.ri;

/** "What does this Java program print?" questions. The answer of each one is produced by running the same logic. */
final class JavaFamilies {

    private JavaFamilies() {
    }

    static List<Family> all() {
        List<Family> l = new ArrayList<>();
        l.add(new Family("div-mod", "Java Basics", "Integer division and remainder", "java-basics", 3, JavaFamilies::divMod));
        l.add(new Family("increment", "Java Basics", "Pre and post increment", "java-basics", 3, JavaFamilies::increment));
        l.add(new Family("loop-sum", "Java Basics", "Loop sums", "java-basics", 3, JavaFamilies::loopSum));
        l.add(new Family("nested-loops", "Java Basics", "Nested loop counts", "java-basics", 3, JavaFamilies::nestedLoops));
        l.add(new Family("string-methods", "Strings", "String methods", "strings", 3, JavaFamilies::stringMethods));
        l.add(new Family("concat", "Strings", "Operator order with +", "strings", 3, JavaFamilies::concat));
        l.add(new Family("short-circuit", "Java Basics", "Short-circuit evaluation", "java-basics", 2, JavaFamilies::shortCircuit));
        l.add(new Family("array-ops", "Arrays", "Array updates", "arrays", 3, JavaFamilies::arrayOps));
        l.add(new Family("overflow", "Java Basics", "Overflow and casts", "java-basics", 3, JavaFamilies::overflow));
        l.add(new Family("char-math", "Java Basics", "char arithmetic", "java-basics", 2, JavaFamilies::charMath));
        l.add(new Family("bitwise", "Bit Manipulation", "Bitwise operators", "bits", 3, JavaFamilies::bitwise));
        l.add(new Family("recursion", "Recursion", "Recursive methods", "recursion", 3, JavaFamilies::recursion));
        l.add(new Family("string-builder", "Strings", "StringBuilder edits", "strings", 3, JavaFamilies::stringBuilder));
        l.add(new Family("integer-cache", "Java Basics", "Integer == and equals", "java-basics", 1, JavaFamilies::integerCache));
        l.add(new Family("switch-fall", "Java Basics", "switch fall-through", "java-basics", 2, JavaFamilies::switchFall));
        l.add(new Family("math-functions", "Java Basics", "Math and rounding", "java-basics", 2, JavaFamilies::mathFunctions));
        l.add(new Family("list-remove", "Collections", "List remove tricks", "collections", 2, JavaFamilies::listRemove));
        l.add(new Family("map-count", "Collections", "HashMap counting", "collections", 3, JavaFamilies::mapCount));
        l.add(new Family("stack-queue", "Collections", "Stack and queue", "collections", 3, JavaFamilies::stackQueue));
        return l;
    }

    // ---------------------------------------------------------------- Java basics

    static Spec divMod(Random r, int tier) {
        int a = tier == 0 ? ri(r, 10, 99) : ri(r, -99, 99);
        int b = tier == 2 ? pick(r, -9, -7, -5, -4, -3, -2, 2, 3, 4, 5, 7, 9) : ri(r, 2, 9);
        String code = program("", "int a = " + a + ";\nint b = " + b + ";\nSystem.out.println(a / b + \" \" + a % b);");
        String answer = (a / b) + " " + (a % b);
        return Spec.output("Integer division and remainder", "What does this program print?", code, answer,
                "Java integer division drops the fraction (it truncates toward zero) and % keeps the sign of the "
                        + "left operand. " + a + " / " + b + " = " + (a / b) + " and " + a + " % " + b + " = "
                        + (a % b) + ", because " + (a / b) + " * " + b + " + " + (a % b) + " = " + a + ".", true);
    }

    static Spec increment(Random r, int tier) {
        int n = ri(r, 1, 20);
        int p = ri(r, 0, tier == 0 ? 1 : 3);
        String expr;
        int x = n;
        int y;
        switch (p) {
            case 0 -> { expr = "x++ + ++x"; y = x++ + ++x; }
            case 1 -> { expr = "++x + x++"; y = ++x + x++; }
            case 2 -> { expr = "x-- - --x"; y = x-- - --x; }
            default -> { expr = "x++ * 2 + x"; y = x++ * 2 + x; }
        }
        String code = program("", "int x = " + n + ";\nint y = " + expr + ";\nSystem.out.println(x + \" \" + y);");
        return Spec.output("Pre and post increment", "What does this program print?", code, x + " " + y,
                "x++ gives the current value and then increases x; ++x increases first and then gives the new value. "
                        + "Evaluate left to right, updating x after each increment. Final x = " + x + ", y = " + y + ".", true);
    }

    static Spec loopSum(Random r, int tier) {
        int start = tier == 0 ? 1 : ri(r, -5, 5);
        int step = tier == 0 ? 1 : ri(r, 2, 4);
        int end = start + ri(r, 6, 20) * step;
        int sum = 0;
        String body;
        if (tier < 2) {
            for (int i = start; i < end; i += step) {
                sum += i;
            }
            body = "sum += i;";
        } else {
            int mod = ri(r, 2, 4);
            for (int i = start; i < end; i += step) {
                if (i % mod != 0) {
                    sum += i;
                } else {
                    sum -= 1;
                }
            }
            body = "if (i % " + mod + " != 0) {\n        sum += i;\n    } else {\n        sum -= 1;\n    }";
        }
        String code = program("", "int sum = 0;\nfor (int i = " + start + "; i < " + end + "; i += " + step + ") {\n    "
                + body + "\n}\nSystem.out.println(sum);");
        return Spec.output("Loop sums", "What does this program print?", code, String.valueOf(sum),
                "Trace the loop: i starts at " + start + ", grows by " + step + " and stops before " + end
                        + ". Add up the values (or apply the if/else rule) to get " + sum + ".", true);
    }

    static Spec nestedLoops(Random r, int tier) {
        int n = ri(r, 3, 8);
        int m = ri(r, 3, 8);
        int p = ri(r, 0, tier == 0 ? 0 : 3);
        int count = 0;
        String loops;
        switch (p) {
            case 0 -> {
                for (int i = 0; i < n; i++) for (int j = 0; j < m; j++) count++;
                loops = "for (int i = 0; i < " + n + "; i++) {\n    for (int j = 0; j < " + m + "; j++) {\n        count++;\n    }\n}";
            }
            case 1 -> {
                for (int i = 0; i < n; i++) for (int j = i; j < n; j++) count++;
                loops = "for (int i = 0; i < " + n + "; i++) {\n    for (int j = i; j < " + n + "; j++) {\n        count++;\n    }\n}";
            }
            case 2 -> {
                for (int i = 0; i < n; i++) for (int j = 0; j < m; j++) if ((i + j) % 2 == 0) count++;
                loops = "for (int i = 0; i < " + n + "; i++) {\n    for (int j = 0; j < " + m + "; j++) {\n        if ((i + j) % 2 == 0) {\n            count++;\n        }\n    }\n}";
            }
            default -> {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        if (j > i) break;
                        count++;
                    }
                }
                loops = "for (int i = 0; i < " + n + "; i++) {\n    for (int j = 0; j < " + m + "; j++) {\n        if (j > i) {\n            break;\n        }\n        count++;\n    }\n}";
            }
        }
        String code = program("", "int count = 0;\n" + loops + "\nSystem.out.println(count);");
        return Spec.output("Nested loop counts", "What does this program print?", code, String.valueOf(count),
                "Count how many times the inner statement runs: " + count + ".", true);
    }

    // ---------------------------------------------------------------- strings

    static Spec stringMethods(Random r, int tier) {
        String s = pick(r, Gen.WORDS);
        int len = s.length();
        int p = ri(r, 0, tier == 0 ? 2 : 5);
        String expr;
        String result;
        switch (p) {
            case 0 -> {
                int a = ri(r, 0, len / 2), b = ri(r, a + 1, len);
                expr = "s.substring(" + a + ", " + b + ")";
                result = s.substring(a, b);
            }
            case 1 -> {
                char c = r.nextBoolean() ? s.charAt(ri(r, 0, len - 1)) : 'z';
                expr = "s.indexOf('" + c + "')";
                result = String.valueOf(s.indexOf(c));
            }
            case 2 -> {
                int k = ri(r, 0, len - 1);
                expr = "s.charAt(" + k + ") + \"\" + s.length()";
                result = s.charAt(k) + "" + s.length();
            }
            case 3 -> {
                char c = s.charAt(ri(r, 0, len - 1));
                expr = "s.lastIndexOf('" + c + "') + \" \" + s.indexOf('" + c + "')";
                result = s.lastIndexOf(c) + " " + s.indexOf(c);
            }
            case 4 -> {
                int a = ri(r, 0, len / 2);
                expr = "s.toUpperCase().substring(" + a + ", " + (a + 3) + ")";
                result = s.toUpperCase().substring(a, a + 3);
            }
            default -> {
                char c = s.charAt(ri(r, 0, len - 1));
                char d = (char) ('a' + ri(r, 0, 25));
                expr = "s.replace('" + c + "', '" + d + "')";
                result = s.replace(c, d);
            }
        }
        String code = program("", "String s = \"" + s + "\";\nSystem.out.println(" + expr + ");");
        return Spec.output("String methods", "What does this program print?", code, result,
                "Strings are indexed from 0. indexOf returns -1 when the character is missing, substring(a, b) "
                        + "excludes position b, and lastIndexOf searches from the end. Result: " + result + ".", true);
    }

    static Spec concat(Random r, int tier) {
        int a = ri(r, 1, 9), b = ri(r, 1, 9), c = ri(r, 1, 9), d = ri(r, 1, 9);
        int p = ri(r, 0, tier == 0 ? 1 : 3);
        String expr;
        String result;
        switch (p) {
            case 0 -> { expr = a + " + " + b + " + \"-\" + " + c + " + " + d; result = a + b + "-" + c + d; }
            case 1 -> { expr = "\"\" + " + a + " + " + b + " + " + c; result = "" + a + b + c; }
            case 2 -> { expr = a + " + " + b + " + " + c + " + \"!\""; result = a + b + c + "!"; }
            default -> {
                char ch = (char) ('a' + ri(r, 0, 5));
                expr = "'" + ch + "' + " + a + " + \"x\" + " + b;
                result = ch + a + "x" + b;
            }
        }
        String code = program("", "System.out.println(" + expr + ");");
        return Spec.output("Operator order with +", "What does this program print?", code, result,
                "+ works left to right. While both sides are numbers (a char counts as a number) it adds; as soon as "
                        + "a String is involved it joins text. Result: " + result + ".", true);
    }

    static Spec stringBuilder(Random r, int tier) {
        StringBuilder sb = new StringBuilder(pick(r, "dev", "code", "vault", "java", "loop"));
        StringBuilder code = new StringBuilder("StringBuilder sb = new StringBuilder(\"" + sb + "\");\n");
        int ops = tier == 0 ? 2 : tier == 1 ? 3 : 4;
        for (int i = 0; i < ops; i++) {
            int op = ri(r, 0, 4);
            int len = sb.length();
            switch (op) {
                case 0 -> { String w = pick(r, "x", "yz", "ok", "42"); sb.append(w); code.append("sb.append(\"").append(w).append("\");\n"); }
                case 1 -> { int at = ri(r, 0, len); String w = pick(r, "-", "AB", "7"); sb.insert(at, w); code.append("sb.insert(").append(at).append(", \"").append(w).append("\");\n"); }
                case 2 -> { sb.reverse(); code.append("sb.reverse();\n"); }
                case 3 -> { int at = ri(r, 0, len - 1); sb.deleteCharAt(at); code.append("sb.deleteCharAt(").append(at).append(");\n"); }
                default -> { int a = ri(r, 0, len - 1), b = ri(r, a, len); String w = pick(r, "#", "Q"); sb.replace(a, b, w); code.append("sb.replace(").append(a).append(", ").append(b).append(", \"").append(w).append("\");\n"); }
            }
        }
        code.append("System.out.println(sb);");
        return Spec.output("StringBuilder edits", "What does this program print?", program("", code.toString()), sb.toString(),
                "Apply each call in order to the same StringBuilder. insert(i, s) puts text before position i, "
                        + "replace(a, b, s) replaces positions a up to (not including) b. Result: " + sb + ".", true);
    }

    // ---------------------------------------------------------------- arrays, numbers

    static Spec arrayOps(Random r, int tier) {
        int n = ri(r, 4, 6);
        int[] a = Gen.randomArray(r, n, 1, 9);
        StringBuilder code = new StringBuilder("int[] a = " + Gen.arrayLiteral(a).replace("{", "{").replace("}", "}") + ";\n");
        int ops = tier == 0 ? 1 : tier == 1 ? 2 : 3;
        for (int i = 0; i < ops; i++) {
            int op = ri(r, 0, 3);
            int x = ri(r, 0, n - 1), y = ri(r, 0, n - 1), z = ri(r, 0, n - 1);
            switch (op) {
                case 0 -> { a[x] = a[y] + a[z]; code.append("a[").append(x).append("] = a[").append(y).append("] + a[").append(z).append("];\n"); }
                case 1 -> { a[x] *= 2; code.append("a[").append(x).append("] *= 2;\n"); }
                case 2 -> { a[0] = a[a.length - 1] - a[y]; code.append("a[0] = a[a.length - 1] - a[").append(y).append("];\n"); }
                default -> { int t = a[x]; a[x] = a[y]; a[y] = t; code.append("int t").append(i).append(" = a[").append(x).append("];\na[").append(x).append("] = a[").append(y).append("];\na[").append(y).append("] = t").append(i).append(";\n"); }
            }
        }
        code.append("System.out.println(Arrays.toString(a));");
        return Spec.output("Array updates", "What does this program print?", program("import java.util.Arrays;", code.toString()),
                Arrays.toString(a), "Run the statements top to bottom, remembering that each one sees the changes made "
                        + "by the earlier ones. The final array is " + Arrays.toString(a) + ".", true);
    }

    static Spec overflow(Random r, int tier) {
        int p = ri(r, 0, tier == 0 ? 1 : 4);
        String code;
        String answer;
        String why;
        switch (p) {
            case 0 -> {
                int k = ri(r, 0, 5), m = ri(r, k + 1, k + 8);
                int x = Integer.MAX_VALUE - k;
                x += m;
                code = program("", "int x = Integer.MAX_VALUE - " + k + ";\nx += " + m + ";\nSystem.out.println(x);");
                answer = String.valueOf(x);
                why = "int is 32 bits, so adding past Integer.MAX_VALUE (2147483647) wraps around to the negative side.";
            }
            case 1 -> {
                int a = ri(r, 50000, 200000), b = ri(r, 50000, 200000);
                code = program("", "int a = " + a + ";\nint b = " + b + ";\nSystem.out.println(a * b);");
                answer = String.valueOf(a * b);
                why = "a * b does not fit in an int, so the result wraps around.";
            }
            case 2 -> {
                int a = ri(r, 50000, 200000), b = ri(r, 50000, 200000);
                long x = a * b;
                code = program("", "int a = " + a + ";\nint b = " + b + ";\nlong x = a * b;\nSystem.out.println(x);");
                answer = String.valueOf(x);
                why = "The multiplication happens in int and overflows BEFORE the result is widened to long.";
            }
            case 3 -> {
                int a = ri(r, 50000, 200000), b = ri(r, 50000, 200000);
                long x = (long) a * b;
                code = program("", "int a = " + a + ";\nint b = " + b + ";\nlong x = (long) a * b;\nSystem.out.println(x);");
                answer = String.valueOf(x);
                why = "Casting one operand to long first makes the whole multiplication a long, so nothing overflows.";
            }
            default -> {
                int n = ri(r, 128, 400);
                byte b = (byte) n;
                code = program("", "int n = " + n + ";\nbyte b = (byte) n;\nSystem.out.println(b);");
                answer = String.valueOf(b);
                why = "A byte holds -128 to 127, so the cast keeps only the lowest 8 bits and reads them as a signed number.";
            }
        }
        return Spec.output("Overflow and casts", "What does this program print?", code, answer, why + " Result: " + answer + ".", true);
    }

    static Spec charMath(Random r, int tier) {
        char c = (char) ('a' + ri(r, 0, 12));
        int n = ri(r, 1, 12);
        int p = ri(r, 0, tier == 0 ? 0 : 2);
        String statements;
        String answer;
        switch (p) {
            case 0 -> { char x = c; x += n; statements = "char c = '" + c + "';\nc += " + n + ";\nSystem.out.println(c);"; answer = String.valueOf(x); }
            case 1 -> { char x = c; x += n; statements = "char c = '" + c + "';\nc += " + n + ";\nSystem.out.println((int) c);"; answer = String.valueOf((int) x); }
            default -> { statements = "char c = '" + c + "';\nSystem.out.println(c + " + n + ");"; answer = String.valueOf(c + n); }
        }
        return Spec.output("char arithmetic", "What does this program print?", program("", statements), answer,
                "A char is a number (its Unicode code). c += n moves the letter n places; c + n is an int, so it prints a "
                        + "number, not a letter. Result: " + answer + ".", true);
    }

    static Spec bitwise(Random r, int tier) {
        int p = ri(r, 0, tier == 0 ? 0 : 3);
        String statements;
        String answer;
        String why;
        switch (p) {
            case 0 -> {
                int a = ri(r, 1, 63), b = ri(r, 1, 63);
                statements = "int a = " + a + ";\nint b = " + b + ";\nSystem.out.println((a & b) + \" \" + (a | b) + \" \" + (a ^ b));";
                answer = (a & b) + " " + (a | b) + " " + (a ^ b);
                why = "Write both numbers in binary and combine bit by bit: AND keeps bits set in both, OR in either, XOR in exactly one.";
            }
            case 1 -> {
                int x = ri(r, 1, 50), n = ri(r, 1, 4);
                statements = "int x = " + x + ";\nSystem.out.println((x << " + n + ") + \" \" + (x >> " + n + "));";
                answer = (x << n) + " " + (x >> n);
                why = "x << n multiplies by 2^n; x >> n divides by 2^n, dropping the remainder.";
            }
            case 2 -> {
                int x = -ri(r, 1, 100), n = ri(r, 1, 3);
                statements = "int x = " + x + ";\nSystem.out.println((x >> " + n + ") + \" \" + (x >>> " + n + "));";
                answer = (x >> n) + " " + (x >>> n);
                why = ">> keeps the sign bit (arithmetic shift) while >>> shifts in zeros, so for a negative number the results differ a lot.";
            }
            default -> {
                int x = ri(r, -40, 40);
                statements = "int x = " + x + ";\nSystem.out.println(~x + \" \" + (x & 1) + \" \" + (x & (x - 1)));";
                answer = ~x + " " + (x & 1) + " " + (x & (x - 1));
                why = "~x flips every bit (so ~x = -x - 1), x & 1 is the lowest bit, and x & (x - 1) clears the lowest set bit.";
            }
        }
        return Spec.output("Bitwise operators", "What does this program print?", program("", statements), answer, why + " Result: " + answer + ".", true);
    }

    // ---------------------------------------------------------------- recursion

    private static final String[] REC_NAMES = {"f", "calc", "solve", "go"};

    static Spec recursion(Random r, int tier) {
        String name = pick(r, REC_NAMES);
        int p = ri(r, 0, tier == 0 ? 1 : 4);
        String members;
        long value;
        String arg;
        String why;
        switch (p) {
            case 0 -> {
                int n = ri(r, 3, 12);
                members = "static int " + name + "(int n) {\n    if (n == 0) {\n        return 0;\n    }\n    return n + " + name + "(n - 1);\n}";
                int s = 0;
                for (int i = 1; i <= n; i++) s += i;
                value = s; arg = String.valueOf(n);
                why = name + "(n) adds n + (n-1) + ... + 1.";
            }
            case 1 -> {
                int n = ri(r, 3, 9);
                members = "static int " + name + "(int n) {\n    if (n <= 1) {\n        return 1;\n    }\n    return n * " + name + "(n - 1);\n}";
                long f = 1;
                for (int i = 2; i <= n; i++) f *= i;
                value = f; arg = String.valueOf(n);
                why = name + "(n) is n factorial.";
            }
            case 2 -> {
                int a = ri(r, 12, 90), b = ri(r, 12, 90);
                members = "static int " + name + "(int a, int b) {\n    if (b == 0) {\n        return a;\n    }\n    return " + name + "(b, a % b);\n}";
                int x = a, y = b;
                while (y != 0) { int t = x % y; x = y; y = t; }
                value = x; arg = a + ", " + b;
                why = "This is Euclid's algorithm: it returns the greatest common divisor.";
            }
            case 3 -> {
                int n = ri(r, 5, 14);
                members = "static int " + name + "(int n) {\n    if (n < 2) {\n        return n;\n    }\n    return " + name + "(n - 1) + " + name + "(n - 2);\n}";
                int a = 0, b = 1;
                for (int i = 0; i < n; i++) { int t = a + b; a = b; b = t; }
                value = a; arg = String.valueOf(n);
                why = name + "(n) is the n-th Fibonacci number (0, 1, 1, 2, 3, 5, 8, ...).";
            }
            default -> {
                int n = ri(r, 10, 9999);
                members = "static int " + name + "(int n) {\n    if (n == 0) {\n        return 0;\n    }\n    return n % 10 + " + name + "(n / 10);\n}";
                int s = 0, t = n;
                while (t > 0) { s += t % 10; t /= 10; }
                value = s; arg = String.valueOf(n);
                why = name + "(n) adds up the digits of n.";
            }
        }
        String code = programWith("", members, "System.out.println(" + name + "(" + arg + "));");
        return Spec.output("Recursive methods", "What does this program print?", code, String.valueOf(value),
                why + " For " + arg + " the result is " + value + ".", true);
    }

    // ---------------------------------------------------------------- Java gotchas

    static Spec integerCache(Random r, int tier) {
        int x = pick(r, ri(r, -300, -129), ri(r, -128, 127), ri(r, -128, 127), ri(r, 128, 400));
        int p = ri(r, 0, 1);
        String statements;
        String answer;
        if (p == 0) {
            Integer a = x, b = x;
            statements = "Integer a = " + x + ";\nInteger b = " + x + ";\nSystem.out.println((a == b) + \" \" + a.equals(b));";
            answer = (a == b) + " " + a.equals(b);
        } else {
            Integer a = x;
            int b = x;
            statements = "Integer a = " + x + ";\nint b = " + x + ";\nSystem.out.println(a == b);";
            answer = String.valueOf(a == b);
        }
        return Spec.output("Integer == and equals", "What does this program print?", program("", statements), answer,
                "Boxed Integers between -128 and 127 are cached, so == can be true for two separate variables; outside "
                        + "that range == compares different objects. equals() always compares the values, and comparing an "
                        + "Integer with an int unboxes it. Result: " + answer + ".", true);
    }

    static Spec switchFall(Random r, int tier) {
        int day = ri(r, 0, 5);
        StringBuilder out = new StringBuilder();
        switch (day) {
            case 1: out.append("A");
            case 2: out.append("B"); break;
            case 3: out.append("C");
            default: out.append("D");
        }
        String statements = "int day = " + day + ";\nString r = \"\";\nswitch (day) {\n    case 1:\n        r += \"A\";\n    case 2:\n        r += \"B\";\n        break;\n    case 3:\n        r += \"C\";\n    default:\n        r += \"D\";\n}\nSystem.out.println(r);";
        return Spec.output("switch fall-through", "What does this program print?", program("", statements), out.toString(),
                "Without break, execution falls through into the next case. Starting at case " + day + " the program "
                        + "collects \"" + out + "\".", true);
    }

    static Spec mathFunctions(Random r, int tier) {
        int p = ri(r, 0, 4);
        String expr;
        String answer;
        switch (p) {
            case 0 -> { double x = pick(r, 2.5, -2.5, 3.49, -3.5, 7.5, 0.5, -0.5, 9.51); expr = "Math.round(" + x + ")"; answer = String.valueOf(Math.round(x)); }
            case 1 -> { double x = pick(r, 2.1, -2.1, 5.0, -5.9, 0.2, 7.99); expr = "(int) Math.ceil(" + x + ")"; answer = String.valueOf((int) Math.ceil(x)); }
            case 2 -> { double x = pick(r, 2.9, -2.9, 5.0, -5.1, 0.2, 7.99); expr = "(int) Math.floor(" + x + ")"; answer = String.valueOf((int) Math.floor(x)); }
            case 3 -> { double x = pick(r, 2.9, -2.9, 5.99, -5.1, 0.2, -7.99); expr = "(int) " + x; answer = String.valueOf((int) x); }
            default -> { int a = ri(r, -30, 30), b = ri(r, 2, 9); expr = "Math.floorMod(" + a + ", " + b + ") + \" \" + (" + a + " % " + b + ")"; answer = Math.floorMod(a, b) + " " + (a % b); }
        }
        return Spec.output("Math and rounding", "What does this program print?", program("", "System.out.println(" + expr + ");"), answer,
                "Math.round rounds halves up (toward positive infinity), ceil goes up, floor goes down, a cast to int "
                        + "truncates toward zero, and floorMod always gives a result with the sign of the divisor. Result: " + answer + ".", true);
    }

    static Spec listRemove(Random r, int tier) {
        int n = ri(r, 4, 7);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) list.add(ri(r, 0, n + 2));
        int v = ri(r, 0, n - 1);
        boolean byIndex = r.nextBoolean();
        String init = list.toString().replace("[", "").replace("]", "");
        String remove;
        if (byIndex) {
            remove = "list.remove(" + v + ");";
            list.remove(v);
        } else {
            remove = "list.remove(Integer.valueOf(" + v + "));";
            list.remove(Integer.valueOf(v));
        }
        String statements = "List<Integer> list = new ArrayList<>(List.of(" + init + "));\n" + remove + "\nSystem.out.println(list);";
        return Spec.output("List remove tricks", "What does this program print?",
                program("import java.util.ArrayList;\nimport java.util.List;", statements), list.toString(),
                "list.remove(int) removes the element at that INDEX, while list.remove(Object) such as "
                        + "Integer.valueOf(v) removes the first element EQUAL to v. Result: " + list + ".", true);
    }

    static Spec mapCount(Random r, int tier) {
        String alphabet = "abcde";
        StringBuilder text = new StringBuilder();
        int len = ri(r, 8, 14);
        for (int i = 0; i < len; i++) text.append(alphabet.charAt(r.nextInt(alphabet.length())));
        Map<Character, Integer> map = new HashMap<>();
        for (char c : text.toString().toCharArray()) map.merge(c, 1, Integer::sum);
        char q = (char) ('a' + ri(r, 0, 5));
        int p = ri(r, 0, tier == 0 ? 0 : 2);
        String last;
        String answer;
        switch (p) {
            case 0 -> { last = "System.out.println(map.getOrDefault('" + q + "', 0));"; answer = String.valueOf(map.getOrDefault(q, 0)); }
            case 1 -> { last = "System.out.println(map.size());"; answer = String.valueOf(map.size()); }
            default -> { last = "System.out.println(map.containsKey('" + q + "') + \" \" + map.getOrDefault('" + q + "', -1));"; answer = map.containsKey(q) + " " + map.getOrDefault(q, -1); }
        }
        String statements = "String text = \"" + text + "\";\nMap<Character, Integer> map = new HashMap<>();\nfor (char c : text.toCharArray()) {\n    map.merge(c, 1, Integer::sum);\n}\n" + last;
        return Spec.output("HashMap counting", "What does this program print?",
                program("import java.util.HashMap;\nimport java.util.Map;", statements), answer,
                "map.merge(c, 1, Integer::sum) counts every character. Count the letters of the text yourself. Result: " + answer + ".", true);
    }

    static Spec stackQueue(Random r, int tier) {
        boolean stack = r.nextBoolean();
        Deque<Integer> d = new ArrayDeque<>();
        StringBuilder statements = new StringBuilder("Deque<Integer> d = new ArrayDeque<>();\n");
        int ops = tier == 0 ? 5 : tier == 1 ? 7 : 9;
        for (int i = 0; i < ops; i++) {
            boolean add = d.isEmpty() || r.nextInt(3) != 0;
            if (add) {
                int v = ri(r, 1, 9);
                if (stack) { d.push(v); statements.append("d.push(").append(v).append(");\n"); }
                else { d.offer(v); statements.append("d.offer(").append(v).append(");\n"); }
            } else if (stack) {
                d.pop();
                statements.append("d.pop();\n");
            } else {
                d.poll();
                statements.append("d.poll();\n");
            }
        }
        statements.append("System.out.println(d.peek() + \" \" + d.size());");
        String answer = d.peek() + " " + d.size();
        return Spec.output("Stack and queue", "What does this program print?",
                program("import java.util.ArrayDeque;\nimport java.util.Deque;", statements.toString()), answer,
                (stack ? "push/pop/peek work at the FRONT of the deque (last in, first out)."
                        : "offer adds at the back and poll/peek use the front (first in, first out).")
                        + " After all operations: " + answer + ".", true);
    }

    // ---------------------------------------------------------------- short-circuit

    static Spec shortCircuit(Random r, int tier) {
        int n = ri(r, 1, 9), t1 = ri(r, 1, 9), t2 = ri(r, 1, 12);
        String[] ops = {"&&", "||", "&", "|"};
        String op = ops[ri(r, 0, tier == 0 ? 1 : 3)];
        int x = n;
        boolean left = x > t1;
        boolean res;
        switch (op) {
            case "&&" -> res = left && (++x > t2);
            case "||" -> res = left || (++x > t2);
            case "&" -> res = left & (++x > t2);
            default -> res = left | (++x > t2);
        }
        String statements = "int x = " + n + ";\nboolean r = (x > " + t1 + ") " + op + " (++x > " + t2 + ");\nSystem.out.println(r + \" \" + x);";
        return Spec.output("Short-circuit evaluation", "What does this program print?", program("", statements), res + " " + x,
                "&& and || skip the right side when the left side already decides the answer, so the ++x may never run. "
                        + "& and | always evaluate both sides. Result: " + res + " " + x + ".", true);
    }
}
