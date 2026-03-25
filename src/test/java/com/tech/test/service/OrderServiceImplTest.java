package com.tech.test.service;

import com.tech.test.dto.OrderDTO;
import com.tech.test.entity.Order;
import com.tech.test.mapper.OrderMapper;
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
    private OrderMapper orderMapper;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @Test
    public void testCreateOrder() {
        OrderDTO orderDTO = new OrderDTO(null, "Product A", 5, "123 Main St", "City", "12345");
        Order order = new Order(null, "Product A", 5, "123 Main St", "City", "12345");
        Order savedOrder = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");
        OrderDTO savedOrderDTO = new OrderDTO(1L, "Product A", 5, "123 Main St", "City", "12345");

        when(orderMapper.toEntity(orderDTO)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(orderMapper.toDTO(savedOrder)).thenReturn(savedOrderDTO);
        doNothing().when(kafkaProducerService).sendOrder(savedOrder);

        OrderDTO result = orderServiceImpl.createOrder(orderDTO);

        assertEquals(savedOrderDTO, result);
        verify(orderMapper, times(1)).toEntity(orderDTO);
        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).toDTO(savedOrder);
        verify(kafkaProducerService, times(1)).sendOrder(savedOrder);
    }

    @Test
    public void testGetAllOrders() {
        Order order1 = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");
        Order order2 = new Order(2L, "Product B", 3, "456 Elm St", "Town", "67890");
        List<Order> orders = Arrays.asList(order1, order2);

        OrderDTO orderDTO1 = new OrderDTO(1L, "Product A", 5, "123 Main St", "City", "12345");
        OrderDTO orderDTO2 = new OrderDTO(2L, "Product B", 3, "456 Elm St", "Town", "67890");
        List<OrderDTO> orderDTOs = Arrays.asList(orderDTO1, orderDTO2);

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toDTO(order1)).thenReturn(orderDTO1);
        when(orderMapper.toDTO(order2)).thenReturn(orderDTO2);

        List<OrderDTO> result = orderServiceImpl.getAllOrders();

        assertEquals(orderDTOs, result);
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).toDTO(order1);
        verify(orderMapper, times(1)).toDTO(order2);
    }

    @Test
    public void testGetOrderById() {
        Order order = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");
        OrderDTO orderDTO = new OrderDTO(1L, "Product A", 5, "123 Main St", "City", "12345");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        Optional<OrderDTO> result = orderServiceImpl.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(orderDTO, result.get());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderMapper, times(1)).toDTO(order);
    }

    @Test
    public void testUpdateOrder() {
        Order existingOrder = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");
        OrderDTO updatedDetails = new OrderDTO(null, "Updated Product", 10, "789 Oak St", "Village", "11111");
        Order updatedOrder = new Order(1L, "Updated Product", 10, "789 Oak St", "Village", "11111");
        OrderDTO updatedOrderDTO = new OrderDTO(1L, "Updated Product", 10, "789 Oak St", "Village", "11111");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderMapper.toEntity(updatedDetails)).thenReturn(updatedOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        when(orderMapper.toDTO(updatedOrder)).thenReturn(updatedOrderDTO);
        doNothing().when(kafkaProducerService).sendOrderUpdated(updatedOrder);

        OrderDTO result = orderServiceImpl.updateOrder(1L, updatedDetails);

        assertEquals(updatedOrderDTO, result);
        verify(orderRepository, times(1)).findById(1L);
        verify(orderMapper, times(1)).toEntity(updatedDetails);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).toDTO(updatedOrder);
        verify(kafkaProducerService, times(1)).sendOrderUpdated(updatedOrder);
    }

    @Test
    public void testUpdateOrderNotFound() {
        OrderDTO updatedDetails = new OrderDTO(null, "Updated Product", 10, "789 Oak St", "Village", "11111");

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderServiceImpl.updateOrder(1L, updatedDetails));
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testSubmitOrderWithAddress() {
        OrderDTO orderDTO = new OrderDTO(null, "Product A", 5, "123 Main St", "City", "12345");
        Order order = new Order(null, "Product A", 5, "123 Main St", "City", "12345");
        Order savedOrder = new Order(1L, "Product A", 5, "123 Main St", "City", "12345");
        OrderDTO savedOrderDTO = new OrderDTO(1L, "Product A", 5, "123 Main St", "City", "12345");

        when(orderMapper.toEntity(orderDTO)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(orderMapper.toDTO(savedOrder)).thenReturn(savedOrderDTO);
        doNothing().when(kafkaProducerService).sendOrder(savedOrder);

        OrderDTO result = orderServiceImpl.submitOrderWithAddress(orderDTO);

        assertEquals(savedOrderDTO, result);
        verify(orderMapper, times(1)).toEntity(orderDTO);
        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).toDTO(savedOrder);
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
