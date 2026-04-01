package com.tech.test.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.dto.AnswerRequest;
import com.tech.test.dto.OrderDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.entity.Order;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentAnswer;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.exception.KafkaException;
import com.tech.test.exception.TestSubmissionException;
import com.tech.test.mapper.OrderMapper;
import com.tech.test.repository.QuestionRepository;
import com.tech.test.repository.StudentAnswerRepository;
import com.tech.test.service.EmailService;
import com.tech.test.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerServiceImpl implements com.tech.test.service.KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerServiceImpl.class);

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

            logger.info(
                    "Test processed for student: {} with score: {}/{}",
                    request.getStudentId(),
                    score,
                    total);

        } catch (TestSubmissionException e) {
            logger.error("Test submission processing failed: {}", e.getMessage(), e);
            throw new KafkaException("Test submission processing failed: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error processing Kafka test submission message: {}", e.getMessage(), e);
            throw new KafkaException(
                    "Error processing Kafka test submission message: " + e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "question-added-topic", groupId = "admin-group")
    public void consumeQuestionAdded(Question question) {
        try {
            logger.info("New question added: {} - {}", question.getId(), question.getQuestion());
        } catch (Exception e) {
            logger.error("Error processing question added event: {}", e.getMessage(), e);
            throw new KafkaException("Error processing question added event: " + e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "question-deleted-topic", groupId = "admin-group")
    public void consumeQuestionDeleted(Long questionId) {
        try {
            logger.info("Question deleted: {}", questionId);
        } catch (Exception e) {
            logger.error("Error processing question deleted event: {}", e.getMessage(), e);
            throw new KafkaException(
                    "Error processing question deleted event: " + e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "student-record-updated-topic", groupId = "admin-group")
    public void consumeStudentRecordUpdated(StudentTestRecord record) {
        try {
            logger.info(
                    "Student record updated: {} - Score: {}",
                    record.getStudentId(),
                    record.getScore());
        } catch (Exception e) {
            logger.error("Error processing student record updated event: {}", e.getMessage(), e);
            throw new KafkaException(
                    "Error processing student record updated event: " + e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "student-record-deleted-topic", groupId = "admin-group")
    public void consumeStudentRecordDeleted(Long recordId) {
        try {
            logger.info("Student record deleted: {}", recordId);
        } catch (Exception e) {
            logger.error("Error processing student record deleted event: {}", e.getMessage(), e);
            throw new KafkaException(
                    "Error processing student record deleted event: " + e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "order-updated-topic", groupId = "admin-group")
    public void consumeOrderUpdated(Order order) {
        try {
            logger.info("Order updated: {} - Status: {}", order.getId(), order.getStatus());
        } catch (Exception e) {
            logger.error("Error processing order updated event: {}", e.getMessage(), e);
            throw new KafkaException("Error processing order updated event: " + e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "order-deleted-topic", groupId = "admin-group")
    public void consumeOrderDeleted(Long orderId) {
        try {
            logger.info("Order deleted: {}", orderId);
        } catch (Exception e) {
            logger.error("Error processing order deleted event: {}", e.getMessage(), e);
            throw new KafkaException("Error processing order deleted event: " + e.getMessage(), e);
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

            logger.info(
                    "Received Order: {} - Quantity: {}",
                    orderDTO.getProductName(),
                    orderDTO.getQuantity());
            sendEmail(orderDTO);
            updateInventory(orderDTO);
        } catch (KafkaException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing Kafka order message: {}", e.getMessage(), e);
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
