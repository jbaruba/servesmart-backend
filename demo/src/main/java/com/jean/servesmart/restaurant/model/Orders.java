package com.jean.servesmart.restaurant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "User_id", nullable = false,
        foreignKey = @ForeignKey(name = "FK_Orders_User"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "Restaurant_table_id",
        foreignKey = @ForeignKey(name = "FK_Orders_Table"))
    private RestaurantTable restaurantTable;

    @ManyToOne(optional = false)
    @JoinColumn(name = "OrdersStatus_id", nullable = false,
        foreignKey = @ForeignKey(name = "FK_Orders_Status"))
    private OrdersStatus status;

    @Column(name = "Create_at", nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public RestaurantTable getRestaurantTable() { return restaurantTable; }
    public void setRestaurantTable(RestaurantTable restaurantTable) { this.restaurantTable = restaurantTable; }

    public OrdersStatus getStatus() { return status; }
    public void setStatus(OrdersStatus status) { this.status = status; }

    public LocalDateTime getCreateAt() { return createAt; }
    public void setCreateAt(LocalDateTime createAt) { this.createAt = createAt; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}
