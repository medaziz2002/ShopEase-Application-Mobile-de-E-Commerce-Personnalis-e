package com.example.ecommerce.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "faq")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer faq_id;

    @Column(length = 1000)
    private String question;

    @Column(length = 1000)
    private String reponse;

    // Méthode pour obtenir la réponse (simple getter personnalisé)
    public String getAnswer() {
        return this.reponse; // Retourne directement la réponse stockée
    }

}

