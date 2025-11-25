package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.RestaurantTableStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantTableStatusRepository extends JpaRepository<RestaurantTableStatus, Integer> {
    Optional<RestaurantTableStatus> findByName(String name);
}
