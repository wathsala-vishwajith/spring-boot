# Method Security

## Overview

Method security allows you to secure individual methods with authorization rules. This provides fine-grained security at the service layer, independent of web layer security.

## Enabling Method Security

```java
@SpringBootApplication
@EnableMethodSecurity(
    prePostEnabled = true,    // @PreAuthorize, @PostAuthorize
    securedEnabled = true,    // @Secured
    jsr250Enabled = true      // @RolesAllowed
)
public class Application {
    // ...
}
```

## Annotations

### 1. @PreAuthorize

Checks authorization **BEFORE** method execution.

```java
// Simple role check
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) {
    userRepository.deleteById(id);
}

// Authority check
@PreAuthorize("hasAuthority('WRITE_DOCUMENTS')")
public Document createDocument(Document doc) {
    return documentRepository.save(doc);
}

// Multiple conditions with AND
@PreAuthorize("hasRole('ADMIN') and hasAuthority('DELETE_USERS')")
public void permanentlyDeleteUser(Long id) {
    // ...
}

// Multiple conditions with OR
@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
public void moderateContent(Long id) {
    // ...
}
```

### 2. @PostAuthorize

Checks authorization **AFTER** method execution. Useful when the decision depends on the return value.

```java
// Check if user owns the returned document
@PostAuthorize("returnObject.owner == authentication.name")
public Document getDocument(Long id) {
    return documentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Not found"));
}

// Check if returned value meets criteria
@PostAuthorize("returnObject.isPresent() &&
                returnObject.get().owner == authentication.name")
public Optional<Document> findDocument(Long id) {
    return documentRepository.findById(id);
}
```

**Important:**
- Method still executes even if authorization will fail
- Useful for ownership checks
- Uses `returnObject` to reference the return value

### 3. @PreFilter

Filters collection **PARAMETERS** before method execution.

```java
// Only process documents owned by the user
@PreFilter("filterObject.owner == authentication.name")
public List<Document> saveDocuments(List<Document> documents) {
    return documentRepository.saveAll(documents);
}
```

**Notes:**
- Uses `filterObject` to reference each element
- Only works with Collections
- Elements that don't match are removed from the parameter

### 4. @PostFilter

Filters collection **RESULTS** after method execution.

```java
// Only return documents owned by user or if user is admin
@PostFilter("filterObject.owner == authentication.name or hasRole('ADMIN')")
public List<Document> getAllDocuments() {
    return documentRepository.findAll();
}
```

**Important:**
- Method retrieves all records, then filters
- Can be inefficient for large datasets
- Consider database-level filtering for performance

### 5. @Secured

Simple role-based security. Part of Spring Security (not SpEL).

```java
// Single role
@Secured("ROLE_ADMIN")
public void adminMethod() {
    // ...
}

// Multiple roles (OR condition)
@Secured({"ROLE_ADMIN", "ROLE_MODERATOR"})
public void moderatorMethod() {
    // ...
}
```

**Characteristics:**
- No SpEL expressions
- Requires full role names with "ROLE_" prefix
- Less flexible than @PreAuthorize

### 6. @RolesAllowed (JSR-250)

Standard Java annotation for role-based security.

```java
// Single role (no ROLE_ prefix)
@RolesAllowed("ADMIN")
public void adminMethod() {
    // ...
}

// Multiple roles
@RolesAllowed({"ADMIN", "MODERATOR"})
public void moderatorMethod() {
    // ...
}
```

**Differences from @Secured:**
- Standard Java annotation
- Does NOT use "ROLE_" prefix
- Framework-independent

## SpEL Expressions

### Common Variables

```java
// authentication - current Authentication object
authentication.name                    // username
authentication.principal              // principal object
authentication.authorities            // granted authorities

// returnObject - method return value (in @PostAuthorize)
returnObject.owner
returnObject.status

// filterObject - each element (in @PreFilter/@PostFilter)
filterObject.id
filterObject.owner

// Method parameters (use # prefix)
#userId
#documentId
#username
```

### Examples

```java
// Check method parameter
@PreAuthorize("#username == authentication.name")
public void updateProfile(String username, Profile profile) {
    // ...
}

// Access nested properties
@PreAuthorize("#document.owner == authentication.name")
public void updateDocument(Document document) {
    // ...
}

// Call repository method
@PreAuthorize("@documentRepository.findById(#id).orElse(null)?.owner == authentication.name")
public void updateDocument(Long id, Document doc) {
    // ...
}

// Complex condition
@PreAuthorize("hasRole('ADMIN') or " +
              "(hasAuthority('WRITE_DOCUMENTS') and #doc.owner == authentication.name)")
public Document updateDocument(Document doc) {
    // ...
}
```

