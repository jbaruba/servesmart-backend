package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.Menu.MenuItemDto;
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

        if (dto == null) {
            throw new MenuItemInvalidDataException();
        }

        if (dto.getCategoryId() == null) {
            throw new MenuItemInvalidDataException();
        }
        MenuCategory category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(MenuItemCategoryNotFoundException::new);

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new MenuItemInvalidDataException();
        }

        if (dto.getPrice() == null || dto.getPrice().doubleValue() < 0) {
            throw new MenuItemInvalidDataException();
        }

        String trimmedName = dto.getName().trim();
        if (menuRepo.existsByCategory_IdAndName(category.getId(), trimmedName)) {
            throw new MenuItemAlreadyExistsException();
        }

        MenuItems item = new MenuItems();
        item.setCategory(category);
        item.setName(trimmedName);

        if (dto.getDescription() != null) {
            String desc = dto.getDescription().trim();
            item.setDescription(desc.isEmpty() ? null : desc);
        } else {
            item.setDescription(null);
        }

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
        if (id == null) {
            throw new MenuItemInvalidDataException();
        }

        return menuRepo.findById(id)
                .map(this::toDto);
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
                .collect(Collectors.toList());
    }

    @Override
    public MenuItemDto update(Integer id, MenuItemDto dto) {
        if (id == null) {
            throw new MenuItemInvalidDataException();
        }

        if (dto == null) {
            throw new MenuItemInvalidDataException();
        }

        MenuItems item = menuRepo.findById(id)
                .orElseThrow(MenuItemNotFoundException::new);

        MenuCategory targetCategory = item.getCategory();

        if (dto.getCategoryId() != null) {
            targetCategory = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(MenuItemCategoryNotFoundException::new);
        }

        if (dto.getName() != null) {
            if (dto.getName().isBlank()) {
                throw new MenuItemInvalidDataException();
            }
            String newName = dto.getName().trim();

            Integer targetCatId = targetCategory.getId();

            boolean nameChanged = !newName.equals(item.getName());
            boolean categoryChanged = !targetCatId.equals(item.getCategory().getId());

            if ((nameChanged || categoryChanged)
                    && menuRepo.existsByCategory_IdAndNameAndIdNot(targetCatId, newName, item.getId())) {
                throw new MenuItemAlreadyExistsException();
            }

            item.setName(newName);
        }

        if (dto.getDescription() != null) {
            String desc = dto.getDescription().trim();
            item.setDescription(desc.isEmpty() ? null : desc);
        }

        if (dto.getPrice() != null) {
            if (dto.getPrice().doubleValue() < 0) {
                throw new MenuItemInvalidDataException();
            }
            item.setPrice(dto.getPrice());
        }

        item.setActive(dto.isActive());
        item.setGluten(dto.isGluten());
        item.setNuts(dto.isNuts());
        item.setDairy(dto.isDairy());
        item.setAlcohol(dto.isAlcohol());

        item.setCategory(targetCategory);

        MenuItems updated = menuRepo.save(item);
        return toDto(updated);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            throw new MenuItemInvalidDataException();
        }

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
