package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.User.*;
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
        try {
            if (repo.existsByEmail(dto.getEmail())) {
                return null; 
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

        } catch (Exception e) {
            System.err.println("Error in register(): " + e.getMessage());
            return null; 
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getById(Integer id) {
        try {
            return repo.findById(id).map(this::toResponse);
        } catch (Exception e) {
            System.err.println("Error in getById(): " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        try {
            return repo.findAll()
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getAll(): " + e.getMessage());
            return List.of(); 
        }
    }

    @Override
    public UserResponseDto updateProfile(Integer id, UserUpdateDto dto) {
        try {
            Optional<User> optional = repo.findById(id);
            if (optional.isEmpty()) {
                return null; 
            }

            User u = optional.get();

            if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(u.getEmail())) {
                if (repo.existsByEmail(dto.getEmail())) {
                    return null; 
                }
                u.setEmail(dto.getEmail());
            }

            if (dto.getFirstName()   != null) u.setFirstName(dto.getFirstName());
            if (dto.getLastName()    != null) u.setLastName(dto.getLastName());
            if (dto.getAddress()     != null) u.setAddress(dto.getAddress());
            if (dto.getPhoneNumber() != null) u.setPhoneNumber(dto.getPhoneNumber());
            if (dto.getRole()        != null) u.setRole(dto.getRole());
            if (dto.getActive()      != null) u.setActive(dto.getActive());

            User updated = repo.save(u);
            return toResponse(updated);

        } catch (Exception e) {
            System.err.println("Error in updateProfile(): " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean changePassword(Integer id, ChangePasswordDto dto) {
        try {
            Optional<User> optional = repo.findById(id);
            if (optional.isEmpty()) return false;

            User u = optional.get();

            if (!passwordEncoder.matches(dto.getOldPassword(), u.getPasswordHash())) return false;
            if (passwordEncoder.matches(dto.getNewPassword(), u.getPasswordHash())) return false;

            u.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
            repo.save(u);
            return true;

        } catch (Exception e) {
            System.err.println("Error in changePassword(): " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        try {
            return repo.existsByEmail(email);
        } catch (Exception e) {
            System.err.println("Error in emailExists(): " + e.getMessage());
            return false;
        }
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
