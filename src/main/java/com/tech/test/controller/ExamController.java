package com.tech.test.controller;

import com.tech.test.dto.QuestionDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.TestResultResponse;
import com.tech.test.entity.Question;
import com.tech.test.service.ExamService;
import com.tech.test.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExamController {

    @Autowired
    private ExamService service;
    @Autowired
    private KafkaProducerService kafkaProducerService;


    @PostMapping("/question")
    public Question addQuestion(@RequestBody Question q) {
        return service.addQuestion(q);
    }


    @PostMapping("/questions")
    public List<Question> addAllQuestions(@RequestBody List<Question> questions) {
        return service.addAllQuestions(questions);
    }


    @GetMapping("/questions")
    public List<QuestionDTO> getQuestions() {
        return service.getAllQuestions();
    }

    @PostMapping("/submit")
    public TestResultResponse submit(@RequestBody SubmitTestRequest request) {
        return service.submitTest(request);
    }

    @PostMapping("/submit-test")
    public String submitTest(@RequestBody SubmitTestRequest request) {
        kafkaProducerService.sendTestSubmission(request);
        return "Test submitted successfully (processing async)";
    }
}