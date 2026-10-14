package com.devvault.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Accounts are optional: everyone can browse, only the personal progress API needs a signed-in user.
 * CSRF protection stays on for every form and for the progress API. The older REST endpoints for notes,
 * problems, resources and snippets are exempt so existing API clients (Postman, tests) keep working.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Limits password guessing: too many failed sign-ins from one address are refused for a while. */
    @Bean
    public LoginThrottle loginThrottle(
            @Value("${devvault.security.login.max-failures:5}") int maxFailures,
            @Value("${devvault.security.login.max-failures-per-address:30}") int maxFailuresPerAddress,
            @Value("${devvault.security.login.window-minutes:15}") int windowMinutes) {
        return new LoginThrottle(maxFailures, maxFailuresPerAddress, windowMinutes * 60_000L);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginThrottle throttle) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/me/**", "/interview/start").authenticated()
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            throttle.recordSuccess(request.getRemoteAddr(), request.getParameter("username"));
                            response.sendRedirect(request.getContextPath() + "/progress");
                        })
                        .failureHandler((request, response, exception) -> {
                            throttle.recordFailure(request.getRemoteAddr(), request.getParameter("username"),
                                    System.currentTimeMillis());
                            response.sendRedirect(request.getContextPath() + "/login?error");
                        })
                        .permitAll())
                // Runs before the password is checked, so a blocked client learns nothing from the answer.
                .addFilterBefore(new LoginThrottleFilter(throttle, System::currentTimeMillis),
                        UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/api/notes/**", "/api/problems/**", "/api/resources/**", "/api/snippets/**", "/api/drills/**"))
                // API callers get a plain 401 instead of being redirected to the HTML login page.
                .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        request -> request.getRequestURI().startsWith("/api/")));
        return http.build();
    }
}
