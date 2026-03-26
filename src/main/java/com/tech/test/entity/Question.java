package com.tech.test.entity;

import com.tech.test.enums.Branch;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Question text cannot be blank")
    @Size(min = 10, max = 500, message = "Question must be between 10 and 500 characters")
    private String question;

    @NotBlank(message = "Option A cannot be blank")
    private String optionA;

    @NotBlank(message = "Option B cannot be blank")
    private String optionB;

    @NotBlank(message = "Option C cannot be blank")
    private String optionC;

    @NotBlank(message = "Option D cannot be blank")
    private String optionD;

    @NotBlank(message = "Correct answer cannot be blank")
    private String correctAnswer;

    @Enumerated(EnumType.STRING)
    private Branch branch;
}
