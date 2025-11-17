package com.jean.servesmart.restaurant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LoginLog")
public class LoginLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "User_id", nullable = false,
        foreignKey = @ForeignKey(name = "FK_LoginLog_User"))
    private User user;

    @Column(name = "Status", nullable = false, length = 20)
    private String status;

    @Column(name = "Date", nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
