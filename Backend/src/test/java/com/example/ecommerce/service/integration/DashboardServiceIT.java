package com.example.ecommerce.service.integration;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CategoriesRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class DashboardServiceIT {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoriesRepository categoryRepository;

    @BeforeEach
    void setUp() {
        // Clear the database before each test to ensure clean state
        productRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void getDashboardStats_ShouldReturnCorrectStats() {
        // Given
        User client1 = new User();
        client1.setRole("Client");
        userRepository.save(client1);

        User client2 = new User();
        client2.setRole("Client");
        userRepository.save(client2);

        User seller1 = new User();
        seller1.setRole("Vendeur");
        userRepository.save(seller1);

        Product product1 = new Product();
        product1.setSeller(seller1);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setSeller(seller1);
        productRepository.save(product2);

        Category category = new Category();
        category.setTitre("Catégorie 1");
        categoryRepository.save(category);

        // When
        Map<String, Long> stats = dashboardService.getDashboardStats();

        // Then
        assertThat(stats)
                .containsEntry("totalUsers", 3L)
                .containsEntry("totalClients", 2L)
                .containsEntry("totalSellers", 1L)
                .containsEntry("totalProducts", 2L)
                .containsEntry("totalCategories", 1L);
    }

    @Test
    void getDashboardStatsSeller_ShouldReturnSellerSpecificStats() {
        // Given
        User seller = new User();
        seller.setRole("Vendeur");
        userRepository.save(seller);

        Product product1 = new Product();
        product1.setSeller(seller);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setSeller(seller);
        productRepository.save(product2);

        Category category = new Category();
        category.setTitre("Catégorie 1");
        categoryRepository.save(category);

        // When
        Map<String, Long> stats = dashboardService.getDashboardStatsSaller(seller.getId());

        // Then
        assertThat(stats)
                .containsEntry("totalProducts", 2L)
                .containsEntry("totalCategories", 1L);
    }
}