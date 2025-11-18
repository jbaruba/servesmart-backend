package com.jean.servesmart.restaurant.exception.menuitem;

public class MenuItemCategoryNotFoundException extends RuntimeException {
    public MenuItemCategoryNotFoundException() {
        super();
    }

    public MenuItemCategoryNotFoundException(String message) {
        super(message);
    }
}
