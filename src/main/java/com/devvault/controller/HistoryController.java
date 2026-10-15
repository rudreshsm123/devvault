package com.devvault.controller;

import com.devvault.service.HistoryService;
import com.devvault.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/** "Answer History": the signed-in user's recent drill and problem answers, right or wrong. */
@Controller
public class HistoryController {

    private final HistoryService history;
    private final UserService users;

    public HistoryController(HistoryService history, UserService users) {
        this.history = history;
        this.users = users;
    }

    @GetMapping("/history")
    public String page(Principal principal, Model model) {
        if (principal != null) {
            Long userId = users.getByUsername(principal.getName()).getId();
            model.addAttribute("entries", history.recent(userId));
        }
        return "history";
    }
}
