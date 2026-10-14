package com.devvault.controller;

import com.devvault.ai.AiExplainer;
import com.devvault.ai.RateLimiter;
import com.devvault.config.SecurityConfig;
import com.devvault.drill.Drill;
import com.devvault.drill.DrillService;
import com.devvault.drill.Spec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiController.class)
@Import(SecurityConfig.class)
class AiControllerTest {

    private static final Drill DRILL = new Drill(7, "fam", "Family", "Streams", "group", "EASY", "Title", "Q?",
            "", Spec.Kind.NUMBER, "2", List.of(), "Because.", false);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiExplainer explainer;

    @MockitoBean
    private DrillService drills;

    @MockitoBean
    private RateLimiter limiter;

    private String body(int id) {
        return "{\"drillId\":" + id + ",\"answer\":\"3\"}";
    }

    @Test
    void needsASignedInUser() throws Exception {
        mockMvc.perform(post("/api/me/ai/explain").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body(7)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("rajesh")
    void returnsTheExplanationAndItsSource() throws Exception {
        when(drills.find(7)).thenReturn(Optional.of(DRILL));
        when(limiter.tryAcquire(eq("rajesh"), anyLong())).thenReturn(true);
        when(explainer.explain(DRILL, "3")).thenReturn(new AiExplainer.Explanation("Because.", "builtin"));

        mockMvc.perform(post("/api/me/ai/explain").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body(7)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Because."))
                .andExpect(jsonPath("$.source").value("builtin"));
    }

    @Test
    @WithMockUser("rajesh")
    void unknownDrillIs404AndRateLimitIs429() throws Exception {
        when(drills.find(99)).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/me/ai/explain").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body(99)))
                .andExpect(status().isNotFound());

        when(drills.find(7)).thenReturn(Optional.of(DRILL));
        when(limiter.tryAcquire(eq("rajesh"), anyLong())).thenReturn(false);
        mockMvc.perform(post("/api/me/ai/explain").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body(7)))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @WithMockUser("rajesh")
    void missingCsrfTokenIsRejected() throws Exception {
        mockMvc.perform(post("/api/me/ai/explain").contentType(MediaType.APPLICATION_JSON).content(body(7)))
                .andExpect(status().isForbidden());
    }
}
