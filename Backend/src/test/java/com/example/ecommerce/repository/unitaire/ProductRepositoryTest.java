package com.example.ecommerce.repository.unitaire;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void countBySellerId_ShouldReturnCorrectCount() {
        User seller = new User();
        seller.setId(1);

        Product product1 = new Product();
        product1.setSeller(seller);
        Product product2 = new Product();
        product2.setSeller(seller);
        productRepository.save(product1);
        productRepository.save(product2);

        long count = productRepository.countBySellerId(1);

        assertEquals(2, count);
    }
}