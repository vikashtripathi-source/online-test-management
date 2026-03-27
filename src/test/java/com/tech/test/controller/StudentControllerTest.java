package com.tech.test.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.dto.*;
import com.tech.test.service.StudentService;
import com.tech.test.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(StudentController.class)
@Import(com.tech.test.config.SecurityConfig.class)
class StudentControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private StudentService service;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private com.tech.test.security.CustomUserDetailsService customUserDetailsService;

    @Autowired private ObjectMapper objectMapper;

    private StudentDTO sampleStudentDTO() {
        StudentDTO dto = new StudentDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("password123");
        dto.setStudentId(12345L);
        dto.setBranch("CSE");
        return dto;
    }

    private LoginRequest sampleLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        return request;
    }

    private JwtResponse sampleJwtResponse() {
        return new JwtResponse("sample-jwt-token", sampleStudentDTO());
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/students/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("Online Test Management API"));
    }

    @Test
    void testPublicTestEndpoint() throws Exception {
        mockMvc.perform(get("/api/students/public-test"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value("This is a public endpoint - no JWT required!"));
    }

    @Test
    void testRegisterStudent_Success() throws Exception {
        StudentDTO studentDTO = sampleStudentDTO();
        StudentDTO savedStudent = new StudentDTO();
        savedStudent.setId(1L);
        savedStudent.setName(studentDTO.getName());
        savedStudent.setEmail(studentDTO.getEmail());
        savedStudent.setStudentId(studentDTO.getStudentId());
        savedStudent.setBranch(studentDTO.getBranch());

        when(service.register(any(StudentDTO.class))).thenReturn(savedStudent);

        mockMvc.perform(
                        post("/api/students/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testRegisterStudent_EmailAlreadyExists() throws Exception {
        StudentDTO studentDTO = sampleStudentDTO();

        when(service.register(any(StudentDTO.class)))
                .thenThrow(
                        new RuntimeException("Email 'john.doe@example.com' is already registered"));

        mockMvc.perform(
                        post("/api/students/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void testLoginStudent_Success() throws Exception {
        LoginRequest loginRequest = sampleLoginRequest();
        JwtResponse jwtResponse = sampleJwtResponse();

        when(service.login(any(LoginRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(
                        post("/api/students/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("sample-jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void testLoginStudent_InvalidCredentials() throws Exception {
        LoginRequest loginRequest = sampleLoginRequest();

        when(service.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(
                        post("/api/students/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testGetStudentById_Success() throws Exception {
        StudentDTO studentDTO = sampleStudentDTO();
        studentDTO.setId(1L);

        when(service.getById(1L)).thenReturn(studentDTO);

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    @WithMockUser
    void testGetStudentById_NotFound() throws Exception {
        when(service.getById(1L)).thenThrow(new RuntimeException("Student with ID 1 not found"));

        mockMvc.perform(get("/api/students/1")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testUploadImage_Success() throws Exception {
        MockMultipartFile imageFile =
                new MockMultipartFile(
                        "image", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(service.uploadImage(eq(1L), any(MultipartFile.class)))
                .thenReturn("Image uploaded successfully for student ID: 1");

        mockMvc.perform(multipart("/api/students/1/image").file(imageFile))
                .andExpect(status().isOk())
                .andExpect(content().string("Image uploaded successfully for student ID: 1"));
    }

    @Test
    @WithMockUser
    void testUploadImage_StudentNotFound() throws Exception {
        MockMultipartFile imageFile =
                new MockMultipartFile(
                        "image", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(service.uploadImage(eq(1L), any(MultipartFile.class)))
                .thenThrow(new RuntimeException("Student with ID 1 not found"));

        mockMvc.perform(multipart("/api/students/1/image").file(imageFile))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testUploadImage_FileTooLarge() throws Exception {
        MockMultipartFile imageFile =
                new MockMultipartFile(
                        "image", "test.jpg", "image/jpeg", new byte[3 * 1024 * 1024] // 3MB file
                        );

        when(service.uploadImage(eq(1L), any(MultipartFile.class)))
                .thenThrow(
                        new RuntimeException(
                                "Image size must be less than or equal to 2MB. Current size: 3MB"));

        mockMvc.perform(multipart("/api/students/1/image").file(imageFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetImage_Success() throws Exception {
        byte[] imageData = "test image content".getBytes();

        when(service.getImage(1L)).thenReturn(imageData);

        mockMvc.perform(get("/api/students/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(imageData))
                .andExpect(header().string("Content-Type", "image/jpeg"));
    }

    @Test
    @WithMockUser
    void testGetImage_NotFound() throws Exception {
        when(service.getImage(1L))
                .thenThrow(new RuntimeException("No image found for student ID: 1"));

        mockMvc.perform(get("/api/students/1/image")).andExpect(status().isNotFound());
    }
}
