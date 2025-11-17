package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.User.*;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody UserRegisterDto dto) {
        var user = users.register(dto);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Email already in use or registration failed"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User registered successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        List<UserResponseDto> list = users.getAll();
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(ApiResponse.error("No users found"));
        }
        return ResponseEntity.ok(ApiResponse.success(list, "Users retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Integer id) {
        var user = users.getById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(user.get(), "User retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Integer id, @Valid @RequestBody UserUpdateDto dto) {
        var updated = users.updateProfile(id, dto);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update user — invalid ID or email already used"));
        }
        return ResponseEntity.ok(ApiResponse.success(updated, "User updated successfully"));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<?>> changePassword(@PathVariable Integer id,
                                                         @Valid @RequestBody ChangePasswordDto dto) {
        boolean success = users.changePassword(id, dto);
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Old password incorrect or new password invalid"));
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @GetMapping("/email-exists")
    public ResponseEntity<ApiResponse<?>> emailExists(@RequestParam String email) {
        boolean exists = users.emailExists(email);
        return ResponseEntity.ok(ApiResponse.success(exists, "Email existence check completed"));
    }
}
