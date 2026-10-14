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
    void withoutAKeyNothingIsSentAndTheAnswerIsEmpty() throws IOException {
        AtomicInteger calls = new AtomicInteger();
        String url = start(200, "{}", new AtomicReference<>(), new AtomicReference<>(), new AtomicReference<>(), calls);

        assertThat(new OpenAiCompatibleProvider(url, "m", "  ", "x").complete("s", "u")).isEmpty();
        assertThat(calls.get()).isZero();
    }

    @Test
    void quotaErrorsAndOddResponsesGiveAnEmptyAnswerInsteadOfThrowing() throws IOException {
        String url = start(429, "{\"error\":\"quota\"}", new AtomicReference<>(), new AtomicReference<>(),
                new AtomicReference<>(), new AtomicInteger());
        assertThat(new OpenAiCompatibleProvider(url, "m", "k", "x").complete("s", "u")).isEmpty();

        server.stop(0);
        url = start(200, "{\"choices\":[]}", new AtomicReference<>(), new AtomicReference<>(),
                new AtomicReference<>(), new AtomicInteger());
        assertThat(new OpenAiCompatibleProvider(url, "m", "k", "x").complete("s", "u")).isEmpty();
    }
}
