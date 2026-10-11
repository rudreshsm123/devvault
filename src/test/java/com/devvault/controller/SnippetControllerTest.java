package com.devvault.controller;

import com.devvault.entity.CodeSnippet;
import com.devvault.exception.NotFoundException;
import com.devvault.service.SnippetService;
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

@WebMvcTest(SnippetController.class)
@Import(SecurityConfig.class)
class SnippetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SnippetService snippetService;

    private CodeSnippet snippet(Long id, String title, String code) {
        CodeSnippet snippet = new CodeSnippet();
        snippet.setId(id);
        snippet.setTitle(title);
        snippet.setCode(code);
        return snippet;
    }

    @Test
    void getAllSnippets_returnsJsonArray() throws Exception {
        when(snippetService.getAllSnippets()).thenReturn(List.of(snippet(1L, "Hello", "System.out.println();")));

        mockMvc.perform(get("/api/snippets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Hello"));
    }

    @Test
    void getSnippetById_whenMissing_returns404WithMessage() throws Exception {
        when(snippetService.getSnippetById(99L)).thenThrow(new NotFoundException("Code snippet", 99L));

        mockMvc.perform(get("/api/snippets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Code snippet not found with id: 99"));
    }

    @Test
    void createSnippet_withBlankTitle_returns400WithFieldError() throws Exception {
        CodeSnippet invalid = new CodeSnippet();
        invalid.setCode("int x = 1;");

        mockMvc.perform(post("/api/snippets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title is required"));
    }

    @Test
    void createSnippet_withBlankCode_returns400WithFieldError() throws Exception {
        CodeSnippet invalid = new CodeSnippet();
        invalid.setTitle("No code");

        mockMvc.perform(post("/api/snippets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").value("Code is required"));
    }

    @Test
    void createSnippet_withValidBody_returns201WithLocationHeader() throws Exception {
        when(snippetService.createSnippet(any(CodeSnippet.class)))
                .thenReturn(snippet(5L, "Hello", "System.out.println();"));

        mockMvc.perform(post("/api/snippets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(snippet(null, "Hello", "System.out.println();"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/snippets/5"));
    }

    @Test
    void updateSnippet_withValidBody_returnsUpdatedSnippet() throws Exception {
        when(snippetService.updateSnippet(any(Long.class), any(CodeSnippet.class)))
                .thenReturn(snippet(1L, "Hello v2", "System.out.println(1);"));

        mockMvc.perform(put("/api/snippets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(snippet(null, "Hello v2", "System.out.println(1);"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Hello v2"));
    }

    @Test
    void deleteSnippet_returns204() throws Exception {
        mockMvc.perform(delete("/api/snippets/1"))
                .andExpect(status().isNoContent());

        verify(snippetService).deleteSnippet(1L);
    }
}
