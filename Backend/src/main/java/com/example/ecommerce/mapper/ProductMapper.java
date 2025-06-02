package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Image;
import com.example.ecommerce.entity.SizeType;
import com.example.ecommerce.entity.WeightType;
import com.example.ecommerce.repository.CategoriesRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryMapper categoryMapper;
    private final ImageMapper imageMapper;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;

    // Conversion de l'entité Product en ProductDto
    public ProductDto toDTO(Product product) {
        if (product == null) {
            return null;
        }

        List<String> sizes = Optional.ofNullable(product.getSize())
                .orElse(Collections.emptyList());

        List<String> weights = Optional.ofNullable(product.getWeight())
                .orElse(Collections.emptyList());

        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPercentage(product.getDiscountPercentage())
                .rating(product.getRating())
                .stock(product.getStock())
                .size(sizes)  // Ajout des tailles sous forme de String
                .weight(weights)  // Ajout des poids sous forme de String
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryDto(product.getCategory() != null ? categoryMapper.toDto(product.getCategory()) : null)
                .images(product.getImages() != null ?
                        product.getImages().stream()
                                .map(imageMapper::toDto)
                                .collect(Collectors.toList())
                        : Collections.emptyList())
                .sellerId(product.getSeller() != null ? product.getSeller().getId() : null)
                .sellerName(product.getSeller() != null ?
                        product.getSeller().getNom() + " " + product.getSeller().getPrenom() : null)
                .sellerTelephone(product.getSeller() != null ? product.getSeller().getTelephone() : null)
                .build();
    }


    // Conversion du ProductDto en entité Product
    public Product toEntity(ProductDto dto) {
        if (dto == null) {
            return null;
        }

        Product product = Product.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .discountPercentage(dto.getDiscountPercentage())
                .rating(dto.getRating())
                .stock(dto.getStock())
                .size(dto.getSize() != null ? new ArrayList<>(dto.getSize()) : new ArrayList<>())
                .weight(dto.getWeight() != null ? new ArrayList<>(dto.getWeight()) : new ArrayList<>())
                .category(dto.getCategoryId() != null ? categoriesRepository.findById(dto.getCategoryId()).orElse(null) : null)
                .seller(dto.getSellerId() != null ? userRepository.findById(dto.getSellerId()).orElse(null) : null)
                .build();

        if (dto.getImages() != null) {
            List<Image> images = dto.getImages().stream()
                    .map(imageMapper::toEntity)
                    .peek(image -> image.setProduct(product))
                    .collect(Collectors.toList());
            product.setImages(images);
        }

        return product;
    }



}
