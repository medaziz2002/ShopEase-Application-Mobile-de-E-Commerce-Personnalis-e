package com.example.ecommerce.controller.unitaire;

import com.example.ecommerce.controller.NotificationLogController;
import com.example.ecommerce.dto.NotificationLogDTO;
import com.example.ecommerce.service.NotificationLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationLogControllerTest {

    @Mock
    private NotificationLogService notificationLogService;

    @InjectMocks
    private NotificationLogController notificationLogController;

    @Test
    void getUserNotifications_ShouldReturnList() {
        NotificationLogDTO dto = new NotificationLogDTO();
        when(notificationLogService.getNotificationsForUser(1)).thenReturn(List.of(dto));

        ResponseEntity<List<NotificationLogDTO>> response = notificationLogController.getUserNotifications(1);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }
}