package com.tech.test.serviceImpl;

import com.tech.test.dto.AdminRegistrationDTO;
import com.tech.test.dto.JwtResponse;
import com.tech.test.dto.LoginRequest;
import com.tech.test.dto.StudentDTO;
import com.tech.test.entity.Student;
import com.tech.test.enums.StudentRole;
import com.tech.test.exception.EmailAlreadyExistsException;
import com.tech.test.exception.InvalidPasswordException;
import com.tech.test.exception.StudentNotFoundException;
import com.tech.test.repository.StudentRepository;
import com.tech.test.service.StudentService;
import com.tech.test.util.JwtUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.upload.student.dir:./uploads/student-images}")
    private String uploadDir;

    @Value("${admin.registration.code}")
    private String adminSecretCode;

    @Override
    public StudentDTO register(StudentDTO dto) {
        if (repository.findFirstByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(
                    "Email '"
                            + dto.getEmail()
                            + "' is already registered. Please use a different email or try logging in.");
        }

        Student student = new Student();

        student.setEmail(dto.getEmail());
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setBranch(dto.getBranch());
        student.setMobileNumber(dto.getMobileNumber());
        student.setImageUrl(dto.getImageUrl());
        student.setRole(StudentRole.STUDENT);

        student.setName(dto.getFirstName() + " " + dto.getLastName());

        Student saved = repository.save(student);

        dto.setId(saved.getId());
        dto.setPassword(null);

        return dto;
    }

    @Override
    public StudentDTO registerAdmin(AdminRegistrationDTO adminDTO) {
        if (adminSecretCode == null) {
            adminSecretCode = "admin123";
        }

        if (repository.findFirstByEmail(adminDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(
                    "Email '"
                            + adminDTO.getEmail()
                            + "' is already registered. Please use a different email or try logging in.");
        }

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

        student.setEmail(adminDTO.getEmail());
        student.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        student.setFirstName(adminDTO.getFirstName());
        student.setLastName(adminDTO.getLastName());
        student.setBranch(adminDTO.getBranch());
        student.setMobileNumber(adminDTO.getMobileNumber());
        student.setImageUrl(adminDTO.getImageUrl());
        student.setRole(role);

        student.setName(adminDTO.getFirstName() + " " + adminDTO.getLastName());

        Student saved = repository.save(student);

        StudentDTO dto = new StudentDTO();
        dto.setId(saved.getId());
        dto.setEmail(saved.getEmail());
        dto.setFirstName(saved.getFirstName());
        dto.setLastName(saved.getLastName());
        dto.setBranch(saved.getBranch());
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
        dto.setBranch(student.getBranch());
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
        dto.setBranch(student.getBranch());
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

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = image.getOriginalFilename();
            String fileExtension =
                    originalFilename != null && originalFilename.contains(".")
                            ? originalFilename.substring(originalFilename.lastIndexOf("."))
                            : "";
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/api/students/" + studentId + "/image";
            student.setImageUrl(imageUrl);
            student.setImageFilename(uniqueFilename);
            repository.save(student);

            return "Image uploaded successfully for student ID: " + studentId;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public byte[] getImage(Long studentId) {
        log.debug("Looking for student with ID: {}", studentId);

        Student student =
                repository
                        .findById(studentId)
                        .orElseThrow(
                                () ->
                                        new StudentNotFoundException(
                                                "Student with ID " + studentId + " not found."));

        log.debug("Found student: {}", student.getEmail());

        if (student.getImageFilename() == null) {
            log.debug("No image filename found for student ID: {}", studentId);
            throw new RuntimeException("No image found for student ID: " + studentId);
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            Path filePath = uploadPath.resolve(student.getImageFilename());

            log.debug("Looking for image file: {}", filePath);

            if (!Files.exists(filePath)) {
                log.error("Image file not found: {}", filePath);
                throw new RuntimeException("Image file not found: " + student.getImageFilename());
            }

            byte[] imageBytes = Files.readAllBytes(filePath);
            log.debug("Successfully read image file, size: {} bytes", imageBytes.length);

            return imageBytes;

        } catch (IOException e) {
            log.error("Failed to retrieve image for student ID: {}", studentId, e);
            throw new RuntimeException("Failed to retrieve image: " + e.getMessage());
        }
    }
}
