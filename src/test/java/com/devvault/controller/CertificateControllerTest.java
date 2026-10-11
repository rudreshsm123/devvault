package com.devvault.controller;

import com.devvault.config.CurrentUserAdvice;
import com.devvault.config.SecurityConfig;
import com.devvault.entity.Lesson;
import com.devvault.exception.GlobalExceptionHandler;
import com.devvault.service.LessonService;
import com.devvault.service.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CertificateController.class)
@Import({SecurityConfig.class, CurrentUserAdvice.class, GlobalExceptionHandler.class})
class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LessonService lessonService;

    @MockitoBean
    private ProblemService problemService;

    private static Lesson lesson(long id, int chapter, String chapterTitle, int number) {
        Lesson l = new Lesson();
        l.setId(id);
        l.setChapterNo(chapter);
        l.setChapterTitle(chapterTitle);
        l.setLessonNo(number);
        l.setTitle("Lesson " + id);
        return l;
    }

    private void twoChapters() {
        when(lessonService.getAllInOrder()).thenReturn(List.of(
                lesson(10, 1, "Foundations", 1), lesson(11, 1, "Foundations", 2), lesson(20, 2, "Objects", 1)));
        when(lessonService.count()).thenReturn(3L);
        when(problemService.count()).thenReturn(331L);
    }

    @Test
    void certificatesPageListsEveryChapterWithItsLessonIds() throws Exception {
        twoChapters();

        mockMvc.perform(get("/certificates"))
                .andExpect(status().isOk())
                .andExpect(view().name("certificates"))
                .andExpect(content().string(containsString("data-lessons=\"10,11\"")))
                .andExpect(content().string(containsString("data-lessons=\"20\"")))
                .andExpect(content().string(containsString("solver-100")));
    }

    @Test
    void chapterCertificateKnowsWhichLessonsItRequires() throws Exception {
        twoChapters();

        mockMvc.perform(get("/certificate").param("type", "chapter-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("certificate"))
                .andExpect(model().attribute("requiredLessonIds", "10,11"))
                .andExpect(content().string(containsString("Chapter 1: Foundations")));
    }

    @Test
    void pathCertificateRequiresEveryLesson() throws Exception {
        twoChapters();

        mockMvc.perform(get("/certificate").param("type", "path"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("requiredLessonIds", "10,11,20"));
    }

    @Test
    void solverCertificateRequiresTheMilestoneNumberOfProblems() throws Exception {
        twoChapters();

        mockMvc.perform(get("/certificate").param("type", "solver-25"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("requiredProblems", 25));
    }

    @Test
    void unknownCertificateTypesAreNotFound() throws Exception {
        twoChapters();

        mockMvc.perform(get("/certificate").param("type", "bogus")).andExpect(status().isNotFound());
        mockMvc.perform(get("/certificate").param("type", "chapter-9")).andExpect(status().isNotFound());
        mockMvc.perform(get("/certificate").param("type", "solver-7")).andExpect(status().isNotFound());
        mockMvc.perform(get("/certificate").param("type", "chapter-x")).andExpect(status().isNotFound());
    }
}
