package com.tech.test.serviceImpl;


import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.entity.Order;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentTestRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl implements com.tech.test.service.KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTestSubmission(SubmitTestRequest request) {
        kafkaTemplate.send("submit-test-topic", request);
    }

    public void sendOrder(Order order) {
        kafkaTemplate.send("order-topic", order);
    }

    public void sendQuestionAdded(Question question) {
        kafkaTemplate.send("question-added-topic", question);
    }

    public void sendQuestionDeleted(Long id) {
        kafkaTemplate.send("question-deleted-topic", id);
    }

    public void sendStudentRecordUpdated(StudentTestRecord record) {
        kafkaTemplate.send("student-record-updated-topic", record);
    }

    public void sendStudentRecordDeleted(Long id) {
        kafkaTemplate.send("student-record-deleted-topic", id);
    }

    public void sendOrderUpdated(Order order) {
        kafkaTemplate.send("order-updated-topic", order);
    }

    public void sendOrderDeleted(Long id) {
        kafkaTemplate.send("order-deleted-topic", id);
    }
}
