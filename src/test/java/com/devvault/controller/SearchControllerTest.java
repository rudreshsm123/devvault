package com.devvault.controller;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;
import com.devvault.entity.Lesson;
import com.devvault.service.LessonService;
import com.devvault.service.NoteService;
import com.devvault.service.ProblemService;
import com.devvault.service.ResourceService;
import com.devvault.service.SnippetService;
import com.devvault.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(SearchController.class)
@Import(SecurityConfig.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProblemService problemService;
    @MockitoBean
    private LessonService lessonService;
    @MockitoBean
    private SnippetService snippetService;
    @MockitoBean
    private NoteService noteService;
    @MockitoBean
    private ResourceService resourceService;

    @Test
    void search_groupsResultsFromEveryArea() throws Exception {
        CodingProblem problem = new CodingProblem();
        problem.setId(7L);
        problem.setTitle("Two Sum");
        problem.setDifficulty(Difficulty.EASY);
        problem.setTopic("Hash Map");
        Lesson lesson = new Lesson();
        lesson.setId(3L);
        lesson.setTitle("Map and HashMap");
        lesson.setChapterNo(3);
        lesson.setChapterTitle("Collections");

        when(problemService.search(eq("hash"), any(), any(), any())).thenReturn(List.of(problem));
        when(lessonService.search("hash")).thenReturn(List.of(lesson));
        when(snippetService.search(eq("hash"), any())).thenReturn(List.of());
        when(noteService.search(eq("hash"), any(), any(), any(), any())).thenReturn(List.of());
        when(resourceService.search(eq("hash"), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/search").param("q", "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attribute("total", 2))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Two Sum")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Map and HashMap")));
    }

    @Test
    void search_withOneLetterDoesNotQueryAnything() throws Exception {
        mockMvc.perform(get("/search").param("q", "a"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("tooShort", true))
                .andExpect(model().attribute("total", 0));

        verify(problemService, never()).search(any(), any(), any(), any());
        verify(lessonService, never()).search(any());
    }

    @Test
    void search_withoutQueryShowsTheEmptyPage() throws Exception {
        mockMvc.perform(get("/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attribute("total", 0));
    }
}
