package com.tech.test.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.entity.Order;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.enums.Branch;
import com.tech.test.service.KafkaProducerService;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "app.kafka.enabled=true")
class AllKafkaTopicsTest {

    @Autowired private KafkaProducerService kafkaProducerService;

    @Test
    void testAllKafkaTopicsShouldWorkWhenEnabled() {
        // Test question-added-topic
        Question question = new Question();
        question.setId(1L);
        question.setQuestion("Test Question");
        question.setOptionA("A");
        question.setOptionB("B");
        question.setOptionC("C");
        question.setOptionD("D");
        question.setCorrectAnswer("A");
        question.setBranch(Branch.CSE);

        assertDoesNotThrow(() -> kafkaProducerService.sendQuestionAdded(question));

        // Test question-deleted-topic
        assertDoesNotThrow(() -> kafkaProducerService.sendQuestionDeleted(1L));

        // Test order-topic
        Order order = new Order();
        order.setId(1L);
        order.setProductName("Test Order");
        order.setStudentId(1L);

        assertDoesNotThrow(() -> kafkaProducerService.sendOrder(order));

        // Test order-updated-topic
        assertDoesNotThrow(() -> kafkaProducerService.sendOrderUpdated(order));

        // Test order-deleted-topic
        assertDoesNotThrow(() -> kafkaProducerService.sendOrderDeleted(1L));

        // Test student-record-updated-topic
        StudentTestRecord record = new StudentTestRecord();
        record.setId(1L);
        record.setStudentId(1L);
        record.setStudentName("Test Student");
        record.setTestName("Test Exam");
        record.setScore(85);
        record.setBranch(Branch.CSE);

        assertDoesNotThrow(() -> kafkaProducerService.sendStudentRecordUpdated(record));

        // Test student-record-deleted-topic
        assertDoesNotThrow(() -> kafkaProducerService.sendStudentRecordDeleted(1L));

        // Test submit-test-topic
        SubmitTestRequest testRequest = new SubmitTestRequest();
        testRequest.setStudentId(1L);

        AnswerRequest answer = new AnswerRequest();
        answer.setQuestionId(1L);
        answer.setSelectedAnswer("A");

        testRequest.setAnswers(Arrays.asList(answer));

        assertDoesNotThrow(() -> kafkaProducerService.sendTestSubmission(testRequest));
    }
}
