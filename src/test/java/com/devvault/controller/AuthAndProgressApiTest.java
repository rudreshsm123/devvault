package com.devvault.controller;

import com.devvault.config.CurrentUserAdvice;
import com.devvault.config.SecurityConfig;
import com.devvault.entity.AppUser;
import com.devvault.entity.UserProgress;
import com.devvault.service.ProgressService;
import com.devvault.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({AuthController.class, ProgressApiController.class})
@Import({SecurityConfig.class, CurrentUserAdvice.class})
class AuthAndProgressApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ProgressService progressService;

    @Test
    void loginPageIsPublic() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void register_withValidDataRedirectsToLogin() throws Exception {
        when(userService.register("rajesh", "correct-horse", "correct-horse")).thenReturn(new AppUser("rajesh", "h"));

        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "rajesh").param("password", "correct-horse")
                        .param("confirmPassword", "correct-horse"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    void register_showsTheErrorAndKeepsTheUsername() throws Exception {
        when(userService.register(any(), any(), any())).thenThrow(new IllegalArgumentException("That username is already taken."));

        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "rajesh").param("password", "correct-horse")
                        .param("confirmPassword", "correct-horse"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model()
                        .attribute("error", "That username is already taken."));
    }

    @Test
    void register_withoutCsrfTokenIsRejected() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "rajesh").param("password", "correct-horse")
                        .param("confirmPassword", "correct-horse"))
                .andExpect(status().isForbidden());

        verify(userService, never()).register(any(), any(), any());
    }

    @Test
    void progressApi_requiresSignIn() throws Exception {
        mockMvc.perform(get("/api/me/progress"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "rajesh")
    void progressApi_listsTheUsersFinishedItems() throws Exception {
        when(userService.getByUsername("rajesh")).thenReturn(userWithId(4L));
        when(progressService.list(4L)).thenReturn(List.of(
                new UserProgress(4L, "problem", 12L, "Two Sum", "/problems/12", 1000L)));

        mockMvc.perform(get("/api/me/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problem['12'].t").value("Two Sum"))
                .andExpect(jsonPath("$.problem['12'].u").value("/problems/12"))
                .andExpect(jsonPath("$.lesson").isEmpty());
    }

    @Test
    @WithMockUser(username = "rajesh")
    void progressApi_toggleReturnsTheNewState() throws Exception {
        when(userService.getByUsername("rajesh")).thenReturn(userWithId(4L));
        when(progressService.toggle(eq(4L), eq("lesson"), eq(3L), eq("Loops"), eq("/learn/3"))).thenReturn(true);

        mockMvc.perform(post("/api/me/progress/toggle").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"kind\":\"lesson\",\"id\":3,\"title\":\"Loops\",\"url\":\"/learn/3\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    @WithMockUser(username = "rajesh")
    void progressApi_badInputBecomesBadRequest() throws Exception {
        when(userService.getByUsername("rajesh")).thenReturn(userWithId(4L));
        when(progressService.toggle(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("url must be a path on this site"));

        mockMvc.perform(post("/api/me/progress/toggle").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"kind\":\"lesson\",\"id\":3,\"title\":\"x\",\"url\":\"https://evil\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("url must be a path on this site"));
    }

    @Test
    @WithMockUser(username = "rajesh")
    void progressApi_toggleWithoutCsrfIsRejected() throws Exception {
        mockMvc.perform(post("/api/me/progress/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"kind\":\"lesson\",\"id\":3,\"title\":\"x\",\"url\":\"/learn/3\"}"))
                .andExpect(status().isForbidden());
    }

    private static AppUser userWithId(Long id) {
        AppUser user = new AppUser("rajesh", "hash");
        user.setId(id);
        return user;
    }
}
