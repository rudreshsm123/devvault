package com.devvault.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
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
    /** Tried in order: the main model first, then the fallbacks. */
    private final List<String> models;
    private final String apiKey;
    private final String label;
    private final long retryDelayMillis;

    OpenAiCompatibleProvider(String baseUrl, String model, String apiKey, String label) {
        this(baseUrl, model, apiKey, label, "", 0);
    }

    @Autowired
    public OpenAiCompatibleProvider(
            @Value("${devvault.ai.openai.base-url:https://generativelanguage.googleapis.com/v1beta/openai}") String baseUrl,
            @Value("${devvault.ai.openai.model:gemini-3.8-flash}") String model,
            @Value("${devvault.ai.openai.api-key:}") String apiKey,
            @Value("${devvault.ai.openai.label:gemini}") String label,
            @Value("${devvault.ai.openai.fallback-models:}") String fallbackModels,
            @Value("${devvault.ai.openai.retry-delay-ms:1500}") long retryDelayMillis) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(30_000);
        this.http = RestClient.builder().baseUrl(baseUrl.replaceAll("/+$", "")).requestFactory(factory).build();
        List<String> all = new ArrayList<>();
        all.add(model);
        for (String fallback : fallbackModels.split(",")) {
            if (!fallback.isBlank() && !all.contains(fallback.trim())) {
                all.add(fallback.trim());
            }
        }
        this.models = List.copyOf(all);
        this.apiKey = apiKey;
        this.label = label;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public String name() {
        return label;
    }

    @Override
    public String complete(String system, String user) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI_API_KEY is not set, using the built-in explanation");
            throw new AiUnavailableException("AI_API_KEY is not set on the server (or the variable name has a stray space).");
        }
        AiUnavailableException last = null;
        for (String model : models) {
            try {
                return callWithRetry(model, system, user);
            } catch (AiUnavailableException e) {
                // Overloaded, out of quota or unknown model: try the next model before giving up.
                last = e;
            }
        }
        throw last;
    }

    /** One model, tried twice when the service says it is busy (429 or 5xx), because those spikes are short. */
    private String callWithRetry(String model, String system, String user) {
        for (int attempt = 1; ; attempt++) {
            try {
                return call(model, system, user);
            } catch (HttpStatusCodeException e) {
                int status = e.getStatusCode().value();
                if (attempt < 2 && (status == 429 || status >= 500)) {
                    pause();
                    continue;
                }
                log.warn("Hosted AI call failed: {}", e.getMessage());
                throw new AiUnavailableException(describe(model, e));
            } catch (AiUnavailableException e) {
                throw e;
            } catch (RuntimeException e) {
                // Bad key, network error and so on: the learner still gets the built-in explanation,
                // plus the reason so a misconfigured server is easy to diagnose.
                log.warn("Hosted AI call failed: {}", e.getMessage());
                throw new AiUnavailableException(describe(model, e));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private String call(String model, String system, String user) {
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
        throw new AiUnavailableException("The model returned no text (model \"" + model + "\").");
    }

    private void pause() {
        try {
            Thread.sleep(retryDelayMillis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /** Short reason with the key removed and the length capped. */
    private String describe(String model, RuntimeException e) {
        String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        if (apiKey != null && !apiKey.isBlank()) {
            message = message.replace(apiKey, "***");
        }
        message = message.replaceAll("\s+", " ").trim();
        return "Model \"" + model + "\": " + (message.length() > 300 ? message.substring(0, 300) + "..." : message);
    }
}
