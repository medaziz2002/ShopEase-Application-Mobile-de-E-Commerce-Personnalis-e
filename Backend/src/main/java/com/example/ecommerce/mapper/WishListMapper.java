package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.WishListDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Wishlist;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WishListMapper {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Wishlist toEntity(WishListDto dto) {
        if (dto == null) {
            return null;
        }

        // Find product by ID and throw EntityNotFoundException if not found
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + dto.getProductId()));

        // Find user by ID and throw EntityNotFoundException if not found
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getUserId()));

        return Wishlist.builder()
                .product(product)
                .user(user)
                .build();
    }

    public WishListDto toDto(Wishlist entity) {
        if (entity == null) {
            return null;
        }

        WishListDto dto = new WishListDto();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProduct().getId());
        dto.setUserId(entity.getUser().getId());

        return dto;
    }
}