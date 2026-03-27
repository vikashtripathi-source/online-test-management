package com.tech.test.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerDTO {

    private Long id;

    @NotNull(message = "Student ID cannot be null")
    private Long studentId;

    @NotNull(message = "Question ID cannot be null")
    private Long questionId;

    @NotBlank(message = "Selected answer cannot be blank")
    private String selectedAnswer;
}
