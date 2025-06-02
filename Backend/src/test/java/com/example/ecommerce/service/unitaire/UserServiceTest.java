package com.example.ecommerce.service.unitaire;

import com.example.ecommerce.dto.UserRequest;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Image;
import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void save_ShouldCreateUserWithImage() throws IOException {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setNom("Doe");
        userRequest.setPrenom("John");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setPassword("password123");
        userRequest.setTelephone("0123456789");

        // Créer une image valide pour le test
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                createValidJpegImage()
        );

        // Mock du repository - email n'existe pas
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Mock de la sauvegarde
        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setEmail("john.doe@example.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Mock du mapper
        UserRequest mappedUser = new UserRequest();
        mappedUser.setId(1);
        mappedUser.setEmail("john.doe@example.com");
        when(userMapper.toDto(any(User.class))).thenReturn(mappedUser);

        // Act
        ResponseEntity<?> response = userService.save(userRequest, imageFile);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(userRepository).findByEmail("john.doe@example.com");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void save_ShouldReturnBadRequest_WhenImageFileIsEmpty() throws IOException {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("john.doe@example.com");

        MockMultipartFile emptyFile = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);

        // Act
        ResponseEntity<?> response = userService.save(userRequest, emptyFile);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Le fichier image ne peut pas être vide", response.getBody());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void save_ShouldReturnBadRequest_WhenFileIsNotImage() throws IOException {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("john.doe@example.com");

        MockMultipartFile textFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        // Act
        ResponseEntity<?> response = userService.save(userRequest, textFile);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Le fichier doit être une image", response.getBody());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void save_ShouldReturnBadRequest_WhenEmailAlreadyExists() throws IOException {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("existing@example.com");

        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                createValidJpegImage()
        );

        // Mock - email existe déjà
        User existingUser = new User();
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        // Act
        ResponseEntity<?> response = userService.save(userRequest, imageFile);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email existe déjà", response.getBody());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        String email = "john.doe@example.com";
        User user = new User();
        user.setEmail(email);

        UserRequest expectedDto = new UserRequest();
        expectedDto.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        // Act
        UserRequest result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());

        verify(userRepository).findByEmail(email);
        verify(userMapper).toDto(user);
    }

    @Test
    void getUserByEmail_ShouldThrowException_WhenEmailNotFound() {
        // Arrange
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUserByEmail(email));

        assertTrue(exception.getMessage().contains("Utilisateur non trouvé avec l'email"));
        verify(userRepository).findByEmail(email);
    }

    /**
     * Crée une image JPEG valide pour les tests
     */
    private byte[] createValidJpegImage() {
        try {
            // Créer une image BufferedImage 10x10 pixels
            BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

            // Remplir avec une couleur (bleu)
            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    bufferedImage.setRGB(x, y, 0x0000FF); // Bleu
                }
            }

            // Convertir en bytes JPEG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la création de l'image de test", e);
        }
    }
}