package com.example.jpa.repository;

import com.example.jpa.domain.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Book repository demonstrating N+1 query prevention techniques:
 * 1. Named Entity Graphs
 * 2. @EntityGraph annotation
 * 3. JPQL with JOIN FETCH
 * 4. Custom queries with projections
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ============================================
    // N+1 Prevention: Using Named Entity Graphs
    // ============================================

    /**
     * Find book with author using named entity graph
     * Prevents N+1 when accessing book.getAuthor()
     */
    @EntityGraph(value = "Book.withAuthor")
    Optional<Book> findWithAuthorById(Long id);

    /**
     * Find all books with their authors in one query
     */
    @EntityGraph(value = "Book.withAuthor")
    List<Book> findAllWithAuthors();

    /**
     * Find books with categories
     */
    @EntityGraph(value = "Book.withCategories")
    List<Book> findAllWithCategories();

    /**
     * Find books with full details (author, publisher, categories, reviews)
     */
    @EntityGraph(value = "Book.full")
    List<Book> findAllWithFullDetails();

    /**
     * Find book by ID with all associations
     */
    @EntityGraph(value = "Book.full")
    Optional<Book> findWithFullDetailsById(Long id);

    // ============================================
    // N+1 Prevention: Using JPQL JOIN FETCH
    // ============================================

    /**
     * Custom query with JOIN FETCH for author
     * Alternative to entity graphs
     */
    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE b.id = :id")
    Optional<Book> findByIdWithAuthor(@Param("id") Long id);

    /**
     * Fetch all books with their authors using JOIN FETCH
     */
    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.author")
    List<Book> findAllBooksWithAuthors();

    /**
     * Fetch books with author and publisher
     * Multiple JOIN FETCH in single query
     */
    @Query("SELECT DISTINCT b FROM Book b " +
           "JOIN FETCH b.author " +
           "LEFT JOIN FETCH b.publisher")
    List<Book> findAllWithAuthorAndPublisher();

    /**
     * Fetch books with categories (many-to-many)
     * NOTE: Use LEFT JOIN FETCH for optional relationships
     */
    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN FETCH b.categories")
    List<Book> findAllWithCategoriesFetch();

    /**
     * Complex fetch: books with author, publisher, and categories
     * Demonstrates multiple JOIN FETCH in one query
     */
    @Query("SELECT DISTINCT b FROM Book b " +
           "JOIN FETCH b.author a " +
           "LEFT JOIN FETCH b.publisher p " +
           "LEFT JOIN FETCH b.categories c")
    List<Book> findAllWithCompleteDetails();

    // ============================================
    // Indexed Column Queries (Efficient)
    // ============================================

    /**
     * Find by ISBN - uses index (idx_books_isbn)
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Find by title - uses index (idx_books_title)
     */
    List<Book> findByTitleContaining(String title);

    /**
     * Find by author ID - uses index (idx_books_author_id)
     */
    @EntityGraph(value = "Book.withAuthor")
    List<Book> findByAuthorId(Long authorId);

    // ============================================
    // Custom Projections (Only fetch needed columns)
    // ============================================

    /**
     * Projection to fetch only specific fields
     * More efficient than fetching entire entity
     */
    @Query("SELECT b.id as id, b.title as title, b.isbn as isbn, " +
           "a.firstName as authorFirstName, a.lastName as authorLastName " +
           "FROM Book b JOIN b.author a")
    List<BookSummaryProjection> findAllBookSummaries();

    /**
     * Projection interface for custom DTOs
     */
    interface BookSummaryProjection {
        Long getId();
        String getTitle();
        String getIsbn();
        String getAuthorFirstName();
        String getAuthorLastName();
    }

    // ============================================
    // Batch Operations
    // ============================================

    /**
     * Find books by multiple IDs - efficient batch fetch
     */
    @EntityGraph(value = "Book.withAuthor")
    List<Book> findByIdIn(List<Long> ids);
}
