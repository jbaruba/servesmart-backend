package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.dto.Auth.AuthResponseDto;
import com.jean.servesmart.restaurant.dto.Auth.UserLoginDto;

public interface AuthService {
    AuthResponseDto login(UserLoginDto dto);
}
