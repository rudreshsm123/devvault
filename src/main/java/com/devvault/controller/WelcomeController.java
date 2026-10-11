package com.devvault.controller;

import com.devvault.service.NoteService;
import com.devvault.service.ProblemService;
import com.devvault.service.ResourceService;
import com.devvault.service.SnippetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    private final NoteService noteService;
    private final ProblemService problemService;
    private final ResourceService resourceService;
    private final SnippetService snippetService;

    public WelcomeController(NoteService noteService, ProblemService problemService,
                             ResourceService resourceService, SnippetService snippetService) {
        this.noteService = noteService;
        this.problemService = problemService;
        this.resourceService = resourceService;
        this.snippetService = snippetService;
    }

    @GetMapping("/")
    public String welcome(Model model) {
        model.addAttribute("noteCount", noteService.count());
        model.addAttribute("snippetCount", snippetService.count());
        model.addAttribute("problemCount", problemService.count());
        model.addAttribute("resourceCount", resourceService.count());
        return "welcome";
    }
}
