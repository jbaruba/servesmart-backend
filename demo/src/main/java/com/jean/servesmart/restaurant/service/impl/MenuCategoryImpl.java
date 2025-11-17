package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.MenuCategory.*;
import com.jean.servesmart.restaurant.model.MenuCategory;
import com.jean.servesmart.restaurant.repository.MenuCategoryRepository;
import com.jean.servesmart.restaurant.service.interfaces.MenuCategoryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuCategoryImpl implements MenuCategoryService {

    private final MenuCategoryRepository repo;

    public MenuCategoryImpl(MenuCategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public MenuCategoryResponseDto create(MenuCategoryCreateDto dto) {
        try {
            MenuCategory category = new MenuCategory();
            category.setName(dto.getName());
            category.setPosition(dto.getPosition());
            category.setActive(dto.isActive());
            MenuCategory saved = repo.save(category);
            return toResponse(saved);
        } catch (Exception e) {
            System.err.println("Error in create(): " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuCategoryResponseDto> getAll() {
        try {
            return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getAll(): " + e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuCategoryResponseDto> getById(Integer id) {
        try {
            return repo.findById(id).map(this::toResponse);
        } catch (Exception e) {
            System.err.println("Error in getById(): " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public MenuCategoryResponseDto update(Integer id, MenuCategoryUpdateDto dto) {
        try {
            Optional<MenuCategory> optional = repo.findById(id);
            if (optional.isEmpty()) return null;

            MenuCategory category = optional.get();

            if (dto.getName() != null) category.setName(dto.getName());
            if (dto.getPosition() != null) category.setPosition(dto.getPosition());
            if (dto.getActive() != null) category.setActive(dto.getActive());

            MenuCategory updated = repo.save(category);
            return toResponse(updated);
        } catch (Exception e) {
            System.err.println("Error in update(): " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try {
            if (!repo.existsById(id)) return false;
            repo.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error in delete(): " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuCategoryResponseDto> getActive() {
        try {
            return repo.findByActiveTrue().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getActive(): " + e.getMessage());
            return List.of();
        }
    }

    private MenuCategoryResponseDto toResponse(MenuCategory c) {
        MenuCategoryResponseDto dto = new MenuCategoryResponseDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setPosition(c.getPosition());
        dto.setActive(c.isActive());
        return dto;
    }
}
