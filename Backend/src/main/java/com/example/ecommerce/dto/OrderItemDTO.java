package com.example.ecommerce.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Integer id;
    private Integer productId;
    private ProductDto productDto;
    private int quantity;
    private double unitPrice;
    private double discount;
    private List<String> size;  // Liste de tailles sous forme de String

    private List<String> weight;  // Liste de poids sous forme de String
}