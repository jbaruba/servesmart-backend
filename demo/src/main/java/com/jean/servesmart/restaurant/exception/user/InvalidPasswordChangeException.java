package com.jean.servesmart.restaurant.exception.user;

public class InvalidPasswordChangeException extends RuntimeException {

    public InvalidPasswordChangeException() {
        super();
    }

    public InvalidPasswordChangeException(String message) {
        super(message);
    }
}
