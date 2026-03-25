package com.tech.test.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentAnswerDTO {

    private Long id;
    
    @NotNull(message = "Student ID cannot be null")
    private Long studentId;
    
    @NotNull(message = "Question ID cannot be null")
    private Long questionId;
    
    @NotBlank(message = "Selected answer cannot be blank")
    private String selectedAnswer;
}
