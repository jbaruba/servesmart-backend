package com.jean.servesmart.restaurant.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jean.servesmart.restaurant.dto.MenuCategory.MenuCategoryCreateDto;
import com.jean.servesmart.restaurant.model.MenuCategory;
import com.jean.servesmart.restaurant.repository.MenuCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MenuCategoryIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuCategoryRepository repo;

    @BeforeEach
    void setup() {
        repo.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_success_returns201() throws Exception {
        MenuCategoryCreateDto dto = new MenuCategoryCreateDto();
        dto.setName("Starters");
        dto.setPosition(1);
        dto.setActive(true);

        mockMvc.perform(post("/api/menu-categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Starters"))
                .andExpect(jsonPath("$.data.position").value(1))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void getAll_returnsList() throws Exception {
        MenuCategory c = new MenuCategory();
        c.setName("Starters");
        c.setPosition(1);
        c.setActive(true);
        repo.save(c);

        mockMvc.perform(get("/api/menu-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("Starters"));
    }
}
