package com.tech.test.serviceImpl;

import com.tech.test.dto.OrderDTO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements com.tech.test.service.EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmation(OrderDTO orderDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("customer@example.com"); // In real app, get from order or user
        message.setSubject("Order Confirmation");
        message.setText("Your order for " + orderDTO.getProductName() + " has been received. Quantity: " + orderDTO.getQuantity());
        mailSender.send(message);
    }
}
