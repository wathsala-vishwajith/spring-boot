package com.example.authorization.service;

import com.example.authorization.model.Document;
import com.example.authorization.model.DocumentRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Only users with ROLE_USER can create documents
     */
    @PreAuthorize("hasRole('USER')")
    public Document createDocument(Document document) {
        return documentRepository.save(document);
    }

    /**
     * Anyone can view public documents, but ROLE_ADMIN can view all
     */
    @PreAuthorize("hasRole('ADMIN') or #classification == 'PUBLIC'")
    public List<Document> getDocumentsByClassification(String classification) {
        return documentRepository.findByClassification(classification);
    }

    /**
     * Only the owner or ADMIN can view a specific document
     * Uses PostAuthorize to check after fetching the document
     */
    @PostAuthorize("hasRole('ADMIN') or returnObject.isPresent() and returnObject.get().owner == authentication.name")
    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    /**
     * Only users with ROLE_MANAGER or ROLE_ADMIN can view all documents
     */
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    /**
     * Only the owner or ADMIN can delete a document
     * Custom SpEL expression using a bean method
     */
    @PreAuthorize("hasRole('ADMIN') or @documentSecurity.isOwner(#id, authentication.name)")
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }

    /**
     * Only ADMIN can delete all documents
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllDocuments() {
        documentRepository.deleteAll();
    }

    /**
     * Get documents owned by the current user
     */
    @PreAuthorize("isAuthenticated()")
    public List<Document> getMyDocuments(String username) {
        return documentRepository.findByOwner(username);
    }

    /**
     * Update document - only owner or ADMIN
     */
    @PreAuthorize("hasRole('ADMIN') or @documentSecurity.isOwner(#id, authentication.name)")
    public Document updateDocument(Long id, Document updatedDocument) {
        Optional<Document> existing = documentRepository.findById(id);
        if (existing.isPresent()) {
            Document doc = existing.get();
            doc.setTitle(updatedDocument.getTitle());
            doc.setContent(updatedDocument.getContent());
            doc.setClassification(updatedDocument.getClassification());
            return documentRepository.save(doc);
        }
        throw new RuntimeException("Document not found");
    }
}
