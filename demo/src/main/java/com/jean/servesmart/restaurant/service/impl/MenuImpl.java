package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.Menu.MenuItemDto;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemException;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemNotFoundException;
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
        // Zorg dat de category bestaat
        MenuCategory category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(MenuItemException::new);

        MenuItems item = new MenuItems();
        item.setCategory(category);
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setActive(dto.isActive());
        item.setGluten(dto.isGluten());
        item.setNuts(dto.isNuts());
        item.setDairy(dto.isDairy());
        item.setAlcohol(dto.isAlcohol());

        MenuItems saved = menuRepo.save(item);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemDto> getAll() {
        return menuRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuItemDto> getById(Integer id) {
        return menuRepo.findById(id)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemDto> getByCategory(Integer categoryId) {
        return menuRepo.findByCategory_Id(categoryId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MenuItemDto update(Integer id, MenuItemDto dto) {
        MenuItems item = menuRepo.findById(id)
                .orElseThrow(MenuItemNotFoundException::new);

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            item.setPrice(dto.getPrice());
        }

        item.setActive(dto.isActive());
        item.setGluten(dto.isGluten());
        item.setNuts(dto.isNuts());
        item.setDairy(dto.isDairy());
        item.setAlcohol(dto.isAlcohol());

        if (dto.getCategoryId() != null) {
            MenuCategory category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(MenuItemException::new);
            item.setCategory(category);
        }

        MenuItems updated = menuRepo.save(item);
        return toDto(updated);
    }

    @Override
    public boolean delete(Integer id) {
        if (!menuRepo.existsById(id)) {
            throw new MenuItemNotFoundException();
        }

        menuRepo.deleteById(id);
        return true;
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
