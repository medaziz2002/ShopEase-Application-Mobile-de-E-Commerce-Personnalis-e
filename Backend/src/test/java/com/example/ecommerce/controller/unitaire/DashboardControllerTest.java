package com.example.ecommerce.controller.unitaire;

import com.example.ecommerce.controller.DashboardController;
import com.example.ecommerce.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    void getDashboardStats_ShouldReturnStats() {
        when(dashboardService.getDashboardStats()).thenReturn(Map.of("users", 10L));

        ResponseEntity<Map<String, Long>> response = dashboardController.getDashboardStats();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("users"));
    }

    @Test
    void getDashboardStatsSaller_ShouldReturnStats() {
        when(dashboardService.getDashboardStatsSaller(1)).thenReturn(Map.of("products", 5L));

        ResponseEntity<Map<String, Long>> response = dashboardController.getDashboardStatsSaller(1);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("products"));
    }
}
