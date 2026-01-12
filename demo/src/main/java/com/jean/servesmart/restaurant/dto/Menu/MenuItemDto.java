package com.jean.servesmart.restaurant.dto.menu;

import java.math.BigDecimal;

public class MenuItemDto {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean active;
    private boolean gluten;
    private boolean nuts;
    private boolean dairy;
    private boolean alcohol;
    private Integer categoryId;
    private String categoryName;


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
