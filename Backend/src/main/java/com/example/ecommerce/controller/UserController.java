package com.example.ecommerce.controller;


import com.example.ecommerce.dto.UserRequest;
import com.example.ecommerce.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {


    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> save(@RequestPart("userDto") UserRequest userDto,@RequestPart("pathImage") MultipartFile file)throws IOException
    {
        return userService.save(userDto,file);
    }



    @PostMapping("/modifier")
    public ResponseEntity<?> modifier(
            @RequestPart("userDto") UserRequest userDto,
            @RequestPart(value = "pathImage", required = false) MultipartFile file) throws IOException {
        return userService.modifier(userDto, file);
    }




    @GetMapping("/getByEmail/{email}")
    public UserRequest getUserByEmail(@PathVariable String email)
    {
        return userService.getUserByEmail(email);
    }



    @GetMapping
    public ResponseEntity<List<UserRequest>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }


    @PutMapping("/{userId}/fcm-token")
    public ResponseEntity<Void> updateFcmToken(
            @PathVariable Integer userId,
            @RequestParam String token) {
        userService.updateFcmToken(userId, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getById/{id}")
    public UserRequest getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }



}
