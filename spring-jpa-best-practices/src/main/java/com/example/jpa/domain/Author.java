package com.example.jpa.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Author entity demonstrating:
 * - Proper indexing with @Table and @Index annotations
 * - Optimistic locking with @Version
 * - Audit fields with @CreationTimestamp and @UpdateTimestamp
 * - Lazy loading configuration for collections
 */
@Entity
@Table(name = "authors", indexes = {
    @Index(name = "idx_authors_last_name", columnList = "last_name"),
    @Index(name = "idx_authors_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "books") // Exclude to prevent lazy loading issues in toString()
@EqualsAndHashCode(of = "id") // Only use ID for equals/hashCode
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * One-to-Many relationship with Books
     * - LAZY fetch to prevent N+1 queries
     * - mappedBy to indicate the owning side
     * - cascade for convenience operations
     * - orphanRemoval to clean up orphaned books
     */
    @OneToMany(
        mappedBy = "author",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Book> books = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Optimistic locking - prevents lost updates in concurrent scenarios
     */
    @Version
    @Column(name = "version")
    private Integer version;

    // Helper methods for bidirectional relationship management
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}
