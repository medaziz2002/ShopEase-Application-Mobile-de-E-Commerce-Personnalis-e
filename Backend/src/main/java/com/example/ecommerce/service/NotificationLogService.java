package com.example.ecommerce.service;

import com.example.ecommerce.dto.NotificationLogDTO;
import com.example.ecommerce.mapper.NotificationLogMapper;
import com.example.ecommerce.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;
    private final NotificationLogMapper notificationLogMapper;

    public List<NotificationLogDTO> getNotificationsForUser(Integer userId) {
        return notificationLogRepository.findByUserId(userId).stream()
                .map(notificationLogMapper::toDto)
                .collect(Collectors.toList());
    }


}

