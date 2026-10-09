package com.devvault.controller;

import com.devvault.entity.LearningResource;
import com.devvault.service.ResourceService;
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
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<LearningResource> createResource(@Valid @RequestBody LearningResource resource) {
        LearningResource saved = resourceService.createResource(resource);
        return ResponseEntity.created(URI.create("/api/resources/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<LearningResource> getAllResources() {
        return resourceService.getAllResources();
    }

    @GetMapping("/{id}")
    public LearningResource getResourceById(@PathVariable Long id) {
        return resourceService.getResourceById(id);
    }

    @PutMapping("/{id}")
    public LearningResource updateResource(@PathVariable Long id, @Valid @RequestBody LearningResource resource) {
        return resourceService.updateResource(id, resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
