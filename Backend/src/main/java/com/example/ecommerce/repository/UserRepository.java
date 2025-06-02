package com.example.ecommerce.repository;


import com.example.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {


    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE LOWER(u.role) = LOWER(:role)")
    long countByRoleCaseInsensitive(@Param("role") String role);
}
