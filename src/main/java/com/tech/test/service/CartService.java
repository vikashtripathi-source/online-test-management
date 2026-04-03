package com.tech.test.service;

import com.tech.test.dto.CartItemResponse;
import java.util.List;

public interface CartService {

    CartItemResponse addToCart(Long studentId, Long productId, Integer quantity);

    List<CartItemResponse> getStudentCart(Long studentId);

    CartItemResponse updateCartItem(Long itemId, Integer quantity);

    void removeCartItem(Long itemId);

    void clearCart(Long studentId);
}
