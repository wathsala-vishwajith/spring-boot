package com.example.jpa.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Book entity demonstrating:
 * - Composite indexes for common query patterns
 * - Many-to-One relationships with proper fetch strategies
 * - Many-to-Many relationship with proper configuration
 * - Named entity graphs for N+1 prevention
 */
@Entity
@Table(name = "books", indexes = {
    @Index(name = "idx_books_author_id", columnList = "author_id"),
    @Index(name = "idx_books_title", columnList = "title"),
    @Index(name = "idx_books_isbn", columnList = "isbn"),
    @Index(name = "idx_books_published_date", columnList = "published_date")
})
@NamedEntityGraphs({
    // Entity graph to fetch book with author (prevents N+1)
    @NamedEntityGraph(
        name = "Book.withAuthor",
        attributeNodes = @NamedAttributeNode("author")
    ),
    // Entity graph to fetch book with categories (prevents N+1)
    @NamedEntityGraph(
        name = "Book.withCategories",
        attributeNodes = @NamedAttributeNode("categories")
    ),
    // Entity graph to fetch book with reviews (prevents N+1)
    @NamedEntityGraph(
        name = "Book.withReviews",
        attributeNodes = @NamedAttributeNode("reviews")
    ),
    // Entity graph to fetch book with all associations
    @NamedEntityGraph(
        name = "Book.full",
        attributeNodes = {
            @NamedAttributeNode("author"),
            @NamedAttributeNode("publisher"),
            @NamedAttributeNode("categories"),
            @NamedAttributeNode("reviews")
        }
    )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"author", "publisher", "categories", "reviews"})
@EqualsAndHashCode(of = "id")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "isbn", nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Many-to-One with Author
     * - LAZY fetch by default (best practice)
     * - Use entity graphs or fetch joins to prevent N+1
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_books_author"))
    private Author author;

    /**
     * Many-to-One with Publisher
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", foreignKey = @ForeignKey(name = "fk_books_publisher"))
    private Publisher publisher;

    /**
     * Many-to-Many with Categories
     * - Use Set instead of List for better performance
     * - LAZY fetch with entity graphs when needed
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_categories",
        joinColumns = @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "fk_book_categories_book")),
        inverseJoinColumns = @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_book_categories_category"))
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    /**
     * One-to-Many with Reviews
     */
    @OneToMany(
        mappedBy = "book",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Integer version;

    // Helper methods for bidirectional relationships
    public void addCategory(Category category) {
        categories.add(category);
        category.getBooks().add(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getBooks().remove(this);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setBook(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setBook(null);
    }
}
