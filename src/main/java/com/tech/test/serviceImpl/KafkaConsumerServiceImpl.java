package com.tech.test.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.OrderDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentAnswer;
import com.tech.test.exception.KafkaException;
import com.tech.test.exception.TestSubmissionException;
import com.tech.test.mapper.OrderMapper;
import com.tech.test.repository.QuestionRepository;
import com.tech.test.repository.StudentAnswerRepository;
import com.tech.test.service.EmailService;
import com.tech.test.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerServiceImpl implements com.tech.test.service.KafkaConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QuestionRepository questionRepo;
    private final StudentAnswerRepository answerRepo;
    private final EmailService emailService;
    private final InventoryService inventoryService;
    private final OrderMapper orderMapper;

    public KafkaConsumerServiceImpl(
            QuestionRepository questionRepo,
            StudentAnswerRepository answerRepo,
            EmailService emailService,
            InventoryService inventoryService,
            OrderMapper orderMapper) {
        this.questionRepo = questionRepo;
        this.answerRepo = answerRepo;
        this.emailService = emailService;
        this.inventoryService = inventoryService;
        this.orderMapper = orderMapper;
    }

    @KafkaListener(topics = "submit-test-topic", groupId = "my-group")
    public void consume(String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                throw new KafkaException("Received null or empty message");
            }

            SubmitTestRequest request = objectMapper.readValue(message, SubmitTestRequest.class);

            if (request == null) {
                throw new TestSubmissionException("Failed to deserialize test submission request");
            }
            if (request.getStudentId() == null || request.getStudentId() <= 0) {
                throw new TestSubmissionException("Invalid student ID in test submission");
            }
            if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
                throw new TestSubmissionException("No answers found in test submission");
            }

            int score = 0;
            int total = request.getAnswers().size();

            for (AnswerRequest ans : request.getAnswers()) {
                if (ans.getQuestionId() == null || ans.getQuestionId() <= 0) {
                    throw new TestSubmissionException("Invalid question ID in answer");
                }
                if (ans.getSelectedAnswer() == null || ans.getSelectedAnswer().trim().isEmpty()) {
                    throw new TestSubmissionException(
                            "Empty selected answer for question ID: " + ans.getQuestionId());
                }

                Question q = questionRepo.findById(ans.getQuestionId()).orElse(null);

                if (q != null) {
                    StudentAnswer studentAnswer = new StudentAnswer();
                    studentAnswer.setStudentId(request.getStudentId());
                    studentAnswer.setQuestionId(ans.getQuestionId());
                    studentAnswer.setSelectedAnswer(ans.getSelectedAnswer());

                    answerRepo.save(studentAnswer);

                    if (q.getCorrectAnswer().equalsIgnoreCase(ans.getSelectedAnswer())) {
                        score++;
                    }
                } else {
                    throw new TestSubmissionException(
                            "Question not found with ID: " + ans.getQuestionId());
                }
            }

            System.out.println(" Test processed for student: " + request.getStudentId());
            System.out.println(" Score: " + score + "/" + total);

        } catch (TestSubmissionException e) {
            throw new KafkaException("Test submission processing failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new KafkaException(
                    "Error processing Kafka test submission message: " + e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void consume(OrderDTO orderDTO) {
        try {
            if (orderDTO == null) {
                throw new KafkaException("Received null order DTO");
            }
            if (orderDTO.getProductName() == null || orderDTO.getProductName().trim().isEmpty()) {
                throw new KafkaException("Invalid product name in order DTO");
            }
            if (orderDTO.getQuantity() <= 0) {
                throw new KafkaException("Invalid quantity in order DTO");
            }

            System.out.println("Received Order: " + orderDTO.getProductName());
            sendEmail(orderDTO);
            updateInventory(orderDTO);
        } catch (KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new KafkaException("Error processing Kafka order message: " + e.getMessage(), e);
        }
    }

    private void sendEmail(OrderDTO orderDTO) {
        try {
            emailService.sendOrderConfirmation(orderDTO);
        } catch (Exception e) {
            throw new KafkaException(
                    "Failed to send order confirmation email: " + e.getMessage(), e);
        }
    }

    private void updateInventory(OrderDTO orderDTO) {
        try {
            inventoryService.updateInventory(orderDTO);
        } catch (Exception e) {
            throw new KafkaException("Failed to update inventory: " + e.getMessage(), e);
        }
    }
}
