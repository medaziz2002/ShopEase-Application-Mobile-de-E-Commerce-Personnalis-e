package com.example.ecommerce.controller.integration;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Image;
import com.example.ecommerce.repository.CategoriesRepository;
import com.example.ecommerce.repository.ImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// Import correct pour AssertJ
import static org.assertj.core.api.Assertions.assertThat;
// Imports pour Hamcrest (utilisé par MockMvc)
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriesRepository categoryRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDto categoryDto;
    private MockMultipartFile imageFile;
    private MockMultipartFile categoryJsonFile;

    @BeforeEach
    void setUp() throws IOException {
        categoryRepository.deleteAll();
        imageRepository.deleteAll();

        categoryDto = CategoryDto.builder()
                .titre("Electronics")
                .build();

        // Création d'un vrai fichier image pour les tests
        imageFile = createValidImageFile();

        // Création du JSON pour la catégorie
        try {
            String categoryJson = objectMapper.writeValueAsString(categoryDto);
            categoryJsonFile = new MockMultipartFile(
                    "categoryDto",
                    "categoryDto",
                    "application/json",
                    categoryJson.getBytes()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Crée un fichier image valide pour les tests
     */
    private MockMultipartFile createValidImageFile() throws IOException {
        // Créer une image 1x1 pixel
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 0xFF0000); // Rouge

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        return new MockMultipartFile(
                "pathImage",
                "test-image.jpg",
                "image/jpeg",
                imageBytes
        );
    }

    /**
     * Crée un fichier image vide pour tester la validation
     */
    private MockMultipartFile createEmptyImageFile() {
        return new MockMultipartFile(
                "pathImage",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );
    }

    /**
     * Crée un fichier avec du contenu non-image
     */
    private MockMultipartFile createInvalidImageFile() {
        return new MockMultipartFile(
                "pathImage",
                "test.jpg",
                "image/jpeg",
                "This is not image data".getBytes()
        );
    }

    @Test
    void should_create_category_successfully() throws Exception {
        mockMvc.perform(multipart("/api/v1/categories")
                        .file(categoryJsonFile)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        // Vérification en base de données
        var categories = categoryRepository.findAll();
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getTitre()).isEqualTo("Electronics");
    }

    @Test
    void should_get_category_by_id() throws Exception {
        // Création d'une catégorie en base
        Category category = Category.builder()
                .titre("Books")
                .build();
        Category savedCategory = categoryRepository.save(category);

        mockMvc.perform(get("/api/v1/categories/{id}", savedCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCategory.getId()))
                .andExpect(jsonPath("$.titre").value("Books"));
    }

    @Test
    void should_get_all_categories() throws Exception {
        // Création de plusieurs catégories
        Category category1 = categoryRepository.save(Category.builder().titre("Electronics").build());
        Category category2 = categoryRepository.save(Category.builder().titre("Books").build());

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].titre", containsInAnyOrder("Electronics", "Books")));
    }

    @Test
    void should_update_category_successfully() throws Exception {
        // Création d'une catégorie existante
        Category existingCategory = categoryRepository.save(
                Category.builder().titre("Old Title").build()
        );

        CategoryDto updateDto = CategoryDto.builder()
                .titre("Updated Title")
                .build();

        String updateJson = objectMapper.writeValueAsString(updateDto);
        MockMultipartFile updateCategoryJson = new MockMultipartFile(
                "categoryDto",
                "categoryDto",
                "application/json",
                updateJson.getBytes()
        );

        mockMvc.perform(multipart("/api/v1/categories/{id}", existingCategory.getId())
                        .file(updateCategoryJson)
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isCreated());

        // Vérification de la mise à jour
        Category updated = categoryRepository.findById(existingCategory.getId()).orElseThrow();
        assertThat(updated.getTitre()).isEqualTo("Updated Title");
    }

    @Test
    void should_delete_category_successfully() throws Exception {
        // Création d'une catégorie
        Category category = categoryRepository.save(
                Category.builder().titre("To Delete").build()
        );

        mockMvc.perform(delete("/api/v1/categories/{id}", category.getId()))
                .andExpect(status().isNoContent());

        // Vérification de la suppression
        assertThat(categoryRepository.existsById(category.getId())).isFalse();
    }

    @Test
    void should_return_500_when_category_not_found() throws Exception {
        mockMvc.perform(get("/api/v1/categories/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void should_handle_invalid_image_file() throws Exception {
        // Test avec un fichier non-image
        MockMultipartFile invalidFile = createInvalidImageFile();

        mockMvc.perform(multipart("/api/v1/categories")
                        .file(categoryJsonFile)
                        .file(invalidFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void should_handle_empty_image_file() throws Exception {
        // Test avec un fichier vide
        MockMultipartFile emptyFile = createEmptyImageFile();

        mockMvc.perform(multipart("/api/v1/categories")
                        .file(categoryJsonFile)
                        .file(emptyFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest()) // ✅ Changé de isInternalServerError() à isBadRequest()
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Le fichier image ne peut pas être vide"));
    }

    @Test
    void should_handle_missing_image_file() throws Exception {
        mockMvc.perform(multipart("/api/v1/categories")
                        .file(categoryJsonFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_handle_missing_category_dto() throws Exception {
        // Test sans DTO de catégorie
        mockMvc.perform(multipart("/api/v1/categories")
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_update_category_without_image() throws Exception {
        // Création d'une catégorie existante
        Category existingCategory = categoryRepository.save(
                Category.builder().titre("Old Title").build()
        );

        CategoryDto updateDto = CategoryDto.builder()
                .titre("Updated Without Image")
                .build();

        String updateJson = objectMapper.writeValueAsString(updateDto);
        MockMultipartFile updateCategoryJson = new MockMultipartFile(
                "categoryDto", "categoryDto", "application/json", updateJson.getBytes()
        );

        // Test 1: Mise à jour avec fichier vide (peut réussir selon votre logique)
        MockMultipartFile emptyImageFile = createEmptyImageFile();

        mockMvc.perform(multipart("/api/v1/categories/{id}", existingCategory.getId())
                        .file(updateCategoryJson)
                        .file(emptyImageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isCreated());

        // Test 2: Mise à jour avec image valide
        mockMvc.perform(multipart("/api/v1/categories/{id}", existingCategory.getId())
                        .file(updateCategoryJson)
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isCreated());

        Category updated = categoryRepository.findById(existingCategory.getId()).orElseThrow();
        assertThat(updated.getTitre()).isEqualTo("Updated Without Image");
    }

    @Test
    void should_handle_delete_non_existent_category() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/999"))
                .andExpect(status().isInternalServerError());
    }
}