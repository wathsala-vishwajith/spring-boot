package com.example.authorization.controller;

import com.example.authorization.model.Document;
import com.example.authorization.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller demonstrating method-level security with @PreAuthorize and @PostAuthorize
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Public endpoint - anyone can access
     */
    @GetMapping("/public")
    public ResponseEntity<List<Document>> getPublicDocuments() {
        return ResponseEntity.ok(documentService.getDocumentsByClassification("PUBLIC"));
    }

    /**
     * Get all documents - requires MANAGER or ADMIN role (enforced at service level)
     */
    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    /**
     * Get document by ID - access control enforced at service level
     */
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        Optional<Document> document = documentService.getDocumentById(id);
        return document.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get current user's documents
     */
    @GetMapping("/my-documents")
    public ResponseEntity<List<Document>> getMyDocuments(Authentication authentication) {
        return ResponseEntity.ok(documentService.getMyDocuments(authentication.getName()));
    }

    /**
     * Create document - requires USER role (enforced at service level)
     */
    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document, Authentication authentication) {
        document.setOwner(authentication.getName());
        Document created = documentService.createDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update document - only owner or ADMIN
     */
    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document document) {
        try {
            Document updated = documentService.updateDocument(id, document);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete document - requires authorization check at service level
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete all documents - only ADMIN can do this
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAllDocuments() {
        documentService.deleteAllDocuments();
        return ResponseEntity.noContent().build();
    }

    /**
     * Get confidential documents - only MANAGER or ADMIN
     */
    @GetMapping("/confidential")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<Document>> getConfidentialDocuments() {
        return ResponseEntity.ok(documentService.getDocumentsByClassification("CONFIDENTIAL"));
    }

    /**
     * Get secret documents - only ADMIN
     */
    @GetMapping("/secret")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Document>> getSecretDocuments() {
        return ResponseEntity.ok(documentService.getDocumentsByClassification("SECRET"));
    }
}
