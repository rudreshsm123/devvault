package com.devvault.service;

import com.devvault.drill.DrillService;
import com.devvault.entity.AttemptLog;
import com.devvault.entity.CodingProblem;
import com.devvault.entity.UserProgress;
import com.devvault.repository.AttemptLogRepository;
import com.devvault.repository.ProblemRepository;
import com.devvault.repository.UserProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

/** Turns a user's answer history into "what is due for review" and "which topics are weak". */
@Service
public class InsightsService {

    /** A topic needs this many answers before it can be called weak, so one slip does not label it. */
    static final int MIN_ATTEMPTS_FOR_WEAK = 3;
    static final double WEAK_ACCURACY = 0.7;
    static final int HEATMAP_DAYS = 84;

    public record DueItem(String kind, Long itemId, String title, String url, String topic, int box, long dueAt) {
    }

    public record TopicStat(String topic, int attempts, int correct, int accuracyPercent, boolean weak) {
    }

    public record Day(LocalDate date, int attempts, int level) {
    }

    public record Insights(int dueCount, List<DueItem> due, List<TopicStat> topics, List<TopicStat> weakTopics,
                           int totalAttempts, int accuracyPercent, int currentStreak, int longestStreak,
                           List<Day> activity) {
    }

    private final UserProgressRepository progress;
    private final AttemptLogRepository attempts;
    private final DrillService drills;
    private final ProblemRepository problems;

    public InsightsService(UserProgressRepository progress, AttemptLogRepository attempts, DrillService drills,
                           ProblemRepository problems) {
        this.progress = progress;
        this.attempts = attempts;
        this.drills = drills;
        this.problems = problems;
    }

    @Transactional(readOnly = true)
    public Insights forUser(Long userId, long now, ZoneId zone) {
        Map<Long, String> problemTopics = new HashMap<>();
        for (CodingProblem p : problems.findAll()) {
            problemTopics.put(p.getId(), p.getTopic());
        }

        List<DueItem> due = new ArrayList<>();
        for (UserProgress p : progress.findByUserId(userId)) {
            if (isDue(p, now)) {
                due.add(new DueItem(p.getKind(), p.getItemId(), p.getTitle(), p.getUrl(),
                        topicOf(p.getKind(), p.getItemId(), problemTopics), p.getBox(), p.getDueAt()));
            }
        }
        due.sort(Comparator.comparingLong(DueItem::dueAt));

        return build(due, attempts.findByUserIdAndAtGreaterThanEqual(userId, 0L), problemTopics, now, zone);
    }

    /** The most overdue item, if any. */
    @Transactional(readOnly = true)
    public Optional<DueItem> nextDue(Long userId, long now) {
        return progress.findByUserId(userId).stream()
                .filter(p -> isDue(p, now))
                .min(Comparator.comparingLong(UserProgress::getDueAt))
                .map(p -> new DueItem(p.getKind(), p.getItemId(), p.getTitle(), p.getUrl(), null, p.getBox(),
                        p.getDueAt()));
    }

    private static boolean isDue(UserProgress p, long now) {
        return p.getDueAt() != null && p.getDueAt() <= now && !"lesson".equals(p.getKind());
    }

    private Insights build(List<DueItem> due, List<AttemptLog> log, Map<Long, String> problemTopics, long now,
                           ZoneId zone) {
        Map<String, int[]> perTopic = new TreeMap<>();          // topic -> {attempts, correct}
        int total = 0;
        int right = 0;
        for (AttemptLog a : log) {
            int[] counts = perTopic.computeIfAbsent(topicOf(a.getKind(), a.getItemId(), problemTopics),
                    t -> new int[2]);
            counts[0]++;
            total++;
            if (a.isCorrect()) {
                counts[1]++;
                right++;
            }
        }
        List<TopicStat> topics = new ArrayList<>();
        for (Map.Entry<String, int[]> e : perTopic.entrySet()) {
            topics.add(stat(e.getKey(), e.getValue()[0], e.getValue()[1]));
        }
        topics.sort(Comparator.comparingInt(TopicStat::accuracyPercent).thenComparing(TopicStat::topic));
        List<TopicStat> weak = topics.stream().filter(TopicStat::weak).limit(5).toList();

        Map<LocalDate, Integer> perDay = new HashMap<>();
        for (AttemptLog a : log) {
            perDay.merge(Instant.ofEpochMilli(a.getAt()).atZone(zone).toLocalDate(), 1, Integer::sum);
        }
        LocalDate today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate();
        List<Day> activity = new ArrayList<>();
        for (int i = HEATMAP_DAYS - 1; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            int n = perDay.getOrDefault(d, 0);
            activity.add(new Day(d, n, level(n)));
        }
        int[] streaks = streaks(perDay, today);
        return new Insights(due.size(), due.stream().limit(20).toList(), topics, weak, total,
                total == 0 ? 0 : Math.round(right * 100f / total), streaks[0], streaks[1], activity);
    }

    static TopicStat stat(String topic, int attempts, int correct) {
        int percent = attempts == 0 ? 0 : Math.round(correct * 100f / attempts);
        boolean weak = attempts >= MIN_ATTEMPTS_FOR_WEAK && correct < attempts * WEAK_ACCURACY;
        return new TopicStat(topic, attempts, correct, percent, weak);
    }

    /** {current, longest}. The current streak survives until the end of today, so it is not lost before you practise. */
    static int[] streaks(Map<LocalDate, Integer> perDay, LocalDate today) {
        int current = 0;
        LocalDate d = perDay.containsKey(today) ? today : today.minusDays(1);
        while (perDay.containsKey(d)) {
            current++;
            d = d.minusDays(1);
        }
        int longest = 0;
        int run = 0;
        LocalDate previous = null;
        for (LocalDate day : new TreeSet<>(perDay.keySet())) {
            run = previous != null && previous.plusDays(1).equals(day) ? run + 1 : 1;
            longest = Math.max(longest, run);
            previous = day;
        }
        return new int[]{current, longest};
    }

    private static int level(int attempts) {
        return attempts == 0 ? 0 : attempts < 3 ? 1 : attempts < 6 ? 2 : attempts < 10 ? 3 : 4;
    }

    private String topicOf(String kind, Long itemId, Map<Long, String> problemTopics) {
        String topic = null;
        if ("drill".equals(kind)) {
            topic = drills.find(Math.toIntExact(itemId)).map(d -> d.topic()).orElse(null);
        } else if ("problem".equals(kind)) {
            topic = problemTopics.get(itemId);
        }
        return topic == null || topic.isBlank() ? "Other" : topic;
    }
}
