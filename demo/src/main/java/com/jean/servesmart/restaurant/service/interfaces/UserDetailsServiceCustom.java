package com.jean.servesmart.restaurant.service.interfaces;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsServiceCustom {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
