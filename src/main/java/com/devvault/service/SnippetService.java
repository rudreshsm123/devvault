package com.devvault.service;

import com.devvault.entity.CodeSnippet;
import com.devvault.entity.SnippetLanguage;
import com.devvault.exception.NotFoundException;
import com.devvault.repository.SnippetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnippetService {

    private final SnippetRepository snippetRepository;

    public SnippetService(SnippetRepository snippetRepository) {
        this.snippetRepository = snippetRepository;
    }

    public CodeSnippet createSnippet(CodeSnippet snippet) {
        return snippetRepository.save(snippet);
    }

    public List<CodeSnippet> getAllSnippets() {
        return snippetRepository.findAll();
    }

    public List<CodeSnippet> search(String keyword, SnippetLanguage language) {
        return snippetRepository.search(blankToNull(keyword), language);
    }

    public CodeSnippet getSnippetById(Long id) {
        return snippetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Code snippet", id));
    }

    public CodeSnippet updateSnippet(Long id, CodeSnippet updated) {
        CodeSnippet existing = getSnippetById(id);
        existing.setTitle(updated.getTitle());
        existing.setCode(updated.getCode());
        existing.setDescription(updated.getDescription());
        existing.setLanguage(updated.getLanguage());
        existing.setTags(updated.getTags());
        return snippetRepository.save(existing);
    }

    public void deleteSnippet(Long id) {
        snippetRepository.delete(getSnippetById(id));
    }

    public long count() {
        return snippetRepository.count();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
