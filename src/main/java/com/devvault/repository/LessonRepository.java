package com.devvault.repository;

import com.devvault.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findAllByOrderByChapterNoAscLessonNoAsc();

    @Query("""
            SELECT l FROM Lesson l
            WHERE LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(l.concept) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(l.whyLearn) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(l.chapterTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY l.chapterNo, l.lessonNo
            """)
    List<Lesson> search(@Param("keyword") String keyword);
}
