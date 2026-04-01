package com.tech.test.controller;

import com.tech.test.dto.QuestionDTO;
import com.tech.test.dto.StudentTestRecordDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.TestResultResponse;
import com.tech.test.enums.Branch;
import com.tech.test.mapper.QuestionMapper;
import com.tech.test.service.ExamService;
import com.tech.test.service.KafkaProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams")
@Tag(
        name = "Exam Management API",
        description = "Operations related to Questions, Test Submission and Student Records")
@AllArgsConstructor
public class ExamController {

    private final ExamService service;
    private final KafkaProducerService kafkaProducerService;
    private final QuestionMapper questionMapper;

    @PostMapping("/questions")
    @Operation(summary = "Add a question", description = "Add a new question to the question bank")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Question successfully created"),
                @ApiResponse(responseCode = "400", description = "Invalid question data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<QuestionDTO> addQuestion(@Valid @RequestBody QuestionDTO questionDTO) {
        QuestionDTO saved = service.addQuestion(questionDTO);
        // Send question added event to Kafka
        kafkaProducerService.sendQuestionAdded(questionMapper.toEntity(saved));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/questions/bulk")
    @Operation(
            summary = "Add multiple questions",
            description = "Add multiple questions to the question bank in a single request")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Questions successfully created"),
                @ApiResponse(responseCode = "400", description = "Invalid question data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<QuestionDTO>> addAllQuestions(
            @Valid @RequestBody List<QuestionDTO> questionDTOs) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addAllQuestions(questionDTOs));
    }

    @GetMapping("/questions")
    @Operation(
            summary = "Get all questions",
            description = "Retrieve all questions from the question bank")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved questions"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<QuestionDTO>> getQuestions() {
        return ResponseEntity.ok(service.getAllQuestions());
    }

    @DeleteMapping("/questions/{id}")
    @Operation(
            summary = "Delete a question",
            description = "Delete a question from the question bank by ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Question successfully deleted"),
                @ApiResponse(responseCode = "404", description = "Question not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<Void> deleteQuestion(
            @Parameter(description = "ID of the question to delete") @PathVariable Long id) {
        service.deleteQuestion(id);
        // Send question deleted event to Kafka
        kafkaProducerService.sendQuestionDeleted(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tests/submit")
    @Operation(
            summary = "Submit test",
            description = "Submit a test for immediate evaluation and get results")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Test submitted and evaluated successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid test submission data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<TestResultResponse> submit(
            @Valid @RequestBody SubmitTestRequest request) {
        return ResponseEntity.ok(service.submitTest(request));
    }

    @PostMapping("/tests/async-submit")
    @Operation(
            summary = "Submit test asynchronously",
            description = "Submit a test for asynchronous evaluation via Kafka messaging")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "202",
                        description = "Test submission accepted and queued for evaluation"),
                @ApiResponse(responseCode = "400", description = "Invalid test submission data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<String> submitAsync(@Valid @RequestBody SubmitTestRequest request) {
        kafkaProducerService.sendTestSubmission(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Test submission accepted and queued for evaluation");
    }

    @GetMapping("/student-records")
    @Operation(
            summary = "Get all student test records",
            description = "Retrieve all student test records from the database")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved student test records"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<StudentTestRecordDTO>> getAllStudentTestRecords() {
        return ResponseEntity.ok(service.getAllStudentTestRecords());
    }

    @PostMapping("/student-records")
    @Operation(
            summary = "Save student test record",
            description = "Save a new student test record to the database")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Student test record successfully created"),
                @ApiResponse(responseCode = "400", description = "Invalid student record data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<StudentTestRecordDTO> saveStudentTestRecord(
            @Valid @RequestBody StudentTestRecordDTO recordDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.saveStudentTestRecord(recordDTO));
    }

    @PutMapping("/student-records/{id}")
    @Operation(
            summary = "Update student test record",
            description = "Update an existing student test record by ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Student test record successfully updated"),
                @ApiResponse(responseCode = "400", description = "Invalid student record data"),
                @ApiResponse(responseCode = "404", description = "Student record not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<StudentTestRecordDTO> updateStudentTestRecord(
            @Parameter(description = "ID of the student record to update") @PathVariable Long id,
            @Valid @RequestBody StudentTestRecordDTO recordDTO) {
        return ResponseEntity.ok(service.updateStudentTestRecord(id, recordDTO));
    }

    @DeleteMapping("/student-records/{id}")
    @Operation(
            summary = "Delete student test record",
            description = "Delete a student test record by ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "204",
                        description = "Student test record successfully deleted"),
                @ApiResponse(responseCode = "404", description = "Student record not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<Void> deleteStudentTestRecord(
            @Parameter(description = "ID of the student record to delete") @PathVariable Long id) {
        service.deleteStudentTestRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student-records/branch/{branch}")
    @Operation(
            summary = "Get student records by branch",
            description = "Retrieve all student test records for a specific branch")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved student records"),
                @ApiResponse(responseCode = "400", description = "Invalid branch parameter"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<StudentTestRecordDTO>> getRecordsByBranch(
            @Parameter(description = "Branch to filter student records") @PathVariable
                    Branch branch) {
        return ResponseEntity.ok(service.getRecordsByBranch(branch));
    }

    @GetMapping("/questions/branch/{branch}")
    @Operation(
            summary = "Get questions by branch",
            description = "Retrieve all questions for a specific branch")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved questions"),
                @ApiResponse(responseCode = "400", description = "Invalid branch parameter"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<QuestionDTO>> getByBranch(
            @Parameter(description = "Branch to filter questions") @PathVariable String branch) {

        return ResponseEntity.ok(service.getQuestionsByBranch(branch));
    }

    @GetMapping("/results/{studentId}")
    @Operation(
            summary = "Get test results",
            description = "Retrieve test results for a specific student")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved test results"),
                @ApiResponse(responseCode = "404", description = "Student results not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<TestResultResponse> getResult(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {

        return ResponseEntity.ok(service.getResult(studentId));
    }
}
