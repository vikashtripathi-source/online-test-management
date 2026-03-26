package com.tech.test.controller;

import com.tech.test.dto.LoginRequest;
import com.tech.test.dto.StudentDTO;
import com.tech.test.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @PostMapping("/register")
    public ResponseEntity<StudentDTO> register(@RequestBody StudentDTO dto) {
        return ResponseEntity.ok(service.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<StudentDTO> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(service.login(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

}
