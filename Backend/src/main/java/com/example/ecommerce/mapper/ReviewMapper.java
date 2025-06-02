package com.example.ecommerce.mapper;


import com.example.ecommerce.dto.ReviewDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class ReviewMapper {

    private final ProductRepository productRepository;

    private final UserRepository userRepository;


    public Review toEntity(ReviewDto dto) {
        return Review.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .product(productRepository.findById(dto.getProductId()).get())
                .user(userRepository.findById(dto.getUserId()).get())
                .build();
    }

    public ReviewDto toResponseDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .userId(review.getUser().getId())
                .userNom(review.getUser().getNom()+review.getUser().getNom())
                .userPrenom(review.getUser().getPrenom()+review.getUser().getPrenom())
                .productId(review.getProduct().getId())
                .build();
    }

}
