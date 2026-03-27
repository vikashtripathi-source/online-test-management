package com.tech.test.serviceImpl;

import com.tech.test.dto.SubmitTestRequest;
import com.tech.test.entity.Order;
import com.tech.test.entity.Question;
import com.tech.test.entity.StudentTestRecord;
import com.tech.test.exception.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl implements com.tech.test.service.KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTestSubmission(SubmitTestRequest request) {
        try {
            if (request == null) {
                throw new KafkaException("Test submission request cannot be null");
            }
            if (request.getStudentId() == null || request.getStudentId() <= 0) {
                throw new KafkaException("Invalid student ID in test submission request");
            }
            kafkaTemplate.send("submit-test-topic", request);
        } catch (KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new KafkaException(
                    "Failed to send test submission to Kafka topic 'submit-test-topic': "
                            + e.getMessage(),
                    e);
        }
    }

    public void sendOrder(Order order) {
        try {
            if (order == null) {
                throw new KafkaException("Order cannot be null");
            }
            if (order.getId() == null) {
                throw new KafkaException("Order ID cannot be null");
            }
            kafkaTemplate.send("order-topic", order);
        } catch (KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new KafkaException(
                    "Failed to send order to Kafka topic 'order-topic': " + e.getMessage(), e);
        }
    }

    public void sendQuestionAdded(Question question) {
        try {
            if (question == null) {
                throw new KafkaException("Question cannot be null");
            }
            if (question.getId() == null) {
                throw new KafkaException("Question ID cannot be null");
            }
            kafkaTemplate.send("question-added-topic", question);
        } catch (KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new KafkaException(
                    "Failed to send question added event to Kafka topic 'question-added-topic': "
                            + e.getMessage(),
                    e);
        }
    }

    public void sendQuestionDeleted(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new KafkaException("Question ID must be a positive number");
            }
            kafkaTemplate.send("question-deleted-topic", id);
        } catch (KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new KafkaException(
                    "Failed to send question deleted event to Kafka topic 'question-deleted-topic': "
                            + e.getMessage(),
                    e);
        }
    }

    public void sendStudentRecordUpdated(StudentTestRecord record) {
        try {
            if (record == null) {
                throw new KafkaException("Student test record cannot be null");
            }
            if (record.getId() == null) {
                throw new KafkaException("Student test record ID cannot be null");
            }
            kafkaTemplate.send("student-record-updated-topic", record);
        } catch (KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new KafkaException(
                    "Failed to send student record updated event to Kafka topic 'student-record-updated-topic': "
                            + e.getMessage(),
                    e);
        }
    }

    public void sendStudentRecordDeleted(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new KafkaException("Student record ID must be a positive number");
            }
            kafkaTemplate.send("student-record-deleted-topic", id);
        } catch (KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new KafkaException(
                    "Failed to send student record deleted event to Kafka topic 'student-record-deleted-topic': "
                            + e.getMessage(),
                    e);
        }
    }

    public void sendOrderUpdated(Order order) {
        try {
            if (order == null) {
                throw new KafkaException("Order cannot be null");
            }
            if (order.getId() == null) {
                throw new KafkaException("Order ID cannot be null");
            }
            kafkaTemplate.send("order-updated-topic", order);
        } catch (KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new KafkaException(
                    "Failed to send order updated event to Kafka topic 'order-updated-topic': "
                            + e.getMessage(),
                    e);
        }
    }

    public void sendOrderDeleted(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new KafkaException("Order ID must be a positive number");
            }
            kafkaTemplate.send("order-deleted-topic", id);
        } catch (KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new KafkaException(
                    "Failed to send order deleted event to Kafka topic 'order-deleted-topic': "
                            + e.getMessage(),
                    e);
        }
    }
}
