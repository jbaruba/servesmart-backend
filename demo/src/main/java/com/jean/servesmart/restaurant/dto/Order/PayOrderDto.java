package com.jean.servesmart.restaurant.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PayOrderDto {

    @NotBlank
    private String method; // CARD / CASH / OTHER

    @NotNull
    private Double paidAmount;

    private Double tip;
    private String note;

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }

    public Double getTip() { return tip; }
    public void setTip(Double tip) { this.tip = tip; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
