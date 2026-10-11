package com.devvault.controller;

import com.devvault.service.LessonService;
import com.devvault.service.ProblemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * "My Progress" page. The finished items live in the visitor's own browser (localStorage), so the server only
 * supplies the totals needed to draw the percentage bars.
 */
@Controller
public class ProgressController {

    private final ProblemService problemService;
    private final LessonService lessonService;

    public ProgressController(ProblemService problemService, LessonService lessonService) {
        this.problemService = problemService;
        this.lessonService = lessonService;
    }

    @GetMapping("/progress")
    public String progress(Model model) {
        model.addAttribute("problemCount", problemService.count());
        model.addAttribute("lessonCount", lessonService.count());
        return "progress";
    }
}
