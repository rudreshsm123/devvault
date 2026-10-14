package com.devvault.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * A small model running on your own computer through Ollama (https://ollama.com), so development needs no API key
 * and no internet. Enable with {@code devvault.ai.provider=ollama}.
 */
@Component
@ConditionalOnProperty(name = "devvault.ai.provider", havingValue = "ollama")
public class OllamaProvider implements AiProvider {

    private static final Logger log = LoggerFactory.getLogger(OllamaProvider.class);

    private final RestClient http;
    private final String model;

    public OllamaProvider(@Value("${devvault.ai.ollama.url:http://localhost:11434}") String baseUrl,
                          @Value("${devvault.ai.ollama.model:qwen2.5:3b}") String model) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3_000);
        factory.setReadTimeout(120_000);               // a CPU-only laptop needs a while for a few hundred tokens
        this.http = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
        this.model = model;
    }

    @Override
    public String name() {
        return "ollama";
    }

    @Override
    @SuppressWarnings("unchecked")
    public String complete(String system, String user) {
        try {
            Map<String, Object> body = Map.of(
                    "model", model,
                    "stream", false,
                    "options", Map.of("num_predict", 400),
                    "messages", List.of(
                            Map.of("role", "system", "content", system),
                            Map.of("role", "user", "content", user)));
            Map<String, Object> response = http.post().uri("/api/chat").body(body).retrieve().body(Map.class);
            if (response != null && response.get("message") instanceof Map<?, ?> message
                    && message.get("content") instanceof String content) {
                return content.trim();
            }
            return "";
        } catch (RuntimeException e) {
            log.warn("Ollama call failed (is it running and is the model pulled?): {}", e.getMessage());
            return "";
        }
    }
}
