package com.devvault.controller;

import com.devvault.ai.AiExplainer;
import com.devvault.config.CurrentUserAdvice;
import com.devvault.config.SecurityConfig;
import com.devvault.drill.DrillService;
import com.devvault.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The AI explainer must be easy to notice when it is on, and must not be promised when it is off. */
@WebMvcTest(DrillController.class)
@Import({SecurityConfig.class, CurrentUserAdvice.class, GlobalExceptionHandler.class, DrillService.class})
class DrillAiVisibilityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiExplainer explainer;

    @Test
    void whenAiIsOnAnonymousVisitorsSeeTheHintAndASignInInvitation() throws Exception {
        when(explainer.aiEnabled()).thenReturn(true);

        mockMvc.perform(get("/drills/5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("AI tutor")))
                .andExpect(content().string(containsString("data-ai=\"true\"")))
                .andExpect(content().string(containsString("id=\"ai-signin\"")))
                .andExpect(content().string(containsString("Sign in</a> to use it")));
    }

    @Test
    @WithMockUser("rajesh")
    void whenAiIsOnSignedInUsersSeeTheHintWithoutTheSignInPrompt() throws Exception {
        when(explainer.aiEnabled()).thenReturn(true);

        mockMvc.perform(get("/drills/5"))
                .andExpect(content().string(containsString("AI tutor")))
                .andExpect(content().string(containsString("Explain my answer with AI")))
                .andExpect(content().string(not(containsString("</a> to use it"))));
    }

    @Test
    void whenAiIsOffNothingPromisesIt() throws Exception {
        when(explainer.aiEnabled()).thenReturn(false);

        mockMvc.perform(get("/drills/5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-ai=\"false\"")))
                .andExpect(content().string(not(containsString("AI tutor"))));
    }
}
