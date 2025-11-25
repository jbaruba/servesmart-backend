package com.jean.servesmart.restaurant.dto.Reservation;

import java.time.LocalDateTime;

public class ReservationResponseDto {

    private Integer id;
    private Integer restaurantTableId;
    private String restaurantTableLabel;
    private String fullName;
    private Integer partySize;
    private String phoneNumber;
    private LocalDateTime eventDateTime;
    private String statusName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRestaurantTableId() {
        return restaurantTableId;
    }

    public void setRestaurantTableId(Integer restaurantTableId) {
        this.restaurantTableId = restaurantTableId;
    }

    public String getRestaurantTableLabel() {
        return restaurantTableLabel;
    }

    public void setRestaurantTableLabel(String restaurantTableLabel) {
        this.restaurantTableLabel = restaurantTableLabel;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getPartySize() {
        return partySize;
    }

    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
