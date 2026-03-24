package com.tech.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.entity.Order;
import com.tech.test.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateOrder() throws Exception {

        Order order = new Order(null,"Product A",5,
                "123 Main St","City","12345");

        Order savedOrder = new Order(1L,"Product A",5,
                "123 Main St","City","12345");

        when(orderService.createOrder(any())).thenReturn(savedOrder);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService).createOrder(any());
    }

    @Test
    void testGetAllOrders() throws Exception {

        List<Order> orders = Arrays.asList(
                new Order(1L,"A",5,"Addr","City","111"),
                new Order(2L,"B",3,"Addr2","Town","222")
        );

        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetOrderById() throws Exception {

        Order order = new Order(1L,"A",5,"Addr","City","111");

        when(orderService.getOrderById(1L))
                .thenReturn(Optional.of(order));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {

        when(orderService.getOrderById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateOrder() throws Exception {

        Order updated = new Order(1L,"Updated",10,
                "Addr","City","111");

        when(orderService.updateOrder(eq(1L), any()))
                .thenReturn(updated);

        mockMvc.perform(put("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Updated"));
    }

    @Test
    void testDeleteOrder() throws Exception {

        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testSubmitOrderWithAddress() throws Exception {

        Order saved = new Order(1L,"A",5,
                "Addr","City","111");

        when(orderService.submitOrderWithAddress(any()))
                .thenReturn(saved);

        mockMvc.perform(post("/orders/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}