package com.example.ecommerce.service.integration;

import com.example.ecommerce.dto.WishListDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Wishlist;
import com.example.ecommerce.mapper.WishListMapper;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.repository.WishlistRepository;
import com.example.ecommerce.service.WishlistService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class WishlistServiceIntegrationTest {

    @Autowired private WishlistService wishlistService;
    @Autowired private WishlistRepository wishlistRepository;
    @Autowired private WishListMapper wishlistMapper;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private Product testProduct1;
    private Product testProduct2;
    private WishListDto testWishlistDto;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        wishlistRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testUser1 = User.builder()
                .nom("John")
                .prenom("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .telephone("1234567890")
                .role("USER")
                .build();
        testUser1 = userRepository.save(testUser1);

        testUser2 = User.builder()
                .nom("Jane")
                .prenom("Smith")
                .email("jane.smith@example.com")
                .password("password456")
                .telephone("0987654321")
                .role("USER")
                .build();
        testUser2 = userRepository.save(testUser2);

        // Create test products
        testProduct1 = Product.builder()
                .title("Test Product 1")
                .description("Description 1")
                .price(100.0)
                .stock(10)
                .rating(4.5)
                .discountPercentage(0)
                .seller(testUser1) // Use testUser1 as seller
                .build();
        testProduct1 = productRepository.save(testProduct1);

        testProduct2 = Product.builder()
                .title("Test Product 2")
                .description("Description 2")
                .price(200.0)
                .stock(5)
                .rating(4.0)
                .discountPercentage(10)
                .seller(testUser1) // Use testUser1 as seller
                .build();
        testProduct2 = productRepository.save(testProduct2);

        // Create test DTO with actual persisted entity IDs
        testWishlistDto = new WishListDto();
        testWishlistDto.setUserId(testUser1.getId());
        testWishlistDto.setProductId(testProduct1.getId());
    }

    @Test
    void addToWishlist_ShouldPersistWishlistItem() {
        // when
        wishlistService.addToWishlist(testWishlistDto);

        // then
        List<Wishlist> allItems = wishlistRepository.findAll();
        assertThat(allItems).hasSize(1);
        assertThat(allItems.get(0).getUser().getId()).isEqualTo(testWishlistDto.getUserId());
        assertThat(allItems.get(0).getProduct().getId()).isEqualTo(testWishlistDto.getProductId());
    }

    @Test
    void removeFromWishlist_ShouldDeleteItem() {
        // given - add item first
        wishlistService.addToWishlist(testWishlistDto);
        List<Wishlist> items = wishlistRepository.findAll();
        assertThat(items).hasSize(1);
        int wishlistItemId = items.get(0).getId();

        // when
        wishlistService.removeFromWishlist(wishlistItemId);

        // then
        assertThat(wishlistRepository.findById(wishlistItemId)).isEmpty();
    }

    @Test
    void getUserWishlist_ShouldReturnOnlyUserItems() {
        // given - create wishlist items for both users
        wishlistService.addToWishlist(testWishlistDto);

        WishListDto secondUserItem = new WishListDto();
        secondUserItem.setUserId(testUser2.getId());
        secondUserItem.setProductId(testProduct2.getId());
        wishlistService.addToWishlist(secondUserItem);

        // when
        List<WishListDto> user1Wishlist = wishlistService.getUserWishlist(testUser1.getId());
        List<WishListDto> user2Wishlist = wishlistService.getUserWishlist(testUser2.getId());

        // then
        assertThat(user1Wishlist).hasSize(1);
        assertThat(user1Wishlist.get(0).getUserId()).isEqualTo(testUser1.getId());
        assertThat(user1Wishlist.get(0).getProductId()).isEqualTo(testProduct1.getId());

        assertThat(user2Wishlist).hasSize(1);
        assertThat(user2Wishlist.get(0).getUserId()).isEqualTo(testUser2.getId());
        assertThat(user2Wishlist.get(0).getProductId()).isEqualTo(testProduct2.getId());
    }

    @Test
    void removeFromWishlist_WhenItemNotFound_ShouldThrowException() {
        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            wishlistService.removeFromWishlist(999);
        });
    }

    @Test
    void addToWishlist_WithNonExistentUser_ShouldThrowException() {
        // given
        WishListDto invalidDto = new WishListDto();
        invalidDto.setUserId(999); // Non-existent user
        invalidDto.setProductId(testProduct1.getId());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            wishlistService.addToWishlist(invalidDto);
        });
    }

    @Test
    void addToWishlist_WithNonExistentProduct_ShouldThrowException() {
        // given
        WishListDto invalidDto = new WishListDto();
        invalidDto.setUserId(testUser1.getId());
        invalidDto.setProductId(999); // Non-existent product

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            wishlistService.addToWishlist(invalidDto);
        });
    }
}