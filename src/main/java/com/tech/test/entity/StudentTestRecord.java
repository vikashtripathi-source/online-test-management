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

    @NotBlank(message = "Roll number cannot be blank")
    @Column(nullable = false)
    private String rollNumber;

    @NotNull(message = "Branch cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Branch branch;

    @Min(value = 0, message = "Marks cannot be negative")
    private int marks;

    @NotNull(message = "Student ID cannot be null")
    private Long studentId;
}
