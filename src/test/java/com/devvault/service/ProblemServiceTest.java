package com.devvault.service;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.Difficulty;
import com.devvault.entity.RevisionStatus;
import com.devvault.exception.NotFoundException;
import com.devvault.repository.ProblemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @InjectMocks
    private ProblemService problemService;

    private CodingProblem sampleProblem;

    @BeforeEach
    void setUp() {
        sampleProblem = new CodingProblem();
        sampleProblem.setId(1L);
        sampleProblem.setTitle("Two Sum");
        sampleProblem.setDescription("Find two numbers that add up to a target");
    }

    @Test
    void createProblem_savesAndReturnsTheProblem() {
        when(problemRepository.save(sampleProblem)).thenReturn(sampleProblem);

        CodingProblem result = problemService.createProblem(sampleProblem);

        assertThat(result.getTitle()).isEqualTo("Two Sum");
        verify(problemRepository).save(sampleProblem);
    }

    @Test
    void getProblemById_whenFound_returnsProblem() {
        when(problemRepository.findById(1L)).thenReturn(Optional.of(sampleProblem));

        assertThat(problemService.getProblemById(1L)).isEqualTo(sampleProblem);
    }

    @Test
    void getProblemById_whenMissing_throwsNotFoundException() {
        when(problemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> problemService.getProblemById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateProblem_overwritesFieldsAndSaves() {
        when(problemRepository.findById(1L)).thenReturn(Optional.of(sampleProblem));
        when(problemRepository.save(sampleProblem)).thenReturn(sampleProblem);

        CodingProblem changes = new CodingProblem();
        changes.setTitle("Two Sum II");
        changes.setDescription("Sorted input");
        changes.setDifficulty(Difficulty.MEDIUM);
        changes.setPracticeStatus(RevisionStatus.NEEDS_REVISION);

        CodingProblem result = problemService.updateProblem(1L, changes);

        assertThat(result.getTitle()).isEqualTo("Two Sum II");
        assertThat(result.getDescription()).isEqualTo("Sorted input");
        assertThat(result.getDifficulty()).isEqualTo(Difficulty.MEDIUM);
        assertThat(result.getPracticeStatus()).isEqualTo(RevisionStatus.NEEDS_REVISION);
    }

    @Test
    void deleteProblem_whenFound_deletesIt() {
        when(problemRepository.findById(1L)).thenReturn(Optional.of(sampleProblem));

        problemService.deleteProblem(1L);

        verify(problemRepository).delete(sampleProblem);
    }

    @Test
    void search_convertsBlankStringsToNull() {
        when(problemRepository.search(null, null, Difficulty.EASY, null)).thenReturn(List.of(sampleProblem));

        List<CodingProblem> result = problemService.search("  ", "", Difficulty.EASY, null);

        assertThat(result).containsExactly(sampleProblem);
    }

    @Test
    void getProblemsNeedingRevision_queriesByNeedsRevisionStatus() {
        when(problemRepository.findByPracticeStatus(RevisionStatus.NEEDS_REVISION))
                .thenReturn(List.of(sampleProblem));

        assertThat(problemService.getProblemsNeedingRevision()).containsExactly(sampleProblem);
    }

    @Test
    void saveAttempt_storesAttemptOnProblem() {
        when(problemRepository.findById(1L)).thenReturn(Optional.of(sampleProblem));
        when(problemRepository.save(sampleProblem)).thenReturn(sampleProblem);

        CodingProblem result = problemService.saveAttempt(1L, "my solution");

        assertThat(result.getUserAttempt()).isEqualTo("my solution");
    }

    @Test
    void clearAttempt_removesAttemptFromProblem() {
        sampleProblem.setUserAttempt("old attempt");
        when(problemRepository.findById(1L)).thenReturn(Optional.of(sampleProblem));
        when(problemRepository.save(sampleProblem)).thenReturn(sampleProblem);

        CodingProblem result = problemService.clearAttempt(1L);

        assertThat(result.getUserAttempt()).isNull();
    }
}
