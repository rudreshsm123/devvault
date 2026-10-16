package com.devvault.ai;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Talks to a fake chat-completions server, so no key or internet is needed. */
class OpenAiCompatibleProviderTest {

    private HttpServer server;

    @AfterEach
    void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private String start(int status, String json, AtomicReference<String> path, AtomicReference<String> auth,
                         AtomicReference<String> body, AtomicInteger calls) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            calls.incrementAndGet();
            path.set(exchange.getRequestURI().getPath());
            auth.set(exchange.getRequestHeaders().getFirst("Authorization"));
            body.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] out = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, out.length);
            exchange.getResponseBody().write(out);
            exchange.close();
        });
        server.start();
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/v1beta/openai/";
    }

    @Test
    void sendsTheKeyModelAndMessagesAndReturnsTheReply() throws IOException {
        AtomicReference<String> path = new AtomicReference<>();
        AtomicReference<String> auth = new AtomicReference<>();
        AtomicReference<String> body = new AtomicReference<>();
        String url = start(200, "{\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\" Check the loop. \"}}]}",
                path, auth, body, new AtomicInteger());

        String reply = new OpenAiCompatibleProvider(url, "some-model", "secret-key", "gemini").complete("be kind", "why?");

        assertThat(reply).isEqualTo("Check the loop.");
        assertThat(path.get()).isEqualTo("/v1beta/openai/chat/completions");
        assertThat(auth.get()).isEqualTo("Bearer secret-key");
        assertThat(body.get()).contains("\"model\":\"some-model\"").contains("be kind").contains("why?");
    }

    @Test
    void withoutAKeyNothingIsSentAndTheReasonSaysSo() throws IOException {
        AtomicInteger calls = new AtomicInteger();
        String url = start(200, "{}", new AtomicReference<>(), new AtomicReference<>(), new AtomicReference<>(), calls);

        assertThatThrownBy(() -> new OpenAiCompatibleProvider(url, "m", "  ", "x").complete("s", "u"))
                .isInstanceOf(AiUnavailableException.class).hasMessageContaining("AI_API_KEY");
        assertThat(calls.get()).isZero();
    }

    @Test
    void quotaErrorsAndOddResponsesReportAReasonWithoutLeakingTheKey() throws IOException {
        String quotaUrl = start(429, "{\"error\":\"quota\"}", new AtomicReference<>(), new AtomicReference<>(),
                new AtomicReference<>(), new AtomicInteger());
        assertThatThrownBy(() -> new OpenAiCompatibleProvider(quotaUrl, "my-model", "k", "x").complete("s", "u"))
                .isInstanceOf(AiUnavailableException.class).hasMessageContaining("my-model").hasMessageContaining("429");

        server.stop(0);
        String url = start(200, "{\"choices\":[]}", new AtomicReference<>(), new AtomicReference<>(),
                new AtomicReference<>(), new AtomicInteger());
        String emptyUrl = url;
        assertThatThrownBy(() -> new OpenAiCompatibleProvider(emptyUrl, "m", "k", "x").complete("s", "u"))
                .isInstanceOf(AiUnavailableException.class).hasMessageContaining("no text");
    }

    /** Fails with 503 for the given model and answers normally for any other. */
    private String startOverloaded(String overloadedModel, AtomicInteger calls) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            calls.incrementAndGet();
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            boolean busy = body.contains("\"" + overloadedModel + "\"");
            byte[] out = (busy ? "{\"error\":\"busy\"}"
                    : "{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}").getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(busy ? 503 : 200, out.length);
            exchange.getResponseBody().write(out);
            exchange.close();
        });
        server.start();
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/v1beta/openai/";
    }

    @Test
    void anOverloadedModelIsRetriedOnceThenTheFallbackModelAnswers() throws IOException {
        AtomicInteger calls = new AtomicInteger();
        String url = startOverloaded("main-model", calls);

        String reply = new OpenAiCompatibleProvider(url, "main-model", "k", "x", "backup-model", 0).complete("s", "u");

        assertThat(reply).isEqualTo("ok");
        assertThat(calls.get()).isEqualTo(3);   // main twice, then the backup once
    }

    @Test
    void whenEveryModelIsOverloadedTheLastReasonIsReported() throws IOException {
        AtomicInteger calls = new AtomicInteger();
        String url = start(503, "{\"error\":\"busy\"}", new AtomicReference<>(), new AtomicReference<>(),
                new AtomicReference<>(), calls);

        assertThatThrownBy(() -> new OpenAiCompatibleProvider(url, "a", "k", "x", "b", 0).complete("s", "u"))
                .isInstanceOf(AiUnavailableException.class).hasMessageContaining("\"b\"").hasMessageContaining("503");
        assertThat(calls.get()).isEqualTo(4);
    }
}
