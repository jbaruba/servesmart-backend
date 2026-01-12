package com.jean.servesmart.restaurant.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.jean.servesmart.restaurant.dto.menuCategory.*;

public interface MenuCategoryService {

    MenuCategoryResponseDto create(MenuCategoryCreateDto dto);

    List<MenuCategoryResponseDto> getAll();

    Optional<MenuCategoryResponseDto> getById(Integer id);

    MenuCategoryResponseDto update(Integer id, MenuCategoryUpdateDto dto);

    boolean delete(Integer id);

    List<MenuCategoryResponseDto> getActive();
}
