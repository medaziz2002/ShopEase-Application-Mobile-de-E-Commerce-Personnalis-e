package com.example.ecommerce.service.unitaire;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Image;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.repository.CategoriesRepository;
import com.example.ecommerce.repository.ImageRepository;
import com.example.ecommerce.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoriesRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private CategoryService categoryService;

    /**
     * Helper method to create a valid image byte array for testing
     */
    private byte[] createValidImageBytes() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    @Test
    void saveCategory_ShouldSaveWithCompressedImage() throws IOException {
        // Arrange
        CategoryDto dto = new CategoryDto();
        byte[] validImageBytes = createValidImageBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", validImageBytes);

        Category category = new Category();
        when(categoryMapper.toEntity(dto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        categoryService.saveCategory(dto, file);

        // Assert
        verify(imageRepository).save(any(Image.class));
        verify(categoryRepository, times(2)).save(category); // Once for initial save, once after image
    }

    @Test
    void getCategoryById_ShouldReturnDto() {
        // Arrange
        Integer id = 1;
        Category category = new Category();
        CategoryDto expectedDto = new CategoryDto();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        // Act
        CategoryDto result = categoryService.getCategoryById(id);

        // Assert
        assertEquals(expectedDto, result);
    }

    @Test
    void getAllCategories_ShouldReturnList() {
        // Arrange
        Category category1 = new Category();
        Category category2 = new Category();
        CategoryDto dto1 = new CategoryDto();
        CategoryDto dto2 = new CategoryDto();

        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));
        when(categoryMapper.toDto(category1)).thenReturn(dto1);
        when(categoryMapper.toDto(category2)).thenReturn(dto2);

        // Act
        List<CategoryDto> result = categoryService.getAllCategories();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void updateCategory_ShouldUpdateExisting() throws IOException {
        // Arrange
        Integer id = 1;
        CategoryDto dto = new CategoryDto();
        dto.setTitre("New Title");
        byte[] validImageBytes = createValidImageBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", validImageBytes);

        Category existing = new Category();
        existing.setTitre("Old Title");
        existing.setImage(new Image());

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));

        // Act
        categoryService.updateCategory(id, dto, file);

        // Assert
        assertEquals("New Title", existing.getTitre());
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void deleteCategory_ShouldCallRepository() {
        // Arrange
        Integer id = 1;
        when(categoryRepository.existsById(id)).thenReturn(true);

        // Act
        categoryService.deleteCategory(id);

        // Assert
        verify(categoryRepository).deleteById(id);
    }

    @Test
    void saveCategory_WithNullFile_ShouldThrowException() {
        // Arrange
        CategoryDto dto = new CategoryDto();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.saveCategory(dto, null);
        });

        assertEquals("Le fichier image ne peut pas être vide", exception.getMessage());

        // Verify no repository operations were performed
        verify(imageRepository, never()).save(any(Image.class));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void saveCategory_WithEmptyFile_ShouldThrowException() {
        // Arrange
        CategoryDto dto = new CategoryDto();
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.saveCategory(dto, emptyFile);
        });

        assertEquals("Le fichier image ne peut pas être vide", exception.getMessage());

        // Verify no repository operations were performed
        verify(imageRepository, never()).save(any(Image.class));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WithEmptyFile_ShouldSkipImageUpdate() throws IOException {
        // Arrange
        Integer id = 1;
        CategoryDto dto = new CategoryDto();
        dto.setTitre("New Title");
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);

        Category existing = new Category();
        existing.setTitre("Old Title");
        existing.setImage(new Image());
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));

        // Act
        categoryService.updateCategory(id, dto, emptyFile);

        // Assert
        assertEquals("New Title", existing.getTitre());
        // Verify no image save operation (empty file is ignored)
        verify(imageRepository, never()).save(any(Image.class));
    }

    @Test
    void updateCategory_WithNullFile_ShouldSkipImageUpdate() throws IOException {
        // Arrange
        Integer id = 1;
        CategoryDto dto = new CategoryDto();
        dto.setTitre("New Title");

        Category existing = new Category();
        existing.setTitre("Old Title");
        existing.setImage(new Image());
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));

        // Act
        categoryService.updateCategory(id, dto, null);

        // Assert
        assertEquals("New Title", existing.getTitre());
        // Verify no image save operation (null file is ignored)
        verify(imageRepository, never()).save(any(Image.class));
    }
}