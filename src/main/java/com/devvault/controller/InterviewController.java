package com.devvault.controller;

import com.devvault.drill.DrillService;
import com.devvault.entity.InterviewSession;
import com.devvault.service.InterviewService;
import com.devvault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Timed mock interview: set it up, take it against the clock, read the report. */
@Controller
public class InterviewController {

    public record FinishRequest(Map<String, String> answers) {
    }

    private final InterviewService interviews;
    private final UserService users;
    private final DrillService drills;

    public InterviewController(InterviewService interviews, UserService users, DrillService drills) {
        this.interviews = interviews;
        this.users = users;
        this.drills = drills;
    }

    @GetMapping("/interview")
    public String setup(@RequestParam(required = false) String error, Principal principal, Model model) {
        model.addAttribute("topics", drills.topics());
        model.addAttribute("secondsPerQuestion", InterviewService.SECONDS_PER_QUESTION);
        model.addAttribute("error", error != null);
        if (principal != null) {
            model.addAttribute("recent", interviews.recent(userId(principal)));
        }
        return "interview/index";
    }

    @PostMapping("/interview/start")
    public String start(@RequestParam(required = false) String topic,
                        @RequestParam(required = false) String difficulty,
                        @RequestParam(defaultValue = "10") int count, Principal principal) {
        try {
            Long id = interviews.start(userId(principal), topic, difficulty, count, System.currentTimeMillis(),
                    ThreadLocalRandom.current());
            return "redirect:/interview/" + id;
        } catch (IllegalArgumentException e) {
            return "redirect:/interview?error";
        }
    }

    /** The test while it is running, or the report once it is finished. */
    @GetMapping("/interview/{id}")
    public String show(@PathVariable Long id, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        Long userId = userId(principal);
        InterviewSession session = interviews.get(userId, id);
        if (session.isFinished()) {
            model.addAttribute("report", interviews.report(userId, id));
            return "interview/report";
        }
        model.addAttribute("test", interviews.test(userId, id, System.currentTimeMillis()));
        return "interview/test";
    }

    /** Submits the answers (also called by the page when the time runs out) and grades the interview. */
    @PostMapping("/api/me/interview/{id}/finish")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> finish(@PathVariable Long id, @RequestBody FinishRequest request,
                                                      Principal principal) {
        InterviewService.Report report = interviews.finish(userId(principal), id, request.answers(),
                System.currentTimeMillis());
        return ResponseEntity.ok(Map.of("score", report.score(), "total", report.total(), "late", report.late()));
    }

    private Long userId(Principal principal) {
        return users.getByUsername(principal.getName()).getId();
    }
}
