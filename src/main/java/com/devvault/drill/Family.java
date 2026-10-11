package com.devvault.drill;

import java.util.Random;
import java.util.function.BiFunction;

/**
 * A kind of question. {@code weight} is how often it appears (families with many possible variations get a
 * higher weight); {@code generator} receives a seeded random generator and a difficulty tier (0 easy, 1 medium,
 * 2 hard) and returns one question.
 */
public record Family(String key, String topic, String name, String group, int weight,
                     BiFunction<Random, Integer, Spec> generator) {
}
