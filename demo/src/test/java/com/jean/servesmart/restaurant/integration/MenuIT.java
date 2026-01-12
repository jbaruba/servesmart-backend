package com.jean.servesmart.restaurant.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jean.servesmart.restaurant.dto.menu.MenuItemDto;
import com.jean.servesmart.restaurant.model.MenuCategory;
import com.jean.servesmart.restaurant.model.MenuItems;
import com.jean.servesmart.restaurant.repository.MenuCategoryRepository;
import com.jean.servesmart.restaurant.repository.MenuItemsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MenuIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuCategoryRepository categoryRepo;

    @Autowired
    private MenuItemsRepository menuRepo;

    private Integer categoryId;

    @BeforeEach
    void setup() {
        menuRepo.deleteAll();
        categoryRepo.deleteAll();

        MenuCategory cat = new MenuCategory();
        cat.setName("Starters");
        cat.setPosition(1);
        cat.setActive(true);
        categoryId = categoryRepo.save(cat).getId();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_success_returns201() throws Exception {
        MenuItemDto dto = new MenuItemDto();
        dto.setName("Soup");
        dto.setDescription("Tomato soup");
        dto.setPrice(BigDecimal.valueOf(5.50));
        dto.setActive(true);
        dto.setGluten(false);
        dto.setNuts(false);
        dto.setDairy(false);
        dto.setAlcohol(false);
        dto.setCategoryId(categoryId);

        mockMvc.perform(post("/api/menu")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Soup"))
                .andExpect(jsonPath("$.data.categoryId").value(categoryId));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void getByCategory_returnsItems() throws Exception {
        MenuCategory cat = categoryRepo.findById(categoryId).orElseThrow();

        MenuItems item = new MenuItems();
        item.setCategory(cat);
        item.setName("Soup");
        item.setDescription("Tomato soup");
        item.setPrice(BigDecimal.valueOf(5.50));
        item.setActive(true);
        item.setGluten(false);
        item.setNuts(false);
        item.setDairy(false);
        item.setAlcohol(false);
        menuRepo.save(item);

        mockMvc.perform(get("/api/menu/category/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("Soup"))
                .andExpect(jsonPath("$.data[0].categoryId").value(categoryId));
    }
}
