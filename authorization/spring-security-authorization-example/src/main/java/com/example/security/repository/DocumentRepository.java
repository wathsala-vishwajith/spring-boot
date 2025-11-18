package com.example.security.repository;

import com.example.security.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByOwner(String owner);
    List<Document> findByStatus(Document.DocumentStatus status);
}
