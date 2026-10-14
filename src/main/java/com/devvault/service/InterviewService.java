package com.devvault.service;

import com.devvault.drill.Drill;
import com.devvault.drill.DrillService;
import com.devvault.drill.Spec;
import com.devvault.entity.InterviewAnswer;
import com.devvault.entity.InterviewSession;
import com.devvault.exception.NotFoundException;
import com.devvault.repository.InterviewAnswerRepository;
import com.devvault.repository.InterviewSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Timed mock interviews. The questions are chosen when the interview starts, the correct answers stay on the server
 * until the interview is finished, and the clock is the server's: answers that arrive after the time (plus a short
 * grace period) are not counted.
 */
@Service
public class InterviewService {

    public static final int SECONDS_PER_QUESTION = 90;
    public static final Set<Integer> COUNTS = Set.of(5, 10, 15);
    static final long GRACE_MILLIS = 10_000;
    static final int MAX_ANSWER_CHARS = 300;

    /** A question as shown during the interview: everything except the answer. */
    public record Question(int position, int drillId, String title, String question, String code, Spec.Kind kind,
                           List<String> choices) {
    }

    public record TestView(Long id, String topic, int durationSeconds, long remainingSeconds,
                           List<Question> questions) {
    }

    public record Line(int position, int drillId, String title, String topic, String given, String correctAnswer,
                       boolean right, String explanation, String url) {
    }

    public record TopicResult(String topic, int total, int right) {
    }

    public record Report(Long id, String topic, String difficulty, int total, int score, int percent, String verdict,
                         long secondsUsed, int durationSeconds, boolean late, List<Line> lines,
                         List<TopicResult> topics) {
    }

    private final InterviewSessionRepository sessions;
    private final InterviewAnswerRepository answers;
    private final DrillService drills;
    private final ProgressService progress;

    public InterviewService(InterviewSessionRepository sessions, InterviewAnswerRepository answers,
                            DrillService drills, ProgressService progress) {
        this.sessions = sessions;
        this.answers = answers;
        this.drills = drills;
        this.progress = progress;
    }

    /** Starts an interview and returns its id. A blank topic means a mix of every topic. */
    public Long start(Long userId, String topic, String difficulty, int count, long now, Random random) {
        if (!COUNTS.contains(count)) {
            throw new IllegalArgumentException("Choose 5, 10 or 15 questions.");
        }
        String cleanTopic = blankToNull(topic);
        String cleanDifficulty = blankToNull(difficulty);
        if (cleanTopic != null && !drills.topics().containsKey(cleanTopic)) {
            throw new IllegalArgumentException("Unknown topic.");
        }
        List<Drill> pool = drills.matching(cleanTopic, cleanDifficulty);
        List<Integer> chosen = pick(pool, cleanTopic == null, count, random);
        if (chosen.isEmpty()) {
            throw new IllegalArgumentException("No questions match that choice.");
        }
        String ids = chosen.stream().map(String::valueOf).collect(Collectors.joining(","));
        InterviewSession session = new InterviewSession(userId, cleanTopic, cleanDifficulty, now,
                chosen.size() * SECONDS_PER_QUESTION, ids);
        return sessions.save(session).getId();
    }

    /** Picks distinct drills. For a mix, one topic after the other in turn, so the interview covers many topics. */
    static List<Integer> pick(List<Drill> pool, boolean mixed, int count, Random random) {
        List<Integer> chosen = new ArrayList<>();
        if (!mixed) {
            List<Drill> shuffled = new ArrayList<>(pool);
            Collections.shuffle(shuffled, random);
            shuffled.stream().limit(count).forEach(d -> chosen.add(d.id()));
            return chosen;
        }
        Map<String, List<Drill>> byTopic = new TreeMap<>();
        for (Drill d : pool) {
            byTopic.computeIfAbsent(d.topic(), t -> new ArrayList<>()).add(d);
        }
        List<List<Drill>> queues = new ArrayList<>(byTopic.values());
        queues.forEach(q -> Collections.shuffle(q, random));
        Collections.shuffle(queues, random);
        for (int round = 0; chosen.size() < count; round++) {
            boolean any = false;
            for (List<Drill> queue : queues) {
                if (round < queue.size() && chosen.size() < count) {
                    chosen.add(queue.get(round).id());
                    any = true;
                }
            }
            if (!any) {
                break;
            }
        }
        return chosen;
    }

