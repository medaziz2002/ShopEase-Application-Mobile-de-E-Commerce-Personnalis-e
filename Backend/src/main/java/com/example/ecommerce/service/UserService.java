package com.example.ecommerce.service;


import com.example.ecommerce.dto.UserRequest;
import com.example.ecommerce.entity.Image;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.repository.ImageRepository;
import com.example.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;


    @Transactional
    public ResponseEntity<?> save(UserRequest userDto, MultipartFile imageFile) throws IOException {
        // Validation
        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Le fichier image ne peut pas être vide");
        }
        if (!imageFile.getContentType().startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le fichier doit être une image");
        }
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email existe déjà");
        }

        // 1. Compression de l'image en premier
        byte[] compressedImageBytes;
        try (InputStream inputStream = imageFile.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(inputStream)
                    .size(500, 600)
                    .outputFormat("jpg")
                    .outputQuality(0.8)
                    .toOutputStream(outputStream);
            compressedImageBytes = outputStream.toByteArray();
        }

        // 2. Création de l'image avec l'utilisateur
        Image image = Image.builder()
                .name(imageFile.getOriginalFilename())
                .type(imageFile.getContentType())
                .image(compressedImageBytes)
                .build();

        // 3. Création de l'utilisateur avec l'image
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = User.builder()
                .nom(userDto.getNom())
                .prenom(userDto.getPrenom())
                .telephone(userDto.getTelephone())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole())
                .image(image)
                .build();

        // 4. Association bidirectionnelle
        image.setUser(user);

        // 5. UNE SEULE sauvegarde (la cascade s'occupe de tout)
        User savedUser = userRepository.save(user);

        // Retourner l'utilisateur créé avec le code de statut HTTP 201
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(savedUser));
    }






    public UserRequest getUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec l'email : " + email);
        }

        return userMapper.toDto(optionalUser.get());
    }



    public List<UserRequest> getUsers() {
        List<User> users = userRepository.findAll(); // Récupère tous les utilisateurs

        return users.stream()
                .map(userMapper::toDto) // Convertit chaque User en UserRequest
                .collect(Collectors.toList()); // Retourne une List<UserRequest>
    }

    @Transactional
    public void updateFcmToken(Integer userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'ID: " + userId));
        user.setFcmToken(token);
        userRepository.save(user);
    }

    public UserRequest getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));
        return userMapper.toDto(user);
    }


    @Transactional
    public ResponseEntity<?> modifier(UserRequest userDto, MultipartFile imageFile) throws IOException {
        Optional<User> optionalUser = userRepository.findById(userDto.getId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }
        User user = optionalUser.get();

        // Validation du fichier image, si un fichier est envoyé
        if (imageFile != null && !imageFile.isEmpty()) {
            if (!imageFile.getContentType().startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le fichier doit être une image");
            }
            byte[] compressedImageBytes;
            try (InputStream inputStream = imageFile.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                Thumbnails.of(inputStream)
                        .size(500, 600)
                        .outputFormat("jpg")
                        .outputQuality(0.8)
                        .toOutputStream(outputStream);
                compressedImageBytes = outputStream.toByteArray();
            }
            if (user.getImage() != null) {
                Image existingImage = user.getImage();
                existingImage.setImage(compressedImageBytes);
                existingImage.setName(imageFile.getOriginalFilename());
                existingImage.setType(imageFile.getContentType());
            } else {
                Image image = Image.builder()
                        .name(imageFile.getOriginalFilename())
                        .type(imageFile.getContentType())
                        .image(compressedImageBytes)
                        .build();
                image.setUser(user);
                user.setImage(image);
            }
        }

        user.setNom(userDto.getNom());
        user.setPrenom(userDto.getPrenom());
        user.setTelephone(userDto.getTelephone());

        if (!user.getEmail().equals(userDto.getEmail()) && userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email existe déjà");
        }
        user.setEmail(userDto.getEmail());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(user));
    }



}
