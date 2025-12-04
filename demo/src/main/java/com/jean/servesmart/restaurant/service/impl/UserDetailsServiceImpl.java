package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.UserRepository;
import com.jean.servesmart.restaurant.service.interfaces.UserDetailsServiceCustom;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsServiceCustom {

    private final UserRepository userRepo;

    public UserDetailsServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String roleName = user.getRole() != null ? user.getRole().getName() : null;

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roleName != null && !roleName.isBlank()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.isActive(),
                true,
                true,
                true,
                authorities
        );
    }
}
