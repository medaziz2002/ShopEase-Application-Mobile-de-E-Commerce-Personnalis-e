package com.example.ecommerce.dto;

import com.example.ecommerce.entity.User;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private String telephone;
    private ImageDto imageDto;
    private String fcmToken;

}
