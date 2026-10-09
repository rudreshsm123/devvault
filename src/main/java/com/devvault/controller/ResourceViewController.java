package com.devvault.controller;

import com.devvault.entity.LearningResource;
import com.devvault.entity.ResourceType;
import com.devvault.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/resources")
public class ResourceViewController {

    private final ResourceService resourceService;

    public ResourceViewController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @ModelAttribute("resourceTypes")
    public ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    @GetMapping
    public String listResources(@RequestParam(required = false) String q,
                                 @RequestParam(required = false) String topic,
                                 @RequestParam(required = false) ResourceType resourceType,
                                 Model model) {
        model.addAttribute("resources", resourceService.search(q, topic, resourceType));
        model.addAttribute("q", q);
        model.addAttribute("selectedTopic", topic);
        model.addAttribute("selectedResourceType", resourceType);
        return "resources/list";
    }

    @GetMapping("/new")
    public String newResourceForm(Model model) {
        model.addAttribute("resource", new LearningResource());
        return "resources/form";
    }

    @PostMapping
    public String createResource(@Valid @ModelAttribute("resource") LearningResource resource,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "resources/form";
        }
        resourceService.createResource(resource);
        return "redirect:/resources";
    }

    @GetMapping("/{id}")
    public String viewResource(@PathVariable Long id, Model model) {
        model.addAttribute("resource", resourceService.getResourceById(id));
        return "resources/view";
    }

    @GetMapping("/{id}/edit")
    public String editResourceForm(@PathVariable Long id, Model model) {
        model.addAttribute("resource", resourceService.getResourceById(id));
        return "resources/form";
    }

    @PostMapping("/{id}/edit")
    public String updateResource(@PathVariable Long id, @Valid @ModelAttribute("resource") LearningResource resource,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "resources/form";
        }
        resourceService.updateResource(id, resource);
        return "redirect:/resources";
    }

    @PostMapping("/{id}/delete")
    public String deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return "redirect:/resources";
    }
}
