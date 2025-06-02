package com.example.ecommerce.service.integration;

import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // Use test profile
@Transactional
@Rollback
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private ProductDto testProductDto;
    private MultipartFile[] testFiles;

    @BeforeEach
    void setUp() throws IOException {
        // Clean up any existing test data
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Créer un vendeur de test
        User seller = new User();
        seller.setNom("seller");
        seller.setPrenom("seller");
        seller = userRepository.saveAndFlush(seller); // Use saveAndFlush to ensure ID is available

        // Créer le ProductDto avec les données de base
        testProductDto = ProductDto.builder()
                .title("Test Product")
                .description("Test Description")
                .price(99.99)
                .stock(10)
                .sellerId(seller.getId())
                .build();

        // Générer une image JPEG en mémoire
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2d.dispose();

        // Convertir l'image en tableau de bytes JPEG
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        // Créer un MockMultipartFile avec ces bytes d'image
        testFiles = new MultipartFile[]{
                new MockMultipartFile(
                        "file", // field name
                        "test.jpg", // original filename
                        "image/jpeg", // content type
                        imageBytes
                )
        };
    }

    @Test
    void saveProduct_ShouldSaveProductWithImages() throws IOException {
        // Given - verify clean state
        assertThat(productRepository.findAll()).isEmpty();

        // When
        productService.saveProduct(testProductDto, testFiles);

        // Then
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);

        Product savedProduct = products.get(0);
        assertThat(savedProduct.getTitle()).isEqualTo("Test Product");
        assertThat(savedProduct.getImages()).isNotEmpty();
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() throws IOException {
        // Given
        assertThat(productRepository.findAll()).isEmpty();
        productService.saveProduct(testProductDto, testFiles);

        // When
        List<ProductDto> products = productService.getAllProducts();

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getTitle()).isEqualTo("Test Product");
    }

    @Test
    void isProductOutOfStock_ShouldReturnCorrectStatus() throws IOException {
        // Given
        assertThat(productRepository.findAll()).isEmpty();
        productService.saveProduct(testProductDto, testFiles);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);
        Product product = products.get(0);

        // When - produit en stock
        boolean outOfStock1 = productService.isProductOutOfStock(product.getId());

        // Then
        assertThat(outOfStock1).isFalse();

        // When - mettre le stock à 0
        product.setStock(0);
        productRepository.saveAndFlush(product);
        boolean outOfStock2 = productService.isProductOutOfStock(product.getId());

        // Then
        assertThat(outOfStock2).isTrue();
    }
}