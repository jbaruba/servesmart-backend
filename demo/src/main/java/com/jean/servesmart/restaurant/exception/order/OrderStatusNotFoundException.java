package com.jean.servesmart.restaurant.exception.order;

public class OrderStatusNotFoundException extends RuntimeException {

    public OrderStatusNotFoundException() {
        super();
    }

    public OrderStatusNotFoundException(String message) {
        super(message);
    }
}
