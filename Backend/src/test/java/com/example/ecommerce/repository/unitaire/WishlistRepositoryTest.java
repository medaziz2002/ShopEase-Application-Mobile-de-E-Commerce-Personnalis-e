package com.example.ecommerce.repository.unitaire;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Wishlist;
import com.example.ecommerce.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Test
    void findByUserId_ShouldReturnUserWishlist() {
        User user = new User();
        user.setId(1);

        Wishlist item = new Wishlist();
        item.setUser(user);
        wishlistRepository.save(item);

        List<Wishlist> result = wishlistRepository.findByUserId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUser().getId());
    }
}