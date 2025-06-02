package com.example.ecommerce.controller.unitaire;

import com.example.ecommerce.controller.ProductController;
import com.example.ecommerce.dto.*;
import com.example.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void createProduct_WithImages_ShouldCallService() throws IOException {
        ProductDto dto = new ProductDto();
        MockMultipartFile[] files = {
                new MockMultipartFile("file1", "test1.jpg", "image/jpeg", "test1".getBytes()),
                new MockMultipartFile("file2", "test2.jpg", "image/jpeg", "test2".getBytes())
        };

        productController.createProduct(dto, files);

        verify(productService).saveProduct(dto, files);
    }

    @Test
    void deleteProduct_ShouldReturnNoContent() {
        ResponseEntity<Void> response = productController.deleteProduct(1);

        assertEquals(204, response.getStatusCodeValue());
        verify(productService).deleteProduct(1);
    }

    @Test
    void getAllProducts_ShouldReturnList() {
        ProductDto dto = new ProductDto();
        when(productService.getAllProducts()).thenReturn(List.of(dto));

        ResponseEntity<List<ProductDto>> response = productController.getAllProducts();

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getProduct_ShouldReturnProduct() {
        ProductDto dto = new ProductDto();
        when(productService.getProductById(1)).thenReturn(dto);

        ResponseEntity<ProductDto> response = productController.getProduct(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getSize_ShouldReturnSizeDto() {
        SizeDto dto = new SizeDto();
        when(productService.getAllSizes()).thenReturn(dto);

        ResponseEntity<SizeDto> response = productController.getSize();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getWeight_ShouldReturnWeightDto() {
        WeightDto dto = new WeightDto();
        when(productService.getAllWeights()).thenReturn(dto);

        ResponseEntity<WeightDto> response = productController.getWeight();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void updateProduct_WithImages_ShouldCallService() throws IOException {
        ProductDto dto = new ProductDto();
        MockMultipartFile[] files = {
                new MockMultipartFile("file1", "test1.jpg", "image/jpeg", "test1".getBytes())
        };
        List<Integer> imagesToDelete = List.of(1, 2);

        productController.updateProduct(1, dto, files, imagesToDelete);

        verify(productService).updateProduct(1, dto, files, imagesToDelete);
    }

    @Test
    void checkStockStatus_ShouldReturnBoolean() {
        when(productService.isProductOutOfStock(1)).thenReturn(true);

        ResponseEntity<Boolean> response = productController.checkStockStatus(1);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
    }
}