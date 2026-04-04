package com.tech.test.controller;

import com.tech.test.dto.ProductDTO;
import com.tech.test.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users Product API", description = "Product endpoints accessible under /users path")
public class UsersController {

    private final ProductService productService;

    @GetMapping("/products")
    @Operation(
            summary = "Get All Products for Users",
            description = "Retrieve all products accessible to users")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<ProductDTO>> getProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/products/{id}")
    @Operation(
            summary = "Get Product By ID for Users",
            description = "Retrieve a specific product by ID for users")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
                @ApiResponse(responseCode = "404", description = "Product not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }
}
