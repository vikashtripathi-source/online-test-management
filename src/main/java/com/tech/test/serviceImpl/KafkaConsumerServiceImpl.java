package com.tech.test.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.dto.OrderDTO;
import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.dto.AnswerRequest;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentAnswer;
import com.tech.test.exception.KafkaProcessingException;
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

    public KafkaConsumerServiceImpl(QuestionRepository questionRepo, StudentAnswerRepository answerRepo, EmailService emailService, InventoryService inventoryService, OrderMapper orderMapper) {
        this.questionRepo = questionRepo;
        this.answerRepo = answerRepo;
        this.emailService = emailService;
        this.inventoryService = inventoryService;
        this.orderMapper = orderMapper;
    }

    @KafkaListener(topics = "submit-test-topic", groupId = "my-group")
    public void consume(String message) {

        try {
            SubmitTestRequest request = objectMapper.readValue(message, SubmitTestRequest.class);

            int score = 0;
            int total = request.getAnswers().size();

            for (AnswerRequest ans : request.getAnswers()) {

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
                }
            }

            System.out.println(" Test processed for student: " + request.getStudentId());
            System.out.println(" Score: " + score + "/" + total);

        } catch (Exception e) {
            throw new KafkaProcessingException("Error processing Kafka message");
        }
    }

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void consume(OrderDTO orderDTO) {

        System.out.println("Received Order: " + orderDTO.getProductName());
        sendEmail(orderDTO);
        updateInventory(orderDTO);
    }

    private void sendEmail(OrderDTO orderDTO) {
        emailService.sendOrderConfirmation(orderDTO);
    }

    private void updateInventory(OrderDTO orderDTO) {
        inventoryService.updateInventory(orderDTO);
    }
}
