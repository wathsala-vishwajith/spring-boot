package com.example.security.service;

import com.example.security.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Custom security evaluator for complex authorization logic.
 * This approach allows you to encapsulate complex authorization rules
 * in a reusable component that can be called from @PreAuthorize expressions.
 */
@Component
@RequiredArgsConstructor
public class DocumentSecurityEvaluator {

    private final DocumentRepository documentRepository;

    /**
     * Custom method to evaluate if a user can access a document.
     * Can be called from @PreAuthorize like:
     * @PreAuthorize("@documentSecurityEvaluator.canAccess(authentication, #id)")
     */
    public boolean canAccess(Authentication authentication, Long documentId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Admins can access everything
        if (authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // Check if user is the owner
        return documentRepository.findById(documentId)
            .map(doc -> doc.getOwner().equals(authentication.getName()))
            .orElse(false);
    }

    /**
     * Check if user can modify a document.
     */
    public boolean canModify(Authentication authentication, Long documentId) {
        if (!canAccess(authentication, documentId)) {
            return false;
        }

        // Check if user has WRITE_DOCUMENTS authority
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("WRITE_DOCUMENTS"));
    }
}
