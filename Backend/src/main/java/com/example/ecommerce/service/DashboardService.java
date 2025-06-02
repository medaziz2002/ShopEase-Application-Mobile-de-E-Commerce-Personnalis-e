package com.example.ecommerce.service;

import com.example.ecommerce.repository.CategoriesRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoriesRepository categoryRepository;

    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();

        // Nombre total d'utilisateurs
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);

        // Nombre de clients (supposons que les clients ont le rôle "CLIENT")
        long totalClients = userRepository.countByRoleCaseInsensitive("Client");
        stats.put("totalClients", totalClients);

        // Nombre de vendeurs (supposons que les vendeurs ont le rôle "SELLER")
        long totalSellers = userRepository.countByRoleCaseInsensitive("Vendeur");
        stats.put("totalSellers", totalSellers);

        // Nombre total de produits
        long totalProducts = productRepository.count();
        stats.put("totalProducts", totalProducts);

        // Nombre total de catégories
        long totalCategories = categoryRepository.count();
        stats.put("totalCategories", totalCategories);

        return stats;
    }

    public Map<String, Long> getDashboardStatsSaller(Integer sellerId) {
        Map<String, Long> stats = new HashMap<>();


        long totalProducts = productRepository.countBySellerId(sellerId);
        stats.put("totalProducts", totalProducts);


        long totalCategories = categoryRepository.count();
        stats.put("totalCategories", totalCategories);
        return stats;
    }
}