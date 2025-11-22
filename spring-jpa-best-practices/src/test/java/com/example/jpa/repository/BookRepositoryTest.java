package com.example.jpa.repository;

import com.example.jpa.domain.Author;
import com.example.jpa.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test demonstrating N+1 query prevention
 * Run with SQL logging to see the difference
 */
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void testFindAllWithAuthors_PreventNPlusOne() {
        // This should execute only 1 query (with JOIN)
        // instead of 1 + N queries
        List<Book> books = bookRepository.findAllWithAuthors();

        assertThat(books).isNotEmpty();

        // Accessing author should not trigger additional queries
        books.forEach(book -> {
            assertThat(book.getAuthor()).isNotNull();
            assertThat(book.getAuthor().getLastName()).isNotEmpty();
        });
    }

    @Test
    void testFindByIdWithAuthor() {
        // Fetch book with author in single query
        Optional<Book> bookOpt = bookRepository.findWithAuthorById(1L);

        assertThat(bookOpt).isPresent();

        Book book = bookOpt.get();
        assertThat(book.getAuthor()).isNotNull();
        assertThat(book.getAuthor().getEmail()).isNotEmpty();
    }

    @Test
    void testFindWithFullDetails() {
        // Fetch book with all associations in single query
        List<Book> books = bookRepository.findAllWithFullDetails();

        assertThat(books).isNotEmpty();

        books.forEach(book -> {
            assertThat(book.getAuthor()).isNotNull();
            // Categories and reviews might be empty but should be loaded
            assertThat(book.getCategories()).isNotNull();
            assertThat(book.getReviews()).isNotNull();
        });
    }

    @Test
    void testFindByIsbn_UsesIndex() {
        // This query should use the idx_books_isbn index
        Optional<Book> book = bookRepository.findByIsbn("9780747532699");

        assertThat(book).isPresent();
        assertThat(book.get().getTitle()).contains("Harry Potter");
    }

    @Test
    void testProjection() {
        // Fetch only needed columns (more efficient)
        List<BookRepository.BookSummaryProjection> summaries =
            bookRepository.findAllBookSummaries();

        assertThat(summaries).isNotEmpty();

        summaries.forEach(summary -> {
            assertThat(summary.getTitle()).isNotEmpty();
            assertThat(summary.getAuthorLastName()).isNotEmpty();
        });
    }
}
