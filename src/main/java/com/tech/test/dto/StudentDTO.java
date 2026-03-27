package com.tech.test.dto;

import lombok.Data;

@Data
public class StudentDTO {

    private Long id;
    private String name;
    private String email;
    private String password;
    private Long studentId;
    private String branch;
}
