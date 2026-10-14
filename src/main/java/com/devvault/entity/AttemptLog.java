package com.devvault.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/** One answered problem or drill of one user. UserProgress keeps only the latest result; this keeps the history. */
@Entity
@Table(name = "attempt_log", indexes = @Index(name = "idx_attempt_user_time", columnList = "userId, at"))
public class AttemptLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    /** "problem" or "drill". */
    @Column(nullable = false, length = 10)
    private String kind;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private boolean correct;

    /** Epoch milliseconds. */
    @Column(nullable = false)
    private long at;

    public AttemptLog() {
    }

    public AttemptLog(Long userId, String kind, Long itemId, boolean correct, long at) {
        this.userId = userId;
        this.kind = kind;
        this.itemId = itemId;
        this.correct = correct;
        this.at = at;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getKind() { return kind; }
    public Long getItemId() { return itemId; }
    public boolean isCorrect() { return correct; }
    public long getAt() { return at; }
}
