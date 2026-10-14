package com.devvault.controller;

import com.devvault.service.InsightsService;
import com.devvault.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.ZoneId;

/** "Review & Insights": what is due for review today and which topics the signed-in user is weak at. */
@Controller
public class InsightsController {

    private final InsightsService insights;
    private final UserService users;

    public InsightsController(InsightsService insights, UserService users) {
        this.insights = insights;
        this.users = users;
    }

    @GetMapping("/insights")
    public String page(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("insights", insightsFor(principal));
        }
        return "insights";
    }

    /** Sends the user to the most overdue item, or back to the insights page when nothing is due. */
    @GetMapping("/review/next")
    public String next(Principal principal) {
        if (principal == null) {
            return "redirect:/insights";
        }
        Long userId = users.getByUsername(principal.getName()).getId();
        return insights.nextDue(userId, System.currentTimeMillis())
                .map(item -> "redirect:" + item.url())
                .orElse("redirect:/insights");
    }

    @GetMapping("/api/me/insights")
    @ResponseBody
    public InsightsService.Insights json(Principal principal) {
        return insightsFor(principal);
    }

    private InsightsService.Insights insightsFor(Principal principal) {
        Long userId = users.getByUsername(principal.getName()).getId();
        return insights.forUser(userId, System.currentTimeMillis(), ZoneId.systemDefault());
    }
}
