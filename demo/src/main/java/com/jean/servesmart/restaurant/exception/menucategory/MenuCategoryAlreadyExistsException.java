package com.jean.servesmart.restaurant.exception.menucategory;

public class MenuCategoryAlreadyExistsException extends RuntimeException {

    public MenuCategoryAlreadyExistsException() {
        super();
    }

    public MenuCategoryAlreadyExistsException(String message) {
        super(message);
    }

}
