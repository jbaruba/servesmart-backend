package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.CategoryRequest;
import com.jean.servesmart.restaurant.dto.CategoryResponse;
import com.jean.servesmart.restaurant.service.interfaces.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

  private final CategoryService service;

  public CategoryController(CategoryService service) {
    this.service = service;
  }

  @GetMapping
  public List<CategoryResponse> list(@RequestParam(required = false) String q) {
    return service.list(q);
  }

  @PostMapping
  public CategoryResponse create(@Valid @RequestBody CategoryRequest req) {
    return service.create(req);
  }

  @PutMapping("/{id}")
  public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
    return service.update(id, req);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
