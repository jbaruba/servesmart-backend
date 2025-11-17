package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.dto.Auth.UserLoginDto;
import com.jean.servesmart.restaurant.dto.User.UserResponseDto;

public interface AuthService {
    UserResponseDto login(UserLoginDto dto);
}
