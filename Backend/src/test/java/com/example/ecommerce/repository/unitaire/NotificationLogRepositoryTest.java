package com.example.ecommerce.repository.unitaire;

import com.example.ecommerce.entity.NotificationLog;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.NotificationLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationLogRepositoryTest {

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Test
    void findByUserId_ShouldReturnUserNotifications() {
        User user = new User();
        user.setId(1);

        NotificationLog log = new NotificationLog();
        log.setUser(user);
        log.setTitle("Test");
        log.setSentAt(LocalDateTime.now());
        notificationLogRepository.save(log);

        List<NotificationLog> result = notificationLogRepository.findByUserId(1);

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getTitle());
    }
}