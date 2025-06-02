package com.example.ecommerce.service.integration;
import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    private Product testProduct;
    private Cart testCart;
    private CartDto testCartDto;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1)
                .title("Test Product")
                .description("Test Description")
                .price(19.99) // correction ici
                .stock(100)
                .build();

        User testUser = User.builder()
                .id(1)
                .nom("John")
                .prenom("Doe")
                .email("john@example.com")
                .build();

        testCart = Cart.builder()
                .id(1)
                .product(testProduct)
                .user(testUser)
                .quantity(2)
                .size(Arrays.asList("M"))
                .weight(Arrays.asList("1kg"))
                .build();

        testCartDto = CartDto.builder()
                .id(1)
                .productId(testProduct.getId())
                .userId(testUser.getId())
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(testProduct.getPrice()))
                .size(Arrays.asList("M"))
                .weight(Arrays.asList("1kg"))
                .build();

    }


    @Test
    void testAddToCart_Success() {
        // Given
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        cartService.addToCart(1, 1, 2, Arrays.asList("M"), Arrays.asList("1kg"));

        // Then
        verify(productRepository, times(1)).findById(1);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddToCart_ProductNotFound() {
        // Given
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(1, 1, 2, Arrays.asList("M"), Arrays.asList("1kg"));
        });

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(1);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testAddToCart_WithNullSizesAndWeights() {
        // Given
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        cartService.addToCart(1, 1, 2, null, null);

        // Then
        verify(productRepository, times(1)).findById(1);
        verify(cartRepository, times(1)).save(argThat(cart ->
                cart.getSize().isEmpty() && cart.getWeight().isEmpty()));
    }

    @Test
    void testGetCartItems_Success() {
        // Given
        List<Cart> cartList = Arrays.asList(testCart);
        when(cartRepository.findByUserId(1)).thenReturn(cartList);
        when(cartMapper.toDto(testCart)).thenReturn(testCartDto);

        // When
        List<CartDto> result = cartService.getCartItems(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCartDto, result.get(0));
        verify(cartRepository, times(1)).findByUserId(1);
        verify(cartMapper, times(1)).toDto(testCart);
    }

    @Test
    void testUpdateQuantity_Success() {
        // Given
        when(cartRepository.findById(1)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        cartService.updateQuantity(1, 5);

        // Then
        verify(cartRepository, times(1)).findById(1);
        verify(cartRepository, times(1)).save(argThat(cart -> cart.getQuantity() == 5));
    }

    @Test
    void testUpdateQuantity_CartNotFound() {
        // Given
        when(cartRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateQuantity(1, 5);
        });

        assertEquals("Cart item not found", exception.getMessage());
        verify(cartRepository, times(1)).findById(1);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testRemoveFromCart_Success() {
        // Given
        doNothing().when(cartRepository).deleteById(1);

        // When
        cartService.removeFromCart(1);

        // Then
        verify(cartRepository, times(1)).deleteById(1);
    }

    @Test
    void testClearCart_Success() {
        // Given
        doNothing().when(cartRepository).deleteAllByUserId(1);

        // When
        cartService.clearCart(1);

        // Then
        verify(cartRepository, times(1)).deleteAllByUserId(1);
    }

    @Test
    void testIsProductInUserCart_ReturnsTrue() {
        // Given
        when(cartRepository.existsByProductIdAndUserId(1, 1)).thenReturn(true);

        // When
        boolean result = cartService.isProductInUserCart(1, 1);

        // Then
        assertTrue(result);
        verify(cartRepository, times(1)).existsByProductIdAndUserId(1, 1);
    }

    @Test
    void testIsProductInUserCart_ReturnsFalse() {
        // Given
        when(cartRepository.existsByProductIdAndUserId(1, 1)).thenReturn(false);

        // When
        boolean result = cartService.isProductInUserCart(1, 1);

        // Then
        assertFalse(result);
        verify(cartRepository, times(1)).existsByProductIdAndUserId(1, 1);
    }

    @Test
    void testUpdateCartOptions_WithBothSizesAndWeights() {
        // Given
        Cart existingCart = Cart.builder()
                .id(1)
                .product(testProduct)
                .user(User.builder().id(1).build())
                .quantity(2)
                .size(Arrays.asList("S"))
                .weight(Arrays.asList("500g"))
                .build();

        when(cartRepository.findById(1)).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(existingCart);

        List<String> newSizes = Arrays.asList("M", "L");
        List<String> newWeights = Arrays.asList("1kg", "2kg");

        // When
        cartService.updateCartOptions(1, newSizes, newWeights);

        // Then
        verify(cartRepository, times(1)).findById(1);
        verify(cartRepository, times(1)).save(argThat(cart -> {
            List<String> expectedSizes = Arrays.asList("S", "M", "L");
            List<String> expectedWeights = Arrays.asList("500g", "1kg", "2kg");
            return cart.getSize().equals(expectedSizes) && cart.getWeight().equals(expectedWeights);
        }));
    }



    @Test
    void testUpdateCartOptions_OnlyWeights() {
        // Given
        Cart existingCart = Cart.builder()
                .id(1)
                .product(testProduct)
                .user(User.builder().id(1).build())
                .quantity(2)
                .size(Arrays.asList("M"))
                .weight(Arrays.asList("1kg"))
                .build();

        when(cartRepository.findById(1)).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(existingCart);

        List<String> newWeights = Arrays.asList("3kg");

        // When
        cartService.updateCartOptions(1, null, newWeights);

        // Then
        verify(cartRepository, times(1)).save(argThat(cart -> {
            List<String> expectedSizes = Arrays.asList("M");
            List<String> expectedWeights = Arrays.asList("1kg", "3kg");
            return cart.getSize().equals(expectedSizes) && cart.getWeight().equals(expectedWeights);
        }));
    }

    @Test
    void testUpdateCartOptions_EmptyLists() {
        // Given
        Cart existingCart = Cart.builder()
                .id(1)
                .product(testProduct)
                .user(User.builder().id(1).build())
                .quantity(2)
                .size(Arrays.asList("M"))
                .weight(Arrays.asList("1kg"))
                .build();

        when(cartRepository.findById(1)).thenReturn(Optional.of(existingCart));

        // When
        cartService.updateCartOptions(1, Arrays.asList(), Arrays.asList());

        // Then
        verify(cartRepository, times(1)).findById(1);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testUpdateCartOptions_CartNotFound() {
        // Given
        when(cartRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateCartOptions(1, Arrays.asList("M"), Arrays.asList("1kg"));
        });

        assertEquals("Cart item not found", exception.getMessage());
        verify(cartRepository, times(1)).findById(1);
        verify(cartRepository, never()).save(any(Cart.class));
    }
}

