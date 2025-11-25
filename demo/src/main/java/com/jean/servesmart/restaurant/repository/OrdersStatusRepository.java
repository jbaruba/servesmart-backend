package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.OrdersStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersStatusRepository extends JpaRepository<OrdersStatus, Integer> {
    Optional<OrdersStatus> findByName(String name);
}
