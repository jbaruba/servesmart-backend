package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.user.ChangePasswordDto;
import com.jean.servesmart.restaurant.dto.user.UserRegisterDto;
import com.jean.servesmart.restaurant.dto.user.UserResponseDto;
import com.jean.servesmart.restaurant.dto.user.UserUpdateDto;
import com.jean.servesmart.restaurant.exception.user.InvalidPasswordChangeException;
import com.jean.servesmart.restaurant.exception.user.UserEmailAlreadyUsedException;
import com.jean.servesmart.restaurant.exception.user.UserInvalidDataException;
import com.jean.servesmart.restaurant.exception.user.UserNotFoundException;
import com.jean.servesmart.restaurant.model.Role;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.RoleRepository;
import com.jean.servesmart.restaurant.repository.UserRepository;
import com.jean.servesmart.restaurant.service.interfaces.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserImpl implements UserService {

    private final UserRepository repo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserImpl(UserRepository repo, RoleRepository roleRepo, BCryptPasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto register(UserRegisterDto dto) {
        validateRegisterDto(dto);

        String email = dto.getEmail().trim();
        String roleName = dto.getRole().trim().toUpperCase();

        if (repo.existsByEmail(email)) {
            throw new UserEmailAlreadyUsedException();
        }

        Role role = roleRepo.findByName(roleName)
                .orElseThrow(UserInvalidDataException::new);

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAddress(dto.getAddress());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(role);
        user.setActive(dto.getActive() == null || dto.getActive());

        return toResponse(repo.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getById(Integer id) {
        if (id == null) {
            throw new UserInvalidDataException();
        }

        return repo.findById(id).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponseDto updateProfile(Integer id, UserUpdateDto dto) {
        if (id == null || dto == null) {
            throw new UserInvalidDataException();
        }

        User user = repo.findById(id)
                .orElseThrow(UserNotFoundException::new);

        updateEmail(dto, user);
        updateRole(dto, user);

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }

        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }

        return toResponse(repo.save(user));
    }

    @Override
    public boolean changePassword(Integer id, ChangePasswordDto dto) {
        if (id == null || dto == null) {
            throw new UserInvalidDataException();
        }

        if (dto.getOldPassword() == null || dto.getOldPassword().isBlank()) {
            throw new UserInvalidDataException();
        }

        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new UserInvalidDataException();
        }

        User user = repo.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordChangeException();
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordChangeException();
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        repo.save(user);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        if (email == null || email.isBlank()) {
            throw new UserInvalidDataException();
        }
        return repo.existsByEmail(email.trim());
    }

    @Override
    public void deleteUser(Integer id) {
        if (id == null) {
            throw new UserInvalidDataException();
        }

        if (!repo.existsById(id)) {
            throw new UserNotFoundException();
        }

        repo.deleteById(id);
    }

    private void validateRegisterDto(UserRegisterDto dto) {
        if (dto == null
                || dto.getEmail() == null || dto.getEmail().isBlank()
                || dto.getPassword() == null || dto.getPassword().isBlank()
                || dto.getRole() == null || dto.getRole().isBlank()) {
            throw new UserInvalidDataException();
        }
    }

    private void updateEmail(UserUpdateDto dto, User user) {
        if (dto.getEmail() == null) {
            return;
        }

        if (dto.getEmail().isBlank()) {
            throw new UserInvalidDataException();
        }

        String newEmail = dto.getEmail().trim();
        if (!newEmail.equalsIgnoreCase(user.getEmail()) && repo.existsByEmail(newEmail)) {
            throw new UserEmailAlreadyUsedException();
        }

        user.setEmail(newEmail);
    }

    private void updateRole(UserUpdateDto dto, User user) {
        if (dto.getRole() == null) {
            return;
        }

        String roleName = dto.getRole().trim().toUpperCase();
        if (roleName.isBlank()) {
            throw new UserInvalidDataException();
        }

        Role role = roleRepo.findByName(roleName)
                .orElseThrow(UserInvalidDataException::new);

        user.setRole(role);
    }

    private UserResponseDto toResponse(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAddress(user.getAddress());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole() != null ? user.getRole().getName() : null);
        dto.setActive(user.isActive());
        return dto;
    }
}
