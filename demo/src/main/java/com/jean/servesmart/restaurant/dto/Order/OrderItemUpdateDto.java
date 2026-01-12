package com.jean.servesmart.restaurant.dto.order;

import jakarta.validation.constraints.Min;

public class OrderItemUpdateDto {
    @Min(1)
    private Integer quantity;

    private String notes;
    private Boolean active;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
