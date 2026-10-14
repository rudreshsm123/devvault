package com.devvault.service;

import com.devvault.entity.UserProgress;
import com.devvault.entity.AttemptLog;
import com.devvault.repository.AttemptLogRepository;
import com.devvault.repository.UserProgressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private UserProgressRepository repository;

    @Mock
    private AttemptLogRepository attempts;

    @InjectMocks
    private ProgressService service;

    @Test
    void toggle_marksDoneWhenNotYetStored() {
        when(repository.findByUserIdAndKindAndItemId(1L, "problem", 5L)).thenReturn(Optional.empty());

        boolean done = service.toggle(1L, "problem", 5L, "Two Sum", "/problems/5");

        assertThat(done).isTrue();
        verify(repository).save(any(UserProgress.class));
    }

    @Test
    void toggle_removesWhenAlreadyDone() {
        UserProgress existing = new UserProgress(1L, "lesson", 3L, "Loops", "/learn/3", 1L);
        when(repository.findByUserIdAndKindAndItemId(1L, "lesson", 3L)).thenReturn(Optional.of(existing));

        boolean done = service.toggle(1L, "lesson", 3L, "Loops", "/learn/3");

        assertThat(done).isFalse();
        verify(repository).delete(existing);
    }

    @Test
    void toggle_rejectsBadKindAndExternalLinks() {
        assertThatThrownBy(() -> service.toggle(1L, "note", 1L, "x", "/notes/1"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.toggle(1L, "problem", 1L, "x", "https://evil.example"))
                .hasMessageContaining("path on this site");
        assertThatThrownBy(() -> service.toggle(1L, "problem", 1L, "x", "//evil.example"))
                .hasMessageContaining("path on this site");
        assertThatThrownBy(() -> service.toggle(1L, "problem", 1L, "x", "javascript:alert(1)"))
                .hasMessageContaining("path on this site");
        assertThatThrownBy(() -> service.toggle(1L, "problem", 0L, "x", "/problems/0"))
                .hasMessageContaining("positive");
        verify(repository, never()).save(any());
    }

    @Test
    void merge_addsOnlyItemsTheUserDoesNotHaveYet() {
        UserProgress fresh = new UserProgress(null, "problem", 1L, "A", "/problems/1", 100L);
        UserProgress known = new UserProgress(null, "problem", 2L, "B", "/problems/2", 200L);
        when(repository.findByUserIdAndKindAndItemId(9L, "problem", 1L)).thenReturn(Optional.empty());
        when(repository.findByUserIdAndKindAndItemId(9L, "problem", 2L))
                .thenReturn(Optional.of(new UserProgress(9L, "problem", 2L, "B", "/problems/2", 1L)));

        int added = service.merge(9L, List.of(fresh, known));

        assertThat(added).isEqualTo(1);
        verify(repository).save(any(UserProgress.class));
    }

    @Test
    void setResult_wrongSchedulesReviewTomorrowAndLogsTheAttempt() {
        when(repository.findByUserIdAndKindAndItemId(1L, "drill", 7L)).thenReturn(Optional.empty());
        long before = System.currentTimeMillis();

        service.setResult(1L, "drill", 7L, "Drill 7", "/drills/7", "wrong");

        org.mockito.ArgumentCaptor<UserProgress> saved = org.mockito.ArgumentCaptor.forClass(UserProgress.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getBox()).isZero();
        assertThat(saved.getValue().getDueAt()).isGreaterThanOrEqualTo(before + java.util.concurrent.TimeUnit.DAYS.toMillis(1));
        org.mockito.ArgumentCaptor<AttemptLog> logged = org.mockito.ArgumentCaptor.forClass(AttemptLog.class);
        verify(attempts).save(logged.capture());
        assertThat(logged.getValue().isCorrect()).isFalse();
    }

    @Test
    void setResult_rightAnswersMoveTheItemUpTheBoxes() {
        UserProgress existing = new UserProgress(1L, "drill", 7L, "Drill 7", "/drills/7", 1L, "wrong");
        existing.schedule(2, 0L);
        when(repository.findByUserIdAndKindAndItemId(1L, "drill", 7L)).thenReturn(Optional.of(existing));

        service.setResult(1L, "drill", 7L, "Drill 7", "/drills/7", "right");

        assertThat(existing.getBox()).isEqualTo(3);
        assertThat(existing.getResult()).isEqualTo("right");
    }
}
