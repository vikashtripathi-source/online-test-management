package com.tech.test.serviceImpl;

import com.tech.test.dto.CartItemResponse;
import com.tech.test.entity.Cart;
import com.tech.test.entity.Product;
import com.tech.test.repository.CartRepository;
import com.tech.test.repository.ProductRepository;
import com.tech.test.service.CartService;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    public CartItemResponse addToCart(Long studentId, Long productId, Integer quantity) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Product not found with id: " + productId));

        if (!product.isActive()) {
            throw new RuntimeException("Product is not active: " + productId);
        }

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + productId);
        }

        Cart existingCart =
                cartRepository.findByStudentIdAndProductId(studentId, productId).orElse(null);

        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            cartRepository.save(existingCart);
            return convertToCartItemResponse(existingCart, product);
        } else {
            Cart cart = new Cart();
            cart.setStudentId(studentId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            Cart savedCart = cartRepository.save(cart);
            return convertToCartItemResponse(savedCart, product);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponse> getStudentCart(Long studentId) {
        List<Cart> cartItems = cartRepository.findByStudentId(studentId);

        return cartItems.stream()
                .map(
                        cart -> {
                            Product product =
                                    productRepository
                                            .findById(cart.getProductId())
                                            .orElseThrow(
                                                    () ->
                                                            new RuntimeException(
                                                                    "Product not found: "
                                                                            + cart.getProductId()));
                            return convertToCartItemResponse(cart, product);
                        })
                .collect(Collectors.toList());
    }

    @Override
    public CartItemResponse updateCartItem(Long itemId, Integer quantity) {
        Cart cart =
                cartRepository
                        .findById(itemId)
                        .orElseThrow(() -> new RuntimeException("Cart item not found: " + itemId));

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Product product =
                productRepository
                        .findById(cart.getProductId())
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Product not found: " + cart.getProductId()));

        int quantityDifference = quantity - cart.getQuantity();
        
        if (quantityDifference > 0) {
            if (product.getStockQuantity() < quantityDifference) {
                throw new RuntimeException("Insufficient stock for product: " + cart.getProductId());
            }
        }

        product.setStockQuantity(product.getStockQuantity() - quantityDifference);
        productRepository.save(product);

        cart.setQuantity(quantity);
        Cart updatedCart = cartRepository.save(cart);
        return convertToCartItemResponse(updatedCart, product);
    }

    @Override
    public void removeCartItem(Long itemId) {
        Cart cart = cartRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + itemId));
        
        Product product = productRepository.findById(cart.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + cart.getProductId()));
        
        product.setStockQuantity(product.getStockQuantity() + cart.getQuantity());
        productRepository.save(product);
        
        cartRepository.deleteById(itemId);
    }

    @Override
    public void clearCart(Long studentId) {
        List<Cart> cartItems = cartRepository.findByStudentId(studentId);
        
        for (Cart cart : cartItems) {
            Product product = productRepository.findById(cart.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cart.getProductId()));
            
            product.setStockQuantity(product.getStockQuantity() + cart.getQuantity());
            productRepository.save(product);
        }
        
        cartRepository.deleteByStudentId(studentId);
    }

    private CartItemResponse convertToCartItemResponse(Cart cart, Product product) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cart.getId());
        response.setProductId(product.getId());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setPrice(BigDecimal.valueOf(product.getPrice()));
        response.setQuantity(cart.getQuantity());
        response.setImageUrl(product.getImageUrl());
        return response;
    }
}
