package com.example.ecommerce.service.unitaire;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.repository.NotificationLogRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.FirebaseMessagingService;
import com.example.ecommerce.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private FirebaseMessagingService firebaseMessagingService;


    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_ShouldSaveOrderAndUpdateStock() {
        // Arrange
        OrderDTO dto = new OrderDTO();
        dto.setUserId(1);

        OrderItemDTO itemDto = new OrderItemDTO();
        itemDto.setProductId(1);
        itemDto.setQuantity(2);
        dto.setItems(List.of(itemDto));

        User user = new User();
        Product product = new Product();
        product.setStock(10);
        Order order = new Order();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(orderMapper.toEntity(dto)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(dto);

        // Act
        OrderDTO result = orderService.createOrder(dto);

        // Assert
        assertEquals(8, product.getStock()); // 10 - 2 = 8
        verify(productRepository).save(product);
        assertEquals(dto, result);
    }
    @Test
    void updateOrderStatus_ShouldUpdateAndNotify() {
        // Arrange
        Integer orderId = 1;
        String status = "Commande_confirmée";
        Order order = new Order();
        order.setId(orderId);
        User user = new User();
        user.setFcmToken("token");
        order.setUser(user);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order); // 👈 important
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDTO());

        // Act
        OrderDTO result = orderService.updateOrderStatus(orderId, status);

        // Assert
        assertEquals(status, order.getStatus());
        verify(notificationLogRepository).save(any());
        verify(firebaseMessagingService).sendNotificationToUser(eq(user), anyString(), anyString());
    }

}