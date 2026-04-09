package com.tech.test.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.entity.Order;
import com.tech.test.entity.OrderItem;
import com.tech.test.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class OrderSerializationTest {

    @Test
    void testOrderSerialization() throws Exception {
        // Create an order
        Order order = new Order();
        order.setId(1L);
        order.setStudentId(1L);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setProductName("Test Product"); // Legacy field

        // Try to serialize
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(order);

        assertNotNull(json);
        assertFalse(json.isEmpty());
        System.out.println("Serialized Order: " + json);

        // Try to deserialize
        Order deserializedOrder = objectMapper.readValue(json, Order.class);
        assertNotNull(deserializedOrder);
        assertEquals(order.getId(), deserializedOrder.getId());
        assertEquals(order.getStudentId(), deserializedOrder.getStudentId());
    }

    @Test
    void testOrderWithOrderItemsSerialization() throws Exception {
        // Create an order with order items
        Order order = new Order();
        order.setId(1L);
        order.setStudentId(1L);
        order.setTotalAmount(new BigDecimal("200.00"));
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        // Create order items
        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setProductId(1L);
        item1.setQuantity(2);
        item1.setPrice(new BigDecimal("50.00"));
        item1.setOrder(order);

        OrderItem item2 = new OrderItem();
        item2.setId(2L);
        item2.setProductId(2L);
        item2.setQuantity(1);
        item2.setPrice(new BigDecimal("100.00"));
        item2.setOrder(order);

        order.setOrderItems(java.util.Arrays.asList(item1, item2));

        // Try to serialize
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String json = objectMapper.writeValueAsString(order);

        assertNotNull(json);
        assertFalse(json.isEmpty());
        System.out.println("Serialized Order with Items: " + json);

        // Check that orderItems are included
        assertTrue(json.contains("orderItems"));
    }
}
