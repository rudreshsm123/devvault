package com.devvault.repository;

import com.devvault.entity.AttemptLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttemptLogRepository extends JpaRepository<AttemptLog, Long> {

    List<AttemptLog> findByUserIdAndAtGreaterThanEqual(Long userId, long at);
}
