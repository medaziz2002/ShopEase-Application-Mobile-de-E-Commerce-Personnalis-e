package com.example.ecommerce.controller;

import com.example.ecommerce.dto.NotificationLogDTO;
import com.example.ecommerce.service.NotificationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationLogController {

    private final NotificationLogService notificationLogService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationLogDTO>> getUserNotifications(@PathVariable Integer userId) {
        List<NotificationLogDTO> notifs = notificationLogService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifs);
    }


}
