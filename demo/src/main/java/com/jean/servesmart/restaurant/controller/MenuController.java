package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.Menu.MenuItemDto;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.MenuService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService service;

    public MenuController(MenuService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody MenuItemDto dto) {
        var item = service.create(dto);
        if (item == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to create menu item"));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(item, "Menu item created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        List<MenuItemDto> items = service.getAll();
        if (items.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.error("No menu items found"));
        return ResponseEntity.ok(ApiResponse.success(items, "Menu items loaded"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Integer id) {
        var item = service.getById(id);
        if (item.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Menu item not found"));
        return ResponseEntity.ok(ApiResponse.success(item.get(), "Menu item retrieved"));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<?>> getByCategory(@PathVariable Integer categoryId) {
        var list = service.getByCategory(categoryId);
        if (list.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.error("No items for this category"));
        return ResponseEntity.ok(ApiResponse.success(list, "Menu items retrieved"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Integer id, @RequestBody MenuItemDto dto) {
        var updated = service.update(id, dto);
        if (updated == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to update menu item"));
        return ResponseEntity.ok(ApiResponse.success(updated, "Menu item updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Integer id) {
        boolean deleted = service.delete(id);
        if (!deleted)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed to delete menu item"));
        return ResponseEntity.ok(ApiResponse.success(null, "Menu item deleted successfully"));
    }
}
