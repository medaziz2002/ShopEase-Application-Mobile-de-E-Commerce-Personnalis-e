package com.example.ecommerce.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishListDto {
    private Integer id;
    private Integer productId;
    private Integer userId;
    private UserRequest userRequest;
    private ProductDto productDto;
}

