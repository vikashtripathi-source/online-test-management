package com.tech.test.dto;

import lombok.Data;

@Data
public class DashboardDTO {

    private int totalOrders;

    private int totalTests;

    private double averageMarks;

    private String branch;
}
