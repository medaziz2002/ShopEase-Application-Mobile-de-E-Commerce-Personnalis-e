package com.example.ecommerce.service.unitaire;

import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    @Test
    void addToCart_ShouldSaveNewCartItem() {
        // Arrange
        Integer productId = 1;
        Integer userId = 1;
        Integer quantity = 2;
        List<String> sizes = Arrays.asList("M", "L");
        List<String> weights = Arrays.asList("500g");

        Product mockProduct = new Product();
        mockProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        // Act
        cartService.addToCart(productId, userId, quantity, sizes, weights);

        // Assert
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void getCartItems_ShouldReturnList() {
        // Arrange
        Integer userId = 1;
        Cart cart1 = new Cart();
        Cart cart2 = new Cart();
        List<Cart> carts = Arrays.asList(cart1, cart2);

        when(cartRepository.findByUserId(userId)).thenReturn(carts);
        when(cartMapper.toDto(cart1)).thenReturn(new CartDto());
        when(cartMapper.toDto(cart2)).thenReturn(new CartDto());

        // Act
        List<CartDto> result = cartService.getCartItems(userId);

        // Assert
        assertEquals(2, result.size());
        verify(cartMapper, times(2)).toDto(any());
    }

    @Test
    void updateQuantity_ShouldUpdateExistingCart() {
        // Arrange
        Integer cartId = 1;
        Integer newQuantity = 5;
        Cart existingCart = new Cart();
        existingCart.setQuantity(1);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(existingCart));

        // Act
        cartService.updateQuantity(cartId, newQuantity);

        // Assert
        assertEquals(newQuantity, existingCart.getQuantity());
        verify(cartRepository).save(existingCart);
    }

    @Test
    void removeFromCart_ShouldDeleteItem() {
        // Arrange
        Integer cartId = 1;

        // Act
        cartService.removeFromCart(cartId);

        // Assert
        verify(cartRepository).deleteById(cartId);
    }

    @Test
    void clearCart_ShouldDeleteAllUserItems() {
        // Arrange
        Integer userId = 1;

        // Act
        cartService.clearCart(userId);

        // Assert
        verify(cartRepository).deleteAllByUserId(userId);
    }

    @Test
    void isProductInUserCart_ShouldReturnTrueWhenExists() {
        // Arrange
        Integer productId = 1;
        Integer userId = 1;

        when(cartRepository.existsByProductIdAndUserId(productId, userId)).thenReturn(true);

        // Act
        boolean result = cartService.isProductInUserCart(productId, userId);

        // Assert
        assertTrue(result);
    }

    @Test
    void updateCartOptions_ShouldUpdateSizesAndWeights() {
        // Arrange
        Integer cartId = 1;
        List<String> sizes = Arrays.asList("XL");
        List<String> weights = Arrays.asList("1kg");
        Cart existingCart = new Cart();
        existingCart.setSize(new ArrayList<>());
        existingCart.setWeight(new ArrayList<>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(existingCart));

        // Act
        cartService.updateCartOptions(cartId, sizes, weights);

        // Assert
        assertTrue(existingCart.getSize().contains("XL"));
        assertTrue(existingCart.getWeight().contains("1kg"));
        verify(cartRepository).save(existingCart);
    }
}