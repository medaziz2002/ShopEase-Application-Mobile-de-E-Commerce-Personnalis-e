package com.example.ecommerce.service;

import com.example.ecommerce.entity.NotificationLog;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.NotificationLogRepository;
import com.example.ecommerce.repository.UserRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;


    public void sendNotificationToUser(User user, String title, String body) {
        if (user.getFcmToken() == null || user.getFcmToken().isEmpty()) {
            System.out.println("Aucun token FCM pour l'utilisateur " + user.getId());
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(user.getFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = firebaseMessaging.send(message);
            System.out.println("Notification envoyée avec ID: " + response);



        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                // Token invalide, supprimez-le de l'utilisateur
                user.setFcmToken(null);
                userRepository.save(user);
            }
            System.out.println("Échec d'envoi FCM: " + e.getMessage());
        }
    }
}
