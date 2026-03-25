package com.tech.test.controller;

import com.tech.test.dto.QuestionDTO;
import com.tech.test.dto.StudentTestRecordDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.TestResultResponse;
import com.tech.test.enums.Branch;
import com.tech.test.service.ExamService;
import com.tech.test.service.KafkaProducerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@Tag(name = "Exam Management API", description = "Operations related to Questions, Test Submission and Student Records")
@AllArgsConstructor
public class ExamController {

    private final ExamService service;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/questions")
    public ResponseEntity<QuestionDTO> addQuestion(@Valid @RequestBody QuestionDTO questionDTO) {
        try {
            QuestionDTO saved = service.addQuestion(questionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/questions/bulk")
    public ResponseEntity<List<QuestionDTO>> addAllQuestions(@Valid @RequestBody List<QuestionDTO> questionDTOs) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.addAllQuestions(questionDTOs));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestions() {
        try {
            return ResponseEntity.ok(service.getAllQuestions());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        try {
            service.deleteQuestion(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/tests/submit")
    public ResponseEntity<TestResultResponse> submit(@Valid @RequestBody SubmitTestRequest request) {
        return ResponseEntity.ok(service.submitTest(request));
    }

    @PostMapping("/tests/async-submit")
    public ResponseEntity<String> submitAsync(@Valid @RequestBody SubmitTestRequest request) {

        kafkaProducerService.sendTestSubmission(request);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Test submission accepted and queued for evaluation");
    }

    @PostMapping("/student-records")
    public ResponseEntity<StudentTestRecordDTO> saveStudentTestRecord(
            @Valid @RequestBody StudentTestRecordDTO recordDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.saveStudentTestRecord(recordDTO));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/student-records/{id}")
    public ResponseEntity<StudentTestRecordDTO> updateStudentTestRecord(
            @PathVariable Long id,
            @Valid @RequestBody StudentTestRecordDTO recordDTO) {
        try {
            return ResponseEntity.ok(service.updateStudentTestRecord(id, recordDTO));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/student-records/{id}")
    public ResponseEntity<Void> deleteStudentTestRecord(@PathVariable Long id) {
        try {
            service.deleteStudentTestRecord(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/student-records/branch/{branch}")
    public ResponseEntity<List<StudentTestRecordDTO>> getRecordsByBranch(
            @PathVariable Branch branch) {
        try {
            return ResponseEntity.ok(service.getRecordsByBranch(branch));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}