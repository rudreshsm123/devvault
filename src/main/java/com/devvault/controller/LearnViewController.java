package com.devvault.controller;

import com.devvault.entity.Lesson;
import com.devvault.service.LessonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/learn")
public class LearnViewController {

    private final LessonService lessonService;

    public LearnViewController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping
    public String chapters(Model model) {
        Map<Integer, List<Lesson>> byChapter = new LinkedHashMap<>();
        for (Lesson l : lessonService.getAllInOrder()) {
            byChapter.computeIfAbsent(l.getChapterNo(), k -> new ArrayList<>()).add(l);
        }
        model.addAttribute("chapters", byChapter);
        model.addAttribute("lessonCount", lessonService.count());
        return "learn/index";
    }

    @GetMapping("/{id}")
    public String lesson(@PathVariable Long id, Model model) {
        Lesson lesson = lessonService.getById(id);
        model.addAttribute("lesson", lesson);
        model.addAttribute("prev", lessonService.previous(lesson).orElse(null));
        model.addAttribute("next", lessonService.next(lesson).orElse(null));
        return "learn/lesson";
    }
}
