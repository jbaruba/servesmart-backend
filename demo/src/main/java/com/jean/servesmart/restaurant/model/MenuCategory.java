package com.jean.servesmart.restaurant.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(
    name = "MenuCategory",
    uniqueConstraints = @UniqueConstraint(name = "UK_category_name", columnNames = "Name")
)
public class MenuCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    @Column(name = "Position", nullable = false)
    private Integer position = 0;

    @Column(name = "Is_active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MenuItems> items;

   
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<MenuItems> getItems() { return items; }
    public void setItems(List<MenuItems> items) { this.items = items; }
}
