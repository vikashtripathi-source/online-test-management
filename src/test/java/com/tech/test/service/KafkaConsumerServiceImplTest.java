package com.tech.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.entity.Order;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentAnswer;
import com.tech.test.exception.KafkaProcessingException;
import com.tech.test.repository.QuestionRepository;
import com.tech.test.repository.StudentAnswerRepository;
import com.tech.test.serviceImpl.KafkaConsumerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceImplTest {

    @Mock
    private QuestionRepository questionRepo;

    @Mock
    private StudentAnswerRepository answerRepo;

    @Mock
    private EmailService emailService;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private KafkaConsumerServiceImpl kafkaConsumerServiceImpl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testConsumeSubmitTestTopic_Success() throws Exception {
        Question q1 = new Question(1L, "Q1", "A", "B", "C", "D", "A");
        Question q2 = new Question(2L, "Q2", "A", "B", "C", "D", "B");

        AnswerRequest ans1 = new AnswerRequest();
        ans1.setQuestionId(1L);
        ans1.setSelectedAnswer("A");

        AnswerRequest ans2 = new AnswerRequest();
        ans2.setQuestionId(2L);
        ans2.setSelectedAnswer("C"); // Wrong

        SubmitTestRequest request = new SubmitTestRequest();
        request.setStudentId(1L);
        request.setAnswers(Arrays.asList(ans1, ans2));

        String message = objectMapper.writeValueAsString(request);

        when(questionRepo.findById(1L)).thenReturn(Optional.of(q1));
        when(questionRepo.findById(2L)).thenReturn(Optional.of(q2));
        when(answerRepo.save(any(StudentAnswer.class))).thenReturn(new StudentAnswer());

        assertDoesNotThrow(() -> kafkaConsumerServiceImpl.consume(message));

        verify(questionRepo, times(1)).findById(1L);
        verify(questionRepo, times(1)).findById(2L);
        verify(answerRepo, times(2)).save(any(StudentAnswer.class));
    }

    @Test
    public void testConsumeSubmitTestTopic_InvalidJson() {
        String invalidMessage = "{invalid json}";

        assertThrows(KafkaProcessingException.class, () -> kafkaConsumerServiceImpl.consume(invalidMessage));
    }

    @Test
    public void testConsumeOrderTopic() {
        Order order = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");

        assertDoesNotThrow(() -> kafkaConsumerServiceImpl.consume(order));
    }

    @Test
    public void testConsumeSubmitTestTopic_WithNonExistentQuestion() throws Exception {
        AnswerRequest ans1 = new AnswerRequest();
        ans1.setQuestionId(1L);
        ans1.setSelectedAnswer("A");

        SubmitTestRequest request = new SubmitTestRequest();
        request.setStudentId(1L);
        request.setAnswers(Arrays.asList(ans1));

        String message = objectMapper.writeValueAsString(request);

        when(questionRepo.findById(1L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> kafkaConsumerServiceImpl.consume(message));

        verify(questionRepo, times(1)).findById(1L);
        verify(answerRepo, never()).save(any(StudentAnswer.class)); // No save because question not found
    }
}
