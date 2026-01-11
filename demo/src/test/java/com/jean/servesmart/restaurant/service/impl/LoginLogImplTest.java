package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.LoginLog.LoginLogCreateDto;
import com.jean.servesmart.restaurant.dto.LoginLog.LoginLogResponseDto;
import com.jean.servesmart.restaurant.exception.loginlog.LoginLogInvalidDataException;
import com.jean.servesmart.restaurant.exception.loginlog.LoginLogUserNotFoundException;
import com.jean.servesmart.restaurant.model.LoginLog;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.LoginLogRepository;
import com.jean.servesmart.restaurant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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

    private LoginLogImpl service;

    @BeforeEach
    void setup() {
        service = new LoginLogImpl(logRepo, userRepo);
    }

    @Test
    void log_whenDtoIsNull_throwsLoginLogInvalidDataException() {
        assertThrows(LoginLogInvalidDataException.class, () -> service.log(null));
        verifyNoInteractions(logRepo, userRepo);
    }

    @Test
    void log_whenUserIdIsNull_throwsLoginLogInvalidDataException() {
        LoginLogCreateDto dto = new LoginLogCreateDto();
        dto.setUserId(null);
        dto.setStatus("LOGIN_SUCCESS");

        assertThrows(LoginLogInvalidDataException.class, () -> service.log(dto));
        verifyNoInteractions(logRepo, userRepo);
    }

    @Test
    void log_whenStatusIsNull_throwsLoginLogInvalidDataException() {
        LoginLogCreateDto dto = new LoginLogCreateDto();
        dto.setUserId(1);
        dto.setStatus(null);

        assertThrows(LoginLogInvalidDataException.class, () -> service.log(dto));
        verifyNoInteractions(logRepo, userRepo);
    }

    @Test
    void log_whenStatusIsBlank_throwsLoginLogInvalidDataException() {
        LoginLogCreateDto dto = new LoginLogCreateDto();
        dto.setUserId(1);
        dto.setStatus("   ");

        assertThrows(LoginLogInvalidDataException.class, () -> service.log(dto));
        verifyNoInteractions(logRepo, userRepo);
    }

    @Test
    void log_whenUserNotFound_throwsLoginLogUserNotFoundException() {
        LoginLogCreateDto dto = new LoginLogCreateDto();
        dto.setUserId(1);
        dto.setStatus("LOGIN_SUCCESS");

        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(LoginLogUserNotFoundException.class, () -> service.log(dto));

        verify(userRepo).findById(1);
        verifyNoInteractions(logRepo);
    }

    @Test
    void log_whenValid_savesLoginLogWithNormalizedStatusAndUser() {
        LoginLogCreateDto dto = new LoginLogCreateDto();
        dto.setUserId(1);
        dto.setStatus("  login_success  ");

        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");

        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        service.log(dto);

        ArgumentCaptor<LoginLog> captor = ArgumentCaptor.forClass(LoginLog.class);
        verify(logRepo).save(captor.capture());

        LoginLog saved = captor.getValue();
        assertNotNull(saved);
        assertNotNull(saved.getUser());
        assertEquals(1, saved.getUser().getId());
        assertEquals("LOGIN_SUCCESS", saved.getStatus());

        verify(userRepo).findById(1);
        verifyNoMoreInteractions(userRepo, logRepo);
    }

    @Test
    void getByUser_whenUserIdIsNull_throwsLoginLogInvalidDataException() {
        assertThrows(LoginLogInvalidDataException.class, () -> service.getByUser(null));
        verifyNoInteractions(logRepo, userRepo);
    }

    @Test
    void getByUser_whenUserDoesNotExist_throwsLoginLogUserNotFoundException() {
        when(userRepo.existsById(99)).thenReturn(false);

        assertThrows(LoginLogUserNotFoundException.class, () -> service.getByUser(99));

        verify(userRepo).existsById(99);
        verifyNoInteractions(logRepo);
    }

    @Test
    void getByUser_whenLogsContainMixedStatuses_filtersAndMapsToDtos() {
        when(userRepo.existsById(1)).thenReturn(true);

        User user = new User();
        user.setId(1);
        user.setEmail("u@example.com");

        LoginLog l1 = new LoginLog();
        l1.setId(10);
        l1.setUser(user);
        l1.setStatus("LOGIN_SUCCESS");
        l1.setDate(LocalDateTime.now().minusDays(1));

        LoginLog l2 = new LoginLog();
        l2.setId(11);
        l2.setUser(user);
        l2.setStatus("FAILED");
        l2.setDate(LocalDateTime.now().minusHours(2));

        LoginLog l3 = new LoginLog();
        l3.setId(12);
        l3.setUser(user);
        l3.setStatus("  logout ");
        l3.setDate(LocalDateTime.now());

        LoginLog l4 = new LoginLog();
        l4.setId(13);
        l4.setUser(user);
        l4.setStatus(null);
        l4.setDate(LocalDateTime.now());

        when(logRepo.findByUser_IdOrderByDateDesc(1)).thenReturn(List.of(l1, l2, l3, l4));

        List<LoginLogResponseDto> result = service.getByUser(1);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(10, result.get(0).getId());
        assertEquals(1, result.get(0).getUserId());
        assertEquals("u@example.com", result.get(0).getUserEmail());
        assertEquals("LOGIN_SUCCESS", result.get(0).getStatus());
        assertNotNull(result.get(0).getDate());

        assertEquals(12, result.get(1).getId());
        assertEquals(1, result.get(1).getUserId());
        assertEquals("u@example.com", result.get(1).getUserEmail());
        assertEquals("  logout ", result.get(1).getStatus());
        assertNotNull(result.get(1).getDate());

        verify(userRepo).existsById(1);
        verify(logRepo).findByUser_IdOrderByDateDesc(1);
        verifyNoMoreInteractions(userRepo, logRepo);
    }

    @Test
    void getByUser_whenLogUserIsNull_mapsNullUserFields() {
        when(userRepo.existsById(1)).thenReturn(true);

        LoginLog l1 = new LoginLog();
        l1.setId(10);
        l1.setUser(null);
        l1.setStatus("LOGIN_SUCCESS");
        l1.setDate(LocalDateTime.now());

        when(logRepo.findByUser_IdOrderByDateDesc(1)).thenReturn(List.of(l1));

        List<LoginLogResponseDto> result = service.getByUser(1);

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getId());
        assertNull(result.get(0).getUserId());
        assertNull(result.get(0).getUserEmail());
        assertEquals("LOGIN_SUCCESS", result.get(0).getStatus());
        assertNotNull(result.get(0).getDate());

        verify(userRepo).existsById(1);
        verify(logRepo).findByUser_IdOrderByDateDesc(1);
    }
}
