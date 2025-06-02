package com.example.ecommerce.controller.integration;

import com.example.ecommerce.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // AVEC profil test = pas de sécurité
public class DashboardControllerFunctionalIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DashboardService dashboardService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public DashboardService dashboardService() {
            return Mockito.mock(DashboardService.class);
        }
    }

    @Test
    void getDashboardStats_ShouldReturnStats() throws Exception {
        // Given
        Map<String, Long> mockStats = new HashMap<>();
        mockStats.put("totalUsers", 100L);
        mockStats.put("totalClients", 80L);
        mockStats.put("totalSellers", 20L);
        mockStats.put("totalProducts", 500L);
        mockStats.put("totalCategories", 10L);

        when(dashboardService.getDashboardStats()).thenReturn(mockStats);

        // When & Then
        mockMvc.perform(get("/api/v1/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(100))
                .andExpect(jsonPath("$.totalClients").value(80))
                .andExpect(jsonPath("$.totalSellers").value(20))
                .andExpect(jsonPath("$.totalProducts").value(500))
                .andExpect(jsonPath("$.totalCategories").value(10));
    }

    @Test
    void getDashboardStatsSaller_ShouldReturnSellerStats() throws Exception {
        // Given
        Integer sellerId = 1;
        Map<String, Long> mockStats = new HashMap<>();
        mockStats.put("totalProducts", 50L);
        mockStats.put("totalCategories", 5L);

        when(dashboardService.getDashboardStatsSaller(sellerId)).thenReturn(mockStats);

        // When & Then
        mockMvc.perform(get("/api/v1/dashboard/statsSaller/{seller_id}", sellerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(50))
                .andExpect(jsonPath("$.totalCategories").value(5));
    }
}