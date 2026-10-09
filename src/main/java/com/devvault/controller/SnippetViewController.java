package com.devvault.controller;

import com.devvault.entity.CodeSnippet;
import com.devvault.entity.SnippetLanguage;
import com.devvault.service.SnippetService;
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
@RequestMapping("/snippets")
public class SnippetViewController {

    private final SnippetService snippetService;

    public SnippetViewController(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    @ModelAttribute("languages")
    public SnippetLanguage[] languages() {
        return SnippetLanguage.values();
    }

    @GetMapping
    public String listSnippets(@RequestParam(required = false) String q,
                                @RequestParam(required = false) SnippetLanguage language,
                                Model model) {
        model.addAttribute("snippets", snippetService.search(q, language));
        model.addAttribute("q", q);
        model.addAttribute("selectedLanguage", language);
        return "snippets/list";
    }

    @GetMapping("/new")
    public String newSnippetForm(Model model) {
        model.addAttribute("snippet", new CodeSnippet());
        return "snippets/form";
    }

    @PostMapping
    public String createSnippet(@Valid @ModelAttribute("snippet") CodeSnippet snippet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "snippets/form";
        }
        snippetService.createSnippet(snippet);
        return "redirect:/snippets";
    }

    @GetMapping("/{id}")
    public String viewSnippet(@PathVariable Long id, Model model) {
        model.addAttribute("snippet", snippetService.getSnippetById(id));
        return "snippets/view";
    }

    @GetMapping("/{id}/edit")
    public String editSnippetForm(@PathVariable Long id, Model model) {
        model.addAttribute("snippet", snippetService.getSnippetById(id));
        return "snippets/form";
    }

    @PostMapping("/{id}/edit")
    public String updateSnippet(@PathVariable Long id, @Valid @ModelAttribute("snippet") CodeSnippet snippet,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "snippets/form";
        }
        snippetService.updateSnippet(id, snippet);
        return "redirect:/snippets";
    }

    @PostMapping("/{id}/delete")
    public String deleteSnippet(@PathVariable Long id) {
        snippetService.deleteSnippet(id);
        return "redirect:/snippets";
    }
}
