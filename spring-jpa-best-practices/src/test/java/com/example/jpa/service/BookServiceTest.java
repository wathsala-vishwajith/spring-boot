package com.example.jpa.service;

import com.example.jpa.domain.Author;
import com.example.jpa.domain.Book;
import com.example.jpa.repository.AuthorRepository;
import com.example.jpa.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test demonstrating transaction management
 */
@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void testGetAllBooksWithAuthors() {
        // Should use read-only transaction
        List<Book> books = bookService.getAllBooksWithAuthors();

        assertThat(books).isNotEmpty();
        books.forEach(book -> assertThat(book.getAuthor()).isNotNull());
    }

    @Test
    @Transactional
    void testCreateBook() {
        // Get an existing author
        Author author = authorRepository.findByEmail("jk.rowling@example.com")
            .orElseThrow();

        // Create new book
        Book newBook = Book.builder()
            .title("Harry Potter and the Prisoner of Azkaban")
            .isbn("9780747542155")
            .publishedDate(LocalDate.of(1999, 7, 8))
            .price(BigDecimal.valueOf(21.99))
            .author(author)
            .build();

        Book savedBook = bookService.createBook(newBook);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Harry Potter and the Prisoner of Azkaban");
        assertThat(savedBook.getAuthor().getEmail()).isEqualTo("jk.rowling@example.com");
    }

    @Test
    @Transactional
    void testUpdateBook_OptimisticLocking() {
        // Fetch existing book
        Book book = bookRepository.findById(1L).orElseThrow();
        Integer originalVersion = book.getVersion();

        // Update book
        book.setPrice(BigDecimal.valueOf(25.99));
        Book updatedBook = bookService.updateBook(book.getId(), book);

        // Version should be incremented (optimistic locking)
        assertThat(updatedBook.getVersion()).isGreaterThan(originalVersion);
    }
}
