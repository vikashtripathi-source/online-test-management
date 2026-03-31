package com.tech.test.dto;

import com.tech.test.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;

    @NotNull(message = "Student ID cannot be null")
    private Long studentId;

    private List<OrderItemDTO> orderItems;

    @NotNull(message = "Total amount cannot be null")
    private BigDecimal totalAmount;

    private OrderStatus status;

    private LocalDateTime orderDate;

    private Long addressId;

    // Legacy fields for backward compatibility
    private String productName;

    private Integer quantity;

    private String address;

    private String city;

    private String zipCode;

    private LocalDateTime createdDate;
}
