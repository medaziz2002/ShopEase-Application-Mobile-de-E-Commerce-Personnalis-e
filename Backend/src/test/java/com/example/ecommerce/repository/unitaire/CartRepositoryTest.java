package com.example.ecommerce.repository.unitaire;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUserId_ShouldReturnUserCarts() {
        // Créer et sauvegarder un utilisateur réel
        User user = User.builder()
                .nom("Doe")
                .prenom("John")
                .email("john.doe@example.com")
                .password("password")
                .telephone("0123456789")
                .build();
        User savedUser = entityManager.persistAndFlush(user);

        // Créer et sauvegarder un produit réel
        Product product = Product.builder()
                .title("Test Product")
                .description("Test Description")
                .price(99.99)
                .stock(10)
                .build();
        Product savedProduct = entityManager.persistAndFlush(product);

        // Créer le cart avec les entités sauvegardées
        Cart cart = Cart.builder()
                .user(savedUser)
                .product(savedProduct)
                .quantity(2)
                .build();
        entityManager.persistAndFlush(cart);

        // Test
        List<Cart> result = cartRepository.findByUserId(savedUser.getId());

        // Assertions
        assertEquals(1, result.size());
        assertEquals(savedUser.getId(), result.get(0).getUser().getId());
        assertEquals(savedProduct.getId(), result.get(0).getProduct().getId());
        assertEquals(2, result.get(0).getQuantity());
    }

    @Test
    void existsByProductIdAndUserId_ShouldReturnTrueWhenExists() {
        // Créer et sauvegarder un utilisateur réel
        User user = User.builder()
                .nom("Smith")
                .prenom("Jane")
                .email("jane.smith@example.com")
                .password("password")
                .telephone("0987654321")
                .build();
        User savedUser = entityManager.persistAndFlush(user);

        // Créer et sauvegarder un produit réel
        Product product = Product.builder()
                .title("Test Product 2")
                .description("Test Description 2")
                .price(149.99)
                .stock(5)
                .build();
        Product savedProduct = entityManager.persistAndFlush(product);

        // Créer le cart avec les entités sauvegardées
        Cart cart = Cart.builder()
                .user(savedUser)
                .product(savedProduct)
                .quantity(1)
                .build();
        entityManager.persistAndFlush(cart);

        // Test
        boolean exists = cartRepository.existsByProductIdAndUserId(
                savedProduct.getId(),
                savedUser.getId()
        );

        // Assertion
        assertTrue(exists);
    }

    @Test
    void existsByProductIdAndUserId_ShouldReturnFalseWhenNotExists() {
        // Créer et sauvegarder des entités mais sans créer de cart
        User user = User.builder()
                .nom("Brown")
                .prenom("Bob")
                .email("bob.brown@example.com")
                .password("password")
                .telephone("0111222333")
                .build();
        User savedUser = entityManager.persistAndFlush(user);

        Product product = Product.builder()
                .title("Test Product 3")
                .description("Test Description 3")
                .price(79.99)
                .stock(20)
                .build();
        Product savedProduct = entityManager.persistAndFlush(product);

        // Test sans créer de cart
        boolean exists = cartRepository.existsByProductIdAndUserId(
                savedProduct.getId(),
                savedUser.getId()
        );

        // Assertion
        assertFalse(exists);
    }

    @Test
    void deleteByUserId_ShouldRemoveAllUserCarts() {
        // Créer et sauvegarder un utilisateur
        User user = User.builder()
                .nom("Wilson")
                .prenom("Alice")
                .email("alice.wilson@example.com")
                .password("password")
                .telephone("0444555666")
                .build();
        User savedUser = entityManager.persistAndFlush(user);

        // Créer et sauvegarder plusieurs produits
        Product product1 = Product.builder()
                .title("Product 1")
                .description("Description 1")
                .price(29.99)
                .stock(100)
                .build();
        Product savedProduct1 = entityManager.persistAndFlush(product1);

        Product product2 = Product.builder()
                .title("Product 2")
                .description("Description 2")
                .price(39.99)
                .stock(50)
                .build();
        Product savedProduct2 = entityManager.persistAndFlush(product2);

        // Créer plusieurs carts pour le même utilisateur
        Cart cart1 = Cart.builder()
                .user(savedUser)
                .product(savedProduct1)
                .quantity(3)
                .build();
        entityManager.persistAndFlush(cart1);

        Cart cart2 = Cart.builder()
                .user(savedUser)
                .product(savedProduct2)
                .quantity(1)
                .build();
        entityManager.persistAndFlush(cart2);

        // Vérifier qu'il y a bien 2 carts
        List<Cart> cartsBefore = cartRepository.findByUserId(savedUser.getId());
        assertEquals(2, cartsBefore.size());

        // Supprimer tous les carts de l'utilisateur (si cette méthode existe)
        cartRepository.deleteByUserId(savedUser.getId());
        entityManager.flush();

        // Vérifier que tous les carts ont été supprimés
        List<Cart> cartsAfter = cartRepository.findByUserId(savedUser.getId());
        assertEquals(0, cartsAfter.size());
    }
}