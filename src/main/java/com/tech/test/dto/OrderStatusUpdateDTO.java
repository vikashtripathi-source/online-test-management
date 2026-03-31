package com.tech.test.dto;

import com.tech.test.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDTO {

    @NotNull(message = "Status cannot be null")
    private OrderStatus status;
}
