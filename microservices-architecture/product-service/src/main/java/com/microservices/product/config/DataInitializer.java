package com.microservices.product.config;

import com.microservices.product.model.Product;
import com.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        Product laptop = new Product(null, "Laptop", "High-performance laptop", 999.99, 50, "Electronics", null);
        Product phone = new Product(null, "Smartphone", "Latest smartphone model", 699.99, 100, "Electronics", null);
        Product desk = new Product(null, "Office Desk", "Ergonomic office desk", 299.99, 25, "Furniture", null);

        productRepository.saveAll(Arrays.asList(laptop, phone, desk));
    }
}
