# Spring Boot Caching Strategies

A comprehensive demonstration of different caching strategies in Spring Boot, including in-memory caching, distributed caching, and various cache invalidation patterns.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [Caching Strategies](#caching-strategies)
- [Cache Invalidation Patterns](#cache-invalidation-patterns)
- [API Endpoints](#api-endpoints)
- [Performance Comparison](#performance-comparison)
- [When to Use Each Strategy](#when-to-use-each-strategy)
- [Best Practices](#best-practices)

## Overview

This project demonstrates various caching strategies and their practical implementations in Spring Boot. It provides real-world examples of:

- **In-Memory Caching** (Caffeine, Ehcache)
- **Distributed Caching** (Redis)
- **Cache Invalidation Patterns**
- **Performance comparisons**
- **Best practices and use cases**

## Features

- Multiple cache providers (Caffeine, Ehcache, Redis)
- Declarative caching with Spring annotations
- Programmatic cache management
- Cache statistics and monitoring
- Cache invalidation patterns
- RESTful APIs for testing
- Performance metrics

## Technologies

- **Spring Boot 3.2.0**
- **Java 17**
- **Caffeine** - High-performance in-memory cache
- **Ehcache** - Enterprise-grade in-memory cache
- **Redis** - Distributed cache
- **Spring Cache Abstraction**
- **Spring Boot Actuator** - Monitoring and metrics
- **Lombok** - Reduce boilerplate code

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Redis server (optional, for distributed caching examples)

### Installation

1. Clone the repository:
```bash
cd caching-strategies
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:

**With Redis (recommended for full functionality):**
```bash
# Start Redis (using Docker)
docker run -d -p 6379:6379 redis:latest

# Run the application
mvn spring-boot:run
```

**Without Redis (Caffeine only):**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=no-redis
```

4. Access the application:
```
http://localhost:8080
```

5. Check actuator endpoints:
```
http://localhost:8080/actuator/caches
http://localhost:8080/actuator/health
```

## Caching Strategies

### 1. In-Memory Caching with Caffeine

**Location:** `ProductService.java`

Caffeine is a high-performance, near-optimal caching library for Java.

**Configuration:**
```java
@Bean
public CacheManager caffeineCacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(Caffeine.newBuilder()
        .initialCapacity(100)
        .maximumSize(500)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .recordStats());
    return cacheManager;
}
```

**Usage Example:**
```java
@Cacheable(value = "products", key = "#id")
public Product getProductById(Long id) {
    return productRepository.findById(id);
}
```

**Pros:**
- Extremely fast (<1ms for cache hits)
- Low memory footprint
- Automatic eviction policies
- Built-in statistics

**Cons:**
- Not shared across instances
- Lost on application restart
- Single-node only

### 2. In-Memory Caching with Ehcache

**Configuration:** `ehcache.xml`

Ehcache is a mature, enterprise-grade caching solution.

**Configuration:**
```xml
<cache alias="ehcache:products">
    <expiry>
        <ttl unit="minutes">30</ttl>
    </expiry>
    <heap unit="entries">500</heap>
</cache>
```

**Pros:**
- Mature and well-tested
- Disk persistence support
- Off-heap memory support
- JSR-107 compliant

**Cons:**
- Slower than Caffeine
- More complex configuration
- Requires Terracotta for clustering

### 3. Distributed Caching with Redis

**Location:** `UserService.java`

Redis provides distributed caching shared across all application instances.

**Configuration:**
```java
@Bean
public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(10))
        .serializeKeysWith(...)
        .serializeValuesWith(...);

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(config)
        .build();
}
```

**Usage Example:**
```java
@Cacheable(value = "redis:users", key = "#id", cacheManager = "redisCacheManager")
public User getUserById(Long id) {
    return userRepository.findById(id);
}
```

**Pros:**
- Shared across all instances
- Survives restarts (with persistence)
- Scalable horizontally
- Rich data structures

**Cons:**
- Network latency (10-50ms)
- Requires Redis infrastructure
- Serialization overhead
- More complex operations

## Cache Invalidation Patterns

The project demonstrates 6 cache invalidation patterns:

### 1. Time-Based Invalidation (TTL)

Automatic expiration after a configured time.

```java
.expireAfterWrite(10, TimeUnit.MINUTES)
.expireAfterAccess(5, TimeUnit.MINUTES)
```

**When to use:**
- Data has predictable freshness
- Can tolerate slightly stale data
- Want simple, automatic invalidation

### 2. Event-Based Invalidation

Invalidate on specific events (create, update, delete).

```java
@CacheEvict(value = "products", key = "#id")
public void deleteProduct(Long id) {
    productRepository.deleteById(id);
}
```

**When to use:**
- Need immediate consistency
- Changes are infrequent
- Can identify all write operations

### 3. Manual Invalidation

Programmatic cache clearing.

```java
Cache cache = cacheManager.getCache("products");
cache.evict(key);
```

**When to use:**
- Need fine-grained control
- Complex invalidation logic
- Runtime-based conditions

### 4. Scheduled Cache Warming

Proactively refresh cache before expiration.

```java
@Scheduled(cron = "0 0 * * * *")
public void scheduledCacheWarming() {
    // Populate frequently accessed data
}
```

**When to use:**
- Cache misses are expensive
- Can predict frequently accessed data
- Want consistent response times

### 5. Conditional Eviction

Evict only if conditions are met.

```java
@CacheEvict(value = "products", key = "#id", condition = "#invalidate == true")
public void conditionalEviction(Long id, boolean invalidate) {
    // Logic here
}
```

**When to use:**
- Invalidation depends on business logic
- Want to avoid unnecessary evictions

### 6. Write-Through Caching

Update cache on write operations.

```java
@CachePut(value = "products", key = "#product.id")
public Product updateProduct(Product product) {
    return productRepository.save(product);
}
```

**When to use:**
- Want cache to stay fresh
- Write operations not too frequent
- Can afford update overhead

## API Endpoints

### Product Endpoints (In-Memory Caching - Caffeine)

```bash
# Get product by ID
GET http://localhost:8080/api/products/{id}

# Get all products
GET http://localhost:8080/api/products

# Get products by category
GET http://localhost:8080/api/products/category/{category}

# Create product
POST http://localhost:8080/api/products
Content-Type: application/json
{
  "name": "New Product",
  "description": "Product description",
  "price": 99.99,
  "category": "Electronics",
  "stockQuantity": 100
}

# Update product
PUT http://localhost:8080/api/products/{id}

# Delete product
DELETE http://localhost:8080/api/products/{id}

# Clear all product caches
POST http://localhost:8080/api/products/cache/clear
```

### User Endpoints (Distributed Caching - Redis)

```bash
# Get user by ID
GET http://localhost:8080/api/users/{id}

# Get user by username
GET http://localhost:8080/api/users/username/{username}

# Get all users
GET http://localhost:8080/api/users

# Create user
POST http://localhost:8080/api/users
Content-Type: application/json
{
  "username": "john.doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "active": true
}

# Update user
PUT http://localhost:8080/api/users/{id}

# Delete user
DELETE http://localhost:8080/api/users/{id}

# Clear all user caches
POST http://localhost:8080/api/users/cache/clear
```

### Cache Management Endpoints

```bash
# Get cache statistics
GET http://localhost:8080/api/cache/stats

# Clear specific cache entry
DELETE http://localhost:8080/api/cache/{cacheName}/{key}

# Clear entire cache
DELETE http://localhost:8080/api/cache/{cacheName}

# Clear all caches
DELETE http://localhost:8080/api/cache/all

# Get cache patterns information
GET http://localhost:8080/api/cache/patterns
```

## Performance Comparison

### Test Scenario: Retrieving a Product by ID

| Cache Type | First Call (Miss) | Second Call (Hit) | Network Overhead |
|-----------|-------------------|-------------------|------------------|
| **No Cache** | ~500ms | ~500ms | N/A |
| **Caffeine** | ~500ms | <1ms | None |
| **Ehcache** | ~500ms | ~2ms | None |
| **Redis (localhost)** | ~500ms | ~10-20ms | Low |
| **Redis (remote)** | ~500ms | ~30-50ms | Medium-High |

### Key Observations:

1. **Caffeine** is the fastest for local caching
2. **Redis** adds network latency but enables distribution
3. **Cache hits** provide 50-500x performance improvement
4. **First call** always hits the database (cache miss)

## When to Use Each Strategy

### Use Caffeine When:

- ✅ Single application instance
- ✅ Need maximum performance (<1ms)
- ✅ Data fits in memory
- ✅ Cache loss on restart is acceptable
- ✅ Simple configuration preferred

**Examples:**
- Session data for single-instance apps
- Reference data (countries, currencies)
- Configuration settings
- User preferences

### Use Redis When:

- ✅ Multiple application instances
- ✅ Need shared cache across instances
- ✅ Require cache persistence
- ✅ Horizontal scaling needed
- ✅ Can tolerate 10-50ms latency

**Examples:**
- User sessions in microservices
- Shopping carts
- API rate limiting
- Real-time analytics
- Distributed locks

### Use Ehcache When:

- ✅ Need enterprise features
- ✅ Require disk overflow/persistence
- ✅ Want JSR-107 compliance
- ✅ Using off-heap memory
- ✅ Need clustering (with Terracotta)

**Examples:**
- Large datasets with disk overflow
- Enterprise applications
- Legacy systems requiring JSR-107

## Best Practices

### 1. Choose the Right Cache Key

```java
// Good: Specific, unique key
@Cacheable(value = "products", key = "#id")

// Bad: Too generic
@Cacheable(value = "products", key = "'all'")
```

### 2. Set Appropriate TTL

```java
// Short TTL for frequently changing data
.expireAfterWrite(1, TimeUnit.MINUTES)

// Longer TTL for static data
.expireAfterWrite(1, TimeUnit.HOURS)
```

### 3. Handle Cache Misses Gracefully

```java
public Product getProduct(Long id) {
    try {
        return productService.getProductById(id);
    } catch (Exception e) {
        log.error("Cache error, falling back to database", e);
        return productRepository.findById(id);
    }
}
```

### 4. Monitor Cache Performance

```java
// Use Actuator to monitor cache metrics
management.endpoints.web.exposure.include=caches,metrics
```

### 5. Invalidate Proactively

```java
// Invalidate on write operations
@CacheEvict(value = "products", key = "#id")
public void updateProduct(Product product) {
    productRepository.save(product);
}
```

### 6. Avoid Caching Nulls

```java
RedisCacheConfiguration.defaultCacheConfig()
    .disableCachingNullValues()
```

### 7. Use Cache Statistics

```java
Caffeine.newBuilder()
    .recordStats()
```

### 8. Serialize Properly for Distributed Caching

```java
// Ensure models implement Serializable
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    // fields...
}
```

### 9. Set Maximum Cache Size

```java
Caffeine.newBuilder()
    .maximumSize(500) // Prevent memory issues
```

### 10. Test Cache Behavior

```java
@Test
public void testCachingBehavior() {
    // First call - cache miss
    Product product1 = productService.getProductById(1L);

    // Second call - cache hit
    Product product2 = productService.getProductById(1L);

    // Verify only one database call
    verify(repository, times(1)).findById(1L);
}
```

## Common Pitfalls to Avoid

1. **Over-caching**: Don't cache everything; cache only what's accessed frequently
2. **Under-invalidating**: Stale data can cause bugs; invalidate when data changes
3. **Wrong cache size**: Too small = frequent evictions; too large = memory issues
4. **Ignoring serialization**: Objects must be serializable for distributed caching
5. **Not monitoring**: Always track cache hit rates and performance
6. **Caching nulls**: Can lead to unexpected behavior
7. **Forgetting TTL**: Without TTL, stale data persists forever
8. **Not testing**: Always test cache behavior in integration tests

## Project Structure

```
caching-strategies/
├── src/
│   ├── main/
│   │   ├── java/com/example/caching/
│   │   │   ├── config/
│   │   │   │   ├── CaffeineConfig.java
│   │   │   │   ├── EhcacheConfig.java
│   │   │   │   └── RedisConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── CacheManagementController.java
│   │   │   ├── model/
│   │   │   │   ├── Product.java
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   ├── ProductRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/
│   │   │   │   ├── ProductService.java
│   │   │   │   ├── UserService.java
│   │   │   │   └── CacheInvalidationService.java
│   │   │   └── CachingStrategiesApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-no-redis.properties
│   │       └── ehcache.xml
│   └── test/
├── pom.xml
└── README.md
```

## Additional Resources

- [Spring Cache Documentation](https://docs.spring.io/spring-framework/reference/integration/cache.html)
- [Caffeine GitHub](https://github.com/ben-manes/caffeine)
- [Ehcache Documentation](https://www.ehcache.org/documentation/)
- [Redis Documentation](https://redis.io/documentation)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

## License

This project is created for educational purposes to demonstrate caching strategies in Spring Boot.

## Contributing

Feel free to submit issues and enhancement requests!
