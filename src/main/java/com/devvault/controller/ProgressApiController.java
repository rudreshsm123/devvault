package com.devvault.controller;

import com.devvault.entity.UserProgress;
import com.devvault.service.ProgressService;
import com.devvault.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Progress of the signed-in user. The JSON shape matches what the browser keeps in localStorage:
 * {"problem": {"12": {"t": title, "u": url, "at": millis}}, "lesson": {...}}.
 */
@RestController
@RequestMapping("/api/me/progress")
public class ProgressApiController {

    /** t = title, u = url, at = time, r = result ("right" or "wrong"; missing means right). */
    public record Item(String t, String u, long at, String r) {
    }

    public record ResultRequest(String kind, Long id, String title, String url, String result) {
    }

    public record ToggleRequest(String kind, Long id, String title, String url) {
    }

    public record ImportRequest(Map<String, Item> problem, Map<String, Item> lesson) {
    }

    private final ProgressService progressService;
    private final UserService userService;

    public ProgressApiController(ProgressService progressService, UserService userService) {
        this.progressService = progressService;
        this.userService = userService;
    }

    @GetMapping
    public Map<String, Map<String, Item>> all(Principal principal) {
        return snapshot(userId(principal));
    }

    @PostMapping("/toggle")
    public Map<String, Boolean> toggle(@RequestBody ToggleRequest request, Principal principal) {
        boolean done = progressService.toggle(userId(principal), request.kind(), request.id(),
                request.title(), request.url());
        return Map.of("done", done);
    }

    /** Saves "I got it right / wrong" for a problem. */
    @PostMapping("/result")
    public Map<String, String> result(@RequestBody ResultRequest request, Principal principal) {
        progressService.setResult(userId(principal), request.kind(), request.id(), request.title(),
                request.url(), request.result());
        return Map.of("result", request.result());
    }

    /** Merges progress collected in the browser before the user signed in. */
    @PostMapping("/import")
    public Map<String, Map<String, Item>> importLocal(@RequestBody ImportRequest request, Principal principal) {
        Long userId = userId(principal);
        List<UserProgress> items = new java.util.ArrayList<>();
        collect("problem", request.problem(), items);
        collect("lesson", request.lesson(), items);
        progressService.merge(userId, items);
        return snapshot(userId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }

    private Long userId(Principal principal) {
        return userService.getByUsername(principal.getName()).getId();
    }

    private Map<String, Map<String, Item>> snapshot(Long userId) {
        Map<String, Item> problems = new LinkedHashMap<>();
        Map<String, Item> lessons = new LinkedHashMap<>();
        for (UserProgress p : progressService.list(userId)) {
            Map<String, Item> target = p.getKind().equals("problem") ? problems : lessons;
            target.put(String.valueOf(p.getItemId()), new Item(p.getTitle(), p.getUrl(), p.getDoneAt(), p.getResult()));
        }
        Map<String, Map<String, Item>> out = new LinkedHashMap<>();
        out.put("problem", problems);
        out.put("lesson", lessons);
        return out;
    }

    private static void collect(String kind, Map<String, Item> source, List<UserProgress> out) {
        if (source == null) {
            return;
        }
        for (Map.Entry<String, Item> e : source.entrySet()) {
            long id;
            try {
                id = Long.parseLong(e.getKey());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("ids must be numbers");
            }
            Item item = e.getValue();
            out.add(new UserProgress(null, kind, id, item.t(), item.u(), item.at(), item.r()));
        }
    }
}
