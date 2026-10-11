package com.devvault.controller;

import com.devvault.entity.CodeChallenge;
import com.devvault.exception.NotFoundException;
import com.devvault.service.ChallengeService;
import com.devvault.service.JavaJudgeService;
import com.devvault.service.JavaJudgeService.Verdict;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Judges a Java solution. JavaScript solutions never reach the server: they run in the visitor's browser. */
@RestController
@RequestMapping("/api/judge")
public class JudgeController {

    public record JudgeRequest(Long problemId, String code) {
    }

    private final ChallengeService challengeService;
    private final JavaJudgeService javaJudge;

    public JudgeController(ChallengeService challengeService, JavaJudgeService javaJudge) {
        this.challengeService = challengeService;
        this.javaJudge = javaJudge;
    }

    @PostMapping("/java")
    public Verdict judgeJava(@RequestBody JudgeRequest request) {
        if (!javaJudge.isEnabled()) {
            return javaJudge.judge(null, request.code());      // returns the DISABLED verdict without touching the challenge
        }
        CodeChallenge challenge = challengeService.findForProblem(request.problemId())
                .orElseThrow(() -> new NotFoundException("Code challenge", request.problemId()));
        return javaJudge.judge(challenge, request.code());
    }
}
