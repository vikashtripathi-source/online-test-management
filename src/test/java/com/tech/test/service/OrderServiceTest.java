package com.tech.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tech.test.dto.OrderDTO;
import com.tech.test.entity.Order;
import com.tech.test.mapper.OrderMapper;
import com.tech.test.repository.OrderRepository;
import com.tech.test.serviceImpl.OrderServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private KafkaProducerService kafkaProducerService;

    @InjectMocks private OrderServiceImpl orderService;

    private OrderDTO sampleOrderDTO() {
        OrderDTO dto = new OrderDTO();
        dto.setProductName("Laptop");
        dto.setQuantity(2);
        dto.setAddress("123 Main St");
        dto.setCity("New York");
        dto.setZipCode("10001");
        dto.setStudentId(1L);
        return dto;
    }

    @Test
    void testCreateOrder_Success() {
        OrderDTO orderDTO = sampleOrderDTO();
        Order order = new Order();
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setProductName(orderDTO.getProductName());
        savedOrder.setQuantity(orderDTO.getQuantity());

        OrderDTO resultDTO = new OrderDTO();
        resultDTO.setId(1L);
        resultDTO.setProductName(orderDTO.getProductName());
        resultDTO.setQuantity(orderDTO.getQuantity());

        when(orderMapper.toEntity(orderDTO)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toDTO(savedOrder)).thenReturn(resultDTO);
        doNothing().when(kafkaProducerService).sendOrder(any(Order.class));

        OrderDTO result = orderService.createOrder(orderDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getProductName());
        assertEquals(2, result.getQuantity());
    }

    @Test
    void testGetAllOrders_Success() {
        Order order = new Order();
        order.setId(1L);
        order.setProductName("Laptop");

        OrderDTO orderDTO = sampleOrderDTO();
        orderDTO.setId(1L);

        List<Order> orders = Arrays.asList(order);

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testGetOrderById_Success() {
        Order order = new Order();
        order.setId(1L);
        order.setProductName("Laptop");

        OrderDTO orderDTO = sampleOrderDTO();
        orderDTO.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        Optional<OrderDTO> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<OrderDTO> result = orderService.getOrderById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateOrder_Success() {
        OrderDTO orderDTO = sampleOrderDTO();
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setProductName("Updated Laptop");
        updatedOrder.setQuantity(3);

        OrderDTO resultDTO = new OrderDTO();
        resultDTO.setId(1L);
        resultDTO.setProductName("Updated Laptop");
        resultDTO.setQuantity(3);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderMapper.toEntity(orderDTO)).thenReturn(updatedOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        when(orderMapper.toDTO(updatedOrder)).thenReturn(resultDTO);
        doNothing().when(kafkaProducerService).sendOrderUpdated(any(Order.class));

        OrderDTO result = orderService.updateOrder(1L, orderDTO);

        assertNotNull(result);
        assertEquals("Updated Laptop", result.getProductName());
        assertEquals(3, result.getQuantity());
    }

    @Test
    void testUpdateOrder_NotFound() {
        OrderDTO orderDTO = sampleOrderDTO();

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                com.tech.test.exception.OrderException.class,
                () -> orderService.updateOrder(1L, orderDTO));
    }

    @Test
    void testDeleteOrder_Success() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);
        doNothing().when(kafkaProducerService).sendOrderDeleted(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteOrder_NotFound() {
        when(orderRepository.existsById(1L)).thenReturn(false);

        assertThrows(
                com.tech.test.exception.OrderException.class, () -> orderService.deleteOrder(1L));
    }
}
