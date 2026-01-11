package com.jean.servesmart.restaurant.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jean.servesmart.restaurant.dto.Auth.UserLoginDto;
import com.jean.servesmart.restaurant.model.Role;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.RoleRepository;
import com.jean.servesmart.restaurant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        // ✅ Delete with REAL table names (as shown in the error: PUBLIC.LOGIN_LOG)
        // Child first, then parent
        jdbcTemplate.execute("DELETE FROM LOGIN_LOG");
        jdbcTemplate.execute("DELETE FROM USERS");
        jdbcTemplate.execute("DELETE FROM ROLE");

        Role role = new Role();
        role.setName("ADMIN");
        role = roleRepo.save(role);

        User user = new User();
        user.setEmail("test@test.com");
        user.setPasswordHash(encoder.encode("password123"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role);
        user.setActive(true);
        user.setPhoneNumber("1234567890");
        user.setAddress("123 Test St");
        userRepo.save(user);
    }

    @Test
    void login_success_returnsTokenAndUser() throws Exception {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("test@test.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.data.user.email").value("test@test.com"));
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("test@test.com");
        dto.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
