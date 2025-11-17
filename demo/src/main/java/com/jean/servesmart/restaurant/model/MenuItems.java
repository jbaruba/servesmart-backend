package com.jean.servesmart.restaurant.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table( name = "MenuItems", uniqueConstraints = { @UniqueConstraint(name = "UK_item_name_per_category", columnNames = {"Menu_categories_id", "Name"})
    }
)
public class MenuItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "Menu_categories_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_MenuItems_Category")
    )
    private MenuCategory category;

    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    @Column(name = "Description", length = 2000)
    private String description;

    @Column(name = "Price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

  
    @Column(name = "Is_active", nullable = false)
    private boolean active = true;

    @Column(name = "Gluten", nullable = false)
    private boolean gluten;

    @Column(name = "Nuts", nullable = false)
    private boolean nuts;

    @Column(name = "Dairy", nullable = false)
    private boolean dairy;

    @Column(name = "Alcohol", nullable = false)
    private boolean alcohol;

    @OneToMany(mappedBy = "menuItem")
    private List<OrderItem> orderItems;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public MenuCategory getCategory() { return category; }
    public void setCategory(MenuCategory category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isGluten() { return gluten; }
    public void setGluten(boolean gluten) { this.gluten = gluten; }

    public boolean isNuts() { return nuts; }
    public void setNuts(boolean nuts) { this.nuts = nuts; }

    public boolean isDairy() { return dairy; }
    public void setDairy(boolean dairy) { this.dairy = dairy; }

    public boolean isAlcohol() { return alcohol; }
    public void setAlcohol(boolean alcohol) { this.alcohol = alcohol; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}
