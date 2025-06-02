package com.example.ecommerce;

import com.example.ecommerce.entity.Faq;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.FaqRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
@EnableScheduling
public class ECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }

    @Component
    public static class InitSuperAdmin implements CommandLineRunner {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private FaqRepository faqRepository;

        private static final Logger logger = LoggerFactory.getLogger(InitSuperAdmin.class);

        @Override
        public void run(String... args) {
            try {
                // Supprimer les anciennes FAQ
                faqRepository.deleteAll();

                // FAQ en français
                faqRepository.save(new Faq(null, "Comment créer un compte ?", "Cliquez sur 'S'inscrire' et remplissez vos informations."));
                faqRepository.save(new Faq(null, "Comment modifier mon profil ?", "Allez dans 'Mon compte' puis 'Modifier le profil'."));
                faqRepository.save(new Faq(null, "Comment ajouter un produit au panier ?", "Sur la page produit, cliquez sur 'Ajouter au panier'."));
                faqRepository.save(new Faq(null, "Quels modes de paiement sont acceptés ?", "Cartes bancaires, PayPal et autres."));
                faqRepository.save(new Faq(null, "Comment suivre ma commande ?", "Consultez la section 'Mes commandes' pour le suivi."));
                faqRepository.save(new Faq(null, "Puis-je annuler une commande ?", "Oui, avant la validation finale."));
                faqRepository.save(new Faq(null, "Comment laisser un avis ?", "Après achat, vous pouvez laisser un commentaire."));
                faqRepository.save(new Faq(null, "Y a-t-il des frais de livraison ?", "Cela dépend de votre lieu de livraison et de la commande."));
                faqRepository.save(new Faq(null, "Comment contacter le support ?", "Utilisez la page 'Contact' pour envoyer un message."));
                faqRepository.save(new Faq(null, "Puis-je retourner un produit ?", "Oui, sous 14 jours après réception."));
                faqRepository.save(new Faq(null, "Quels sont les délais de livraison ?", "La livraison est généralement effectuée sous 3 à 7 jours ouvrés."));
                faqRepository.save(new Faq(null, "Comment puis-je changer mon mot de passe ?", "Allez dans les paramètres de compte et sélectionnez 'Changer mot de passe'."));
                faqRepository.save(new Faq(null, "Puis-je enregistrer plusieurs adresses de livraison ?", "Oui, vous pouvez ajouter plusieurs adresses dans votre profil."));
                faqRepository.save(new Faq(null, "Comment appliquer un code promo ?", "Lors de la validation du panier, entrez votre code promo dans la zone dédiée."));
                faqRepository.save(new Faq(null, "Comment suivre le statut de ma commande ?", "Consultez votre espace client dans la section 'Mes commandes'."));
                faqRepository.save(new Faq(null, "Puis-je modifier ma commande après validation ?", "Non, une fois validée, la commande ne peut plus être modifiée."));
                faqRepository.save(new Faq(null, "Quels sont les modes de livraison proposés ?", "Livraison standard, express, et retrait en magasin."));
                faqRepository.save(new Faq(null, "Que faire en cas de produit défectueux ?", "Contactez notre support pour organiser un retour ou un échange."));
                faqRepository.save(new Faq(null, "Comment bénéficier des promotions ?", "Les promotions sont automatiquement appliquées selon les conditions."));
                faqRepository.save(new Faq(null, "Est-il possible de créer une liste de souhaits ?", "Oui, vous pouvez ajouter des produits à votre liste de souhaits."));
                faqRepository.save(new Faq(null, "Puis-je contacter directement un vendeur ?", "Oui, via la messagerie interne de l'application."));
                faqRepository.save(new Faq(null, "Quels sont les critères pour devenir vendeur ?", "Vous devez fournir des informations légales et un compte bancaire valide."));
                faqRepository.save(new Faq(null, "Comment supprimer mon compte ?", "Contactez le support client pour demander la suppression."));
                faqRepository.save(new Faq(null, "L'application est-elle disponible en plusieurs langues ?", "Oui, elle supporte le français et l'anglais."));
                faqRepository.save(new Faq(null, "Comment gérer mes notifications ?", "Dans les paramètres, vous pouvez activer ou désactiver les notifications."));

                // FAQ en anglais
                faqRepository.save(new Faq(null, "How to create an account?", "Click on 'Sign up' and fill your details."));
                faqRepository.save(new Faq(null, "How to edit my profile?", "Go to 'My Account' then 'Edit Profile'."));
                faqRepository.save(new Faq(null, "How to add a product to the cart?", "On the product page, click 'Add to cart'."));
                faqRepository.save(new Faq(null, "What payment methods are accepted?", "Credit cards, PayPal, and others."));
                faqRepository.save(new Faq(null, "How can I track my order?", "Check 'My Orders' section for tracking."));
                faqRepository.save(new Faq(null, "Can I cancel an order?", "Yes, before final confirmation."));
                faqRepository.save(new Faq(null, "How to leave a review?", "After purchase, you can leave a comment."));
                faqRepository.save(new Faq(null, "Are there shipping fees?", "Depends on your location and order."));
                faqRepository.save(new Faq(null, "How to contact support?", "Use the 'Contact' page to send a message."));
                faqRepository.save(new Faq(null, "Can I return a product?", "Yes, within 14 days after delivery."));
                faqRepository.save(new Faq(null, "What are the delivery times?", "Delivery usually takes 3 to 7 business days."));
                faqRepository.save(new Faq(null, "How can I change my password?", "Go to account settings and select 'Change password'."));
                faqRepository.save(new Faq(null, "Can I save multiple delivery addresses?", "Yes, you can add multiple addresses in your profile."));
                faqRepository.save(new Faq(null, "How to apply a promo code?", "Enter your promo code in the designated area during checkout."));
                faqRepository.save(new Faq(null, "How to track my order status?", "Check your client area in the 'My Orders' section."));
                faqRepository.save(new Faq(null, "Can I modify my order after confirmation?", "No, once confirmed, orders cannot be modified."));
                faqRepository.save(new Faq(null, "What delivery methods are available?", "Standard shipping, express shipping, and store pickup."));
                faqRepository.save(new Faq(null, "What to do if the product is defective?", "Contact our support to arrange a return or exchange."));
                faqRepository.save(new Faq(null, "How to benefit from promotions?", "Promotions are automatically applied according to conditions."));
                faqRepository.save(new Faq(null, "Can I create a wish list?", "Yes, you can add products to your wish list."));
                faqRepository.save(new Faq(null, "Can I contact a seller directly?", "Yes, via the app's internal messaging."));
                faqRepository.save(new Faq(null, "What are the criteria to become a seller?", "You must provide legal info and a valid bank account."));
                faqRepository.save(new Faq(null, "How to delete my account?", "Contact customer support to request deletion."));
                faqRepository.save(new Faq(null, "Is the app available in multiple languages?", "Yes, it supports French and English."));
                faqRepository.save(new Faq(null, "How to manage my notifications?", "You can enable or disable notifications in settings."));

                // Création de l'administrateur
                userRepository.findByEmail("admin@gmail.com").ifPresentOrElse(
                        user -> logger.info("Admin user already exists with email: admin@gmail.com"),
                        () -> {
                            try {
                                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                                User admin = User.builder()
                                        .nom("Belhaj hssine")
                                        .prenom("Mohamed aziz")
                                        .email("admin@gmail.com")
                                        .telephone("7445896215")
                                        .role("SUPERADMIN")
                                        .password(encoder.encode("123456"))
                                        .build();
                                User savedAdmin = userRepository.save(admin);
                                logger.info("Super admin created successfully with ID: {}", savedAdmin.getId());
                            } catch (Exception e) {
                                logger.error("Failed to create admin user: {}", e.getMessage(), e);
                                throw new RuntimeException("Failed to initialize admin user", e);
                            }
                        }
                );

            } catch (Exception e) {
                logger.error("Error during application initialization: {}", e.getMessage(), e);
            }
        }
    }
}
