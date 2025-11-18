package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.User.ChangePasswordDto;
import com.jean.servesmart.restaurant.dto.User.UserRegisterDto;
import com.jean.servesmart.restaurant.dto.User.UserResponseDto;
import com.jean.servesmart.restaurant.dto.User.UserUpdateDto;
import com.jean.servesmart.restaurant.exception.user.InvalidPasswordChangeException;
import com.jean.servesmart.restaurant.exception.user.UserEmailAlreadyUsedException;
import com.jean.servesmart.restaurant.exception.user.UserNotFoundException;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody UserRegisterDto dto) {
        try {
            UserResponseDto user = users.register(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(user, "User registered successfully"));
        } catch (UserEmailAlreadyUsedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Email already in use"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed"));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        try {
            List<UserResponseDto> list = users.getAll();

            String message = list.isEmpty()
                    ? "No users found"
                    : "Users retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load users"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Integer id) {
        try {
            Optional<UserResponseDto> user = users.getById(id);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(user.get(), "User retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load user"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Integer id,
                                                 @Valid @RequestBody UserUpdateDto dto) {
        try {
            UserResponseDto updated = users.updateProfile(id, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "User updated successfully"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        } catch (UserEmailAlreadyUsedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Email already in use"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update user"));
        }
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<?>> changePassword(@PathVariable Integer id,
                                                         @Valid @RequestBody ChangePasswordDto dto) {
        try {
            users.changePassword(id, dto);
            return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        } catch (InvalidPasswordChangeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Old password incorrect or new password invalid"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to change password"));
        }
    }

    @GetMapping("/email-exists")
    public ResponseEntity<ApiResponse<?>> emailExists(@RequestParam String email) {
        try {
            boolean exists = users.emailExists(email);
            return ResponseEntity.ok(ApiResponse.success(exists, "Email existence check completed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check email existence"));
        }
    }
}
