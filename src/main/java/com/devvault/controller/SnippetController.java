package com.devvault.controller;

import com.devvault.entity.CodeSnippet;
import com.devvault.service.SnippetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/snippets")
public class SnippetController {

    private final SnippetService snippetService;

    public SnippetController(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    @PostMapping
    public ResponseEntity<CodeSnippet> createSnippet(@Valid @RequestBody CodeSnippet snippet) {
        CodeSnippet saved = snippetService.createSnippet(snippet);
        return ResponseEntity.created(URI.create("/api/snippets/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<CodeSnippet> getAllSnippets() {
        return snippetService.getAllSnippets();
    }

    @GetMapping("/{id}")
    public CodeSnippet getSnippetById(@PathVariable Long id) {
        return snippetService.getSnippetById(id);
    }

    @PutMapping("/{id}")
    public CodeSnippet updateSnippet(@PathVariable Long id, @Valid @RequestBody CodeSnippet snippet) {
        return snippetService.updateSnippet(id, snippet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnippet(@PathVariable Long id) {
        snippetService.deleteSnippet(id);
        return ResponseEntity.noContent().build();
    }
}
