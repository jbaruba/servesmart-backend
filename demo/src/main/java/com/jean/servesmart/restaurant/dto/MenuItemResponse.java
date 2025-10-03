package com.jean.servesmart.restaurant.dto;

import java.math.BigDecimal;

public class MenuItemResponse {
  private Long id;
  private String name;
  private BigDecimal price;
  private String description;
  private boolean active;
  private Long categoryId;
  private String categoryName;

 
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
  public Long getCategoryId() { return categoryId; }
  public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
  public String getCategoryName() { return categoryName; }
  public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
