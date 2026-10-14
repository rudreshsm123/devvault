package com.devvault.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Chooses the items for one day's plan: reviews that are due, then new questions from the learner's weak topics,
 * then new questions from the topics they have practised least. It has no Spring or database dependency, so the
 * rules can be unit tested directly. The same seed always gives the same plan.
 */
public final class PlanBuilder {

    public static final String REVIEW = "review";
    public static final String WEAK = "weak";
    public static final String NEW = "new";

    static final int MAX_REVIEWS = 5;
    static final int WEAK_QUOTA = 3;

    /** One chosen item. {@code kind} is "drill" or "problem". */
    public record Pick(String source, String kind, long itemId) {
    }

    private PlanBuilder() {
    }

    /**
     * @param size           how many items the plan should have
     * @param due            items due for review, most overdue first
     * @param weakTopics     the learner's weak topics, weakest first
     * @param topicAttempts  how many answers the learner has given per topic (missing means none)
     * @param drillsByTopic  every drill id of every topic
     * @param alreadySeen    drill ids the learner has answered before, which are never offered as "new"
     * @param seed           makes the random choices repeatable
     */
    public static List<Pick> build(int size, List<InsightsService.DueItem> due, List<String> weakTopics,
                                   Map<String, Integer> topicAttempts, Map<String, List<Integer>> drillsByTopic,
                                   Set<Integer> alreadySeen, long seed) {
        Random random = new Random(seed);
        List<Pick> plan = new ArrayList<>();
        Set<Integer> usedDrills = new HashSet<>(alreadySeen);

        for (InsightsService.DueItem item : due) {
            if (plan.size() >= MAX_REVIEWS || plan.size() >= size) {
                break;
            }
            plan.add(new Pick(REVIEW, item.kind(), item.itemId()));
            if ("drill".equals(item.kind())) {
                usedDrills.add(Math.toIntExact(item.itemId()));
            }
        }

        int weakTarget = Math.min(size, plan.size() + WEAK_QUOTA);
        fill(plan, weakTarget, WEAK, weakTopics, drillsByTopic, usedDrills, random);

        List<String> leastPractised = new ArrayList<>(drillsByTopic.keySet());
        leastPractised.sort(Comparator.<String>comparingInt(t -> topicAttempts.getOrDefault(t, 0))
                .thenComparing(Comparator.naturalOrder()));
        fill(plan, size, NEW, leastPractised, drillsByTopic, usedDrills, random);
        return plan;
    }

    /** Takes one unseen drill from each topic in turn until the plan reaches {@code target} or no topic has any left. */
    private static void fill(List<Pick> plan, int target, String source, List<String> topics,
                             Map<String, List<Integer>> drillsByTopic, Set<Integer> used, Random random) {
        Set<String> exhausted = new LinkedHashSet<>();
        while (plan.size() < target && exhausted.size() < topics.size()) {
            for (String topic : topics) {
                if (plan.size() >= target) {
                    return;
                }
                if (exhausted.contains(topic)) {
                    continue;
                }
                List<Integer> candidates = drillsByTopic.getOrDefault(topic, List.of()).stream()
                        .filter(id -> !used.contains(id)).toList();
                if (candidates.isEmpty()) {
                    exhausted.add(topic);
                    continue;
                }
                int id = candidates.get(random.nextInt(candidates.size()));
                used.add(id);
                plan.add(new Pick(source, "drill", id));
            }
        }
    }
}
