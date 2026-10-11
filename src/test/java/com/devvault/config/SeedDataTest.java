package com.devvault.config;

import com.devvault.config.seed.BasicsProblemsA;
import com.devvault.config.seed.BasicsProblemsB;
import com.devvault.config.seed.BeginnerProblems;
import com.devvault.config.seed.ExtraProblems;
import com.devvault.config.seed.HardArraysHeaps;
import com.devvault.config.seed.HardDpStrings;
import com.devvault.config.seed.HardGraphsTreesDesign;
import com.devvault.config.seed.EarlyBeginnerExplanations;
import com.devvault.config.seed.EarlyIntermediateExplanations;
import com.devvault.config.seed.HardProblemExplanations;
import com.devvault.config.seed.StarterProblemExplanations;
import com.devvault.config.seed.HardProblems;
import com.devvault.config.seed.HardSystemsBackend;
import com.devvault.config.seed.IntermediateProblems;
import com.devvault.config.seed.LessonsAlgorithmsWeb;
import com.devvault.config.seed.LessonsBackendCareer;
import com.devvault.config.seed.LessonsCollections;
import com.devvault.config.seed.LessonsFoundations;
import com.devvault.config.seed.MediumArraysStrings;
import com.devvault.config.seed.MediumDpGreedyStack;
import com.devvault.config.seed.MediumListsTreesGraphs;
import com.devvault.config.seed.MediumWebsiteBackend;
import com.devvault.config.seed.NoteSeedData;
import com.devvault.config.seed.ResourceSeedData;
import com.devvault.config.seed.ResourceSeedMore;
import com.devvault.config.seed.SnippetSeedData;
import com.devvault.entity.CodeSnippet;
import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;
import com.devvault.entity.LearningResource;
import com.devvault.entity.Lesson;
import com.devvault.entity.Note;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/** Guards the quality of the starter content so a typo can never ship an empty or duplicated entry. */
class SeedDataTest {

    private static List<CodingProblem> explainedProblems() {
        List<CodingProblem> all = new ArrayList<>();
        all.addAll(BasicsProblemsA.all());
        all.addAll(BasicsProblemsB.all());
        all.addAll(ExtraProblems.all());
        all.addAll(MediumArraysStrings.all());
        all.addAll(MediumListsTreesGraphs.all());
        all.addAll(MediumDpGreedyStack.all());
        all.addAll(MediumWebsiteBackend.all());
        all.addAll(HardDpStrings.all());
        all.addAll(HardGraphsTreesDesign.all());
        all.addAll(HardArraysHeaps.all());
        all.addAll(HardSystemsBackend.all());
        return all;
    }

    private static long pairs(String text) {
        return text.lines().filter(line -> line.contains("==>")).count();
    }

    @Test
    void newProblemLibraryHasTwoHundredExplainedProblems() {
        assertThat(explainedProblems()).hasSize(200);
    }

    @Test
    void everyProblemHasTheFieldsTheSiteDisplays() {
        List<CodingProblem> all = new ArrayList<>(explainedProblems());
        all.addAll(BeginnerProblems.all());
        all.addAll(IntermediateProblems.all());
        all.addAll(HardProblems.all());

        for (CodingProblem p : all) {
            assertThat(p.getTitle()).as("title").isNotBlank();
            assertThat(p.getDescription()).as(p.getTitle() + " description").isNotBlank();
            assertThat(p.getSolutionCode()).as(p.getTitle() + " solution").isNotBlank();
            assertThat(p.getExplanation()).as(p.getTitle() + " explanation").isNotBlank();
            assertThat(p.getPatternHint()).as(p.getTitle() + " hint").isNotBlank();
            assertThat(p.getTimeComplexity()).as(p.getTitle() + " time").isNotBlank();
            assertThat(p.getSpaceComplexity()).as(p.getTitle() + " space").isNotBlank();
            assertThat(p.getDifficulty()).as(p.getTitle() + " difficulty").isNotNull();
        }
    }

    @Test
    void problemTitlesAreUnique() {
        Set<String> seen = new HashSet<>();
        List<CodingProblem> all = new ArrayList<>(explainedProblems());
        all.addAll(BeginnerProblems.all());
        all.addAll(IntermediateProblems.all());
        all.addAll(HardProblems.all());
        for (CodingProblem p : all) {
            assertThat(seen.add(p.getTitle().toLowerCase(Locale.ROOT))).as("duplicate title: " + p.getTitle()).isTrue();
        }
    }

    @Test
    void everyNewProblemExplainsItsCodeLineByLineAndTheInterviewerView() {
        for (CodingProblem p : explainedProblems()) {
            assertThat(pairs(p.getLineByLine())).as(p.getTitle() + " line-by-line pairs").isGreaterThanOrEqualTo(3);
            assertThat(p.getInterviewTips()).as(p.getTitle() + " interview tips").isNotBlank();
        }
    }

    @Test
    void difficultyMixCoversBeginnerIntermediateAndHard() {
        List<CodingProblem> all = explainedProblems();
        assertThat(all.stream().filter(p -> p.getDifficulty() == Difficulty.EASY).count()).isGreaterThanOrEqualTo(40);
        assertThat(all.stream().filter(p -> p.getDifficulty() == Difficulty.MEDIUM).count()).isGreaterThanOrEqualTo(60);
        assertThat(all.stream().filter(p -> p.getDifficulty() == Difficulty.HARD).count()).isGreaterThanOrEqualTo(60);
    }

