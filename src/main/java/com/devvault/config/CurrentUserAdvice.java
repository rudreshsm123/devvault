package com.devvault.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/** Makes the signed-in username (or null) available to every page so the sidebar can show it. */
@ControllerAdvice
public class CurrentUserAdvice {

    @ModelAttribute("currentUser")
    public String currentUser(Principal principal) {
        return principal == null ? null : principal.getName();
    }
}
