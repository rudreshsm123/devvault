package com.devvault.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Public demo mode (devvault.readonly=true): visitors can browse everything, practise problems, create an account
 * and save their own progress, but cannot create, edit or delete content.
 */
@Component
@ConditionalOnProperty(name = "devvault.readonly", havingValue = "true")
public class ReadOnlyFilter extends OncePerRequestFilter {

    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS");
    /** Practice attempts, creating an account and a signed-in user's own progress are still allowed. */
    private static final Pattern ALLOWED_WRITES = Pattern.compile(
            "^(/problems/[0-9]+/attempt(/clear)?|/register|/api/me/progress(/toggle|/import|/result)?|/api/me/ai/explain|/api/judge/java|/api/drills/[0-9]+/check)$");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (SAFE_METHODS.contains(request.getMethod()) || ALLOWED_WRITES.matcher(request.getRequestURI()).matches()) {
            chain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<!DOCTYPE html><html><body style=\"font-family:sans-serif;max-width:40rem;margin:4rem auto\">"
                + "<h2>Read-only demo</h2><p>This public copy of DevVault is read-only, so changes are disabled.</p>"
                + "<p><a href=\"/\">&larr; Back to DevVault</a></p></body></html>");
    }
}
