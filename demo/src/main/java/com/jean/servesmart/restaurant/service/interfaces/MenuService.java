package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.dto.Menu.MenuItemDto;
import java.util.List;
import java.util.Optional;

public interface MenuService {

    MenuItemDto create(MenuItemDto dto);

    List<MenuItemDto> getAll();

    Optional<MenuItemDto> getById(Integer id);

    List<MenuItemDto> getByCategory(Integer categoryId);

    MenuItemDto update(Integer id, MenuItemDto dto);

    boolean delete(Integer id);
}
