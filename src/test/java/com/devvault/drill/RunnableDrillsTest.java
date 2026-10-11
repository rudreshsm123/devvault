package com.devvault.drill;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.DiagnosticCollector;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Proves the stored answers: every sampled "runnable" drill is compiled, executed, and its output compared. */
class RunnableDrillsTest {

    private static final Map<JavaCompiler, StandardJavaFileManager> SHARED = new HashMap<>();

    @Test
    void runnableProgramsPrintTheStoredAnswer() throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertThat(compiler).isNotNull();
        List<String> failures = new ArrayList<>();
        int checked = 0;
        int runnableCount = 0;
        for (Drill d : DrillCatalogTest.DRILLS) {
            if (!d.runnable()) {
                continue;
            }
            runnableCount++;
            if (runnableCount % 6 != 0) {
                continue;
            }
            checked++;
            String actual;
            try {
                actual = run(compiler, d.code());
            } catch (Exception ex) {
                failures.add(d.id() + " " + d.familyKey() + " error: " + ex.getMessage());
                continue;
            }
            if (!normalize(actual).equals(normalize(d.answer()))) {
                failures.add(d.id() + " " + d.familyKey() + " expected [" + d.answer() + "] but program printed [" + actual.trim() + "]");
            }
        }
        System.out.println("RUNNABLE checked=" + checked + " of " + runnableCount);
        assertThat(checked).isGreaterThan(500);
        assertThat(failures).as("mismatches").isEmpty();
    }

    private static String normalize(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    private static String run(JavaCompiler compiler, String source) throws Exception {
        Map<String, ByteArrayOutputStream> classes = new HashMap<>();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager std = SHARED.computeIfAbsent(compiler, c -> c.getStandardFileManager(null, null, null));
        JavaFileManager fm = new ForwardingJavaFileManager<>(std) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
                                                       javax.tools.FileObject sibling) {
                return new SimpleJavaFileObject(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind) {
                    @Override
                    public java.io.OutputStream openOutputStream() {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        classes.put(className, out);
                        return out;
                    }
                };
            }
        };
        JavaFileObject file = new SimpleJavaFileObject(URI.create("string:///Main.java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignore) {
                return source;
            }
        };
        boolean ok = compiler.getTask(null, fm, diagnostics, List.of("-proc:none"), null, List.of(file)).call();
        if (!ok) {
            throw new IllegalStateException("compile error: " + diagnostics.getDiagnostics());
        }
        ClassLoader loader = new ClassLoader(RunnableDrillsTest.class.getClassLoader()) {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                ByteArrayOutputStream b = classes.get(name);
                if (b == null) {
                    throw new ClassNotFoundException(name);
                }
                byte[] bytes = b.toByteArray();
                return defineClass(name, bytes, 0, bytes.length);
            }
        };
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true));
        try {
            Class<?> main = loader.loadClass("Main");
            Throwable[] error = new Throwable[1];
            Thread t = new Thread(() -> {
                try {
                    main.getMethod("main", String[].class).invoke(null, (Object) new String[0]);
                } catch (Throwable e) {
                    error[0] = e;
                }
            });
            t.setDaemon(true);
            t.start();
            t.join(3000);
            if (t.isAlive()) {
                throw new IllegalStateException("timeout (possible endless loop)");
            }
            if (error[0] != null) {
                throw new IllegalStateException("runtime error " + error[0]);
            }
        } finally {
            System.setOut(original);
        }
        return captured.toString();
    }
}
