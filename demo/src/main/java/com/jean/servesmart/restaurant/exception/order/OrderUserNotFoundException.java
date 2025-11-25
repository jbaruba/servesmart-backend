package com.jean.servesmart.restaurant.exception.order;

public class OrderUserNotFoundException extends RuntimeException {

    public OrderUserNotFoundException() {
        super();
    }

    public OrderUserNotFoundException(String message) {
        super(message);
    }
}
