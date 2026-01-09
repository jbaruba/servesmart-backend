package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrder_Id(Integer orderId);
    void deleteByOrder_Id(Integer orderId);
    Optional<OrderItem> findByIdAndOrder_Id(Integer id, Integer orderId);
}
