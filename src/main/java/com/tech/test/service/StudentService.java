package com.tech.test.service;

import com.tech.test.dto.JwtResponse;
import com.tech.test.dto.LoginRequest;
import com.tech.test.dto.StudentDTO;

public interface StudentService {

    StudentDTO register(StudentDTO dto);

    JwtResponse login(LoginRequest request);

    StudentDTO getById(Long id);
}
