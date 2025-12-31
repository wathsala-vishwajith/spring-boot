package com.example.productapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a product.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating a product")
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    @Schema(description = "Product name", example = "Wireless Mouse")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Product description", example = "Ergonomic wireless mouse with 6 buttons")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places")
    @Schema(description = "Product price", example = "29.99")
    private BigDecimal price;

    @NotBlank(message = "Category is required")
    @Schema(description = "Product category", example = "Electronics")
    private String category;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    @Schema(description = "Available stock quantity", example = "100")
    private Integer stockQuantity;

    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must contain only uppercase letters, numbers, and hyphens")
    @Schema(description = "Stock Keeping Unit (SKU)", example = "WM-001")
    private String sku;

    @Schema(description = "Whether the product is active", example = "true")
    private Boolean active = true;
}
