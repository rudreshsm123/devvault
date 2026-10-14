package com.devvault.controller;

import com.devvault.config.CurrentUserAdvice;
import com.devvault.config.SecurityConfig;
import com.devvault.entity.AppUser;
import com.devvault.service.InsightsService;
import com.devvault.service.InsightsService.DueItem;
import com.devvault.service.InsightsService.Insights;
import com.devvault.service.InsightsService.TopicStat;
import com.devvault.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(InsightsController.class)
@Import({SecurityConfig.class, CurrentUserAdvice.class})
class InsightsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InsightsService insights;

    @MockitoBean
    private UserService users;

    @Test
    void anonymousVisitorsSeeASignInPrompt() throws Exception {
        mockMvc.perform(get("/insights"))
                .andExpect(status().isOk())
                .andExpect(view().name("insights"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sign in to unlock")));
    }

    @Test
    @WithMockUser("rajesh")
    void signedInUsersSeeDueItemsWeakTopicsAndTheHeatmap() throws Exception {
        AppUser user = new AppUser("rajesh", "h");
        user.setId(4L);
        when(users.getByUsername("rajesh")).thenReturn(user);
        TopicStat weak = new TopicStat("Streams", 5, 2, 40, true);
        DueItem due = new DueItem("drill", 12L, "Drill 12", "/drills/12", "Streams", 0, 1L);
        when(insights.forUser(eq(4L), anyLong(), any())).thenReturn(new Insights(
                1, List.of(due), List.of(weak), List.of(weak), 5, 40, 2, 3,
                List.of(new InsightsService.Day(LocalDate.of(2026, 10, 14), 4, 2))));

        mockMvc.perform(get("/insights"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Drill 12")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Streams")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("heat-2")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Start reviewing")));
    }

    @Test
    @WithMockUser("rajesh")
    void reviewNextGoesToTheMostOverdueItemOrBackToInsights() throws Exception {
        AppUser user = new AppUser("rajesh", "h");
        user.setId(4L);
        when(users.getByUsername("rajesh")).thenReturn(user);
        when(insights.nextDue(eq(4L), anyLong())).thenReturn(
                Optional.of(new DueItem("drill", 12L, "Drill 12", "/drills/12", null, 0, 1L)));

        mockMvc.perform(get("/review/next")).andExpect(redirectedUrl("/drills/12"));

        when(insights.nextDue(eq(4L), anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/review/next")).andExpect(redirectedUrl("/insights"));
    }

    @Test
    void theJsonEndpointNeedsASignedInUser() throws Exception {
        mockMvc.perform(get("/api/me/insights")).andExpect(status().isUnauthorized());
    }
}
