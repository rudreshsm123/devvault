package com.devvault.ai;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.anthropic.models.messages.StopReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Hosted Claude through the official Anthropic Java SDK. The API key is read from the ANTHROPIC_API_KEY
 * environment variable and never stored in the project. Enable with {@code devvault.ai.provider=claude}.
 */
@Component
@ConditionalOnProperty(name = "devvault.ai.provider", havingValue = "claude")
public class ClaudeProvider implements AiProvider {

    private static final Logger log = LoggerFactory.getLogger(ClaudeProvider.class);
    private static final long MAX_TOKENS = 1_000;

    private final String model;
    private AnthropicClient client;                // created on first use so a missing key never stops the app starting

    public ClaudeProvider(@Value("${devvault.ai.claude.model:claude-opus-5-5}") String model) {
        this.model = model;
    }

    @Override
    public String name() {
        return "claude";
    }

    @Override
    public String complete(String system, String user) {
        try {
            MessageCreateParams params = MessageCreateParams.builder()
                    .model(model)
                    .maxTokens(MAX_TOKENS)
                    .system(system)
                    // A short explanation does not need deep reasoning, so keep effort (and cost) low.
                    .outputConfig(OutputConfig.builder().effort(OutputConfig.Effort.LOW).build())
                    .addUserMessage(user)
                    .build();
            Message response = client().messages().create(params);
            if (response.stopReason().map(StopReason.REFUSAL::equals).orElse(false)) {
                log.info("Claude declined to explain this question, using the built-in explanation");
                return "";
            }
            StringBuilder text = new StringBuilder();
            response.content().forEach(block -> block.text().ifPresent(t -> text.append(t.text())));
            return text.toString().trim();
        } catch (RuntimeException e) {
            // Missing key, rate limit, network error: the learner still gets the built-in explanation.
            log.warn("Claude call failed: {}", e.getMessage());
            return "";
        }
    }

    private synchronized AnthropicClient client() {
        if (client == null) {
            client = AnthropicOkHttpClient.fromEnv();
        }
        return client;
    }
}
