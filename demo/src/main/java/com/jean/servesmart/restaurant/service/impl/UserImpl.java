package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.User.ChangePasswordDto;
import com.jean.servesmart.restaurant.dto.User.UserRegisterDto;
import com.jean.servesmart.restaurant.dto.User.UserResponseDto;
import com.jean.servesmart.restaurant.dto.User.UserUpdateDto;
import com.jean.servesmart.restaurant.exception.user.InvalidPasswordChangeException;
import com.jean.servesmart.restaurant.exception.user.UserEmailAlreadyUsedException;
import com.jean.servesmart.restaurant.exception.user.UserNotFoundException;
import com.jean.servesmart.restaurant.model.User;
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
    private final BCryptPasswordEncoder passwordEncoder;

    public UserImpl(UserRepository repo, BCryptPasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto register(UserRegisterDto dto) {
        if (repo.existsByEmail(dto.getEmail())) {
            throw new UserEmailAlreadyUsedException();
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAddress(dto.getAddress());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(dto.getRole());
        user.setActive(true);

        User saved = repo.save(user);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getById(Integer id) {
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
        User u = repo.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(u.getEmail())) {
            if (repo.existsByEmail(dto.getEmail())) {
                throw new UserEmailAlreadyUsedException();
            }
            u.setEmail(dto.getEmail());
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
            u.setRole(dto.getRole());
        }
        if (dto.getActive() != null) {
            u.setActive(dto.getActive());
        }

        User updated = repo.save(u);
        return toResponse(updated);
    }

    @Override
    public boolean changePassword(Integer id, ChangePasswordDto dto) {
        User u = repo.findById(id)
                .orElseThrow(UserNotFoundException::new);

        // oude wachtwoord klopt niet
        if (!passwordEncoder.matches(dto.getOldPassword(), u.getPasswordHash())) {
            throw new InvalidPasswordChangeException();
        }

        // nieuw wachtwoord mag niet hetzelfde zijn als oude
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
        return repo.existsByEmail(email);
    }

    private UserResponseDto toResponse(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAddress(user.getAddress());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        return dto;
    }
}
