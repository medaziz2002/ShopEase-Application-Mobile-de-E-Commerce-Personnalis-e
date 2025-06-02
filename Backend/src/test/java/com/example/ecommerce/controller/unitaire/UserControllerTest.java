package com.example.ecommerce.controller.unitaire;

import com.example.ecommerce.controller.UserController;
import com.example.ecommerce.dto.UserRequest;
import com.example.ecommerce.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void saveUser_WithImage_ShouldCallService() throws Exception {
        UserRequest userDto = new UserRequest();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());

        when(userService.save(any(), any())).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = userController.save(userDto, file);

        assertEquals(200, response.getStatusCodeValue());
        verify(userService).save(userDto, file);
    }

    @Test
    void modifierUser_WithOptionalImage_ShouldCallService() throws Exception {
        UserRequest userDto = new UserRequest();

        when(userService.modifier(any(), any())).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = userController.modifier(userDto, null);

        assertEquals(200, response.getStatusCodeValue());
        verify(userService).modifier(userDto, null);
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        UserRequest expected = new UserRequest();
        when(userService.getUserByEmail("test@example.com")).thenReturn(expected);

        UserRequest result = userController.getUserByEmail("test@example.com");

        assertEquals(expected, result);
    }

    @Test
    void getUsers_ShouldReturnList() {
        when(userService.getUsers()).thenReturn(List.of(new UserRequest()));

        ResponseEntity<List<UserRequest>> response = userController.getUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void updateFcmToken_ShouldCallService() {
        ResponseEntity<Void> response = userController.updateFcmToken(1, "token");

        assertEquals(200, response.getStatusCodeValue());
        verify(userService).updateFcmToken(1, "token");
    }

    @Test
    void getUserById_ShouldReturnUser() {
        UserRequest expected = new UserRequest();
        when(userService.getUserById(1)).thenReturn(expected);

        UserRequest result = userController.getUserById(1);

        assertEquals(expected, result);
    }
}