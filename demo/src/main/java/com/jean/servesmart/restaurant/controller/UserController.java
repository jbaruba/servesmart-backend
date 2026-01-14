package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.user.ChangePasswordDto;
import com.jean.servesmart.restaurant.dto.user.UserRegisterDto;
import com.jean.servesmart.restaurant.dto.user.UserResponseDto;
import com.jean.servesmart.restaurant.dto.user.UserUpdateDto;
import com.jean.servesmart.restaurant.exception.user.InvalidPasswordChangeException;
import com.jean.servesmart.restaurant.exception.user.UserEmailAlreadyUsedException;
import com.jean.servesmart.restaurant.exception.user.UserInvalidDataException;
import com.jean.servesmart.restaurant.exception.user.UserNotFoundException;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final String USER_NOT_FOUND = "User not found";

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@Valid @RequestBody UserRegisterDto dto) {
        try {
            UserResponseDto user = users.register(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(user, "User registered successfully"));
        } catch (UserInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid user data"));
        } catch (UserEmailAlreadyUsedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Email already in use"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed"));
        }
    }

    @RolesAllowed("ADMIN")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAll() {
        try {
            List<UserResponseDto> list = users.getAll();
            String message = list.isEmpty() ? "No users found" : "Users retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load users"));
        }
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getById(@PathVariable Integer id) {
        try {
            Optional<UserResponseDto> user = users.getById(id);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(USER_NOT_FOUND));
            }
            return ResponseEntity.ok(ApiResponse.success(user.get(), "User retrieved successfully"));
        } catch (UserInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid user id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load user"));
        }
    }

    @RolesAllowed("ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(@PathVariable Integer id, @Valid @RequestBody UserUpdateDto dto) {
        try {
            UserResponseDto updated = users.updateProfile(id, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "User updated successfully"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(USER_NOT_FOUND));
        } catch (UserInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid user data"));
        } catch (UserEmailAlreadyUsedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Email already in use"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update user"));
        }
    }

    @RolesAllowed("ADMIN")
    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable Integer id, @Valid @RequestBody ChangePasswordDto dto) {
        try {
            users.changePassword(id, dto);
            return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(USER_NOT_FOUND));
        } catch (UserInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid password change data"));
        } catch (InvalidPasswordChangeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Old password incorrect or new password invalid"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to change password"));
        }
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/email-exists")
    public ResponseEntity<ApiResponse<Boolean>> emailExists(@RequestParam String email) {
        try {
            boolean exists = users.emailExists(email);
            return ResponseEntity.ok(ApiResponse.success(exists, "Email existence check completed"));
        } catch (UserInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid email"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check email existence"));
        }
    }

    @RolesAllowed("ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer id) {
        try {
            users.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(USER_NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete user"));
        }
    }
}
