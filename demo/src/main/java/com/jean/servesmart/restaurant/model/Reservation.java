package com.jean.servesmart.restaurant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "Reservation", indexes = @Index(name = "IX_Reservation_Table_Time", columnList = "Restaurant_table_id,Event_datetime")
)
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Restaurant_table_id", nullable = false,
        foreignKey = @ForeignKey(name = "FK_Reservation_Table"))
    private RestaurantTable restaurantTable;

    @Column(name = "Full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "Party_size", nullable = false)
    private Integer partySize;

    @Column(name = "Phone_number", length = 32)
    private String phoneNumber;

    @Column(name = "Event_datetime", nullable = false)
    private LocalDateTime eventDateTime;

    @Column(name = "Status", nullable = false, length = 20)
    private String status;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public RestaurantTable getRestaurantTable() { return restaurantTable; }
    public void setRestaurantTable(RestaurantTable restaurantTable) { this.restaurantTable = restaurantTable; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Integer getPartySize() { return partySize; }
    public void setPartySize(Integer partySize) { this.partySize = partySize; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public void setEventDateTime(LocalDateTime eventDateTime) { this.eventDateTime = eventDateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
