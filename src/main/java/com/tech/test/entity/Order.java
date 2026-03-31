package com.tech.test.entity;

import com.tech.test.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Student ID cannot be null")
    private Long studentId;

    @NotNull(message = "Total amount cannot be null")
    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    private Long addressId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    // Legacy fields for backward compatibility
    private String productName;
    private Integer quantity;
    private String address;
    private String city;
    private String zipCode;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }
}
