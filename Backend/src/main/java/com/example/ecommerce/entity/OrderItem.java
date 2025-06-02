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
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private double unitPrice;
    private double discount;

    @ElementCollection
    @CollectionTable(name = "order_item_sizes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "size")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Builder.Default
    private List<String> size = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "order_item_weights", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "weight")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Builder.Default
    private List<String> weight = new ArrayList<>();
}