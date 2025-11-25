package com.jean.servesmart.restaurant.exception.restauranttable;

public class RestaurantTableStatusNotFoundException extends RuntimeException {

    public RestaurantTableStatusNotFoundException() {
        super();
    }

    public RestaurantTableStatusNotFoundException(String message) {
        super(message);
    }
}
