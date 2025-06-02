package com.example.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_cart_product")
    )
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_cart_user")
    )
    private User user;



    @Column(nullable = false)
    private int quantity;


    @ElementCollection
    @CollectionTable(name = "cart_sizes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "size")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Builder.Default
    private List<String> size = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "cart_weights", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "weight")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Builder.Default
    private List<String> weight = new ArrayList<>();
}