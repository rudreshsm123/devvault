package com.devvault.controller;

import com.devvault.entity.CodeSnippet;
import com.devvault.entity.CodingProblem;
import com.devvault.entity.LearningResource;
import com.devvault.entity.Lesson;
import com.devvault.entity.Note;
import com.devvault.service.LessonService;
import com.devvault.service.NoteService;
import com.devvault.service.ProblemService;
import com.devvault.service.ResourceService;
import com.devvault.service.SnippetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/** One search box across lessons, problems, snippets, notes and resources. */
@Controller
public class SearchController {

    /** How many results to show per group; the "see all" link opens the full filtered list. */
    static final int PER_GROUP = 8;

    private final ProblemService problemService;
    private final LessonService lessonService;
    private final SnippetService snippetService;
    private final NoteService noteService;
    private final ResourceService resourceService;

    public SearchController(ProblemService problemService, LessonService lessonService,
                            SnippetService snippetService, NoteService noteService,
                            ResourceService resourceService) {
        this.problemService = problemService;
        this.lessonService = lessonService;
        this.snippetService = snippetService;
        this.noteService = noteService;
        this.resourceService = resourceService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        String query = q == null ? "" : q.trim();
        model.addAttribute("q", query);

        if (query.length() < 2) {
            model.addAttribute("tooShort", !query.isEmpty());
            model.addAttribute("total", 0);
            return "search";
        }

        List<Lesson> lessons = lessonService.search(query);
        List<CodingProblem> problems = problemService.search(query, null, null, null);
        List<CodeSnippet> snippets = snippetService.search(query, null);
        List<Note> notes = noteService.search(query, null, null, null, null);
        List<LearningResource> resources = resourceService.search(query, null, null);

        model.addAttribute("lessons", limit(lessons));
        model.addAttribute("lessonTotal", lessons.size());
        model.addAttribute("problems", limit(problems));
        model.addAttribute("problemTotal", problems.size());
        model.addAttribute("snippets", limit(snippets));
        model.addAttribute("snippetTotal", snippets.size());
        model.addAttribute("notes", limit(notes));
        model.addAttribute("noteTotal", notes.size());
        model.addAttribute("resources", limit(resources));
        model.addAttribute("resourceTotal", resources.size());
        model.addAttribute("total",
                lessons.size() + problems.size() + snippets.size() + notes.size() + resources.size());
        model.addAttribute("tooShort", false);
        return "search";
    }

    private static <T> List<T> limit(List<T> items) {
        return items.size() > PER_GROUP ? items.subList(0, PER_GROUP) : items;
    }
}
