package com.example.ecommerce.service.unitaire;

import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.SizeDto;
import com.example.ecommerce.dto.WeightDto;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.ImageRepository;
import com.example.ecommerce.repository.NotificationLogRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductMapper productMapper;


    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private ImageRepository productImagesRepository;

    @InjectMocks
    private ProductService productService;


    @Test
    void saveProduct_ShouldSaveWithImages() throws IOException {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setSellerId(1);
        dto.setTitle("Test Product");

        Product productEntity = new Product();
        productEntity.setId(10);
        productEntity.setTitle("Test Product");

        Product savedProduct = new Product();
        savedProduct.setId(10);
        savedProduct.setTitle("Test Product");
        savedProduct.setImages(Collections.emptyList());

        when(productMapper.toEntity(dto)).thenReturn(productEntity);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));

        // Mock save image repository
        when(productImagesRepository.save(any(Image.class))).thenAnswer(invocation -> {
            Image img = invocation.getArgument(0);
            img.setIdImage(100); // Simuler l'id de l'image créée
            return img;
        });

        // Créer une image JPEG valide minimale (1x1 pixel)
        byte[] validJpegBytes = createMinimalValidJpeg();
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", validJpegBytes);

        ArgumentCaptor<Image> imageCaptor = ArgumentCaptor.forClass(Image.class);

        // Act
        productService.saveProduct(dto, new MockMultipartFile[]{file});

        // Assert
        verify(productMapper).toEntity(dto);
        verify(productRepository, times(2)).save(any(Product.class)); // product + updated product with images
        verify(productImagesRepository).save(imageCaptor.capture());
        verify(notificationLogRepository).save(any(NotificationLog.class));
        verify(userRepository).findById(1);

        Image savedImage = imageCaptor.getValue();
        assertEquals("image.jpg", savedImage.getName());
        assertEquals("image/jpeg", savedImage.getType());
        assertNotNull(savedImage.getImage());
        // Vérifier que l'image a été traitée (les bytes seront différents après le traitement Thumbnailator)
        assertTrue(savedImage.getImage().length > 0);
        assertEquals(savedProduct.getId(), savedImage.getProduct().getId());
    }

    /**
     * Crée une image JPEG valide minimale pour les tests
     */
    private byte[] createMinimalValidJpeg() {
        try {
            // Créer une image BufferedImage 1x1 pixel
            BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            bufferedImage.setRGB(0, 0, 0xFF0000); // Pixel rouge

            // Convertir en bytes JPEG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la création de l'image de test", e);
        }
    }


    @Test
    void isProductOutOfStock_ShouldReturnTrueWhenZeroStock() {
        // Arrange
        Integer productId = 1;
        Product product = new Product();
        product.setStock(0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        boolean result = productService.isProductOutOfStock(productId);

        // Assert
        assertTrue(result);
    }

    @Test
    void getAllSizes_ShouldReturnAllSizeTypes() {
        // Act
        SizeDto result = productService.getAllSizes();

        // Assert
        assertFalse(result.getSizes().isEmpty());
    }
}