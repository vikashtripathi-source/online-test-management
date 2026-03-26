package com.tech.test.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    
    @NotBlank(message = "Product name cannot be blank")
    private String productName;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
    
    private String address;
    
    @NotBlank(message = "City cannot be blank")
    private String city;
    
    @NotBlank(message = "Zip code cannot be blank")
    private String zipCode;

    private LocalDateTime createdDate;

    private Long studentId;
}
