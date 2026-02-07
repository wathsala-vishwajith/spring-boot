package com.example.jpa.service;

import com.example.jpa.domain.Author;
import com.example.jpa.domain.Book;
import com.example.jpa.domain.Category;
import com.example.jpa.repository.AuthorRepository;
import com.example.jpa.repository.BookRepository;
import com.example.jpa.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Book service demonstrating transaction management best practices:
 * 1. Read-only transactions for queries
 * 2. Proper transaction boundaries
 * 3. Transaction propagation
 * 4. Isolation levels
 * 5. Optimistic locking handling
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    // ============================================
    // READ Operations (Read-Only Transactions)
    // ============================================

    /**
     * Read-only transaction for better performance
     * - Allows Hibernate to skip dirty checking
     * - Enables certain database optimizations
     */
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll();
    }

    /**
     * Fetch books with authors - prevents N+1
     * Read-only transaction
     */
    @Transactional(readOnly = true)
    public List<Book> getAllBooksWithAuthors() {
        log.info("Fetching all books with authors using entity graph");
        return bookRepository.findAllWithAuthors();
    }

    /**
     * Fetch book with full details
     */
    @Transactional(readOnly = true)
    public Book getBookWithFullDetails(Long id) {
        log.info("Fetching book {} with full details", id);
        return bookRepository.findWithFullDetailsById(id)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    /**
     * Search books by title - uses index
     */
    @Transactional(readOnly = true)
    public List<Book> searchBooksByTitle(String title) {
        log.info("Searching books by title: {}", title);
        return bookRepository.findByTitleContaining(title);
    }

    // ============================================
    // WRITE Operations (Default Transaction)
    // ============================================

    /**
     * Create a new book
     * Default transaction: readOnly = false, propagation = REQUIRED
     */
    @Transactional
    public Book createBook(Book book) {
        log.info("Creating new book: {}", book.getTitle());

        // Validate author exists
        if (book.getAuthor() != null && book.getAuthor().getId() != null) {
            Author author = authorRepository.findById(book.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
            book.setAuthor(author);
        }

        return bookRepository.save(book);
    }

    /**
     * Update book details
     * Demonstrates optimistic locking
     */
    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        log.info("Updating book {}", id);

        Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        // Update fields
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setPrice(updatedBook.getPrice());
        existingBook.setPublishedDate(updatedBook.getPublishedDate());

        // The version field ensures optimistic locking
        // If another transaction modified the book, this will throw OptimisticLockException
        return bookRepository.save(existingBook);
    }

    /**
     * Delete a book
     */
    @Transactional
    public void deleteBook(Long id) {
        log.info("Deleting book {}", id);

        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        bookRepository.delete(book);
    }

    // ============================================
    // Complex Operations with Proper Transaction Management
    // ============================================

    /**
     * Add categories to a book
     * Demonstrates managing bidirectional relationships in a transaction
     */
    @Transactional
    public Book addCategoriesToBook(Long bookId, List<Long> categoryIds) {
        log.info("Adding {} categories to book {}", categoryIds.size(), bookId);

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));

        List<Category> categories = categoryRepository.findAllById(categoryIds);

        // Use helper method to maintain bidirectional relationship
        for (Category category : categories) {
            book.addCategory(category);
        }

        return bookRepository.save(book);
    }

    /**
     * Batch create books
     * Demonstrates batch operations with transactions
     */
    @Transactional
    public List<Book> createBooksBatch(List<Book> books) {
        log.info("Batch creating {} books", books.size());

        // saveAll uses batch insert if configured properly
        return bookRepository.saveAll(books);
    }

    // ============================================
    // Advanced Transaction Scenarios
    // ============================================

    /**
     * Requires new transaction - always creates a new transaction
     * Use case: logging/audit that should commit even if parent transaction rolls back
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditBookAccess(Long bookId) {
        log.info("Auditing access to book {}", bookId);
        // Audit logic here - will commit independently
    }

    /**
     * Mandatory transaction - must be called within an existing transaction
     * Throws exception if no transaction exists
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void validateBookInTransaction(Book book) {
        log.info("Validating book within transaction: {}", book.getId());
        // Validation logic that must run in a transaction
    }

    /**
     * Never use transaction - throws exception if called within a transaction
     * Use case: operations that should not run in a transaction
     */
    @Transactional(propagation = Propagation.NEVER)
    public void sendBookNotification(Long bookId) {
        log.info("Sending notification for book {} (outside transaction)", bookId);
        // External service calls that shouldn't be in a transaction
    }

    /**
     * Read committed isolation level
     * Prevents dirty reads but allows non-repeatable reads
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Book getBookWithReadCommitted(Long id) {
        log.info("Fetching book {} with READ_COMMITTED isolation", id);
        return bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    /**
     * Repeatable read isolation level
     * Prevents dirty reads and non-repeatable reads
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Book getBookWithRepeatableRead(Long id) {
        log.info("Fetching book {} with REPEATABLE_READ isolation", id);
        return bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    /**
     * Timeout configuration - transaction will rollback if exceeds timeout
     * Useful for preventing long-running transactions
     */
    @Transactional(timeout = 30) // 30 seconds
    public void complexBookOperation(Long bookId) {
        log.info("Performing complex operation on book {} with 30s timeout", bookId);
        // Complex operation that should complete within 30 seconds
    }

    /**
     * Rollback on specific exceptions
     * By default, only RuntimeException and Error trigger rollback
     */
    @Transactional(rollbackFor = Exception.class)
    public Book createBookWithCheckedExceptionRollback(Book book) throws Exception {
        log.info("Creating book with rollback on all exceptions");
        // This will rollback even on checked exceptions
        return bookRepository.save(book);
    }

    /**
     * No rollback on specific exceptions
     */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public Book createBookWithNoRollbackOnIllegalArgument(Book book) {
        log.info("Creating book - won't rollback on IllegalArgumentException");
        return bookRepository.save(book);
    }
}
