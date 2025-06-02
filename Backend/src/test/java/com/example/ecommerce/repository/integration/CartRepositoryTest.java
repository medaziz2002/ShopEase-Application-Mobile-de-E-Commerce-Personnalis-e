package com.example.ecommerce.repository.integration;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CartRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    private User testUser1;
    private User testUser2;
    private Product testProduct1;
    private Product testProduct2;
    private Cart testCart1;
    private Cart testCart2;
    private Cart testCart3;

    @BeforeEach
    void setUp() {
        // Créer des utilisateurs de test
        testUser1 = User.builder()
                .nom("Test User 1")
                .prenom("Test User 1")
                .email("test1@example.com")
                .build();
        testUser1 = entityManager.persistAndFlush(testUser1);

        testUser2 = User.builder()
                .nom("Test User 2")
                .prenom("Test User 2")
                .email("test2@example.com")
                .build();
        testUser2 = entityManager.persistAndFlush(testUser2);

        // Créer des produits de test
        testProduct1 = Product.builder()
                .title("Test Product 1")
                .description("Description 1")
                .price(19.99)
                .stock(100)
                .build();
        testProduct1 = entityManager.persistAndFlush(testProduct1);

        testProduct2 = Product.builder()
                .title("Test Product 2")
                .description("Description 2")
                .price(29.99)
                .stock(50)
                .build();
        testProduct2 = entityManager.persistAndFlush(testProduct2);

        // Créer des éléments de panier de test
        testCart1 = Cart.builder()
                .product(testProduct1)
                .user(testUser1)
                .quantity(2)
                .size(Arrays.asList("M"))
                .weight(Arrays.asList("1kg"))
                .build();
        testCart1 = entityManager.persistAndFlush(testCart1);

        testCart2 = Cart.builder()
                .product(testProduct2)
                .user(testUser1)
                .quantity(1)
                .size(Arrays.asList("L"))
                .weight(Arrays.asList("2kg"))
                .build();
        testCart2 = entityManager.persistAndFlush(testCart2);

        testCart3 = Cart.builder()
                .product(testProduct1)
                .user(testUser2)
                .quantity(3)
                .size(Arrays.asList("S"))
                .weight(Arrays.asList("500g"))
                .build();
        testCart3 = entityManager.persistAndFlush(testCart3);

        entityManager.clear();
    }

    @Test
    void testFindByUserId_ReturnsCorrectItems() {
        // When
        List<Cart> user1CartItems = cartRepository.findByUserId(testUser1.getId());
        List<Cart> user2CartItems = cartRepository.findByUserId(testUser2.getId());

        // Then
        assertEquals(2, user1CartItems.size());
        assertEquals(1, user2CartItems.size());

        // Vérifier que les articles appartiennent au bon utilisateur
        assertTrue(user1CartItems.stream().allMatch(cart -> cart.getUser().getId().equals(testUser1.getId())));
        assertTrue(user2CartItems.stream().allMatch(cart -> cart.getUser().getId().equals(testUser2.getId())));
    }

    @Test
    void testFindByUserId_EmptyCart() {
        // Given
        User newUser = User.builder()
                .nom("New User")
                .prenom("New User")
                .email("new@example.com")
                .build();
        newUser = entityManager.persistAndFlush(newUser);

        // When
        List<Cart> cartItems = cartRepository.findByUserId(newUser.getId());

        // Then
        assertTrue(cartItems.isEmpty());
    }

    @Test
    void testFindByProductIdAndUserIdAndSizeAndWeight_Found() {
        // When
        Optional<Cart> foundCart = cartRepository.findByProductIdAndUserIdAndSizeAndWeight(
                testProduct1.getId(),
                testUser1.getId(),
                "M",
                "1kg"
        );

        // Then
        assertTrue(foundCart.isPresent());
        assertEquals(testCart1.getId(), foundCart.get().getId());
    }

    @Test
    void testFindByProductIdAndUserIdAndSizeAndWeight_NotFound() {
        // When
        Optional<Cart> foundCart = cartRepository.findByProductIdAndUserIdAndSizeAndWeight(
                testProduct1.getId(),
                testUser1.getId(),
                "XL",
                "5kg"
        );

        // Then
        assertFalse(foundCart.isPresent());
    }

    @Test
    void testDeleteAllByUserId_Success() {
        // Given
        assertEquals(2, cartRepository.findByUserId(testUser1.getId()).size());

        // When
        cartRepository.deleteAllByUserId(testUser1.getId());
        entityManager.flush();

        // Then
        List<Cart> remainingItems = cartRepository.findByUserId(testUser1.getId());
        assertTrue(remainingItems.isEmpty());

        // Vérifier que les articles de l'autre utilisateur ne sont pas affectés
        List<Cart> user2Items = cartRepository.findByUserId(testUser2.getId());
        assertEquals(1, user2Items.size());
    }

    @Test
    void testExistsByProductIdAndUserId_ReturnsTrue() {
        // When
        boolean exists = cartRepository.existsByProductIdAndUserId(
                testProduct1.getId(),
                testUser1.getId()
        );

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByProductIdAndUserId_ReturnsFalse() {
        // When
        boolean exists = cartRepository.existsByProductIdAndUserId(
                testProduct2.getId(),
                testUser2.getId()
        );

        // Then
        assertFalse(exists);
    }

    @Test
    void testDeleteByUserId_Success() {
        // Given
        assertEquals(2, cartRepository.findByUserId(testUser1.getId()).size());

        // When
        cartRepository.deleteByUserId(testUser1.getId());
        entityManager.flush();

        // Then
        List<Cart> remainingItems = cartRepository.findByUserId(testUser1.getId());
        assertTrue(remainingItems.isEmpty());

        // Vérifier que les articles de l'autre utilisateur ne sont pas affectés
        List<Cart> user2Items = cartRepository.findByUserId(testUser2.getId());
        assertEquals(1, user2Items.size());
    }

    @Test
    void testSaveAndFind_VerifyDataIntegrity() {
        // Given
        Cart newCart = Cart.builder()
                .product(testProduct2)
                .user(testUser2)
                .quantity(5)
                .size(Arrays.asList("XL", "XXL"))
                .weight(Arrays.asList("3kg", "4kg"))
                .build();

        // When
        Cart savedCart = cartRepository.save(newCart);
        entityManager.flush();
        entityManager.clear();

        Optional<Cart> foundCart = cartRepository.findById(savedCart.getId());

        // Then
        assertTrue(foundCart.isPresent());
        Cart retrievedCart = foundCart.get();
        assertEquals(testProduct2.getId(), retrievedCart.getProduct().getId());
        assertEquals(testUser2.getId(), retrievedCart.getUser().getId());
        assertEquals(5, retrievedCart.getQuantity());
        assertEquals(Arrays.asList("XL", "XXL"), retrievedCart.getSize());
        assertEquals(Arrays.asList("3kg", "4kg"), retrievedCart.getWeight());
    }

    @Test
    void testCascadeOperations() {
        // Given
        int initialCount = cartRepository.findAll().size();

        // When - Supprimer un utilisateur ne devrait pas affecter les carts directement
        // (selon votre configuration de cascade)
        List<Cart> user1Items = cartRepository.findByUserId(testUser1.getId());
        assertFalse(user1Items.isEmpty());

        // Supprimer manuellement les carts de l'utilisateur
        cartRepository.deleteAllByUserId(testUser1.getId());
        entityManager.flush();

        // Then
        int finalCount = cartRepository.findAll().size();
        assertEquals(initialCount - 2, finalCount);
    }
}