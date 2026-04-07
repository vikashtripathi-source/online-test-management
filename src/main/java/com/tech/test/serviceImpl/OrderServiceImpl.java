package com.tech.test.serviceImpl;

import com.tech.test.dto.CartItemResponse;
import com.tech.test.dto.CartOrderRequest;
import com.tech.test.dto.OrderDTO;
import com.tech.test.dto.OrderItemDTO;
import com.tech.test.entity.Order;
import com.tech.test.entity.OrderItem;
import com.tech.test.entity.Product;
import com.tech.test.enums.OrderStatus;
import com.tech.test.exception.InvalidDataException;
import com.tech.test.exception.KafkaException;
import com.tech.test.exception.OrderException;
import com.tech.test.mapper.OrderMapper;
import com.tech.test.repository.OrderRepository;
import com.tech.test.repository.ProductRepository;
import com.tech.test.service.CartService;
import com.tech.test.service.KafkaProducerService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements com.tech.test.service.OrderService {

    @Autowired private OrderRepository repository;

    @Autowired private OrderMapper orderMapper;

    @Autowired private KafkaProducerService producer;

    @Autowired private CartService cartService;

    @Autowired private ProductRepository productRepository;

    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            if (orderDTO == null) {
                throw new InvalidDataException("Order DTO cannot be null");
            }

            // Handle new orderItems structure
            if (orderDTO.getOrderItems() != null && !orderDTO.getOrderItems().isEmpty()) {
                return createOrderWithItems(orderDTO);
            }

            // Handle legacy structure for backward compatibility
            if (orderDTO.getProductName() == null || orderDTO.getProductName().trim().isEmpty()) {
                throw new InvalidDataException("Product name cannot be null or empty");
            }
            if (orderDTO.getQuantity() <= 0) {
                throw new InvalidDataException("Quantity must be a positive number");
            }

            Order order = orderMapper.toEntity(orderDTO);
            order.setCreatedDate(LocalDateTime.now()); // Set creation date
            Order savedOrder = repository.save(order);

            try {
                producer.sendOrder(savedOrder);
            } catch (Exception e) {
                throw new KafkaException(
                        "Failed to send order event to Kafka: " + e.getMessage(), e);
            }

            return createSimpleDTO(savedOrder);
        } catch (InvalidDataException | KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to create order: " + e.getMessage(), e);
        }
    }

    public List<OrderDTO> getAllOrders() {
        try {
            return repository.findAll().stream()
                    .map(this::createSimpleDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new OrderException("Failed to retrieve all orders: " + e.getMessage(), e);
        }
    }

    public Optional<OrderDTO> getOrderById(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Order ID must be a positive number");
            }
            return repository.findById(id).map(orderMapper::toDTO);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException(
                    "Failed to retrieve order with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Order ID must be a positive number");
            }
            if (orderDTO == null) {
                throw new InvalidDataException("Order DTO cannot be null");
            }

            Optional<Order> optionalOrder = repository.findById(id);
            if (optionalOrder.isPresent()) {
                Order updatedOrder = orderMapper.toEntity(orderDTO);
                updatedOrder.setId(id);
                Order saved = repository.save(updatedOrder);

                try {
                    producer.sendOrderUpdated(saved);
                } catch (Exception e) {
                    throw new KafkaException(
                            "Failed to send order update event to Kafka: " + e.getMessage(), e);
                }

                return orderMapper.toDTO(saved);
            } else {
                throw new OrderException("Order not found with ID: " + id);
            }
        } catch (OrderException | InvalidDataException | KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException(
                    "Failed to update order with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public void deleteOrder(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Order ID must be a positive number");
            }
            if (!repository.existsById(id)) {
                throw new OrderException("Order not found with ID: " + id);
            }
            repository.deleteById(id);

            try {
                producer.sendOrderDeleted(id);
            } catch (Exception e) {
                throw new KafkaException(
                        "Failed to send order delete event to Kafka: " + e.getMessage(), e);
            }
        } catch (OrderException | InvalidDataException | KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException(
                    "Failed to delete order with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public OrderDTO submitOrderWithAddress(OrderDTO orderDTO) {
        try {
            if (orderDTO == null) {
                throw new InvalidDataException("Order DTO cannot be null");
            }
            return createOrder(orderDTO);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to submit order with address: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public OrderDTO submitOrderFromCart(CartOrderRequest cartOrderRequest) {
        try {
            if (cartOrderRequest == null) {
                throw new InvalidDataException("Cart order request cannot be null");
            }

            List<CartItemResponse> cartItems =
                    cartService.getStudentCart(cartOrderRequest.getStudentId());

            if (cartItems.isEmpty()) {
                throw new OrderException(
                        "Cart is empty for student: " + cartOrderRequest.getStudentId());
            }

            BigDecimal totalAmount =
                    cartItems.stream()
                            .map(
                                    item ->
                                            item.getPrice()
                                                    .multiply(
                                                            BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            Order order = new Order();
            order.setStudentId(cartOrderRequest.getStudentId());
            order.setAddressId(cartOrderRequest.getAddressId());
            order.setTotalAmount(totalAmount);
            order.setStatus(OrderStatus.PENDING);
            order.setOrderDate(LocalDateTime.now());
            order.setCreatedDate(LocalDateTime.now());

            List<OrderItem> orderItems =
                    cartItems.stream()
                            .map(
                                    cartItem -> {
                                        Product product =
                                                productRepository
                                                        .findById(cartItem.getProductId())
                                                        .orElseThrow(
                                                                () ->
                                                                        new OrderException(
                                                                                "Product not found: "
                                                                                        + cartItem
                                                                                                .getProductId()));

                                        if (product.getStockQuantity() < cartItem.getQuantity()) {
                                            throw new OrderException(
                                                    "Insufficient stock for product: "
                                                            + cartItem.getProductId());
                                        }

                                        product.setStockQuantity(
                                                product.getStockQuantity()
                                                        - cartItem.getQuantity());
                                        productRepository.save(product);

                                        OrderItem orderItem = new OrderItem();
                                        orderItem.setOrder(order);
                                        orderItem.setProductId(cartItem.getProductId());
                                        orderItem.setQuantity(cartItem.getQuantity());
                                        orderItem.setPrice(cartItem.getPrice());
                                        return orderItem;
                                    })
                            .collect(Collectors.toList());

            order.setOrderItems(orderItems);
            Order savedOrder = repository.save(order);

            cartService.clearCart(cartOrderRequest.getStudentId());

            try {
                producer.sendOrder(savedOrder);
            } catch (Exception e) {
                throw new KafkaException(
                        "Failed to send order event to Kafka: " + e.getMessage(), e);
            }

            // Temporarily bypass mapper to avoid circular reference
            OrderDTO dto = new OrderDTO();
            dto.setId(savedOrder.getId());
            dto.setStudentId(savedOrder.getStudentId());
            dto.setTotalAmount(savedOrder.getTotalAmount());
            dto.setStatus(savedOrder.getStatus());
            dto.setOrderDate(savedOrder.getOrderDate());
            dto.setAddressId(savedOrder.getAddressId());
            dto.setCreatedDate(savedOrder.getCreatedDate());
            return dto;
        } catch (InvalidDataException | OrderException | KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Failed to submit order from cart: " + e.getMessage(), e);
        }
    }

    @Override
    public List<OrderDTO> getOrdersByStudent(Long studentId) {

        List<Order> orders = repository.findByStudentId(studentId);

        return orders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private OrderDTO mapToDTO(Order order) {

        OrderDTO dto = new OrderDTO();

        dto.setId(order.getId());
        dto.setProductName(order.getProductName());
        dto.setQuantity(order.getQuantity());
        dto.setAddress(order.getAddress());
        dto.setCity(order.getCity());
        dto.setZipCode(order.getZipCode());
        dto.setCreatedDate(order.getCreatedDate());
        dto.setStudentId(order.getStudentId());

        return dto;
    }

    @Override
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("Order ID must be a positive number");
            }
            if (status == null) {
                throw new InvalidDataException("Order status cannot be null");
            }

            Optional<Order> optionalOrder = repository.findById(id);
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();
                order.setStatus(status);
                Order saved = repository.save(order);

                try {
                    producer.sendOrderUpdated(saved);
                } catch (Exception e) {
                    throw new KafkaException(
                            "Failed to send order status update event to Kafka: " + e.getMessage(),
                            e);
                }

                return orderMapper.toDTO(saved);
            } else {
                throw new OrderException("Order not found with ID: " + id);
            }
        } catch (InvalidDataException | OrderException | KafkaException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException(
                    "Failed to update order status for ID " + id + ": " + e.getMessage(), e);
        }
    }

    private OrderDTO createOrderWithItems(OrderDTO orderDTO) {
        Order order = new Order();
        order.setStudentId(orderDTO.getStudentId());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setStatus(orderDTO.getStatus());
        order.setOrderDate(orderDTO.getOrderDate());
        order.setAddressId(orderDTO.getAddressId());
        order.setCreatedDate(LocalDateTime.now());

        // Process order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
            Product product =
                    productRepository
                            .findById(itemDTO.getProductId())
                            .orElseThrow(
                                    () ->
                                            new OrderException(
                                                    "Product not found: "
                                                            + itemDTO.getProductId()));

            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                throw new OrderException(
                        "Insufficient stock for product: " + itemDTO.getProductId());
            }

            // Update stock
            product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(itemDTO.getProductId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(itemDTO.getPrice());
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        Order savedOrder = repository.save(order);

        try {
            producer.sendOrder(savedOrder);
        } catch (Exception e) {
            throw new KafkaException("Failed to send order event to Kafka: " + e.getMessage(), e);
        }

        return createSimpleDTO(savedOrder);
    }

    private OrderDTO createSimpleDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStudentId(order.getStudentId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setAddressId(order.getAddressId());
        dto.setCreatedDate(order.getCreatedDate());
        return dto;
    }
}
