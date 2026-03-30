package com.tech.test.controller;

import com.tech.test.entity.Student;
import com.tech.test.repository.StudentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@Tag(
        name = "Debug Controller",
        description = "Debug endpoints for managing student data and database operations")
public class DebugController {

    private final StudentRepository studentRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/duplicate-emails")
    @Operation(
            summary = "Find duplicate emails",
            description = "Find all students with duplicate email addresses")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully found duplicate emails"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<Map<String, Object>> findDuplicateEmails() {
        Map<String, Object> response = new HashMap<>();

        var allStudents = studentRepository.findAll();
        Map<String, Integer> emailCounts = new HashMap<>();

        for (var student : allStudents) {
            emailCounts.put(
                    student.getEmail(), emailCounts.getOrDefault(student.getEmail(), 0) + 1);
        }

        Map<String, Object> duplicates = new HashMap<>();
        for (Map.Entry<String, Integer> entry : emailCounts.entrySet()) {
            if (entry.getValue() > 1) {
                var duplicateStudents = studentRepository.findAllByEmail(entry.getKey());
                duplicates.put(entry.getKey(), duplicateStudents);
            }
        }

        response.put("duplicateCount", duplicates.size());
        response.put("duplicates", duplicates);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-duplicate/{email}")
    @Operation(
            summary = "Remove duplicate students",
            description =
                    "Remove duplicate student records for a given email, keeping only the first one")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully removed duplicates or no duplicates found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<Map<String, Object>> removeDuplicates(
            @Parameter(description = "Email address for which to remove duplicates") @PathVariable
                    String email) {
        Map<String, Object> response = new HashMap<>();

        var duplicates = studentRepository.findAllByEmail(email);
        if (duplicates.size() <= 1) {
            response.put("message", "No duplicates found for email: " + email);
            return ResponseEntity.ok(response);
        }

        var toKeep = duplicates.get(0);
        for (int i = 1; i < duplicates.size(); i++) {
            studentRepository.delete(duplicates.get(i));
        }

        response.put(
                "message",
                "Removed " + (duplicates.size() - 1) + " duplicate records for email: " + email);
        response.put("keptRecord", toKeep.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-auto-increment")
    @Operation(
            summary = "Reset auto increment",
            description = "Reset the auto increment value for students table to max(id) + 1")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully reset auto increment"),
                @ApiResponse(responseCode = "500", description = "Failed to reset auto increment")
            })
    public ResponseEntity<Map<String, Object>> resetAutoIncrement() {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer maxId =
                    jdbcTemplate.queryForObject("SELECT MAX(id) FROM students", Integer.class);
            if (maxId == null) maxId = 0;

            jdbcTemplate.execute("ALTER TABLE students AUTO_INCREMENT = " + (maxId + 1));

            response.put("message", "Auto-increment reset successfully");
            response.put("nextId", maxId + 1);

        } catch (Exception e) {
            response.put("error", "Failed to reset auto-increment: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/table-info")
    @Operation(
            summary = "Get table information",
            description =
                    "Get information about the students table including auto increment value, record count, and max ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved table information"),
                @ApiResponse(responseCode = "500", description = "Failed to get table info")
            })
    public ResponseEntity<Map<String, Object>> getTableInfo() {
        Map<String, Object> response = new HashMap<>();

        try {
            Long autoIncrement =
                    jdbcTemplate.queryForObject(
                            "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'students'",
                            Long.class);

            Integer count =
                    jdbcTemplate.queryForObject("SELECT COUNT(*) FROM students", Integer.class);
            Integer maxId =
                    jdbcTemplate.queryForObject("SELECT MAX(id) FROM students", Integer.class);

            response.put("autoIncrement", autoIncrement);
            response.put("recordCount", count);
            response.put("maxId", maxId);

        } catch (Exception e) {
            response.put("error", "Failed to get table info: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/encode-passwords")
    @Operation(
            summary = "Encode existing passwords",
            description = "Encode all plain text passwords in the database using BCrypt")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Successfully encoded passwords"),
                @ApiResponse(responseCode = "500", description = "Failed to encode passwords")
            })
    public ResponseEntity<Map<String, Object>> encodeExistingPasswords() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Student> allStudents = studentRepository.findAll();
            int updatedCount = 0;

            for (Student student : allStudents) {
                if (!student.getPassword().startsWith("$2")) {
                    String encodedPassword = passwordEncoder.encode(student.getPassword());
                    student.setPassword(encodedPassword);
                    studentRepository.save(student);
                    updatedCount++;
                }
            }

            response.put("message", "Successfully encoded " + updatedCount + " passwords");
            response.put("totalStudents", allStudents.size());

        } catch (Exception e) {
            response.put("error", "Failed to encode passwords: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/encode-password/{email}/{newPassword}")
    @Operation(
            summary = "Encode specific password",
            description = "Encode and update password for a specific student by email")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Successfully updated password"),
                @ApiResponse(responseCode = "200", description = "Student not found"),
                @ApiResponse(responseCode = "500", description = "Failed to update password")
            })
    public ResponseEntity<Map<String, Object>> encodeSpecificPassword(
            @Parameter(description = "Email address of the student") @PathVariable String email,
            @Parameter(description = "New password to encode and set") @PathVariable
                    String newPassword) {
        Map<String, Object> response = new HashMap<>();

        try {
            var students = studentRepository.findAllByEmail(email);
            if (students.isEmpty()) {
                response.put("error", "Student not found with email: " + email);
                return ResponseEntity.ok(response);
            }

            Student student = students.get(0);
            String encodedPassword = passwordEncoder.encode(newPassword);
            student.setPassword(encodedPassword);
            studentRepository.save(student);

            response.put("message", "Password updated successfully for: " + email);

        } catch (Exception e) {
            response.put("error", "Failed to update password: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
