package com.tech.test.service;

import com.tech.test.entity.Order;

public interface EmailService {

    void sendOrderConfirmation(Order order);
}
