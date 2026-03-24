package com.tech.test.service;

import com.tech.test.entity.Order;
import com.tech.test.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testCreateOrder() {
        Order order = new Order(null, "Product A", 5);
        Order savedOrder = new Order(1L, "Product A", 5);

        when(orderRepository.save(order)).thenReturn(savedOrder);
        doNothing().when(kafkaProducerService).sendOrder(savedOrder);

        Order result = orderService.createOrder(order);

        assertEquals(savedOrder, result);
        verify(orderRepository, times(1)).save(order);
        verify(kafkaProducerService, times(1)).sendOrder(savedOrder);
    }
}
