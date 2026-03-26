package com.tech.test.controller;

import com.tech.test.dto.AddressDTO;
import com.tech.test.dto.OrderDTO;
import com.tech.test.service.AddressService;
import com.tech.test.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Order Management API", description = "Operations related to Order, Order Submission and Order Records")
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AddressService addressService;


    @PostMapping
    @Operation(summary = "Create Order")
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO saved = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get All Orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Order By Id")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Order")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id,
                                             @Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Order")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit Order With Address")
    public ResponseEntity<OrderDTO> submitOrderWithAddress(@Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.submitOrderWithAddress(orderDTO));
    }
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStudent(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(orderService.getOrdersByStudent(studentId));
    }

    @GetMapping("/student/{studentId}/addresses")
    public ResponseEntity<List<AddressDTO>> getByStudent(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(addressService.getByStudent(studentId));
    }
}
