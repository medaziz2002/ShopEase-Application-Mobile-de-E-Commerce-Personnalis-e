package com.example.ecommerce.mapper;
import org.springframework.stereotype.Component;
import com.example.ecommerce.dto.ImageDto;
import com.example.ecommerce.entity.Image;
import java.util.Base64;

@Component
public class ImageMapper {

    public ImageDto toDto(Image image) {
        if (image == null) {
            return null;
        }

        return ImageDto.builder()
                .id(image.getIdImage())
                .name(image.getName())
                .type(image.getType())
                .imageBase64(image.getImage() != null ?
                        Base64.getEncoder().encodeToString(image.getImage()) : null)
                .build();
    }

    public Image toEntity(ImageDto dto) {
        if (dto == null) {
            return null;
        }

        return Image.builder()
                .idImage(dto.getId())
                .name(dto.getName())
                .type(dto.getType())
                .image(dto.getImageBase64() != null ?
                        Base64.getDecoder().decode(dto.getImageBase64()) : null)
                .build();
    }
}
