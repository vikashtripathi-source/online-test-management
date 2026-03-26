package com.tech.test.serviceImpl;

import com.tech.test.dto.JwtResponse;
import com.tech.test.dto.LoginRequest;
import com.tech.test.dto.StudentDTO;
import com.tech.test.entity.Student;
import com.tech.test.exception.EmailAlreadyExistsException;
import com.tech.test.exception.InvalidPasswordException;
import com.tech.test.exception.StudentNotFoundException;
import com.tech.test.repository.StudentRepository;
import com.tech.test.service.StudentService;
import com.tech.test.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public StudentDTO register(StudentDTO dto) {
        // Check if email already exists
        if (repository.findFirstByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email '" + dto.getEmail() + "' is already registered. Please use a different email or try logging in.");
        }

        Student student = new Student();

        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setStudentId(dto.getStudentId());
        student.setBranch(dto.getBranch());

        Student saved = repository.save(student);

        dto.setId(saved.getId());
        dto.setPassword(null);

        return dto;
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        Student student = repository.findFirstByEmail(request.getEmail())
                .orElseThrow(() -> new StudentNotFoundException("No account found with email '" + request.getEmail() + "'. Please check your email or register for a new account."));

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new InvalidPasswordException("Incorrect password. Please verify your password and try again.");
        }

        String token = jwtUtil.generateToken(student.getEmail(), student.getId());

        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setStudentId(student.getStudentId());
        dto.setBranch(student.getBranch());

        return new JwtResponse(token, dto);
    }

    @Override
    public StudentDTO getById(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));

        StudentDTO dto = new StudentDTO();

        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setStudentId(student.getStudentId());
        dto.setBranch(student.getBranch());

        return dto;
    }
}
