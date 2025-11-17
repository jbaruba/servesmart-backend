package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.dto.User.*;
import java.util.List;
import java.util.Optional;

public interface UserService {

    UserResponseDto register(UserRegisterDto dto);
    Optional<UserResponseDto> getById(Integer id);
    List<UserResponseDto> getAll();
    UserResponseDto updateProfile(Integer id, UserUpdateDto dto);
    boolean changePassword(Integer id, ChangePasswordDto dto);
    boolean emailExists(String email);
}
