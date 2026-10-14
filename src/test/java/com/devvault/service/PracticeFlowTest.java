package com.devvault.service;

import com.devvault.entity.CodingProblem;
import com.devvault.entity.RevisionStatus;
import com.devvault.entity.UserProgress;
import com.devvault.repository.AttemptLogRepository;
import com.devvault.repository.ProblemRepository;
import com.devvault.repository.UserProgressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The attempt, right/wrong and "mark as done" rules that decide what a learner sees as progress. */
@ExtendWith(MockitoExtension.class)
class PracticeFlowTest {

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private UserProgressRepository progressRepository;

    @Mock
    private AttemptLogRepository attemptLogRepository;

    private ProgressService progressService() {
        return new ProgressService(progressRepository, attemptLogRepository);
    }

    @Test
    void submittingAnAttemptMovesNotStartedToLearning() {
        CodingProblem problem = new CodingProblem();
        problem.setId(1L);
        problem.setPracticeStatus(RevisionStatus.NOT_STARTED);
        when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));
        when(problemRepository.save(problem)).thenReturn(problem);

        CodingProblem saved = new ProblemService(problemRepository).saveAttempt(1L, "my idea");

        assertThat(saved.getPracticeStatus()).isEqualTo(RevisionStatus.LEARNING);
    }

    @Test
    void submittingAnAttemptNeverDowngradesAnAlreadyLearnedProblem() {
        CodingProblem problem = new CodingProblem();
        problem.setId(1L);
        problem.setPracticeStatus(RevisionStatus.LEARNED);
        when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));
        when(problemRepository.save(problem)).thenReturn(problem);

        CodingProblem saved = new ProblemService(problemRepository).saveAttempt(1L, "second try");

        assertThat(saved.getPracticeStatus()).isEqualTo(RevisionStatus.LEARNED);
    }

    @Test
    void setResult_createsAWrongEntryForAnewProblem() {
        when(progressRepository.findByUserIdAndKindAndItemId(1L, "problem", 5L)).thenReturn(Optional.empty());

        progressService().setResult(1L, "problem", 5L, "Two Sum", "/problems/5", "wrong");

        ArgumentCaptor<UserProgress> saved = ArgumentCaptor.forClass(UserProgress.class);
        verify(progressRepository).save(saved.capture());
        assertThat(saved.getValue().getResult()).isEqualTo("wrong");
    }

    @Test
    void setResult_replacesAnEarlierResult() {
        UserProgress existing = new UserProgress(1L, "problem", 5L, "Two Sum", "/problems/5", 1L, "wrong");
        when(progressRepository.findByUserIdAndKindAndItemId(1L, "problem", 5L)).thenReturn(Optional.of(existing));

        progressService().setResult(1L, "problem", 5L, "Two Sum", "/problems/5", "right");

        assertThat(existing.getResult()).isEqualTo("right");
        verify(progressRepository).save(existing);
    }

    @Test
    void setResult_rejectsAnythingExceptRightOrWrong() {
        assertThatThrownBy(() -> progressService()
                .setResult(1L, "problem", 5L, "Two Sum", "/problems/5", "maybe"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(progressRepository, never()).save(any());
    }

    @Test
    void markAsDoneAfterAWrongAttemptTurnsItIntoSolvedInsteadOfDeletingIt() {
        UserProgress wrong = new UserProgress(1L, "problem", 5L, "Two Sum", "/problems/5", 1L, "wrong");
        when(progressRepository.findByUserIdAndKindAndItemId(1L, "problem", 5L)).thenReturn(Optional.of(wrong));

        boolean done = progressService().toggle(1L, "problem", 5L, "Two Sum", "/problems/5");

        assertThat(done).isTrue();
        assertThat(wrong.getResult()).isEqualTo("right");
        verify(progressRepository, never()).delete(any());
    }
}
