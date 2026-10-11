package com.devvault.controller;

import com.devvault.service.LessonService;
import com.devvault.service.NoteService;
import com.devvault.service.ProblemService;
import com.devvault.service.ResourceService;
import com.devvault.service.SnippetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** "Skills and Tech" page: what the project is built with and what it demonstrates to a recruiter. */
@Controller
public class AboutController {

    private final NoteService noteService;
    private final ProblemService problemService;
    private final ResourceService resourceService;
    private final SnippetService snippetService;
    private final LessonService lessonService;

    public AboutController(NoteService noteService, ProblemService problemService,
                           ResourceService resourceService, SnippetService snippetService,
                           LessonService lessonService) {
        this.noteService = noteService;
        this.problemService = problemService;
        this.resourceService = resourceService;
        this.snippetService = snippetService;
        this.lessonService = lessonService;
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("noteCount", noteService.count());
        model.addAttribute("problemCount", problemService.count());
        model.addAttribute("resourceCount", resourceService.count());
        model.addAttribute("snippetCount", snippetService.count());
        model.addAttribute("lessonCount", lessonService.count());
        return "about";
    }
}
