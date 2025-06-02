package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.dto.ImageDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    private final ImageMapper imageMapper;

    public CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }

        ImageDto imageDto = null;
        if (category.getImage() != null) {
            imageDto = imageMapper.toDto(category.getImage());
        }

        return CategoryDto.builder()
                .id(category.getId())
                .titre(category.getTitre())
                .imageDto(imageDto)  // Ajout de l'imageDto dans le DTO
                .build();
    }

    public Category toEntity(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }

        Category.CategoryBuilder categoryBuilder = Category.builder()
                .id(categoryDto.getId())
                .titre(categoryDto.getTitre());

        if (categoryDto.getImageDto() != null) {
            Image image = imageMapper.toEntity(categoryDto.getImageDto());
            categoryBuilder.image(image);
        }

        return categoryBuilder.build();
    }
}