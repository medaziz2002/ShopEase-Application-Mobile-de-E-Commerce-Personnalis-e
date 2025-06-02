package com.example.ecommerce.service;
import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.SizeDto;
import com.example.ecommerce.dto.WeightDto;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.ImageRepository;
import com.example.ecommerce.repository.NotificationLogRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

        private final ProductRepository productRepository;
        private final ProductMapper productMapper;
        private final ImageRepository productImagesRepository;
        private final NotificationLogRepository notificationLogRepository;
        private final UserRepository userRepository;
        private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
        public void saveProduct(ProductDto productDto, MultipartFile[] files) throws IOException {
                if (files == null || files.length == 0) {
                        throw new IllegalArgumentException("Au moins une image est requise");
                }
                Product product = productMapper.toEntity(productDto);
                Product savedProduct = productRepository.save(product);
                List<Image> images = new ArrayList<>();

                for (MultipartFile file : files) {
                        if (file.isEmpty()) {
                                continue;
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try (ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes())) {
                                Thumbnails.of(bais)
                                        .size(500, 600)
                                        .outputFormat("jpg")
                                        .toOutputStream(baos);
                        } catch (IOException e) {
                                throw new IOException("Erreur lors du traitement de l'image: " + e.getMessage());
                        }
                        Image productImage = Image.builder()
                                .name(file.getOriginalFilename())
                                .type(file.getContentType())
                                .image(baos.toByteArray())
                                .product(savedProduct)
                                .build();
                        Image savedImage = productImagesRepository.save(productImage);
                        images.add(savedImage);
                }

                savedProduct.setImages(images);
                productRepository.save(savedProduct);
                notificationLogRepository.save(
                        NotificationLog.builder()
                                .title("Nouveau produit ajouté")
                                .body("Le produit \"" + savedProduct.getTitle() + "\" a été ajouté avec succès.")
                                .sentAt(LocalDateTime.now())
                                .user(userRepository.findById(productDto.getSellerId()).get())
                                .build());
        }



        public List<ProductDto> getAllProducts() {
                List<Product> products = productRepository.findAll();
                return products.stream()
                        .map(productMapper::toDTO)
                        .collect(Collectors.toList());
        }

        public ProductDto getProductById(Integer id) {
                Product product = productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
                return productMapper.toDTO(product);
        }


        public SizeDto getAllSizes() {
                List<String> sizes = Arrays.stream(SizeType.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());

                SizeDto sizeDto = new SizeDto();
                sizeDto.setSizes(sizes);
                return sizeDto;
        }

        public WeightDto getAllWeights() {
                List<String> weights = Arrays.stream(WeightType.values())
                        .map(WeightType::getLabel)
                        .collect(Collectors.toList());

                WeightDto weightDto = new WeightDto();
                weightDto.setWeights(weights);
                return weightDto;
        }


        public void deleteProduct(Integer id) {
                Product product = productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'id : " + id));
                productImagesRepository.deleteAll(product.getImages());
                productRepository.delete(product);
        }


        public void updateProduct(Integer id, ProductDto productDto, MultipartFile[] files, List<Integer> imagesToDelete) throws IOException {
                logger.info("Starting product update for product ID: {}", id);
                logger.info("Received image IDs to delete: {}", imagesToDelete);
                logger.debug("Received product DTO: {}", productDto);
                logger.debug("Received {} files to upload", files != null ? files.length : 0);


                Product existingProduct = productRepository.findById(id)
                        .orElseThrow(() -> {
                                logger.error("Product not found with ID: {}", id);
                                return new RuntimeException("Produit introuvable avec l'id : " + id);
                        });

                logger.debug("Existing product found: {}", existingProduct.getTitle());
                logger.debug("Existing product has {} images", existingProduct.getImages().size());

                Product updatedProduct = productMapper.toEntity(productDto);
                updatedProduct.setId(existingProduct.getId());

                // 1. Handle image deletion
                List<Image> images = new ArrayList<>(existingProduct.getImages());
                if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
                        logger.info("Processing deletion for {} images", imagesToDelete.size());
                        logger.debug("Images to delete IDs: {}", imagesToDelete);

                        // Log existing image IDs before deletion
                        List<Integer> existingImageIds = existingProduct.getImages().stream()
                                .map(Image::getIdImage)
                                .collect(Collectors.toList());
                        logger.debug("Existing image IDs before deletion: {}", existingImageIds);

                        images.removeIf(img -> {
                                boolean shouldRemove = imagesToDelete.contains(img.getIdImage());
                                if (shouldRemove) {
                                        logger.debug("Marking image for deletion - ID: {}, Name: {}", img.getIdImage(), img.getName());
                                }
                                return shouldRemove;
                        });

                        try {
                                logger.info("Attempting to delete {} images from repository", imagesToDelete.size());
                                productImagesRepository.deleteAllById(imagesToDelete);
                                logger.info("Successfully deleted images from repository");
                        } catch (Exception e) {
                                logger.error("Failed to delete images: {}", e.getMessage());
                                throw e;
                        }
                }

                // 2. Handle new image uploads
                if (files != null && files.length > 0) {
                        logger.info("Processing {} new image uploads", files.length);
                        for (MultipartFile file : files) {
                                if (file.isEmpty()) {
                                        logger.debug("Skipping empty file");
                                        continue;
                                }

                                logger.debug("Processing file: {}", file.getOriginalFilename());
                                try {
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        try (ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes())) {
                                                Thumbnails.of(bais)
                                                        .size(500, 600)
                                                        .outputFormat("jpg")
                                                        .toOutputStream(baos);

                                                Image productImage = Image.builder()
                                                        .name(file.getOriginalFilename())
                                                        .type(file.getContentType())
                                                        .image(baos.toByteArray())
                                                        .product(updatedProduct)
                                                        .build();

                                                Image savedImage = productImagesRepository.save(productImage);
                                                images.add(savedImage);
                                                logger.debug("Successfully saved new image with ID: {}", savedImage.getIdImage());
                                        }
                                } catch (IOException e) {
                                        logger.error("Error processing image {}: {}", file.getOriginalFilename(), e.getMessage());
                                        throw e;
                                }
                        }
                }

                updatedProduct.setImages(images);
                try {
                        Product savedProduct = productRepository.save(updatedProduct);
                        logger.info("Successfully updated product with ID: {}", savedProduct.getId());
                        logger.debug("Updated product now has {} images", savedProduct.getImages().size());
                } catch (Exception e) {
                        logger.error("Failed to save updated product: {}", e.getMessage());
                        throw e;
                }

                try {
                        notificationLogRepository.save(
                                NotificationLog.builder()
                                        .title("produit Modifier")
                                        .body("Le produit \"" + productDto.getTitle() + "\" a été modifier avec succès.")
                                        .sentAt(LocalDateTime.now())
                                        .user(userRepository.findById(productDto.getSellerId()).get())
                                        .build());
                        logger.info("Notification log created for product update");
                } catch (Exception e) {
                        logger.error("Failed to create notification log: {}", e.getMessage());
                        // Continue even if notification fails
                }
        }

        public boolean isProductOutOfStock(Integer productId) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + productId));
                return product.getStock() <= 0;
        }


        // Soldes d'hiver : 1er janvier au 31 janvier
        @Scheduled(cron = "0 0 1 1 1 *") // 1er janvier à 01h00
        public void applyWinterSaleDiscount() {
                applyDiscountToAllProducts(50.0);
        }

        @Scheduled(cron = "0 0 1 1 2 *") // 1er février à 01h00
        public void resetPricesAfterWinterSale() {
                applyDiscountToAllProducts(0.0);
        }

        // Soldes de printemps : 1er mars au 31 mars
        @Scheduled(cron = "0 0 1 1 3 *") // 1er mars à 01h00
        public void applySpringSaleDiscount() {
                applyDiscountToAllProducts(50.0);
        }

        @Scheduled(cron = "0 0 1 1 4 *") // 1er avril à 01h00
        public void resetPricesAfterSpringSale() {
                applyDiscountToAllProducts(0.0);
        }

        // Soldes d'été : 1er juin au 30 juin
        @Scheduled(cron = "0 0 1 1 6 *") // 1er juin à 01h00
        public void applySummerSaleDiscount() {
                applyDiscountToAllProducts(50.0);
        }

        @Scheduled(cron = "0 0 1 1 7 *") // 1er juillet à 01h00
        public void resetPricesAfterSummerSale() {
                applyDiscountToAllProducts(0.0);
        }

        // Soldes d'automne : 1er septembre au 30 septembre
        @Scheduled(cron = "0 0 1 1 9 *") // 1er septembre à 01h00
        public void applyAutumnSaleDiscount() {
                applyDiscountToAllProducts(50.0);
        }

        @Scheduled(cron = "0 0 1 1 10 *") // 1er octobre à 01h00
        public void resetPricesAfterAutumnSale() {
                applyDiscountToAllProducts(0.0);
        }

        // Black Friday : dernier vendredi de novembre (à détecter dynamiquement)
        @Scheduled(cron = "0 0 1 * 11 *") // Tous les jours à 01h00 en novembre
        public void applyBlackFridayDiscount() {
                LocalDate today = LocalDate.now();
                if (today.getDayOfWeek() == DayOfWeek.FRIDAY) {
                        LocalDate lastFriday = today.with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY));
                        if (today.equals(lastFriday)) {
                                applyDiscountToAllProducts(50.0);
                        }
                }
        }


        /*
        private void applyDiscountToAllProducts(double discount) {
                List<Product> products = productRepository.findAll();
                for (Product product : products) {
                        product.setDiscountPercentage(discount);
                }
                productRepository.saveAll(products);
        }
*/

        private void applyDiscountToAllProducts(double discountPercentage) {
                List<Product> products = productRepository.findAll();
                for (Product product : products) {
                        if (discountPercentage > 0) {
                                double discountedPrice = product.getPrice() * (1 - discountPercentage / 100);
                                product.setPrice(discountedPrice);
                        } else {
                                product.setPrice(product.getPrice());
                        }
                }
                productRepository.saveAll(products);
                logger.info("Applied discount of {}% to all products", discountPercentage);
        }


        public List<ProductDto> getTopRatedProducts(int limit) {
                List<Product> products = productRepository.findAll();
                List<Product> sortedProducts = products.stream()
                        .sorted((p1, p2) -> {
                                double score1 = calculateProductScore(p1);
                                double score2 = calculateProductScore(p2);

                                return Double.compare(score2, score1);
                        })
                        .limit(limit)
                        .collect(Collectors.toList());

                return sortedProducts.stream()
                        .map(productMapper::toDTO)
                        .collect(Collectors.toList());
        }

        private double calculateProductScore(Product product) {
                final double MIN_VOTES = 5.0;
                final double AVG_RATING = 2.5;
                double rating = product.getRating();
                int votes = product.getNbPersonne();

                return (votes / (votes + MIN_VOTES)) * rating +
                        (MIN_VOTES / (votes + MIN_VOTES)) * AVG_RATING;
        }


}