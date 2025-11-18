package com.jean.servesmart.restaurant.exception.menuitem;

public class MenuItemNotFoundException extends RuntimeException {

    public MenuItemNotFoundException() {
        super();
    }

    public MenuItemNotFoundException(String message) {
        super(message);
    }
}
