package com.example.productservice.config;

import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Initializer
 *
 * Populates the database with sample data on application startup.
 *
 * CommandLineRunner:
 * - Runs after the application context is loaded
 * - Perfect for initialization tasks
 * - Can inject dependencies
 *
 * Learning Point: In production, use database migration tools
 * like Flyway or Liquibase instead of CommandLineRunner for data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing sample product data...");

        if (productRepository.count() == 0) {
            createSampleProducts();
            log.info("Sample data initialized successfully");
        } else {
            log.info("Database already contains data, skipping initialization");
        }
    }

    private void createSampleProducts() {
        // Electronics
        createProduct(
                "Laptop",
                "High-performance laptop with 16GB RAM and 512GB SSD",
                new BigDecimal("999.99"),
                50,
                "electronics",
                "https://example.com/laptop.jpg"
        );

        createProduct(
                "Smartphone",
                "Latest model smartphone with amazing camera",
                new BigDecimal("699.99"),
                100,
                "electronics",
                "https://example.com/phone.jpg"
        );

        createProduct(
                "Wireless Headphones",
                "Noise-cancelling wireless headphones",
                new BigDecimal("199.99"),
                75,
                "electronics",
                "https://example.com/headphones.jpg"
        );

        // Books
        createProduct(
                "Spring Boot in Action",
                "Comprehensive guide to Spring Boot",
                new BigDecimal("49.99"),
                30,
                "books",
                "https://example.com/spring-book.jpg"
        );

        createProduct(
                "Clean Code",
                "A handbook of agile software craftsmanship",
                new BigDecimal("39.99"),
                25,
                "books",
                "https://example.com/clean-code.jpg"
        );

        // Clothing
        createProduct(
                "T-Shirt",
                "Comfortable cotton t-shirt",
                new BigDecimal("19.99"),
                200,
                "clothing",
                "https://example.com/tshirt.jpg"
        );

        createProduct(
                "Jeans",
                "Classic blue jeans",
                new BigDecimal("59.99"),
                150,
                "clothing",
                "https://example.com/jeans.jpg"
        );

        // Home & Garden
        createProduct(
                "Coffee Maker",
                "Programmable coffee maker with thermal carafe",
                new BigDecimal("79.99"),
                40,
                "home",
                "https://example.com/coffee-maker.jpg"
        );

        createProduct(
                "Desk Lamp",
                "LED desk lamp with adjustable brightness",
                new BigDecimal("29.99"),
                60,
                "home",
                "https://example.com/lamp.jpg"
        );

        createProduct(
                "Plant Pot",
                "Ceramic plant pot with drainage",
                new BigDecimal("14.99"),
                80,
                "home",
                "https://example.com/pot.jpg"
        );
    }

    private void createProduct(String name, String description, BigDecimal price,
                                Integer stock, String category, String imageUrl) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stock);
        product.setCategory(category);
        product.setImageUrl(imageUrl);
        product.setActive(true);

        productRepository.save(product);
        log.info("Created product: {}", name);
    }
}
