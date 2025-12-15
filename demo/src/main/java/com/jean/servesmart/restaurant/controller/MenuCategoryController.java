package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.MenuCategory.MenuCategoryCreateDto;
import com.jean.servesmart.restaurant.dto.MenuCategory.MenuCategoryResponseDto;
import com.jean.servesmart.restaurant.dto.MenuCategory.MenuCategoryUpdateDto;

import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryAlreadyExistsException;
import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryInvalidDataException;
import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryNotFoundException;

import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.MenuCategoryService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu-categories")
public class MenuCategoryController {

    private final MenuCategoryService service;

    public MenuCategoryController(MenuCategoryService service) {
        this.service = service;
    }

    @PostMapping
    @RolesAllowed({"ADMIN", "STAFF"})
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody MenuCategoryCreateDto dto) {
        try {
            MenuCategoryResponseDto category = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(category, "Category created successfully"));
        } catch (MenuCategoryInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid category data"));
        } catch (MenuCategoryAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Category name already exists"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create category"));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        try {
            List<MenuCategoryResponseDto> list = service.getAll();
            String message = list.isEmpty()
                    ? "No categories found"
                    : "Categories loaded";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load categories"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Integer id) {
        try {
            Optional<MenuCategoryResponseDto> category = service.getById(id);
            if (category.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Category not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(category.get(), "Category retrieved"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load category"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Integer id,
                                                 @RequestBody MenuCategoryUpdateDto dto) {
        try {
            MenuCategoryResponseDto updated = service.update(id, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "Category updated successfully"));
        } catch (MenuCategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Category not found"));
        } catch (MenuCategoryInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid category data"));
        } catch (MenuCategoryAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Category name already exists"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update category"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Integer id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
        } catch (MenuCategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Category not found"));
        } catch (MenuCategoryInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid category data"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete category"));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<?>> getActive() {
        try {
            List<MenuCategoryResponseDto> list = service.getActive();

            String message = list.isEmpty()
                    ? "No active categories found"
                    : "Active categories retrieved";

            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load active categories"));
        }
    }
}
