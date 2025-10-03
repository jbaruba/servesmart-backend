package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.MenuItemRequest;
import com.jean.servesmart.restaurant.dto.MenuItemResponse;
import com.jean.servesmart.restaurant.service.interfaces.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

  private final MenuItemService service;

  public MenuItemController(MenuItemService service) { this.service = service; }

  @GetMapping
  public List<MenuItemResponse> list(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Long categoryId) {
    return service.list(q, categoryId);
  }

  @PostMapping
  public MenuItemResponse create(@Valid @RequestBody MenuItemRequest req) {
    return service.create(req);
  }

  @PutMapping("/{id}")
  public MenuItemResponse update(@PathVariable Long id, @Valid @RequestBody MenuItemRequest req) {
    return service.update(id, req);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
