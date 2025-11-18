-- Demo Users for Spring Security Authentication Examples
-- Password: 'password' (encoded with BCrypt)
-- BCrypt hash: $2a$10$xdbKoM48VySZqVSU/cSlVeJn0VvdHGfXJwM8KXhHGKLFhHKpAyHlK

INSERT INTO users (id, username, password, email, enabled) VALUES
(1, 'admin', '$2a$10$xdbKoM48VySZqVSU/cSlVeJn0VvdHGfXJwM8KXhHGKLFhHKpAyHlK', 'admin@example.com', true),
(2, 'user', '$2a$10$xdbKoM48VySZqVSU/cSlVeJn0VvdHGfXJwM8KXhHGKLFhHKpAyHlK', 'user@example.com', true),
(3, 'john', '$2a$10$xdbKoM48VySZqVSU/cSlVeJn0VvdHGfXJwM8KXhHGKLFhHKpAyHlK', 'john@example.com', true),
(4, 'jane', '$2a$10$xdbKoM48VySZqVSU/cSlVeJn0VvdHGfXJwM8KXhHGKLFhHKpAyHlK', 'jane@example.com', true),
(5, 'bob', '$2a$10$xdbKoM48VySZqVSU/cSlVeJn0VvdHGfXJwM8KXhHGKLFhHKpAyHlK', 'bob@example.com', true);

-- User Roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_ADMIN'),
(1, 'ROLE_USER'),
(2, 'ROLE_USER'),
(3, 'ROLE_USER'),
(4, 'ROLE_USER'),
(4, 'ROLE_MANAGER'),
(5, 'ROLE_USER');
