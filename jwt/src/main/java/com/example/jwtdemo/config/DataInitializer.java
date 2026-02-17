package com.example.jwtdemo.config;

import com.example.jwtdemo.model.User;
import com.example.jwtdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Data initializer to create demo users on application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create a regular user
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("password"));
            user.setEmail("user@example.com");
            user.setRoles(Set.of("ROLE_USER"));
            user.setEnabled(true);
            userRepository.save(user);
            System.out.println("Created user: user / password");
        }

        // Create an admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setRoles(Set.of("ROLE_USER", "ROLE_ADMIN"));
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("Created admin: admin / admin123");
        }
    }
}
