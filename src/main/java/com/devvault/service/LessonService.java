package com.devvault.service;

import com.devvault.entity.Lesson;
import com.devvault.exception.NotFoundException;
import com.devvault.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<Lesson> getAllInOrder() {
        return lessonRepository.findAllByOrderByChapterNoAscLessonNoAsc();
    }

    public Lesson getById(Long id) {
        return lessonRepository.findById(id).orElseThrow(() -> new NotFoundException("Lesson", id));
    }

    public long count() {
        return lessonRepository.count();
    }

    /** Previous lesson in reading order, if any. */
    public Optional<Lesson> previous(Lesson current) {
        List<Lesson> all = getAllInOrder();
        for (int i = 1; i < all.size(); i++) {
            if (all.get(i).getId().equals(current.getId())) {
                return Optional.of(all.get(i - 1));
            }
        }
        return Optional.empty();
    }

    /** Next lesson in reading order, if any. */
    public Optional<Lesson> next(Lesson current) {
        List<Lesson> all = getAllInOrder();
        for (int i = 0; i < all.size() - 1; i++) {
            if (all.get(i).getId().equals(current.getId())) {
                return Optional.of(all.get(i + 1));
            }
        }
        return Optional.empty();
    }
}
