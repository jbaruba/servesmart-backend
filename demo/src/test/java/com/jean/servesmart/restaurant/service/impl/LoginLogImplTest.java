package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.LoginLog.LoginLogCreateDto;
import com.jean.servesmart.restaurant.dto.LoginLog.LoginLogResponseDto;
import com.jean.servesmart.restaurant.exception.loginlog.LoginLogInvalidDataException;
import com.jean.servesmart.restaurant.exception.loginlog.LoginLogUserNotFoundException;
import com.jean.servesmart.restaurant.model.LoginLog;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.LoginLogRepository;
import com.jean.servesmart.restaurant.repository.UserRepository;
import com.jean.servesmart.restaurant.service.interfaces.LoginLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginLogImplTest {

    @Mock
    private LoginLogRepository logRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private LoginLogImpl service;

    private LoginLogCreateDto createDto(Integer userId, String status) {
        LoginLogCreateDto dto = new LoginLogCreateDto();
        dto.setUserId(userId);
        dto.setStatus(status);
        return dto;
    }

    private User user(Integer id, String email) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        return u;
    }

    private LoginLog log(Integer id, User user, String status) {
        LoginLog l = new LoginLog();
        l.setId(id);
        l.setUser(user);
        l.setStatus(status);
        return l;
    }

    // log

    @Test
    void log_shouldThrowInvalidData_whenDtoNull() {
        assertThrows(LoginLogInvalidDataException.class,
                () -> service.log(null));

        verifyNoInteractions(userRepo, logRepo);
    }

    @Test
    void log_shouldThrowInvalidData_whenUserIdNull() {
        LoginLogCreateDto dto = createDto(null, "LOGIN_SUCCESS");

        assertThrows(LoginLogInvalidDataException.class,() -> service.log(dto));

        verifyNoInteractions(userRepo, logRepo);
    }

    @Test
    void log_shouldThrowInvalidData_whenStatusNullOrBlank() {
        LoginLogCreateDto dtoNull = createDto(1, null);
        LoginLogCreateDto dtoBlank = createDto(1, "   ");

        assertThrows(LoginLogInvalidDataException.class,() -> service.log(dtoNull));
        assertThrows(LoginLogInvalidDataException.class,() -> service.log(dtoBlank));

        verifyNoInteractions(userRepo, logRepo);
    }

    @Test
    void log_shouldThrowUserNotFound_whenUserDoesNotExist() {
        LoginLogCreateDto dto = createDto(1, "LOGIN_SUCCESS");

        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(LoginLogUserNotFoundException.class,() -> service.log(dto));

        verify(userRepo).findById(1);
        verifyNoInteractions(logRepo);
    }

    @Test
    void log_shouldSaveLoginLogWithNormalizedStatus_whenValid() {
        LoginLogCreateDto dto = createDto(1, "  login_success  ");

        User u = user(1, "user@mail.com");
        when(userRepo.findById(1)).thenReturn(Optional.of(u));

        ArgumentCaptor<LoginLog> captor = ArgumentCaptor.forClass(LoginLog.class);
        when(logRepo.save(any(LoginLog.class))).thenAnswer(inv -> inv.getArgument(0));

        service.log(dto);

        verify(userRepo).findById(1);
        verify(logRepo).save(captor.capture());

        LoginLog saved = captor.getValue();
        assertEquals(u, saved.getUser());
        assertEquals("LOGIN_SUCCESS", saved.getStatus()); 
    }

    // getByUser

    @Test
    void getByUser_shouldThrowInvalidData_whenUserIdNull() {
        assertThrows(LoginLogInvalidDataException.class,() -> service.getByUser(null));

        verifyNoInteractions(userRepo, logRepo);
    }

    @Test
    void getByUser_shouldThrowUserNotFound_whenUserDoesNotExist() {
        when(userRepo.existsById(99)).thenReturn(false);

        assertThrows(LoginLogUserNotFoundException.class,
                () -> service.getByUser(99));

        verify(userRepo).existsById(99);
        verifyNoInteractions(logRepo);
    }

    @Test
    void getByUser_shouldReturnFilteredAndMappedList_whenValid() {
        User u = user(1, "user@mail.com");

        when(userRepo.existsById(1)).thenReturn(true);

        List<LoginLog> logs = Arrays.asList(
                log(1, u, "LOGIN_SUCCESS"),
                log(2, u, " logout "),
                log(3, u, "OTHER"),
                log(4, u, null)
        );

        when(logRepo.findByUser_IdOrderByDateDesc(1)).thenReturn(logs);

        List<LoginLogResponseDto> result = service.getByUser(1);

        assertEquals(2, result.size());

        LoginLogResponseDto first = result.get(0);
        LoginLogResponseDto second = result.get(1);

        assertEquals(1, first.getUserId());
        assertEquals("user@mail.com", first.getUserEmail());
        assertEquals("LOGIN_SUCCESS", first.getStatus());

        assertEquals(1, second.getUserId());
        assertEquals("user@mail.com", second.getUserEmail());
        assertEquals(" logout ", second.getStatus()); // originele string blijft in dto

        verify(userRepo).existsById(1);
        verify(logRepo).findByUser_IdOrderByDateDesc(1);
    }
}
