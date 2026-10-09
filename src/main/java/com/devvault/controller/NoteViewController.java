package com.devvault.controller;

import com.devvault.entity.Difficulty;
import com.devvault.entity.Note;
import com.devvault.entity.RevisionStatus;
import com.devvault.service.NoteService;
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
@RequestMapping("/notes")
public class NoteViewController {

    private final NoteService noteService;

    public NoteViewController(NoteService noteService) {
        this.noteService = noteService;
    }

    @ModelAttribute("difficulties")
    public Difficulty[] difficulties() {
        return Difficulty.values();
    }

    @ModelAttribute("revisionStatuses")
    public RevisionStatus[] revisionStatuses() {
        return RevisionStatus.values();
    }

    @GetMapping
    public String listNotes(@RequestParam(required = false) String q,
                             @RequestParam(required = false) String language,
                             @RequestParam(required = false) String topic,
                             @RequestParam(required = false) Difficulty difficulty,
                             @RequestParam(required = false) RevisionStatus revisionStatus,
                             Model model) {
        model.addAttribute("notes", noteService.search(q, language, topic, difficulty, revisionStatus));
        model.addAttribute("q", q);
        model.addAttribute("selectedLanguage", language);
        model.addAttribute("selectedTopic", topic);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("selectedRevisionStatus", revisionStatus);
        return "notes/list";
    }

    @GetMapping("/new")
    public String newNoteForm(Model model) {
        model.addAttribute("note", new Note());
        return "notes/form";
    }

    @PostMapping
    public String createNote(@Valid @ModelAttribute("note") Note note, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notes/form";
        }
        noteService.createNote(note);
        return "redirect:/notes";
    }

    @GetMapping("/{id}")
    public String viewNote(@PathVariable Long id, Model model) {
        model.addAttribute("note", noteService.getNoteById(id));
        return "notes/view";
    }

    @GetMapping("/{id}/edit")
    public String editNoteForm(@PathVariable Long id, Model model) {
        model.addAttribute("note", noteService.getNoteById(id));
        return "notes/form";
    }

    @PostMapping("/{id}/edit")
    public String updateNote(@PathVariable Long id, @Valid @ModelAttribute("note") Note note,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notes/form";
        }
        noteService.updateNote(id, note);
        return "redirect:/notes";
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return "redirect:/notes";
    }
}
