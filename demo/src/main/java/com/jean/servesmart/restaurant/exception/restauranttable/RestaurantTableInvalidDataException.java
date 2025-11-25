package com.jean.servesmart.restaurant.exception.restauranttable;

public class RestaurantTableInvalidDataException extends RuntimeException {

    public RestaurantTableInvalidDataException() {
        super();
    }

    public RestaurantTableInvalidDataException(String message) {
        super(message);
    }
}
