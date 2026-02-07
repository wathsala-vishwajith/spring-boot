package com.example.authorization.security;

import com.example.authorization.model.Document;
import com.example.authorization.model.DocumentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Custom security component for document-level authorization checks
 * Used in SpEL expressions like @documentSecurity.isOwner(#id, authentication.name)
 */
@Component("documentSecurity")
public class DocumentSecurity {

    private final DocumentRepository documentRepository;

    public DocumentSecurity(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Check if the given username is the owner of the document
     */
    public boolean isOwner(Long documentId, String username) {
        Optional<Document> document = documentRepository.findById(documentId);
        return document.isPresent() && document.get().getOwner().equals(username);
    }

    /**
     * Check if user can access document based on classification and role
     */
    public boolean canAccess(String classification, String role) {
        return switch (classification) {
            case "PUBLIC" -> true;
            case "CONFIDENTIAL" -> role.equals("ROLE_MANAGER") || role.equals("ROLE_ADMIN");
            case "SECRET" -> role.equals("ROLE_ADMIN");
            default -> false;
        };
    }
}
