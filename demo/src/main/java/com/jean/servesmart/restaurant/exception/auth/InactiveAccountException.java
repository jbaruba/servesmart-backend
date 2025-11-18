package com.jean.servesmart.restaurant.exception.auth;

public class InactiveAccountException extends RuntimeException {

    public InactiveAccountException() {
        super();
    }

    public InactiveAccountException(String message) {
        super(message);
    }
}
