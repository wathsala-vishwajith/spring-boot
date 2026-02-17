package com.example.userservice.security;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Custom UserDetailsService
 *
 * Implements Spring Security's UserDetailsService interface.
 * Loads user from database for authentication.
 *
 * Flow:
 * 1. Spring Security calls loadUserByUsername during authentication
 * 2. We query database for user
 * 3. Return UserDetails object with username and password
 * 4. Spring Security compares provided password with stored hash
 *
 * Learning Points:
 * - UserDetails is Spring Security's representation of a user
 * - We convert our User entity to UserDetails format
 * - This abstraction allows Spring Security to work with any user model
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by username for authentication
     *
     * Called by Spring Security during login process.
     * Returns UserDetails with username and hashed password.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert our User entity to Spring Security's UserDetails
        // Empty ArrayList for authorities (roles/permissions) - can be extended later
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getActive(),
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                new ArrayList<>() // authorities - can add roles here later
        );
    }
}
