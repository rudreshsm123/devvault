package com.devvault.service;

import com.devvault.drill.Drill;
import com.devvault.drill.DrillService;
import com.devvault.entity.DailyPlanItem;
import com.devvault.repository.AttemptLogRepository;
import com.devvault.repository.DailyPlanItemRepository;
import com.devvault.repository.UserProgressRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** "Today's plan": a short, saved list of what to practise today, and how much of it is done. */
@Service
public class PlanService {

    public static final int PLAN_SIZE = 10;

    public record Entry(String source, String kind, Long itemId, String title, String url, String topic,
                        boolean done) {
    }

    public record Plan(LocalDate day, List<Entry> entries, int doneCount) {
        public int total() {
            return entries.size();
        }

        public boolean complete() {
            return !entries.isEmpty() && doneCount == entries.size();
        }
    }

    private final InsightsService insights;
    private final DailyPlanItemRepository plans;
    private final UserProgressRepository progress;
    private final AttemptLogRepository attempts;
    private final DrillService drills;

    public PlanService(InsightsService insights, DailyPlanItemRepository plans, UserProgressRepository progress,
                       AttemptLogRepository attempts, DrillService drills) {
        this.insights = insights;
        this.plans = plans;
        this.progress = progress;
        this.attempts = attempts;
        this.drills = drills;
    }

    /** Today's plan, created the first time it is asked for that day and then kept. */
    public Plan today(Long userId, long now, ZoneId zone) {
        LocalDate day = Instant.ofEpochMilli(now).atZone(zone).toLocalDate();
        List<DailyPlanItem> items = plans.findByUserIdAndPlanDayOrderByPosition(userId, day);
        if (items.isEmpty()) {
            items = create(userId, day, now, zone);
        }
        long startOfDay = day.atStartOfDay(zone).toInstant().toEpochMilli();
        Set<String> answeredToday = new HashSet<>();
        attempts.findByUserIdAndAtGreaterThanEqual(userId, startOfDay)
                .forEach(a -> answeredToday.add(a.getKind() + ":" + a.getItemId()));

        List<Entry> entries = new ArrayList<>();
        int done = 0;
        for (DailyPlanItem item : items) {
            boolean isDone = answeredToday.contains(item.getKind() + ":" + item.getItemId());
            done += isDone ? 1 : 0;
            entries.add(new Entry(item.getSource(), item.getKind(), item.getItemId(), item.getTitle(), item.getUrl(),
                    item.getTopic(), isDone));
        }
        return new Plan(day, entries, done);
    }

    private List<DailyPlanItem> create(Long userId, LocalDate day, long now, ZoneId zone) {
        InsightsService.Insights snapshot = insights.forUser(userId, now, zone);
        Map<String, Integer> topicAttempts = new HashMap<>();
        snapshot.topics().forEach(t -> topicAttempts.put(t.topic(), t.attempts()));
        Set<Integer> seen = new HashSet<>();
        progress.findByUserId(userId).stream().filter(p -> "drill".equals(p.getKind()))
                .forEach(p -> seen.add(Math.toIntExact(p.getItemId())));

        List<PlanBuilder.Pick> picks = PlanBuilder.build(PLAN_SIZE, snapshot.due(),
                snapshot.weakTopics().stream().map(InsightsService.TopicStat::topic).toList(),
                topicAttempts, drills.idsByTopic(), seen, userId * 1_000_003L + day.toEpochDay());

        Map<String, InsightsService.DueItem> dueByKey = new HashMap<>();
        snapshot.due().forEach(d -> dueByKey.put(d.kind() + ":" + d.itemId(), d));

        List<DailyPlanItem> items = new ArrayList<>();
        for (PlanBuilder.Pick pick : picks) {
            int position = items.size();
            if ("drill".equals(pick.kind())) {
                Drill drill = drills.find(Math.toIntExact(pick.itemId())).orElse(null);
                if (drill != null) {
                    items.add(new DailyPlanItem(userId, day, position, pick.source(), "drill", pick.itemId(),
                            "Drill " + drill.id() + ": " + drill.title(), "/drills/" + drill.id(), drill.topic()));
                }
            } else {
                InsightsService.DueItem due = dueByKey.get(pick.kind() + ":" + pick.itemId());
                if (due != null) {
                    items.add(new DailyPlanItem(userId, day, position, pick.source(), pick.kind(), pick.itemId(),
                            due.title(), due.url(), due.topic()));
                }
            }
        }
        // Closing the gaps left by items that no longer exist keeps positions 0..n-1, as the unique key expects.
        List<DailyPlanItem> numbered = new ArrayList<>();
        for (DailyPlanItem item : items) {
            numbered.add(new DailyPlanItem(userId, day, numbered.size(), item.getSource(), item.getKind(),
                    item.getItemId(), item.getTitle(), item.getUrl(), item.getTopic()));
        }
        try {
            return plans.saveAll(numbered);
        } catch (DataIntegrityViolationException e) {
            // Two tabs opened the plan at the same moment: the other one won, so use its saved plan.
            return plans.findByUserIdAndPlanDayOrderByPosition(userId, day);
        }
    }
}
