package com.tech.test.service;

import com.tech.test.dto.OrderDTO;

public interface EmailService {

    void sendOrderConfirmation(OrderDTO orderDTO);
}
