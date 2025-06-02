package com.example.ecommerce.service.integration;
import com.example.ecommerce.dto.NotificationLogDTO;
import com.example.ecommerce.entity.NotificationLog;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.NotificationLogRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.NotificationLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationLogServiceIT {

    @Autowired
    private NotificationLogService notificationLogService;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private NotificationLog notification1;
    private NotificationLog notification2;

    @BeforeEach
    void setUp() {
        // Nettoyer les données
        notificationLogRepository.deleteAll();
        userRepository.deleteAll();

        // Créer un utilisateur de test
        testUser = User.builder()
                .email("test@example.com")
                .nom("testuser")
                .build();
        testUser = userRepository.save(testUser);

        // Créer des notifications de test - FIX: Use consistent titles
        notification1 = NotificationLog.builder()
                .user(testUser)
                .title("INFO")  // Changed from "Notification 1"
                .body("Service test notification 1")  // Removed " - INFO" suffix
                .sentAt(LocalDateTime.now().minusHours(3))
                .build();

        notification2 = NotificationLog.builder()
                .user(testUser)
                .title("SUCCESS")  // Changed from "Notification 2"
                .body("Service test notification 2")  // Removed " - SUCCESS" suffix
                .sentAt(LocalDateTime.now().minusHours(1))
                .build();

        notificationLogRepository.saveAll(List.of(notification1, notification2));
    }


    @Test
    void getNotificationsForUser_ShouldReturnNotificationDTOs_WhenUserHasNotifications() {
        // When
        List<NotificationLogDTO> result = notificationLogService.getNotificationsForUser(testUser.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        // FIX: Update filters to match the corrected test data
        NotificationLogDTO dto1 = result.stream()
                .filter(dto -> dto.getBody().equals("Service test notification 1"))  // Now matches
                .findFirst()
                .orElse(null);

        assertThat(dto1).isNotNull();
        assertThat(dto1.getUserId()).isEqualTo(testUser.getId());
        assertThat(dto1.getTitle()).isEqualTo("INFO");
        assertThat(dto1.getSentAt()).isNotNull();

        NotificationLogDTO dto2 = result.stream()
                .filter(dto -> dto.getBody().equals("Service test notification 2"))  // Fixed filter
                .findFirst()
                .orElse(null);

        assertThat(dto2).isNotNull();
        assertThat(dto2.getUserId()).isEqualTo(testUser.getId());
        assertThat(dto2.getTitle()).isEqualTo("SUCCESS");
        assertThat(dto2.getSentAt()).isNotNull();
    }

    @Test
    void getNotificationsForUser_ShouldReturnEmptyList_WhenUserHasNoNotifications() {
        // Given - créer un utilisateur sans notifications
        User userWithoutNotifications = new User();
        userWithoutNotifications.setEmail("empty@example.com");
        userWithoutNotifications.setNom("emptyuser");
        userWithoutNotifications.setPrenom("emptyuser");
        userWithoutNotifications = userRepository.save(userWithoutNotifications);

        // When
        List<NotificationLogDTO> result = notificationLogService.getNotificationsForUser(userWithoutNotifications.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void getNotificationsForUser_ShouldReturnEmptyList_WhenUserDoesNotExist() {
        // Given
        Integer nonExistentUserId = 99999;

        // When
        List<NotificationLogDTO> result = notificationLogService.getNotificationsForUser(nonExistentUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void getNotificationsForUser_ShouldHandleMultipleNotifications_WithDifferentTitles() {
        // Given - ajouter plus de notifications avec différents titres
        NotificationLog notification3 = NotificationLog.builder()
                .user(testUser)
                .title("ERROR")
                .body("Error notification")
                .sentAt(LocalDateTime.now())
                .build();

        NotificationLog notification4 = NotificationLog.builder()
                .user(testUser)
                .title("WARNING")
                .body("Warning notification")
                .sentAt(LocalDateTime.now().minusMinutes(30))
                .build();

        notificationLogRepository.saveAll(List.of(notification3, notification4));

        // When
        List<NotificationLogDTO> result = notificationLogService.getNotificationsForUser(testUser.getId());

        // Then
        assertThat(result).hasSize(4);

        // Vérifier que tous les titres sont présents (au lieu des types)
        List<String> titles = result.stream()
                .map(NotificationLogDTO::getTitle)
                .toList();

        assertThat(titles).containsExactlyInAnyOrder("INFO", "SUCCESS", "ERROR", "WARNING");

        // Vérifier que tous appartiennent au bon utilisateur
        assertThat(result.stream().allMatch(dto -> dto.getUserId().equals(testUser.getId()))).isTrue();
    }


    @Test
    void getNotificationsForUser_ShouldMapAllFieldsCorrectly() {
        // When
        List<NotificationLogDTO> result = notificationLogService.getNotificationsForUser(testUser.getId());

        // Then
        assertThat(result).hasSize(2);

        for (NotificationLogDTO dto : result) {
            assertThat(dto.getId()).isNotNull();
            assertThat(dto.getUserId()).isEqualTo(testUser.getId());
            assertThat(dto.getBody()).isNotBlank();
            assertThat(dto.getTitle()).isNotBlank();
            assertThat(dto.getSentAt()).isNotNull();

        }
    }

    @Test
    void getNotificationsForUser_ShouldNotAffectOtherUsersNotifications() {
        // Given - créer un autre utilisateur avec ses propres notifications
        final User otherUser = User.builder()
                .email("other@example.com")
                .prenom("otheruser")
                .nom("otheruser")
                .build();

        userRepository.save(otherUser);

        NotificationLog otherNotification = NotificationLog.builder()
                .user(otherUser)
                .title("INFO")
                .body("Other user notification")
                .sentAt(LocalDateTime.now())
                .build();

        notificationLogRepository.save(otherNotification);

        // When
        final Integer testUserId = testUser.getId();
        final Integer otherUserId = otherUser.getId();

        List<NotificationLogDTO> testUserResult = notificationLogService.getNotificationsForUser(testUserId);
        List<NotificationLogDTO> otherUserResult = notificationLogService.getNotificationsForUser(otherUserId);

        // Then
        assertThat(testUserResult).hasSize(2);
        assertThat(otherUserResult).hasSize(1);

        // Vérifier que chaque utilisateur ne voit que ses propres notifications
        assertThat(testUserResult.stream().allMatch(dto -> testUserId.equals(dto.getUserId()))).isTrue();
        assertThat(otherUserResult.stream().allMatch(dto -> otherUserId.equals(dto.getUserId()))).isTrue();

        assertThat(otherUserResult.get(0).getBody()).isEqualTo("Other user notification");
    }

}
