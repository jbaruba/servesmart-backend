package com.jean.servesmart.restaurant.exception.menucategory;

public class MenuCategoryInvalidDataException extends RuntimeException {

    public MenuCategoryInvalidDataException() {
        super();
    }


     public MenuCategoryInvalidDataException(String message) {
        super(message);
    }
}
