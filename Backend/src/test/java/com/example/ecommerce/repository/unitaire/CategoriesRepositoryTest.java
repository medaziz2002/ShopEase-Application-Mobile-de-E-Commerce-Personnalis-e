package com.example.ecommerce.repository.unitaire;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoriesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoriesRepositoryTest {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Test
    void saveCategory_ShouldPersist() {
        Category category = new Category();
        category.setTitre("nourriture");

        Category saved = categoriesRepository.save(category);

        assertNotNull(saved.getId());
        assertEquals("nourriture", saved.getTitre());
    }

    @Test
    void findById_ShouldReturnCategory() {
        Category category = new Category();
        category.setTitre("Jouets");
        Category saved = categoriesRepository.save(category);

        Category found = categoriesRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("Jouets", found.getTitre());
    }
}