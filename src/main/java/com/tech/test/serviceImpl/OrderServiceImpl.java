package com.tech.test.serviceImpl;

import com.tech.test.entity.Order;
import com.tech.test.repository.OrderRepository;
import com.tech.test.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements com.tech.test.service.OrderService {

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

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return repository.findById(id);
    }

    public Order updateOrder(Long id, Order orderDetails) {
        Optional<Order> optionalOrder = repository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setProductName(orderDetails.getProductName());
            order.setQuantity(orderDetails.getQuantity());
            order.setAddress(orderDetails.getAddress());
            order.setCity(orderDetails.getCity());
            order.setZipCode(orderDetails.getZipCode());
            Order saved = repository.save(order);
            producer.sendOrderUpdated(saved);
            return saved;
        } else {
            throw new RuntimeException("Order not found with id " + id);
        }
    }

    public void deleteOrder(Long id) {
        repository.deleteById(id);
        producer.sendOrderDeleted(id);
    }

    public Order submitOrderWithAddress(Order order) {
        // Assuming address is already in the order
        return createOrder(order);
    }
}
