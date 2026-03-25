package com.tech.test.serviceImpl;

import com.tech.test.dto.OrderDTO;
import com.tech.test.exception.BusinessLogicException;
import com.tech.test.exception.InvalidDataException;
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
        try {
            if (orderDTO == null) {
                throw new InvalidDataException("Order DTO cannot be null");
            }
            if (orderDTO.getProductName() == null || orderDTO.getProductName().trim().isEmpty()) {
                throw new InvalidDataException("Product name cannot be null or empty");
            }
            if (orderDTO.getQuantity() <= 0) {
                throw new InvalidDataException("Quantity must be a positive number");
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("customer@example.com");
            message.setSubject("Order Confirmation");
            message.setText("Your order for " + orderDTO.getProductName() + " has been received. Quantity: " + orderDTO.getQuantity());
            
            if (mailSender == null) {
                throw new BusinessLogicException("Mail sender is not properly configured");
            }
            
            mailSender.send(message);
        } catch (InvalidDataException e) {
            throw e;
        } catch (BusinessLogicException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessLogicException("Failed to send order confirmation email: " + e.getMessage(), e);
        }
    }
}
