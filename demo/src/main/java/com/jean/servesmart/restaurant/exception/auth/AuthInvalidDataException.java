package com.jean.servesmart.restaurant.exception.auth;

public class AuthInvalidDataException extends RuntimeException {

    public AuthInvalidDataException() {
        super();
    }

    public AuthInvalidDataException(String message) {
        super(message);
    }
}
