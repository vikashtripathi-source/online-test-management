package com.tech.test.service;

import com.tech.test.entity.Order;

public interface KafkaConsumerService {

    void consume(String message);

    void consume(Order order);
}
