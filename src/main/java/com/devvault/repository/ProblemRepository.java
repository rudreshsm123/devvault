package com.devvault.repository;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;
import com.devvault.entity.RevisionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<CodingProblem, Long> {

    boolean existsByTitleIgnoreCase(String title);

    @Query("""
            SELECT p FROM CodingProblem p
            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:topic IS NULL OR p.topic = :topic)
              AND (:difficulty IS NULL OR p.difficulty = :difficulty)
              AND (:practiceStatus IS NULL OR p.practiceStatus = :practiceStatus)
            ORDER BY p.createdAt DESC
            """)
    List<CodingProblem> search(@Param("keyword") String keyword,
                                @Param("topic") String topic,
                                @Param("difficulty") Difficulty difficulty,
                                @Param("practiceStatus") RevisionStatus practiceStatus);

    List<CodingProblem> findByPracticeStatus(RevisionStatus practiceStatus);
}
