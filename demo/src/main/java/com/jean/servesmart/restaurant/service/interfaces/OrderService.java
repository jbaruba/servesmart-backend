package com.jean.servesmart.restaurant.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.jean.servesmart.restaurant.dto.order.*;

public interface OrderService {

    // CRUD
    OrderResponseDto create(OrderCreateDto dto);

    boolean delete(Integer id);

    Optional<OrderResponseDto> getById(Integer id);

    List<OrderResponseDto> getByTable(Integer tableId);

    List<OrderResponseDto> getByStatus(String statusName);

    OrderResponseDto updateStatus(Integer id, OrderStatusUpdateDto dto);

    // ORDER ITEMS
    OrderResponseDto addItem(Integer orderId, OrderItemCreateDto dto);

    OrderResponseDto updateItem(Integer orderId, Integer itemId, OrderItemUpdateDto dto);
    OrderResponseDto removeItem(Integer orderId, Integer itemId);

    // STAFF OPERATIONS
    List<OrderResponseDto> getPaid();
    List<OrderResponseDto> getOpenByTable();
    OrderResponseDto start(Integer userId, Integer restaurantTableId);

    // PAYMENT
    OrderResponseDto pay(Integer orderId, PayOrderDto dto);
}
