package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.auth.AuthResponseDto;
import com.jean.servesmart.restaurant.dto.auth.UserLoginDto;
import com.jean.servesmart.restaurant.exception.auth.AuthInvalidDataException;
import com.jean.servesmart.restaurant.exception.auth.InactiveAccountException;
import com.jean.servesmart.restaurant.exception.auth.InvalidCredentialsException;
import com.jean.servesmart.restaurant.model.Role;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.UserRepository;
import com.jean.servesmart.restaurant.service.interfaces.JwtService;
import com.jean.servesmart.restaurant.service.interfaces.LoginLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthImplTest {

    @Mock
    private UserRepository repo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private LoginLogService loginLogService;

    @Mock
    private JwtService jwtService;

    private AuthImpl service;

    @BeforeEach
    void setup() {
        service = new AuthImpl(repo, passwordEncoder, loginLogService, jwtService);
    }

    @Test
    void login_whenDtoIsNull_throwsAuthInvalidDataException() {
        assertThrows(AuthInvalidDataException.class, () -> service.login(null));
        verifyNoInteractions(repo, passwordEncoder, loginLogService, jwtService);
    }

    @Test
    void login_whenEmailIsNull_throwsAuthInvalidDataException() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail(null);
        dto.setPassword("pass");

        assertThrows(AuthInvalidDataException.class, () -> service.login(dto));
        verifyNoInteractions(repo, passwordEncoder, loginLogService, jwtService);
    }

    @Test
    void login_whenEmailIsBlank_throwsAuthInvalidDataException() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("   ");
        dto.setPassword("pass");

        assertThrows(AuthInvalidDataException.class, () -> service.login(dto));
        verifyNoInteractions(repo, passwordEncoder, loginLogService, jwtService);
    }

    @Test
    void login_whenPasswordIsNull_throwsAuthInvalidDataException() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("test@example.com");
        dto.setPassword(null);

        assertThrows(AuthInvalidDataException.class, () -> service.login(dto));
        verifyNoInteractions(repo, passwordEncoder, loginLogService, jwtService);
    }

    @Test
    void login_whenPasswordIsBlank_throwsAuthInvalidDataException() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("test@example.com");
        dto.setPassword("   ");

        assertThrows(AuthInvalidDataException.class, () -> service.login(dto));
        verifyNoInteractions(repo, passwordEncoder, loginLogService, jwtService);
    }

    @Test
    void login_whenUserNotFound_throwsInvalidCredentialsException() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("  test@example.com  ");
        dto.setPassword("admin123");

        when(repo.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> service.login(dto));

        verify(repo).findByEmail("test@example.com");
        verifyNoInteractions(passwordEncoder, loginLogService, jwtService);
    }

    @Test
    void login_whenUserInactive_throwsInactiveAccountException() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("test@example.com");
        dto.setPassword("admin123");

        User user = new User();
        user.setId(10);
        user.setEmail("test@example.com");
        user.setActive(false);

        when(repo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(InactiveAccountException.class, () -> service.login(dto));

        verify(repo).findByEmail("test@example.com");
        verifyNoInteractions(passwordEncoder, loginLogService, jwtService);
    }

    @Test
    void login_whenPasswordDoesNotMatch_throwsInvalidCredentialsException() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("test@example.com");
        dto.setPassword("wrong");

        User user = new User();
        user.setId(10);
        user.setEmail("test@example.com");
        user.setActive(true);
        user.setPasswordHash("$2a$10$hash");

        when(repo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "$2a$10$hash")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> service.login(dto));

        verify(repo).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrong", "$2a$10$hash");
        verifyNoInteractions(loginLogService, jwtService);
    }

    @Test
    void login_whenValid_returnsAuthResponseDto_andLogs_andGeneratesToken() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("  test@example.com  ");
        dto.setPassword("admin123");

        Role role = new Role();
        role.setName("ADMIN");

        User user = new User();
        user.setId(10);
        user.setEmail("test@example.com");
        user.setFirstName("Jean");
        user.setLastName("Baruba");
        user.setAddress("Street 1");
        user.setPhoneNumber("0612345678");
        user.setRole(role);
        user.setActive(true);
        user.setPasswordHash("$2a$10$hash");

        when(repo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("admin123", "$2a$10$hash")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponseDto result = service.login(dto);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertNotNull(result.getUser());
        assertEquals(10, result.getUser().getId());
        assertEquals("test@example.com", result.getUser().getEmail());
        assertEquals("Jean", result.getUser().getFirstName());
        assertEquals("Baruba", result.getUser().getLastName());
        assertEquals("Street 1", result.getUser().getAddress());
        assertEquals("0612345678", result.getUser().getPhoneNumber());
        assertEquals("ADMIN", result.getUser().getRole());
        assertTrue(result.getUser().isActive());

        ArgumentCaptor<com.jean.servesmart.restaurant.dto.loginLog.LoginLogCreateDto> captor =
                ArgumentCaptor.forClass(com.jean.servesmart.restaurant.dto.loginLog.LoginLogCreateDto.class);

        verify(loginLogService).log(captor.capture());
        assertEquals(10, captor.getValue().getUserId());
        assertEquals("LOGIN_SUCCESS", captor.getValue().getStatus());

        verify(repo).findByEmail("test@example.com");
        verify(passwordEncoder).matches("admin123", "$2a$10$hash");
        verify(jwtService).generateToken(user);
        verifyNoMoreInteractions(repo, passwordEncoder, loginLogService, jwtService);
    }

    @Test
    void login_whenRoleIsNull_returnsUserWithNullRole() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("test@example.com");
        dto.setPassword("admin123");

        User user = new User();
        user.setId(10);
        user.setEmail("test@example.com");
        user.setActive(true);
        user.setPasswordHash("$2a$10$hash");
        user.setRole(null);

        when(repo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("admin123", "$2a$10$hash")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponseDto result = service.login(dto);

        assertNotNull(result);
        assertNotNull(result.getUser());
        assertNull(result.getUser().getRole());

        verify(loginLogService).log(any());
        verify(jwtService).generateToken(user);
    }
}
