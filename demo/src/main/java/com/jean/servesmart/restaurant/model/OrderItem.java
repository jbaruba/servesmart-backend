package com.jean.servesmart.restaurant.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "OrderItem")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Orders_id", nullable = false,
        foreignKey = @ForeignKey(name = "FK_OrderItem_Orders"))
    private Orders order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Menu_items_id", nullable = false,
        foreignKey = @ForeignKey(name = "FK_OrderItem_MenuItems"))
    private MenuItems menuItem;

    @Column(name = "Items_name", nullable = false, length = 150)
    private String itemsName;

    @Column(name = "Items_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal itemsPrice;

    @Column(name = "Items_quantity", nullable = false)
    private Integer itemsQuantity = 1;

    @Column(name = "Is_active", nullable = false)
    private boolean active = true;

    @Column(name = "Notes", length = 255)
    private String notes;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Orders getOrder() { return order; }
    public void setOrder(Orders order) { this.order = order; }

    public MenuItems getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItems menuItem) { this.menuItem = menuItem; }

    public String getItemsName() { return itemsName; }
    public void setItemsName(String itemsName) { this.itemsName = itemsName; }

    public BigDecimal getItemsPrice() { return itemsPrice; }
    public void setItemsPrice(BigDecimal itemsPrice) { this.itemsPrice = itemsPrice; }

    public Integer getItemsQuantity() { return itemsQuantity; }
    public void setItemsQuantity(Integer itemsQuantity) { this.itemsQuantity = itemsQuantity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
