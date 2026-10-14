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
 * Any hosted model that speaks the common "chat completions" protocol, which includes Google Gemini and Groq and
 * both have free tiers. Enable with {@code devvault.ai.provider=openai} and set the key in the AI_API_KEY
 * environment variable. The defaults point at Gemini; change AI_BASE_URL and AI_MODEL to use another service.
 */
@Component
@ConditionalOnProperty(name = "devvault.ai.provider", havingValue = "openai")
public class OpenAiCompatibleProvider implements AiProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleProvider.class);

    private final RestClient http;
    private final String model;
    private final String apiKey;
    private final String label;

    public OpenAiCompatibleProvider(
            @Value("${devvault.ai.openai.base-url:https://generativelanguage.googleapis.com/v1beta/openai}") String baseUrl,
            @Value("${devvault.ai.openai.model:gemini-3.8-flash}") String model,
            @Value("${devvault.ai.openai.api-key:}") String apiKey,
            @Value("${devvault.ai.openai.label:gemini}") String label) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(30_000);
        this.http = RestClient.builder().baseUrl(baseUrl.replaceAll("/+$", "")).requestFactory(factory).build();
        this.model = model;
        this.apiKey = apiKey;
        this.label = label;
    }

    @Override
    public String name() {
        return label;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String complete(String system, String user) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI_API_KEY is not set, using the built-in explanation");
            return "";
        }
        try {
            Map<String, Object> body = Map.of(
                    "model", model,
                    "max_tokens", 600,
                    "messages", List.of(
                            Map.of("role", "system", "content", system),
                            Map.of("role", "user", "content", user)));
            Map<String, Object> response = http.post().uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(body).retrieve().body(Map.class);
            if (response != null && response.get("choices") instanceof List<?> choices && !choices.isEmpty()
                    && choices.get(0) instanceof Map<?, ?> choice
                    && choice.get("message") instanceof Map<?, ?> message
                    && message.get("content") instanceof String content) {
                return content.trim();
            }
            return "";
        } catch (RuntimeException e) {
            // Out of free quota, bad key, network error: the learner still gets the built-in explanation.
            log.warn("Hosted AI call failed: {}", e.getMessage());
            return "";
        }
    }
}
