package com.devvault.controller;

import com.devvault.entity.CodingProblem;
import com.devvault.exception.NotFoundException;
import com.devvault.service.ProblemService;
import tools.jackson.databind.ObjectMapper;
import com.devvault.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProblemController.class)
@Import(SecurityConfig.class)
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProblemService problemService;

    private CodingProblem problem(Long id, String title, String description) {
        CodingProblem problem = new CodingProblem();
        problem.setId(id);
        problem.setTitle(title);
        problem.setDescription(description);
        return problem;
    }

    @Test
    void getAllProblems_returnsJsonArray() throws Exception {
        when(problemService.getAllProblems()).thenReturn(List.of(problem(1L, "Two Sum", "Find two numbers")));

        mockMvc.perform(get("/api/problems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Two Sum"));
    }

    @Test
    void getProblemById_whenMissing_returns404WithMessage() throws Exception {
        when(problemService.getProblemById(99L)).thenThrow(new NotFoundException("Coding problem", 99L));

        mockMvc.perform(get("/api/problems/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coding problem not found with id: 99"));
    }

    @Test
    void createProblem_withBlankTitle_returns400WithFieldError() throws Exception {
        CodingProblem invalid = new CodingProblem();
        invalid.setDescription("has a description but no title");

        mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title is required"));
    }

    @Test
    void createProblem_withBlankDescription_returns400WithFieldError() throws Exception {
        CodingProblem invalid = new CodingProblem();
        invalid.setTitle("No description");

        mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").value("Description is required"));
    }

    @Test
    void createProblem_withValidBody_returns201WithLocationHeader() throws Exception {
        when(problemService.createProblem(any(CodingProblem.class)))
                .thenReturn(problem(5L, "Two Sum", "Find two numbers"));

        mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(problem(null, "Two Sum", "Find two numbers"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/problems/5"));
    }

    @Test
    void updateProblem_withValidBody_returnsUpdatedProblem() throws Exception {
        when(problemService.updateProblem(any(Long.class), any(CodingProblem.class)))
                .thenReturn(problem(1L, "Two Sum II", "Sorted input"));

        mockMvc.perform(put("/api/problems/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(problem(null, "Two Sum II", "Sorted input"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Two Sum II"));
    }

    @Test
    void deleteProblem_returns204() throws Exception {
        mockMvc.perform(delete("/api/problems/1"))
                .andExpect(status().isNoContent());

        verify(problemService).deleteProblem(1L);
    }
}
