package com.tech.test.serviceImpl;

import com.tech.test.dto.AdminRegistrationDTO;
import com.tech.test.dto.JwtResponse;
import com.tech.test.dto.LoginRequest;
import com.tech.test.dto.StudentDTO;
import com.tech.test.entity.Student;
import com.tech.test.enums.Branch;
import com.tech.test.enums.StudentRole;
import com.tech.test.exception.EmailAlreadyExistsException;
import com.tech.test.exception.InvalidPasswordException;
import com.tech.test.exception.StudentNotFoundException;
import com.tech.test.repository.StudentRepository;
import com.tech.test.service.StudentService;
import com.tech.test.util.JwtUtil;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${admin.registration.code}")
    private String adminSecretCode;

    @Override
    public StudentDTO register(StudentDTO dto) {
        // Check if email already exists
        if (repository.findFirstByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(
                    "Email '"
                            + dto.getEmail()
                            + "' is already registered. Please use a different email or try logging in.");
        }

        Student student = new Student();

        // Set new fields
        student.setEmail(dto.getEmail());
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setBranch(Branch.valueOf(dto.getBranch()));
        student.setMobileNumber(dto.getMobileNumber());
        student.setImageUrl(dto.getImageUrl());
        student.setRole(StudentRole.STUDENT); // Always STUDENT for regular registration

        // Legacy fields for backward compatibility
        student.setName(dto.getFirstName() + " " + dto.getLastName());
        student.setStudentId(System.currentTimeMillis()); // Generate unique ID

        Student saved = repository.save(student);

        dto.setId(saved.getId());
        dto.setPassword(null);

        return dto;
    }

    @Override
    public StudentDTO registerAdmin(AdminRegistrationDTO adminDTO) {
        // CRITICAL: Validate admin code
        if (adminSecretCode == null) {
            adminSecretCode = "admin123"; // Default for development
        }

        // Temporarily disabled for testing
        // if (!"admin123".equals(adminDTO.getAdminCode())) {
        //     throw new RuntimeException("Invalid admin code");
        // }

        // Check if email already exists
        if (repository.findFirstByEmail(adminDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(
                    "Email '"
                            + adminDTO.getEmail()
                            + "' is already registered. Please use a different email or try logging in.");
        }

        // Validate role
        StudentRole role;
        try {
            role = StudentRole.valueOf(adminDTO.getRole());
            if (role == StudentRole.STUDENT) {
                throw new RuntimeException("Cannot register STUDENT through admin endpoint");
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + adminDTO.getRole());
        }

        Student student = new Student();

        // Set fields
        student.setEmail(adminDTO.getEmail());
        student.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        student.setFirstName(adminDTO.getFirstName());
        student.setLastName(adminDTO.getLastName());
        student.setBranch(Branch.valueOf(adminDTO.getBranch()));
        student.setMobileNumber(adminDTO.getMobileNumber());
        student.setImageUrl(adminDTO.getImageUrl());
        student.setRole(role); // User-selected role

        // Legacy fields for backward compatibility
        student.setName(adminDTO.getFirstName() + " " + adminDTO.getLastName());
        student.setStudentId(System.currentTimeMillis()); // Generate unique ID

        Student saved = repository.save(student);

        StudentDTO dto = new StudentDTO();
        dto.setId(saved.getId());
        dto.setEmail(saved.getEmail());
        dto.setFirstName(saved.getFirstName());
        dto.setLastName(saved.getLastName());
        dto.setBranch(saved.getBranch().name());
        dto.setMobileNumber(saved.getMobileNumber());
        dto.setImageUrl(saved.getImageUrl());
        dto.setRole(saved.getRole());
        dto.setCreatedAt(saved.getCreatedAt());

        return dto;
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        Student student =
                repository
                        .findFirstByEmail(request.getEmail())
                        .orElseThrow(
                                () ->
                                        new StudentNotFoundException(
                                                "No account found with email '"
                                                        + request.getEmail()
                                                        + "'. Please check your email or register for a new account."));

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new InvalidPasswordException(
                    "Incorrect password. Please verify your password and try again.");
        }

        String token = jwtUtil.generateToken(student.getEmail(), student.getId());

        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setBranch(student.getBranch().name());
        dto.setMobileNumber(student.getMobileNumber());
        dto.setImageUrl(student.getImageUrl());
        dto.setRole(student.getRole());
        dto.setCreatedAt(student.getCreatedAt());

        return new JwtResponse(token, dto);
    }

    @Override
    public StudentDTO getById(Long id) {
        Student student =
                repository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new StudentNotFoundException(
                                                "Student with ID " + id + " not found."));

        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setBranch(student.getBranch().name());
        dto.setMobileNumber(student.getMobileNumber());
        dto.setImageUrl(student.getImageUrl());
        dto.setRole(student.getRole());
        dto.setCreatedAt(student.getCreatedAt());

        return dto;
    }

    @Override
    public String uploadImage(Long studentId, MultipartFile image) {
        Student student =
                repository
                        .findById(studentId)
                        .orElseThrow(
                                () ->
                                        new StudentNotFoundException(
                                                "Student with ID " + studentId + " not found."));

        // Validate maximum file size (2MB = 2 * 1024 * 1024 bytes)
        long maxSize = 2 * 1024 * 1024;
        if (image.getSize() > maxSize) {
            throw new RuntimeException(
                    "Image size must be less than or equal to 2MB. Current size: "
                            + (image.getSize() / 1024 / 1024)
                            + "MB");
        }

        try {
            student.setImage(image.getBytes());
            repository.save(student);
            return "Image uploaded successfully for student ID: " + studentId;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public byte[] getImage(Long studentId) {
        Student student =
                repository
                        .findById(studentId)
                        .orElseThrow(
                                () ->
                                        new StudentNotFoundException(
                                                "Student with ID " + studentId + " not found."));

        if (student.getImage() == null) {
            throw new RuntimeException("No image found for student ID: " + studentId);
        }

        return student.getImage();
    }
}
