package com.devvault.service;

import com.devvault.drill.DrillService;
import com.devvault.entity.AttemptLog;
import com.devvault.entity.CodingProblem;
import com.devvault.repository.AttemptLogRepository;
import com.devvault.repository.ProblemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** The signed-in user's most recent answers, newest first, with a link back to each drill or problem. */
@Service
public class HistoryService {

    public record Entry(String kind, Long itemId, String title, String url, String topic, boolean correct, long at) {
    }

    private final AttemptLogRepository attempts;
    private final DrillService drills;
    private final ProblemRepository problems;

    public HistoryService(AttemptLogRepository attempts, DrillService drills, ProblemRepository problems) {
        this.attempts = attempts;
        this.drills = drills;
        this.problems = problems;
    }

    @Transactional(readOnly = true)
    public List<Entry> recent(Long userId) {
        return attempts.findTop100ByUserIdOrderByAtDesc(userId).stream().map(this::entry).toList();
    }

    private Entry entry(AttemptLog a) {
        if ("drill".equals(a.getKind())) {
            return drills.find(Math.toIntExact(a.getItemId()))
                    .map(d -> new Entry("drill", a.getItemId(), "Drill " + d.id() + ": " + d.title(),
                            "/drills/" + d.id(), d.topic(), a.isCorrect(), a.getAt()))
                    .orElseGet(() -> missing(a, "Drill " + a.getItemId()));
        }
        return problems.findById(a.getItemId())
                .map((CodingProblem p) -> new Entry("problem", a.getItemId(), p.getTitle(),
                        "/problems/" + p.getId(), p.getTopic(), a.isCorrect(), a.getAt()))
                .orElseGet(() -> missing(a, "Problem " + a.getItemId()));
    }

    private static Entry missing(AttemptLog a, String title) {
        return new Entry(a.getKind(), a.getItemId(), title + " (removed)", null, "Other", a.isCorrect(), a.getAt());
    }
}
