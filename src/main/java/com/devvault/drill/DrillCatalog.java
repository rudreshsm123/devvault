package com.devvault.drill;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Builds the 10,000 drills deterministically: the same ids always give the same questions, so a learner's saved
 * progress (by id) keeps pointing at the same question after a restart or a redeploy.
 */
public final class DrillCatalog {

    public static final int SIZE = 10_000;
    private static final String[] TIERS = {"Beginner", "Intermediate", "Advanced"};

    private DrillCatalog() {
    }

    public static List<Family> families() {
        List<Family> all = new ArrayList<>();
        all.addAll(JavaFamilies.all());
        all.addAll(AlgoFamilies.all());
        all.addAll(AppFamilies.all());
        return all;
    }

    public static List<Drill> build() {
        List<Family> families = families();
        List<Family> schedule = new ArrayList<>();           // each family appears `weight` times per cycle
        for (Family f : families) {
            for (int i = 0; i < f.weight(); i++) {
                schedule.add(f);
            }
        }
        Set<String> seen = new HashSet<>();
        List<Drill> drills = new ArrayList<>(SIZE);
        for (int id = 1; id <= SIZE; id++) {
            int tier = tierFor(id);
            Spec spec = null;
            Family used = null;
            for (int shift = 0; shift < families.size() && spec == null; shift++) {
                Family f = shift == 0 ? schedule.get((id - 1) % schedule.size())
                        : families.get(((id - 1) + shift) % families.size());
                for (int attempt = 0; attempt < 40; attempt++) {
                    Spec candidate;
                    try {
                        candidate = f.generator().apply(new Random(seed(id, f.key(), attempt)), tier);
                    } catch (RuntimeException ex) {
                        continue;
                    }
                    if (!candidate.answer().isBlank() && seen.add(f.key() + "|" + candidate.question() + "|" + candidate.code())) {
                        spec = candidate;
                        used = f;
                        break;
                    }
                }
            }
            if (spec == null) {
                throw new IllegalStateException("Could not generate a unique drill for id " + id);
            }
            drills.add(new Drill(id, used.key(), used.name(), used.topic(), used.group(), TIERS[tier], spec.title(),
                    spec.question(), spec.code(), spec.kind(), spec.answer(), spec.choices(), spec.explanation(),
                    spec.runnable()));
        }
        return List.copyOf(drills);
    }

    /** Difficulty cycles so every page of the list has a mix. */
    static int tierFor(int id) {
        int m = id % 10;
        return m < 4 ? 0 : m < 8 ? 1 : 2;
    }

    private static long seed(int id, String key, int attempt) {
        return id * 1_000_003L + key.hashCode() * 31L + attempt * 7_919L;
    }
}
