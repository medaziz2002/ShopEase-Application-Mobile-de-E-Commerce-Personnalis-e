package com.example.ecommerce.controller;

import com.example.ecommerce.dto.WishListDto;
import com.example.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public void addToWishlist(@RequestBody WishListDto dto) {
         wishlistService.addToWishlist(dto);
    }

    @DeleteMapping("/{id}")
    public void removeFromWishlist(@PathVariable Integer id) {
        wishlistService.removeFromWishlist(id);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WishListDto>> getUserWishlist(@PathVariable Integer userId) {
        return ResponseEntity.ok(wishlistService.getUserWishlist(userId));
    }
}
