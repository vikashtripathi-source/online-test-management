package com.tech.test.service;

import com.tech.test.entity.Order;
import com.tech.test.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private KafkaProducerService producer;

    public Order createOrder(Order order) {

        // 1. Save in DB
        Order savedOrder = repository.save(order);

        // 2. Send event to Kafka
        producer.sendOrder(savedOrder);

        return savedOrder;
    }
}
