package com.devvault.controller;

import com.devvault.config.CurrentUserAdvice;
import com.devvault.config.SecurityConfig;
import com.devvault.drill.Drill;
import com.devvault.drill.DrillService;
import com.devvault.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DrillController.class)
@Import({SecurityConfig.class, CurrentUserAdvice.class, GlobalExceptionHandler.class, DrillService.class,
        com.devvault.ai.AiExplainer.class})
class DrillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DrillService drills;

    @Test
    void correctAnswerIsRightAndWrongAnswerIsWrong() throws Exception {
        Drill d = drills.find(7).orElseThrow();
        mockMvc.perform(post("/api/drills/7/check").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\":\"" + d.answer().replace("\\", "\\\\").replace("\"", "\\\"") + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true));
        mockMvc.perform(post("/api/drills/7/check").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\":\"definitely-not-the-answer\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(false))
                .andExpect(jsonPath("$.answer").value(d.answer()));
    }

    @Test
    void everyDrillAcceptsItsOwnAnswer() {
        for (int id = 1; id <= drills.count(); id++) {
            Drill d = drills.find(id).orElseThrow();
            org.assertj.core.api.Assertions.assertThat(drills.check(d, d.answer()).correct()).as("drill %d", id).isTrue();
        }
    }

    @Test
    void listViewAndRandomWork() throws Exception {
        mockMvc.perform(get("/drills")).andExpect(status().isOk()).andExpect(content().string(containsString("10000")));
        mockMvc.perform(get("/drills").param("difficulty", "Advanced").param("page", "3")).andExpect(status().isOk());
        mockMvc.perform(get("/drills/1")).andExpect(status().isOk()).andExpect(content().string(containsString("Check answer")));
        mockMvc.perform(get("/drills/10000")).andExpect(status().isOk());
        mockMvc.perform(get("/drills/random")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("/drills/*"));
        mockMvc.perform(get("/drills/10001")).andExpect(status().isNotFound());
    }
}
