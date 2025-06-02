package com.example.ecommerce.controller.integration;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private OrderDTO testOrderDTO;
    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setNom("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole("CLIENT");
        testUser = userRepository.save(testUser);

        // Create test product
        testProduct = new Product();
        testProduct.setTitle("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(49.99);
        testProduct.setStock(10);
        testProduct.setSeller(testUser); // Or create a separate seller user
        testProduct = productRepository.save(testProduct);

        // Create test order DTO with existing entities
        testOrderDTO = OrderDTO.builder()
                .userId(testUser.getId())
                .orderDate(new Date())
                .deliveryDate(new Date(System.currentTimeMillis() + 86400000)) // +1 jour
                .status("En_attente_de_confirmation")
                .paymentMethod("Carte de crédit")
                .deliveryAddress("123 Rue de Test, Ville")
                .deliveryMethod("Standard")
                .totalAmount(99.99)
                .deliveryCost(5.99)
                .items(Arrays.asList(
                        OrderItemDTO.builder()
                                .productId(testProduct.getId())
                                .quantity(2)
                                .unitPrice(49.99)
                                .discount(0)
                                .build()
                ))
                .build();
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testOrderDTO.getUserId()))
                .andExpect(jsonPath("$.status").value(testOrderDTO.getStatus()));
    }

    @Test
    void getUserOrders_ShouldReturnUserOrders() throws Exception {
        // Créer une commande de test
        Order order = new Order();
        order.setUser(testUser);
        order.setStatus(testOrderDTO.getStatus());
        orderRepository.save(order);

        mockMvc.perform(get("/api/v1/orders/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(testUser.getId()));
    }

    @Test
    void getVendeurOrders_ShouldReturnVendeurOrders() throws Exception {
        // Create a seller user if needed
        User vendeur = new User();
        vendeur.setNom("Vendeur Test");
        vendeur.setEmail("vendeur@example.com");
        vendeur.setPassword("password");
        vendeur.setRole("VENDEUR");
        vendeur = userRepository.save(vendeur);

        mockMvc.perform(get("/api/v1/orders/vendeur/{userId}", vendeur.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderDetails_ShouldReturnOrder() throws Exception {
        Order order = new Order();
        order.setUser(testUser);
        order.setStatus(testOrderDTO.getStatus());
        Order savedOrder = orderRepository.save(order);

        mockMvc.perform(get("/api/v1/orders/{orderId}", savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()));
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus() throws Exception {
        Order order = new Order();
        order.setUser(testUser);
        order.setStatus("En_attente_de_confirmation");
        Order savedOrder = orderRepository.save(order);

        String newStatus = "Commande_confirmée";

        mockMvc.perform(patch("/api/v1/orders/{orderId}/status", savedOrder.getId())
                        .param("status", newStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(newStatus));
    }
}