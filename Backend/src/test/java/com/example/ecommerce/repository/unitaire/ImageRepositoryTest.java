package com.example.ecommerce.repository.unitaire;

import com.example.ecommerce.entity.Image;
import com.example.ecommerce.repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ImageRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;

    @Test
    void saveImage_ShouldPersist() {
        Image image = new Image();
        image.setName("test.jpg");
        image.setType("image/jpeg");

        Image saved = imageRepository.save(image);

        assertNotNull(saved.getIdImage());
        assertEquals("test.jpg", saved.getName());
    }
}