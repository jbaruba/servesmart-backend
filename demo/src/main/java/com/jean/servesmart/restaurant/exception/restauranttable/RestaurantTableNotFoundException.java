package com.jean.servesmart.restaurant.exception.restauranttable;

public class RestaurantTableNotFoundException extends RuntimeException {

    public RestaurantTableNotFoundException() {
        super();
    }

    public RestaurantTableNotFoundException(String message) {
        super(message);
    }
}
