package com.devvault.service;

import com.devvault.drill.Drill;
import com.devvault.drill.DrillService;
import com.devvault.entity.InterviewAnswer;
import com.devvault.entity.InterviewSession;
import com.devvault.exception.NotFoundException;
import com.devvault.repository.InterviewAnswerRepository;
import com.devvault.repository.InterviewSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    private static final long START = 1_000_000L;

    @Mock
    private InterviewSessionRepository sessions;
    @Mock
    private InterviewAnswerRepository answers;
    @Mock
    private ProgressService progress;

    private final DrillService drills = new DrillService();
    private final List<InterviewAnswer> savedAnswers = new ArrayList<>();
    private InterviewService service;

    @BeforeEach
    void setUp() {
        service = new InterviewService(sessions, answers, drills, progress);
    }

    private InterviewSession saveWithId(InterviewSession s) {
        ReflectionTestUtils.setField(s, "id", 5L);
        return s;
    }

    @Test
    void aMixedInterviewHasDistinctQuestionsFromManyTopicsAndNinetySecondsEach() {
        when(sessions.save(any())).thenAnswer(i -> saveWithId(i.getArgument(0)));

        service.start(1L, "", "", 10, START, new Random(1));

        ArgumentCaptor<InterviewSession> saved = ArgumentCaptor.forClass(InterviewSession.class);
        verify(sessions).save(saved.capture());
        List<Integer> ids = List.of(saved.getValue().getDrillIds().split(",")).stream().map(Integer::valueOf).toList();
        assertThat(ids).hasSize(10).doesNotHaveDuplicates();
        assertThat(ids.stream().map(id -> drills.find(id).orElseThrow().topic()).distinct().count()).isGreaterThan(4);
        assertThat(saved.getValue().getDurationSeconds()).isEqualTo(900);
        assertThat(saved.getValue().getTopic()).isNull();
    }

    @Test
    void aSingleTopicInterviewOnlyAsksThatTopic() {
        when(sessions.save(any())).thenAnswer(i -> saveWithId(i.getArgument(0)));
        String topic = drills.topics().keySet().iterator().next();

        service.start(1L, topic, null, 5, START, new Random(2));

        ArgumentCaptor<InterviewSession> saved = ArgumentCaptor.forClass(InterviewSession.class);
        verify(sessions).save(saved.capture());
        for (String id : saved.getValue().getDrillIds().split(",")) {
            assertThat(drills.find(Integer.parseInt(id)).orElseThrow().topic()).isEqualTo(topic);
        }
    }

    @Test
    void badChoicesAreRejected() {
        assertThatThrownBy(() -> service.start(1L, null, null, 7, START, new Random())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.start(1L, "No such topic", null, 5, START, new Random()))
                .isInstanceOf(IllegalArgumentException.class);
        verify(sessions, never()).save(any());
    }

    private InterviewSession runningSession(int first, int second) {
        InterviewSession s = new InterviewSession(1L, null, null, START, 180, first + "," + second);
        ReflectionTestUtils.setField(s, "id", 5L);
        lenient().when(sessions.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(s));
        lenient().when(answers.save(any())).thenAnswer(i -> {
            savedAnswers.add(i.getArgument(0));
            return i.getArgument(0);
        });
        lenient().when(answers.findBySessionIdOrderByPosition(5L)).thenAnswer(i -> savedAnswers);
        return s;
    }

    @Test
    void finishingGradesOnTheServerAndRecordsEveryQuestionInTheLearnersProgress() {
        Drill a = drills.find(100).orElseThrow();
        Drill b = drills.find(200).orElseThrow();
        runningSession(100, 200);

        InterviewService.Report report = service.finish(1L, 5L,
                Map.of("100", a.answer(), "200", "definitely-not-the-answer"), START + 60_000);

        assertThat(report.score()).isEqualTo(1);
        assertThat(report.total()).isEqualTo(2);
        assertThat(report.percent()).isEqualTo(50);
        assertThat(report.late()).isFalse();
        assertThat(report.secondsUsed()).isEqualTo(60);
        assertThat(report.lines()).extracting(InterviewService.Line::right).containsExactly(true, false);
        verify(progress).setResult(eq(1L), eq("drill"), eq(100L), any(), eq("/drills/100"), eq("right"));
        verify(progress).setResult(eq(1L), eq("drill"), eq(200L), any(), eq("/drills/200"), eq("wrong"));
    }

    @Test
    void anUnansweredQuestionCountsAsWrong() {
        runningSession(100, 200);

        InterviewService.Report report = service.finish(1L, 5L, Map.of(), START + 10_000);

        assertThat(report.score()).isZero();
        assertThat(report.lines()).allMatch(l -> l.given() == null && !l.right());
    }

    @Test
    void answersThatArriveAfterTheDeadlineAndGraceAreNotCounted() {
        Drill a = drills.find(100).orElseThrow();
        runningSession(100, 200);
        long wellAfterTheDeadline = START + 180_000 + InterviewService.GRACE_MILLIS + 1;

        InterviewService.Report report = service.finish(1L, 5L, Map.of("100", a.answer()), wellAfterTheDeadline);

        assertThat(report.late()).isTrue();
        assertThat(report.score()).isZero();
    }

    @Test
    void answersInsideTheGracePeriodStillCount() {
        Drill a = drills.find(100).orElseThrow();
        runningSession(100, 200);

        InterviewService.Report report = service.finish(1L, 5L, Map.of("100", a.answer()), START + 180_000 + 5_000);

        assertThat(report.late()).isFalse();
        assertThat(report.score()).isEqualTo(1);
    }

    @Test
    void finishingTwiceReturnsTheFirstResultAndGradesNothingAgain() {
        InterviewSession s = runningSession(100, 200);
        s.finish(START + 1_000, 2, false);
        savedAnswers.add(new InterviewAnswer(5L, 0, 100, "x", true));
        savedAnswers.add(new InterviewAnswer(5L, 1, 200, "y", true));

        InterviewService.Report report = service.finish(1L, 5L, Map.of(), START + 2_000);

        assertThat(report.score()).isEqualTo(2);
        verify(answers, never()).save(any());
        verify(progress, never()).setResult(anyLong(), any(), anyLong(), any(), any(), any());
    }

    @Test
    void anotherUsersInterviewIsNotFound() {
        when(sessions.findByIdAndUserId(5L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.test(2L, 5L, START)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> service.finish(2L, 5L, Map.of(), START)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void theRunningTestShowsQuestionsWithoutAnswersAndTheSecondsLeft() {
        runningSession(100, 200);

        InterviewService.TestView view = service.test(1L, 5L, START + 30_000);

        assertThat(view.remainingSeconds()).isEqualTo(150);
        assertThat(view.questions()).extracting(InterviewService.Question::drillId).containsExactly(100, 200);
        assertThat(InterviewService.Question.class.getRecordComponents())
                .extracting(c -> c.getName()).doesNotContain("answer", "explanation");
    }

    @Test
    void theRemainingTimeNeverGoesBelowZero() {
        runningSession(100, 200);

        assertThat(service.test(1L, 5L, START + 999_000).remainingSeconds()).isZero();
    }

    @Test
    void verdictsFollowThePercentage() {
        assertThat(InterviewService.verdict(80)).isEqualTo("Interview ready");
        assertThat(InterviewService.verdict(79)).isEqualTo("Getting there");
        assertThat(InterviewService.verdict(60)).isEqualTo("Getting there");
        assertThat(InterviewService.verdict(59)).isEqualTo("Keep practising");
    }

    @Test
    void pickingTheSameSeedGivesTheSameQuestions() {
        List<Drill> pool = drills.matching(null, null);

        assertThat(InterviewService.pick(pool, true, 10, new Random(7)))
                .isEqualTo(InterviewService.pick(pool, true, 10, new Random(7)));
        assertThat(InterviewService.pick(pool, true, 10, new Random(7)).stream().collect(Collectors.toSet())).hasSize(10);
    }
}
