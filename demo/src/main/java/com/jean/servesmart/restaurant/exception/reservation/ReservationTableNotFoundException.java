package com.jean.servesmart.restaurant.exception.reservation;

public class ReservationTableNotFoundException extends RuntimeException {

    public ReservationTableNotFoundException() {
        super();
    }

    public ReservationTableNotFoundException(String message) {
        super(message);
    }
}
