package com.jean.servesmart.restaurant.exception.restauranttable;

public class RestaurantTableLabelAlreadyExistsException extends RuntimeException {

    public RestaurantTableLabelAlreadyExistsException() {
        super();
    }

    public RestaurantTableLabelAlreadyExistsException(String message) {
        super(message);
    }
}
