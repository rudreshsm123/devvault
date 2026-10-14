package com.devvault.service;

import com.devvault.service.InsightsService.DueItem;
import com.devvault.service.PlanBuilder.Pick;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class PlanBuilderTest {

    private static List<Integer> ids(int from, int to) {
        return IntStream.rangeClosed(from, to).boxed().toList();
    }

    private static Map<String, List<Integer>> topics() {
        Map<String, List<Integer>> m = new TreeMap<>();
        m.put("Arrays", ids(1, 50));
        m.put("Streams", ids(51, 100));
        m.put("Strings", ids(101, 150));
        return m;
    }

    private static DueItem due(long id) {
        return new DueItem("drill", id, "Drill " + id, "/drills/" + id, "Arrays", 0, id);
    }

    private static long count(List<Pick> plan, String source) {
        return plan.stream().filter(p -> p.source().equals(source)).count();
    }

    @Test
    void aBrandNewLearnerGetsATenItemPlanSpreadAcrossTopics() {
        List<Pick> plan = PlanBuilder.build(10, List.of(), List.of(), Map.of(), topics(), Set.of(), 1L);

        assertThat(plan).hasSize(10);
        assertThat(count(plan, PlanBuilder.NEW)).isEqualTo(10);
        assertThat(plan.stream().map(Pick::itemId).distinct()).hasSize(10);
        Set<String> topicsUsed = plan.stream().map(p -> p.itemId() <= 50 ? "A" : p.itemId() <= 100 ? "S" : "T")
                .collect(Collectors.toSet());
        assertThat(topicsUsed).hasSize(3);
    }

    @Test
    void dueReviewsComeFirstAndAreCappedAtFive() {
        List<DueItem> dueItems = new ArrayList<>();
        for (long i = 1; i <= 8; i++) {
            dueItems.add(due(i));
        }

        List<Pick> plan = PlanBuilder.build(10, dueItems, List.of(), Map.of(), topics(), Set.of(), 1L);

        assertThat(plan).hasSize(10);
        assertThat(plan.subList(0, 5)).allMatch(p -> p.source().equals(PlanBuilder.REVIEW));
        assertThat(count(plan, PlanBuilder.REVIEW)).isEqualTo(5);
        assertThat(plan.stream().map(Pick::itemId).distinct()).hasSize(10);       // no due drill is offered twice
    }

    @Test
    void weakTopicsGetUpToThreeNewQuestionsEach_inTurn() {
        List<Pick> plan = PlanBuilder.build(10, List.of(), List.of("Streams"), Map.of(), topics(), Set.of(), 1L);

        List<Pick> weak = plan.stream().filter(p -> p.source().equals(PlanBuilder.WEAK)).toList();
        assertThat(weak).hasSize(3);
        assertThat(weak).allMatch(p -> p.itemId() >= 51 && p.itemId() <= 100);
    }

    @Test
    void questionsTheLearnerHasAlreadySeenAreNeverOfferedAsNew() {
        Set<Integer> seen = IntStream.rangeClosed(1, 140).boxed().collect(Collectors.toSet());

        List<Pick> plan = PlanBuilder.build(10, List.of(), List.of(), Map.of(), topics(), seen, 1L);

        assertThat(plan).hasSize(10);
        assertThat(plan).allMatch(p -> p.itemId() > 140);
    }

    @Test
    void leastPractisedTopicsAreChosenFirstForNewQuestions() {
        Map<String, Integer> attempts = Map.of("Arrays", 40, "Streams", 30, "Strings", 0);

        List<Pick> plan = PlanBuilder.build(2, List.of(), List.of(), attempts, topics(), Set.of(), 1L);

        assertThat(plan.get(0).itemId()).isBetween(101L, 150L);     // Strings has no answers yet
        assertThat(plan.get(1).itemId()).isBetween(51L, 100L);      // then Streams, which has fewer than Arrays
    }

    @Test
    void theSameSeedGivesTheSamePlanAndAnotherSeedDiffers() {
        List<Pick> a = PlanBuilder.build(10, List.of(), List.of(), Map.of(), topics(), Set.of(), 42L);
        List<Pick> b = PlanBuilder.build(10, List.of(), List.of(), Map.of(), topics(), Set.of(), 42L);
        List<Pick> c = PlanBuilder.build(10, List.of(), List.of(), Map.of(), topics(), Set.of(), 43L);

        assertThat(a).isEqualTo(b);
        assertThat(a).isNotEqualTo(c);
    }

    @Test
    void thePlanIsShorterWhenThereIsNothingLeftToOffer() {
        Map<String, List<Integer>> tiny = Map.of("Arrays", ids(1, 3));

        List<Pick> plan = PlanBuilder.build(10, List.of(), List.of(), Map.of(), tiny, Set.of(), 1L);

        assertThat(plan).hasSize(3);
    }
}
