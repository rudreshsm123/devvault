package com.devvault.controller;

import com.devvault.service.PlanService;
import com.devvault.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.ZoneId;

/** "Today's plan" for signed-in learners. */
@Controller
public class PlanController {

    private final PlanService plans;
    private final UserService users;

    public PlanController(PlanService plans, UserService users) {
        this.plans = plans;
        this.users = users;
    }

    @GetMapping("/plan")
    public String plan(Principal principal, Model model) {
        if (principal != null) {
            Long userId = users.getByUsername(principal.getName()).getId();
            model.addAttribute("plan", plans.today(userId, System.currentTimeMillis(), ZoneId.systemDefault()));
        }
        return "plan";
    }
}
