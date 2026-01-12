package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.menuCategory.MenuCategoryCreateDto;
import com.jean.servesmart.restaurant.dto.menuCategory.MenuCategoryResponseDto;
import com.jean.servesmart.restaurant.dto.menuCategory.MenuCategoryUpdateDto;
import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryAlreadyExistsException;
import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryInvalidDataException;
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

        if (dto == null) {
            throw new MenuCategoryInvalidDataException();
        }

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new MenuCategoryInvalidDataException();
        }

        if (dto.getPosition() == null || dto.getPosition() < 0) {
            throw new MenuCategoryInvalidDataException();
        }

        String trimmedName = dto.getName().trim();

        if (repo.existsByName(trimmedName)) {
            throw new MenuCategoryAlreadyExistsException();
        }

        Integer position = dto.getPosition();
        if (position != 0 && repo.existsByPosition(position)) {
            position = 0;
        }

        MenuCategory category = new MenuCategory();
        category.setName(trimmedName);
        category.setPosition(position);
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
        if (id == null) {
            throw new MenuCategoryInvalidDataException();
        }

        if (dto == null) {
            throw new MenuCategoryInvalidDataException();
        }

        MenuCategory category = repo.findById(id)
                .orElseThrow(MenuCategoryNotFoundException::new);

        if (dto.getName() != null) {
            if (dto.getName().isBlank()) {
                throw new MenuCategoryInvalidDataException();
            }

            String newName = dto.getName().trim();

            if (!newName.equals(category.getName()) && repo.existsByName(newName)) {
                throw new MenuCategoryAlreadyExistsException();
            }

            category.setName(newName);
        }

        if (dto.getPosition() != null) {
            if (dto.getPosition() < 0) {
                throw new MenuCategoryInvalidDataException();
            }

            Integer newPosition = dto.getPosition();

           
            if (newPosition != 0
                    && !newPosition.equals(category.getPosition())
                    && repo.existsByPosition(newPosition)) {
                newPosition = 0;
            }

            category.setPosition(newPosition);
        }

        if (dto.getActive() != null) {
            category.setActive(dto.getActive());
        }

        MenuCategory updated = repo.save(category);
        return toResponse(updated);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            throw new MenuCategoryInvalidDataException();
        }

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
