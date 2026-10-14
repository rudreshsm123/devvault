package com.devvault.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** What a learner answered to one question of a finished interview, and whether it was right. */
@Entity
@Table(name = "interview_answer", uniqueConstraints = @UniqueConstraint(columnNames = {"sessionId", "position"}))
public class InterviewAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private int drillId;

    /** Null when the question was left unanswered. */
    @Column(length = 300)
    private String given;

    @Column(nullable = false)
    private boolean correct;

    public InterviewAnswer() {
    }

    public InterviewAnswer(Long sessionId, int position, int drillId, String given, boolean correct) {
        this.sessionId = sessionId;
        this.position = position;
        this.drillId = drillId;
        this.given = given;
        this.correct = correct;
    }

    public Long getId() { return id; }
    public Long getSessionId() { return sessionId; }
    public int getPosition() { return position; }
    public int getDrillId() { return drillId; }
    public String getGiven() { return given; }
    public boolean isCorrect() { return correct; }
}
