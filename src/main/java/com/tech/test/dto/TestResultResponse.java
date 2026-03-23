package com.tech.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestResultResponse {

    private Long studentId;
    private int score;
    private int totalQuestions;
}
