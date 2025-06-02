package com.example.ecommerce.controller.integration;

import com.example.ecommerce.controller.CartController;
import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAddToCart_Success() throws Exception {
        // Given
        doNothing().when(cartService).addToCart(any(), any(), any(), any(), any());

        // When & Then
        mockMvc.perform(post("/api/v1/cart/add")
                        .param("productId", "1")
                        .param("userId", "1")
                        .param("quantity", "2")
                        .param("sizes", "M", "L")
                        .param("weights", "1kg", "2kg"))
                .andExpect(status().isCreated());

        verify(cartService, times(1)).addToCart(1, 1, 2, Arrays.asList("M", "L"), Arrays.asList("1kg", "2kg"));
    }

    @Test
    void testAddToCart_WithoutOptionalParams() throws Exception {
        // Given
        doNothing().when(cartService).addToCart(any(), any(), any(), any(), any());

        // When & Then
        mockMvc.perform(post("/api/v1/cart/add")
                        .param("productId", "1")
                        .param("userId", "1")
                        .param("quantity", "1"))
                .andExpect(status().isCreated());

        verify(cartService, times(1)).addToCart(1, 1, 1, null, null);
    }

    @Test
    void testGetCartItems_Success() throws Exception {
        // Given
        ProductDto product1 = ProductDto.builder()
                .id(1)
                .title("Product 1")
                .price(19.99)
                .build();

        ProductDto product2 = ProductDto.builder()
                .id(2)
                .title("Product 2")
                .price(29.99)
                .build();

        CartDto cartDto1 = CartDto.builder()
                .id(1)
                .productId(1)
                .userId(1)
                .productDto(product1)
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(39.98))
                .size(Arrays.asList("M"))
                .weight(Arrays.asList("1kg"))
                .build();

        CartDto cartDto2 = CartDto.builder()
                .id(2)
                .productId(2)
                .userId(1)
                .productDto(product2)
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(29.99))
                .size(Arrays.asList("L"))
                .weight(Arrays.asList("2kg"))
                .build();

        List<CartDto> cartItems = Arrays.asList(cartDto1, cartDto2);
        when(cartService.getCartItems(1)).thenReturn(cartItems);

        // When & Then
        mockMvc.perform(get("/api/v1/cart/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].productDto.title").value("Product 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].quantity").value(1))
                .andExpect(jsonPath("$[1].productDto.title").value("Product 2"));

        verify(cartService, times(1)).getCartItems(1);
    }



    @Test
    void testUpdateQuantity_Success() throws Exception {
        // Given
        doNothing().when(cartService).updateQuantity(any(), any());

        // When & Then
        mockMvc.perform(put("/api/v1/cart/1")
                        .param("quantity", "5"))
                .andExpect(status().isOk());

        verify(cartService, times(1)).updateQuantity(1, 5);
    }

    @Test
    void testUpdateCartOptions_Success() throws Exception {
        // Given
        doNothing().when(cartService).updateCartOptions(any(), any(), any());

        // When & Then
        mockMvc.perform(post("/api/v1/cart/1/options")
                        .param("sizes", "XL", "XXL")
                        .param("weights", "3kg", "4kg"))
                .andExpect(status().isOk());

        verify(cartService, times(1)).updateCartOptions(1, Arrays.asList("XL", "XXL"), Arrays.asList("3kg", "4kg"));
    }

    @Test
    void testRemoveFromCart_Success() throws Exception {
        // Given
        doNothing().when(cartService).removeFromCart(any());

        // When & Then
        mockMvc.perform(delete("/api/v1/cart/1"))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).removeFromCart(1);
    }

    @Test
    void testClearCart_Success() throws Exception {
        // Given
        doNothing().when(cartService).clearCart(any());

        // When & Then
        mockMvc.perform(delete("/api/v1/cart/clear/1"))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).clearCart(1);
    }

    @Test
    void testCheckProductInCart_ReturnsTrue() throws Exception {
        // Given
        when(cartService.isProductInUserCart(1, 1)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/v1/cart/exists")
                        .param("productId", "1")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(cartService, times(1)).isProductInUserCart(1, 1);
    }

    @Test
    void testCheckProductInCart_ReturnsFalse() throws Exception {
        // Given
        when(cartService.isProductInUserCart(1, 1)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/v1/cart/exists")
                        .param("productId", "1")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(cartService, times(1)).isProductInUserCart(1, 1);
    }
}