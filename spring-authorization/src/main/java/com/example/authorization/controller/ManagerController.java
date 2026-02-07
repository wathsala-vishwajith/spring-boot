package com.example.authorization.controller;

import com.example.authorization.service.DocumentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Manager controller - accessible to MANAGER and ADMIN roles
 */
@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    private final DocumentService documentService;

    public ManagerController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public Map<String, Object> managerDashboard() {
        long totalDocuments = documentService.getAllDocuments().size();
        return Map.of(
            "message", "Welcome to Manager Dashboard",
            "access", "MANAGER and ADMIN roles can access this endpoint",
            "totalDocuments", totalDocuments
        );
    }

    @GetMapping("/reports")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public Map<String, String> getReports() {
        return Map.of(
            "message", "Manager Reports",
            "info", "This endpoint demonstrates role hierarchy - both MANAGER and ADMIN can access"
        );
    }
}
