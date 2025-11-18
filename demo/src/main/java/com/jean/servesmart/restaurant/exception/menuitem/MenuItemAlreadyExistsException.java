package com.jean.servesmart.restaurant.exception.menuitem;

public class MenuItemAlreadyExistsException extends RuntimeException {
    public MenuItemAlreadyExistsException() {
        super();
    }

    public MenuItemAlreadyExistsException(String message) {
        super(message);
    }
}
