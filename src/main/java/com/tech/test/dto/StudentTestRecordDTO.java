package com.tech.test.dto;

import com.tech.test.enums.Branch;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentTestRecordDTO {

    private Long id;
    
    @NotBlank(message = "Roll number cannot be blank")
    private String rollNumber;
    
    @NotNull(message = "Branch cannot be null")
    private Branch branch;
    
    @Min(value = 0, message = "Marks cannot be negative")
    private int marks;
    
    @NotNull(message = "Student ID cannot be null")
    private Long studentId;
}
