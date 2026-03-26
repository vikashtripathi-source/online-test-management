package com.tech.test.serviceImpl;

import com.tech.test.dto.LoginRequest;
import com.tech.test.dto.StudentDTO;
import com.tech.test.entity.Student;
import com.tech.test.repository.StudentRepository;
import com.tech.test.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    @Override
    public StudentDTO register(StudentDTO dto) {

        Student student = new Student();

        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setPassword(dto.getPassword());
        student.setStudentId(dto.getStudentId());
        student.setBranch(dto.getBranch());

        Student saved = repository.save(student);

        dto.setId(saved.getId());

        return dto;
    }

    @Override
    public StudentDTO login(LoginRequest request) {

        Student student = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!student.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        StudentDTO dto = new StudentDTO();

        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setStudentId(student.getStudentId());
        dto.setBranch(student.getBranch());

        return dto;
    }

    @Override
    public StudentDTO getById(Long id) {

        Student student = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentDTO dto = new StudentDTO();

        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setStudentId(student.getStudentId());
        dto.setBranch(student.getBranch());

        return dto;
    }
}