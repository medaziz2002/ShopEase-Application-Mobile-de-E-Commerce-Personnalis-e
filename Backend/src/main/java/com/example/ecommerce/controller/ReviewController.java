package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ReviewDto;
import com.example.ecommerce.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reviews", description = "API de gestion des avis et notations")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Créer un avis", description = "Permet à un utilisateur de créer un avis pour un produit")
    @ApiResponse(responseCode = "201", description = "Avis créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Produit ou utilisateur non trouvé")
    @ApiResponse(responseCode = "409", description = "L'utilisateur a déjà laissé un avis pour ce produit")
    public void createReview(@RequestBody ReviewDto createReviewDto) {
        reviewService.createReview(createReviewDto);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Mettre à jour un avis", description = "Permet à un utilisateur de modifier son avis")
    @ApiResponse(responseCode = "200", description = "Avis mis à jour avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "403", description = "Non autorisé à modifier cet avis")
    @ApiResponse(responseCode = "404", description = "Avis non trouvé")
    public void updateReview(
            @PathVariable Integer reviewId,
            @RequestBody ReviewDto updateReviewDto
    ) {
        reviewService.updateReview(reviewId, updateReviewDto);
    }


    @PutMapping("/rating/{reviewId}")
    @Operation(summary = "Mettre à jour la note d'un avis", description = "Permet à un utilisateur de modifier la note de son avis")
    @ApiResponse(responseCode = "200", description = "Note mise à jour avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "403", description = "Non autorisé à modifier cet avis")
    @ApiResponse(responseCode = "404", description = "Avis non trouvé")
    public void updateReviewRating(
            @PathVariable Integer reviewId,
            @RequestParam Float rating
    ) {
        reviewService.updateReviewRating(reviewId, rating);
    }



    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Supprimer un avis", description = "Permet à un utilisateur de supprimer son avis")
    @ApiResponse(responseCode = "204", description = "Avis supprimé avec succès")
    @ApiResponse(responseCode = "403", description = "Non autorisé à supprimer cet avis")
    @ApiResponse(responseCode = "404", description = "Avis non trouvé")
    public void deleteReview(
            @PathVariable Integer reviewId,
            @RequestParam Integer userId // correction ici
    ) {
        reviewService.deleteReview(reviewId, userId);
    }


    @GetMapping("/product/user/{productId}/{userId}")
    @Operation(summary = "Lister les avis d’un produit", description = "Récupère tous les avis pour un produit donné")
    @ApiResponse(responseCode = "200", description = "Liste des avis récupérée avec succès")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    public ResponseEntity<List<ReviewDto>> getReviewsByProductId(@PathVariable Integer productId,@PathVariable Integer userId) {
        System.out.println("c bon il a appeler cette méthode bravo");
        System.out.println("le size est comme ca "+reviewService.getReviewsByProductId(productId,userId).size());
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId,userId));
    }

    @GetMapping("/product/{productId}/user/{userId}")
    public ResponseEntity<ReviewDto> getReviewByUserAndProduct(
            @PathVariable Integer productId,
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(reviewService.getReviewByUserAndProduct(productId, userId));
    }

    @GetMapping("/has-reviewed")
    public ResponseEntity<Boolean> hasUserReviewedProduct(
            @RequestParam Long productId,
            @RequestParam Long userId
    ) {
        System.out.println("hasUserReviewedProduct called with productId = " + productId + ", userId = " + userId);

        boolean hasReviewed = reviewService.hasUserReviewedProduct(productId, userId);

        System.out.println("Result of hasUserReviewedProduct: " + hasReviewed);

        return ResponseEntity.ok(hasReviewed);
    }



}
