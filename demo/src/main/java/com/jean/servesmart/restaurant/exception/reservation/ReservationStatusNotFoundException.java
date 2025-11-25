package com.jean.servesmart.restaurant.exception.reservation;

public class ReservationStatusNotFoundException extends RuntimeException {

    public ReservationStatusNotFoundException() {
        super();
    }

    public ReservationStatusNotFoundException(String message) {
        super(message);
    }
}
