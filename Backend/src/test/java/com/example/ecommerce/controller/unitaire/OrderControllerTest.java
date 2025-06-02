package com.example.ecommerce.controller.unitaire;

import com.example.ecommerce.controller.OrderController;
import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.service.OrderService;
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
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void createOrder_ShouldReturnOrder() {
        OrderDTO dto = new OrderDTO();
        when(orderService.createOrder(dto)).thenReturn(dto);

        ResponseEntity<OrderDTO> response = orderController.createOrder(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getUserOrders_ShouldReturnList() {
        OrderDTO dto = new OrderDTO();
        when(orderService.getUserOrders(1)).thenReturn(List.of(dto));

        ResponseEntity<List<OrderDTO>> response = orderController.getUserOrders(1);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getVendeurOrders_ShouldReturnList() {
        OrderDTO dto = new OrderDTO();
        when(orderService.getVendeurOrders(1)).thenReturn(List.of(dto));

        ResponseEntity<List<OrderDTO>> response = orderController.getVendeurOrders(1);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getOrderDetails_ShouldReturnOrder() {
        OrderDTO dto = new OrderDTO();
        when(orderService.getOrderDetails(1)).thenReturn(dto);

        ResponseEntity<OrderDTO> response = orderController.getOrderDetails(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrder() {
        OrderDTO dto = new OrderDTO();
        when(orderService.updateOrderStatus(1, "SHIPPED")).thenReturn(dto);

        ResponseEntity<OrderDTO> response = orderController.updateOrderStatus(1, "SHIPPED");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }
}