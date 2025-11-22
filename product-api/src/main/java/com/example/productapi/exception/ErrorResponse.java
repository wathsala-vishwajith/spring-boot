package com.example.productapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response object")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Error message", example = "Resource not found")
    private String message;

    @Schema(description = "Detailed error description", example = "Product not found with id: '123'")
    private String details;

    @Schema(description = "Timestamp of the error", example = "2025-01-01T10:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Request path", example = "/api/v1/products/123")
    private String path;

    @Schema(description = "List of validation errors")
    private List<ValidationError> validationErrors;

    public ErrorResponse(int status, String message, String details, String path) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidationError {
        @Schema(description = "Field name", example = "price")
        private String field;

        @Schema(description = "Error message", example = "Price must be greater than 0")
        private String message;
    }
}
