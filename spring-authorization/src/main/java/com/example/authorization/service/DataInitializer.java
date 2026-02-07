package com.example.authorization.service;

import com.example.authorization.model.Document;
import com.example.authorization.model.DocumentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DocumentRepository documentRepository;

    public DataInitializer(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize sample documents
        documentRepository.save(new Document("Public Announcement", "This is a public document", "admin", "PUBLIC"));
        documentRepository.save(new Document("Company Policy", "Internal company policy", "admin", "CONFIDENTIAL"));
        documentRepository.save(new Document("Secret Project", "Top secret project details", "admin", "SECRET"));
        documentRepository.save(new Document("User Guide", "User manual for the application", "user", "PUBLIC"));
        documentRepository.save(new Document("Financial Report", "Q4 financial report", "manager", "CONFIDENTIAL"));
    }
}
