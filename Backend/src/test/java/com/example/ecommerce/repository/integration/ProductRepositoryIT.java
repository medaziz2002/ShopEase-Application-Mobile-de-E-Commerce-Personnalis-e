package com.example.ecommerce.repository.integration;


import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void countBySellerId_ShouldReturnCorrectCount() {
        // Given
        User seller = new User();
        seller.setNom("seller");
        seller.setPrenom("seller");
        entityManager.persist(seller);

        Product product1 = new Product();
        product1.setTitle("Product 1");
        product1.setSeller(seller);
        entityManager.persist(product1);

        Product product2 = new Product();
        product2.setTitle("Product 2");
        product2.setSeller(seller);
        entityManager.persist(product2);

        entityManager.flush();

        // When
        long count = productRepository.countBySellerId(seller.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }
}
