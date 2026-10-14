package com.devvault.drill;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/** In-memory access to the 10,000 generated drills and the answer check. */
@Service
public class DrillService {

    public static final int PAGE_SIZE = 50;

    public record Page(List<Drill> items, int page, int pages, int total) {
    }

    public record Verdict(boolean correct, String answer, String explanation) {
    }

    private final List<Drill> drills = DrillCatalog.build();

    /** Topic name -> ids of every drill in it, in id order. */
    private final Map<String, List<Integer>> idsByTopic = drills.stream().collect(Collectors.groupingBy(
            Drill::topic, TreeMap::new, Collectors.mapping(Drill::id, Collectors.toList())));

    public int count() {
        return drills.size();
    }

    public Optional<Drill> find(int id) {
        return id >= 1 && id <= drills.size() ? Optional.of(drills.get(id - 1)) : Optional.empty();
    }

    public int randomId() {
        return ThreadLocalRandom.current().nextInt(1, drills.size() + 1);
    }

    /** Topic name -> how many drills it has, in alphabetical order. */
    public Map<String, Long> topics() {
        return drills.stream().collect(Collectors.groupingBy(Drill::topic, TreeMap::new, Collectors.counting()));
    }

    /** Every drill of a topic and level; a blank topic or level means "any". */
    public List<Drill> matching(String topic, String difficulty) {
        return drills.stream()
                .filter(d -> topic == null || topic.isBlank() || d.topic().equalsIgnoreCase(topic))
                .filter(d -> difficulty == null || difficulty.isBlank() || d.difficulty().equalsIgnoreCase(difficulty))
                .toList();
    }

    public Map<String, List<Integer>> idsByTopic() {
        return idsByTopic;
    }

    public Page search(String topic, String difficulty, String q, int page) {
        String needle = q == null ? "" : q.trim().toLowerCase();
        List<Drill> matches = drills.stream()
                .filter(d -> topic == null || topic.isBlank() || d.topic().equalsIgnoreCase(topic))
                .filter(d -> difficulty == null || difficulty.isBlank() || d.difficulty().equalsIgnoreCase(difficulty))
                .filter(d -> needle.isEmpty() || d.title().toLowerCase().contains(needle)
                        || d.question().toLowerCase().contains(needle) || d.code().toLowerCase().contains(needle))
                .toList();
        int pages = Math.max(1, (matches.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int p = Math.min(Math.max(page, 1), pages);
        int from = (p - 1) * PAGE_SIZE;
        return new Page(matches.subList(from, Math.min(matches.size(), from + PAGE_SIZE)), p, pages, matches.size());
    }

    public Verdict check(Drill drill, String given) {
        return new Verdict(matches(drill, given), drill.answer(), drill.explanation());
    }

    static boolean matches(Drill drill, String given) {
        if (given == null) {
            return false;
        }
        String a = given.trim();
        switch (drill.kind()) {
            case NUMBER -> {
                try {
                    return Long.parseLong(a.replace(",", "").replace(" ", "")) == Long.parseLong(drill.answer());
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case CHOICE -> {
                return a.equalsIgnoreCase(drill.answer());
            }
            default -> {
                return a.replaceAll("\\s+", "").equals(drill.answer().replaceAll("\\s+", ""));
            }
        }
    }
}
