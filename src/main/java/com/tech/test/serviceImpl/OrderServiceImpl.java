package com.tech.test.serviceImpl;

import com.tech.test.dto.OrderDTO;
import com.tech.test.entity.Order;
import com.tech.test.exception.InvalidDataException;
import com.tech.test.exception.KafkaException;
import com.tech.test.exception.OrderException;
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
        try {
            if (orderDTO == null) {
                throw new InvalidDataException("Order DTO cannot be null");
            }
            if (orderDTO.getProductName() == null || orderDTO.getProductName().trim().isEmpty()) {
                throw new InvalidDataException("Product name cannot be null or empty");
            }
            if (orderDTO.getQuantity() <= 0) {
                throw new InvalidDataException("Quantity must be a positive number");
            }
            
            Order order = orderMapper.toEntity(orderDTO);
            Order savedOrder = repository.save(order);
            
            try {
                producer.sendOrder(savedOrder);
            } catch (Exception e) {
                throw new KafkaException("Failed to send order event to Kafka: " + e.getMessage(), e);
            }
            
            return orderMapper.toDTO(savedOrder);
        } catch (InvalidDataException | KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to create order: " + e.getMessage(), e);
        }
    }

    public List<OrderDTO> getAllOrders() {
        try {
            return repository.findAll().stream()
                    .map(orderMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new OrderException("Failed to retrieve all orders: " + e.getMessage(), e);
        }
    }

    public Optional<OrderDTO> getOrderById(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Order ID must be a positive number");
            }
            return repository.findById(id)
                    .map(orderMapper::toDTO);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to retrieve order with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Order ID must be a positive number");
            }
            if (orderDTO == null) {
                throw new InvalidDataException("Order DTO cannot be null");
            }
            
            Optional<Order> optionalOrder = repository.findById(id);
            if (optionalOrder.isPresent()) {
                Order updatedOrder = orderMapper.toEntity(orderDTO);
                updatedOrder.setId(id);
                Order saved = repository.save(updatedOrder);
                
                try {
                    producer.sendOrderUpdated(saved);
                } catch (Exception e) {
                    throw new KafkaException("Failed to send order update event to Kafka: " + e.getMessage(), e);
                }
                
                return orderMapper.toDTO(saved);
            } else {
                throw new OrderException("Order not found with ID: " + id);
            }
        } catch (OrderException | InvalidDataException | KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to update order with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public void deleteOrder(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Order ID must be a positive number");
            }
            if (!repository.existsById(id)) {
                throw new OrderException("Order not found with ID: " + id);
            }
            repository.deleteById(id);
            
            try {
                producer.sendOrderDeleted(id);
            } catch (Exception e) {
                throw new KafkaException("Failed to send order delete event to Kafka: " + e.getMessage(), e);
            }
        } catch (OrderException | InvalidDataException | KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to delete order with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public OrderDTO submitOrderWithAddress(OrderDTO orderDTO) {
        try {
            if (orderDTO == null) {
                throw new InvalidDataException("Order DTO cannot be null");
            }
            return createOrder(orderDTO);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to submit order with address: " + e.getMessage(), e);
        }
    }
}
