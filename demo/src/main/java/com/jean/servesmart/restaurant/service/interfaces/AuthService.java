package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.dto.auth.AuthResponseDto;
import com.jean.servesmart.restaurant.dto.auth.UserLoginDto;

public interface AuthService {
    AuthResponseDto login(UserLoginDto dto);
}
