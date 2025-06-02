package com.example.ecommerce.service;

import com.example.ecommerce.dto.ReviewDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.NotFoundException;
import com.example.ecommerce.mapper.ReviewMapper;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public void createReview(ReviewDto createReviewDto) {
        User user = userRepository.findById(createReviewDto.getUserId())
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé avec l'ID: " + createReviewDto.getUserId()));
        Product product = productRepository.findById(createReviewDto.getProductId())
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + createReviewDto.getProductId()));

        Review review = reviewMapper.toEntity(createReviewDto);
        reviewRepository.save(review);


        updateProductRatingOnCreate(createReviewDto.getProductId(), createReviewDto.getRating());
    }

    public void updateReview(Integer reviewId, ReviewDto updateReviewDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Avis non trouvé avec l'ID: " + reviewId));

        Float oldRating = review.getRating();
        review.setRating(updateReviewDto.getRating());
        review.setComment(updateReviewDto.getComment());
        reviewRepository.save(review);

        // Mettre à jour le rating du produit après modification
        updateProductRatingOnUpdate(review.getProduct().getId(), oldRating, updateReviewDto.getRating());
    }

    public void deleteReview(Integer reviewId, Integer userId) {
        log.info("Suppression de l'avis {} par l'utilisateur {}", reviewId, userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Avis non trouvé avec l'ID: " + reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Vous n'avez pas l'autorisation de supprimer cet avis");
        }

        Integer productId = review.getProduct().getId();
        Float ratingToRemove = review.getRating();
        reviewRepository.delete(review);

        // Mettre à jour le rating du produit après suppression
        updateProductRatingOnDelete(productId, ratingToRemove);

        log.info("Avis supprimé avec succès");
    }


    private void updateProductRatingOnCreate(Integer productId, Float newRating) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + productId));

        int currentNbPersonne = product.getNbPersonne();
        double currentTotalRating = product.getRating() * currentNbPersonne; // Récupérer la somme totale

        // Ajouter le nouveau rating
        double newTotalRating = currentTotalRating + newRating;
        int newNbPersonne = currentNbPersonne + 1;

        // Calculer la nouvelle moyenne
        double newAverageRating = newTotalRating / newNbPersonne;

        product.setNbPersonne(newNbPersonne);
        product.setRating(newAverageRating);

        productRepository.save(product);
        log.info("Rating du produit {} mis à jour: {} (basé sur {} avis)",
                productId, product.getRating(), product.getNbPersonne());
    }


    private void updateProductRatingOnUpdate(Integer productId, Float oldRating, Float newRating) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + productId));

        int nbPersonne = product.getNbPersonne();
        if (nbPersonne > 0) {
            double currentTotalRating = product.getRating() * nbPersonne;


            double newTotalRating = currentTotalRating - oldRating + newRating;

            // Calculer la nouvelle moyenne
            double newAverageRating = newTotalRating / nbPersonne;

            product.setRating(newAverageRating);

            productRepository.save(product);
            log.info("Rating du produit {} mis à jour: {} (basé sur {} avis)",
                    productId, product.getRating(), product.getNbPersonne());
        }
    }

    // Méthode pour supprimer un avis
    private void updateProductRatingOnDelete(Integer productId, Float ratingToRemove) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + productId));

        int currentNbPersonne = product.getNbPersonne();
        if (currentNbPersonne > 1) {
            double currentTotalRating = product.getRating() * currentNbPersonne; // Récupérer la somme totale

            // Retirer le rating supprimé
            double newTotalRating = currentTotalRating - ratingToRemove;
            int newNbPersonne = currentNbPersonne - 1;

            // Calculer la nouvelle moyenne
            double newAverageRating = newTotalRating / newNbPersonne;

            product.setNbPersonne(newNbPersonne);
            product.setRating(newAverageRating);
        } else {
            // Si c'était le dernier avis, remettre à zéro
            product.setNbPersonne(0);
            product.setRating(0.0);
        }

        productRepository.save(product);
        log.info("Rating du produit {} mis à jour: {} (basé sur {} avis)",
                productId, product.getRating(), product.getNbPersonne());
    }

    public List<ReviewDto> getReviewsByProductId(Integer productId, Integer userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec l'ID: " + productId));

        List<Review> reviews = reviewRepository.findByProduct(product);

        return reviews.stream()
                .filter(review -> !review.getUser().getId().equals(userId)) // exclusion ici
                .map(reviewMapper::toResponseDto)
                .toList();
    }

    public ReviewDto getReviewByUserAndProduct(Integer productId, Integer userId) {
        Review review = reviewRepository.findByProductIdAndUserId(productId, userId)
                .orElseThrow(() -> new NotFoundException("Review not found for productId " + productId + " and userId " + userId));
        return reviewMapper.toResponseDto(review);
    }

    public boolean hasUserReviewedProduct(Long productId, Long userId) {
        return reviewRepository.existsByProductIdAndUserId(productId, userId);
    }

    public void updateReviewRating(Integer reviewId, Float rating) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Avis non trouvé avec l'ID: " + reviewId));

        // Validation de la note
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 0 et 5");
        }

        Float oldRating = review.getRating();
        review.setRating(rating);
        reviewRepository.save(review);

        // Mettre à jour le rating du produit
        updateProductRatingOnUpdate(review.getProduct().getId(), oldRating, rating);
    }
}