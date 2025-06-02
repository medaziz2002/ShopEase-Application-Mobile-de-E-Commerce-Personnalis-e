package com.example.ecommerce.service.unitaire;

import com.example.ecommerce.repository.CategoriesRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoriesRepository categoryRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboardStats_ShouldReturnAllStats() {
        // Arrange
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countByRoleCaseInsensitive("Client")).thenReturn(8L);
        when(userRepository.countByRoleCaseInsensitive("Vendeur")).thenReturn(2L);
        when(productRepository.count()).thenReturn(50L);
        when(categoryRepository.count()).thenReturn(5L);

        // Act
        Map<String, Long> stats = dashboardService.getDashboardStats();

        // Assert
        assertEquals(5, stats.size());
        assertEquals(10L, stats.get("totalUsers"));
        assertEquals(8L, stats.get("totalClients"));
        assertEquals(2L, stats.get("totalSellers"));
        assertEquals(50L, stats.get("totalProducts"));
        assertEquals(5L, stats.get("totalCategories"));
    }

    @Test
    void getDashboardStatsSaller_ShouldReturnSellerStats() {
        // Arrange
        Integer sellerId = 1;
        when(productRepository.countBySellerId(sellerId)).thenReturn(10L);
        when(categoryRepository.count()).thenReturn(5L);

        // Act
        Map<String, Long> stats = dashboardService.getDashboardStatsSaller(sellerId);

        // Assert
        assertEquals(2, stats.size());
        assertEquals(10L, stats.get("totalProducts"));
        assertEquals(5L, stats.get("totalCategories"));
    }
}