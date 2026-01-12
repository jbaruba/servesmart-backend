package com.jean.servesmart.restaurant.dto.menuCategory;

public class MenuCategoryCreateDto {
    private String name;
    private Integer position;
    private boolean active;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
