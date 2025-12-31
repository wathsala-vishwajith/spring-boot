package com.example.security.service;

import com.example.security.model.Document;
import com.example.security.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service demonstrating ACL (Access Control Lists) usage.
 *
 * ACLs provide fine-grained, instance-level security. Each domain object
 * can have its own set of permissions for different users/roles.
 *
 * Example: User A can READ Document 1 but not Document 2,
 *          User B can WRITE Document 2 but not Document 1.
 *
 * This is more flexible than role-based security which applies globally.
 */
@Service
@RequiredArgsConstructor
public class AclDocumentService {

    private final DocumentRepository documentRepository;
    private final MutableAclService aclService;

    /**
     * Create a document and set up ACL permissions.
     * The creator gets full permissions (READ, WRITE, DELETE, ADMIN).
     */
    @Transactional
    public Document createDocumentWithAcl(Document document) {
        // Save the document
        Document savedDocument = documentRepository.save(document);

        // Create ACL for the document
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(
            Document.class, savedDocument.getId());

        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(objectIdentity);
        } catch (NotFoundException e) {
            acl = aclService.createAcl(objectIdentity);
        }

        // Grant permissions to the owner
        Sid owner = new PrincipalSid(document.getOwner());
        acl.setOwner(owner);
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, owner, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, owner, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, owner, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, owner, true);

        aclService.updateAcl(acl);

        return savedDocument;
    }

    /**
     * Grant READ permission to a user for a specific document.
     *
     * @param documentId The document to grant access to
     * @param username The user to grant access to
     */
    @PreAuthorize("hasPermission(#documentId, 'com.example.security.model.Document', 'ADMINISTRATION')")
    @Transactional
    public void grantReadPermission(Long documentId, String username) {
        addPermission(documentId, username, BasePermission.READ);
    }

    /**
     * Grant WRITE permission to a user for a specific document.
     */
    @PreAuthorize("hasPermission(#documentId, 'com.example.security.model.Document', 'ADMINISTRATION')")
    @Transactional
    public void grantWritePermission(Long documentId, String username) {
        addPermission(documentId, username, BasePermission.WRITE);
    }

    /**
     * Revoke all permissions for a user on a specific document.
     */
    @PreAuthorize("hasPermission(#documentId, 'com.example.security.model.Document', 'ADMINISTRATION')")
    @Transactional
    public void revokePermissions(Long documentId, String username) {
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(Document.class, documentId);
        MutableAcl acl = (MutableAcl) aclService.readAclById(objectIdentity);

        Sid sid = new PrincipalSid(username);

        // Remove all ACEs for this user
        List<?> entries = acl.getEntries();
        for (int i = entries.size() - 1; i >= 0; i--) {
            if (acl.getEntries().get(i).getSid().equals(sid)) {
                acl.deleteAce(i);
            }
        }

        aclService.updateAcl(acl);
    }

    /**
     * Get a document. Access checked via ACL - user must have READ permission.
     * hasPermission(object, permission) is provided by AclPermissionEvaluator.
     */
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public Document getDocumentWithAcl(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    /**
     * Update a document. User must have WRITE permission via ACL.
     */
    @PreAuthorize("hasPermission(#id, 'com.example.security.model.Document', 'WRITE')")
    @Transactional
    public Document updateDocumentWithAcl(Long id, Document updatedDocument) {
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
     * Delete a document and its ACL. User must have DELETE permission.
     */
    @PreAuthorize("hasPermission(#id, 'com.example.security.model.Document', 'DELETE')")
    @Transactional
    public void deleteDocumentWithAcl(Long id) {
        documentRepository.deleteById(id);

        // Delete the ACL
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(Document.class, id);
        aclService.deleteAcl(objectIdentity, true);
    }

    /**
     * Get all documents where current user has READ permission.
     * Uses @PostFilter with hasPermission to filter results.
     */
    @PostFilter("hasPermission(filterObject, 'READ')")
    public List<Document> getAccessibleDocuments() {
        return documentRepository.findAll();
    }

    /**
     * Helper method to add a permission to an ACL.
     */
    private void addPermission(Long documentId, String username, Permission permission) {
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(Document.class, documentId);
        MutableAcl acl = (MutableAcl) aclService.readAclById(objectIdentity);

        Sid sid = new PrincipalSid(username);
        acl.insertAce(acl.getEntries().size(), permission, sid, true);

        aclService.updateAcl(acl);
    }
}
