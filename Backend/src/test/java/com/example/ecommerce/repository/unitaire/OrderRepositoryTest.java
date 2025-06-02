package com.example.ecommerce.repository.unitaire;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findByUserId_ShouldReturnUserOrders() {
        User user = new User();
        user.setId(1);

        Order order = new Order();
        order.setUser(user);
        orderRepository.save(order);

        List<Order> result = orderRepository.findByUserId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUser().getId());
    }
}