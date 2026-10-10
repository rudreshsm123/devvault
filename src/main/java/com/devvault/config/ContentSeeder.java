package com.devvault.config;

import com.devvault.config.seed.BeginnerProblems;
import com.devvault.config.seed.HardProblems;
import com.devvault.config.seed.IntermediateProblems;
import com.devvault.config.seed.ResourceSeedData;
import com.devvault.config.seed.SnippetSeedData;
import com.devvault.entity.CodeSnippet;
import com.devvault.entity.CodingProblem;
import com.devvault.entity.LearningResource;
import com.devvault.repository.ProblemRepository;
import com.devvault.repository.ResourceRepository;
import com.devvault.repository.SnippetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Adds the large starter library (problems, snippets, resources). Safe to re-run: it skips anything already present. */
@Component
@Order(2)
public class ContentSeeder implements CommandLineRunner {

    private final ProblemRepository problems;
    private final SnippetRepository snippets;
    private final ResourceRepository resources;

    public ContentSeeder(ProblemRepository problems, SnippetRepository snippets, ResourceRepository resources) {
        this.problems = problems;
        this.snippets = snippets;
        this.resources = resources;
    }

    @Override
    public void run(String... args) {
        Set<String> titles = new HashSet<>();
        problems.findAll().forEach(p -> titles.add(key(p.getTitle())));
        List<CodingProblem> newProblems = new java.util.ArrayList<>();
        for (List<CodingProblem> group : List.of(BeginnerProblems.all(), IntermediateProblems.all(), HardProblems.all())) {
            for (CodingProblem p : group) {
                if (titles.add(key(p.getTitle()))) newProblems.add(p);
            }
        }
        problems.saveAll(newProblems);

        Set<String> snippetTitles = new HashSet<>();
        snippets.findAll().forEach(s -> snippetTitles.add(key(s.getTitle())));
        List<CodeSnippet> newSnippets = SnippetSeedData.all().stream()
                .filter(s -> snippetTitles.add(key(s.getTitle()))).toList();
        snippets.saveAll(newSnippets);

        Set<String> urls = new HashSet<>();
        resources.findAll().forEach(r -> urls.add(key(r.getUrl())));
        List<LearningResource> newResources = ResourceSeedData.all().stream()
                .filter(r -> urls.add(key(r.getUrl()))).toList();
        resources.saveAll(newResources);

        System.out.printf("ContentSeeder added %d problems, %d snippets, %d resources%n",
                newProblems.size(), newSnippets.size(), newResources.size());
    }

    private static String key(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }
}
