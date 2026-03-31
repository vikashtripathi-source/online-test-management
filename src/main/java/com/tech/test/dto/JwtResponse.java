package com.tech.test.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private StudentDTO student;

    public JwtResponse(String token, StudentDTO studentDTO) {
        this.token = token;
        this.student = studentDTO;
    }
}
