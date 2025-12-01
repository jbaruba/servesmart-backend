package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.User.ChangePasswordDto;
import com.jean.servesmart.restaurant.dto.User.UserRegisterDto;
import com.jean.servesmart.restaurant.dto.User.UserResponseDto;
import com.jean.servesmart.restaurant.dto.User.UserUpdateDto;
import com.jean.servesmart.restaurant.exception.user.InvalidPasswordChangeException;
import com.jean.servesmart.restaurant.exception.user.UserEmailAlreadyUsedException;
import com.jean.servesmart.restaurant.exception.user.UserInvalidDataException;
import com.jean.servesmart.restaurant.exception.user.UserNotFoundException;
import com.jean.servesmart.restaurant.model.Role;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.RoleRepository;
import com.jean.servesmart.restaurant.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
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

    // ===== Helpers =====

    private UserRegisterDto regDto(String email, String pass, String roleName) {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail(email);
        dto.setPassword(pass);
        dto.setFirstName("Jean");
        dto.setLastName("Tester");
        dto.setAddress("Street 1");
        dto.setPhoneNumber("12345");
        dto.setRole(roleName);
        dto.setActive(true);
        return dto;
    }

    private UserUpdateDto updateDto(String email, String first, String last,
                                   String address, String phone, Boolean active, String role) {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail(email);
        dto.setFirstName(first);
        dto.setLastName(last);
        dto.setAddress(address);
        dto.setPhoneNumber(phone);
        dto.setActive(active);
        dto.setRole(role);
        return dto;
    }

    private ChangePasswordDto cpDto(String oldPass, String newPass) {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword(oldPass);
        dto.setNewPassword(newPass);
        return dto;
    }

    private Role role(String name) {
        Role r = new Role();
        r.setName(name);
        return r;
    }

    private User user(Integer id, String email, String hash, Role role, boolean active) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setPasswordHash(hash);
        u.setFirstName("Jean");
        u.setLastName("Tester");
        u.setAddress("Street 1");
        u.setPhoneNumber("12345");
        u.setRole(role);
        u.setActive(active);
        return u;
    }

    // ===== register() tests =====

    @Test
    void register_shouldThrowInvalid_whenDtoNull() {
        assertThrows(UserInvalidDataException.class,
                () -> service.register(null));
    }

    @Test
    void register_shouldThrowInvalid_whenEmailBlank() {
        UserRegisterDto dto = regDto("   ", "pw", "ADMIN");
        assertThrows(UserInvalidDataException.class,
                () -> service.register(dto));
    }

    @Test
    void register_shouldThrowInvalid_whenPasswordBlank() {
        UserRegisterDto dto = regDto("mail@mail.com", "  ", "ADMIN");
        assertThrows(UserInvalidDataException.class,
                () -> service.register(dto));
    }

    @Test
    void register_shouldThrowInvalid_whenRoleBlank() {
        UserRegisterDto dto = regDto("mail@mail.com", "pw", " ");
        assertThrows(UserInvalidDataException.class,
                () -> service.register(dto));
    }

    @Test
    void register_shouldThrowEmailExists_whenEmailAlreadyUsed() {
        UserRegisterDto dto = regDto("mail@mail.com", "pw", "ADMIN");

        when(repo.existsByEmail("mail@mail.com")).thenReturn(true);

        assertThrows(UserEmailAlreadyUsedException.class,
                () -> service.register(dto));

        verify(repo).existsByEmail("mail@mail.com");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void register_shouldThrowInvalid_whenRoleNotFound() {
        UserRegisterDto dto = regDto("mail@mail.com", "pw", "ADMIN");

        when(repo.existsByEmail("mail@mail.com")).thenReturn(false);
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.empty());

        assertThrows(UserInvalidDataException.class,
                () -> service.register(dto));

        verify(repo).existsByEmail("mail@mail.com");
        verify(roleRepo).findByName("ADMIN");
    }

    @Test
    void register_shouldCreateUserAndReturnDto_whenValid() {
        UserRegisterDto dto = regDto("mail@mail.com", "pw", "ADMIN");

        when(repo.existsByEmail("mail@mail.com")).thenReturn(false);

        Role admin = role("ADMIN");
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.of(admin));

        when(encoder.encode("pw")).thenReturn("hashed");

        User saved = user(10, "mail@mail.com", "hashed", admin, true);
        when(repo.save(any(User.class))).thenReturn(saved);

        UserResponseDto result = service.register(dto);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("mail@mail.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
        assertTrue(result.isActive());

        verify(repo).existsByEmail("mail@mail.com");
        verify(roleRepo).findByName("ADMIN");
        verify(encoder).encode("pw");
        verify(repo).save(any(User.class));
    }

    // ===== getById() tests =====

    @Test
    void getById_shouldThrowInvalid_whenIdNull() {
        assertThrows(UserInvalidDataException.class,
                () -> service.getById(null));
    }

    @Test
    void getById_shouldReturnEmpty_whenUserNotFound() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        Optional<UserResponseDto> result = service.getById(99);

        assertTrue(result.isEmpty());
        verify(repo).findById(99);
    }

    @Test
    void getById_shouldReturnDto_whenUserFound() {
        Role r = role("ADMIN");
        User u = user(1, "mail@mail.com", "hash", r, true);

        when(repo.findById(1)).thenReturn(Optional.of(u));

        Optional<UserResponseDto> result = service.getById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("mail@mail.com", result.get().getEmail());
        assertEquals("ADMIN", result.get().getRole());

        verify(repo).findById(1);
    }

    // ===== getAll() tests =====

    @Test
    void getAll_shouldReturnMappedList() {
        Role admin = role("ADMIN");
        Role staff = role("STAFF");
        User u1 = user(1, "a@mail.com", "h1", admin, true);
        User u2 = user(2, "b@mail.com", "h2", staff, false);

        when(repo.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<UserResponseDto> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("a@mail.com", result.get(0).getEmail());
        assertEquals("ADMIN", result.get(0).getRole());
        assertEquals("b@mail.com", result.get(1).getEmail());
        assertEquals("STAFF", result.get(1).getRole());

        verify(repo).findAll();
    }

    // ===== updateProfile() tests =====

    @Test
    void updateProfile_shouldThrowInvalid_whenIdNull() {
        UserUpdateDto dto = updateDto("x@mail.com", null, null, null, null, null, null);

        assertThrows(UserInvalidDataException.class,
                () -> service.updateProfile(null, dto));
    }

    @Test
    void updateProfile_shouldThrowInvalid_whenDtoNull() {
        assertThrows(UserInvalidDataException.class,
                () -> service.updateProfile(1, null));
    }

    @Test
    void updateProfile_shouldThrowNotFound_whenUserNotFound() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        UserUpdateDto dto = updateDto("x@mail.com", null, null, null, null, null, null);

        assertThrows(UserNotFoundException.class,
                () -> service.updateProfile(99, dto));

        verify(repo).findById(99);
    }

    @Test
    void updateProfile_shouldThrowInvalid_whenNewEmailBlank() {
        Role admin = role("ADMIN");
        User existing = user(1, "old@mail.com", "h", admin, true);

        when(repo.findById(1)).thenReturn(Optional.of(existing));

        UserUpdateDto dto = updateDto("   ", null, null, null, null, null, null);

        assertThrows(UserInvalidDataException.class,
                () -> service.updateProfile(1, dto));

        verify(repo).findById(1);
    }

    @Test
    void updateProfile_shouldThrowEmailExists_whenEmailUsedByOther() {
        Role admin = role("ADMIN");
        User existing = user(1, "old@mail.com", "h", admin, true);

        when(repo.findById(1)).thenReturn(Optional.of(existing));
        when(repo.existsByEmail("new@mail.com")).thenReturn(true);

        UserUpdateDto dto = updateDto("new@mail.com", null, null, null, null, null, null);

        assertThrows(UserEmailAlreadyUsedException.class,
                () -> service.updateProfile(1, dto));

        verify(repo).findById(1);
        verify(repo).existsByEmail("new@mail.com");
    }

    @Test
    void updateProfile_shouldThrowInvalid_whenRoleBlank() {
        Role admin = role("ADMIN");
        User existing = user(1, "old@mail.com", "h", admin, true);

        when(repo.findById(1)).thenReturn(Optional.of(existing));

        UserUpdateDto dto = updateDto(null, null, null, null, null, null, "  ");

        assertThrows(UserInvalidDataException.class,
                () -> service.updateProfile(1, dto));

        verify(repo).findById(1);
        verifyNoInteractions(roleRepo);
    }

    @Test
    void updateProfile_shouldThrowInvalid_whenRoleNotFound() {
        Role admin = role("ADMIN");
        User existing = user(1, "old@mail.com", "h", admin, true);

        when(repo.findById(1)).thenReturn(Optional.of(existing));
        when(roleRepo.findByName("MANAGER")).thenReturn(Optional.empty());

        UserUpdateDto dto = updateDto(null, null, null, null, null, null, "MANAGER");

        assertThrows(UserInvalidDataException.class,
                () -> service.updateProfile(1, dto));

        verify(repo).findById(1);
        verify(roleRepo).findByName("MANAGER");
    }

    @Test
    void updateProfile_shouldUpdateFieldsAndRole_whenValid() {
        Role admin = role("ADMIN");
        Role staff = role("STAFF");

        User existing = user(1, "old@mail.com", "h", admin, true);

        when(repo.findById(1)).thenReturn(Optional.of(existing));
        when(repo.existsByEmail("new@mail.com")).thenReturn(false);
        when(roleRepo.findByName("STAFF")).thenReturn(Optional.of(staff));

        UserUpdateDto dto = updateDto(
                "new@mail.com",
                "NewFirst",
                "NewLast",
                "New Street",
                "999",
                false,
                "STAFF"
        );

        User updatedEntity = user(1, "new@mail.com", "h", staff, false);
        updatedEntity.setFirstName("NewFirst");
        updatedEntity.setLastName("NewLast");
        updatedEntity.setAddress("New Street");
        updatedEntity.setPhoneNumber("999");

        when(repo.save(any(User.class))).thenReturn(updatedEntity);

        UserResponseDto result = service.updateProfile(1, dto);

        assertEquals(1, result.getId());
        assertEquals("new@mail.com", result.getEmail());
        assertEquals("NewFirst", result.getFirstName());
        assertEquals("NewLast", result.getLastName());
        assertEquals("New Street", result.getAddress());
        assertEquals("999", result.getPhoneNumber());
        assertEquals("STAFF", result.getRole());
        assertFalse(result.isActive());

        verify(repo).findById(1);
        verify(repo).existsByEmail("new@mail.com");
        verify(roleRepo).findByName("STAFF");
        verify(repo).save(any(User.class));
    }

    // ===== changePassword() tests =====

    @Test
    void changePassword_shouldThrowInvalid_whenIdNull() {
        ChangePasswordDto dto = cpDto("old", "new");
        assertThrows(UserInvalidDataException.class,
                () -> service.changePassword(null, dto));
    }

    @Test
    void changePassword_shouldThrowInvalid_whenDtoNull() {
        assertThrows(UserInvalidDataException.class,
                () -> service.changePassword(1, null));
    }

    @Test
    void changePassword_shouldThrowInvalid_whenOldBlank() {
        ChangePasswordDto dto = cpDto("  ", "new");
        assertThrows(UserInvalidDataException.class,
                () -> service.changePassword(1, dto));
    }

    @Test
    void changePassword_shouldThrowInvalid_whenNewBlank() {
        ChangePasswordDto dto = cpDto("old", "  ");
        assertThrows(UserInvalidDataException.class,
                () -> service.changePassword(1, dto));
    }

    @Test
    void changePassword_shouldThrowNotFound_whenUserNotFound() {
        ChangePasswordDto dto = cpDto("old", "new");

        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.changePassword(99, dto));

        verify(repo).findById(99);
    }

    @Test
    void changePassword_shouldThrowInvalidPassword_whenOldDoesNotMatch() {
        Role admin = role("ADMIN");
        User u = user(1, "mail@mail.com", "hash", admin, true);

        ChangePasswordDto dto = cpDto("old", "new");

        when(repo.findById(1)).thenReturn(Optional.of(u));
        when(encoder.matches("old", "hash")).thenReturn(false);

        assertThrows(InvalidPasswordChangeException.class,
                () -> service.changePassword(1, dto));

        verify(repo).findById(1);
        verify(encoder).matches("old", "hash");
    }

    @Test
    void changePassword_shouldThrowInvalidPassword_whenNewEqualsOldHash() {
        Role admin = role("ADMIN");
        User u = user(1, "mail@mail.com", "hash", admin, true);

        ChangePasswordDto dto = cpDto("old", "new");

        when(repo.findById(1)).thenReturn(Optional.of(u));
        when(encoder.matches("old", "hash")).thenReturn(true);
        when(encoder.matches("new", "hash")).thenReturn(true);

        assertThrows(InvalidPasswordChangeException.class,
                () -> service.changePassword(1, dto));

        verify(repo).findById(1);
        verify(encoder).matches("old", "hash");
        verify(encoder).matches("new", "hash");
    }

    @Test
    void changePassword_shouldUpdatePassword_whenValid() {
        Role admin = role("ADMIN");
        User u = user(1, "mail@mail.com", "hash", admin, true);

        ChangePasswordDto dto = cpDto("old", "new");

        when(repo.findById(1)).thenReturn(Optional.of(u));
        when(encoder.matches("old", "hash")).thenReturn(true);
        when(encoder.matches("new", "hash")).thenReturn(false);
        when(encoder.encode("new")).thenReturn("newHash");

        // we willen checken welke user naar save gaat
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        boolean result = service.changePassword(1, dto);

        assertTrue(result);

        verify(repo).findById(1);
        verify(encoder).matches("old", "hash");
        verify(encoder).matches("new", "hash");
        verify(encoder).encode("new");

        verify(repo).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals("newHash", savedUser.getPasswordHash());
    }

    // ===== emailExists() tests =====

    @Test
    void emailExists_shouldThrowInvalid_whenEmailBlank() {
        assertThrows(UserInvalidDataException.class,
                () -> service.emailExists(" "));
    }

    @Test
    void emailExists_shouldReturnRepoResult_whenEmailValid() {
        when(repo.existsByEmail("test@mail.com")).thenReturn(true);

        boolean result = service.emailExists("  test@mail.com  ");

        assertTrue(result);
        verify(repo).existsByEmail("test@mail.com");
    }

    // ===== deleteUser() tests =====

    @Test
    void deleteUser_shouldThrowInvalid_whenIdNull() {
        assertThrows(UserInvalidDataException.class,
                () -> service.deleteUser(null));
    }

    @Test
    void deleteUser_shouldThrowNotFound_whenUserNotExist() {
        when(repo.existsById(99)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> service.deleteUser(99));

        verify(repo).existsById(99);
        verify(repo, never()).deleteById(anyInt());
    }

    @Test
    void deleteUser_shouldDelete_whenUserExists() {
        when(repo.existsById(1)).thenReturn(true);

        service.deleteUser(1);

        verify(repo).existsById(1);
        verify(repo).deleteById(1);
    }
}
