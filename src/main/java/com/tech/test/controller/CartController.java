package com.tech.test.controller;

import com.tech.test.dto.CartItemResponse;
import com.tech.test.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart Management API", description = "Operations related to shopping cart management")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @Operation(
            summary = "Add item to cart",
            description = "Add a product to the student's shopping cart")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Item successfully added to cart"),
                @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                @ApiResponse(responseCode = "404", description = "Product not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<CartItemResponse> addToCart(
            @Parameter(description = "Student ID") @RequestParam @NotNull Long studentId,
            @Parameter(description = "Product ID") @RequestParam @NotNull Long productId,
            @Parameter(description = "Quantity") @RequestParam @Min(1) Integer quantity) {

        CartItemResponse response = cartService.addToCart(studentId, productId, quantity);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{studentId}")
    @Operation(
            summary = "Get student's cart",
            description = "Retrieve all items in the student's shopping cart")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Cart items retrieved successfully"),
                @ApiResponse(responseCode = "404", description = "Student not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<CartItemResponse>> getStudentCart(
            @Parameter(description = "Student ID") @PathVariable Long studentId) {

        List<CartItemResponse> cartItems = cartService.getStudentCart(studentId);
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("/update")
    @Operation(
            summary = "Update cart item quantity",
            description = "Update the quantity of an existing item in the cart")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Cart item updated successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                @ApiResponse(responseCode = "404", description = "Cart item not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<CartItemResponse> updateCartItem(
            @Parameter(description = "Cart item ID") @RequestParam @NotNull Long itemId,
            @Parameter(description = "New quantity") @RequestParam @Min(1) Integer quantity) {

        CartItemResponse response = cartService.updateCartItem(itemId, quantity);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Remove cart item", description = "Remove a specific item from the cart")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Cart item removed successfully"),
                @ApiResponse(responseCode = "404", description = "Cart item not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<Void> removeCartItem(
            @Parameter(description = "Cart item ID") @PathVariable Long itemId) {

        cartService.removeCartItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{studentId}")
    @Operation(summary = "Clear cart", description = "Remove all items from the student's cart")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Cart cleared successfully"),
                @ApiResponse(responseCode = "404", description = "Student not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<Void> clearCart(
            @Parameter(description = "Student ID") @PathVariable Long studentId) {

        cartService.clearCart(studentId);
        return ResponseEntity.noContent().build();
    }
}
