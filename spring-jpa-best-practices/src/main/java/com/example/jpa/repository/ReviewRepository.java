package com.example.jpa.repository;

import com.example.jpa.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Review repository demonstrating composite index usage
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Find reviews by book ID - uses index (idx_reviews_book_id)
     */
    List<Review> findByBookId(Long bookId);

    /**
     * Find reviews by rating - uses index (idx_reviews_rating)
     */
    List<Review> findByRating(Integer rating);

    /**
     * Find reviews by book and rating - uses composite index (idx_reviews_book_rating)
     * This is more efficient than filtering separately
     */
    List<Review> findByBookIdAndRating(Long bookId, Integer rating);

    /**
     * Find reviews with minimum rating for a book
     * Uses composite index efficiently
     */
    @Query("SELECT r FROM Review r WHERE r.book.id = :bookId AND r.rating >= :minRating")
    List<Review> findByBookIdAndMinimumRating(
        @Param("bookId") Long bookId,
        @Param("minRating") Integer minRating
    );

    /**
     * Calculate average rating for a book
     * Efficient aggregation query
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);

    /**
     * Count reviews by rating for a book
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.book.id = :bookId GROUP BY r.rating")
    List<Object[]> countReviewsByRatingForBook(@Param("bookId") Long bookId);
}
