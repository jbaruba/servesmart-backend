package com.jean.servesmart.restaurant.exception.order;

public class OrderRestaurantTableNotFoundException extends RuntimeException {

    public OrderRestaurantTableNotFoundException() {
        super();
    }

    public OrderRestaurantTableNotFoundException(String message) {
        super(message);
    }
}
