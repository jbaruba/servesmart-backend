package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.*;
import com.jean.servesmart.restaurant.model.*;
import com.jean.servesmart.restaurant.repository.*;
import com.jean.servesmart.restaurant.service.interfaces.MenuItemService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemServiceImpl implements MenuItemService {

  private final MenuItemRepository menuRepo;
  private final CategoryRepository catRepo;

  public MenuItemServiceImpl(MenuItemRepository menuRepo, CategoryRepository catRepo) {
    this.menuRepo = menuRepo;
    this.catRepo = catRepo;
  }

  private MenuItemResponse toDto(MenuItem m) {
    var dto = new MenuItemResponse();
    dto.setId(m.getId());
    dto.setName(m.getName());
    dto.setPrice(m.getPrice());
    dto.setDescription(m.getDescription());
    dto.setActive(m.isActive());
    dto.setCategoryId(m.getCategory().getId());
    dto.setCategoryName(m.getCategory().getName());
    return dto;
  }

  @Override
  public List<MenuItemResponse> list(String q, Long categoryId) {
    return menuRepo.search(q, categoryId).stream().map(this::toDto).toList();
  }

  @Override
  public MenuItemResponse create(MenuItemRequest req) {
    Category cat = catRepo.findById(req.getCategoryId())
      .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    var m = new MenuItem();
    m.setName(req.getName());
    m.setPrice(req.getPrice());
    m.setDescription(req.getDescription());
    m.setActive(req.getActive() != null ? req.getActive() : true);
    m.setCategory(cat);

    return toDto(menuRepo.save(m));
  }

  @Override
  public MenuItemResponse update(Long id, MenuItemRequest req) {
    var m = menuRepo.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("MenuItem not found"));

    if (req.getCategoryId() != null && !req.getCategoryId().equals(m.getCategory().getId())) {
      var cat = catRepo.findById(req.getCategoryId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));
      m.setCategory(cat);
    }
    if (req.getName() != null) m.setName(req.getName());
    if (req.getPrice() != null) m.setPrice(req.getPrice());
    if (req.getDescription() != null) m.setDescription(req.getDescription());
    if (req.getActive() != null) m.setActive(req.getActive());

    return toDto(menuRepo.save(m));
  }

  @Override
  public void delete(Long id) {
    menuRepo.deleteById(id);
  }
}
