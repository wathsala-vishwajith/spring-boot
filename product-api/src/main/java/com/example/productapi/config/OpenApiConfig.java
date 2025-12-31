package com.example.productapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 *
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * Access OpenAPI JSON at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product REST API")
                        .description("RESTful API built with Spring Boot demonstrating best practices:\n\n" +
                                "- **Proper HTTP Methods**: GET, POST, PUT, DELETE\n" +
                                "- **Appropriate Status Codes**: 200, 201, 204, 400, 404, 409, 500\n" +
                                "- **API Versioning**: URI versioning (/api/v1)\n" +
                                "- **Pagination**: Page-based pagination with configurable size\n" +
                                "- **Filtering**: Multiple filter criteria (category, price range, search)\n" +
                                "- **Sorting**: Customizable sorting on any field\n" +
                                "- **HATEOAS**: Hypermedia links for resource navigation\n" +
                                "- **Validation**: Request validation with detailed error messages\n" +
                                "- **Error Handling**: Consistent error response structure")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Production server")
                ));
    }
}
