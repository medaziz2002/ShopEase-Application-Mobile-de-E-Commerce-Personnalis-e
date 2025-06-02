package com.example.ecommerce.controller.integration;


import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private ProductDto testProductDto;

    @BeforeEach
    void setUp() {
        testProductDto = ProductDto.builder()
                .title("Produit de test")
                .description("Description de test")
                .price(99.99)
                .stock(10)
                .sellerId(1) // Assurez-vous que cet ID existe
                .build();
    }

    @Test
    void createProduct_ShouldCreateProduct() throws Exception {
        MockMultipartFile productDtoFile = new MockMultipartFile(
                "productDto",
                "",
                "application/json",
                objectMapper.writeValueAsString(testProductDto).getBytes());

        // Créer une vraie image 1x1 pixel au lieu de texte
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        MockMultipartFile imageFile = new MockMultipartFile(
                "pathImages",
                "test.jpg",
                "image/jpeg",
                baos.toByteArray()); // Vraie image au lieu de "test image content".getBytes()

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/products")
                        .file(productDtoFile)
                        .file(imageFile))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllProducts_ShouldReturnProducts() throws Exception {
        Product product = new Product();
        product.setTitle(testProductDto.getTitle());
        product.setPrice(testProductDto.getPrice());
        product.setStock(testProductDto.getStock());
        product.setSeller(userRepository.findById(testProductDto.getSellerId()).orElseThrow());
        productRepository.save(product);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)) // Verify only one product
                .andExpect(jsonPath("$[0].title").value(testProductDto.getTitle()));
    }


    @Test
    void getProduct_ShouldReturnProduct() throws Exception {
        Product product = new Product();
        product.setTitle(testProductDto.getTitle());
        product.setPrice(testProductDto.getPrice());
        product.setStock(testProductDto.getStock());
        product.setSeller(userRepository.findById(testProductDto.getSellerId()).orElseThrow());
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(get("/api/v1/products/{id}", savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedProduct.getId()));
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() throws Exception {
        Product product = new Product();
        product.setTitle(testProductDto.getTitle());
        product.setPrice(testProductDto.getPrice());
        product.setStock(testProductDto.getStock());
        product.setSeller(userRepository.findById(testProductDto.getSellerId()).orElseThrow());
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(delete("/api/v1/products/{id}", savedProduct.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void checkStockStatus_ShouldReturnStatus() throws Exception {
        Product product = new Product();
        product.setTitle(testProductDto.getTitle());
        product.setPrice(testProductDto.getPrice());
        product.setStock(0); // Produit en rupture de stock
        product.setSeller(userRepository.findById(testProductDto.getSellerId()).orElseThrow());
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(get("/api/v1/products/{id}/status", savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}