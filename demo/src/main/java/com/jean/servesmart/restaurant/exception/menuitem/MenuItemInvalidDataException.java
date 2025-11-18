package com.jean.servesmart.restaurant.exception.menuitem;

public class MenuItemInvalidDataException extends RuntimeException {
    public MenuItemInvalidDataException() {
        super();
    }

    public MenuItemInvalidDataException(String message) {
        super(message);
    }
}
