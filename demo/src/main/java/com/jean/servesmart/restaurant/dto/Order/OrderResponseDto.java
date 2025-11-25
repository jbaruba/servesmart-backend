package com.jean.servesmart.restaurant.dto.Order;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDto {

    private Integer id;
    private Integer userId;
    private String userEmail;
    private Integer restaurantTableId;
    private String restaurantTableLabel;
    private String statusName;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDto> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getRestaurantTableId() {
        return restaurantTableId;
    }

    public void setRestaurantTableId(Integer restaurantTableId) {
        this.restaurantTableId = restaurantTableId;
    }

    public String getRestaurantTableLabel() {
        return restaurantTableLabel;
    }

    public void setRestaurantTableLabel(String restaurantTableLabel) {
        this.restaurantTableLabel = restaurantTableLabel;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItemResponseDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponseDto> items) {
        this.items = items;
    }
}
