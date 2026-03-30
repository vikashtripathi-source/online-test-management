package com.tech.test.controller;

import com.tech.test.dto.ProductDTO;
import com.tech.test.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(
        name = "Product Management API",
        description = "Operations related to Product management and catalog")
public class ProductController {

    private final ProductService service;

    @PostMapping
    @Operation(
            summary = "Create Product",
            description = "Create a new product with the provided product details")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Product successfully created"),
                @ApiResponse(responseCode = "400", description = "Invalid product data"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "Get All Products",
            description = "Retrieve all products from the product catalog")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved all products"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<ProductDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Product By Id",
            description = "Retrieve a specific product by its unique identifier")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the product"),
                @ApiResponse(responseCode = "404", description = "Product not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<ProductDTO> getById(
            @Parameter(description = "ID of the product to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/branch/{branch}")
    @Operation(
            summary = "Get Products By Branch",
            description = "Retrieve all products associated with a specific branch")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved products by branch"),
                @ApiResponse(responseCode = "400", description = "Invalid branch parameter"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<ProductDTO>> getByBranch(
            @Parameter(description = "Branch to filter products") @PathVariable String branch) {
        return ResponseEntity.ok(service.getByBranch(branch));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Product",
            description = "Update an existing product with new information")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Product successfully updated"),
                @ApiResponse(responseCode = "400", description = "Invalid product data"),
                @ApiResponse(responseCode = "404", description = "Product not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<ProductDTO> update(
            @Parameter(description = "ID of the product to update") @PathVariable Long id,
            @RequestBody ProductDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Product",
            description = "Delete a product from the system by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
                @ApiResponse(responseCode = "404", description = "Product not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the product to delete") @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inventory")
    @Operation(
            summary = "Get Inventory Status",
            description = "Retrieve inventory status of all products including stock levels")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved inventory status"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<ProductDTO>> getInventoryStatus() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/inventory/low-stock")
    @Operation(
            summary = "Get Low Stock Products",
            description = "Retrieve products with low stock levels")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved low stock products"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}/stock")
    @Operation(
            summary = "Update Stock Quantity",
            description = "Update the stock quantity for a specific product")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Stock quantity successfully updated"),
                @ApiResponse(responseCode = "400", description = "Invalid stock quantity"),
                @ApiResponse(responseCode = "404", description = "Product not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<ProductDTO> updateStock(
            @Parameter(description = "ID of the product") @PathVariable Long id,
            @Parameter(description = "New stock quantity") @RequestParam int stockQuantity) {
        ProductDTO product = service.getById(id);
        product.setStockQuantity(stockQuantity);
        return ResponseEntity.ok(service.update(id, product));
    }

    @PostMapping("/{id}/stock/increase")
    @Operation(
            summary = "Increase Stock",
            description = "Increase stock quantity for a specific product")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Stock successfully increased"),
                @ApiResponse(responseCode = "400", description = "Invalid quantity"),
                @ApiResponse(responseCode = "404", description = "Product not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<ProductDTO> increaseStock(
            @Parameter(description = "ID of the product") @PathVariable Long id,
            @Parameter(description = "Quantity to add") @RequestParam int quantity) {
        ProductDTO product = service.getById(id);
        product.setStockQuantity(product.getStockQuantity() + quantity);
        return ResponseEntity.ok(service.update(id, product));
    }

    @PostMapping("/{id}/stock/decrease")
    @Operation(
            summary = "Decrease Stock",
            description = "Decrease stock quantity for a specific product")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Stock successfully decreased"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Insufficient stock or invalid quantity"),
                @ApiResponse(responseCode = "404", description = "Product not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<ProductDTO> decreaseStock(
            @Parameter(description = "ID of the product") @PathVariable Long id,
            @Parameter(description = "Quantity to subtract") @RequestParam int quantity) {
        ProductDTO product = service.getById(id);
        if (product.getStockQuantity() < quantity) {
            return ResponseEntity.badRequest().build();
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        return ResponseEntity.ok(service.update(id, product));
    }

    @GetMapping("/inventory/branch/{branch}")
    @Operation(
            summary = "Get Branch Inventory",
            description = "Retrieve inventory status for products in a specific branch")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved branch inventory"),
                @ApiResponse(responseCode = "400", description = "Invalid branch parameter"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<ProductDTO>> getBranchInventory(
            @Parameter(description = "Branch to filter inventory") @PathVariable String branch) {
        return ResponseEntity.ok(service.getByBranch(branch));
    }
}
