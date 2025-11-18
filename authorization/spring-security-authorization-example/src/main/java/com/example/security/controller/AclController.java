package com.example.security.controller;

import com.example.security.model.Document;
import com.example.security.service.AclDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller demonstrating ACL (Access Control Lists) functionality.
 * Shows how to manage instance-level permissions for documents.
 */
@RestController
@RequestMapping("/api/acl")
@RequiredArgsConstructor
public class AclController {

    private final AclDocumentService aclDocumentService;

    /**
     * Create a document with ACL permissions.
     * The creator gets full permissions automatically.
     */
    @PostMapping("/documents")
    public ResponseEntity<Document> createDocument(
        @RequestBody Document document,
        @AuthenticationPrincipal UserDetails userDetails) {

        document.setOwner(userDetails.getUsername());
        Document created = aclDocumentService.createDocumentWithAcl(document);
        return ResponseEntity.ok(created);
    }

    /**
     * Get a document. Access controlled by ACL - user must have READ permission.
     */
    @GetMapping("/documents/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable Long id) {
        return ResponseEntity.ok(aclDocumentService.getDocumentWithAcl(id));
    }

    /**
     * Update a document. User must have WRITE permission via ACL.
     */
    @PutMapping("/documents/{id}")
    public ResponseEntity<Document> updateDocument(
        @PathVariable Long id,
        @RequestBody Document document) {

        Document updated = aclDocumentService.updateDocumentWithAcl(id, document);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a document. User must have DELETE permission via ACL.
     */
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        aclDocumentService.deleteDocumentWithAcl(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all documents where current user has READ permission.
     */
    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getAccessibleDocuments() {
        return ResponseEntity.ok(aclDocumentService.getAccessibleDocuments());
    }

    /**
     * Grant READ permission to another user.
     * Only document administrators can grant permissions.
     */
    @PostMapping("/documents/{id}/grant-read")
    public ResponseEntity<String> grantReadPermission(
        @PathVariable Long id,
        @RequestBody Map<String, String> request) {

        String username = request.get("username");
        aclDocumentService.grantReadPermission(id, username);
        return ResponseEntity.ok("READ permission granted to " + username);
    }

    /**
     * Grant WRITE permission to another user.
     */
    @PostMapping("/documents/{id}/grant-write")
    public ResponseEntity<String> grantWritePermission(
        @PathVariable Long id,
        @RequestBody Map<String, String> request) {

        String username = request.get("username");
        aclDocumentService.grantWritePermission(id, username);
        return ResponseEntity.ok("WRITE permission granted to " + username);
    }

    /**
     * Revoke all permissions for a user.
     */
    @PostMapping("/documents/{id}/revoke")
    public ResponseEntity<String> revokePermissions(
        @PathVariable Long id,
        @RequestBody Map<String, String> request) {

        String username = request.get("username");
        aclDocumentService.revokePermissions(id, username);
        return ResponseEntity.ok("All permissions revoked for " + username);
    }
}
