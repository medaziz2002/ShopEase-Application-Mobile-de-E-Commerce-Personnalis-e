package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderItemMapper {

    private final ProductMapper productMapper;

    public OrderItemDTO toDto(OrderItem orderItem) {

        List<String> sizes = Optional.ofNullable(orderItem.getSize())
                .orElse(Collections.emptyList());

        List<String> weights = Optional.ofNullable(orderItem.getWeight())
                .orElse(Collections.emptyList());
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productDto(productMapper.toDTO(orderItem.getProduct()))
                .quantity(orderItem.getQuantity())
                .size(sizes)
                .weight(weights)
                .unitPrice(orderItem.getUnitPrice())
                .discount(orderItem.getDiscount())
                .build();
    }

    public OrderItem toEntity(OrderItemDTO orderItemDTO) {
        return OrderItem.builder()
                .id(orderItemDTO.getId())
                .product(Product.builder().id(orderItemDTO.getProductId()).build())
                .quantity(orderItemDTO.getQuantity())
                .unitPrice(orderItemDTO.getUnitPrice())
                .size(orderItemDTO.getSize() != null ? new ArrayList<>(orderItemDTO.getSize()) : new ArrayList<>())
                .weight(orderItemDTO.getWeight() != null ? new ArrayList<>(orderItemDTO.getWeight()) : new ArrayList<>())
                .discount(orderItemDTO.getDiscount())
                .build();
    }
}