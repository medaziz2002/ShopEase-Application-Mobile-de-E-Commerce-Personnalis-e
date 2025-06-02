package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.NotificationLogDTO;
import com.example.ecommerce.entity.NotificationLog;
import com.example.ecommerce.entity.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationLogMapper {

    public NotificationLogDTO toDto(NotificationLog entity) {
        return NotificationLogDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .body(entity.getBody())
                .sentAt(entity.getSentAt())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .build();
    }

    public NotificationLog toEntity(NotificationLogDTO dto, User user) {
        return NotificationLog.builder()
                .title(dto.getTitle())
                .body(dto.getBody())
                .sentAt(dto.getSentAt())
                .user(user)
                .build();
    }
}

