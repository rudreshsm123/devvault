package com.devvault.service;

import com.devvault.entity.Difficulty;
import com.devvault.entity.Note;
import com.devvault.entity.RevisionStatus;
import com.devvault.exception.NotFoundException;
import com.devvault.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public List<Note> search(String keyword, String language, String topic,
                              Difficulty difficulty, RevisionStatus revisionStatus) {
        return noteRepository.search(blankToNull(keyword), blankToNull(language), blankToNull(topic),
                difficulty, revisionStatus);
    }

    public List<Note> getNotesNeedingRevision() {
        return noteRepository.findByRevisionStatus(RevisionStatus.NEEDS_REVISION);
    }

    public List<Note> getRecentNotes() {
        return noteRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public long count() {
        return noteRepository.count();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note", id));
    }

    public Note updateNote(Long id, Note updatedNote) {
        Note existingNote = getNoteById(id);
        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        existingNote.setProgrammingLanguage(updatedNote.getProgrammingLanguage());
        existingNote.setTopic(updatedNote.getTopic());
        existingNote.setDifficulty(updatedNote.getDifficulty());
        existingNote.setTags(updatedNote.getTags());
        existingNote.setImportant(updatedNote.isImportant());
        existingNote.setRevisionStatus(updatedNote.getRevisionStatus());
        return noteRepository.save(existingNote);
    }

    public void deleteNote(Long id) {
        Note note = getNoteById(id);
        noteRepository.delete(note);
    }
}
