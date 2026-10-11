package com.devvault.controller;

import com.devvault.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

/** Sign-in page (the form post is handled by Spring Security) and account registration. */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Principal principal) {
        return principal != null ? "redirect:/progress" : "login";
    }

    @GetMapping("/register")
    public String registerForm(Principal principal) {
        return principal != null ? "redirect:/progress" : "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password,
                           @RequestParam String confirmPassword, Model model) {
        try {
            userService.register(username, password, confirmPassword);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            return "register";
        }
        return "redirect:/login?registered";
    }
}
