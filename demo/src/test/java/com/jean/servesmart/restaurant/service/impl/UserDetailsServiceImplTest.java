package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.model.Role;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepo;

    private UserDetailsServiceImpl service;

    @BeforeEach
    void setup() {
        service = new UserDetailsServiceImpl(userRepo);
    }

    @Test
    void loadUserByUsername_whenUserNotFound_throwsUsernameNotFoundException() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("test@example.com"));

        verify(userRepo).findByEmail("test@example.com");
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void loadUserByUsername_whenRoleIsNull_returnsUserDetailsWithoutAuthorities() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");
        user.setActive(true);
        user.setRole(null);

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("test@example.com");

        assertNotNull(details);
        assertEquals("test@example.com", details.getUsername());
        assertEquals("hash", details.getPassword());
        assertTrue(details.isEnabled());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
        assertNotNull(details.getAuthorities());
        assertEquals(0, details.getAuthorities().size());

        verify(userRepo).findByEmail("test@example.com");
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void loadUserByUsername_whenRoleIsBlank_returnsUserDetailsWithoutAuthorities() {
        Role role = new Role();
        role.setName("   ");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");
        user.setActive(true);
        user.setRole(role);

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("test@example.com");

        assertNotNull(details);
        assertEquals(0, details.getAuthorities().size());

        verify(userRepo).findByEmail("test@example.com");
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void loadUserByUsername_whenRolePresent_addsRoleAuthorityUppercased() {
        Role role = new Role();
        role.setName("admin");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");
        user.setActive(true);
        user.setRole(role);

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("test@example.com");

        assertNotNull(details);
        assertEquals(1, details.getAuthorities().size());
        assertEquals("ROLE_ADMIN", details.getAuthorities().iterator().next().getAuthority());

        verify(userRepo).findByEmail("test@example.com");
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void loadUserByUsername_whenUserInactive_setsEnabledFalse() {
        Role role = new Role();
        role.setName("ADMIN");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");
        user.setActive(false);
        user.setRole(role);

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("test@example.com");

        assertNotNull(details);
        assertFalse(details.isEnabled());
        assertEquals(1, details.getAuthorities().size());
        assertEquals("ROLE_ADMIN", details.getAuthorities().iterator().next().getAuthority());

        verify(userRepo).findByEmail("test@example.com");
        verifyNoMoreInteractions(userRepo);
    }
}
