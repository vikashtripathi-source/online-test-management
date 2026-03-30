package com.tech.test.controller;

import com.tech.test.dto.DashboardDTO;
import com.tech.test.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(
        name = "Dashboard Controller",
        description = "Dashboard endpoints for retrieving student dashboard information")
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/{studentId}")
    @Operation(
            summary = "Get student dashboard",
            description =
                    "Retrieve dashboard information for a specific student including tests, scores, and progress")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved dashboard data"),
                @ApiResponse(responseCode = "404", description = "Student not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<DashboardDTO> getDashboard(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {

        return ResponseEntity.ok(service.getDashboard(studentId));
    }
}
