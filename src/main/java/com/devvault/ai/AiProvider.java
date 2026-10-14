package com.devvault.ai;

/** A text-generation backend for the AI explainer. Implementations return empty text instead of throwing. */
public interface AiProvider {

    /** Short name shown to the learner, for example "claude" or "ollama". */
    String name();

    /**
     * @return the model's answer, or an empty string when the model declined, returned nothing or the call failed,
     *         so the caller can fall back to the built-in explanation
     */
    String complete(String system, String user);
}