    /** The interview, only if it belongs to this user. */
    public InterviewSession get(Long userId, Long id) {
        return sessions.findByIdAndUserId(id, userId).orElseThrow(() -> new NotFoundException("Interview", id));
    }

    /** The running interview as the learner sees it: questions without answers and the seconds left. */
    public TestView test(Long userId, Long id, long now) {
        InterviewSession session = get(userId, id);
        List<Question> questions = new ArrayList<>();
        int position = 0;
        for (int drillId : parseIds(session.getDrillIds())) {
            Drill d = drills.find(drillId).orElseThrow(() -> new NotFoundException("Drill", (long) drillId));
            questions.add(new Question(position++, d.id(), d.title(), d.question(), d.code(), d.kind(), d.choices()));
        }
        long remaining = Math.max(0, (session.deadline() - now) / 1000);
        return new TestView(session.getId(), session.getTopic(), session.getDurationSeconds(), remaining, questions);
    }

    /**
     * Grades the interview. Finishing twice just returns the first result. Answers that arrive long after the
     * deadline are ignored (every question counts as unanswered). Every question is also recorded in the learner's
     * progress, so the wrong ones come back through spaced repetition.
     */
    @Transactional
    public Report finish(Long userId, Long id, Map<String, String> given, long now) {
        InterviewSession session = get(userId, id);
        if (session.isFinished()) {
            return report(userId, id);
        }
        boolean late = now > session.deadline() + GRACE_MILLIS;
        List<Integer> ids = parseIds(session.getDrillIds());
        int score = 0;
        for (int position = 0; position < ids.size(); position++) {
            int drillId = ids.get(position);
            Drill drill = drills.find(drillId).orElseThrow(() -> new NotFoundException("Drill", (long) drillId));
            String answer = late || given == null ? null : clean(given.get(String.valueOf(drillId)));
            boolean correct = answer != null && drills.check(drill, answer).correct();
            score += correct ? 1 : 0;
            answers.save(new InterviewAnswer(id, position, drillId, answer, correct));
            progress.setResult(userId, "drill", (long) drillId, "Drill " + drillId + ": " + drill.title(),
                    "/drills/" + drillId, correct ? "right" : "wrong");
        }
        session.finish(now, score, late);
        sessions.save(session);
        return report(userId, id);
    }

    public Report report(Long userId, Long id) {
        InterviewSession session = get(userId, id);
        List<Line> lines = new ArrayList<>();
        Map<String, int[]> perTopic = new LinkedHashMap<>();
        for (InterviewAnswer a : answers.findBySessionIdOrderByPosition(id)) {
            Drill d = drills.find(a.getDrillId()).orElseThrow(() -> new NotFoundException("Drill", (long) a.getDrillId()));
            lines.add(new Line(a.getPosition() + 1, d.id(), d.title(), d.topic(), a.getGiven(), d.answer(),
                    a.isCorrect(), d.explanation(), "/drills/" + d.id()));
            int[] counts = perTopic.computeIfAbsent(d.topic(), t -> new int[2]);
            counts[0]++;
            counts[1] += a.isCorrect() ? 1 : 0;
        }
        List<TopicResult> topics = perTopic.entrySet().stream()
                .map(e -> new TopicResult(e.getKey(), e.getValue()[0], e.getValue()[1])).toList();
        int total = lines.size();
        int score = session.getScore() == null ? 0 : session.getScore();
        int percent = total == 0 ? 0 : Math.round(score * 100f / total);
        long used = session.getFinishedAt() == null ? 0
                : Math.min(session.getDurationSeconds(), (session.getFinishedAt() - session.getStartedAt()) / 1000);
        return new Report(id, session.getTopic(), session.getDifficulty(), total, score, percent, verdict(percent),
                used, session.getDurationSeconds(), session.isLate(), lines, topics);
    }

    public List<InterviewSession> recent(Long userId) {
        return sessions.findTop5ByUserIdAndFinishedAtIsNotNullOrderByFinishedAtDesc(userId);
    }

    static String verdict(int percent) {
        return percent >= 80 ? "Interview ready" : percent >= 60 ? "Getting there" : "Keep practising";
    }

    private static List<Integer> parseIds(String csv) {
        return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(Integer::valueOf).toList();
    }

    private static String clean(String answer) {
        if (answer == null || answer.isBlank()) {
            return null;
        }
        String trimmed = answer.strip();
        return trimmed.length() > MAX_ANSWER_CHARS ? trimmed.substring(0, MAX_ANSWER_CHARS) : trimmed;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
