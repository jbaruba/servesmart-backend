package com.jean.servesmart.restaurant.exception.menucategory;

public class MenuCategoryNotFoundException extends RuntimeException {

    public MenuCategoryNotFoundException() {
        super();
    }

    public MenuCategoryNotFoundException(String message) {
        super(message);
    }
}
