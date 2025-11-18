package com.jean.servesmart.restaurant.exception.user;

public class UserEmailAlreadyUsedException extends RuntimeException {

    public UserEmailAlreadyUsedException() {
        super();
    }

    public UserEmailAlreadyUsedException(String message) {
        super(message);
    }
}
