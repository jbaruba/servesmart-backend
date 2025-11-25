package com.jean.servesmart.restaurant.exception.order;

public class OrderMenuItemNotFoundException extends RuntimeException {

    public OrderMenuItemNotFoundException() {
        super();
    }

    public OrderMenuItemNotFoundException(String message) {
        super(message);
    }
}
