package com.devvault.controller;

import com.devvault.ai.AiExplainer;
import com.devvault.ai.RateLimiter;
import com.devvault.drill.Drill;
import com.devvault.drill.DrillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

/** "Explain with AI" for a drill the signed-in learner just answered. */
@RestController
public class AiController {

    public record ExplainRequest(Integer drillId, String answer) {
    }

    private final AiExplainer explainer;
    private final DrillService drills;
    private final RateLimiter limiter;

    public AiController(AiExplainer explainer, DrillService drills, RateLimiter limiter) {
        this.explainer = explainer;
        this.drills = drills;
        this.limiter = limiter;
    }

    @PostMapping("/api/me/ai/explain")
    public ResponseEntity<Map<String, Object>> explain(@RequestBody ExplainRequest request, Principal principal) {
        Optional<Drill> drill = request.drillId() == null ? Optional.empty() : drills.find(request.drillId());
        if (drill.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Drill not found"));
        }
        if (!limiter.tryAcquire(principal.getName(), System.currentTimeMillis())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "You have used the AI explainer a lot this hour. Try again later."));
        }
        AiExplainer.Explanation result = explainer.explain(drill.get(), request.answer());
        return ResponseEntity.ok(Map.of("text", result.text(), "source", result.source()));
    }
}
