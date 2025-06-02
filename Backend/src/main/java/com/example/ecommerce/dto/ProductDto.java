package com.example.ecommerce.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private Integer id;

    private String title;

    private String description;

    private double price;

    private double discountPercentage;

    private double rating;

    private int stock;

    private List<String> size;  // Liste de tailles sous forme de String

    private List<String> weight;  // Liste de poids sous forme de String

    private Integer categoryId;

    private CategoryDto categoryDto;

    private List<ImageDto> images;

    private Integer sellerId;

    private String sellerName;

    private String sellerTelephone;
}
