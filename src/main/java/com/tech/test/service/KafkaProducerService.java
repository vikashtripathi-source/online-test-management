package com.tech.test.service;


import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.entity.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTestSubmission(SubmitTestRequest request) {
        kafkaTemplate.send("submit-test-topic", request);
    }

    public void sendOrder(Order order) {
        kafkaTemplate.send("order-topic", order);
    }
}
