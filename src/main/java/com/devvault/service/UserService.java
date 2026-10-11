package com.devvault.service;

import com.devvault.entity.AppUser;
import com.devvault.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/** Registers learners and lets Spring Security load them by username at login. */
@Service
public class UserService implements UserDetailsService {

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,30}$");

    /** BCrypt only reads the first 72 bytes, so longer passwords would silently be truncated. */
    static final int MAX_PASSWORD_LENGTH = 72;
    static final int MIN_PASSWORD_LENGTH = 8;

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates an account.
     *
     * @throws IllegalArgumentException with a message that is safe to show to the user
     */
    public AppUser register(String username, String rawPassword, String confirmPassword) {
        String name = username == null ? "" : username.trim();
        if (!USERNAME.matcher(name).matches()) {
            throw new IllegalArgumentException("Username must be 3 to 30 characters: letters, digits or underscore.");
        }
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (rawPassword.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at most " + MAX_PASSWORD_LENGTH + " characters.");
        }
        if (rawPassword.equalsIgnoreCase(name)) {
            throw new IllegalArgumentException("Password must not be the same as the username.");
        }
        if (!rawPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("The two passwords do not match.");
        }
        if (users.existsByUsernameIgnoreCase(name)) {
            throw new IllegalArgumentException("That username is already taken.");
        }
        return users.save(new AppUser(name, passwordEncoder.encode(rawPassword)));
    }

    public AppUser getByUsername(String username) {
        return users.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = getByUsername(username);
        return new User(user.getUsername(), user.getPasswordHash(), List.of());
    }
}
