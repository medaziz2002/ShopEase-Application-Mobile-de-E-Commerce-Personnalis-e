package com.example.ecommerce.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private Integer id;
    private Float rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer productId;
    private Integer userId;
    private String userNom;
    private String userPrenom;
    private String productTitle;
    private Double productRating;
}