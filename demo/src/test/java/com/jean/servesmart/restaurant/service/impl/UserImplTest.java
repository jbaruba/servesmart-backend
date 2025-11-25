package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.User.UserRegisterDto;
import com.jean.servesmart.restaurant.dto.User.UserResponseDto;
import com.jean.servesmart.restaurant.exception.user.UserEmailAlreadyUsedException;
import com.jean.servesmart.restaurant.exception.user.UserInvalidDataException;
import com.jean.servesmart.restaurant.model.Role;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.RoleRepository;
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
class UserImplTest {

    @Mock
    private UserRepository repo;

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserImpl service;

    private UserRegisterDto regDto(String email, String pass, String role) {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail(email);
        dto.setPassword(pass);
        dto.setFirstName("Jean");
        dto.setLastName("Test");
        dto.setRole(role);
        dto.setActive(true);
        return dto;
    }

    // register

    @Test
    void register_shouldThrowInvalid_whenDtoNull() {
        assertThrows(UserInvalidDataException.class,
                () -> service.register(null));
    }

    @Test
    void register_shouldThrowInvalid_whenEmailBlank() {
        UserRegisterDto dto = regDto(" ", "123", "ADMIN");
        assertThrows(UserInvalidDataException.class,
                () -> service.register(dto));
    }

    @Test
    void register_shouldThrowInvalid_whenPasswordBlank() {
        UserRegisterDto dto = regDto("mail@mail.com", " ", "ADMIN");
        assertThrows(UserInvalidDataException.class,
                () -> service.register(dto));
    }

    @Test
    void register_shouldThrowInvalid_whenRoleBlank() {
        UserRegisterDto dto = regDto("mail@mail.com", "123", " ");
        assertThrows(UserInvalidDataException.class,
                () -> service.register(dto));
    }

    @Test
    void register_shouldThrowEmailExists_whenEmailUsed() {
        UserRegisterDto dto = regDto("mail@mail.com", "123", "ADMIN");

        when(repo.existsByEmail("mail@mail.com")).thenReturn(true);

        assertThrows(UserEmailAlreadyUsedException.class,
                () -> service.register(dto));
    }

    @Test
    void register_shouldThrowInvalid_whenRoleNotFound() {
        UserRegisterDto dto = regDto("mail@mail.com", "123", "ADMIN");

        when(repo.existsByEmail("mail@mail.com")).thenReturn(false);
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.empty());

        assertThrows(UserInvalidDataException.class,
                () -> service.register(dto));
    }

    @Test
    void register_shouldCreateAndReturnDto_whenValid() {
        UserRegisterDto dto = regDto("mail@mail.com", "123", "ADMIN");

        when(repo.existsByEmail("mail@mail.com")).thenReturn(false);

        Role role = new Role();
        role.setName("ADMIN");
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.of(role));

        when(encoder.encode("123")).thenReturn("hashed-password");

        User saved = new User();
        saved.setId(10);
        saved.setEmail("mail@mail.com");
        saved.setPasswordHash("hashed-password");
        saved.setRole(role);
        saved.setFirstName("Jean");
        saved.setLastName("Test");
        saved.setActive(true);

        when(repo.save(any(User.class))).thenReturn(saved);


        UserResponseDto result = service.register(dto);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("mail@mail.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
        assertTrue(result.isActive());

        verify(repo).existsByEmail("mail@mail.com");
        verify(roleRepo).findByName("ADMIN");
        verify(encoder).encode("123");
        verify(repo).save(any(User.class));
    }
}
