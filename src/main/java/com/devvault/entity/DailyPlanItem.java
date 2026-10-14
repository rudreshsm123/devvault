package com.devvault.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

/**
 * One item of a learner's plan for one day. The plan is saved the first time it is opened that day, so it stays the
 * same all day even though answering an item changes what would be chosen if it were built again.
 */
@Entity
@Table(name = "daily_plan_item",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "planDay", "position"}))
public class DailyPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate planDay;

    @Column(nullable = false)
    private int position;

    /** "review", "weak" or "new": why this item is in the plan. */
    @Column(nullable = false, length = 10)
    private String source;

    /** "drill" or "problem". */
    @Column(nullable = false, length = 10)
    private String kind;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    @Column(length = 100)
    private String topic;

    public DailyPlanItem() {
    }

    public DailyPlanItem(Long userId, LocalDate planDay, int position, String source, String kind, Long itemId,
                         String title, String url, String topic) {
        this.userId = userId;
        this.planDay = planDay;
        this.position = position;
        this.source = source;
        this.kind = kind;
        this.itemId = itemId;
        this.title = title;
        this.url = url;
        this.topic = topic;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LocalDate getPlanDay() { return planDay; }
    public int getPosition() { return position; }
    public String getSource() { return source; }
    public String getKind() { return kind; }
    public Long getItemId() { return itemId; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public String getTopic() { return topic; }
}
