package com.jean.servesmart.restaurant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Users", uniqueConstraints = @UniqueConstraint(name = "UK_user_email", columnNames = "Email"))

public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Email", nullable = false, length = 256)
    private String email;

    @Column(name = "Role", nullable = false, length = 20)
    private String role;

    @Column(name = "Password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "First_name", nullable = false, length = 150)
    private String firstName;

    @Column(name = "Last_name", nullable = false,  length = 150)
    private String lastName;

    @Column(name = "Address", nullable = false,  length = 255)
    private String address;

    @Column(name = "Phone_number", nullable = false,  length = 32)
    private String phoneNumber;

    @Column(name = "Is_active", nullable = false)
    private boolean active;

    @Column(name = "Create_at", nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user")
    private List<Orders> orders;

    @OneToMany(mappedBy = "user")
    private List<LoginLog> loginLogs;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreateAt() { return createAt; }
    public void setCreateAt(LocalDateTime createAt) { this.createAt = createAt; }

    public List<Orders> getOrders() { return orders; }
    public void setOrders(List<Orders> orders) { this.orders = orders; }

    public List<LoginLog> getLoginLogs() { return loginLogs; }
    public void setLoginLogs(List<LoginLog> loginLogs) { this.loginLogs = loginLogs; }
}
