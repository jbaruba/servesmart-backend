package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.dto.MenuItemRequest;
import com.jean.servesmart.restaurant.dto.MenuItemResponse;
import java.util.List;

public interface MenuItemService {
  List<MenuItemResponse> list(String q, Long categoryId);
  MenuItemResponse create(MenuItemRequest req);
  MenuItemResponse update(Long id, MenuItemRequest req);
  void delete(Long id);
}
