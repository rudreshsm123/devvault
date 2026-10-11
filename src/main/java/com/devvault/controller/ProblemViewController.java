package com.devvault.controller;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;
import com.devvault.entity.RevisionStatus;
import com.devvault.service.ChallengeService;
import com.devvault.service.JavaJudgeService;
import com.devvault.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/problems")
public class ProblemViewController {

    private final ProblemService problemService;
    private final ChallengeService challengeService;
    private final JavaJudgeService javaJudge;
    private final boolean readOnly;

    public ProblemViewController(ProblemService problemService, ChallengeService challengeService,
                                 JavaJudgeService javaJudge,
                                 @org.springframework.beans.factory.annotation.Value("${devvault.readonly:false}") boolean readOnly) {
        this.problemService = problemService;
        this.challengeService = challengeService;
        this.javaJudge = javaJudge;
        this.readOnly = readOnly;
    }

    @ModelAttribute("difficulties")
    public Difficulty[] difficulties() {
        return Difficulty.values();
    }

    @ModelAttribute("practiceStatuses")
    public RevisionStatus[] practiceStatuses() {
        return RevisionStatus.values();
    }

    @GetMapping
    public String listProblems(@RequestParam(required = false) String q,
                                @RequestParam(required = false) String topic,
                                @RequestParam(required = false) Difficulty difficulty,
                                @RequestParam(required = false) RevisionStatus practiceStatus,
                                Model model) {
        model.addAttribute("problems", problemService.search(q, topic, difficulty, practiceStatus));
        model.addAttribute("q", q);
        model.addAttribute("selectedTopic", topic);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("selectedPracticeStatus", practiceStatus);
        return "problems/list";
    }

    @GetMapping("/new")
    public String newProblemForm(Model model) {
        model.addAttribute("problem", new CodingProblem());
        return "problems/form";
    }

    @PostMapping
    public String createProblem(@Valid @ModelAttribute("problem") CodingProblem problem, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "problems/form";
        }
        problemService.createProblem(problem);
        return "redirect:/problems";
    }

    @GetMapping("/{id}")
    public String viewProblem(@PathVariable Long id, Model model) {
        model.addAttribute("problem", problemService.getProblemById(id));
        challengeService.findForProblem(id).ifPresent(c ->
                model.addAttribute("challengeJson", challengeService.clientJson(c, javaJudge.isEnabled(), !readOnly)));
        return "problems/view";
    }

    @GetMapping("/{id}/edit")
    public String editProblemForm(@PathVariable Long id, Model model) {
        model.addAttribute("problem", problemService.getProblemById(id));
        return "problems/form";
    }

    @PostMapping("/{id}/edit")
    public String updateProblem(@PathVariable Long id, @Valid @ModelAttribute("problem") CodingProblem problem,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "problems/form";
        }
        problemService.updateProblem(id, problem);
        return "redirect:/problems";
    }

    @PostMapping("/{id}/delete")
    public String deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return "redirect:/problems";
    }

    @PostMapping("/{id}/attempt")
    public String submitAttempt(@PathVariable Long id, @RequestParam String userAttempt) {
        problemService.saveAttempt(id, userAttempt);
        return "redirect:/problems/" + id;
    }

    /** One-click practice status (not started, learning, learned, needs revision). */
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam RevisionStatus status) {
        problemService.updatePracticeStatus(id, status);
        return "redirect:/problems/" + id;
    }

    @PostMapping("/{id}/attempt/clear")
    public String clearAttempt(@PathVariable Long id) {
        problemService.clearAttempt(id);
        return "redirect:/problems/" + id;
    }
}
