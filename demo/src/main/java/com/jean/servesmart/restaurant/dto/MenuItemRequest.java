package com.jean.servesmart.restaurant.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class MenuItemRequest {
    @NotBlank @Size(max = 150) private String name;
    @Positive private BigDecimal price;
    @Size(max = 1000) private String description;
      private Boolean active = true;
    @NotNull  private Long categoryId;

public String getName() { return name; }
public void setName(String name) { this.name = name; }
public BigDecimal getPrice() { return price; }
public void setPrice(BigDecimal price) { this.price = price; }
public String getDescription() { return description; }  public void setDescription(String description) { this.description = description; }
public Boolean getActive() { return active; }
 public void setActive(Boolean active) { this.active = active; }
public Long getCategoryId() { return categoryId; }
  public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

}