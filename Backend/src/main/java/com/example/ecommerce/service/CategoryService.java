package com.example.ecommerce.service;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Image;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.repository.CategoriesRepository;
import com.example.ecommerce.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoriesRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ImageRepository imageRepository;

    public void saveCategory(CategoryDto categoryDto, MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Le fichier image ne peut pas être vide");
        }

        if (!imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image");
        }
        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        ByteArrayOutputStream compressedImage = new ByteArrayOutputStream();
        try (InputStream inputStream = imageFile.getInputStream()) {
            Thumbnails.of(inputStream)
                    .size(500, 600)
                    .outputFormat("jpg")
                    .outputQuality(0.8) // Qualité à 80% pour réduire la taille
                    .toOutputStream(compressedImage);
        } catch (IOException e) {
            throw new IOException("Erreur lors du traitement de l'image: " + e.getMessage());
        }
        Image newImage = Image.builder()
                .category(savedCategory)
                .name(imageFile.getOriginalFilename())
                .type(imageFile.getContentType())
                .image(compressedImage.toByteArray())
                .build();
        Image savedImage = imageRepository.save(newImage);
        savedCategory.setImage(savedImage);
        categoryRepository.save(savedCategory);
    }

    public CategoryDto getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return categoryMapper.toDto(category);
    }

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public void updateCategory(Integer id, CategoryDto categoryDto, MultipartFile imageFile) throws IOException {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        existing.setTitre(categoryDto.getTitre());

        if (imageFile != null && !imageFile.isEmpty()) {
            if (!imageFile.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("Le fichier doit être une image");
            }

            ByteArrayOutputStream compressedImage = new ByteArrayOutputStream();
            try (InputStream inputStream = imageFile.getInputStream()) {
                Thumbnails.of(inputStream)
                        .size(500, 600)
                        .outputFormat("jpg")
                        .outputQuality(0.8)
                        .toOutputStream(compressedImage);
            } catch (IOException e) {
                throw new IOException("Erreur lors du traitement de l'image: " + e.getMessage());
            }

            Image imageToUpdate = existing.getImage();
            if (imageToUpdate == null) {
                imageToUpdate = new Image();
                imageToUpdate.setCategory(existing);
            }

            imageToUpdate.setName(imageFile.getOriginalFilename());
            imageToUpdate.setType(imageFile.getContentType());
            imageToUpdate.setImage(compressedImage.toByteArray());

            imageRepository.save(imageToUpdate);
            existing.setImage(imageToUpdate);
        }

        categoryRepository.save(existing);
    }


    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
