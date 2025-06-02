package com.example.ecommerce.controller.unitaire;

import com.example.ecommerce.controller.WishlistController;
import com.example.ecommerce.dto.WishListDto;
import com.example.ecommerce.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    @Test
    void addToWishlist_ShouldCallService() {
        WishListDto dto = new WishListDto();
        wishlistController.addToWishlist(dto);
        verify(wishlistService).addToWishlist(dto);
    }

    @Test
    void removeFromWishlist_ShouldCallService() {
        wishlistController.removeFromWishlist(1);
        verify(wishlistService).removeFromWishlist(1);
    }

    @Test
    void getUserWishlist_ShouldReturnList() {
        WishListDto dto = new WishListDto();
        when(wishlistService.getUserWishlist(1)).thenReturn(List.of(dto));

        ResponseEntity<List<WishListDto>> response = wishlistController.getUserWishlist(1);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }
}