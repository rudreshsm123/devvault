package com.devvault.controller;

import com.devvault.entity.LearningResource;
import com.devvault.exception.NotFoundException;
import com.devvault.service.ResourceService;
import tools.jackson.databind.ObjectMapper;
import com.devvault.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
@Import(SecurityConfig.class)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ResourceService resourceService;

    private LearningResource resource(Long id, String title, String url) {
        LearningResource resource = new LearningResource();
        resource.setId(id);
        resource.setTitle(title);
        resource.setUrl(url);
        return resource;
    }

    @Test
    void getAllResources_returnsJsonArray() throws Exception {
        when(resourceService.getAllResources())
                .thenReturn(List.of(resource(1L, "Spring Docs", "https://spring.io")));

        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Docs"));
    }

    @Test
    void getResourceById_whenMissing_returns404WithMessage() throws Exception {
        when(resourceService.getResourceById(99L)).thenThrow(new NotFoundException("Learning resource", 99L));

        mockMvc.perform(get("/api/resources/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Learning resource not found with id: 99"));
    }

    @Test
    void createResource_withBlankTitle_returns400WithFieldError() throws Exception {
        LearningResource invalid = new LearningResource();
        invalid.setUrl("https://example.com");

        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title is required"));
    }

    @Test
    void createResource_withBlankUrl_returns400WithFieldError() throws Exception {
        LearningResource invalid = new LearningResource();
        invalid.setTitle("No url");

        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.url").value("URL is required"));
    }

    @Test
    void createResource_withValidBody_returns201WithLocationHeader() throws Exception {
        when(resourceService.createResource(any(LearningResource.class)))
                .thenReturn(resource(5L, "Spring Docs", "https://spring.io"));

        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resource(null, "Spring Docs", "https://spring.io"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/resources/5"));
    }

    @Test
    void updateResource_withValidBody_returnsUpdatedResource() throws Exception {
        when(resourceService.updateResource(any(Long.class), any(LearningResource.class)))
                .thenReturn(resource(1L, "Spring Boot Docs", "https://spring.io/boot"));

        mockMvc.perform(put("/api/resources/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                resource(null, "Spring Boot Docs", "https://spring.io/boot"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot Docs"));
    }

    @Test
    void deleteResource_returns204() throws Exception {
        mockMvc.perform(delete("/api/resources/1"))
                .andExpect(status().isNoContent());

        verify(resourceService).deleteResource(1L);
    }
}
