package com.example.ecommerce.repository.unitaire;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser() {
        User user = new User();
        user.setEmail("test@example.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void countByRoleCaseInsensitive_ShouldIgnoreCase() {
        User admin1 = new User();
        admin1.setRole("ADMIN");
        User admin2 = new User();
        admin2.setRole("admin");
        userRepository.save(admin1);
        userRepository.save(admin2);

        long count = userRepository.countByRoleCaseInsensitive("admin");

        assertEquals(2, count);
    }
}