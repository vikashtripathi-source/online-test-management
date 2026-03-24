package com.tech.test.serviceImpl;

import com.tech.test.entity.Order;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements com.tech.test.service.EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmation(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("customer@example.com"); // In real app, get from order or user
        message.setSubject("Order Confirmation");
        message.setText("Your order for " + order.getProductName() + " has been received. Quantity: " + order.getQuantity());
        mailSender.send(message);
    }
}
