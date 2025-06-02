package com.example.ecommerce.repository.integration;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Image;
import com.example.ecommerce.repository.CategoriesRepository;
import com.example.ecommerce.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CategoriesRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ImageRepository imageRepository;

    private Category category1;
    private Category category2;
    private Image image1;
    private Image image2;

    @BeforeEach
    void setUp() {
        // Nettoyage de la base de données
        categoriesRepository.deleteAll();
        imageRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Création des données de test
        category1 = Category.builder()
                .titre("Electronics")
                .build();

        category2 = Category.builder()
                .titre("Books")
                .build();

        image1 = Image.builder()
                .name("electronics.jpg")
                .type("image/jpeg")
                .image("fake image 1".getBytes())
                .build();

        image2 = Image.builder()
                .name("books.jpg")
                .type("image/jpeg")
                .image("fake image 2".getBytes())
                .build();
    }

    @Test
    void should_save_category_successfully() {
        Category savedCategory = categoriesRepository.save(category1);

        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getTitre()).isEqualTo("Electronics");
    }

    @Test
    void should_save_category_with_image_successfully() {
        // Sauvegarde de la catégorie d'abord
        Category savedCategory = categoriesRepository.save(category1);
        entityManager.flush();

        // Association et sauvegarde de l'image
        image1.setCategory(savedCategory);
        Image savedImage = imageRepository.save(image1);
        entityManager.flush();

        // Mise à jour de la catégorie avec l'image
        savedCategory.setImage(savedImage);
        Category updatedCategory = categoriesRepository.save(savedCategory);
        entityManager.flush();
        entityManager.clear();

        // Vérification
        Category retrievedCategory = categoriesRepository.findById(updatedCategory.getId()).orElseThrow();
        assertThat(retrievedCategory.getImage()).isNotNull();
        assertThat(retrievedCategory.getImage().getName()).isEqualTo("electronics.jpg");
        assertThat(retrievedCategory.getImage().getCategory().getId()).isEqualTo(retrievedCategory.getId());
    }

    @Test
    void should_find_category_by_id_successfully() {
        Category savedCategory = categoriesRepository.save(category1);
        entityManager.flush();
        entityManager.clear();

        Optional<Category> found = categoriesRepository.findById(savedCategory.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitre()).isEqualTo("Electronics");
    }

    @Test
    void should_return_empty_when_category_not_found() {
        Optional<Category> found = categoriesRepository.findById(999);

        assertThat(found).isEmpty();
    }

    @Test
    void should_find_all_categories_successfully() {
        categoriesRepository.save(category1);
        categoriesRepository.save(category2);
        entityManager.flush();

        List<Category> categories = categoriesRepository.findAll();

        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(Category::getTitre)
                .containsExactlyInAnyOrder("Electronics", "Books");
    }

    @Test
    void should_update_category_successfully() {
        Category savedCategory = categoriesRepository.save(category1);
        entityManager.flush();
        entityManager.clear();

        // Récupération et modification
        Category categoryToUpdate = categoriesRepository.findById(savedCategory.getId()).orElseThrow();
        categoryToUpdate.setTitre("Updated Electronics");
        Category updatedCategory = categoriesRepository.save(categoryToUpdate);
        entityManager.flush();

        assertThat(updatedCategory.getTitre()).isEqualTo("Updated Electronics");
    }

    @Test
    void should_delete_category_by_id_successfully() {
        Category savedCategory = categoriesRepository.save(category1);
        entityManager.flush();

        categoriesRepository.deleteById(savedCategory.getId());
        entityManager.flush();

        Optional<Category> deleted = categoriesRepository.findById(savedCategory.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void should_check_category_existence_successfully() {
        Category savedCategory = categoriesRepository.save(category1);
        entityManager.flush();

        boolean exists = categoriesRepository.existsById(savedCategory.getId());
        boolean notExists = categoriesRepository.existsById(999);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void should_delete_all_categories_successfully() {
        categoriesRepository.save(category1);
        categoriesRepository.save(category2);
        entityManager.flush();

        categoriesRepository.deleteAll();
        entityManager.flush();

        List<Category> categories = categoriesRepository.findAll();
        assertThat(categories).isEmpty();
    }

    @Test
    void should_count_categories_successfully() {
        categoriesRepository.save(category1);
        categoriesRepository.save(category2);
        entityManager.flush();

        long count = categoriesRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void should_handle_cascade_operations_with_image() {
        // Sauvegarde de la catégorie avec image
        Category savedCategory = categoriesRepository.save(category1);
        entityManager.flush();

        image1.setCategory(savedCategory);
        Image savedImage = imageRepository.save(image1);
        entityManager.flush();

        savedCategory.setImage(savedImage);
        categoriesRepository.save(savedCategory);
        entityManager.flush();
        entityManager.clear();

        // Vérification que la suppression de la catégorie gère correctement l'image
        // (selon la configuration de cascade dans vos entités)
        categoriesRepository.deleteById(savedCategory.getId());
        entityManager.flush();

        Optional<Category> deletedCategory = categoriesRepository.findById(savedCategory.getId());
        assertThat(deletedCategory).isEmpty();
    }
}