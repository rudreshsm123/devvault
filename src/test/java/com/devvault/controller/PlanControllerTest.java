package com.devvault.controller;

import com.devvault.config.CurrentUserAdvice;
import com.devvault.config.SecurityConfig;
import com.devvault.entity.AppUser;
import com.devvault.service.PlanService;
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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PlanController.class)
@Import({SecurityConfig.class, CurrentUserAdvice.class})
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanService plans;

    @MockitoBean
    private UserService users;

    @Test
    void anonymousVisitorsSeeASignInPrompt() throws Exception {
        mockMvc.perform(get("/plan"))
                .andExpect(status().isOk())
                .andExpect(view().name("plan"))
                .andExpect(content().string(containsString("Sign in to get a")));
    }

    @Test
    @WithMockUser("rajesh")
    void signedInUsersSeeTheirItemsTagsAndProgress() throws Exception {
        AppUser user = new AppUser("rajesh", "h");
        user.setId(4L);
        when(users.getByUsername("rajesh")).thenReturn(user);
        when(plans.today(eq(4L), anyLong(), any())).thenReturn(new PlanService.Plan(LocalDate.of(2026, 10, 14),
                List.of(new PlanService.Entry("review", "drill", 5L, "Drill 5: loops", "/drills/5", "Arrays", true),
                        new PlanService.Entry("weak", "drill", 6L, "Drill 6: streams", "/drills/6", "Streams", false),
                        new PlanService.Entry("new", "drill", 7L, "Drill 7: strings", "/drills/7", "Strings", false)),
                1));

        mockMvc.perform(get("/plan"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Wednesday, 14 October 2026")))
                .andExpect(content().string(containsString("1 of 3 done")))
                .andExpect(content().string(containsString("Drill 6: streams")))
                .andExpect(content().string(containsString("Weak topic")))
                .andExpect(content().string(containsString("Review")))
                .andExpect(content().string(containsString("is-done")));
    }
}
