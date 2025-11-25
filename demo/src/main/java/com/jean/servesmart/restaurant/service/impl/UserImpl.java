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
import com.jean.servesmart.restaurant.service.interfaces.UserService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (dto == null) {
            throw new UserInvalidDataException();
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new UserInvalidDataException();
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new UserInvalidDataException();
        }

        if (dto.getRole() == null || dto.getRole().isBlank()) {
            throw new UserInvalidDataException();
        }

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
        user.setActive(dto.getActive() != null ? dto.getActive() : true);

        User saved = repo.save(user);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getById(Integer id) {
        if (id == null) {
            throw new UserInvalidDataException();
        }

        return repo.findById(id)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto updateProfile(Integer id, UserUpdateDto dto) {
        if (id == null) {
            throw new UserInvalidDataException();
        }

        if (dto == null) {
            throw new UserInvalidDataException();
        }

        User u = repo.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (dto.getEmail() != null) {
            if (dto.getEmail().isBlank()) {
                throw new UserInvalidDataException();
            }

            String newEmail = dto.getEmail().trim();

            if (!newEmail.equalsIgnoreCase(u.getEmail()) && repo.existsByEmail(newEmail)) {
                throw new UserEmailAlreadyUsedException();
            }

            u.setEmail(newEmail);
        }

        if (dto.getFirstName() != null) {
            u.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            u.setLastName(dto.getLastName());
        }

        if (dto.getAddress() != null) {
            u.setAddress(dto.getAddress());
        }

        if (dto.getPhoneNumber() != null) {
            u.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getRole() != null) {
            String roleName = dto.getRole().trim().toUpperCase();
            if (roleName.isBlank()) {
                throw new UserInvalidDataException();
            }
            Role role = roleRepo.findByName(roleName)
                    .orElseThrow(UserInvalidDataException::new);
            u.setRole(role);
        }

        if (dto.getActive() != null) {
            u.setActive(dto.getActive());
        }

        User updated = repo.save(u);
        return toResponse(updated);
    }

    @Override
    public boolean changePassword(Integer id, ChangePasswordDto dto) {
        if (id == null) {
            throw new UserInvalidDataException();
        }

        if (dto == null) {
            throw new UserInvalidDataException();
        }

        if (dto.getOldPassword() == null || dto.getOldPassword().isBlank()) {
            throw new UserInvalidDataException();
        }

        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new UserInvalidDataException();
        }

        User u = repo.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(dto.getOldPassword(), u.getPasswordHash())) {
            throw new InvalidPasswordChangeException();
        }
        if (passwordEncoder.matches(dto.getNewPassword(), u.getPasswordHash())) {
            throw new InvalidPasswordChangeException();
        }

        u.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        repo.save(u);
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
