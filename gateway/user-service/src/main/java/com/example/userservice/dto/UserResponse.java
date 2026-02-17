package com.example.userservice.dto;

import com.example.userservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Response DTO
 *
 * Used to return user information without exposing sensitive data.
 *
 * Security Best Practice:
 * - NEVER return password (even hashed) in API responses
 * - Use DTOs to control what data is exposed
 * - Separate internal models from external API contracts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean active;
    private LocalDateTime createdAt;

    /**
     * Factory method to create UserResponse from User entity
     */
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getActive(),
                user.getCreatedAt()
        );
    }
}
