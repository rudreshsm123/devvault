package com.devvault.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** One finished problem or lesson of one user. */
@Entity
@Table(name = "user_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "kind", "itemId"}))
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    /** "problem" or "lesson". */
    @Column(nullable = false, length = 10)
    private String kind;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    /** Epoch milliseconds when the item was marked done. */
    @Column(nullable = false)
    private long doneAt;

    public UserProgress() {
    }

    public UserProgress(Long userId, String kind, Long itemId, String title, String url, long doneAt) {
        this.userId = userId;
        this.kind = kind;
        this.itemId = itemId;
        this.title = title;
        this.url = url;
        this.doneAt = doneAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getKind() { return kind; }
    public Long getItemId() { return itemId; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public long getDoneAt() { return doneAt; }
}
