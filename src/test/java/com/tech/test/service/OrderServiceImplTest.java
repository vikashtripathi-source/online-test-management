package com.tech.test.service;

import com.tech.test.entity.Order;
import com.tech.test.repository.OrderRepository;
import com.tech.test.serviceImpl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @Test
    public void testCreateOrder() {
        Order order = new Order(null, "Product A", 5, "123 Main St", "City", "12345");
        Order savedOrder = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");

        when(orderRepository.save(order)).thenReturn(savedOrder);
        doNothing().when(kafkaProducerService).sendOrder(savedOrder);

        Order result = orderServiceImpl.createOrder(order);

        assertEquals(savedOrder, result);
        verify(orderRepository, times(1)).save(order);
        verify(kafkaProducerService, times(1)).sendOrder(savedOrder);
    }

    @Test
    public void testGetAllOrders() {
        Order order1 = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");
        Order order2 = new Order(2L, "Product B", 3, "456 Elm St", "Town", "67890");
        List<Order> orders = Arrays.asList(order1, order2);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderServiceImpl.getAllOrders();

        assertEquals(orders, result);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testGetOrderById() {
        Order order = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderServiceImpl.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateOrder() {
        Order existingOrder = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");
        Order updatedDetails = new Order(null, "Updated Product", 10, "789 Oak St", "Village", "11111");
        Order updatedOrder = new Order(1L, "Updated Product", 10, "789 Oak St", "Village", "11111");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        doNothing().when(kafkaProducerService).sendOrderUpdated(updatedOrder);

        Order result = orderServiceImpl.updateOrder(1L, updatedDetails);

        assertEquals(updatedOrder, result);
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(kafkaProducerService, times(1)).sendOrderUpdated(updatedOrder);
    }

    @Test
    public void testUpdateOrderNotFound() {
        Order updatedDetails = new Order(null, "Updated Product", 10, "789 Oak St", "Village", "11111");

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderServiceImpl.updateOrder(1L, updatedDetails));
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testSubmitOrderWithAddress() {
        Order order = new Order(null, "Product A", 5, "123 Main St", "City", "12345");
        Order savedOrder = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");

        when(orderRepository.save(order)).thenReturn(savedOrder);
        doNothing().when(kafkaProducerService).sendOrder(savedOrder);

        Order result = orderServiceImpl.submitOrderWithAddress(order);

        assertEquals(savedOrder, result);
        verify(orderRepository, times(1)).save(order);
        verify(kafkaProducerService, times(1)).sendOrder(savedOrder);
    }

    @Test
    public void testDeleteOrder() {
        doNothing().when(orderRepository).deleteById(1L);
        doNothing().when(kafkaProducerService).sendOrderDeleted(1L);

        orderServiceImpl.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
        verify(kafkaProducerService, times(1)).sendOrderDeleted(1L);
    }
}
