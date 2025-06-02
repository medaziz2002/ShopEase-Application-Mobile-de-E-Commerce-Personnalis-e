package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByProduct(Product product);
    Optional<Review> findByProductIdAndUserId(Integer productId, Integer userId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

}

