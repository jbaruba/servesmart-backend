package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.model.Role;
import com.jean.servesmart.restaurant.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtImplTest {

    private static String base64Secret() {
        // 32 bytes minimum for HS256
        byte[] raw = "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(raw);
    }

    @Test
    void generateToken_whenRoleIsNull_doesNotThrow_andRoleClaimMissingOrNull() {
        JwtImpl jwt = new JwtImpl(base64Secret(), 60_000);

        User user = new User();
        user.setId(7);
        user.setEmail("test@gmail.com");
        user.setRole(null);

        String token = jwt.generateToken(user);
        assertNotNull(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret())))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("test@gmail.com", claims.getSubject());
        assertEquals(7, ((Number) claims.get("userId")).intValue());

        // claim might be missing -> get returns null
        assertNull(claims.get("role"));
    }

    @Test
    void generateToken_whenRoleExists_includesRoleClaim() {
        JwtImpl jwt = new JwtImpl(base64Secret(), 60_000);

        Role role = new Role();
        role.setName("ADMIN");

        User user = new User();
        user.setId(10);
        user.setEmail("admin@gmail.com");
        user.setRole(role);

        String token = jwt.generateToken(user);
        assertNotNull(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret())))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("admin@gmail.com", claims.getSubject());
        assertEquals(10, ((Number) claims.get("userId")).intValue());
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    void extractUsername_returnsSubjectEmail() {
        JwtImpl jwt = new JwtImpl(base64Secret(), 60_000);

        Role role = new Role();
        role.setName("STAFF");

        User user = new User();
        user.setId(1);
        user.setEmail("staff@gmail.com");
        user.setRole(role);

        String token = jwt.generateToken(user);

        assertEquals("staff@gmail.com", jwt.extractUsername(token));
    }

    @Test
    void isTokenValid_whenMatchesUserAndNotExpired_returnsTrue() {
        JwtImpl jwt = new JwtImpl(base64Secret(), 60_000);

        User user = new User();
        user.setId(5);
        user.setEmail("ok@gmail.com");

        String token = jwt.generateToken(user);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("ok@gmail.com");

        assertTrue(jwt.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_whenUsernameDifferent_returnsFalse() {
        JwtImpl jwt = new JwtImpl(base64Secret(), 60_000);

        User user = new User();
        user.setId(5);
        user.setEmail("ok@gmail.com");

        String token = jwt.generateToken(user);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("other@gmail.com");

        assertFalse(jwt.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_whenExpired_returnsFalse() throws Exception {
        JwtImpl jwt = new JwtImpl(base64Secret(), 1); // 1ms

        User user = new User();
        user.setId(5);
        user.setEmail("expired@gmail.com");

        String token = jwt.generateToken(user);

        // ensure it is expired
        Thread.sleep(10);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("expired@gmail.com");

        assertFalse(jwt.isTokenValid(token, userDetails));
    }

    @Test
    void generateToken_whenUserMissingIdOrEmail_throwsIllegalArgumentException() {
        JwtImpl jwt = new JwtImpl(base64Secret(), 60_000);

        User user = new User();
        user.setId(null);
        user.setEmail("x@gmail.com");

        assertThrows(IllegalArgumentException.class, () -> jwt.generateToken(user));
    }
}
