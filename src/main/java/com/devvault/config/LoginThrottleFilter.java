package com.devvault.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.function.LongSupplier;

/**
 * Refuses sign-in attempts while the throttle says they are blocked. It runs before Spring Security checks the
 * password, so a blocked client learns nothing about whether the password it tried was right. It is created inside
 * {@link SecurityConfig} and is deliberately not a bean, so it is not also registered as a plain servlet filter.
 */
public class LoginThrottleFilter extends OncePerRequestFilter {

    private final LoginThrottle throttle;
    private final LongSupplier clock;

    public LoginThrottleFilter(LoginThrottle throttle, LongSupplier clock) {
        this.throttle = throttle;
        this.clock = clock;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equals(request.getMethod()) && request.getRequestURI().equals(request.getContextPath() + "/login"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (throttle.isBlocked(request.getRemoteAddr(), request.getParameter("username"), clock.getAsLong())) {
            response.sendRedirect(request.getContextPath() + "/login?locked");
            return;
        }
        chain.doFilter(request, response);
    }
}
