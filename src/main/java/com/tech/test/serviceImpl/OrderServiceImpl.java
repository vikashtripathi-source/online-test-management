package com.tech.test.serviceImpl;

import com.tech.test.dto.OrderDTO;
import com.tech.test.entity.Order;
import com.tech.test.mapper.OrderMapper;
import com.tech.test.repository.OrderRepository;
import com.tech.test.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements com.tech.test.service.OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private KafkaProducerService producer;

    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        // 1. Save in DB
        Order savedOrder = repository.save(order);

        // 2. Send event to Kafka
        producer.sendOrder(savedOrder);

        return orderMapper.toDTO(savedOrder);
    }

    public List<OrderDTO> getAllOrders() {
        return repository.findAll().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<OrderDTO> getOrderById(Long id) {
        return repository.findById(id)
                .map(orderMapper::toDTO);
    }

    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Optional<Order> optionalOrder = repository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            // Update fields from DTO
            Order updatedOrder = orderMapper.toEntity(orderDTO);
            updatedOrder.setId(id); // Preserve the ID
            Order saved = repository.save(updatedOrder);
            producer.sendOrderUpdated(saved);
            return orderMapper.toDTO(saved);
        } else {
            throw new RuntimeException("Order not found with id " + id);
        }
    }

    public void deleteOrder(Long id) {
        repository.deleteById(id);
        producer.sendOrderDeleted(id);
    }

    public OrderDTO submitOrderWithAddress(OrderDTO orderDTO) {
        // Assuming address is already in the order
        return createOrder(orderDTO);
    }
}
