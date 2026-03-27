package com.tech.test.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.test.dto.OrderDTO;
import com.tech.test.service.AddressService;
import com.tech.test.service.OrderService;
import com.tech.test.util.JwtUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@Import(com.tech.test.config.SecurityConfig.class)
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private OrderService orderService;
    @MockBean private AddressService addressService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private com.tech.test.security.CustomUserDetailsService customUserDetailsService;

    @Autowired private ObjectMapper objectMapper;

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
    @WithMockUser
    void testCreateOrder_Success() throws Exception {
        OrderDTO orderDTO = sampleOrderDTO();
        OrderDTO savedOrder = new OrderDTO();
        savedOrder.setId(1L);
        savedOrder.setProductName(orderDTO.getProductName());
        savedOrder.setQuantity(orderDTO.getQuantity());

        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(savedOrder);

        mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @WithMockUser
    void testCreateOrder_ValidationError() throws Exception {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setProductName(""); // Invalid empty product name

        mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetAllOrders_Success() throws Exception {
        List<OrderDTO> orders = Arrays.asList(sampleOrderDTO());
        orders.get(0).setId(1L);

        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].productName").value("Laptop"));
    }

    @Test
    @WithMockUser
    void testGetAllOrders_EmptyList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    void testGetOrderById_Success() throws Exception {
        OrderDTO orderDTO = sampleOrderDTO();
        orderDTO.setId(1L);

        when(orderService.getOrderById(1L)).thenReturn(Optional.of(orderDTO));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Laptop"));
    }

    @Test
    @WithMockUser
    void testGetOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/orders/1")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testUpdateOrder_Success() throws Exception {
        OrderDTO orderDTO = sampleOrderDTO();
        OrderDTO updatedOrder = new OrderDTO();
        updatedOrder.setId(1L);
        updatedOrder.setProductName("Updated Laptop");
        updatedOrder.setQuantity(3);

        when(orderService.updateOrder(eq(1L), any(OrderDTO.class))).thenReturn(updatedOrder);

        mockMvc.perform(
                        put("/orders/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Updated Laptop"))
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    @WithMockUser
    void testUpdateOrder_NotFound() throws Exception {
        OrderDTO orderDTO = sampleOrderDTO();

        when(orderService.updateOrder(eq(1L), any(OrderDTO.class))).thenReturn(null);

        mockMvc.perform(
                        put("/orders/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testDeleteOrder_Success() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/orders/1")).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testDeleteOrder_NotFound() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/orders/1")).andExpect(status().isNoContent());
    }
}
