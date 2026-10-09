package com.devvault.repository;

import com.devvault.entity.Difficulty;
import com.devvault.entity.Note;
import com.devvault.entity.RevisionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("""
            SELECT n FROM Note n
            WHERE (:keyword IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:language IS NULL OR n.programmingLanguage = :language)
              AND (:topic IS NULL OR n.topic = :topic)
              AND (:difficulty IS NULL OR n.difficulty = :difficulty)
              AND (:revisionStatus IS NULL OR n.revisionStatus = :revisionStatus)
            ORDER BY n.createdAt DESC
            """)
    List<Note> search(@Param("keyword") String keyword,
                       @Param("language") String language,
                       @Param("topic") String topic,
                       @Param("difficulty") Difficulty difficulty,
                       @Param("revisionStatus") RevisionStatus revisionStatus);

    List<Note> findByRevisionStatus(RevisionStatus revisionStatus);

    List<Note> findTop5ByOrderByCreatedAtDesc();
}
