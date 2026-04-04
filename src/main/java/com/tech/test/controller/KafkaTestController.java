package com.tech.test.controller;

import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.service.KafkaProducerService;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-kafka")
public class KafkaTestController {

    @Autowired private KafkaProducerService kafkaProducerService;

    @PostMapping("/test-all-topics")
    public ResponseEntity<String> testAllKafkaTopics() {
        try {
            // Test submit-test-topic
            SubmitTestRequest testRequest = new SubmitTestRequest();
            testRequest.setStudentId(1L);

            AnswerRequest answer = new AnswerRequest();
            answer.setQuestionId(1L);
            answer.setSelectedAnswer("A");
            testRequest.setAnswers(Arrays.asList(answer));

            kafkaProducerService.sendTestSubmission(testRequest);

            // Test student-record-updated-topic
            StudentTestRecord record = new StudentTestRecord();
            record.setId(1L);
            record.setStudentId(1L);
            record.setStudentName("Test Student");
            record.setTestName("Test Exam");
            record.setScore(85);
            kafkaProducerService.sendStudentRecordUpdated(record);

            // Test student-record-deleted-topic
            kafkaProducerService.sendStudentRecordDeleted(1L);

            // Test question-added-topic
            Question question = new Question();
            question.setId(1L);
            question.setQuestion("Test Question for Kafka");
            question.setOptionA("Option A");
            question.setOptionB("Option B");
            question.setOptionC("Option C");
            question.setOptionD("Option D");
            question.setCorrectAnswer("A");
            kafkaProducerService.sendQuestionAdded(question);

            // Test question-deleted-topic
            kafkaProducerService.sendQuestionDeleted(1L);

            return ResponseEntity.ok("All Kafka topics tested successfully! Check your Kafka UI.");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error testing Kafka topics: " + e.getMessage());
        }
    }

    @PostMapping("/test/{topic}")
    public ResponseEntity<String> testSpecificTopic(@PathVariable String topic) {
        try {
            switch (topic.toLowerCase()) {
                case "submit-test":
                    SubmitTestRequest request = new SubmitTestRequest();
                    request.setStudentId(1L);

                    AnswerRequest answer = new AnswerRequest();
                    answer.setQuestionId(1L);
                    answer.setSelectedAnswer("A");
                    request.setAnswers(Arrays.asList(answer));

                    kafkaProducerService.sendTestSubmission(request);
                    break;

                case "student-record-updated":
                    StudentTestRecord record = new StudentTestRecord();
                    record.setId(1L);
                    record.setStudentId(1L);
                    kafkaProducerService.sendStudentRecordUpdated(record);
                    break;

                case "student-record-deleted":
                    kafkaProducerService.sendStudentRecordDeleted(1L);
                    break;

                case "question-added":
                    Question question = new Question();
                    question.setId(1L);
                    question.setQuestion("Single Test Question");
                    kafkaProducerService.sendQuestionAdded(question);
                    break;

                case "question-deleted":
                    kafkaProducerService.sendQuestionDeleted(1L);
                    break;

                default:
                    return ResponseEntity.badRequest().body("Unknown topic: " + topic);
            }

            return ResponseEntity.ok("Topic '" + topic + "' tested successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error testing topic '" + topic + "': " + e.getMessage());
        }
    }
}
