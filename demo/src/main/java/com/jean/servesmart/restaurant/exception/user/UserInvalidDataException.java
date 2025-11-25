package com.jean.servesmart.restaurant.exception.user;

public class UserInvalidDataException extends RuntimeException {
    public UserInvalidDataException() {
        super();
    }

    public UserInvalidDataException(String message) {
        super(message);
    }

}
