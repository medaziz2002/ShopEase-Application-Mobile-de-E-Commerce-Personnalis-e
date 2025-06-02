package com.example.ecommerce.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLogDTO {
    private Integer id;
    private String title;
    private String body;
    private LocalDateTime sentAt;
    private Integer userId;
}

