package com.tech.test.service;

import com.tech.test.dto.OrderDTO;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderDTO createOrder(OrderDTO orderDTO);

    List<OrderDTO> getAllOrders();

    Optional<OrderDTO> getOrderById(Long id);

    OrderDTO updateOrder(Long id, OrderDTO orderDTO);

    void deleteOrder(Long id);

    OrderDTO submitOrderWithAddress(OrderDTO orderDTO);

    List<OrderDTO> getOrdersByStudent(Long studentId);
}
