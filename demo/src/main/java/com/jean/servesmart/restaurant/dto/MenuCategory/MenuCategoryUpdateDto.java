package com.jean.servesmart.restaurant.dto.menucategory;

public class MenuCategoryUpdateDto {
    private String name;
    private Integer position;
    private Boolean active;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
