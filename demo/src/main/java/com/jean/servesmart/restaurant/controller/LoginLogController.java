package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.loginLog.LoginLogResponseDto;
import com.jean.servesmart.restaurant.exception.loginlog.LoginLogInvalidDataException;
import com.jean.servesmart.restaurant.exception.loginlog.LoginLogUserNotFoundException;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.LoginLogService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/login-logs")
public class LoginLogController {

    private final LoginLogService logs;

    public LoginLogController(LoginLogService logs) {
        this.logs = logs;
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getByUser(@PathVariable Integer userId) {
        try {
            List<LoginLogResponseDto> list = logs.getByUser(userId);
            String message = list.isEmpty() ? "No login logs found for this user" : "Login logs retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (LoginLogInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid user id"));
        } catch (LoginLogUserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Could not load login logs"));
        }
    }
}
