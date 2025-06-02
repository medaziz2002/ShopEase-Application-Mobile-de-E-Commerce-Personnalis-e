package com.example.ecommerce.repository.integration;



import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findByUserId_ShouldReturnUserOrders() {
        // Given
        User user = new User();
        user.setNom("testuser");
        user.setPrenom("testuser");
        entityManager.persist(user);

        Order order = new Order();
        order.setUser(user);
        entityManager.persist(order);
        entityManager.flush();

        // When
        List<Order> foundOrders = orderRepository.findByUserId(user.getId());

        // Then
        assertThat(foundOrders).hasSize(1);
        assertThat(foundOrders.get(0).getUser().getId()).isEqualTo(user.getId());
    }
}