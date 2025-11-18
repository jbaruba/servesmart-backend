package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.MenuCategory.*;
import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryNotFoundException;
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
        MenuCategory category = new MenuCategory();
        category.setName(dto.getName());
        category.setPosition(dto.getPosition());
        category.setActive(dto.isActive());

        MenuCategory saved = repo.save(category);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuCategoryResponseDto> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuCategoryResponseDto> getById(Integer id) {
        return repo.findById(id)
                .map(this::toResponse);
    }

    @Override
    public MenuCategoryResponseDto update(Integer id, MenuCategoryUpdateDto dto) {
        MenuCategory category = repo.findById(id)
                .orElseThrow(MenuCategoryNotFoundException::new);

        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        if (dto.getPosition() != null) {
            category.setPosition(dto.getPosition());
        }
        if (dto.getActive() != null) {
            category.setActive(dto.getActive());
        }

        MenuCategory updated = repo.save(category);
        return toResponse(updated);
    }

    @Override
    public boolean delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new MenuCategoryNotFoundException();
        }

        repo.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuCategoryResponseDto> getActive() {
        return repo.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
