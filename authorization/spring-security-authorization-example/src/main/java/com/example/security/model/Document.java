package com.example.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Document entity used to demonstrate ACL (Access Control Lists) and
 * object-level authorization.
 */
@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String content;

    @Column(nullable = false)
    private String owner;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status = DocumentStatus.DRAFT;

    public Document(String title, String content, String owner) {
        this.title = title;
        this.content = content;
        this.owner = owner;
    }

    public enum DocumentStatus {
        DRAFT, PUBLISHED, ARCHIVED
    }
}
