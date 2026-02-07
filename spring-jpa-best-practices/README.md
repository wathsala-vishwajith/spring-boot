# Spring Data JPA Best Practices

A comprehensive demonstration of database design and Spring Data JPA best practices including proper indexing, N+1 query prevention, connection pooling, transaction management, and database migrations with Flyway.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Best Practices Demonstrated](#best-practices-demonstrated)
  - [1. Proper Indexing](#1-proper-indexing)
  - [2. N+1 Query Prevention](#2-n1-query-prevention)
  - [3. Connection Pooling (HikariCP)](#3-connection-pooling-hikaricp)
  - [4. Transaction Management](#4-transaction-management)
  - [5. Database Migrations (Flyway)](#5-database-migrations-flyway)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [Monitoring](#monitoring)
- [Key Takeaways](#key-takeaways)

## Features

- **Proper Database Indexing**: Demonstrates strategic index placement on columns used in queries
- **N+1 Query Prevention**: Multiple techniques including Entity Graphs, JOIN FETCH, and projections
- **HikariCP Connection Pooling**: Optimized connection pool configuration with monitoring
- **Transaction Management**: Best practices for transaction boundaries, isolation levels, and propagation
- **Flyway Migrations**: Version-controlled database schema changes
- **Optimistic Locking**: Concurrent update handling with `@Version`
- **Audit Fields**: Automatic timestamp tracking with `@CreationTimestamp` and `@UpdateTimestamp`
- **Performance Monitoring**: Hibernate statistics and connection pool metrics

## Technologies

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Hibernate
- HikariCP (Connection Pooling)
- Flyway (Database Migrations)
- H2 Database (for demo/testing)
- PostgreSQL (production-ready configuration)
- Lombok
- Maven

## Project Structure

```
spring-jpa-best-practices/
├── src/main/java/com/example/jpa/
│   ├── config/              # Configuration classes
│   │   ├── DatabaseConfig.java
│   │   ├── HikariPoolMonitor.java
│   │   └── JpaConfig.java
│   ├── domain/              # Entity classes
│   │   ├── Author.java
│   │   ├── Book.java
│   │   ├── Category.java
│   │   ├── Publisher.java
│   │   └── Review.java
│   ├── repository/          # Repository interfaces
│   │   ├── AuthorRepository.java
│   │   ├── BookRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── PublisherRepository.java
│   │   └── ReviewRepository.java
│   ├── service/             # Service layer
│   │   ├── AuthorService.java
│   │   └── BookService.java
│   └── JpaApplication.java  # Main application
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/        # Flyway migrations
│       ├── V1__create_initial_schema.sql
│       └── V2__add_sample_data.sql
└── pom.xml
```

## Database Schema

The project includes a book catalog system with the following entities:

- **Authors**: Book authors with indexed email and last name
- **Books**: Books with relationships to authors, publishers, and categories
- **Publishers**: Publishing companies
- **Categories**: Book categories (many-to-many with books)
- **Reviews**: Book reviews (one-to-many with books)

## Best Practices Demonstrated

### 1. Proper Indexing

#### What is Indexing?

Database indexes are data structures that improve query performance by allowing faster data retrieval. Think of them like a book's index - instead of scanning every page, you can quickly find what you need.

#### When to Add Indexes

✅ **Add indexes on:**
- Primary keys (automatic)
- Foreign keys (for JOIN performance)
- Columns used in WHERE clauses
- Columns used in ORDER BY
- Columns used in JOIN conditions
- Unique constraint columns

❌ **Avoid indexes on:**
- Small tables (< 1000 rows)
- Columns with high update frequency and low query frequency
- Columns with low cardinality (few distinct values)
- Wide columns (large text/blob)

#### Examples in this Project

```java
@Entity
@Table(name = "authors", indexes = {
    @Index(name = "idx_authors_last_name", columnList = "last_name"),
    @Index(name = "idx_authors_email", columnList = "email")
})
public class Author {
    // Email is queried frequently for lookups
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Last name is used in search queries
    @Column(name = "last_name", nullable = false)
    private String lastName;
}
```

**Composite Index Example:**
```java
@Table(name = "reviews", indexes = {
    @Index(name = "idx_reviews_book_rating", columnList = "book_id, rating")
})
```

This composite index is efficient for queries like:
```sql
SELECT * FROM reviews WHERE book_id = ? AND rating >= ?
```

### 2. N+1 Query Prevention

#### What is the N+1 Problem?

The N+1 query problem occurs when:
1. You execute 1 query to fetch N parent records
2. Then execute N additional queries to fetch related child records (one per parent)

**Example of N+1 Problem:**
```java
// BAD: Causes N+1 queries
List<Book> books = bookRepository.findAll(); // 1 query
for (Book book : books) {
    String authorName = book.getAuthor().getName(); // N queries!
}
```

This executes 1 + N queries total (1 to fetch books, N to fetch each book's author).

#### Solutions Demonstrated

**Solution 1: Named Entity Graphs**
```java
@NamedEntityGraph(
    name = "Book.withAuthor",
    attributeNodes = @NamedAttributeNode("author")
)
public class Book { ... }

// In Repository
@EntityGraph(value = "Book.withAuthor")
List<Book> findAllWithAuthors();
```

**Solution 2: JPQL JOIN FETCH**
```java
@Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.author")
List<Book> findAllBooksWithAuthors();
```

**Solution 3: Projections (fetch only needed columns)**
```java
@Query("SELECT b.id as id, b.title as title, a.name as authorName " +
       "FROM Book b JOIN b.author a")
List<BookSummaryProjection> findAllBookSummaries();
```

**Solution 4: Batch Fetching**
```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=20
```

### 3. Connection Pooling (HikariCP)

#### Why Connection Pooling?

Creating database connections is expensive (time and resources). Connection pooling:
- Reuses existing connections
- Reduces connection creation overhead
- Controls maximum database load
- Improves application performance

#### HikariCP Configuration Best Practices

```properties
# Maximum pool size - match your database connection limit
spring.datasource.hikari.maximum-pool-size=10

# Minimum idle - keep connections ready
spring.datasource.hikari.minimum-idle=5

# Connection timeout - how long to wait for a connection
spring.datasource.hikari.connection-timeout=20000

# Max lifetime - recycle connections (prevent stale connections)
spring.datasource.hikari.max-lifetime=1800000

# Idle timeout - remove idle connections
spring.datasource.hikari.idle-timeout=600000

# Leak detection - warn about unclosed connections
spring.datasource.hikari.leak-detection-threshold=60000
```

#### Sizing Guidelines

**Formula**: `connections = ((core_count * 2) + effective_spindle_count)`

For most applications:
- **Small apps**: 5-10 connections
- **Medium apps**: 10-20 connections
- **Large apps**: 20-50 connections

**Don't over-provision!** More connections ≠ better performance.

#### Monitoring

The project includes `HikariPoolMonitor` that logs:
- Active connections
- Idle connections
- Threads waiting for connections
- Pool utilization percentage

### 4. Transaction Management

#### Read-Only Transactions

Use `@Transactional(readOnly = true)` for query methods:

```java
@Transactional(readOnly = true)
public List<Book> getAllBooks() {
    return bookRepository.findAll();
}
```

**Benefits:**
- Skips dirty checking (performance)
- Allows database optimizations
- Prevents accidental writes

#### Transaction Propagation

```java
// REQUIRED (default) - use existing or create new
@Transactional(propagation = Propagation.REQUIRED)

// REQUIRES_NEW - always create new transaction
@Transactional(propagation = Propagation.REQUIRES_NEW)

// MANDATORY - must be called within transaction
@Transactional(propagation = Propagation.MANDATORY)

// NEVER - must NOT be in transaction
@Transactional(propagation = Propagation.NEVER)
```

#### Isolation Levels

```java
// READ_COMMITTED - prevents dirty reads
@Transactional(isolation = Isolation.READ_COMMITTED)

// REPEATABLE_READ - prevents dirty and non-repeatable reads
@Transactional(isolation = Isolation.REPEATABLE_READ)

// SERIALIZABLE - full isolation (slowest)
@Transactional(isolation = Isolation.SERIALIZABLE)
```

#### Optimistic Locking

Prevents lost updates in concurrent scenarios:

```java
@Entity
public class Book {
    @Version
    private Integer version;
}
```

When two transactions try to update the same entity, the second one fails with `OptimisticLockException`.

### 5. Database Migrations (Flyway)

#### Why Use Flyway?

- **Version Control**: Track database schema changes
- **Reproducibility**: Same schema across all environments
- **Automation**: Apply migrations automatically on startup
- **Rollback**: Know exactly what changed and when

#### Migration Naming Convention

```
V{version}__{description}.sql

Examples:
V1__create_initial_schema.sql
V2__add_sample_data.sql
V3__add_user_table.sql
V4__add_index_to_users.sql
```

#### Best Practices

1. **Never modify existing migrations** - create new ones
2. **Test migrations** on a copy of production data
3. **Keep migrations small** - one logical change per migration
4. **Include rollback plan** - know how to undo changes
5. **Use baseline** for existing databases

```properties
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- (Optional) PostgreSQL database

### Installation

1. Clone the repository:
```bash
cd spring-jpa-best-practices
```

2. Build the project:
```bash
mvn clean install
```

## Running the Application

### Using H2 In-Memory Database (Default)

```bash
mvn spring-boot:run
```

Access H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave blank)

### Using PostgreSQL

1. Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/jpa_demo
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

2. Run:
```bash
mvn spring-boot:run
```

## Monitoring

### Hibernate Statistics

Check logs for periodic statistics:
```
==== Hibernate Statistics ====
Queries executed: 45
Entities loaded: 120
Collections loaded: 30
...
```

**Warning signs:**
- High entity loads per query (N+1 problem)
- Many collection loads (lazy loading issues)
- High optimistic lock failures (concurrency issues)

### HikariCP Pool Statistics

```
==== HikariCP Pool Statistics ====
Active Connections: 3
Idle Connections: 7
Total Connections: 10
Threads Awaiting Connection: 0
```

**Warning signs:**
- Threads awaiting connection > 0 (pool exhausted)
- Active connections consistently > 80% (need more connections)
- Idle connections always 0 (pool too small)

## Key Takeaways

### Indexing
- ✅ Index foreign keys, WHERE clause columns, and JOIN columns
- ✅ Use composite indexes for multi-column queries
- ❌ Don't over-index (slows down writes)

### N+1 Prevention
- ✅ Use Entity Graphs or JOIN FETCH for associations
- ✅ Enable Hibernate statistics to detect N+1 queries
- ✅ Use projections when you don't need full entities

### Connection Pooling
- ✅ Configure pool size based on workload, not guessing
- ✅ Monitor pool metrics in production
- ✅ Set leak detection threshold in development
- ❌ Don't set pool size too high

### Transactions
- ✅ Use `readOnly = true` for query methods
- ✅ Keep transactions short and focused
- ✅ Use optimistic locking for concurrent updates
- ❌ Don't nest transactions unnecessarily

### Migrations
- ✅ Version control all schema changes
- ✅ Test migrations on production-like data
- ✅ Never modify existing migrations
- ❌ Don't make breaking changes without migration path

## Further Reading

- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Hibernate Performance Tuning](https://vladmihalcea.com/tutorials/hibernate/)

## License

This project is created for educational purposes demonstrating Spring Data JPA best practices.
