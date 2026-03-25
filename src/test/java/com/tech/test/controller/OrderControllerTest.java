package com.tech.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.dto.OrderDTO;
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

        OrderDTO orderDTO = new OrderDTO(null,"Product A",5,
                "123 Main St","City","12345");

        OrderDTO savedOrderDTO = new OrderDTO(1L,"Product A",5,
                "123 Main St","City","12345");

        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(savedOrderDTO);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetAllOrders() throws Exception {

        OrderDTO order1 = new OrderDTO(1L,"Product A",5,
                "123 Main St","City","12345");

        OrderDTO order2 = new OrderDTO(2L,"Product B",3,
                "456 Elm St","Town","67890");

        List<OrderDTO> orders = Arrays.asList(order1, order2);

        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void testGetOrderById() throws Exception {

        OrderDTO order = new OrderDTO(1L,"Product A",5,
                "123 Main St","City","12345");

        when(orderService.getOrderById(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {

        when(orderService.getOrderById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateOrder() throws Exception {

        OrderDTO updatedOrder = new OrderDTO(1L,"Updated Product",10,
                "789 Oak St","Village","11111");

        when(orderService.updateOrder(eq(1L), any(OrderDTO.class))).thenReturn(updatedOrder);

        mockMvc.perform(put("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Updated Product"));
    }

    @Test
    void testDeleteOrder() throws Exception {

        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1L);
    }

    @Test
    void testSubmitOrderWithAddress() throws Exception {

        OrderDTO orderDTO = new OrderDTO(null,"Product A",5,
                "123 Main St","City","12345");

        OrderDTO submittedOrder = new OrderDTO(1L,"Product A",5,
                "123 Main St","City","12345");

        when(orderService.submitOrderWithAddress(any(OrderDTO.class))).thenReturn(submittedOrder);

        mockMvc.perform(post("/orders/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}