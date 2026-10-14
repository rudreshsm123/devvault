package com.devvault.controller;

import com.devvault.config.SecurityConfig;
import com.devvault.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Browsers always ask for /favicon.ico; a missing static file must be a 404, never a 500. */
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class MissingResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void aMissingStaticFileIsA404WithAJsonBody() throws Exception {
        mockMvc.perform(get("/favicon.ico"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        mockMvc.perform(get("/css/does-not-exist.css"))
                .andExpect(status().isNotFound());
    }
}
