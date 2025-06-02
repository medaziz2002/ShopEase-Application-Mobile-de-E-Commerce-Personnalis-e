package com.example.ecommerce.service.integration;

import com.example.ecommerce.dto.UserRequest;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserRequest testUserRequest;
    private MockMultipartFile testImageFile;

    @BeforeEach
    void setUp() throws Exception {
        testUserRequest = UserRequest.builder()
                .nom("Test")
                .prenom("User")
                .email("test@example.com")
                .password("password")
                .role("Client")
                .telephone("1234567890")
                .build();

        // Create a real test image
        testImageFile = createTestImageFile();
    }

    private MockMultipartFile createTestImageFile() throws IOException {
        // Create a simple test image
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 100, 100);
        g2d.setColor(Color.WHITE);
        g2d.drawString("TEST", 35, 55);
        g2d.dispose();

        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        return new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                imageBytes
        );
    }

    @Test
    void save_ShouldCreateUserWithImage() throws IOException {
        ResponseEntity<?> response = userService.save(testUserRequest, testImageFile);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        UserRequest createdUser = (UserRequest) response.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(testUserRequest.getEmail());

        User user = userRepository.findByEmail(testUserRequest.getEmail()).orElseThrow();
        assertThat(user.getImage()).isNotNull();
    }

    @Test
    void save_ShouldRejectDuplicateEmail() throws IOException {
        userService.save(testUserRequest, testImageFile);

        ResponseEntity<?> response = userService.save(testUserRequest, testImageFile);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("Email existe déjà");
    }

    @Test
    void getUserByEmail_ShouldReturnUser() throws IOException {
        userService.save(testUserRequest, testImageFile);

        UserRequest user = userService.getUserByEmail(testUserRequest.getEmail());
        assertThat(user.getEmail()).isEqualTo(testUserRequest.getEmail());
    }

    @Test
    void getUserByEmail_ShouldThrowWhenNotFound() {
        assertThrows(RuntimeException.class, () -> {
            userService.getUserByEmail("nonexistent@example.com");
        });
    }

    @Test
    @Transactional
    @Rollback
    void getUsers_ShouldReturnAllUsers() throws IOException {
        // S'assurer qu'on part d'un état propre
        long initialCount = userRepository.count();

        // Créer le premier utilisateur
        userService.save(testUserRequest, testImageFile);

        // Créer le deuxième utilisateur avec un email différent
        UserRequest anotherUser = UserRequest.builder()
                .nom("Another")
                .prenom("User")
                .email("another@example.com")
                .password("password")
                .role("Client")
                .telephone("0987654321")
                .build();
        userService.save(anotherUser, createTestImageFile());

        // Vérifier qu'il y a exactement 2 utilisateurs de plus que l'état initial
        assertThat(userService.getUsers()).hasSize((int)(initialCount + 2));
    }


    @Test
    void updateFcmToken_ShouldUpdateToken() throws IOException {
        ResponseEntity<?> response = userService.save(testUserRequest, testImageFile);
        UserRequest createdUser = (UserRequest) response.getBody();

        userService.updateFcmToken(createdUser.getId(), "new-token");

        User user = userRepository.findById(createdUser.getId()).orElseThrow();
        assertThat(user.getFcmToken()).isEqualTo("new-token");
    }

    @Test
    void updateFcmToken_ShouldThrowWhenUserNotFound() {
        assertThrows(ResponseStatusException.class, () -> {
            userService.updateFcmToken(999, "token");
        });
    }

    @Test
    void modifier_ShouldUpdateUser() throws IOException {
        ResponseEntity<?> response = userService.save(testUserRequest, testImageFile);
        UserRequest createdUser = (UserRequest) response.getBody();
        createdUser.setNom("Updated Name");

        ResponseEntity<?> updateResponse = userService.modifier(createdUser, createTestImageFile());

        assertThat(updateResponse.getStatusCodeValue()).isEqualTo(200);
        UserRequest updatedUser = (UserRequest) updateResponse.getBody();
        assertThat(updatedUser.getNom()).isEqualTo("Updated Name");
    }
}