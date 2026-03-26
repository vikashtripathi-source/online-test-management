package com.tech.test.dto;

import lombok.Data;

@Data
public class ProductDTO {

    private Long id;

    private String productName;

    private String description;

    private double price;

    private String branch;

    private String imageUrl;
}