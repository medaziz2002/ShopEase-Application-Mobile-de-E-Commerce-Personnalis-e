package com.example.ecommerce.service.integration;


import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private OrderDTO testOrderDTO;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur de test
        User user = new User();
        user.setPrenom("testuser");
        user.setNom("testuser");
        userRepository.save(user);

        // Créer un produit de test
        Product product = new Product();
        product.setTitle("Test Product");
        product.setPrice(49.99);
        product.setStock(10);
        productRepository.save(product);

        testOrderDTO = OrderDTO.builder()
                .userId(user.getId())
                .orderDate(new Date())
                .status("En_attente_de_confirmation")
                .totalAmount(99.99)
                .items(Arrays.asList(
                        OrderItemDTO.builder()
                                .productId(product.getId())
                                .quantity(2)
                                .unitPrice(49.99)
                                .build()
                ))
                .build();
    }

    @Test
    void createOrder_ShouldCreateOrderAndUpdateStock() {
        // When
        OrderDTO createdOrder = orderService.createOrder(testOrderDTO);

        // Then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getUserId()).isEqualTo(testOrderDTO.getUserId());

        // Vérifier que le stock a été mis à jour
        Product product = productRepository.findById(testOrderDTO.getItems().get(0).getProductId()).orElseThrow();
        assertThat(product.getStock()).isEqualTo(8); // 10 - 2 = 8
    }

    @Test
    void getUserOrders_ShouldReturnUserOrders() {
        // Given
        orderService.createOrder(testOrderDTO);

        // When
        List<OrderDTO> userOrders = orderService.getUserOrders(testOrderDTO.getUserId());

        // Then
        assertThat(userOrders).isNotEmpty();
        assertThat(userOrders.get(0).getUserId()).isEqualTo(testOrderDTO.getUserId());
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus() {
        // Given
        OrderDTO createdOrder = orderService.createOrder(testOrderDTO);

        // When
        OrderDTO updatedOrder = orderService.updateOrderStatus(createdOrder.getId(), "Commande_confirmée");

        // Then
        assertThat(updatedOrder.getStatus()).isEqualTo("Commande_confirmée");
    }
}