package com.example.authorization.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Public endpoints accessible to everyone
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello! This is a public endpoint accessible to everyone.");
    }

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
            "application", "Spring Authorization Example",
            "description", "Demonstrates various Spring Security authorization features",
            "version", "1.0.0"
        );
    }
}
