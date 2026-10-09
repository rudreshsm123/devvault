package com.devvault.service;

import com.devvault.entity.Note;
import com.devvault.exception.NotFoundException;
import com.devvault.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private Note sampleNote;

    @BeforeEach
    void setUp() {
        sampleNote = new Note();
        sampleNote.setId(1L);
        sampleNote.setTitle("Java Streams");
        sampleNote.setContent("filter/map/collect");
    }

    @Test
    void createNote_savesAndReturnsTheNote() {
        when(noteRepository.save(sampleNote)).thenReturn(sampleNote);

        Note result = noteService.createNote(sampleNote);

        assertThat(result.getTitle()).isEqualTo("Java Streams");
        verify(noteRepository).save(sampleNote);
    }

    @Test
    void getNoteById_whenFound_returnsNote() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));

        Note result = noteService.getNoteById(1L);

        assertThat(result).isEqualTo(sampleNote);
    }

    @Test
    void getNoteById_whenMissing_throwsNotFoundException() {
        when(noteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.getNoteById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteNote_whenFound_deletesIt() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));

        noteService.deleteNote(1L);

        verify(noteRepository).delete(sampleNote);
    }

    @Test
    void updateNote_overwritesFieldsAndSaves() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));
        when(noteRepository.save(sampleNote)).thenReturn(sampleNote);

        Note changes = new Note();
        changes.setTitle("Updated Title");
        changes.setContent("Updated content");

        Note result = noteService.updateNote(1L, changes);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getContent()).isEqualTo("Updated content");
    }
}
