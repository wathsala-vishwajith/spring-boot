package com.example.security.controller;

import com.example.security.model.Document;
import com.example.security.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller demonstrating various authorization patterns for document operations.
 * Combines URL-based authorization (from SecurityConfig) with method security.
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Create a new document. Requires WRITE_DOCUMENTS authority.
     * Authorization enforced at service layer via @PreAuthorize.
     */
    @PostMapping("/create")
    public ResponseEntity<Document> createDocument(
        @RequestBody Document document,
        @AuthenticationPrincipal UserDetails userDetails) {

        document.setOwner(userDetails.getUsername());
        Document created = documentService.createDocument(document);
        return ResponseEntity.ok(created);
    }

    /**
     * Get a document by ID. Returns only if user is the owner.
     * Authorization enforced via @PostAuthorize at service layer.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable Long id) {
        return documentService.getDocument(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update a document. Allowed for ADMIN or document owner.
     * Authorization enforced via @PreAuthorize at service layer.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(
        @PathVariable Long id,
        @RequestBody Document document) {

        Document updated = documentService.updateDocument(id, document);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a document. Requires ADMIN role.
     * Authorization enforced both at URL level (SecurityConfig) and service level (@Secured).
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all documents. Requires ADMIN or MODERATOR role.
     * Authorization enforced via @RolesAllowed at service layer.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    /**
     * Get documents accessible by current user.
     * Filtered via @PostFilter at service layer.
     */
    @GetMapping("/my-documents")
    public ResponseEntity<List<Document>> getMyDocuments() {
        return ResponseEntity.ok(documentService.getMyDocuments());
    }

    /**
     * Get public documents. No authorization required.
     */
    @GetMapping("/public")
    public ResponseEntity<List<Document>> getPublicDocuments() {
        return ResponseEntity.ok(documentService.getPublicDocuments());
    }

    /**
     * Get a published document. Complex authorization logic.
     */
    @GetMapping("/published/{id}")
    public ResponseEntity<Document> getPublishedDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getPublishedDocument(id));
    }
}
