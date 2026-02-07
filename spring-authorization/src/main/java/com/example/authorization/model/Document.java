package com.example.authorization.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String owner;

    private String classification; // PUBLIC, CONFIDENTIAL, SECRET

    public Document(String title, String content, String owner, String classification) {
        this.title = title;
        this.content = content;
        this.owner = owner;
        this.classification = classification;
    }
}
