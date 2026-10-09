package com.devvault.controller;

import com.devvault.entity.CodingProblem;
import com.devvault.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping
    public ResponseEntity<CodingProblem> createProblem(@Valid @RequestBody CodingProblem problem) {
        CodingProblem saved = problemService.createProblem(problem);
        return ResponseEntity.created(URI.create("/api/problems/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<CodingProblem> getAllProblems() {
        return problemService.getAllProblems();
    }

    @GetMapping("/{id}")
    public CodingProblem getProblemById(@PathVariable Long id) {
        return problemService.getProblemById(id);
    }

    @PutMapping("/{id}")
    public CodingProblem updateProblem(@PathVariable Long id, @Valid @RequestBody CodingProblem problem) {
        return problemService.updateProblem(id, problem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}
