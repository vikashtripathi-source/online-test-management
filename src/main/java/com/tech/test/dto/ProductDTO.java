package com.tech.test.dto;

import com.tech.test.enums.Branch;
import lombok.Data;

@Data
public class ProductDTO {

    private Long id;

    private String productName;

    private String description;

    private double price;

    private Branch branch;

    private String imageUrl;

    private String imageFilename;

    private int stockQuantity;

    private String sku;

    private boolean isActive;
}
