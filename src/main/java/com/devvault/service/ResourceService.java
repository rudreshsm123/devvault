package com.devvault.service;

import com.devvault.entity.LearningResource;
import com.devvault.entity.ResourceType;
import com.devvault.exception.NotFoundException;
import com.devvault.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public LearningResource createResource(LearningResource resource) {
        return resourceRepository.save(resource);
    }

    public List<LearningResource> getAllResources() {
        return resourceRepository.findAll();
    }

    public List<LearningResource> search(String keyword, String topic, ResourceType resourceType) {
        return resourceRepository.search(blankToNull(keyword), blankToNull(topic), resourceType);
    }

    public long count() {
        return resourceRepository.count();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    public LearningResource getResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Learning resource", id));
    }

    public LearningResource updateResource(Long id, LearningResource updated) {
        LearningResource existing = getResourceById(id);
        existing.setTitle(updated.getTitle());
        existing.setUrl(updated.getUrl());
        existing.setDescription(updated.getDescription());
        existing.setTopic(updated.getTopic());
        existing.setResourceType(updated.getResourceType());
        existing.setTags(updated.getTags());
        return resourceRepository.save(existing);
    }

    public void deleteResource(Long id) {
        resourceRepository.delete(getResourceById(id));
    }
}
