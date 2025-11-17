package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.dto.MenuCategory.*;
import java.util.List;
import java.util.Optional;

public interface MenuCategoryService {

    MenuCategoryResponseDto create(MenuCategoryCreateDto dto);

    List<MenuCategoryResponseDto> getAll();

    Optional<MenuCategoryResponseDto> getById(Integer id);

    MenuCategoryResponseDto update(Integer id, MenuCategoryUpdateDto dto);

    boolean delete(Integer id);

    List<MenuCategoryResponseDto> getActive();
}
