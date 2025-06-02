package com.example.ecommerce.service;


import com.example.ecommerce.dto.WishListDto;
import com.example.ecommerce.entity.Wishlist;
import com.example.ecommerce.mapper.WishListMapper;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.repository.WishlistRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WishListMapper wishlistMapper;


    public void addToWishlist(WishListDto dto) {


        Wishlist wishlist = wishlistMapper.toEntity(dto);
          wishlistRepository.save(wishlist);

    }





    public void removeFromWishlist(int id) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist item not found with id: " + id));
        wishlistRepository.delete(wishlist);
    }



    public List<WishListDto> getUserWishlist(Integer userId) {
        return wishlistRepository.findByUserId(userId).stream()
                .map(wishlistMapper::toDto)
                .collect(Collectors.toList());
    }
}
