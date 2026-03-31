package com.tech.test.dto;

import com.tech.test.enums.Branch;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentTestRecordDTO {

    private Long id;

    @NotNull(message = "Student ID cannot be null")
    private Long studentId;

    @NotBlank(message = "Student name cannot be blank")
    private String studentName;

    @NotBlank(message = "Test name cannot be blank")
    private String testName;

    @NotNull(message = "Score cannot be null")
    @Min(value = 0, message = "Score cannot be negative")
    private Integer score;

    @NotNull(message = "Total questions cannot be null")
    @Min(value = 0, message = "Total questions cannot be negative")
    private Integer totalQuestions;

    @NotNull(message = "Correct answers cannot be null")
    @Min(value = 0, message = "Correct answers cannot be negative")
    private Integer correctAnswers;

    @NotNull(message = "Branch cannot be null")
    private Branch branch;

    private java.time.LocalDateTime testDate;

    // Legacy fields for backward compatibility
    @NotBlank(message = "Roll number cannot be blank")
    private String rollNumber;

    @Min(value = 0, message = "Marks cannot be negative")
    private int marks;
}
