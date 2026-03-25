package com.tech.test.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryDTO {

    private Long id;
    
    @NotBlank(message = "Product name cannot be blank")
    private String productName;
    
    @NotNull(message = "Stock quantity cannot be null")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;
}
