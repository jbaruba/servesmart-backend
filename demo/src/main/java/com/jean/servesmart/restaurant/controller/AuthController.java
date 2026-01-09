package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.Auth.AuthResponseDto;
import com.jean.servesmart.restaurant.dto.Auth.UserLoginDto;
import com.jean.servesmart.restaurant.dto.LoginLog.LoginLogCreateDto;
import com.jean.servesmart.restaurant.exception.auth.AuthInvalidDataException;
import com.jean.servesmart.restaurant.exception.auth.InactiveAccountException;
import com.jean.servesmart.restaurant.exception.auth.InvalidCredentialsException;
import com.jean.servesmart.restaurant.exception.loginlog.LoginLogInvalidDataException;
import com.jean.servesmart.restaurant.exception.loginlog.LoginLogUserNotFoundException;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.AuthService;
import com.jean.servesmart.restaurant.service.interfaces.LoginLogService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;
    private final LoginLogService loginLogs;

    public AuthController(AuthService auth, LoginLogService loginLogs) {
        this.auth = auth;
        this.loginLogs = loginLogs;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody UserLoginDto dto) {
        try {
            AuthResponseDto authResponse = auth.login(dto);
            return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"));
        } catch (AuthInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid login data"));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid email or password"));
        } catch (InactiveAccountException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Account is inactive"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Login failed"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestBody LogoutRequest req) {
        try {
            if (req == null || req.getUserId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("User id is required for logout"));
            }

            LoginLogCreateDto dto = new LoginLogCreateDto();
            dto.setUserId(req.getUserId());
            dto.setStatus("LOGOUT");
            loginLogs.log(dto);

            return ResponseEntity.ok(ApiResponse.success(null, "Logout logged"));
        } catch (LoginLogUserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        } catch (LoginLogInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid logout data"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to log logout"));
        }
    }

    public static class LogoutRequest {
        private Integer userId;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }
    }
}
