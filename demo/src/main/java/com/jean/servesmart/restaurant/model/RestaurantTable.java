package com.jean.servesmart.restaurant.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "RestaurantTable", uniqueConstraints = @UniqueConstraint(name = "UK_table_label", columnNames = "Label"))
public class RestaurantTable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Label", nullable = false, length = 100)
    private String label;

    @Column(name = "Status", nullable = false, length = 20)
    private String status;

    @Column(name = "Seats", nullable = false)
    private Integer seats;

    @Column(name = "Is_active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "restaurantTable")
    private List<Orders> orders;

    @OneToMany(mappedBy = "restaurantTable")
    private List<Reservation> reservations;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<Orders> getOrders() { return orders; }
    public void setOrders(List<Orders> orders) { this.orders = orders; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
}
