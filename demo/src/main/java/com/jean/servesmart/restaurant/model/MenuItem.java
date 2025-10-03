package com.jean.servesmart.restaurant.model;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "MenuItem")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

    @Column(nullable = false, length = 150) private String name;
    @Column(precision = 5, scale = 2, nullable = false) private BigDecimal price;
    @Column(nullable = false, length = 1000) private String description;
    @Column(nullable = false) private boolean active = true;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") private Category category;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }
  public Category getCategory() { return category; }
  public void setCategory(Category category) { this.category = category; }
}
