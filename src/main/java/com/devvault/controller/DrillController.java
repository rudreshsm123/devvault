package com.devvault.controller;

import com.devvault.drill.Drill;
import com.devvault.drill.DrillResources;
import com.devvault.drill.DrillService;
import com.devvault.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/** 10,000 auto-graded practice drills: the page lists and shows them, the JSON endpoint says right or wrong. */
@Controller
public class DrillController {

    public record CheckRequest(String answer) {
    }

    private final DrillService drills;

    public DrillController(DrillService drills) {
        this.drills = drills;
    }

    @GetMapping("/drills")
    public String list(@RequestParam(required = false) String topic,
                       @RequestParam(required = false) String difficulty,
                       @RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "1") int page, Model model) {
        DrillService.Page result = drills.search(topic, difficulty, q, page);
        model.addAttribute("result", result);
        model.addAttribute("topics", drills.topics());
        model.addAttribute("total", drills.count());
        model.addAttribute("selectedTopic", topic);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("q", q);
        return "drills/list";
    }

    @GetMapping("/drills/random")
    public String random() {
        return "redirect:/drills/" + drills.randomId();
    }

    @GetMapping("/drills/{id}")
    public String view(@PathVariable int id, Model model) {
        Drill drill = drills.find(id).orElseThrow(() -> new NotFoundException("Drill", (long) id));
        model.addAttribute("drill", drill);
        model.addAttribute("links", DrillResources.forGroup(drill.group()));
        model.addAttribute("previousId", id > 1 ? id - 1 : null);
        model.addAttribute("nextId", id < drills.count() ? id + 1 : null);
        model.addAttribute("total", drills.count());
        return "drills/view";
    }

    @PostMapping("/api/drills/{id}/check")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> check(@PathVariable int id, @RequestBody CheckRequest request) {
        return drills.find(id)
                .map(d -> {
                    DrillService.Verdict v = drills.check(d, request.answer());
                    return ResponseEntity.ok(Map.<String, Object>of("correct", v.correct(), "answer", v.answer(),
                            "explanation", v.explanation()));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Drill not found")));
    }
}
