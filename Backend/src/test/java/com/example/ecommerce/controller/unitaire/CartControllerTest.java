package com.example.ecommerce.controller.unitaire;

import com.example.ecommerce.controller.CartController;
import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private CartDto cartDto1, cartDto2;

    @BeforeEach
    void setUp() {
        cartDto1 = new CartDto(1, 101, 1, 2, null, null);
        cartDto2 = new CartDto(2, 101, 2, 1, null, null);
    }

    @Test
    void addToCart_ShouldCallService() {
        // Arrange
        Integer productId = 1;
        Integer userId = 101;
        Integer quantity = 2;
        List<String> sizes = Arrays.asList("M", "L");
        List<String> weights = Arrays.asList("500g");


        cartController.addToCart(productId, userId, quantity, sizes, weights);


        verify(cartService, times(1))
                .addToCart(productId, userId, quantity, sizes, weights);
    }

    @Test
    void getCartItems_ShouldReturnList() {

        Integer userId = 101;
        List<CartDto> expectedItems = Arrays.asList(cartDto1, cartDto2);
        when(cartService.getCartItems(userId)).thenReturn(expectedItems);


        ResponseEntity<List<CartDto>> response = cartController.getCartItems(userId);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItems, response.getBody());
    }

    @Test
    void updateQuantity_ShouldCallService() {
        // Arrange
        Integer cartId = 1;
        Integer quantity = 5;


        cartController.updateQuantity(cartId, quantity);


        verify(cartService, times(1)).updateQuantity(cartId, quantity);
    }

    @Test
    void updateCartOptions_ShouldCallService() {
        // Arrange
        Integer cartId = 1;
        List<String> sizes = Arrays.asList("XL");
        List<String> weights = Arrays.asList("1kg");


        cartController.updateCartOptions(cartId, sizes, weights);


        verify(cartService, times(1)).updateCartOptions(cartId, sizes, weights);
    }

    @Test
    void removeFromCart_ShouldReturnNoContent() {

        Integer cartId = 1;


        ResponseEntity<Void> response = cartController.removeFromCart(cartId);


        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService, times(1)).removeFromCart(cartId);
    }

    @Test
    void clearCart_ShouldReturnNoContent() {

        Integer userId = 101;


        ResponseEntity<Void> response = cartController.clearCart(userId);


        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService, times(1)).clearCart(userId);
    }

    @Test
    void checkProductInCart_ShouldReturnBoolean() {

        Integer productId = 1;
        Integer userId = 101;
        boolean expected = true;
        when(cartService.isProductInUserCart(productId, userId)).thenReturn(expected);


        ResponseEntity<Boolean> response = cartController.checkProductInCart(productId, userId);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }
}