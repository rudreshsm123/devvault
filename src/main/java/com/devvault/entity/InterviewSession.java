package com.devvault.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/** One timed mock interview of one user: which questions, when it started, how long it lasts and how it went. */
@Entity
@Table(name = "interview_session", indexes = @Index(name = "idx_interview_user", columnList = "userId, startedAt"))
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    /** The chosen topic, or null for a mix of every topic. */
    @Column(length = 100)
    private String topic;

    /** The chosen level, or null for any. */
    @Column(length = 30)
    private String difficulty;

    /** Epoch milliseconds. */
    @Column(nullable = false)
    private long startedAt;

    @Column(nullable = false)
    private int durationSeconds;

    /** Comma separated drill ids, in the order they are asked. */
    @Column(nullable = false, length = 255)
    private String drillIds;

    /** Epoch milliseconds, null while the interview is still running. */
    private Long finishedAt;

    private Integer score;

    /** True when the answers arrived after the time (and a short grace period) was over, so none were counted. */
    private boolean late;

    public InterviewSession() {
    }

    public InterviewSession(Long userId, String topic, String difficulty, long startedAt, int durationSeconds,
                            String drillIds) {
        this.userId = userId;
        this.topic = topic;
        this.difficulty = difficulty;
        this.startedAt = startedAt;
        this.durationSeconds = durationSeconds;
        this.drillIds = drillIds;
    }

    public void finish(long finishedAt, int score, boolean late) {
        this.finishedAt = finishedAt;
        this.score = score;
        this.late = late;
    }

    public boolean isFinished() { return finishedAt != null; }
    public long deadline() { return startedAt + durationSeconds * 1000L; }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTopic() { return topic; }
    public String getDifficulty() { return difficulty; }
    public long getStartedAt() { return startedAt; }
    public int getDurationSeconds() { return durationSeconds; }
    public String getDrillIds() { return drillIds; }
    public Long getFinishedAt() { return finishedAt; }
    public Integer getScore() { return score; }
    public boolean isLate() { return late; }
}
