package com.example.productapi.controller.v1;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.model.Product;
import com.example.productapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST Controller for Product API - Version 1
 *
 * This controller demonstrates RESTful API best practices:
 * - Proper HTTP methods (GET, POST, PUT, DELETE)
 * - Appropriate HTTP status codes (200, 201, 204, 404, etc.)
 * - API versioning (URI versioning: /api/v1)
 * - Pagination and filtering
 * - HATEOAS (Hypermedia as the Engine of Application State)
 * - OpenAPI/Swagger documentation
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;
    private final PagedResourcesAssembler<Product> pagedResourcesAssembler;

    /**
     * GET /api/v1/products - Get all products with pagination and filtering
     *
     * Supports:
     * - Pagination (page, size)
     * - Sorting (sort)
     * - Filtering (category, active, minPrice, maxPrice, search)
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a paginated list of products with optional filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
                content = @Content(schema = @Schema(implementation = PagedModel.class)))
    })
    public ResponseEntity<PagedModel<EntityModel<ProductResponse>>> getAllProducts(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort field and direction", example = "name,asc")
            @RequestParam(defaultValue = "id,asc") String[] sort,

            @Parameter(description = "Filter by category", example = "Electronics")
            @RequestParam(required = false) String category,

            @Parameter(description = "Filter by active status", example = "true")
            @RequestParam(required = false) Boolean active,

            @Parameter(description = "Minimum price", example = "10.00")
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(description = "Maximum price", example = "100.00")
            @RequestParam(required = false) BigDecimal maxPrice,

            @Parameter(description = "Search term (searches name and description)", example = "mouse")
            @RequestParam(required = false) String search) {

        // Build pageable with sorting
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        // Get products with filters
        Page<Product> productPage = productService.getProductsWithFilters(
                category, active, minPrice, maxPrice, search, pageable);

        // Convert to HATEOAS PagedModel
        PagedModel<EntityModel<ProductResponse>> pagedModel = pagedResourcesAssembler.toModel(
                productPage.map(this::mapToResponse),
                this::addHateoasLinks
        );

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * GET /api/v1/products/{id} - Get product by ID
     *
     * Returns: 200 OK with product, or 404 Not Found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a single product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found",
                content = @Content(schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {

        Product product = productService.getProductById(id);
        ProductResponse response = mapToResponse(product);
        addHateoasLinks(response);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/products - Create a new product
     *
     * Returns: 201 Created with Location header
     */
    @PostMapping
    @Operation(summary = "Create a new product", description = "Create a new product with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully",
                content = @Content(schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Product with same SKU already exists")
    })
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(description = "Product details", required = true)
            @Valid @RequestBody ProductRequest request) {

        Product product = productService.createProduct(request);
        ProductResponse response = mapToResponse(product);
        addHateoasLinks(response);

        return ResponseEntity
                .created(linkTo(methodOn(ProductController.class).getProductById(product.getId())).toUri())
                .body(response);
    }

    /**
     * PUT /api/v1/products/{id} - Update an existing product
     *
     * Returns: 200 OK with updated product, or 404 Not Found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Update an existing product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully",
                content = @Content(schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id,

            @Parameter(description = "Updated product details", required = true)
            @Valid @RequestBody ProductRequest request) {

        Product product = productService.updateProduct(id, request);
        ProductResponse response = mapToResponse(product);
        addHateoasLinks(response);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/products/{id} - Delete a product (soft delete)
     *
     * Returns: 204 No Content, or 404 Not Found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Soft delete a product by marking it as inactive")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Map Product entity to ProductResponse DTO
     */
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setCategory(product.getCategory());
        response.setStockQuantity(product.getStockQuantity());
        response.setSku(product.getSku());
        response.setActive(product.getActive());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    /**
     * Add HATEOAS links to ProductResponse
     *
     * Links include:
     * - self: link to the product itself
     * - all-products: link to get all products
     * - update: link to update the product
     * - delete: link to delete the product
     */
    private ProductResponse addHateoasLinks(ProductResponse response) {
        // Self link
        response.add(linkTo(methodOn(ProductController.class)
                .getProductById(response.getId())).withSelfRel());

        // Link to all products
        response.add(linkTo(methodOn(ProductController.class)
                .getAllProducts(0, 10, new String[]{"id", "asc"}, null, null, null, null, null))
                .withRel("all-products"));

        // Link to update
        response.add(linkTo(methodOn(ProductController.class)
                .updateProduct(response.getId(), null)).withRel("update"));

        // Link to delete
        response.add(linkTo(methodOn(ProductController.class)
                .deleteProduct(response.getId())).withRel("delete"));

        return response;
    }

    /**
     * Add HATEOAS links to EntityModel<ProductResponse>
     */
    private EntityModel<ProductResponse> addHateoasLinks(EntityModel<ProductResponse> model) {
        ProductResponse response = model.getContent();
        if (response != null) {
            addHateoasLinks(response);
        }
        return model;
    }
}
