package com.tech.test.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class SubmitTestRequest {
    @NotNull(message = "Student ID cannot be null")
    private Long studentId;

    @NotEmpty(message = "Answers list cannot be empty")
    @Valid
    private List<AnswerRequest> answers;
}