    @Test
    void everyEarlyHardProblemGetsALineByLineExplanation() {
        var explanations = HardProblemExplanations.all();
        for (CodingProblem p : HardProblems.all()) {
            assertThat(explanations).as("missing explanation for " + p.getTitle()).containsKey(p.getTitle());
            assertThat(pairs(explanations.get(p.getTitle()).lineByLine())).as(p.getTitle()).isGreaterThanOrEqualTo(3);
            assertThat(explanations.get(p.getTitle()).interviewTips()).as(p.getTitle()).isNotBlank();
        }
        assertThat(explanations).hasSize(HardProblems.all().size());
    }

    @Test
    void everyEarlierProblemNowHasALineByLineExplanation() {
        java.util.Map<String, HardProblemExplanations.Explanation> all = new java.util.HashMap<>(HardProblemExplanations.all());
        all.putAll(StarterProblemExplanations.all());
        all.putAll(EarlyBeginnerExplanations.all());
        all.putAll(EarlyIntermediateExplanations.all());

        List<CodingProblem> earlier = new ArrayList<>();
        earlier.addAll(BeginnerProblems.all());
        earlier.addAll(IntermediateProblems.all());
        earlier.addAll(HardProblems.all());
        for (CodingProblem p : earlier) {
            assertThat(all).as("missing explanation for " + p.getTitle()).containsKey(p.getTitle());
        }
        for (var entry : StarterProblemExplanations.all().entrySet()) {
            assertThat(pairs(entry.getValue().lineByLine())).as(entry.getKey()).isGreaterThanOrEqualTo(3);
            assertThat(entry.getValue().interviewTips()).as(entry.getKey()).isNotBlank();
        }
        for (var entry : EarlyBeginnerExplanations.all().entrySet()) {
            assertThat(pairs(entry.getValue().lineByLine())).as(entry.getKey()).isGreaterThanOrEqualTo(3);
        }
        for (var entry : EarlyIntermediateExplanations.all().entrySet()) {
            assertThat(pairs(entry.getValue().lineByLine())).as(entry.getKey()).isGreaterThanOrEqualTo(3);
        }
        assertThat(StarterProblemExplanations.all()).hasSize(26);
    }

    @Test
    void lessonsFormAnOrderedChapterPathWithLineByLineExplanations() {
        List<Lesson> lessons = new ArrayList<>();
        lessons.addAll(LessonsFoundations.all());
        lessons.addAll(LessonsCollections.all());
        lessons.addAll(LessonsAlgorithmsWeb.all());
        lessons.addAll(LessonsBackendCareer.all());

        assertThat(lessons.size()).isGreaterThanOrEqualTo(50);
        Set<String> keys = new HashSet<>();
        Set<Integer> chaptersWithIntro = new HashSet<>();
        for (Lesson l : lessons) {
            assertThat(keys.add(l.getChapterNo() + ":" + l.getLessonNo())).as("duplicate lesson key " + l.getTitle()).isTrue();
            assertThat(l.getWhyLearn()).as(l.getTitle() + " why").isNotBlank();
            assertThat(l.getConcept()).as(l.getTitle() + " concept").isNotBlank();
            assertThat(l.getInterviewView()).as(l.getTitle() + " interview view").isNotBlank();
            assertThat(pairs(l.getLineByLine())).as(l.getTitle() + " line-by-line pairs").isGreaterThanOrEqualTo(4);
            if (l.getChapterIntro() != null) {
                chaptersWithIntro.add(l.getChapterNo());
            }
        }
        assertThat(chaptersWithIntro).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    void snippetsAllCarryAMeaning() {
        Set<String> titles = new HashSet<>();
        for (CodeSnippet s : SnippetSeedData.all()) {
            assertThat(s.getMeaning()).as(s.getTitle() + " meaning").isNotBlank();
            assertThat(s.getCode()).as(s.getTitle() + " code").isNotBlank();
            assertThat(titles.add(s.getTitle().toLowerCase(Locale.ROOT))).as("duplicate snippet " + s.getTitle()).isTrue();
        }
    }

    @Test
    void resourceLinksAreSecureAndUnique() {
        List<LearningResource> all = new ArrayList<>(ResourceSeedData.all());
        all.addAll(ResourceSeedMore.all());
        all.addAll(com.devvault.config.seed.ResourceSeedDrills.all());
        Set<String> urls = new HashSet<>();
        for (LearningResource r : all) {
            assertThat(r.getUrl()).as(r.getTitle()).startsWith("http");
            assertThat(urls.add(r.getUrl().toLowerCase(Locale.ROOT))).as("duplicate url " + r.getUrl()).isTrue();
        }
        assertThat(all.size()).isGreaterThanOrEqualTo(120);
    }

    @Test
    void notesHaveContentAndUniqueTitles() {
        Set<String> titles = new HashSet<>();
        for (Note n : NoteSeedData.all()) {
            assertThat(n.getContent()).as(n.getTitle()).isNotBlank();
            assertThat(titles.add(n.getTitle().toLowerCase(Locale.ROOT))).as("duplicate note " + n.getTitle()).isTrue();
        }
        assertThat(titles.size()).isGreaterThanOrEqualTo(30);
    }
}
