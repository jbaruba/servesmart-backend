package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.Menu.MenuItemDto;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemAlreadyExistsException;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemCategoryNotFoundException;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemInvalidDataException;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemNotFoundException;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.MenuService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService service;

    public MenuController(MenuService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody MenuItemDto dto) {
        try {
            MenuItemDto item = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(item, "Menu item created successfully"));
        } catch (MenuItemInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid menu item data"));
        } catch (MenuItemCategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Category for menu item not found"));
        } catch (MenuItemAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Menu item name already exists in this category"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create menu item"));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        try {
            List<MenuItemDto> items = service.getAll();

            String message = items.isEmpty()
                    ? "No menu items found"
                    : "Menu items loaded";
            return ResponseEntity.ok(ApiResponse.success(items, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load menu items"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Integer id) {
        try {
            Optional<MenuItemDto> item = service.getById(id);
            if (item.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Menu item not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(item.get(), "Menu item retrieved"));
        } catch (MenuItemInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid menu item id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load menu item"));
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<?>> getByCategory(@PathVariable Integer categoryId) {
        try {
            List<MenuItemDto> list = service.getByCategory(categoryId);

            String message = list.isEmpty()
                    ? "No menu items for this category"
                    : "Menu items retrieved";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (MenuItemInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid category id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load menu items for category"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Integer id,
                                                 @RequestBody MenuItemDto dto) {
        try {
            MenuItemDto updated = service.update(id, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "Menu item updated successfully"));
        } catch (MenuItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Menu item not found"));
        } catch (MenuItemCategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Category for menu item not found"));
        } catch (MenuItemInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid data for menu item"));
        } catch (MenuItemAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Menu item name already exists in this category"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update menu item"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Integer id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Menu item deleted successfully"));
        } catch (MenuItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Menu item not found"));
        } catch (MenuItemInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid menu item id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete menu item"));
        }
    }
}
