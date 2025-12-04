package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(User user);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}
