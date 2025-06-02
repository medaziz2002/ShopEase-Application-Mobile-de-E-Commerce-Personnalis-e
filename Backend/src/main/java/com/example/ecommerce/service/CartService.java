package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Transactional
    public void addToCart(Integer productId, Integer userId, Integer quantity,
                          List<String> sizes, List<String> weights) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Cart newCart = Cart.builder()
                .product(product)
                .user(User.builder().id(userId).build())
                .quantity(quantity)
                .size(sizes != null ? new ArrayList<>(sizes) : new ArrayList<>())
                .weight(weights != null ? new ArrayList<>(weights) : new ArrayList<>())
                .build();

        cartRepository.save(newCart);
    }

    @Transactional(readOnly = true)
    public List<CartDto> getCartItems(Integer userId) {
        return cartRepository.findByUserId(userId).stream()
                .map(cartMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateQuantity(Integer cartId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cart.setQuantity(quantity);
        cartRepository.save(cart);
    }

    @Transactional
    public void removeFromCart(Integer cartId) {
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(Integer userId) {
        cartRepository.deleteAllByUserId(userId);
    }

    public boolean isProductInUserCart(int productId, int userId) {
        return cartRepository.existsByProductIdAndUserId(productId, userId);
    }


    @Transactional
    public void updateCartOptions(Integer cartId, List<String> sizes, List<String> weights) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        boolean modified = false;

        if (sizes != null && !sizes.isEmpty()) {
            // Always create a new mutable ArrayList from existing sizes
            List<String> existingSizes = new ArrayList<>(cart.getSize() != null ? cart.getSize() : new ArrayList<>());
            existingSizes.addAll(sizes);
            cart.setSize(existingSizes);
            modified = true;
        }

        if (weights != null && !weights.isEmpty()) {
            // Always create a new mutable ArrayList from existing weights
            List<String> existingWeights = new ArrayList<>(cart.getWeight() != null ? cart.getWeight() : new ArrayList<>());
            existingWeights.addAll(weights);
            cart.setWeight(existingWeights);
            modified = true;
        }

        // Only save if modifications were made
        if (modified) {
            cartRepository.save(cart);
        }
    }


}