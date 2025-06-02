package com.example.ecommerce.controller.integration;
import com.example.ecommerce.dto.UserRequest;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private UserRequest testUserRequest;
    private MockMultipartFile testImageFile;

    @BeforeEach
    void setUp() throws Exception {
        // Clean database before each test
        userRepository.deleteAll();

        testUserRequest = UserRequest.builder()
                .nom("Test")
                .prenom("User")
                .email("test@example.com")
                .password("password")
                .role("Client")
                .telephone("1234567890")
                .build();

        // Create a real image file for testing
        testImageFile = createTestImageFile();
    }

    // Solution 1: Create a real image programmatically
    private MockMultipartFile createTestImageFile() throws Exception {
        // Create a simple 100x100 pixel image
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 100, 100);
        g2d.dispose();

        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        return new MockMultipartFile(
                "pathImage",
                "test.jpg",
                "image/jpeg",
                imageBytes);
    }

    @Test
    void saveUser_ShouldCreateUser() throws Exception {
        MockMultipartFile userDtoFile = new MockMultipartFile(
                "userDto",
                "",
                "application/json",
                objectMapper.writeValueAsString(testUserRequest).getBytes());

        mockMvc.perform(multipart("/api/v1/users/add")
                        .file(userDtoFile)
                        .file(testImageFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(testUserRequest.getEmail()));
    }

    @Test
    void modifierUser_ShouldUpdateUser() throws Exception {
        // First create a user to update
        User user = new User();
        user.setEmail("existing@example.com");
        user.setPassword("password");
        user.setNom("Existing");
        user.setPrenom("User");
        user = userRepository.save(user);

        testUserRequest.setId(user.getId());
        testUserRequest.setEmail("updated@example.com");

        MockMultipartFile userDtoFile = new MockMultipartFile(
                "userDto",
                "",
                "application/json",
                objectMapper.writeValueAsString(testUserRequest).getBytes());

        mockMvc.perform(multipart("/api/v1/users/modifier")
                        .file(userDtoFile)
                        .file(testImageFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    // Alternative approach: Test without image file when it's optional
    @Test
    void modifierUser_WithoutImage_ShouldUpdateUser() throws Exception {
        User user = new User();
        user.setEmail("existing@example.com");
        user.setPassword("password");
        user.setNom("Existing");
        user.setPrenom("User");
        user = userRepository.save(user);

        testUserRequest.setId(user.getId());
        testUserRequest.setEmail("updated@example.com");

        MockMultipartFile userDtoFile = new MockMultipartFile(
                "userDto",
                "",
                "application/json",
                objectMapper.writeValueAsString(testUserRequest).getBytes());

        // Test without image file if it's optional
        mockMvc.perform(multipart("/api/v1/users/modifier")
                        .file(userDtoFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void getUserByEmail_ShouldReturnUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        userRepository.save(user);

        mockMvc.perform(get("/api/v1/users/getByEmail/{email}", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    // SOLUTION 1: Clean database and count exactly what we create
    @Test
    void getUsers_ShouldReturnAllUsers() throws Exception {
        // Database is already cleaned in setUp()

        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        userRepository.save(user2);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // SOLUTION 2: Alternative approach - count current users plus new ones
    @Test
    void getUsers_ShouldReturnAllUsers_Alternative() throws Exception {
        // Count existing users first
        long initialCount = userRepository.count();

        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        userRepository.save(user2);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int)(initialCount + 2)));
    }

    // SOLUTION 3: Test specific content instead of just count
    @Test
    void getUsers_ShouldContainCreatedUsers() throws Exception {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setNom("User");
        user1.setPrenom("One");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setNom("User");
        user2.setPrenom("Two");
        userRepository.save(user2);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.email == 'user1@example.com')]").exists())
                .andExpect(jsonPath("$[?(@.email == 'user2@example.com')]").exists());
    }

    @Test
    void updateFcmToken_ShouldUpdateToken() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user = userRepository.save(user);

        mockMvc.perform(put("/api/v1/users/{userId}/fcm-token", user.getId())
                        .param("token", "new-fcm-token"))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getFcmToken()).isEqualTo("new-fcm-token");
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user = userRepository.save(user);

        mockMvc.perform(get("/api/v1/users/getById/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}