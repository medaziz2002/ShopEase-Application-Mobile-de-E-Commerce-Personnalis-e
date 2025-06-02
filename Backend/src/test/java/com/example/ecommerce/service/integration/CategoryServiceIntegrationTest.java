package com.example.ecommerce.service.integration;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoriesRepository;
import com.example.ecommerce.repository.ImageRepository;
import com.example.ecommerce.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoriesRepository categoryRepository;

    @Autowired
    private ImageRepository imageRepository;

    private CategoryDto categoryDto;
    private MultipartFile imageFile;

    @BeforeEach
    void setUp() throws IOException {
        categoryRepository.deleteAll();
        imageRepository.deleteAll();

        categoryDto = CategoryDto.builder()
                .titre("Test Category")
                .build();

        // Création d'un fichier image mock réaliste et valide
        imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                createValidTestImageBytes()
        );
    }

    private byte[] createValidTestImageBytes() throws IOException {
        // Créer une vraie image JPEG valide de 10x10 pixels
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Remplir avec une couleur de fond
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 10, 10);

        // Ajouter quelques pixels de couleur différente pour rendre l'image plus réaliste
        g2d.setColor(Color.WHITE);
        g2d.fillRect(2, 2, 6, 6);

        g2d.dispose();

        // Convertir en bytes JPEG
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    @Test
    void should_save_category_with_image_successfully() throws IOException {
        categoryService.saveCategory(categoryDto, imageFile);

        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(1);

        Category savedCategory = categories.get(0);
        assertThat(savedCategory.getTitre()).isEqualTo("Test Category");
        assertThat(savedCategory.getImage()).isNotNull();
        assertThat(savedCategory.getImage().getName()).isEqualTo("test.jpg");
    }

    @Test
    void should_throw_exception_when_image_file_is_null() {
        assertThatThrownBy(() -> categoryService.saveCategory(categoryDto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le fichier image ne peut pas être vide");
    }

    @Test
    void should_throw_exception_when_image_file_is_empty() {
        MultipartFile emptyFile = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> categoryService.saveCategory(categoryDto, emptyFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le fichier image ne peut pas être vide");
    }

    @Test
    void should_throw_exception_when_file_is_not_image() {
        MultipartFile textFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        assertThatThrownBy(() -> categoryService.saveCategory(categoryDto, textFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le fichier doit être une image");
    }

    @Test
    void should_get_category_by_id_successfully() throws IOException {
        categoryService.saveCategory(categoryDto, imageFile);
        Category savedCategory = categoryRepository.findAll().get(0);

        CategoryDto result = categoryService.getCategoryById(savedCategory.getId());

        assertThat(result).isNotNull();
        assertThat(result.getTitre()).isEqualTo("Test Category");
        assertThat(result.getId()).isEqualTo(savedCategory.getId());
    }

    @Test
    void should_throw_exception_when_category_not_found() {
        assertThatThrownBy(() -> categoryService.getCategoryById(999))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found with id: 999");
    }

    @Test
    void should_get_all_categories_successfully() throws IOException {
        // Création de plusieurs catégories
        CategoryDto category1 = CategoryDto.builder().titre("Category 1").build();
        CategoryDto category2 = CategoryDto.builder().titre("Category 2").build();

        // Créer des images différentes pour chaque catégorie
        MultipartFile imageFile1 = new MockMultipartFile(
                "image1",
                "test1.jpg",
                "image/jpeg",
                createValidTestImageBytes()
        );

        MultipartFile imageFile2 = new MockMultipartFile(
                "image2",
                "test2.jpg",
                "image/jpeg",
                createValidTestImageBytes()
        );

        categoryService.saveCategory(category1, imageFile1);
        categoryService.saveCategory(category2, imageFile2);

        List<CategoryDto> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CategoryDto::getTitre)
                .containsExactlyInAnyOrder("Category 1", "Category 2");
    }

    @Test
    void should_update_category_successfully() throws IOException {
        // Création d'une catégorie initiale
        categoryService.saveCategory(categoryDto, imageFile);
        Category savedCategory = categoryRepository.findAll().get(0);

        // Mise à jour
        CategoryDto updateDto = CategoryDto.builder()
                .titre("Updated Category")
                .build();

        MultipartFile newImageFile = new MockMultipartFile(
                "newImage",
                "updated.jpg",
                "image/jpeg",
                createValidTestImageBytes()
        );

        categoryService.updateCategory(savedCategory.getId(), updateDto, newImageFile);

        // Vérification
        Category updatedCategory = categoryRepository.findById(savedCategory.getId()).orElseThrow();
        assertThat(updatedCategory.getTitre()).isEqualTo("Updated Category");
        assertThat(updatedCategory.getImage().getName()).isEqualTo("updated.jpg");
    }

    @Test
    void should_update_category_without_image() throws IOException {
        // Création d'une catégorie initiale
        categoryService.saveCategory(categoryDto, imageFile);
        Category savedCategory = categoryRepository.findAll().get(0);
        String originalImageName = savedCategory.getImage().getName();

        // Mise à jour sans image
        CategoryDto updateDto = CategoryDto.builder()
                .titre("Updated Without Image")
                .build();

        categoryService.updateCategory(savedCategory.getId(), updateDto, null);

        // Vérification
        Category updatedCategory = categoryRepository.findById(savedCategory.getId()).orElseThrow();
        assertThat(updatedCategory.getTitre()).isEqualTo("Updated Without Image");
        assertThat(updatedCategory.getImage().getName()).isEqualTo(originalImageName); // Image inchangée
    }

    @Test
    void should_delete_category_successfully() throws IOException {
        categoryService.saveCategory(categoryDto, imageFile);
        Category savedCategory = categoryRepository.findAll().get(0);

        categoryService.deleteCategory(savedCategory.getId());

        assertThat(categoryRepository.existsById(savedCategory.getId())).isFalse();
    }

    @Test
    void should_throw_exception_when_deleting_non_existent_category() {
        assertThatThrownBy(() -> categoryService.deleteCategory(999))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found with id: 999");
    }
}