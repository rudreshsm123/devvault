package com.devvault.service;

import com.devvault.entity.AppUser;
import com.devvault.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository users;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(users, encoder);
    }

    @Test
    void register_storesOnlyAHashOfThePassword() {
        when(users.existsByUsernameIgnoreCase("rajesh")).thenReturn(false);
        when(users.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser saved = service.register("rajesh", "correct-horse", "correct-horse");

        assertThat(saved.getUsername()).isEqualTo("rajesh");
        assertThat(saved.getPasswordHash()).isNotEqualTo("correct-horse");
        assertThat(encoder.matches("correct-horse", saved.getPasswordHash())).isTrue();
    }

    @Test
    void register_rejectsDuplicateUsernameIgnoringCase() {
        when(users.existsByUsernameIgnoreCase("Rajesh")).thenReturn(true);

        assertThatThrownBy(() -> service.register("Rajesh", "correct-horse", "correct-horse"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already taken");
        verify(users, never()).save(any());
    }

    @Test
    void register_rejectsWeakOrInvalidInput() {
        assertThatThrownBy(() -> service.register("ab", "correct-horse", "correct-horse"))
                .hasMessageContaining("Username");
        assertThatThrownBy(() -> service.register("has space", "correct-horse", "correct-horse"))
                .hasMessageContaining("Username");
        assertThatThrownBy(() -> service.register("rajesh", "short", "short"))
                .hasMessageContaining("at least");
        assertThatThrownBy(() -> service.register("rajesh", "x".repeat(73), "x".repeat(73)))
                .hasMessageContaining("at most");
        assertThatThrownBy(() -> service.register("rajesh", "Rajesh", "Rajesh"))
                .hasMessageContaining("at least");
        assertThatThrownBy(() -> service.register("rajesh123", "RAJESH123", "RAJESH123"))
                .hasMessageContaining("same as the username");
        assertThatThrownBy(() -> service.register("rajesh", "correct-horse", "different-one"))
                .hasMessageContaining("do not match");
        verify(users, never()).save(any());
    }

    @Test
    void loadUserByUsername_returnsTheStoredHash() {
        when(users.findByUsernameIgnoreCase("rajesh")).thenReturn(Optional.of(new AppUser("rajesh", "hash")));

        UserDetails details = service.loadUserByUsername("rajesh");

        assertThat(details.getUsername()).isEqualTo("rajesh");
        assertThat(details.getPassword()).isEqualTo("hash");
    }

    @Test
    void loadUserByUsername_throwsWhenUnknown() {
        when(users.findByUsernameIgnoreCase("nobody")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("nobody"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
