package com.jean.servesmart.restaurant.dto.order;

import java.util.List;

public class OrderCreateDto {

    private Integer userId;
    private Integer restaurantTableId; 
    private String statusName; 
    private List<OrderItemCreateDto> items;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRestaurantTableId() {
        return restaurantTableId;
    }

    public void setRestaurantTableId(Integer restaurantTableId) {
        this.restaurantTableId = restaurantTableId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<OrderItemCreateDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemCreateDto> items) {
        this.items = items;
    }
}
