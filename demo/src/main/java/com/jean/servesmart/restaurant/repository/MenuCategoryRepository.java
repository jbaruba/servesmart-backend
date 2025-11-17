package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Integer> {
    Optional<MenuCategory> findByName(String name);
    List<MenuCategory> findByActiveTrue();

}
