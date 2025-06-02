package com.example.ecommerce.repository.integration;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Wishlist;
import com.example.ecommerce.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WishlistRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Test
    void whenFindByUserId_thenReturnWishlistItems() {
        // given
        User user1 = new User();
        entityManager.persist(user1);
        entityManager.flush(); // Ensure ID is generated

        User user2 = new User();
        entityManager.persist(user2);
        entityManager.flush(); // Ensure ID is generated

        Product product1 = new Product();
        entityManager.persist(product1);
        entityManager.flush(); // Ensure ID is generated

        Product product2 = new Product();
        entityManager.persist(product2);
        entityManager.flush(); // Ensure ID is generated

        Wishlist item1 = Wishlist.builder()
                .user(user1)
                .product(product1)
                .build();
        entityManager.persist(item1);

        Wishlist item2 = Wishlist.builder()
                .user(user1)
                .product(product2)
                .build();
        entityManager.persist(item2);

        Wishlist item3 = Wishlist.builder()
                .user(user2)
                .product(product1)
                .build();
        entityManager.persist(item3);

        entityManager.flush();

        // when - Use the actual generated user ID
        List<Wishlist> foundItems = wishlistRepository.findByUserId(user1.getId());

        // then
        assertThat(foundItems).hasSize(2);
        assertThat(foundItems)
                .extracting(wishlist -> wishlist.getUser().getId())
                .containsOnly(user1.getId());
    }

    @Test
    void whenFindByUserIdWithNoItems_thenReturnEmptyList() {
        // when
        List<Wishlist> foundItems = wishlistRepository.findByUserId(999);

        // then
        assertThat(foundItems).isEmpty();
    }

    @Test
    void whenMultipleUsersHaveWishlistItems_thenReturnCorrectItemsForEachUser() {
        // given
        User user1 = new User();
        entityManager.persist(user1);
        entityManager.flush();

        User user2 = new User();
        entityManager.persist(user2);
        entityManager.flush();

        Product product1 = new Product();
        entityManager.persist(product1);
        entityManager.flush();

        Product product2 = new Product();
        entityManager.persist(product2);
        entityManager.flush();

        // User1 has 1 item, User2 has 2 items
        Wishlist item1 = Wishlist.builder()
                .user(user1)
                .product(product1)
                .build();
        entityManager.persist(item1);

        Wishlist item2 = Wishlist.builder()
                .user(user2)
                .product(product1)
                .build();
        entityManager.persist(item2);

        Wishlist item3 = Wishlist.builder()
                .user(user2)
                .product(product2)
                .build();
        entityManager.persist(item3);

        entityManager.flush();

        // when & then
        List<Wishlist> user1Items = wishlistRepository.findByUserId(user1.getId());
        assertThat(user1Items).hasSize(1);
        assertThat(user1Items.get(0).getUser().getId()).isEqualTo(user1.getId());

        List<Wishlist> user2Items = wishlistRepository.findByUserId(user2.getId());
        assertThat(user2Items).hasSize(2);
        assertThat(user2Items)
                .extracting(wishlist -> wishlist.getUser().getId())
                .containsOnly(user2.getId());
    }
}