package com.jean.servesmart.restaurant.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.jean.servesmart.restaurant.dto.user.*;

public interface UserService {
    UserResponseDto register(UserRegisterDto dto);
    Optional<UserResponseDto> getById(Integer id);
    List<UserResponseDto> getAll();
    UserResponseDto updateProfile(Integer id, UserUpdateDto dto);
    boolean changePassword(Integer id, ChangePasswordDto dto);
    boolean emailExists(String email);
    void deleteUser(Integer id);

}
