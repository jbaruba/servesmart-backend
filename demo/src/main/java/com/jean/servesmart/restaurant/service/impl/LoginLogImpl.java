package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.loginLog.LoginLogCreateDto;
import com.jean.servesmart.restaurant.dto.loginLog.LoginLogResponseDto;
import com.jean.servesmart.restaurant.exception.loginlog.LoginLogInvalidDataException;
import com.jean.servesmart.restaurant.exception.loginlog.LoginLogUserNotFoundException;
import com.jean.servesmart.restaurant.model.LoginLog;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.LoginLogRepository;
import com.jean.servesmart.restaurant.repository.UserRepository;
import com.jean.servesmart.restaurant.service.interfaces.LoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoginLogImpl implements LoginLogService {

    private final LoginLogRepository logRepo;
    private final UserRepository userRepo;

    public LoginLogImpl(LoginLogRepository logRepo, UserRepository userRepo) {
        this.logRepo = logRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void log(LoginLogCreateDto dto) {

        if (dto == null) {
            throw new LoginLogInvalidDataException();
        }

        if (dto.getUserId() == null) {
            throw new LoginLogInvalidDataException("User id is required");
        }

        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            throw new LoginLogInvalidDataException("Status is required");
        }

        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(LoginLogUserNotFoundException::new);

        String normalizedStatus = dto.getStatus().trim().toUpperCase();

        LoginLog log = new LoginLog();
        log.setUser(user);
        log.setStatus(normalizedStatus);

        logRepo.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginLogResponseDto> getByUser(Integer userId) {

        if (userId == null) {
            throw new LoginLogInvalidDataException();
        }

        if (!userRepo.existsById(userId)) {
            throw new LoginLogUserNotFoundException();
        }

        return logRepo.findByUser_IdOrderByDateDesc(userId)
                .stream()
                .filter(l -> {
                    String s = l.getStatus();
                    if (s == null) return false;
                    String up = s.trim().toUpperCase();
                    return "LOGIN_SUCCESS".equals(up) || "LOGOUT".equals(up);
                })
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private LoginLogResponseDto toResponse(LoginLog l) {
        LoginLogResponseDto dto = new LoginLogResponseDto();
        dto.setId(l.getId());
        dto.setUserId(l.getUser() != null ? l.getUser().getId() : null);
        dto.setUserEmail(l.getUser() != null ? l.getUser().getEmail() : null);
        dto.setStatus(l.getStatus());
        dto.setDate(l.getDate());
        return dto;
    }
}
