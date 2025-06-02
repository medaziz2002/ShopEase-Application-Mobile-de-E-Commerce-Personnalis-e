package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;





    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCategory(  @RequestPart("categoryDto") CategoryDto categoryDto, @RequestPart("pathImage") MultipartFile file)throws IOException {
         categoryService.saveCategory(categoryDto,file);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Integer id)  {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        System.out.println("je suis dans la liste de category");
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void  updateCategory(@PathVariable Integer id, @RequestPart("categoryDto") CategoryDto categoryDto, @RequestPart("pathImage") MultipartFile file) throws IOException  {
         categoryService.updateCategory(id, categoryDto,file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Integer id)  {
        categoryService.deleteCategory(id);
    }


}