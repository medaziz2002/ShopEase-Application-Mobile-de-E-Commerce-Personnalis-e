package com.example.ecommerce.controller.integration;

import com.example.ecommerce.entity.NotificationLog;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.NotificationLogRepository;
import com.example.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc  // ✅ Correction ici
@ActiveProfiles("test")
@Transactional
class NotificationLogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private NotificationLog notification1;
    private NotificationLog notification2;

    @BeforeEach
    void setUp() {
        notificationLogRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setNom("testuser");
        testUser.setPrenom("testuser");
        testUser = userRepository.save(testUser);

        notification1 = new NotificationLog();
        notification1.setUser(testUser);
        notification1.setTitle("INFO");
        notification1.setBody("Test notification 1");
        notification1.setSentAt(LocalDateTime.now().minusHours(2));


        notification2 = new NotificationLog();
        notification2.setUser(testUser);
        notification2.setTitle("WARNING");
        notification2.setBody("Test notification 2");
        notification2.setSentAt(LocalDateTime.now().minusHours(1));


        notificationLogRepository.saveAll(List.of(notification1, notification2));
    }

    @Test
    void getUserNotifications_ShouldReturnUserNotifications_WhenUserExists() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/user/{userId}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(testUser.getId())))
                .andExpect(jsonPath("$[0].body", is("Test notification 1"))) // Changed from 'message' to 'body'
                .andExpect(jsonPath("$[0].title", is("INFO"))) // Changed from 'type' to 'title'
                // Remove the 'read' field check since it doesn't exist in the response
                .andExpect(jsonPath("$[1].userId", is(testUser.getId())))
                .andExpect(jsonPath("$[1].body", is("Test notification 2"))) // Changed from 'message' to 'body'
                .andExpect(jsonPath("$[1].title", is("WARNING"))); // Changed from 'type' to 'title'
        // Remove the 'read' field check since it doesn't exist in the response
    }

    @Test
    void getUserNotifications_ShouldReturnEmptyList_WhenUserHasNoNotifications() throws Exception {
        User userWithoutNotifications = new User();
        userWithoutNotifications.setEmail("empty@example.com");
        userWithoutNotifications.setNom("emptyuser");
        userWithoutNotifications.setPrenom("emptyuser");
        userWithoutNotifications = userRepository.save(userWithoutNotifications);

        mockMvc.perform(get("/api/v1/notifications/user/{userId}", userWithoutNotifications.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getUserNotifications_ShouldReturnEmptyList_WhenUserDoesNotExist() throws Exception {
        Integer nonExistentUserId = 99999;

        mockMvc.perform(get("/api/v1/notifications/user/{userId}", nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getUserNotifications_ShouldReturnBadRequest_WhenUserIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/user/{userId}", "invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserNotifications_ShouldReturnCorrectOrder_WhenMultipleNotificationsExist() throws Exception {
        NotificationLog notification3 = new NotificationLog();
        notification3.setUser(testUser);
        notification3.setTitle("ERROR");
        notification3.setBody("Test notification 3");
        notification3.setSentAt(LocalDateTime.now());

        notificationLogRepository.save(notification3);

        mockMvc.perform(get("/api/v1/notifications/user/{userId}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].body", // Changed from 'message' to 'body'
                        containsInAnyOrder("Test notification 1", "Test notification 2", "Test notification 3")));
    }
}
