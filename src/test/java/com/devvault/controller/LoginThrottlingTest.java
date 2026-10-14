package com.devvault.controller;

import com.devvault.config.CurrentUserAdvice;
import com.devvault.config.SecurityConfig;
import com.devvault.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The real security chain, with a fake user store: wrong passwords must lock sign-in, right ones must reset it. */
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, CurrentUserAdvice.class, LoginThrottlingTest.Providers.class})
@TestPropertySource(properties = "devvault.security.login.max-failures-per-address=1000")
class LoginThrottlingTest {

    private static final String PASSWORD = "correct-horse-1";

    /** The web-layer test slice does not build a sign-in provider from the mocked user store, so give it one. */
    @TestConfiguration
    static class Providers {
        @Bean
        AuthenticationProvider authenticationProvider(UserService userService, PasswordEncoder encoder) {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
            provider.setPasswordEncoder(encoder);
            return provider;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder encoder;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        doThrow(new UsernameNotFoundException("Unknown user")).when(userService).loadUserByUsername(anyString());
        for (String name : List.of("locky", "okay", "okay2", "bystander")) {
            // A new object per call, like the real service: Spring Security wipes the password of the one it was given.
            doAnswer(call -> new User(name, encoder.encode(PASSWORD), List.of())).when(userService).loadUserByUsername(name);
        }
    }

    private org.springframework.test.web.servlet.ResultActions login(String user, String password) throws Exception {
        return mockMvc.perform(post("/login").with(csrf()).param("username", user).param("password", password));
    }

    @Test
    void afterFiveWrongPasswordsEvenTheRightPasswordIsRefused() throws Exception {
        for (int i = 0; i < 5; i++) {
            login("locky", "wrong-" + i).andExpect(redirectedUrl("/login?error"));
        }

        login("locky", PASSWORD).andExpect(redirectedUrl("/login?locked"));
    }

    @Test
    void aSuccessfulSignInResetsTheCount() throws Exception {
        for (int i = 0; i < 3; i++) {
            login("okay", "wrong").andExpect(redirectedUrl("/login?error"));
        }
        login("okay", PASSWORD).andExpect(redirectedUrl("/progress"));

        for (int i = 0; i < 4; i++) {
            login("okay", "wrong").andExpect(redirectedUrl("/login?error"));
        }
        login("okay", PASSWORD).andExpect(redirectedUrl("/progress"));
    }

    @Test
    void guessingAnUnknownUsernameIsThrottledAndLooksTheSameAsAWrongPassword() throws Exception {
        for (int i = 0; i < 5; i++) {
            login("ghost", "whatever").andExpect(redirectedUrl("/login?error"));
        }

        login("ghost", "whatever").andExpect(redirectedUrl("/login?locked"));
    }

    @Test
    void lockingOneUserDoesNotStopAnotherUserFromSigningIn() throws Exception {
        for (int i = 0; i < 5; i++) {
            login("okay2", "wrong").andExpect(redirectedUrl("/login?error"));
        }
        login("okay2", PASSWORD).andExpect(redirectedUrl("/login?locked"));

        login("bystander", PASSWORD).andExpect(redirectedUrl("/progress"));
    }

    @Test
    void theLoginPageStillLoadsAndExplainsALockout() throws Exception {
        mockMvc.perform(get("/login?locked"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Too many failed attempts")));
        mockMvc.perform(get("/login?error"))
                .andExpect(content().string(containsString("Wrong username or password.")));
    }
}
