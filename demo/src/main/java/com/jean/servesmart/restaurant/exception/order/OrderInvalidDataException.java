package com.jean.servesmart.restaurant.exception.order;

public class OrderInvalidDataException extends RuntimeException {

    public OrderInvalidDataException() {
        super();
    }

    public OrderInvalidDataException(String message) {
        super(message);
    }
}
