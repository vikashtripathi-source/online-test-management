package com.tech.test.dto;

import java.util.List;
import lombok.Data;

@Data
public class DashboardDTO {

    private Long studentId;
    private String studentName;
    private String branch;
    private int totalOrders;
    private int totalTests;
    private double averageMarks;
    private List<String> recentTests;
    private List<String> recentOrders;
    private String performanceStatus;
}
