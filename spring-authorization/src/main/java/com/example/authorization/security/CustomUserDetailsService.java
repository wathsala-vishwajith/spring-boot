package com.example.authorization.security;

import com.example.authorization.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation
 * In a real application, this would load users from a database
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final Map<String, User> users;

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        // Initialize in-memory users with different roles
        users = new HashMap<>();

        users.put("admin", new User(
            "admin",
            passwordEncoder.encode("admin123"),
            Set.of("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER")
        ));

        users.put("manager", new User(
            "manager",
            passwordEncoder.encode("manager123"),
            Set.of("ROLE_MANAGER", "ROLE_USER")
        ));

        users.put("user", new User(
            "user",
            passwordEncoder.encode("user123"),
            Set.of("ROLE_USER")
        ));

        users.put("guest", new User(
            "guest",
            passwordEncoder.encode("guest123"),
            Set.of("ROLE_GUEST")
        ));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!user.isEnabled())
            .build();
    }

    public Map<String, User> getAllUsers() {
        return new HashMap<>(users);
    }
}
