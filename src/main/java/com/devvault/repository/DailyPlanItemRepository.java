package com.devvault.repository;

import com.devvault.entity.DailyPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyPlanItemRepository extends JpaRepository<DailyPlanItem, Long> {

    List<DailyPlanItem> findByUserIdAndPlanDayOrderByPosition(Long userId, LocalDate planDay);
}
