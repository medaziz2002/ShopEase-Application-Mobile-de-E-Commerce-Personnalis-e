package com.example.ecommerce.controller.integration;

import com.example.ecommerce.dto.WishListDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Wishlist;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.repository.WishlistRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class WishlistControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private WishListDto testWishlistDto;
    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        wishlistRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setNom("testuser");
        testUser.setPrenom("Test");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setTelephone("1234567890");
        // Set other required fields based on your User entity
        testUser = userRepository.save(testUser);

        // Create test product
        testProduct = new Product();
        testProduct.setTitle("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(99.99);
        testProduct.setStock(10);
        testProduct.setRating(4.5f);
        testProduct.setDiscountPercentage(0.0f);
        // Set other required fields like categoryId, sellerId based on your Product entity
        // testProduct.setCategoryId(1L);
        // testProduct.setSellerId(1L);
        testProduct = productRepository.save(testProduct);

        // Create test DTO with actual saved entity IDs
        testWishlistDto = new WishListDto();
        testWishlistDto.setUserId(testUser.getId());
        testWishlistDto.setProductId(testProduct.getId());
    }

    @Test
    void addToWishlist_ShouldReturnCreatedStatus() throws Exception {
        mockMvc.perform(post("/api/v1/wishlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWishlistDto)))
                .andExpect(status().isOk());

        // Verify the wishlist item was actually created
        assertThat(wishlistRepository.count()).isEqualTo(1);
    }

    @Test
    void getWishlist_ShouldReturnUserWishlist() throws Exception {
        // Given - add item to wishlist first
        mockMvc.perform(post("/api/v1/wishlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWishlistDto)))
                .andExpect(status().isOk());

        // When & Then - retrieve wishlist
        mockMvc.perform(get("/api/v1/wishlist/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(testUser.getId()));
    }

    @Test
    void deleteWishlistItem_ShouldReturnOk() throws Exception {
        // Given - create wishlist item directly in database
        Wishlist item = Wishlist.builder()
                .user(testUser)
                .product(testProduct)
                .build();
        Wishlist savedItem = wishlistRepository.save(item);

        // When & Then - delete the item
        mockMvc.perform(delete("/api/v1/wishlist/{id}", savedItem.getId()))
                .andExpect(status().isOk());

        // Verify the item was actually deleted
        assertThat(wishlistRepository.findById(savedItem.getId())).isEmpty();
        assertThat(wishlistRepository.count()).isEqualTo(0);
    }

    @Test
    void addToWishlist_WithNonExistentProduct_ShouldReturn404() throws Exception {
        WishListDto invalidDto = new WishListDto();
        invalidDto.setUserId(testUser.getId());
        invalidDto.setProductId(999); // Non-existent product ID

        // With GlobalExceptionHandler, EntityNotFoundException should return 404
        mockMvc.perform(post("/api/v1/wishlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"));

        // Verify no item was added to wishlist
        assertThat(wishlistRepository.count()).isEqualTo(0);
    }

    @Test
    void addToWishlist_WithNonExistentUser_ShouldReturn404() throws Exception {
        WishListDto invalidDto = new WishListDto();
        invalidDto.setUserId(999); // Non-existent user ID
        invalidDto.setProductId(testProduct.getId());

        // With GlobalExceptionHandler, EntityNotFoundException should return 404
        mockMvc.perform(post("/api/v1/wishlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));

        // Verify no item was added to wishlist
        assertThat(wishlistRepository.count()).isEqualTo(0);
    }

    @Test
    void getWishlist_ForUserWithNoItems_ShouldReturnEmptyList() throws Exception {
        // When & Then - get wishlist for user with no items
        mockMvc.perform(get("/api/v1/wishlist/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getWishlist_ForNonExistentUser_ShouldReturnEmptyList() throws Exception {
        // This test assumes your controller handles non-existent users gracefully
        // If it should return 404, adjust accordingly
        mockMvc.perform(get("/api/v1/wishlist/user/{userId}", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}