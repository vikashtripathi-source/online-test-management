package com.tech.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestResultResponse {

    private Long studentId;
    private int score;
    private int totalQuestions;

    private int totalTests;

    private int totalMarks;

    private double averageMarks;

    private String branch;
}
