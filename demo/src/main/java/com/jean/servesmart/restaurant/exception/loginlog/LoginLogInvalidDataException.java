package com.jean.servesmart.restaurant.exception.loginlog;

public class LoginLogInvalidDataException extends RuntimeException {

    public LoginLogInvalidDataException() {
        super();
    }

    public LoginLogInvalidDataException(String message) {
        super(message);
    }
}
