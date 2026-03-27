package com.tech.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.tech.test.dto.*;
import com.tech.test.entity.Student;
import com.tech.test.exception.EmailAlreadyExistsException;
import com.tech.test.exception.InvalidPasswordException;
import com.tech.test.exception.StudentNotFoundException;
import com.tech.test.repository.StudentRepository;
import com.tech.test.serviceImpl.StudentServiceImpl;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository repository;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private com.tech.test.util.JwtUtil jwtUtil;

    @InjectMocks private StudentServiceImpl studentService;

    private Student sampleStudent() {
        Student student = new Student();
        student.setName("John Doe");
        student.setEmail("john.doe@example.com");
        student.setPassword("encodedPassword");
        student.setStudentId(12345L);
        student.setBranch("CSE");
        return student;
    }

    @Test
    void testRegister_Success() {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setName("John Doe");
        studentDTO.setEmail("john.doe@example.com");
        studentDTO.setPassword("password123");
        studentDTO.setStudentId(12345L);
        studentDTO.setBranch("CSE");

        Student savedStudent = sampleStudent();
        savedStudent.setId(1L);

        when(repository.findFirstByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(repository.save(any(Student.class))).thenReturn(savedStudent);

        StudentDTO result = studentService.register(studentDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals(12345L, result.getStudentId());
        assertEquals("CSE", result.getBranch());
        assertNull(result.getPassword());
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setEmail("john.doe@example.com");

        Student existingStudent = sampleStudent();
        when(repository.findFirstByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(existingStudent));

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> {
                    studentService.register(studentDTO);
                });
    }

    @Test
    void testLogin_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password123");

        Student student = sampleStudent();
        student.setId(1L);

        when(repository.findFirstByEmail("john.doe@example.com")).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("john.doe@example.com", 1L)).thenReturn("jwt-token");

        JwtResponse result = studentService.login(loginRequest);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("Bearer", result.getType());
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals(12345L, result.getStudentId());
        assertEquals("CSE", result.getBranch());
    }

    @Test
    void testLogin_StudentNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password123");

        when(repository.findFirstByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        assertThrows(
                StudentNotFoundException.class,
                () -> {
                    studentService.login(loginRequest);
                });
    }

    @Test
    void testLogin_InvalidPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("wrongpassword");

        Student student = sampleStudent();
        when(repository.findFirstByEmail("john.doe@example.com")).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(
                InvalidPasswordException.class,
                () -> {
                    studentService.login(loginRequest);
                });
    }

    @Test
    void testGetById_Success() {
        Student student = sampleStudent();
        student.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(student));

        StudentDTO result = studentService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals(12345L, result.getStudentId());
        assertEquals("CSE", result.getBranch());
    }

    @Test
    void testGetById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                StudentNotFoundException.class,
                () -> {
                    studentService.getById(1L);
                });
    }

    @Test
    void testUploadImage_Success() throws IOException {
        Student student = sampleStudent();
        student.setId(1L);

        MockMultipartFile imageFile =
                new MockMultipartFile(
                        "image", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(repository.findById(1L)).thenReturn(Optional.of(student));
        when(repository.save(any(Student.class))).thenReturn(student);

        String result = studentService.uploadImage(1L, imageFile);

        assertEquals("Image uploaded successfully for student ID: 1", result);
    }

    @Test
    void testUploadImage_StudentNotFound() {
        MockMultipartFile imageFile =
                new MockMultipartFile(
                        "image", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> {
                    studentService.uploadImage(1L, imageFile);
                });
    }

    @Test
    void testUploadImage_FileTooLarge() throws IOException {
        Student student = sampleStudent();
        student.setId(1L);

        MockMultipartFile imageFile =
                new MockMultipartFile(
                        "image", "test.jpg", "image/jpeg", new byte[3 * 1024 * 1024] // 3MB file
                        );

        when(repository.findById(1L)).thenReturn(Optional.of(student));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> {
                            studentService.uploadImage(1L, imageFile);
                        });

        assertTrue(exception.getMessage().contains("Image size must be less than or equal to 2MB"));
    }

    @Test
    void testGetImage_Success() {
        Student student = sampleStudent();
        student.setImage("test image content".getBytes());

        when(repository.findById(1L)).thenReturn(Optional.of(student));

        byte[] result = studentService.getImage(1L);

        assertNotNull(result);
        assertEquals("test image content", new String(result));
    }

    @Test
    void testGetImage_NoImageFound() {
        Student student = sampleStudent();
        student.setImage(null);

        when(repository.findById(1L)).thenReturn(Optional.of(student));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> {
                            studentService.getImage(1L);
                        });

        assertTrue(exception.getMessage().contains("No image found for student ID: 1"));
    }
}
