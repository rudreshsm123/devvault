package com.devvault.controller;

import com.devvault.config.CurrentUserAdvice;
import com.devvault.config.SecurityConfig;
import com.devvault.drill.DrillService;
import com.devvault.drill.Spec;
import com.devvault.entity.AppUser;
import com.devvault.entity.InterviewSession;
import com.devvault.service.InterviewService;
import com.devvault.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(InterviewController.class)
@Import({SecurityConfig.class, CurrentUserAdvice.class})
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InterviewService interviews;

    @MockitoBean
    private UserService users;

    @MockitoBean
    private DrillService drills;

    @BeforeEach
    void setUp() {
        AppUser user = new AppUser("rajesh", "h");
        user.setId(4L);
        when(users.getByUsername("rajesh")).thenReturn(user);
        when(drills.topics()).thenReturn(new java.util.TreeMap<>(Map.of("Arrays", 10L, "Streams", 10L)));
    }

    @Test
    void anonymousVisitorsSeeTheSetupPageWithASignInPrompt() throws Exception {
        mockMvc.perform(get("/interview"))
                .andExpect(status().isOk())
                .andExpect(view().name("interview/index"))
                .andExpect(content().string(containsString("Sign in to take a timed interview")))
                .andExpect(content().string(not(containsString("Start the clock"))));
    }

    @Test
    @WithMockUser("rajesh")
    void signedInUsersSeeTheFormWithTopicsAndTheirLastScores() throws Exception {
        InterviewSession past = new InterviewSession(4L, "Arrays", null, 1L, 450, "1,2,3,4,5");
        past.finish(2L, 4, false);
        when(interviews.recent(4L)).thenReturn(List.of(past));

        mockMvc.perform(get("/interview"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Start the clock")))
                .andExpect(content().string(containsString("Streams")))
                .andExpect(content().string(containsString("4 / 5")));
    }

    @Test
    void startingWithoutSigningInIsRejected() throws Exception {
        mockMvc.perform(post("/interview/start").with(csrf()).param("count", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("rajesh")
    void startingRedirectsToTheNewInterviewAndBadChoicesGoBackToTheForm() throws Exception {
        when(interviews.start(eq(4L), eq("Arrays"), any(), eq(10), anyLong(), any())).thenReturn(9L);
        mockMvc.perform(post("/interview/start").with(csrf()).param("topic", "Arrays").param("count", "10"))
                .andExpect(redirectedUrl("/interview/9"));

        when(interviews.start(eq(4L), eq("Nope"), any(), anyInt(), anyLong(), any()))
                .thenThrow(new IllegalArgumentException("Unknown topic."));
        mockMvc.perform(post("/interview/start").with(csrf()).param("topic", "Nope").param("count", "10"))
                .andExpect(redirectedUrl("/interview?error"));
    }

    @Test
    @WithMockUser("rajesh")
    void aRunningInterviewShowsTheTimerAndQuestionsButNoAnswers() throws Exception {
        InterviewSession running = new InterviewSession(4L, null, null, 1L, 180, "1,2");
        when(interviews.get(4L, 9L)).thenReturn(running);
        when(interviews.test(eq(4L), eq(9L), anyLong())).thenReturn(new InterviewService.TestView(9L, null, 180, 150,
                List.of(new InterviewService.Question(0, 1, "Loops", "What prints?", "System.out.println(1);",
                                Spec.Kind.NUMBER, List.of()),
                        new InterviewService.Question(1, 2, "Pick", "Which one?", "", Spec.Kind.CHOICE,
                                List.of("alpha", "beta")))));

        mockMvc.perform(get("/interview/9"))
                .andExpect(status().isOk())
                .andExpect(view().name("interview/test"))
                .andExpect(content().string(containsString("data-remaining=\"150\"")))
                .andExpect(content().string(containsString("What prints?")))
                .andExpect(content().string(containsString("alpha")))
                .andExpect(content().string(containsString("/js/interview-")));
    }

    @Test
    @WithMockUser("rajesh")
    void aFinishedInterviewShowsTheReport() throws Exception {
        InterviewSession done = new InterviewSession(4L, null, null, 1L, 180, "1,2");
        done.finish(60_001L, 1, false);
        when(interviews.get(4L, 9L)).thenReturn(done);
        when(interviews.report(4L, 9L)).thenReturn(new InterviewService.Report(9L, null, null, 2, 1, 50,
                "Keep practising", 60, 180, false,
                List.of(new InterviewService.Line(1, 1, "Drill 1: Loops", "Arrays", "3", "2", false, "Because.", "/drills/1"),
                        new InterviewService.Line(2, 2, "Drill 2: Pick", "Streams", "beta", "beta", true, "Yes.", "/drills/2")),
                List.of(new InterviewService.TopicResult("Arrays", 1, 0), new InterviewService.TopicResult("Streams", 1, 1))));

        mockMvc.perform(get("/interview/9"))
                .andExpect(status().isOk())
                .andExpect(view().name("interview/report"))
                .andExpect(content().string(containsString("1 / 2")))
                .andExpect(content().string(containsString("Keep practising")))
                .andExpect(content().string(containsString("Correct answer")))
                .andExpect(content().string(containsString("Because.")));
    }

    @Test
    @WithMockUser("rajesh")
    void finishReturnsTheScoreAsJson() throws Exception {
        when(interviews.finish(eq(4L), eq(9L), any(), anyLong())).thenReturn(new InterviewService.Report(9L, null, null,
                10, 7, 70, "Getting there", 100, 900, false, List.of(), List.of()));

        mockMvc.perform(post("/api/me/interview/9/finish").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answers\":{\"1\":\"2\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(7))
                .andExpect(jsonPath("$.total").value(10));
    }

    @Test
    void finishNeedsASignedInUserAndACsrfToken() throws Exception {
        mockMvc.perform(post("/api/me/interview/9/finish").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answers\":{}}"))
                .andExpect(status().isUnauthorized());
    }
}
