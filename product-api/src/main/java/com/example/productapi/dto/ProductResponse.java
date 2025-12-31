package com.example.productapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for product response with HATEOAS support.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Product response object with HATEOAS links")
public class ProductResponse extends RepresentationModel<ProductResponse> {

    @Schema(description = "Product ID", example = "1")
    private Long id;

    @Schema(description = "Product name", example = "Wireless Mouse")
    private String name;

    @Schema(description = "Product description", example = "Ergonomic wireless mouse with 6 buttons")
    private String description;

    @Schema(description = "Product price", example = "29.99")
    private BigDecimal price;

    @Schema(description = "Product category", example = "Electronics")
    private String category;

    @Schema(description = "Available stock quantity", example = "100")
    private Integer stockQuantity;

    @Schema(description = "Stock Keeping Unit (SKU)", example = "WM-001")
    private String sku;

    @Schema(description = "Whether the product is active", example = "true")
    private Boolean active;

    @Schema(description = "Creation timestamp", example = "2025-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2025-01-01T10:00:00")
    private LocalDateTime updatedAt;
}
