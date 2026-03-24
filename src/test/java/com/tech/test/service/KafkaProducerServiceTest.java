package com.tech.test.service;

import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.entity.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    public void testSendTestSubmission() {
        SubmitTestRequest request = new SubmitTestRequest();
        request.setStudentId(1L);
        AnswerRequest answer = new AnswerRequest();
        answer.setQuestionId(1L);
        answer.setSelectedAnswer("A");
        request.setAnswers(Arrays.asList(answer));

        kafkaProducerService.sendTestSubmission(request);

        verify(kafkaTemplate, times(1)).send(eq("submit-test-topic"), eq(request));
    }

    @Test
    public void testSendOrder() {
        Order order = new Order(1L, "Product A", 5);

        kafkaProducerService.sendOrder(order);

        verify(kafkaTemplate, times(1)).send(eq("order-topic"), eq(order));
    }
}
