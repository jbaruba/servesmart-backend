package com.jean.servesmart.restaurant.exception.reservation;

public class ReservationTimeSlotUnavailableException extends RuntimeException {

    public ReservationTimeSlotUnavailableException() {
        super();
    }

    public ReservationTimeSlotUnavailableException(String message) {
        super(message);
    }
}
