package com.devvault.ai;

/** A provider could not answer, with a short reason that is safe to show to the learner (never contains a key). */
public class AiUnavailableException extends RuntimeException {

    public AiUnavailableException(String reason) {
        super(reason);
    }
}
