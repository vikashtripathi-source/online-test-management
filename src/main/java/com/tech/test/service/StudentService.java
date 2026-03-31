package com.tech.test.service;

import com.tech.test.dto.AdminRegistrationDTO;
import com.tech.test.dto.JwtResponse;
import com.tech.test.dto.LoginRequest;
import com.tech.test.dto.StudentDTO;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {

    StudentDTO register(StudentDTO dto);

    StudentDTO registerAdmin(AdminRegistrationDTO adminDTO);

    JwtResponse login(LoginRequest request);

    StudentDTO getById(Long id);

    String uploadImage(Long studentId, MultipartFile image);

    byte[] getImage(Long studentId);
}
