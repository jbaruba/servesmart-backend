package com.jean.servesmart.restaurant.exception.loginlog;

public class LoginLogUserNotFoundException extends RuntimeException {

    public LoginLogUserNotFoundException() {
        super();
    }

    public LoginLogUserNotFoundException(String message) {
        super(message);
    }
}