## Custom Security Expressions

Create a custom bean for reusable authorization logic.

```java
@Component
public class DocumentSecurityEvaluator {

    private final DocumentRepository documentRepository;

    public boolean canAccess(Authentication auth, Long documentId) {
        // Admins can access everything
        if (auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // Check ownership
        return documentRepository.findById(documentId)
            .map(doc -> doc.getOwner().equals(auth.getName()))
            .orElse(false);
    }
}
```

**Usage:**

```java
@PreAuthorize("@documentSecurityEvaluator.canAccess(authentication, #id)")
public Document getDocument(Long id) {
    return documentRepository.findById(id).orElseThrow();
}
```

## Class-Level Security

Apply security to all methods in a class.

```java
@Service
@PreAuthorize("hasRole('ADMIN')")  // Applies to all methods
public class AdminService {

    public void method1() { }  // Requires ADMIN

    public void method2() { }  // Requires ADMIN

    @PreAuthorize("permitAll()")  // Override for specific method
    public void publicMethod() { }
}
```

## Method Security with Interfaces

Security annotations work on interfaces but must be configured properly.

```java
public interface DocumentService {
    @PreAuthorize("hasAuthority('WRITE_DOCUMENTS')")
    Document createDocument(Document doc);
}

@Service
public class DocumentServiceImpl implements DocumentService {
    @Override
    public Document createDocument(Document doc) {
        // Implementation
    }
}
```

## Testing Method Security

```java
@SpringBootTest
@EnableMethodSecurity
class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;

    @Test
    @WithMockUser(authorities = "WRITE_DOCUMENTS")
    void authorizedUserCanCreate() {
        Document doc = new Document();
        assertDoesNotThrow(() -> documentService.createDocument(doc));
    }

    @Test
    @WithMockUser(authorities = "READ_DOCUMENTS")  // Wrong authority
    void unauthorizedUserCannotCreate() {
        Document doc = new Document();
        assertThrows(AccessDeniedException.class,
            () -> documentService.createDocument(doc));
    }

    @Test
    @WithAnonymousUser
    void anonymousUserCannotCreate() {
        Document doc = new Document();
        assertThrows(AccessDeniedException.class,
            () -> documentService.createDocument(doc));
    }
}
```

## Best Practices

1. **Layer Defense**: Use both URL and method security
2. **Service Layer**: Apply method security at service layer, not controllers
3. **Clear Expressions**: Keep SpEL expressions simple and readable
4. **Custom Beans**: Extract complex logic into custom security evaluators
5. **Performance**: Be cautious with @PostFilter on large datasets
6. **Consistency**: Use consistent annotation style across your application
7. **Documentation**: Document complex authorization logic

## Common Patterns

### Pattern 1: Owner or Admin

```java
@PreAuthorize("hasRole('ADMIN') or #entity.owner == authentication.name")
public void update(Entity entity) { }
```

### Pattern 2: Status-Based Access

```java
@PreAuthorize("@documentRepository.findById(#id).orElse(null)?.status == 'PUBLISHED' " +
              "or hasRole('ADMIN')")
public Document getDocument(Long id) { }
```

### Pattern 3: Time-Based Access

```java
@PreAuthorize("@timeBasedAccessEvaluator.isAccessibleNow(#resourceId)")
public Resource getResource(Long resourceId) { }
```

### Pattern 4: Hierarchical Access

```java
@PreAuthorize("@organizationService.hasAccessToOrganization(authentication, #orgId)")
public void updateOrganization(Long orgId, Organization org) { }
```

## Common Mistakes

1. **Forgetting @EnableMethodSecurity**: Annotations won't work
2. **Wrong Return Type with @PostAuthorize**: Expecting Optional but getting null
3. **Performance with @PostFilter**: Retrieving all records then filtering
4. **Incorrect Parameter Names**: Using wrong parameter name in expression
5. **Complex Expressions**: Making SpEL too complex and hard to maintain

## Performance Considerations

1. **@PostFilter vs Database Query**: Filter at database level when possible
2. **Repository Calls in Expressions**: Can cause N+1 query problems
3. **Lazy Loading**: Be careful with lazy-loaded entities in expressions
4. **Caching**: Consider caching for frequently checked permissions

## Debugging

Enable debug logging to see method security decisions:

```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.access.intercept=TRACE
```

## See Also

- [Authorization Architecture](01-AUTHORIZATION-ARCHITECTURE.md)
- [HTTP Request Authorization](02-HTTP-REQUEST-AUTHORIZATION.md)
- [Access Control Lists](04-ACCESS-CONTROL-LISTS.md)
- [Code Examples](../src/main/java/com/example/security/service/DocumentService.java)
