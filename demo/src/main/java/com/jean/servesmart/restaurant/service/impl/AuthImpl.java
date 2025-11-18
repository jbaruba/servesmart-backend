package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.Auth.UserLoginDto;
import com.jean.servesmart.restaurant.dto.User.UserResponseDto;
import com.jean.servesmart.restaurant.exception.auth.InactiveAccountException;
import com.jean.servesmart.restaurant.exception.auth.InvalidCredentialsException;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.UserRepository;
import com.jean.servesmart.restaurant.service.interfaces.AuthService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthImpl implements AuthService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthImpl(UserRepository repo, BCryptPasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto login(UserLoginDto dto) {
        User user = repo.findByEmail(dto.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new InactiveAccountException();
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return toResponse(user);
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
