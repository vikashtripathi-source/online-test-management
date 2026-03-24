package com.tech.test.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerRequest {

    @NotNull(message = "Question ID cannot be null")
    private Long questionId;

    @NotBlank(message = "Selected answer cannot be blank")
    private String selectedAnswer;
}
