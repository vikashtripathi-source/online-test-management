package com.tech.test.controller;

import com.tech.test.dto.AddressDTO;
import com.tech.test.dto.OrderDTO;
import com.tech.test.service.AddressService;
import com.tech.test.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(
        name = "Order Management API",
        description = "Operations related to Order, Order Submission and Order Records")
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AddressService addressService;

    @PostMapping
    @Operation(
            summary = "Create Order",
            description = "Create a new order with the provided order details")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Order successfully created"),
                @ApiResponse(responseCode = "400", description = "Invalid order data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO saved = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get All Orders", description = "Retrieve all orders from the system")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved all orders"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Order By Id",
            description = "Retrieve a specific order by its unique identifier")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the order"),
                @ApiResponse(responseCode = "404", description = "Order not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "ID of the order to retrieve") @PathVariable Long id) {
        return orderService
                .getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Order",
            description = "Update an existing order with new information")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Order successfully updated"),
                @ApiResponse(responseCode = "400", description = "Invalid order data"),
                @ApiResponse(responseCode = "404", description = "Order not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<OrderDTO> updateOrder(
            @Parameter(description = "ID of the order to update") @PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Order", description = "Delete an order from the system by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Order successfully deleted"),
                @ApiResponse(responseCode = "404", description = "Order not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "ID of the order to delete") @PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/submit")
    @Operation(
            summary = "Submit Order With Address",
            description = "Submit an order along with address information for processing")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Order successfully submitted with address"),
                @ApiResponse(responseCode = "400", description = "Invalid order or address data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<OrderDTO> submitOrderWithAddress(@Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.submitOrderWithAddress(orderDTO));
    }

    @GetMapping("/student/{studentId}")
    @Operation(
            summary = "Get Orders By Student",
            description = "Retrieve all orders associated with a specific student")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved student orders"),
                @ApiResponse(responseCode = "404", description = "Student not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<OrderDTO>> getOrdersByStudent(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {

        return ResponseEntity.ok(orderService.getOrdersByStudent(studentId));
    }

    @GetMapping("/student/{studentId}/addresses")
    @Operation(
            summary = "Get Student Addresses",
            description = "Retrieve all addresses associated with a specific student")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved student addresses"),
                @ApiResponse(responseCode = "404", description = "Student not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<AddressDTO>> getByStudent(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {

        return ResponseEntity.ok(addressService.getByStudent(studentId));
    }
}
