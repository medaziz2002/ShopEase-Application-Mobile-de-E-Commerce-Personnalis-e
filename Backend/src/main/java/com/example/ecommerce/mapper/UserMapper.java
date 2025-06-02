package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.ImageDto;
import com.example.ecommerce.dto.UserRequest;
import com.example.ecommerce.entity.Image;
import com.example.ecommerce.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ImageMapper imageMapper;

    public User toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }

        Image image = null;
        if (request.getImageDto() != null) {
            image = imageMapper.toEntity(request.getImageDto());
        }

        return User.builder()
                .id(request.getId())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .telephone(request.getTelephone())
                .image(image)
                .build();
    }


    public UserRequest toDto(User user) {
        if (user == null) {
            return null;
        }
        ImageDto imageDto = null;
        if (user.getImage() != null) {
            imageDto = imageMapper.toDto(user.getImage());
        }

        return UserRequest.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                 .imageDto(imageDto)
                .telephone(user.getTelephone())
                .email(user.getEmail())
                .fcmToken(user.getFcmToken())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }



}
