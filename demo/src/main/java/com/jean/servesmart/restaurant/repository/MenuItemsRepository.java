package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.MenuItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemsRepository extends JpaRepository<MenuItems, Integer> {
    List<MenuItems> findByCategory_Id(Integer categoryId);
    List<MenuItems> findByActiveTrue();
    List<MenuItems> findByNameContainingIgnoreCase(String namePart);
}
