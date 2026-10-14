package com.devvault.service;

import com.devvault.drill.DrillService;
import com.devvault.entity.AttemptLog;
import com.devvault.entity.DailyPlanItem;
import com.devvault.repository.AttemptLogRepository;
import com.devvault.repository.DailyPlanItemRepository;
import com.devvault.repository.UserProgressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    private static final ZoneId ZONE = ZoneId.of("UTC");
    private static final long NOW = LocalDate.of(2026, 10, 14).atTime(12, 0).atZone(ZONE).toInstant().toEpochMilli();
    private static final LocalDate DAY = LocalDate.of(2026, 10, 14);

    @Mock
    private InsightsService insights;
    @Mock
    private DailyPlanItemRepository plans;
    @Mock
    private UserProgressRepository progress;
    @Mock
    private AttemptLogRepository attempts;

    private final DrillService drills = new DrillService();

    private PlanService service() {
        return new PlanService(insights, plans, progress, attempts, drills);
    }

    private InsightsService.Insights emptyInsights() {
        return new InsightsService.Insights(0, List.of(), List.of(), List.of(), 0, 0, 0, 0, List.of());
    }

    @Test
    void theFirstVisitOfTheDayBuildsAndSavesATenItemPlan() {
        when(plans.findByUserIdAndPlanDayOrderByPosition(1L, DAY)).thenReturn(List.of());
        when(insights.forUser(1L, NOW, ZONE)).thenReturn(emptyInsights());
        when(progress.findByUserId(1L)).thenReturn(List.of());
        when(plans.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));
        when(attempts.findByUserIdAndAtGreaterThanEqual(anyLong(), anyLong())).thenReturn(List.of());

        PlanService.Plan plan = service().today(1L, NOW, ZONE);

        assertThat(plan.total()).isEqualTo(10);
        assertThat(plan.doneCount()).isZero();
        assertThat(plan.complete()).isFalse();
        assertThat(plan.entries()).allMatch(e -> e.url().startsWith("/drills/"));
        verify(plans).saveAll(anyList());
    }

    @Test
    void anExistingPlanIsReusedAndItemsAnsweredTodayAreMarkedDone() {
        DailyPlanItem a = new DailyPlanItem(1L, DAY, 0, "new", "drill", 5L, "Drill 5: x", "/drills/5", "Arrays");
        DailyPlanItem b = new DailyPlanItem(1L, DAY, 1, "weak", "drill", 6L, "Drill 6: y", "/drills/6", "Streams");
        when(plans.findByUserIdAndPlanDayOrderByPosition(1L, DAY)).thenReturn(List.of(a, b));
        when(attempts.findByUserIdAndAtGreaterThanEqual(anyLong(), anyLong()))
                .thenReturn(List.of(new AttemptLog(1L, "drill", 5L, false, NOW)));

        PlanService.Plan plan = service().today(1L, NOW, ZONE);

        assertThat(plan.entries()).extracting(PlanService.Entry::done).containsExactly(true, false);
        assertThat(plan.doneCount()).isEqualTo(1);
        verify(plans, never()).saveAll(anyList());
        verify(insights, never()).forUser(anyLong(), anyLong(), any());
    }

    @Test
    void whenEveryItemIsAnsweredThePlanIsComplete() {
        DailyPlanItem a = new DailyPlanItem(1L, DAY, 0, "new", "drill", 5L, "Drill 5: x", "/drills/5", "Arrays");
        when(plans.findByUserIdAndPlanDayOrderByPosition(1L, DAY)).thenReturn(List.of(a));
        when(attempts.findByUserIdAndAtGreaterThanEqual(anyLong(), anyLong()))
                .thenReturn(List.of(new AttemptLog(1L, "drill", 5L, true, NOW)));

        assertThat(service().today(1L, NOW, ZONE).complete()).isTrue();
    }

    @Test
    void aRaceWithAnotherTabUsesThePlanThatWasSavedFirst() {
        DailyPlanItem winner = new DailyPlanItem(1L, DAY, 0, "new", "drill", 9L, "Drill 9: z", "/drills/9", "Arrays");
        when(plans.findByUserIdAndPlanDayOrderByPosition(1L, DAY)).thenReturn(List.of(), List.of(winner));
        when(insights.forUser(1L, NOW, ZONE)).thenReturn(emptyInsights());
        when(progress.findByUserId(1L)).thenReturn(List.of());
        when(plans.saveAll(anyList())).thenThrow(new DataIntegrityViolationException("duplicate"));
        when(attempts.findByUserIdAndAtGreaterThanEqual(anyLong(), anyLong())).thenReturn(List.of());

        PlanService.Plan plan = service().today(1L, NOW, ZONE);

        assertThat(plan.entries()).extracting(PlanService.Entry::itemId).containsExactly(9L);
    }
}
