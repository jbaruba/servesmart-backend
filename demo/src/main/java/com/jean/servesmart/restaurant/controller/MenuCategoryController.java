package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.MenuCategory.*;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.MenuCategoryService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-categories")
public class MenuCategoryController {

    private final MenuCategoryService service;

    public MenuCategoryController(MenuCategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody MenuCategoryCreateDto dto) {
        var category = service.create(dto);
        if (category == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to create category"));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(category, "Category created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        List<MenuCategoryResponseDto> list = service.getAll();
        if (list.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.error("No categories found"));
        return ResponseEntity.ok(ApiResponse.success(list, "Categories loaded"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Integer id) {
        var category = service.getById(id);
        if (category.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Category not found"));
        return ResponseEntity.ok(ApiResponse.success(category.get(), "Category retrieved"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Integer id, @RequestBody MenuCategoryUpdateDto dto) {
        var updated = service.update(id, dto);
        if (updated == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to update category"));
        return ResponseEntity.ok(ApiResponse.success(updated, "Category updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Integer id) {
        boolean deleted = service.delete(id);
        if (!deleted)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to delete category"));
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<?>> getActive() {
        List<MenuCategoryResponseDto> list = service.getActive();
        if (list.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.error("No active categories found"));
        return ResponseEntity.ok(ApiResponse.success(list, "Active categories retrieved"));
    }
}
