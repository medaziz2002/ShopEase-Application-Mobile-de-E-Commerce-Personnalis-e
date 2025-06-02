package com.example.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import com.google.cloud.firestore.annotation.DocumentId;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "titre")
    private String titre;



    @OneToOne(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Image image;


}