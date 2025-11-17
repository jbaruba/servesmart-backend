package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.Menu.MenuItemDto;
import com.jean.servesmart.restaurant.model.MenuCategory;
import com.jean.servesmart.restaurant.model.MenuItems;
import com.jean.servesmart.restaurant.repository.MenuCategoryRepository;
import com.jean.servesmart.restaurant.repository.MenuItemsRepository;
import com.jean.servesmart.restaurant.service.interfaces.MenuService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuImpl implements MenuService {

    private final MenuItemsRepository menuRepo;
    private final MenuCategoryRepository categoryRepo;

    public MenuImpl(MenuItemsRepository menuRepo, MenuCategoryRepository categoryRepo) {
        this.menuRepo = menuRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public MenuItemDto create(MenuItemDto dto) {
        try {
            Optional<MenuCategory> category = categoryRepo.findById(dto.getCategoryId());
            if (category.isEmpty()) return null;

            MenuItems item = new MenuItems();
            item.setCategory(category.get());
            item.setName(dto.getName());
            item.setDescription(dto.getDescription());
            item.setPrice(dto.getPrice());
            item.setActive(dto.isActive());
            item.setGluten(dto.isGluten());
            item.setNuts(dto.isNuts());
            item.setDairy(dto.isDairy());
            item.setAlcohol(dto.isAlcohol());

            return toDto(menuRepo.save(item));

        } catch (Exception e) {
            System.err.println("Error in create(): " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemDto> getAll() {
        try {
            return menuRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getAll(): " + e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuItemDto> getById(Integer id) {
        try {
            return menuRepo.findById(id).map(this::toDto);
        } catch (Exception e) {
            System.err.println("Error in getById(): " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemDto> getByCategory(Integer categoryId) {
        try {
            return menuRepo.findByCategory_Id(categoryId).stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getByCategory(): " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public MenuItemDto update(Integer id, MenuItemDto dto) {
        try {
            Optional<MenuItems> optional = menuRepo.findById(id);
            if (optional.isEmpty()) return null;

            MenuItems item = optional.get();

            if (dto.getName() != null) item.setName(dto.getName());
            if (dto.getDescription() != null) item.setDescription(dto.getDescription());
            if (dto.getPrice() != null) item.setPrice(dto.getPrice());
            item.setActive(dto.isActive());
            item.setGluten(dto.isGluten());
            item.setNuts(dto.isNuts());
            item.setDairy(dto.isDairy());
            item.setAlcohol(dto.isAlcohol());

            if (dto.getCategoryId() != null) {
                categoryRepo.findById(dto.getCategoryId()).ifPresent(item::setCategory);
            }

            return toDto(menuRepo.save(item));

        } catch (Exception e) {
            System.err.println("Error in update(): " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try {
            if (!menuRepo.existsById(id)) return false;
            menuRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error in delete(): " + e.getMessage());
            return false;
        }
    }

    private MenuItemDto toDto(MenuItems item) {
        MenuItemDto dto = new MenuItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setActive(item.isActive());
        dto.setGluten(item.isGluten());
        dto.setNuts(item.isNuts());
        dto.setDairy(item.isDairy());
        dto.setAlcohol(item.isAlcohol());
        dto.setCategoryId(item.getCategory().getId());
        dto.setCategoryName(item.getCategory().getName());
        return dto;
    }
}
