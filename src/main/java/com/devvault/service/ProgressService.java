package com.devvault.service;

import com.devvault.entity.UserProgress;
import com.devvault.repository.UserProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Server-side "mark as done" for signed-in users. */
@Service
public class ProgressService {

    private static final Set<String> KINDS = Set.of("problem", "lesson");
    private static final int MAX_TEXT = 255;

    private final UserProgressRepository repository;

    public ProgressService(UserProgressRepository repository) {
        this.repository = repository;
    }

    public List<UserProgress> list(Long userId) {
        return repository.findByUserId(userId);
    }

    /** Flips the item and returns true when it is now marked as done. */
    @Transactional
    public boolean toggle(Long userId, String kind, Long itemId, String title, String url) {
        validate(kind, itemId, title, url);
        Optional<UserProgress> existing = repository.findByUserIdAndKindAndItemId(userId, kind, itemId);
        if (existing.isPresent()) {
            repository.delete(existing.get());
            return false;
        }
        repository.save(new UserProgress(userId, kind, itemId, title, url, System.currentTimeMillis()));
        return true;
    }

    /** Adds items (for example progress collected in the browser before signing in) without removing anything. */
    @Transactional
    public int merge(Long userId, List<UserProgress> items) {
        int added = 0;
        for (UserProgress item : items) {
            validate(item.getKind(), item.getItemId(), item.getTitle(), item.getUrl());
            if (repository.findByUserIdAndKindAndItemId(userId, item.getKind(), item.getItemId()).isEmpty()) {
                repository.save(new UserProgress(userId, item.getKind(), item.getItemId(),
                        item.getTitle(), item.getUrl(),
                        item.getDoneAt() > 0 ? item.getDoneAt() : System.currentTimeMillis()));
                added++;
            }
        }
        return added;
    }

    private static void validate(String kind, Long itemId, String title, String url) {
        if (kind == null || !KINDS.contains(kind)) {
            throw new IllegalArgumentException("kind must be 'problem' or 'lesson'");
        }
        if (itemId == null || itemId < 1) {
            throw new IllegalArgumentException("id must be a positive number");
        }
        if (title == null || title.isBlank() || title.length() > MAX_TEXT) {
            throw new IllegalArgumentException("title is required (max 255 characters)");
        }
        // Only same-site links are allowed, so a stored value can never become an external or javascript: link.
        if (url == null || !url.startsWith("/") || url.startsWith("//") || url.length() > MAX_TEXT) {
            throw new IllegalArgumentException("url must be a path on this site");
        }
    }
}
