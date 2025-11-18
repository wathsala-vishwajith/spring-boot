package com.example.security.config;

import com.example.security.model.Document;
import com.example.security.model.Role;
import com.example.security.model.User;
import com.example.security.repository.DocumentRepository;
import com.example.security.repository.RoleRepository;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Data initializer to populate the database with sample data.
 * This creates users with different roles and authorities for testing.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("Initializing test data...");

            // Create roles with different authorities
            Role adminRole = createRole("ADMIN",
                "READ_DOCUMENTS", "WRITE_DOCUMENTS", "DELETE_DOCUMENTS",
                "READ_REPORTS", "WRITE_REPORTS", "CUSTOM_ACCESS", "ADVANCED_ACCESS");

            Role userRole = createRole("USER",
                "READ_DOCUMENTS", "WRITE_DOCUMENTS");

            Role moderatorRole = createRole("MODERATOR",
                "READ_DOCUMENTS", "WRITE_DOCUMENTS", "READ_REPORTS");

            Role viewerRole = createRole("VIEWER",
                "READ_DOCUMENTS");

            // Create users with different roles
            User admin = createUser("admin", "admin123");
            admin.addRole(adminRole);
            userRepository.save(admin);

            User user1 = createUser("user1", "password1");
            user1.addRole(userRole);
            userRepository.save(user1);

            User user2 = createUser("user2", "password2");
            user2.addRole(userRole);
            userRepository.save(user2);

            User moderator = createUser("moderator", "mod123");
            moderator.addRole(moderatorRole);
            userRepository.save(moderator);

            User viewer = createUser("viewer", "view123");
            viewer.addRole(viewerRole);
            userRepository.save(viewer);

            // Create sample documents
            createDocument("Public Document", "This is a public document", "admin",
                Document.DocumentStatus.PUBLISHED);

            createDocument("Admin's Draft", "Admin's draft document", "admin",
                Document.DocumentStatus.DRAFT);

            createDocument("User1's Document", "User1's personal document", "user1",
                Document.DocumentStatus.DRAFT);

            createDocument("User2's Document", "User2's personal document", "user2",
                Document.DocumentStatus.PUBLISHED);

            log.info("Test data initialized successfully!");
            log.info("\n=== Available Users ===");
            log.info("Username: admin     | Password: admin123    | Role: ADMIN");
            log.info("Username: user1     | Password: password1   | Role: USER");
            log.info("Username: user2     | Password: password2   | Role: USER");
            log.info("Username: moderator | Password: mod123      | Role: MODERATOR");
            log.info("Username: viewer    | Password: view123     | Role: VIEWER");
            log.info("========================\n");
        };
    }

    private Role createRole(String name, String... authorities) {
        Role role = new Role(name);
        for (String authority : authorities) {
            role.addAuthority(authority);
        }
        return roleRepository.save(role);
    }

    private User createUser(String username, String password) {
        return new User(username, passwordEncoder.encode(password));
    }

    private void createDocument(String title, String content, String owner,
                               Document.DocumentStatus status) {
        Document document = new Document(title, content, owner);
        document.setStatus(status);
        documentRepository.save(document);
    }
}
