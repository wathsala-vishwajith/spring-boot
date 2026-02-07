-- V2: Insert Sample Data
-- Demonstrates: Data seeding through migrations

-- Insert sample authors
INSERT INTO authors (first_name, last_name, email) VALUES
('J.K.', 'Rowling', 'jk.rowling@example.com'),
('George R.R.', 'Martin', 'george.martin@example.com'),
('J.R.R.', 'Tolkien', 'jrr.tolkien@example.com'),
('Agatha', 'Christie', 'agatha.christie@example.com'),
('Stephen', 'King', 'stephen.king@example.com');

-- Insert sample publishers
INSERT INTO publishers (name, country, established_year) VALUES
('Bloomsbury Publishing', 'UK', 1986),
('Bantam Spectra', 'USA', 1985),
('Allen & Unwin', 'UK', 1914),
('HarperCollins', 'USA', 1989),
('Scribner', 'USA', 1846);

-- Insert sample books
INSERT INTO books (title, isbn, published_date, price, author_id, publisher_id) VALUES
('Harry Potter and the Philosopher''s Stone', '9780747532699', '1997-06-26', 19.99, 1, 1),
('Harry Potter and the Chamber of Secrets', '9780747538493', '1998-07-02', 19.99, 1, 1),
('A Game of Thrones', '9780553103540', '1996-08-01', 29.99, 2, 2),
('The Hobbit', '9780261102217', '1937-09-21', 24.99, 3, 3),
('Murder on the Orient Express', '9780062693662', '1934-01-01', 14.99, 4, 4),
('The Shining', '9780307743657', '1977-01-28', 18.99, 5, 5);

-- Insert sample categories
INSERT INTO categories (name, description) VALUES
('Fantasy', 'Fantasy fiction and magical worlds'),
('Mystery', 'Mystery and detective stories'),
('Horror', 'Horror and thriller novels'),
('Adventure', 'Adventure and quest narratives'),
('Young Adult', 'Books targeted at young adult readers');

-- Link books to categories
INSERT INTO book_categories (book_id, category_id) VALUES
(1, 1), (1, 5), -- Harry Potter 1: Fantasy, YA
(2, 1), (2, 5), -- Harry Potter 2: Fantasy, YA
(3, 1), (3, 4), -- Game of Thrones: Fantasy, Adventure
(4, 1), (4, 4), -- The Hobbit: Fantasy, Adventure
(5, 2),         -- Murder on Orient Express: Mystery
(6, 3);         -- The Shining: Horror

-- Insert sample reviews
INSERT INTO reviews (book_id, reviewer_name, rating, comment) VALUES
(1, 'John Doe', 5, 'A magical masterpiece that started it all!'),
(1, 'Jane Smith', 5, 'Perfect for all ages. Absolutely captivating.'),
(1, 'Bob Wilson', 4, 'Great book, though a bit slow in the beginning.'),
(2, 'Alice Brown', 5, 'Even better than the first one!'),
(3, 'Charlie Davis', 5, 'Epic fantasy at its finest.'),
(3, 'Diana Evans', 4, 'Great world-building but very long.'),
(4, 'Frank Miller', 5, 'A timeless classic. Must read!'),
(5, 'Grace Lee', 5, 'The best mystery novel ever written.'),
(6, 'Henry Taylor', 4, 'Terrifying and brilliant.');
