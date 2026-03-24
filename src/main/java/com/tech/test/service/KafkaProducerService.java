package com.tech.test.service;

import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.entity.Order;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentTestRecord;

public interface KafkaProducerService {

    void sendTestSubmission(SubmitTestRequest request);

    void sendOrder(Order order);

    void sendQuestionAdded(Question question);

    void sendQuestionDeleted(Long id);

    void sendStudentRecordUpdated(StudentTestRecord record);

    void sendStudentRecordDeleted(Long id);

    void sendOrderUpdated(Order order);

    void sendOrderDeleted(Long id);
}
