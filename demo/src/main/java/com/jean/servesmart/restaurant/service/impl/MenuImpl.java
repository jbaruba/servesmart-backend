package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.menu.MenuItemDto;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemAlreadyExistsException;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemCategoryNotFoundException;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemInvalidDataException;
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
        validateCreateDto(dto);

        MenuCategory category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(MenuItemCategoryNotFoundException::new);

        String name = dto.getName().trim();
        if (menuRepo.existsByCategory_IdAndName(category.getId(), name)) {
            throw new MenuItemAlreadyExistsException();
        }

        MenuItems item = new MenuItems();
        item.setCategory(category);
        item.setName(name);
        item.setDescription(normalizeDescription(dto.getDescription()));
        item.setPrice(dto.getPrice());
        item.setActive(dto.isActive());
        item.setGluten(dto.isGluten());
        item.setNuts(dto.isNuts());
        item.setDairy(dto.isDairy());
        item.setAlcohol(dto.isAlcohol());

        return toDto(menuRepo.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemDto> getAll() {
        return menuRepo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuItemDto> getById(Integer id) {
        if (id == null) {
            throw new MenuItemInvalidDataException();
        }
        return menuRepo.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemDto> getByCategory(Integer categoryId) {
        if (categoryId == null) {
            throw new MenuItemInvalidDataException();
        }
        return menuRepo.findByCategory_Id(categoryId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public MenuItemDto update(Integer id, MenuItemDto dto) {
        validateUpdateInput(id, dto);

        MenuItems item = menuRepo.findById(id)
                .orElseThrow(MenuItemNotFoundException::new);

        MenuCategory targetCategory = resolveCategory(item, dto);
        handleNameUpdate(item, dto, targetCategory);
        handlePriceUpdate(item, dto);

        item.setDescription(normalizeDescription(dto.getDescription()));
        item.setActive(dto.isActive());
        item.setGluten(dto.isGluten());
        item.setNuts(dto.isNuts());
        item.setDairy(dto.isDairy());
        item.setAlcohol(dto.isAlcohol());
        item.setCategory(targetCategory);

        return toDto(menuRepo.save(item));
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null || !menuRepo.existsById(id)) {
            throw new MenuItemNotFoundException();
        }
        menuRepo.deleteById(id);
        return true;
    }

    private void validateCreateDto(MenuItemDto dto) {
        if (dto == null || dto.getCategoryId() == null || dto.getName() == null || dto.getName().isBlank()
                || dto.getPrice() == null || dto.getPrice().doubleValue() < 0) {
            throw new MenuItemInvalidDataException();
        }
    }

    private void validateUpdateInput(Integer id, MenuItemDto dto) {
        if (id == null || dto == null) {
            throw new MenuItemInvalidDataException();
        }
    }

    private MenuCategory resolveCategory(MenuItems item, MenuItemDto dto) {
        if (dto.getCategoryId() == null) {
            return item.getCategory();
        }
        return categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(MenuItemCategoryNotFoundException::new);
    }

    private void handleNameUpdate(MenuItems item, MenuItemDto dto, MenuCategory targetCategory) {
        if (dto.getName() == null) {
            return;
        }
        if (dto.getName().isBlank()) {
            throw new MenuItemInvalidDataException();
        }

        String newName = dto.getName().trim();
        boolean nameChanged = !newName.equals(item.getName());
        boolean categoryChanged = !targetCategory.getId().equals(item.getCategory().getId());

        if ((nameChanged || categoryChanged)
                && menuRepo.existsByCategory_IdAndNameAndIdNot(
                        targetCategory.getId(), newName, item.getId())) {
            throw new MenuItemAlreadyExistsException();
        }

        item.setName(newName);
    }

    private void handlePriceUpdate(MenuItems item, MenuItemDto dto) {
        if (dto.getPrice() == null) {
            return;
        }
        if (dto.getPrice().doubleValue() < 0) {
            throw new MenuItemInvalidDataException();
        }
        item.setPrice(dto.getPrice());
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
