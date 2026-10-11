package com.devvault.repository;

import com.devvault.entity.CodeChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeChallengeRepository extends JpaRepository<CodeChallenge, Long> {

    Optional<CodeChallenge> findByProblemId(Long problemId);
}
