package com.jean.servesmart.restaurant.exception.reservation;

public class ReservationInvalidDataException extends RuntimeException {

    public ReservationInvalidDataException() {
        super();
    }

    public ReservationInvalidDataException(String message) {
        super(message);
    }
}
