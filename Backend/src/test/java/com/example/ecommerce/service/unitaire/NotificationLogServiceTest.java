package com.example.ecommerce.service.unitaire;

import com.example.ecommerce.dto.NotificationLogDTO;
import com.example.ecommerce.entity.NotificationLog;
import com.example.ecommerce.mapper.NotificationLogMapper;
import com.example.ecommerce.repository.NotificationLogRepository;
import com.example.ecommerce.service.NotificationLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationLogServiceTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private NotificationLogMapper notificationLogMapper;

    @InjectMocks
    private NotificationLogService notificationLogService;

    @Test
    void getNotificationsForUser_ShouldReturnMappedList() {
        // Arrange
        Integer userId = 1;
        NotificationLog log1 = new NotificationLog();
        NotificationLog log2 = new NotificationLog();
        NotificationLogDTO dto1 = new NotificationLogDTO();
        NotificationLogDTO dto2 = new NotificationLogDTO();

        when(notificationLogRepository.findByUserId(userId)).thenReturn(List.of(log1, log2));
        when(notificationLogMapper.toDto(log1)).thenReturn(dto1);
        when(notificationLogMapper.toDto(log2)).thenReturn(dto2);

        // Act
        List<NotificationLogDTO> result = notificationLogService.getNotificationsForUser(userId);

        // Assert
        assertEquals(2, result.size());
        verify(notificationLogMapper, times(2)).toDto(any());
    }
}