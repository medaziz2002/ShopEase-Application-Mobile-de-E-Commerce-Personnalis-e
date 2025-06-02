package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.SizeDto;
import com.example.ecommerce.dto.WeightDto;
import com.example.ecommerce.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;



@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createProduct(
            @RequestPart("productDto") ProductDto productDto,
            @RequestPart("pathImages") MultipartFile[] files
    ) throws IOException {
        productService.saveProduct(productDto, files);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {

        return ResponseEntity.ok(productService.getAllProducts());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Integer id)  {
        return ResponseEntity.ok(productService.getProductById(id));
    }


    @GetMapping("/sizes")
    public ResponseEntity<SizeDto> getSize() {
        SizeDto sizeDto = productService.getAllSizes();
        return ResponseEntity.ok(sizeDto);
    }

    @GetMapping("/weights")
    public ResponseEntity<WeightDto> getWeight() {
        WeightDto weightDto = productService.getAllWeights();
        return ResponseEntity.ok(weightDto);
    }


    @PutMapping("/{id}/with-images")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateProduct(
            @PathVariable Integer id,
            @RequestPart("productDto") ProductDto productDto,
            @RequestPart(value = "pathImages", required = false) MultipartFile[] files,
            @RequestPart(value = "imagesToDelete", required = false) List<Integer> imagesToDelete
    ) throws IOException {
        productService.updateProduct(id, productDto, files, imagesToDelete);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Boolean> checkStockStatus(@PathVariable Integer id) {
        boolean isOutOfStock = productService.isProductOutOfStock(id);
        return ResponseEntity.ok(isOutOfStock);
    }



    @GetMapping("/top-rated")
    public ResponseEntity<List<ProductDto>> getTopRatedProducts(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(productService.getTopRatedProducts(limit));
    }




}