package com.devvault.controller;

import com.devvault.entity.Lesson;
import com.devvault.exception.NotFoundException;
import com.devvault.service.LessonService;
import com.devvault.service.ProblemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Certificates are practice records issued by this app: they are earned from the learner's own progress
 * (lessons finished, problems solved) and can be printed or saved as a PDF from the browser.
 * The progress itself lives in the browser or in the signed-in account, so the page's script checks eligibility.
 */
@Controller
public class CertificateController {

    /** Number of solved problems that earn the three problem-solver certificates. */
    static final int[] SOLVER_MILESTONES = {25, 100, 250};

    public record ChapterInfo(int number, String title, String lessonIds, int lessonCount) {
    }

    private final LessonService lessonService;
    private final ProblemService problemService;

    public CertificateController(LessonService lessonService, ProblemService problemService) {
        this.lessonService = lessonService;
        this.problemService = problemService;
    }

    @GetMapping("/certificates")
    public String certificates(Model model) {
        List<ChapterInfo> chapters = chapters();
        model.addAttribute("chapters", chapters);
        model.addAttribute("allLessonIds", chapters.stream().map(ChapterInfo::lessonIds)
                .collect(Collectors.joining(",")));
        model.addAttribute("lessonCount", lessonService.count());
        model.addAttribute("problemCount", problemService.count());
        model.addAttribute("milestones", SOLVER_MILESTONES);
        return "certificates";
    }

    /** One printable certificate: type is chapter-N, path, or solver-N (N = 25, 100 or 250). */
    @GetMapping("/certificate")
    public String certificate(@RequestParam String type, Model model) {
        List<ChapterInfo> chapters = chapters();
        model.addAttribute("type", type);
        model.addAttribute("requiredLessonIds", "");
        model.addAttribute("requiredProblems", 0);

        if (type.equals("path")) {
            model.addAttribute("title", "DevVault Learning Path");
            model.addAttribute("achievement", "completed all " + lessonService.count()
                    + " lessons of the DevVault Learning Path, from first program to interview playbook");
            model.addAttribute("requiredLessonIds", chapters.stream().map(ChapterInfo::lessonIds)
                    .collect(Collectors.joining(",")));
        } else if (type.startsWith("chapter-")) {
            int number = parse(type.substring("chapter-".length()));
            ChapterInfo chapter = chapters.stream().filter(c -> c.number() == number).findFirst()
                    .orElseThrow(() -> new NotFoundException("Certificate", (long) number));
            model.addAttribute("title", "Chapter " + chapter.number() + ": " + chapter.title());
            model.addAttribute("achievement", "completed all " + chapter.lessonCount()
                    + " lessons of Chapter " + chapter.number() + " - " + chapter.title());
            model.addAttribute("requiredLessonIds", chapter.lessonIds());
        } else if (type.startsWith("solver-")) {
            int target = parse(type.substring("solver-".length()));
            boolean known = false;
            for (int m : SOLVER_MILESTONES) {
                known |= m == target;
            }
            if (!known) {
                throw new NotFoundException("Certificate", (long) target);
            }
            model.addAttribute("title", target + " Problems Solved");
            model.addAttribute("achievement", "solved " + target + " coding problems on DevVault");
            model.addAttribute("requiredProblems", target);
        } else {
            throw new NotFoundException("Certificate", 0L);
        }
        return "certificate";
    }

    private List<ChapterInfo> chapters() {
        Map<Integer, List<Lesson>> byChapter = new LinkedHashMap<>();
        for (Lesson l : lessonService.getAllInOrder()) {
            byChapter.computeIfAbsent(l.getChapterNo(), k -> new ArrayList<>()).add(l);
        }
        List<ChapterInfo> out = new ArrayList<>();
        byChapter.forEach((no, lessons) -> out.add(new ChapterInfo(no, lessons.get(0).getChapterTitle(),
                lessons.stream().map(l -> String.valueOf(l.getId())).collect(Collectors.joining(",")),
                lessons.size())));
        return out;
    }

    private static int parse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Certificate", 0L);
        }
    }
}
