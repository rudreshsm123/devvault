package com.devvault.repository;

import com.devvault.entity.CodeSnippet;
import com.devvault.entity.SnippetLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SnippetRepository extends JpaRepository<CodeSnippet, Long> {

    @Query("""
            SELECT s FROM CodeSnippet s
            WHERE (:keyword IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:language IS NULL OR s.language = :language)
            ORDER BY s.createdAt DESC
            """)
    List<CodeSnippet> search(@Param("keyword") String keyword, @Param("language") SnippetLanguage language);
}
