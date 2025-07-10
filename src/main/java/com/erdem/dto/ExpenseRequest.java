package com.erdem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class ExpenseRequest {

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Amount cannot be empty")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Category ID cannot be empty")
    private Long categoryId;

    public @NotBlank(message = "Description cannot be empty") String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank(message = "Description cannot be empty") String description) {
        this.description = description;
    }

    public @NotNull(message = "Amount cannot be empty") @Positive(message = "Amount must be positive") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull(message = "Amount cannot be empty") @Positive(message = "Amount must be positive") BigDecimal amount) {
        this.amount = amount;
    }

    public @NotNull(message = "Category ID cannot be empty") Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@NotNull(message = "Category ID cannot be empty") Long categoryId) {
        this.categoryId = categoryId;
    }
}
