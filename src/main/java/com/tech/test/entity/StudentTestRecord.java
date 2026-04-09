package com.tech.test.entity;

import com.tech.test.enums.Branch;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentTestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Student ID cannot be null")
    private Long studentId;

    @NotBlank(message = "Student name cannot be blank")
    @Column(nullable = false)
    private String studentName;

    @NotBlank(message = "Test name cannot be blank")
    @Column(nullable = false)
    private String testName;

    @NotNull(message = "Score cannot be null")
    @Min(value = 0, message = "Score cannot be negative")
    @Column(nullable = false)
    private Integer score;

    @NotNull(message = "Total questions cannot be null")
    @Min(value = 0, message = "Total questions cannot be negative")
    @Column(nullable = false)
    private Integer totalQuestions;

    @NotNull(message = "Correct answers cannot be null")
    @Min(value = 0, message = "Correct answers cannot be negative")
    @Column(nullable = false)
    private Integer correctAnswers;

    @NotNull(message = "Branch cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Branch branch;

    @Column(name = "test_date")
    private java.time.LocalDateTime testDate;

    @NotBlank(message = "Roll number cannot be blank")
    @Column(nullable = false)
    private String rollNumber;

    @Min(value = 0, message = "Marks cannot be negative")
    private int marks;
}
