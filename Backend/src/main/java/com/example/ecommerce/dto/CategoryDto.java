package com.example.ecommerce.dto;

import lombok.*;





@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Integer id;
    private String titre;
    private ImageDto imageDto;
}
