package com.tech.test.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.entity.Order;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.enums.Branch;
import com.tech.test.serviceImpl.KafkaProducerServiceImpl;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceImplTest {

    @Mock private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks private KafkaProducerServiceImpl kafkaProducerServiceImpl;

    @Test
    void testSendTestSubmission() {

        SubmitTestRequest request = new SubmitTestRequest();
        request.setStudentId(1L);

        AnswerRequest answer = new AnswerRequest();
        answer.setQuestionId(1L);
        answer.setSelectedAnswer("A");

        request.setAnswers(Arrays.asList(answer));

        kafkaProducerServiceImpl.sendTestSubmission(request);

        verify(kafkaTemplate).send(eq("submit-test-topic"), eq(request));
    }

    @Test
    void testSendOrder() {

        Order order = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");

        kafkaProducerServiceImpl.sendOrder(order);

        verify(kafkaTemplate).send(eq("order-topic"), eq(order));
    }

    @Test
    void testSendQuestionAdded() {

        Question question = new Question(1L, "What is 2+2?", "3", "4", "5", "6", "B");

        kafkaProducerServiceImpl.sendQuestionAdded(question);

        verify(kafkaTemplate).send(eq("question-added-topic"), eq(question));
    }

    @Test
    void testSendQuestionDeleted() {

        kafkaProducerServiceImpl.sendQuestionDeleted(1L);

        verify(kafkaTemplate).send(eq("question-deleted-topic"), eq(1L));
    }

    @Test
    void testSendStudentRecordUpdated() {

        StudentTestRecord record = new StudentTestRecord(1L, "12345", Branch.CSE, 85, 1L);

        kafkaProducerServiceImpl.sendStudentRecordUpdated(record);

        verify(kafkaTemplate).send(eq("student-record-updated-topic"), eq(record));
    }

    @Test
    void testSendStudentRecordDeleted() {

        kafkaProducerServiceImpl.sendStudentRecordDeleted(1L);

        verify(kafkaTemplate).send(eq("student-record-deleted-topic"), eq(1L));
    }

    @Test
    void testSendOrderUpdated() {

        Order order = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");

        kafkaProducerServiceImpl.sendOrderUpdated(order);

        verify(kafkaTemplate).send(eq("order-updated-topic"), eq(order));
    }

    @Test
    void testSendOrderDeleted() {

        kafkaProducerServiceImpl.sendOrderDeleted(1L);

        verify(kafkaTemplate).send(eq("order-deleted-topic"), eq(1L));
    }
}
