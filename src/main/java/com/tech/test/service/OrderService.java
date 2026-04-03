package com.tech.test.service;

import com.tech.test.dto.CartOrderRequest;
import com.tech.test.dto.OrderDTO;
import com.tech.test.enums.OrderStatus;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderDTO createOrder(OrderDTO orderDTO);

    List<OrderDTO> getAllOrders();

    Optional<OrderDTO> getOrderById(Long id);

    OrderDTO updateOrder(Long id, OrderDTO orderDTO);

    void deleteOrder(Long id);

    OrderDTO submitOrderWithAddress(OrderDTO orderDTO);

    OrderDTO submitOrderFromCart(CartOrderRequest cartOrderRequest);

    List<OrderDTO> getOrdersByStudent(Long studentId);

    OrderDTO updateOrderStatus(Long id, OrderStatus status);
}
