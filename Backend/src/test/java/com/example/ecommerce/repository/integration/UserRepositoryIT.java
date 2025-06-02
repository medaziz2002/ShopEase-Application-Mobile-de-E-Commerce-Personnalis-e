package com.example.ecommerce.repository.integration;



import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = userRepository.findByEmail(user.getEmail());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void findByEmail_ShouldReturnEmptyWhenNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    void countByRoleCaseInsensitive_ShouldCountUsers() {
        User client1 = new User();
        client1.setEmail("client1@example.com");
        client1.setPassword("password");
        client1.setRole("Client");
        entityManager.persist(client1);

        User client2 = new User();
        client2.setEmail("client2@example.com");
        client2.setPassword("password");
        client2.setRole("Client");
        entityManager.persist(client2);

        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setRole("Admin");
        entityManager.persist(admin);

        entityManager.flush();

        long clientCount = userRepository.countByRoleCaseInsensitive("client");
        assertThat(clientCount).isEqualTo(2);

        long adminCount = userRepository.countByRoleCaseInsensitive("ADMIN");
        assertThat(adminCount).isEqualTo(1);
    }
}