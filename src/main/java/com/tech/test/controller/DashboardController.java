package com.tech.test.controller;

import com.tech.test.dto.DashboardDTO;
import com.tech.test.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/{studentId}")
    public ResponseEntity<DashboardDTO> getDashboard(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(service.getDashboard(studentId));
    }
}