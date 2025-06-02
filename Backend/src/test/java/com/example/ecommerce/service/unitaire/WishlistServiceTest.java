package com.example.ecommerce.service.unitaire;

import com.example.ecommerce.dto.WishListDto;
import com.example.ecommerce.entity.Wishlist;
import com.example.ecommerce.mapper.WishListMapper;
import com.example.ecommerce.repository.WishlistRepository;
import com.example.ecommerce.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishListMapper wishlistMapper;

    @InjectMocks
    private WishlistService wishlistService;

    @Test
    void addToWishlist_ShouldSaveEntity() {
        // Arrange
        WishListDto dto = new WishListDto();
        Wishlist entity = new Wishlist();

        when(wishlistMapper.toEntity(dto)).thenReturn(entity);

        // Act
        wishlistService.addToWishlist(dto);

        // Assert
        verify(wishlistRepository).save(entity);
    }

    @Test
    void getUserWishlist_ShouldReturnMappedList() {
        // Arrange
        Integer userId = 1;
        Wishlist item1 = new Wishlist();
        Wishlist item2 = new Wishlist();
        WishListDto dto1 = new WishListDto();
        WishListDto dto2 = new WishListDto();

        when(wishlistRepository.findByUserId(userId)).thenReturn(List.of(item1, item2));
        when(wishlistMapper.toDto(item1)).thenReturn(dto1);
        when(wishlistMapper.toDto(item2)).thenReturn(dto2);

        // Act
        List<WishListDto> result = wishlistService.getUserWishlist(userId);

        // Assert
        assertEquals(2, result.size());
    }
}