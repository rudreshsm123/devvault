package com.devvault.service;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;
import com.devvault.entity.RevisionStatus;
import com.devvault.exception.NotFoundException;
import com.devvault.repository.ProblemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    public CodingProblem createProblem(CodingProblem problem) {
        return problemRepository.save(problem);
    }

    public List<CodingProblem> getAllProblems() {
        return problemRepository.findAll();
    }

    public List<CodingProblem> search(String keyword, String topic, Difficulty difficulty,
                                       RevisionStatus practiceStatus) {
        return problemRepository.search(blankToNull(keyword), blankToNull(topic), difficulty, practiceStatus);
    }

    public List<CodingProblem> getProblemsNeedingRevision() {
        return problemRepository.findByPracticeStatus(RevisionStatus.NEEDS_REVISION);
    }

    public long count() {
        return problemRepository.count();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    public CodingProblem getProblemById(Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coding problem", id));
    }

    public CodingProblem updateProblem(Long id, CodingProblem updated) {
        CodingProblem existing = getProblemById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setTopic(updated.getTopic());
        existing.setDifficulty(updated.getDifficulty());
        existing.setSolutionCode(updated.getSolutionCode());
        existing.setExplanation(updated.getExplanation());
        existing.setTimeComplexity(updated.getTimeComplexity());
        existing.setSpaceComplexity(updated.getSpaceComplexity());
        existing.setTags(updated.getTags());
        existing.setPatternHint(updated.getPatternHint());
        existing.setCommonMistakes(updated.getCommonMistakes());
        existing.setCompanyTags(updated.getCompanyTags());
        existing.setLineByLine(updated.getLineByLine());
        existing.setInterviewTips(updated.getInterviewTips());
        existing.setPracticeStatus(updated.getPracticeStatus());
        return problemRepository.save(existing);
    }

    public CodingProblem updatePracticeStatus(Long id, RevisionStatus status) {
        CodingProblem problem = getProblemById(id);
        problem.setPracticeStatus(status);
        return problemRepository.save(problem);
    }

    public void deleteProblem(Long id) {
        problemRepository.delete(getProblemById(id));
    }

    public CodingProblem saveAttempt(Long id, String attempt) {
        CodingProblem problem = getProblemById(id);
        problem.setUserAttempt(attempt);
        if (problem.getPracticeStatus() == null || problem.getPracticeStatus() == RevisionStatus.NOT_STARTED) {
            problem.setPracticeStatus(RevisionStatus.LEARNING);        // an attempt means it is no longer "not started"
        }
        return problemRepository.save(problem);
    }

    public CodingProblem clearAttempt(Long id) {
        CodingProblem problem = getProblemById(id);
        problem.setUserAttempt(null);
        return problemRepository.save(problem);
    }
}
