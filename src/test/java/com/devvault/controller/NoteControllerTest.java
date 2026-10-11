package com.devvault.controller;

import com.devvault.entity.Note;
import com.devvault.exception.NotFoundException;
import com.devvault.service.NoteService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoteController.class)
@Import(SecurityConfig.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NoteService noteService;

    @Test
    void getAllNotes_returnsJsonArray() throws Exception {
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Java Streams");
        note.setContent("filter/map/collect");

        when(noteService.getAllNotes()).thenReturn(List.of(note));

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Streams"));
    }

    @Test
    void getNoteById_whenMissing_returns404WithMessage() throws Exception {
        when(noteService.getNoteById(99L)).thenThrow(new NotFoundException("Note", 99L));

        mockMvc.perform(get("/api/notes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Note not found with id: 99"));
    }

    @Test
    void createNote_withBlankTitle_returns400WithFieldError() throws Exception {
        Note invalidNote = new Note();
        invalidNote.setContent("has content but no title");

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidNote)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title is required"));
    }

    @Test
    void createNote_withValidBody_returns201WithLocationHeader() throws Exception {
        Note request = new Note();
        request.setTitle("New Note");
        request.setContent("Some content");

        Note saved = new Note();
        saved.setId(5L);
        saved.setTitle("New Note");
        saved.setContent("Some content");

        when(noteService.createNote(any(Note.class))).thenReturn(saved);

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/notes/5"));
    }
}
