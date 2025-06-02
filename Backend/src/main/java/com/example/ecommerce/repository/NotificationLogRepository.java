package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Image;
import com.example.ecommerce.entity.NotificationLog;
import com.example.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Integer> {
    List<NotificationLog> findByUserId(Integer userId);

}
