package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.Auth.UserLoginDto;
import com.jean.servesmart.restaurant.dto.User.UserResponseDto;
import com.jean.servesmart.restaurant.exception.auth.AuthInvalidDataException;
import com.jean.servesmart.restaurant.exception.auth.InactiveAccountException;
import com.jean.servesmart.restaurant.exception.auth.InvalidCredentialsException;
import com.jean.servesmart.restaurant.model.Role;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthImplTest {

    @Mock
    private UserRepository repo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthImpl service;

    private UserLoginDto loginDto(String email, String password) {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    private User user(Integer id, String email, String password, boolean active) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setPasswordHash(password);
        u.setActive(active);
        Role r = new Role();
        r.setName("ADMIN");
        u.setRole(r);
        return u;
    }


    @Test
    void login_shouldThrowInvalidData_whenDtoNull() {
        assertThrows(AuthInvalidDataException.class,
                () -> service.login(null));
    }

    @Test
    void login_shouldThrowInvalidData_whenEmailBlank() {
        UserLoginDto dto = loginDto("   ", "123");
        assertThrows(AuthInvalidDataException.class,
                () -> service.login(dto));
    }

    @Test
    void login_shouldThrowInvalidData_whenPasswordBlank() {
        UserLoginDto dto = loginDto("admin@mail.com", " ");
        assertThrows(AuthInvalidDataException.class,
                () -> service.login(dto));
    }

    @Test
    void login_shouldThrowInvalidCredentials_whenUserNotFound() {
        UserLoginDto dto = loginDto("test@mail.com", "123");

        when(repo.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> service.login(dto));
    }

    @Test
    void login_shouldThrowInactiveAccount_whenUserInactive() {
        UserLoginDto dto = loginDto("test@mail.com", "123");
        User u = user(1, "test@mail.com", "hash", false);

        when(repo.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(u));

        assertThrows(InactiveAccountException.class,
                () -> service.login(dto));
    }

    @Test
    void login_shouldThrowInvalidCredentials_whenPasswordIncorrect() {
        UserLoginDto dto = loginDto("test@mail.com", "wrong");
        User u = user(1, "test@mail.com", "hashed", true);

        when(repo.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(u));

        when(passwordEncoder.matches("wrong", "hashed"))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> service.login(dto));
    }

    @Test
    void login_shouldReturnUserResponse_whenValidCredentials() {
        UserLoginDto dto = loginDto("test@mail.com", "secret");
        User u = user(1, "test@mail.com", "hashed", true);

        when(repo.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(u));

        when(passwordEncoder.matches("secret", "hashed"))
                .thenReturn(true);

        UserResponseDto result = service.login(dto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("test@mail.com", result.getEmail());
    }
}
