package com.tech.test.controller;

import com.tech.test.dto.ImageUploadDTO;
import com.tech.test.dto.JwtResponse;
import com.tech.test.dto.LoginRequest;
import com.tech.test.dto.StudentDTO;
import com.tech.test.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(
        name = "Student Management",
        description = "APIs for managing student operations including image upload")
public class StudentController {

    private final StudentService service;

    @Operation(
            summary = "Health Check",
            description = "Check if the student management API is running and healthy")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "API is healthy and running"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "Online Test Management API");
        response.put("time", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Public Test Endpoint",
            description = "Test endpoint that doesn't require authentication for API testing")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Test successful - public endpoint accessible"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("/public-test")
    public ResponseEntity<Map<String, String>> publicTest() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a public endpoint - no JWT required!");
        response.put("time", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Register Student",
            description = "Create a new student account with registration details")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Student registered successfully",
                        content = @Content(schema = @Schema(implementation = StudentDTO.class))),
                @ApiResponse(responseCode = "409", description = "Email already exists"),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PostMapping("/register")
    public ResponseEntity<StudentDTO> register(@RequestBody StudentDTO dto) {
        return ResponseEntity.ok(service.register(dto));
    }

    @Operation(
            summary = "Student Login",
            description =
                    "Authenticate student credentials and return JWT token for session management")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Login successful - JWT token returned",
                        content = @Content(schema = @Schema(implementation = JwtResponse.class))),
                @ApiResponse(responseCode = "401", description = "Invalid credentials"),
                @ApiResponse(responseCode = "404", description = "Student not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(service.login(req));
    }

    @Operation(
            summary = "Get Student By ID",
            description = "Retrieve complete student details by their unique identifier")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Student details retrieved successfully",
                        content = @Content(schema = @Schema(implementation = StudentDTO.class))),
                @ApiResponse(responseCode = "404", description = "Student not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(
            @Parameter(description = "Unique identifier of the student", required = true)
                    @PathVariable
                    Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(
            summary = "Upload Student Image",
            description = "Upload a profile image for a student (JPEG format, max 2MB)")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid file format or file too large"),
                @ApiResponse(responseCode = "404", description = "Student not found"),
                @ApiResponse(
                        responseCode = "500",
                        description = "Upload failed due to server error")
            })
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @Parameter(description = "Unique identifier of the student", required = true)
                    @PathVariable
                    Long id,
            @Valid @ModelAttribute ImageUploadDTO uploadDTO) {

        MultipartFile image = uploadDTO.getImage();
        String result = service.uploadImage(id, image);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Get Student Image",
            description = "Retrieve the profile image of a specific student")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Image retrieved successfully",
                        content = @Content(mediaType = "image/jpeg")),
                @ApiResponse(responseCode = "404", description = "Student or image not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(
            @Parameter(description = "Unique identifier of the student", required = true)
                    @PathVariable
                    Long id) {
        byte[] imageData = service.getImage(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageData.length);

        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }
}
