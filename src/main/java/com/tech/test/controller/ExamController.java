package com.tech.test.controller;

import com.tech.test.dto.QuestionDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.TestResultResponse;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentTestRecord;
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
    public ResponseEntity<Question> addQuestion(@Valid @RequestBody Question q) {
        Question saved = service.addQuestion(q);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/questions/bulk")
    public ResponseEntity<List<Question>> addAllQuestions(@Valid @RequestBody List<Question> questions) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addAllQuestions(questions));
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestions() {
        return ResponseEntity.ok(service.getAllQuestions());
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        service.deleteQuestion(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<StudentTestRecord> saveStudentTestRecord(
            @Valid @RequestBody StudentTestRecord record) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.saveStudentTestRecord(record));
    }

    @PutMapping("/student-records/{id}")
    public ResponseEntity<StudentTestRecord> updateStudentTestRecord(
            @PathVariable Long id,
            @Valid @RequestBody StudentTestRecord record) {

        return ResponseEntity.ok(service.updateStudentTestRecord(id, record));
    }

    @DeleteMapping("/student-records/{id}")
    public ResponseEntity<Void> deleteStudentTestRecord(@PathVariable Long id) {

        service.deleteStudentTestRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student-records/branch/{branch}")
    public ResponseEntity<List<StudentTestRecord>> getRecordsByBranch(
            @PathVariable Branch branch) {

        return ResponseEntity.ok(service.getRecordsByBranch(branch));
    }
}