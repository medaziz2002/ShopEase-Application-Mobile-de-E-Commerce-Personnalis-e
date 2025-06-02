package com.example.ecommerce.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Integer id;
    private Integer productId;
    private Integer userId;
    private ProductDto productDto;
    private UserRequest userRequest;
    private Integer quantity;
    private BigDecimal totalPrice;
    private List<String> size;
    private List<String> weight;

    public CartDto(Integer id, Integer productId, Integer userId, Integer quantity, List<String> size, List<String> weight) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.quantity = quantity;
        this.size = size;
        this.weight = weight;
    }

}
