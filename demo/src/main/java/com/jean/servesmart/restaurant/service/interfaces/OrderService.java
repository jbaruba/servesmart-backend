package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.dto.Order.OrderCreateDto;
import com.jean.servesmart.restaurant.dto.Order.OrderResponseDto;
import com.jean.servesmart.restaurant.dto.Order.OrderStatusUpdateDto;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderResponseDto create(OrderCreateDto dto);

    boolean delete(Integer id);

    Optional<OrderResponseDto> getById(Integer id);

    List<OrderResponseDto> getByTable(Integer tableId);

    List<OrderResponseDto> getByStatus(String statusName);

    OrderResponseDto updateStatus(Integer id, OrderStatusUpdateDto dto);
}
