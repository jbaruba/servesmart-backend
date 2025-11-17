package com.jean.servesmart.restaurant.dto.MenuCategory;

public class MenuCategoryResponseDto {
    private Integer id;
    private String name;
    private Integer position;
    private boolean active;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
