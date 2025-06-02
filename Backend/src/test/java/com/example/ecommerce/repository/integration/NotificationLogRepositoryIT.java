package com.example.ecommerce.repository.integration;

import com.example.ecommerce.entity.NotificationLog;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.NotificationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class NotificationLogRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    private User testUser1;
    private User testUser2;
    private NotificationLog notification1;
    private NotificationLog notification2;
    private NotificationLog notification3;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = new User();
        testUser1.setEmail("user1@example.com");
        testUser1.setNom("user1");
        testUser1.setPrenom("user1");
        testUser1 = entityManager.persistAndFlush(testUser1);

        testUser2 = new User();
        testUser2.setEmail("user2@example.com");
        testUser2.setNom("user2");
        testUser2.setPrenom("user2");
        testUser2 = entityManager.persistAndFlush(testUser2);

        // Create notifications for testUser1
        notification1 = new NotificationLog();
        notification1.setUser(testUser1);
        notification1.setTitle("Title 1");
        notification1.setBody("Body 1");
        notification1.setSentAt(LocalDateTime.now().minusHours(2));
        notification1 = entityManager.persistAndFlush(notification1);

        // Create notification for testUser2
        notification2 = new NotificationLog();
        notification2.setUser(testUser1); // This should be for testUser1 to match the test expectations
        notification2.setTitle("Title 2");
        notification2.setBody("Body 2");
        notification2.setSentAt(LocalDateTime.now().minusHours(1));
        notification2 = entityManager.persistAndFlush(notification2);

        // Create notification for testUser2
        notification3 = new NotificationLog();
        notification3.setUser(testUser2);
        notification3.setTitle("Title 3");
        notification3.setBody("Body 3");
        notification3.setSentAt(LocalDateTime.now());
        notification3 = entityManager.persistAndFlush(notification3);
    }

    @Test
    void findByUserId_ShouldReturnUserNotifications_WhenUserExists() {
        List<NotificationLog> result = notificationLogRepository.findByUserId(testUser1.getId());

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.stream().allMatch(n -> n.getUser().getId().equals(testUser1.getId()))).isTrue();

        List<String> titles = result.stream()
                .map(NotificationLog::getTitle)
                .toList();

        assertThat(titles).containsExactlyInAnyOrder("Title 1", "Title 2");
    }

    @Test
    void findByUserId_ShouldReturnEmptyList_WhenUserHasNoNotifications() {
        // Given - create user without notifications
        User userWithoutNotifications = new User();
        userWithoutNotifications.setEmail("empty@example.com");
        userWithoutNotifications.setNom("emptyuser");
        userWithoutNotifications.setPrenom("emptyuser");
        userWithoutNotifications = entityManager.persistAndFlush(userWithoutNotifications);

        // When
        List<NotificationLog> result = notificationLogRepository.findByUserId(userWithoutNotifications.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void findByUserId_ShouldReturnEmptyList_WhenUserDoesNotExist() {
        // Given
        Integer nonExistentUserId = 99999;

        // When
        List<NotificationLog> result = notificationLogRepository.findByUserId(nonExistentUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void findByUserId_ShouldIsolateUserData_WhenMultipleUsersHaveNotifications() {
        // When
        List<NotificationLog> user1Notifications = notificationLogRepository.findByUserId(testUser1.getId());
        List<NotificationLog> user2Notifications = notificationLogRepository.findByUserId(testUser2.getId());

        // Then
        assertThat(user1Notifications).hasSize(2);
        assertThat(user2Notifications).hasSize(1);

        // Verify each user only sees their own notifications
        assertThat(user1Notifications.stream().allMatch(n -> n.getUser().getId().equals(testUser1.getId()))).isTrue();
        assertThat(user2Notifications.stream().allMatch(n -> n.getUser().getId().equals(testUser2.getId()))).isTrue();

        assertThat(user2Notifications.get(0).getBody()).isEqualTo("Body 3"); // Fixed expected value
    }

    @Test
    void findByUserId_ShouldReturnNotificationsWithAllFields() {
        // When
        List<NotificationLog> result = notificationLogRepository.findByUserId(testUser1.getId());

        // Then
        assertThat(result).hasSize(2);

        for (NotificationLog notification : result) {
            assertThat(notification.getId()).isNotNull();
            assertThat(notification.getUser().getId()).isEqualTo(testUser1.getId());
            assertThat(notification.getBody()).isNotBlank();
            assertThat(notification.getTitle()).isNotBlank();
            assertThat(notification.getSentAt()).isNotNull();
        }
    }

    @Test
    void findByUserId_ShouldHandleLargeNumberOfNotifications() {
        // Given - create many notifications for a user
        User tmpUser = new User();
        tmpUser.setEmail("many@example.com");
        tmpUser.setNom("manyuser");
        tmpUser.setPrenom("manyuser");
        User userWithManyNotifications = entityManager.persistAndFlush(tmpUser);

        // Create 50 notifications
        for (int i = 0; i < 50; i++) {
            NotificationLog notification = new NotificationLog();
            notification.setUser(userWithManyNotifications);
            notification.setBody("Bulk notification " + i);
            notification.setTitle(i % 2 == 0 ? "INFO" : "WARNING");
            notification.setSentAt(LocalDateTime.now().minusHours(i));
            entityManager.persist(notification);
        }
        entityManager.flush();

        // When
        List<NotificationLog> result = notificationLogRepository.findByUserId(userWithManyNotifications.getId());

        // Then
        Integer expectedUserId = userWithManyNotifications.getId();
        assertThat(result).hasSize(50);
        assertThat(result.stream().allMatch(n -> n.getUser().getId().equals(expectedUserId))).isTrue();
    }


    @Test
    void findByUserId_ShouldHandleNullUserId() {
        // When
        List<NotificationLog> result = notificationLogRepository.findByUserId(null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void findByUserId_ShouldPreserveDataIntegrity() {
        // Given
        int initialCount = (int) notificationLogRepository.count();

        // When - retrieve user notifications
        List<NotificationLog> result = notificationLogRepository.findByUserId(testUser1.getId());

        // Then - verify data wasn't modified
        assertThat(notificationLogRepository.count()).isEqualTo(initialCount);
        assertThat(result).hasSize(2);

        // Verify returned entities are managed instances
        result.forEach(notification -> {
            assertThat(entityManager.getEntityManager().contains(notification)).isTrue();
        });
    }
}