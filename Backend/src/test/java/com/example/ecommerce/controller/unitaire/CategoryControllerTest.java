package com.example.ecommerce.controller.unitaire;

import com.example.ecommerce.controller.CategoryController;
import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void createCategory_ShouldCallService() throws Exception {
        CategoryDto dto = new CategoryDto();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());

        categoryController.createCategory(dto, file);

        verify(categoryService).saveCategory(dto, file);
    }

    @Test
    void getCategory_ShouldReturnCategory() {
        CategoryDto expected = new CategoryDto();
        when(categoryService.getCategoryById(1)).thenReturn(expected);

        ResponseEntity<CategoryDto> response = categoryController.getCategory(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getAllCategories_ShouldReturnList() {
        when(categoryService.getAllCategories()).thenReturn(List.of(new CategoryDto()));

        ResponseEntity<List<CategoryDto>> response = categoryController.getAllCategories();

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void updateCategory_ShouldCallService() throws Exception {
        CategoryDto dto = new CategoryDto();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());

        categoryController.updateCategory(1, dto, file);

        verify(categoryService).updateCategory(1, dto, file);
    }

    @Test
    void deleteCategory_ShouldCallService() {
        categoryController.deleteCategory(1);
        verify(categoryService).deleteCategory(1);
    }
}