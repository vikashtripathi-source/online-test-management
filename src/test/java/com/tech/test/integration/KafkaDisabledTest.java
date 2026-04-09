package com.tech.test.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.tech.test.entity.Question;
import com.tech.test.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "app.kafka.enabled=false")
class KafkaDisabledTest {

    @Autowired private KafkaProducerService kafkaProducerService;

    @Test
    void testKafkaMethodsShouldNotThrowExceptionWhenDisabled() {
        Question question = new Question();
        question.setId(1L);
        question.setQuestion("Test Question");
        question.setOptionA("A");
        question.setOptionB("B");
        question.setOptionC("C");
        question.setOptionD("D");
        question.setCorrectAnswer("A");

        // These should not throw exceptions when Kafka is disabled
        assertDoesNotThrow(() -> kafkaProducerService.sendQuestionAdded(question));
        assertDoesNotThrow(() -> kafkaProducerService.sendQuestionDeleted(1L));
        assertDoesNotThrow(() -> kafkaProducerService.sendOrder(null));
        assertDoesNotThrow(() -> kafkaProducerService.sendStudentRecordUpdated(null));
        assertDoesNotThrow(() -> kafkaProducerService.sendStudentRecordDeleted(1L));
        assertDoesNotThrow(() -> kafkaProducerService.sendOrderUpdated(null));
        assertDoesNotThrow(() -> kafkaProducerService.sendOrderDeleted(1L));
    }
}
