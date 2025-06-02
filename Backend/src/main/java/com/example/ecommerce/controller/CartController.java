package com.example.ecommerce.controller;
import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {


    private final CartService cartService;
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addToCart(
            @RequestParam Integer productId,
            @RequestParam Integer userId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) List<String> sizes,
            @RequestParam(required = false) List<String> weights) {
        System.out.println("Product ID: " + productId);
        System.out.println("User ID: " + userId);
        System.out.println("Quantity: " + quantity);
        System.out.println("Sizes: " + sizes);
        System.out.println("Weights: " + weights);

        cartService.addToCart(productId, userId, quantity, sizes, weights);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartDto>> getCartItems(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PutMapping("/{cartId}")
    public void updateQuantity(
            @PathVariable Integer cartId,
            @RequestParam Integer quantity) {
         cartService.updateQuantity(cartId, quantity);
    }


    @PostMapping("/{cartId}/options")
    public void updateCartOptions(
            @PathVariable Integer cartId,
            @RequestParam(required = false) List<String> sizes,
            @RequestParam(required = false) List<String> weights) {
        cartService.updateCartOptions(cartId, sizes,weights);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Integer cartId) {
        cartService.removeFromCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkProductInCart(
            @RequestParam int productId,
            @RequestParam int userId) {
        boolean exists = cartService.isProductInUserCart(productId, userId);
        return ResponseEntity.ok(exists);
    }

}
