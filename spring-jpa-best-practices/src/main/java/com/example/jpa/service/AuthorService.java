package com.example.jpa.service;

import com.example.jpa.domain.Author;
import com.example.jpa.domain.Book;
import com.example.jpa.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Author service demonstrating transaction best practices
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        log.info("Fetching all authors");
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Author getAuthorById(Long id) {
        log.info("Fetching author {}", id);
        return authorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
    }

    /**
     * Fetch author with books - prevents N+1
     */
    @Transactional(readOnly = true)
    public Author getAuthorWithBooks(Long id) {
        log.info("Fetching author {} with books", id);
        return authorRepository.findByIdWithBooks(id)
            .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Author getAuthorByEmail(String email) {
        log.info("Fetching author by email: {}", email);
        return authorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Author not found with email: " + email));
    }

    @Transactional
    public Author createAuthor(Author author) {
        log.info("Creating new author: {} {}", author.getFirstName(), author.getLastName());
        return authorRepository.save(author);
    }

    /**
     * Update author and their books in a single transaction
     * Demonstrates managing parent-child relationships
     */
    @Transactional
    public Author updateAuthorWithBooks(Long id, Author updatedAuthor) {
        log.info("Updating author {} with books", id);

        Author existingAuthor = authorRepository.findByIdWithBooks(id)
            .orElseThrow(() -> new RuntimeException("Author not found"));

        // Update author details
        existingAuthor.setFirstName(updatedAuthor.getFirstName());
        existingAuthor.setLastName(updatedAuthor.getLastName());
        existingAuthor.setEmail(updatedAuthor.getEmail());

        // Update books if provided
        if (updatedAuthor.getBooks() != null) {
            existingAuthor.getBooks().clear();
            for (Book book : updatedAuthor.getBooks()) {
                existingAuthor.addBook(book);
            }
        }

        return authorRepository.save(existingAuthor);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        log.info("Deleting author {}", id);

        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Author not found"));

        authorRepository.delete(author);
    }
}
