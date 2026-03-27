package com.tech.test.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private Long studentId;
    private String branch;

    public JwtResponse(String token, StudentDTO studentDTO) {
        this.token = token;
        this.id = studentDTO.getId();
        this.name = studentDTO.getName();
        this.email = studentDTO.getEmail();
        this.studentId = studentDTO.getStudentId();
        this.branch = studentDTO.getBranch();
    }
}
