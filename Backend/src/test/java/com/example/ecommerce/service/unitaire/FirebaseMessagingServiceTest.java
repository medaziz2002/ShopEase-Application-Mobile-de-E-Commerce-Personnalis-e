package com.example.ecommerce.service.unitaire;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.FirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class FirebaseMessagingServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FirebaseMessagingService firebaseMessagingService; // Use @InjectMocks instead of @Mock

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        // Initialise les mocks avant chaque test
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendNotificationToUser_shouldSendMessageSuccessfully() throws Exception {
        // GIVEN
        User user = new User();
        user.setId(1);
        user.setFcmToken("fake-token");

        // Fake response from Firebase
        when(firebaseMessaging.send(any(Message.class))).thenReturn("mocked-message-id");

        // WHEN
        firebaseMessagingService.sendNotificationToUser(user, "Test Title", "Test Body");

        // THEN
        verify(firebaseMessaging, times(1)).send(any(Message.class));
    }

    @Test
    void sendNotificationToUser_shouldSkipWhenNoToken() throws FirebaseMessagingException {
        // GIVEN
        User user = new User();
        user.setId(2);
        user.setFcmToken(null); // No token

        // WHEN
        firebaseMessagingService.sendNotificationToUser(user, "Title", "Body");

        // THEN
        verify(firebaseMessaging, never()).send(any(Message.class));
    }

    // Optional: close mocks after test
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}