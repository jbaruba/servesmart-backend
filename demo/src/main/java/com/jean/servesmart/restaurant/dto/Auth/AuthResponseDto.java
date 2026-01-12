package com.jean.servesmart.restaurant.dto.auth;

import com.jean.servesmart.restaurant.dto.user.UserResponseDto;

public class AuthResponseDto {
    private UserResponseDto user;
    private String token;

    public UserResponseDto getUser() {
        return user;
    }

    public void setUser(UserResponseDto user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
