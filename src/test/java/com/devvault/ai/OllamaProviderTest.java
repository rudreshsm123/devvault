package com.devvault.ai;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/** Talks to a fake Ollama server so the request shape and the failure fallback are checked without a model. */
class OllamaProviderTest {

    private HttpServer server;

    @AfterEach
    void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private String start(int status, String json, AtomicReference<String> seenBody) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/chat", exchange -> {
            seenBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] out = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, out.length);
            exchange.getResponseBody().write(out);
            exchange.close();
        });
        server.start();
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @Test
    void sendsTheModelAndBothMessagesAndReturnsTheReply() throws IOException {
        AtomicReference<String> body = new AtomicReference<>();
        String url = start(200, "{\"message\":{\"role\":\"assistant\",\"content\":\"  Use a loop.  \"}}", body);

        String reply = new OllamaProvider(url, "tiny-model").complete("be kind", "why?");

        assertThat(reply).isEqualTo("Use a loop.");
        assertThat(body.get()).contains("\"model\":\"tiny-model\"").contains("be kind").contains("why?")
                .contains("\"stream\":false");
    }

    @Test
    void aServerErrorOrAMissingServerGivesAnEmptyAnswerInsteadOfThrowing() throws IOException {
        String url = start(500, "{}", new AtomicReference<>());
        assertThat(new OllamaProvider(url, "m").complete("s", "u")).isEmpty();

        server.stop(0);
        assertThat(new OllamaProvider(url, "m").complete("s", "u")).isEmpty();
    }
}
