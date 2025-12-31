package com.example.productapi.service;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.model.Product;
import com.example.productapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service implementation for Product operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product createProduct(ProductRequest request) {
        Product product = mapToEntity(request);
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        Product existingProduct = getProductById(id);

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setStockQuantity(request.getStockQuantity());
        existingProduct.setSku(request.getSku());
        existingProduct.setActive(request.getActive());

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        // Soft delete - just mark as inactive
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    public Page<Product> getProductsWithFilters(
            String category,
            Boolean active,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String searchTerm,
            Pageable pageable) {

        return productRepository.findByFilters(
                category,
                active,
                minPrice,
                maxPrice,
                searchTerm,
                pageable
        );
    }

    @Override
    public Page<Product> searchProducts(String searchTerm, Pageable pageable) {
        return productRepository.searchProducts(searchTerm, pageable);
    }

    /**
     * Map ProductRequest DTO to Product entity
     */
    private Product mapToEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStockQuantity(request.getStockQuantity());
        product.setSku(request.getSku());
        product.setActive(request.getActive() != null ? request.getActive() : true);
        return product;
    }
}
