package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Cart;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByUserId(Integer userId);

    Optional<Cart> findByProductIdAndUserIdAndSizeAndWeight(
            Integer productId,
            Integer userId,
            String size,
            String weight);
    void deleteAllByUserId(Integer userId);

    boolean existsByProductIdAndUserId(Integer productId, Integer userId);

    void deleteByUserId(Integer id);
}
