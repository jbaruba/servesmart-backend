package com.jean.servesmart.restaurant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "RestaurantTableStatus")
public class RestaurantTableStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Name", nullable = false, length = 20)
    private String name;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
