package com.devvault.service;

import com.devvault.entity.LearningResource;
import com.devvault.entity.ResourceType;
import com.devvault.exception.NotFoundException;
import com.devvault.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    private LearningResource sampleResource;

    @BeforeEach
    void setUp() {
        sampleResource = new LearningResource();
        sampleResource.setId(1L);
        sampleResource.setTitle("Spring Docs");
        sampleResource.setUrl("https://spring.io");
    }

    @Test
    void createResource_savesAndReturnsTheResource() {
        when(resourceRepository.save(sampleResource)).thenReturn(sampleResource);

        LearningResource result = resourceService.createResource(sampleResource);

        assertThat(result.getTitle()).isEqualTo("Spring Docs");
        verify(resourceRepository).save(sampleResource);
    }

    @Test
    void getResourceById_whenFound_returnsResource() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(sampleResource));

        assertThat(resourceService.getResourceById(1L)).isEqualTo(sampleResource);
    }

    @Test
    void getResourceById_whenMissing_throwsNotFoundException() {
        when(resourceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resourceService.getResourceById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateResource_overwritesFieldsAndSaves() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(sampleResource));
        when(resourceRepository.save(sampleResource)).thenReturn(sampleResource);

        LearningResource changes = new LearningResource();
        changes.setTitle("Spring Boot Docs");
        changes.setUrl("https://spring.io/boot");
        changes.setResourceType(ResourceType.DOCUMENTATION);

        LearningResource result = resourceService.updateResource(1L, changes);

        assertThat(result.getTitle()).isEqualTo("Spring Boot Docs");
        assertThat(result.getUrl()).isEqualTo("https://spring.io/boot");
        assertThat(result.getResourceType()).isEqualTo(ResourceType.DOCUMENTATION);
    }

    @Test
    void deleteResource_whenFound_deletesIt() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(sampleResource));

        resourceService.deleteResource(1L);

        verify(resourceRepository).delete(sampleResource);
    }

    @Test
    void search_convertsBlankStringsToNull() {
        when(resourceRepository.search(null, null, ResourceType.VIDEO)).thenReturn(List.of(sampleResource));

        List<LearningResource> result = resourceService.search(" ", "", ResourceType.VIDEO);

        assertThat(result).containsExactly(sampleResource);
    }
}
