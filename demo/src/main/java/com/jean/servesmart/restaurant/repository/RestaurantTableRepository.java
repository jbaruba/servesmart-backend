package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Integer> {
    Optional<RestaurantTable> findByLabel(String label);
    List<RestaurantTable> findByStatus_Name(String status);
    List<RestaurantTable> findByActiveTrue();
}
