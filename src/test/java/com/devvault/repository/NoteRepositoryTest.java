package com.devvault.repository;

import com.devvault.entity.Difficulty;
import com.devvault.entity.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("local")
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Test
    void search_filtersByKeywordAcrossTitleAndContent() {
        Note note = new Note();
        note.setTitle("Binary Search Deep Dive");
        note.setContent("Covers log(n) search on sorted arrays");
        note.setDifficulty(Difficulty.MEDIUM);
        noteRepository.save(note);

        List<Note> matching = noteRepository.search("binary search", null, null, null, null);
        List<Note> notMatching = noteRepository.search("kubernetes", null, null, null, null);

        assertThat(matching).extracting(Note::getTitle).contains("Binary Search Deep Dive");
        assertThat(notMatching).extracting(Note::getTitle).doesNotContain("Binary Search Deep Dive");
    }

    @Test
    void search_filtersByDifficulty() {
        Note easyNote = new Note();
        easyNote.setTitle("Easy Note For Repository Test");
        easyNote.setContent("content");
        easyNote.setDifficulty(Difficulty.EASY);
        noteRepository.save(easyNote);

        Note hardNote = new Note();
        hardNote.setTitle("Hard Note For Repository Test");
        hardNote.setContent("content");
        hardNote.setDifficulty(Difficulty.HARD);
        noteRepository.save(hardNote);

        List<Note> easyResults = noteRepository.search(null, null, null, Difficulty.EASY, null);

        assertThat(easyResults).extracting(Note::getTitle).contains("Easy Note For Repository Test");
        assertThat(easyResults).extracting(Note::getTitle).doesNotContain("Hard Note For Repository Test");
    }
}
