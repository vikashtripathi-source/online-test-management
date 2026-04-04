package com.tech.test.entity;

import com.tech.test.enums.Branch;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private String description;

    private double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Branch branch;

    private String imageUrl;
    
    private String imageFilename;

    private int stockQuantity;

    private String sku;

    private boolean isActive;
}
