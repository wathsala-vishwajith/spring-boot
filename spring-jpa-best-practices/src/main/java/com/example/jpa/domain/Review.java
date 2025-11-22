package com.example.jpa.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Review entity demonstrating:
 * - Composite indexes for filtering
 * - Check constraints
 * - Proper relationship configuration
 */
@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_reviews_book_id", columnList = "book_id"),
    @Index(name = "idx_reviews_rating", columnList = "rating"),
    @Index(name = "idx_reviews_book_rating", columnList = "book_id, rating")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "book")
@EqualsAndHashCode(of = "id")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_book"))
    private Book book;

    @Column(name = "reviewer_name", nullable = false, length = 100)
    private String reviewerName;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5 (validated by database constraint)

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    @Column(name = "version")
    private Integer version;

    @PrePersist
    @PreUpdate
    private void validateRating() {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}
