package com.jean.servesmart.restaurant.exception.reservation;

public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException() {
        super();
    }

    public ReservationNotFoundException(String message) {
        super(message);
    }
}
