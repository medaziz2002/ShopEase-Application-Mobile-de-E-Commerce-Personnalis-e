package com.example.ecommerce.mapper;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    public CartDto toDto(Cart cart) {

        List<String> sizes = Optional.ofNullable(cart.getSize())
                .orElse(Collections.emptyList());

        List<String> weights = Optional.ofNullable(cart.getWeight())
                .orElse(Collections.emptyList());
        return CartDto.builder()
                .id(cart.getId())
                .productId(cart.getProduct().getId())
                .productDto(productMapper.toDTO(cart.getProduct()))
                .userRequest(userMapper.toDto(cart.getUser()))
                .size(sizes)
                .weight(weights)
                .quantity(cart.getQuantity())
                .totalPrice(calculateTotalPrice(cart))
                .build();
    }

    public Cart toEntity(CartDto cartDto) {
        return Cart.builder()
                .id(cartDto.getId())
                .size(cartDto.getSize() != null ? new ArrayList<>(cartDto.getSize()) : new ArrayList<>())
                .weight(cartDto.getWeight() != null ? new ArrayList<>(cartDto.getWeight()) : new ArrayList<>())
                .product(Product.builder().id(cartDto.getProductId()).build())
                .user(User.builder().id(cartDto.getUserId()).build())
                .quantity(cartDto.getQuantity())
                .build();
    }

    private BigDecimal calculateTotalPrice(Cart cart) {
        double price = cart.getProduct().getPrice();
        double discount = cart.getProduct().getDiscountPercentage();
        double discountedPrice = price * (1 - discount / 100);
        return BigDecimal.valueOf(discountedPrice * cart.getQuantity());
    }
}