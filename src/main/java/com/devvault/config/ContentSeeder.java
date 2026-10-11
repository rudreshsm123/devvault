package com.devvault.config;

import com.devvault.config.seed.BasicsProblemsA;
import com.devvault.config.seed.BasicsProblemsB;
import com.devvault.config.seed.BeginnerProblems;
import com.devvault.config.seed.EarlyBeginnerExplanations;
import com.devvault.config.seed.EarlyIntermediateExplanations;
import com.devvault.config.seed.ExtraProblems;
import com.devvault.config.seed.HardArraysHeaps;
import com.devvault.config.seed.HardDpStrings;
import com.devvault.config.seed.HardGraphsTreesDesign;
import com.devvault.config.seed.HardSystemsBackend;
import com.devvault.config.seed.MediumArraysStrings;
import com.devvault.config.seed.MediumDpGreedyStack;
import com.devvault.config.seed.MediumListsTreesGraphs;
import com.devvault.config.seed.MediumWebsiteBackend;
import com.devvault.config.seed.NoteSeedData;
import com.devvault.config.seed.ResourceSeedMore;
import com.devvault.config.seed.HardProblemExplanations;
import com.devvault.config.seed.HardProblems;
import com.devvault.config.seed.IntermediateProblems;
import com.devvault.config.seed.LessonsAlgorithmsWeb;
import com.devvault.config.seed.LessonsBackendCareer;
import com.devvault.config.seed.LessonsCollections;
import com.devvault.config.seed.LessonsFoundations;
import com.devvault.config.seed.ResourceSeedData;
import com.devvault.config.seed.SnippetSeedData;
import com.devvault.config.seed.StarterProblemExplanations;
import com.devvault.entity.CodeSnippet;
import com.devvault.entity.CodingProblem;
import com.devvault.entity.Lesson;
import com.devvault.entity.Note;
import com.devvault.repository.LessonRepository;
import com.devvault.repository.NoteRepository;
import com.devvault.entity.LearningResource;
import com.devvault.repository.ProblemRepository;
import com.devvault.repository.ResourceRepository;
import com.devvault.repository.SnippetRepository;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;

/** Adds the large starter library (problems, snippets, resources). Safe to re-run: it skips anything already present. Runs before the web server starts. */
@Component
public class ContentSeeder implements SmartInitializingSingleton {

    private final ProblemRepository problems;
    private final SnippetRepository snippets;
    private final ResourceRepository resources;
    private final LessonRepository lessons;
    private final NoteRepository notes;

    public ContentSeeder(ProblemRepository problems, SnippetRepository snippets, ResourceRepository resources,
                         LessonRepository lessons, NoteRepository notes) {
        this.notes = notes;
        this.problems = problems;
        this.snippets = snippets;
        this.resources = resources;
        this.lessons = lessons;
    }

    /**
     * Runs after all beans exist but BEFORE the web server starts accepting requests, so visitors can never
     * see a half-filled library (for example zero lessons) while the data is still loading.
     */
    @Override
    public void afterSingletonsInstantiated() {
        Set<String> titles = new HashSet<>();
        problems.findAll().forEach(p -> titles.add(key(p.getTitle())));
        List<CodingProblem> newProblems = new java.util.ArrayList<>();
        for (List<CodingProblem> group : List.of(
                BeginnerProblems.all(), IntermediateProblems.all(), HardProblems.all(),
                BasicsProblemsA.all(), BasicsProblemsB.all(), ExtraProblems.all(),
                MediumArraysStrings.all(), MediumListsTreesGraphs.all(), MediumDpGreedyStack.all(), MediumWebsiteBackend.all(),
                HardDpStrings.all(), HardGraphsTreesDesign.all(), HardArraysHeaps.all(), HardSystemsBackend.all())) {
            for (CodingProblem p : group) {
                if (titles.add(key(p.getTitle()))) newProblems.add(p);
            }
        }
        problems.saveAll(newProblems);

        // Problems seeded before line-by-line explanations existed get them added here.
        Map<String, HardProblemExplanations.Explanation> extra = new java.util.HashMap<>(HardProblemExplanations.all());
        extra.putAll(StarterProblemExplanations.all());
        extra.putAll(EarlyBeginnerExplanations.all());
        extra.putAll(EarlyIntermediateExplanations.all());
        List<CodingProblem> toEnrich = problems.findAll().stream()
                .filter(p -> p.getLineByLine() == null || p.getLineByLine().isBlank())
                .filter(p -> extra.containsKey(p.getTitle()))
                .toList();
        for (CodingProblem p : toEnrich) {
            p.setLineByLine(extra.get(p.getTitle()).lineByLine());
            p.setInterviewTips(extra.get(p.getTitle()).interviewTips());
        }
        problems.saveAll(toEnrich);

        Set<String> snippetTitles = new HashSet<>();
        snippets.findAll().forEach(s -> snippetTitles.add(key(s.getTitle())));
        List<CodeSnippet> newSnippets = SnippetSeedData.all().stream()
                .filter(s -> snippetTitles.add(key(s.getTitle()))).toList();
        snippets.saveAll(newSnippets);

        Set<String> urls = new HashSet<>();
        resources.findAll().forEach(r -> urls.add(key(r.getUrl())));
        List<LearningResource> allResources = new java.util.ArrayList<>(ResourceSeedData.all());
        allResources.addAll(ResourceSeedMore.all());
        List<LearningResource> newResources = allResources.stream()
                .filter(r -> urls.add(key(r.getUrl()))).toList();
        resources.saveAll(newResources);

        Set<String> lessonKeys = new HashSet<>();
        lessons.findAll().forEach(x -> lessonKeys.add(x.getChapterNo() + ":" + x.getLessonNo()));
        List<Lesson> allLessons = new java.util.ArrayList<>();
        allLessons.addAll(LessonsFoundations.all());
        allLessons.addAll(LessonsCollections.all());
        allLessons.addAll(LessonsAlgorithmsWeb.all());
        allLessons.addAll(LessonsBackendCareer.all());
        List<Lesson> newLessons = allLessons.stream()
                .filter(x -> lessonKeys.add(x.getChapterNo() + ":" + x.getLessonNo())).toList();
        lessons.saveAll(newLessons);

        Set<String> noteTitles = new HashSet<>();
        notes.findAll().forEach(x -> noteTitles.add(key(x.getTitle())));
        List<Note> newNotes = NoteSeedData.all().stream().filter(x -> noteTitles.add(key(x.getTitle()))).toList();
        notes.saveAll(newNotes);

        System.out.printf("ContentSeeder added %d problems, %d snippets, %d resources, %d lessons, %d notes%n",
                newProblems.size(), newSnippets.size(), newResources.size(), newLessons.size(), newNotes.size());
    }

    private static String key(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }
}
