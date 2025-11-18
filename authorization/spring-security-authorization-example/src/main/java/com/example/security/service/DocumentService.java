package com.example.security.service;

import com.example.security.model.Document;
import com.example.security.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;

import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

/**
 * Service demonstrating Method Security with various authorization annotations.
 *
 * Method security allows you to secure individual methods with authorization rules.
 * This is more fine-grained than URL-based security and works at the service layer.
 *
 * Annotations demonstrated:
 * - @PreAuthorize: Check authorization before method execution
 * - @PostAuthorize: Check authorization after method execution
 * - @PreFilter: Filter collection parameters before method execution
 * - @PostFilter: Filter collection results after method execution
 * - @Secured: Simple role-based security
 * - @RolesAllowed: JSR-250 standard annotation
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    /**
     * @PreAuthorize: Checks authorization BEFORE method execution.
     * User must have WRITE_DOCUMENTS authority to create documents.
     */
    @PreAuthorize("hasAuthority('WRITE_DOCUMENTS')")
    public Document createDocument(Document document) {
        return documentRepository.save(document);
    }

    /**
     * @PostAuthorize: Checks authorization AFTER method execution.
     * Returns document only if the user is the owner.
     * Uses 'returnObject' to reference the method's return value.
     */
    @PostAuthorize("returnObject.isPresent() && returnObject.get().owner == authentication.name")
    public Optional<Document> getDocument(Long id) {
        return documentRepository.findById(id);
    }

    /**
     * @PreAuthorize with complex SpEL expression.
     * Allows ADMIN role OR the document owner to update.
     * Uses method parameter '#id' in the expression.
     */
    @PreAuthorize("hasRole('ADMIN') or @documentRepository.findById(#id).orElse(null)?.owner == authentication.name")
    public Document updateDocument(Long id, Document updatedDocument) {
        return documentRepository.findById(id)
            .map(doc -> {
                doc.setTitle(updatedDocument.getTitle());
                doc.setContent(updatedDocument.getContent());
                doc.setStatus(updatedDocument.getStatus());
                return documentRepository.save(doc);
            })
            .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    /**
     * @Secured: Simple role-based authorization.
     * Only users with ROLE_ADMIN can delete documents.
     * Note: @Secured uses full role names (including ROLE_ prefix).
     */
    @Secured("ROLE_ADMIN")
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }

    /**
     * @RolesAllowed: JSR-250 standard annotation.
     * Similar to @Secured but uses role names without ROLE_ prefix.
     */
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    /**
     * @PostFilter: Filters the returned collection.
     * Only returns documents where user is the owner OR user has ADMIN role.
     * Uses 'filterObject' to reference each element in the collection.
     */
    @PostFilter("filterObject.owner == authentication.name or hasRole('ADMIN')")
    public List<Document> getMyDocuments() {
        return documentRepository.findAll();
    }

    /**
     * @PreFilter: Filters collection parameters before method execution.
     * Only processes documents where user is the owner.
     */
    @PreFilter("filterObject.owner == authentication.name")
    @PreAuthorize("hasAuthority('WRITE_DOCUMENTS')")
    public List<Document> bulkCreateDocuments(List<Document> documents) {
        return documentRepository.saveAll(documents);
    }

    /**
     * Complex authorization: Combines multiple conditions.
     * User must have READ_DOCUMENTS authority AND
     * (be an ADMIN OR the document must be PUBLISHED).
     */
    @PreAuthorize("hasAuthority('READ_DOCUMENTS') and " +
                  "(hasRole('ADMIN') or " +
                  "@documentRepository.findById(#id).orElse(null)?.status?.name() == 'PUBLISHED')")
    public Document getPublishedDocument(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    /**
     * Using @PreAuthorize with a custom bean method.
     * Delegates authorization logic to a separate bean.
     */
    @PreAuthorize("@documentSecurityEvaluator.canAccess(authentication, #id)")
    public Document getDocumentWithCustomEvaluator(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    /**
     * Public method - no authorization required.
     * Returns only published documents visible to everyone.
     */
    public List<Document> getPublicDocuments() {
        return documentRepository.findByStatus(Document.DocumentStatus.PUBLISHED);
    }
}
