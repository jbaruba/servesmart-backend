package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByRestaurantTable_Id(Integer tableId);
    List<Orders> findByStatus_Name(String status);
    List<Orders> findByStatus_NameNotIn(List<String> statusNames);
}
