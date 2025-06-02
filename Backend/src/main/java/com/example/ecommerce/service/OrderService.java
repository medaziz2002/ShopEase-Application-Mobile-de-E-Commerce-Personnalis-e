// OrderService.java
package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.repository.NotificationLogRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final FirebaseMessagingService firebaseMessagingService;
    private final NotificationLogRepository notificationLogRepository;


    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));
        Order order = orderMapper.toEntity(orderDTO);
        order.setUser(user);
        for (OrderItemDTO orderItemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(orderItemDTO.getProductId())
                    .orElseThrow(() -> new NotFoundException("Produit non trouvé"));
            int newQuantity = product.getStock() - orderItemDTO.getQuantity();
            product.setStock(newQuantity);
            productRepository.save(product);
        }
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }



    public List<OrderDTO> getUserOrders(Integer userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getVendeurOrders(Integer userId) {
        List<Order> allOrders = orderRepository.findAll(); // Récupère toutes les commandes
        Set<Order> filteredOrders = new HashSet<>();

        for (Order order : allOrders) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product != null && product.getSeller() != null && product.getSeller().getId().equals(userId)) {
                    filteredOrders.add(order);
                    break; // On a trouvé un produit vendu par ce vendeur, on peut passer à la commande suivante
                }
            }
        }

        return filteredOrders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }





    public OrderDTO getOrderDetails(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée"));
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Commande non trouvée"));

        User user = order.getUser();
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        String title = "📦 Suivi de commande #" + orderId;
        String message = generateStatusMessage(status, orderId);

        if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
            notificationLogRepository.save(
                    NotificationLog.builder()
                            .title(title)
                            .body(message)
                            .sentAt(LocalDateTime.now())
                            .user(user)
                            .build());
            firebaseMessagingService.sendNotificationToUser(user, title, message);
        }else
        {
            notificationLogRepository.save(
                    NotificationLog.builder()
                            .title(title)
                            .body(message)
                            .sentAt(LocalDateTime.now())
                            .user(user)
                            .build());
        }

        return orderMapper.toDto(updatedOrder);
    }

    private String generateStatusMessage(String status, Integer orderId) {
        switch(status) {
            case "En_attente_de_confirmation":
                return "Votre commande #" + orderId + " est en attente ⏳ Nous traitons votre demande et vous confirmerons très bientôt !";

            case "Commande_confirmée":
                return "Votre commande #" + orderId + " a été confirmée ✅ Préparation en cours dans nos ateliers";

            case "Commande_expédiée":
                return "C'est parti ! Votre commande #" + orderId + " est en route 🚚 Numéro de suivi : TRK-" + orderId + " Livraison prévue sous 2-3 jours ouvrés";

            case "Commande_livrée":
                return "Bonne nouvelle ! Votre commande #" + orderId + " a été livrée 🎉 Merci pour votre confiance. Que pensez-vous de votre achat ?";

            default:
                // Fallback pour les statuts non gérés
                return "Mise à jour de votre commande #" + orderId + "Statut : " + status.replace("_", " ");
        }
    }


}
