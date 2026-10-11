package com.devvault.controller;

import com.devvault.service.NoteService;
import com.devvault.service.ProblemService;
import com.devvault.service.ResourceService;
import com.devvault.service.SnippetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final NoteService noteService;
    private final ProblemService problemService;
    private final ResourceService resourceService;
    private final SnippetService snippetService;

    public DashboardController(NoteService noteService, ProblemService problemService,
                                ResourceService resourceService, SnippetService snippetService) {
        this.noteService = noteService;
        this.problemService = problemService;
        this.resourceService = resourceService;
        this.snippetService = snippetService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("noteCount", noteService.count());
        model.addAttribute("snippetCount", snippetService.count());
        model.addAttribute("problemCount", problemService.count());
        model.addAttribute("resourceCount", resourceService.count());

        model.addAttribute("recentNotes", noteService.getRecentNotes());
        model.addAttribute("notesNeedingRevision", noteService.getNotesNeedingRevision());
        model.addAttribute("problemsNeedingRevision", problemService.getProblemsNeedingRevision());

        return "dashboard";
    }
}
