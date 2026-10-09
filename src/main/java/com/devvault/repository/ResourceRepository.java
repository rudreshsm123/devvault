package com.devvault.repository;

import com.devvault.entity.LearningResource;
import com.devvault.entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResourceRepository extends JpaRepository<LearningResource, Long> {

    @Query("""
            SELECT r FROM LearningResource r
            WHERE (:keyword IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:topic IS NULL OR r.topic = :topic)
              AND (:resourceType IS NULL OR r.resourceType = :resourceType)
            ORDER BY r.createdAt DESC
            """)
    List<LearningResource> search(@Param("keyword") String keyword,
                                   @Param("topic") String topic,
                                   @Param("resourceType") ResourceType resourceType);
}
