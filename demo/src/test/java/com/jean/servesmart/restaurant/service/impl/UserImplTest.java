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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    private BCryptPasswordEncoder passwordEncoder;

    private UserImpl service;

    @BeforeEach
    void setup() {
        service = new UserImpl(repo, roleRepo, passwordEncoder);
    }

    @Test
    void register_whenDtoIsNull_throwsUserInvalidDataException() {
        assertThrows(UserInvalidDataException.class, () -> service.register(null));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void register_whenEmailIsNull_throwsUserInvalidDataException() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail(null);
        dto.setPassword("pass");
        dto.setRole("ADMIN");

        assertThrows(UserInvalidDataException.class, () -> service.register(dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void register_whenEmailIsBlank_throwsUserInvalidDataException() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("   ");
        dto.setPassword("pass");
        dto.setRole("ADMIN");

        assertThrows(UserInvalidDataException.class, () -> service.register(dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void register_whenPasswordIsNull_throwsUserInvalidDataException() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword(null);
        dto.setRole("ADMIN");

        assertThrows(UserInvalidDataException.class, () -> service.register(dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void register_whenPasswordIsBlank_throwsUserInvalidDataException() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("   ");
        dto.setRole("ADMIN");

        assertThrows(UserInvalidDataException.class, () -> service.register(dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void register_whenRoleIsNull_throwsUserInvalidDataException() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("pass");
        dto.setRole(null);

        assertThrows(UserInvalidDataException.class, () -> service.register(dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void register_whenRoleIsBlank_throwsUserInvalidDataException() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("pass");
        dto.setRole("   ");

        assertThrows(UserInvalidDataException.class, () -> service.register(dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void register_whenEmailAlreadyExists_throwsUserEmailAlreadyUsedException() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("  test@example.com  ");
        dto.setPassword("pass");
        dto.setRole("ADMIN");

        when(repo.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(UserEmailAlreadyUsedException.class, () -> service.register(dto));

        verify(repo).existsByEmail("test@example.com");
        verifyNoInteractions(roleRepo, passwordEncoder);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void register_whenRoleNotFound_throwsUserInvalidDataException() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("pass");
        dto.setRole("admin");

        when(repo.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.empty());

        assertThrows(UserInvalidDataException.class, () -> service.register(dto));

        verify(repo).existsByEmail("test@example.com");
        verify(roleRepo).findByName("ADMIN");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void register_whenActiveNull_defaultsToTrue() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("pass");
        dto.setRole("ADMIN");
        dto.setFirstName("Jean");
        dto.setLastName("Baruba");
        dto.setAddress("Street 1");
        dto.setPhoneNumber("0612345678");
        dto.setActive(null);

        Role role = new Role();
        role.setName("ADMIN");

        when(repo.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass")).thenReturn("hashed");

        when(repo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10);
            return u;
        });

        UserResponseDto result = service.register(dto);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
        assertTrue(result.isActive());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());
        assertEquals("test@example.com", captor.getValue().getEmail());
        assertEquals("hashed", captor.getValue().getPasswordHash());
        assertTrue(captor.getValue().isActive());
        assertNotNull(captor.getValue().getRole());

        verify(repo).existsByEmail("test@example.com");
        verify(roleRepo).findByName("ADMIN");
        verify(passwordEncoder).encode("pass");
    }

    @Test
    void register_whenActiveProvided_usesProvidedValue() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("pass");
        dto.setRole("STAFF");
        dto.setActive(false);

        Role role = new Role();
        role.setName("STAFF");

        when(repo.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepo.findByName("STAFF")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDto result = service.register(dto);

        assertFalse(result.isActive());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());
        assertFalse(captor.getValue().isActive());
    }

    @Test
    void getById_whenIdIsNull_throwsUserInvalidDataException() {
        assertThrows(UserInvalidDataException.class, () -> service.getById(null));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void getById_whenNotFound_returnsEmptyOptional() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        Optional<UserResponseDto> result = service.getById(99);

        assertTrue(result.isEmpty());
        verify(repo).findById(99);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getById_whenFound_mapsToDto() {
        Role role = new Role();
        role.setName("ADMIN");

        User user = new User();
        user.setId(1);
        user.setEmail("a@b.com");
        user.setFirstName("A");
        user.setLastName("B");
        user.setAddress("Addr");
        user.setPhoneNumber("06");
        user.setRole(role);
        user.setActive(true);

        when(repo.findById(1)).thenReturn(Optional.of(user));

        Optional<UserResponseDto> result = service.getById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("a@b.com", result.get().getEmail());
        assertEquals("ADMIN", result.get().getRole());
        assertTrue(result.get().isActive());

        verify(repo).findById(1);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAll_mapsAllToDtos() {
        Role role = new Role();
        role.setName("ADMIN");

        User u1 = new User();
        u1.setId(1);
        u1.setEmail("a@b.com");
        u1.setRole(role);
        u1.setActive(true);

        User u2 = new User();
        u2.setId(2);
        u2.setEmail("c@d.com");
        u2.setRole(null);
        u2.setActive(false);

        when(repo.findAll()).thenReturn(List.of(u1, u2));

        List<UserResponseDto> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("ADMIN", result.get(0).getRole());
        assertEquals(2, result.get(1).getId());
        assertNull(result.get(1).getRole());

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void updateProfile_whenIdIsNull_throwsUserInvalidDataException() {
        UserUpdateDto dto = new UserUpdateDto();
        assertThrows(UserInvalidDataException.class, () -> service.updateProfile(null, dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void updateProfile_whenDtoIsNull_throwsUserInvalidDataException() {
        assertThrows(UserInvalidDataException.class, () -> service.updateProfile(1, null));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void updateProfile_whenUserNotFound_throwsUserNotFoundException() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        UserUpdateDto dto = new UserUpdateDto();

        assertThrows(UserNotFoundException.class, () -> service.updateProfile(1, dto));

        verify(repo).findById(1);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void updateProfile_whenEmailProvidedBlank_throwsUserInvalidDataException() {
        User u = new User();
        u.setId(1);
        u.setEmail("old@example.com");

        when(repo.findById(1)).thenReturn(Optional.of(u));

        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("   ");

        assertThrows(UserInvalidDataException.class, () -> service.updateProfile(1, dto));

        verify(repo).findById(1);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void updateProfile_whenEmailChangesAndExists_throwsUserEmailAlreadyUsedException() {
        User u = new User();
        u.setId(1);
        u.setEmail("old@example.com");

        when(repo.findById(1)).thenReturn(Optional.of(u));
        when(repo.existsByEmail("new@example.com")).thenReturn(true);

        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("  new@example.com  ");

        assertThrows(UserEmailAlreadyUsedException.class, () -> service.updateProfile(1, dto));

        verify(repo).findById(1);
        verify(repo).existsByEmail("new@example.com");
    }

    @Test
    void updateProfile_whenRoleBlankAfterTrim_throwsUserInvalidDataException() {
        Role oldRole = new Role();
        oldRole.setName("ADMIN");

        User u = new User();
        u.setId(1);
        u.setEmail("old@example.com");
        u.setRole(oldRole);

        when(repo.findById(1)).thenReturn(Optional.of(u));

        UserUpdateDto dto = new UserUpdateDto();
        dto.setRole("   ");

        assertThrows(UserInvalidDataException.class, () -> service.updateProfile(1, dto));

        verify(repo).findById(1);
        verifyNoMoreInteractions(repo, roleRepo);
    }

    @Test
    void updateProfile_whenRoleNotFound_throwsUserInvalidDataException() {
        Role oldRole = new Role();
        oldRole.setName("ADMIN");

        User u = new User();
        u.setId(1);
        u.setEmail("old@example.com");
        u.setRole(oldRole);

        when(repo.findById(1)).thenReturn(Optional.of(u));
        when(roleRepo.findByName("STAFF")).thenReturn(Optional.empty());

        UserUpdateDto dto = new UserUpdateDto();
        dto.setRole("staff");

        assertThrows(UserInvalidDataException.class, () -> service.updateProfile(1, dto));

        verify(repo).findById(1);
        verify(roleRepo).findByName("STAFF");
        verifyNoMoreInteractions(repo, roleRepo);
    }

    @Test
    void updateProfile_whenValid_updatesFieldsAndSaves() {
        Role oldRole = new Role();
        oldRole.setName("ADMIN");

        Role newRole = new Role();
        newRole.setName("STAFF");

        User u = new User();
        u.setId(1);
        u.setEmail("old@example.com");
        u.setFirstName("Old");
        u.setLastName("Name");
        u.setAddress("Addr");
        u.setPhoneNumber("06");
        u.setRole(oldRole);
        u.setActive(true);

        when(repo.findById(1)).thenReturn(Optional.of(u));
        when(repo.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepo.findByName("STAFF")).thenReturn(Optional.of(newRole));
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("  new@example.com  ");
        dto.setFirstName("Jean");
        dto.setLastName("Baruba");
        dto.setAddress("Street 1");
        dto.setPhoneNumber("0612345678");
        dto.setRole("staff");
        dto.setActive(false);

        UserResponseDto result = service.updateProfile(1, dto);

        assertEquals("new@example.com", result.getEmail());
        assertEquals("Jean", result.getFirstName());
        assertEquals("Baruba", result.getLastName());
        assertEquals("Street 1", result.getAddress());
        assertEquals("0612345678", result.getPhoneNumber());
        assertEquals("STAFF", result.getRole());
        assertFalse(result.isActive());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());
        assertEquals("new@example.com", captor.getValue().getEmail());
        assertEquals(newRole, captor.getValue().getRole());
        assertFalse(captor.getValue().isActive());

        verify(repo).findById(1);
        verify(repo).existsByEmail("new@example.com");
        verify(roleRepo).findByName("STAFF");
        verify(repo).save(any(User.class));
        verifyNoMoreInteractions(repo, roleRepo);
    }

    @Test
    void changePassword_whenIdIsNull_throwsUserInvalidDataException() {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("old");
        dto.setNewPassword("new");

        assertThrows(UserInvalidDataException.class, () -> service.changePassword(null, dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void changePassword_whenDtoIsNull_throwsUserInvalidDataException() {
        assertThrows(UserInvalidDataException.class, () -> service.changePassword(1, null));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void changePassword_whenOldPasswordNull_throwsUserInvalidDataException() {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword(null);
        dto.setNewPassword("new");

        assertThrows(UserInvalidDataException.class, () -> service.changePassword(1, dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void changePassword_whenOldPasswordBlank_throwsUserInvalidDataException() {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("   ");
        dto.setNewPassword("new");

        assertThrows(UserInvalidDataException.class, () -> service.changePassword(1, dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void changePassword_whenNewPasswordNull_throwsUserInvalidDataException() {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("old");
        dto.setNewPassword(null);

        assertThrows(UserInvalidDataException.class, () -> service.changePassword(1, dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void changePassword_whenNewPasswordBlank_throwsUserInvalidDataException() {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("old");
        dto.setNewPassword("   ");

        assertThrows(UserInvalidDataException.class, () -> service.changePassword(1, dto));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void changePassword_whenUserNotFound_throwsUserNotFoundException() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("old");
        dto.setNewPassword("new");

        assertThrows(UserNotFoundException.class, () -> service.changePassword(1, dto));

        verify(repo).findById(1);
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void changePassword_whenOldPasswordDoesNotMatch_throwsInvalidPasswordChangeException() {
        User u = new User();
        u.setId(1);
        u.setPasswordHash("hash");

        when(repo.findById(1)).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("old", "hash")).thenReturn(false);

        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("old");
        dto.setNewPassword("new");

        assertThrows(InvalidPasswordChangeException.class, () -> service.changePassword(1, dto));

        verify(repo).findById(1);
        verify(passwordEncoder).matches("old", "hash");
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void changePassword_whenNewPasswordSameAsOld_throwsInvalidPasswordChangeException() {
        User u = new User();
        u.setId(1);
        u.setPasswordHash("hash");

        when(repo.findById(1)).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("old", "hash")).thenReturn(true);
        when(passwordEncoder.matches("new", "hash")).thenReturn(true);

        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("old");
        dto.setNewPassword("new");

        assertThrows(InvalidPasswordChangeException.class, () -> service.changePassword(1, dto));

        verify(repo).findById(1);
        verify(passwordEncoder).matches("old", "hash");
        verify(passwordEncoder).matches("new", "hash");
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void changePassword_whenValid_updatesHashAndReturnsTrue() {
        User u = new User();
        u.setId(1);
        u.setPasswordHash("oldHash");

        when(repo.findById(1)).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("old", "oldHash")).thenReturn(true);
        when(passwordEncoder.matches("new", "oldHash")).thenReturn(false);
        when(passwordEncoder.encode("new")).thenReturn("newHash");
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("old");
        dto.setNewPassword("new");

        boolean result = service.changePassword(1, dto);

        assertTrue(result);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());
        assertEquals("newHash", captor.getValue().getPasswordHash());

        verify(repo).findById(1);
        verify(passwordEncoder).matches("old", "oldHash");
        verify(passwordEncoder).matches("new", "oldHash");
        verify(passwordEncoder).encode("new");
        verify(repo).save(any(User.class));
        verifyNoMoreInteractions(repo, passwordEncoder);
    }

    @Test
    void emailExists_whenEmailNull_throwsUserInvalidDataException() {
        assertThrows(UserInvalidDataException.class, () -> service.emailExists(null));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void emailExists_whenEmailBlank_throwsUserInvalidDataException() {
        assertThrows(UserInvalidDataException.class, () -> service.emailExists("   "));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void emailExists_whenValid_trimsAndChecksRepo() {
        when(repo.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = service.emailExists("  test@example.com  ");

        assertTrue(result);
        verify(repo).existsByEmail("test@example.com");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteUser_whenIdIsNull_throwsUserInvalidDataException() {
        assertThrows(UserInvalidDataException.class, () -> service.deleteUser(null));
        verifyNoInteractions(repo, roleRepo, passwordEncoder);
    }

    @Test
    void deleteUser_whenNotExists_throwsUserNotFoundException() {
        when(repo.existsById(9)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> service.deleteUser(9));

        verify(repo).existsById(9);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteUser_whenExists_deletes() {
        when(repo.existsById(9)).thenReturn(true);

        service.deleteUser(9);

        verify(repo).existsById(9);
        verify(repo).deleteById(9);
        verifyNoMoreInteractions(repo);
    }
}
