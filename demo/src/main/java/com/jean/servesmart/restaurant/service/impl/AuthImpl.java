package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.Auth.UserLoginDto;
import com.jean.servesmart.restaurant.dto.LoginLog.LoginLogCreateDto;
import com.jean.servesmart.restaurant.dto.User.UserResponseDto;
import com.jean.servesmart.restaurant.exception.auth.AuthInvalidDataException;
import com.jean.servesmart.restaurant.exception.auth.InactiveAccountException;
import com.jean.servesmart.restaurant.exception.auth.InvalidCredentialsException;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.UserRepository;
import com.jean.servesmart.restaurant.service.interfaces.AuthService;
import com.jean.servesmart.restaurant.service.interfaces.LoginLogService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthImpl implements AuthService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginLogService loginLogService;

    public AuthImpl(UserRepository repo,
                    BCryptPasswordEncoder passwordEncoder,
                    LoginLogService loginLogService) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.loginLogService = loginLogService;
    }

    @Override
    public UserResponseDto login(UserLoginDto dto) {

        if (dto == null) {
            throw new AuthInvalidDataException();
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new AuthInvalidDataException();
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new AuthInvalidDataException();
        }

        String email = dto.getEmail().trim();

        User user = repo.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new InactiveAccountException();
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        logLogin(user.getId(), "LOGIN_SUCCESS");

        return toResponse(user);
    }

    private void logLogin(Integer userId, String status) {
        LoginLogCreateDto logDto = new LoginLogCreateDto();
        logDto.setUserId(userId);
        logDto.setStatus(status);
        loginLogService.log(logDto);
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
