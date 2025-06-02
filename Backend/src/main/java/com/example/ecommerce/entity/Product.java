package com.example.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Lob
    private String description;


    private double price;

    @Column(name = "discount_per")
    private double discountPercentage;

    private double rating;

    private int nbPersonne;

    private int stock;


    @ElementCollection
    @CollectionTable(name = "product_sizes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "size")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> size = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_weights", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "weight")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> weight = new ArrayList<>();





    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discountPercentage=" + discountPercentage +
                ", rating=" + rating +
                ", stock=" + stock +
                ", size=" + size +
                ", weight=" + weight +
                ", category=" + category +
                ", seller=" + seller +
                ", images=" + images +
                '}';
    }

}

