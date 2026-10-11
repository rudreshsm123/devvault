package com.devvault.controller;

import com.devvault.config.SecurityConfig;
import com.devvault.entity.CodeChallenge;
import com.devvault.service.ChallengeService;
import com.devvault.service.JavaJudgeService;
import com.devvault.service.JavaJudgeService.TestResult;
import com.devvault.service.JavaJudgeService.Verdict;
import com.devvault.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JudgeController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class JudgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChallengeService challengeService;

    @MockitoBean
    private JavaJudgeService javaJudge;

    private static final String BODY = "{\"problemId\":21,\"code\":\"class Solution { }\"}";

    @Test
    void returnsTheVerdictOfTheJudge() throws Exception {
        CodeChallenge challenge = new CodeChallenge(21L, "twoSum", "js", "java", "[]");
        when(javaJudge.isEnabled()).thenReturn(true);
        when(challengeService.findForProblem(21L)).thenReturn(Optional.of(challenge));
        when(javaJudge.judge(eq(challenge), any())).thenReturn(new Verdict("WRONG_ANSWER", "", 1, 2,
                List.of(new TestResult(false, "[1]", "2", "3", "", false))));

        mockMvc.perform(post("/api/judge/java").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WRONG_ANSWER"))
                .andExpect(jsonPath("$.passed").value(1))
                .andExpect(jsonPath("$.results[0].actual").value("3"));
    }

    @Test
    void unknownProblemIsNotFound() throws Exception {
        when(javaJudge.isEnabled()).thenReturn(true);
        when(challengeService.findForProblem(21L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/judge/java").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTheJudgeIsOffNoChallengeIsLookedUp() throws Exception {
        when(javaJudge.isEnabled()).thenReturn(false);
        when(javaJudge.judge(any(), any())).thenReturn(new Verdict("DISABLED", "switched off", 0, 0, List.of()));

        mockMvc.perform(post("/api/judge/java").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISABLED"));

        verify(challengeService, never()).findForProblem(any());
    }

    @Test
    void requestsWithoutACsrfTokenAreRejected() throws Exception {
        mockMvc.perform(post("/api/judge/java").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
    }
}
