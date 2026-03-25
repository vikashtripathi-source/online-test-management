package com.tech.test.service;

import com.tech.test.dto.OrderDTO;

public interface KafkaConsumerService {

    void consume(String message);

    void consume(OrderDTO orderDTO);
}
