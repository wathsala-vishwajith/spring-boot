# Access Control Lists (ACLs)

## Overview

ACLs (Access Control Lists) provide instance-level security, allowing you to define fine-grained permissions for individual domain objects. Unlike role-based security that applies globally, ACLs let you specify permissions per object instance.

**Example Scenarios:**
- User A can READ Document 1 but not Document 2
- User B can WRITE Document 2 but only READ Document 3
- User C can ADMIN Document 1 (grant permissions to others)

## Key Concepts

### 1. ACL (Access Control List)

A list of permissions for a specific domain object instance.

### 2. ACE (Access Control Entry)

A single permission entry within an ACL.

**Structure:**
- **SID** (Security Identity): Who the permission applies to
- **Permission**: What can be done (READ, WRITE, DELETE, etc.)
- **Granting**: Whether permission is granted or denied
- **Audit**: Whether to log access

### 3. SID (Security Identity)

Represents a user or role.

- **PrincipalSid**: Represents a user (e.g., "john")
- **GrantedAuthoritySid**: Represents a role (e.g., "ROLE_ADMIN")

### 4. ObjectIdentity

Uniquely identifies a domain object.

**Components:**
- **Class**: Domain object type (e.g., Document.class)
- **Identifier**: Object ID (e.g., documentId)

### 5. Permissions

Standard permissions:
- **READ** (1): View the object
- **WRITE** (2): Modify the object
- **CREATE** (4): Create child objects
- **DELETE** (8): Delete the object
- **ADMINISTRATION** (16): Manage ACL permissions

## Database Schema

ACLs are persisted in four tables:

### acl_class
Stores domain object types.

```sql
CREATE TABLE acl_class (
    id BIGINT PRIMARY KEY,
    class VARCHAR(255) UNIQUE
);
```

### acl_sid
Stores security identities (users/roles).

```sql
CREATE TABLE acl_sid (
    id BIGINT PRIMARY KEY,
    principal BOOLEAN,
    sid VARCHAR(100) UNIQUE
);
```

### acl_object_identity
Stores individual domain object instances.

```sql
CREATE TABLE acl_object_identity (
    id BIGINT PRIMARY KEY,
    object_id_class BIGINT,
    object_id_identity BIGINT,
    parent_object BIGINT,
    owner_sid BIGINT,
    entries_inheriting BOOLEAN
);
```

### acl_entry
Stores ACL entries (permissions).

```sql
CREATE TABLE acl_entry (
    id BIGINT PRIMARY KEY,
    acl_object_identity BIGINT,
    ace_order INTEGER,
    sid BIGINT,
    mask INTEGER,
    granting BOOLEAN,
    audit_success BOOLEAN,
    audit_failure BOOLEAN
);
```

## Configuration

### 1. Enable ACL Support

```java
@Configuration
public class AclConfig {

    @Bean
    public JdbcMutableAclService aclService(
        DataSource dataSource,
        LookupStrategy lookupStrategy,
        AclCache aclCache) {

        return new JdbcMutableAclService(
            dataSource, lookupStrategy, aclCache);
    }

    @Bean
    public LookupStrategy lookupStrategy(
        DataSource dataSource,
        AclCache aclCache,
        AclAuthorizationStrategy authStrategy,
        PermissionGrantingStrategy grantingStrategy) {

        return new BasicLookupStrategy(
            dataSource, aclCache, authStrategy, grantingStrategy);
    }

    @Bean
    public AclCache aclCache(
        EhCacheFactoryBean cacheFactory,
        PermissionGrantingStrategy grantingStrategy,
        AclAuthorizationStrategy authStrategy) {

        return new EhCacheBasedAclCache(
            cacheFactory.getObject(),
            grantingStrategy,
            authStrategy);
    }
}
```

### 2. Enable hasPermission() Expressions

```java
@Bean
public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
    MutableAclService aclService) {

    DefaultMethodSecurityExpressionHandler handler =
        new DefaultMethodSecurityExpressionHandler();

    AclPermissionEvaluator evaluator =
        new AclPermissionEvaluator(aclService);
    handler.setPermissionEvaluator(evaluator);

    return handler;
}
```

## Creating ACLs

### Creating an ACL for a New Object

```java
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MutableAclService aclService;

    public Document createDocument(Document doc) {
        // Save the document
        Document saved = documentRepository.save(doc);

        // Create ACL
        ObjectIdentity oid = new ObjectIdentityImpl(
            Document.class, saved.getId());

        MutableAcl acl = aclService.createAcl(oid);

        // Grant permissions to owner
        Sid owner = new PrincipalSid(doc.getOwner());
        acl.setOwner(owner);

        // Grant all permissions
        acl.insertAce(0, BasePermission.READ, owner, true);
        acl.insertAce(1, BasePermission.WRITE, owner, true);
        acl.insertAce(2, BasePermission.DELETE, owner, true);
        acl.insertAce(3, BasePermission.ADMINISTRATION, owner, true);

        aclService.updateAcl(acl);

        return saved;
    }
}
```

## Managing Permissions

### Granting Permissions

```java
public void grantReadPermission(Long documentId, String username) {
    ObjectIdentity oid = new ObjectIdentityImpl(Document.class, documentId);
    MutableAcl acl = (MutableAcl) aclService.readAclById(oid);

    Sid recipient = new PrincipalSid(username);
    acl.insertAce(
        acl.getEntries().size(),  // Add at end
        BasePermission.READ,
        recipient,
        true                      // Granting (not denying)
    );

    aclService.updateAcl(acl);
}
```

### Revoking Permissions

```java
public void revokePermissions(Long documentId, String username) {
    ObjectIdentity oid = new ObjectIdentityImpl(Document.class, documentId);
    MutableAcl acl = (MutableAcl) aclService.readAclById(oid);

    Sid sid = new PrincipalSid(username);

    // Remove all ACEs for this user
    List<?> entries = acl.getEntries();
    for (int i = entries.size() - 1; i >= 0; i--) {
        if (acl.getEntries().get(i).getSid().equals(sid)) {
            acl.deleteAce(i);
        }
    }

    aclService.updateAcl(acl);
}
```

### Deleting ACLs

```java
public void deleteDocument(Long documentId) {
    // Delete document
    documentRepository.deleteById(documentId);

    // Delete ACL
    ObjectIdentity oid = new ObjectIdentityImpl(Document.class, documentId);
    aclService.deleteAcl(oid, true);  // true = delete child ACLs too
}
```

## Using ACLs in Authorization

### @PreAuthorize with hasPermission()

```java
// Check permission on object by ID
@PreAuthorize("hasPermission(#id, 'com.example.security.model.Document', 'READ')")
public Document getDocument(Long id) {
    return documentRepository.findById(id).orElseThrow();
}

// Check permission on object by ID (alternative syntax)
@PreAuthorize("hasPermission(#id, 'Document', 'WRITE')")
public Document updateDocument(Long id, Document doc) {
    // ...
}

// Check ADMINISTRATION permission
@PreAuthorize("hasPermission(#documentId, 'Document', 'ADMINISTRATION')")
public void grantAccess(Long documentId, String username) {
    // ...
}
```

### @PostAuthorize with hasPermission()

```java
// Check permission on returned object
@PostAuthorize("hasPermission(returnObject, 'READ')")
public Document getDocument(Long id) {
    return documentRepository.findById(id).orElseThrow();
}
```

### @PostFilter with hasPermission()

```java
// Filter collection based on permissions
@PostFilter("hasPermission(filterObject, 'READ')")
public List<Document> getAllDocuments() {
    return documentRepository.findAll();
}
```

## hasPermission() Syntax

### By Object ID

```java
hasPermission(objectId, objectType, permission)

// Examples:
hasPermission(#id, 'Document', 'READ')
hasPermission(#docId, 'com.example.security.model.Document', 'WRITE')
hasPermission(1L, 'Document', 'DELETE')
```

### By Object Instance

```java
hasPermission(object, permission)

// Examples:
hasPermission(returnObject, 'READ')
hasPermission(filterObject, 'WRITE')
hasPermission(#document, 'DELETE')
```

## Custom Permissions

### Define Custom Permission

```java
public class CustomPermission extends BasePermission {
    public static final Permission PUBLISH = new CustomPermission(1 << 5, 'P');
    public static final Permission ARCHIVE = new CustomPermission(1 << 6, 'A');

    protected CustomPermission(int mask, char code) {
        super(mask, code);
    }
}
```

### Use Custom Permission

```java
// Grant custom permission
acl.insertAce(
    acl.getEntries().size(),
    CustomPermission.PUBLISH,
    sid,
    true
);

// Check custom permission
@PreAuthorize("hasPermission(#id, 'Document', 'PUBLISH')")
public void publishDocument(Long id) {
    // ...
}
```

## Inheritance

ACLs support permission inheritance from parent objects.

```java
// Create parent ACL
ObjectIdentity parentOid = new ObjectIdentityImpl(Folder.class, folderId);
MutableAcl parentAcl = aclService.createAcl(parentOid);

// Create child ACL with inheritance
ObjectIdentity childOid = new ObjectIdentityImpl(Document.class, documentId);
MutableAcl childAcl = aclService.createAcl(childOid);
childAcl.setParent(parentAcl);
childAcl.setEntriesInheriting(true);  // Enable inheritance

aclService.updateAcl(childAcl);
```

## Performance Optimization

### 1. Caching

Use EhCache for ACL caching (configured in AclConfig).

### 2. Batch Loading

```java
// Load multiple ACLs at once
List<ObjectIdentity> oids = documents.stream()
    .map(doc -> new ObjectIdentityImpl(Document.class, doc.getId()))
    .collect(Collectors.toList());

Map<ObjectIdentity, Acl> acls = aclService.readAclsById(oids);
```

### 3. Database Indexing

Ensure proper indexes on ACL tables (already in schema-acl.sql).

## Testing ACLs

```java
@SpringBootTest
class AclDocumentServiceTest {

    @Autowired
    private AclDocumentService service;

    @Test
    @WithMockUser(username = "owner")
    void ownerCanAccessDocument() {
        Document doc = service.createDocumentWithAcl(
            new Document("Test", "Content", "owner"));

        assertDoesNotThrow(() ->
            service.getDocumentWithAcl(doc.getId()));
    }

    @Test
    @WithMockUser(username = "other")
    void otherUserCannotAccessDocument() {
        Document doc = service.createDocumentWithAcl(
            new Document("Test", "Content", "owner"));

        assertThrows(AccessDeniedException.class, () ->
            service.getDocumentWithAcl(doc.getId()));
    }

    @Test
    @WithMockUser(username = "owner")
    void ownerCanGrantPermission() {
        Document doc = service.createDocumentWithAcl(
            new Document("Test", "Content", "owner"));

        assertDoesNotThrow(() ->
            service.grantReadPermission(doc.getId(), "other"));
    }
}
```

## Best Practices

1. **Clean Up**: Always delete ACLs when deleting domain objects
2. **Default Permissions**: Grant reasonable defaults to object creators
3. **Admin Override**: Allow admins to bypass ACL checks
4. **Caching**: Use caching for frequently accessed ACLs
5. **Auditing**: Enable audit logging for sensitive operations
6. **Testing**: Thoroughly test permission scenarios
7. **Documentation**: Document permission requirements for objects

## Common Patterns

### Pattern 1: Owner Gets Full Access

```java
public Document create(Document doc) {
    Document saved = repository.save(doc);

    ObjectIdentity oid = new ObjectIdentityImpl(Document.class, saved.getId());
    MutableAcl acl = aclService.createAcl(oid);
    Sid owner = new PrincipalSid(doc.getOwner());

    acl.setOwner(owner);
    acl.insertAce(0, BasePermission.ADMINISTRATION, owner, true);

    aclService.updateAcl(acl);
    return saved;
}
```

### Pattern 2: Shared Documents

```java
public void shareDocument(Long docId, String username, Permission permission) {
    ObjectIdentity oid = new ObjectIdentityImpl(Document.class, docId);
    MutableAcl acl = (MutableAcl) aclService.readAclById(oid);

    Sid recipient = new PrincipalSid(username);
    acl.insertAce(acl.getEntries().size(), permission, recipient, true);

    aclService.updateAcl(acl);
}
```

### Pattern 3: Team Access

```java
public void grantTeamAccess(Long docId, String teamRole) {
    ObjectIdentity oid = new ObjectIdentityImpl(Document.class, docId);
    MutableAcl acl = (MutableAcl) aclService.readAclById(oid);

    Sid team = new GrantedAuthoritySid(teamRole);
    acl.insertAce(acl.getEntries().size(), BasePermission.READ, team, true);

    aclService.updateAcl(acl);
}
```

## Limitations

1. **Complexity**: More complex than role-based security
2. **Performance**: Database queries for each permission check
3. **Schema**: Requires additional database tables
4. **Learning Curve**: Takes time to understand and implement correctly

## When to Use ACLs

**Use ACLs when:**
- Need instance-level permissions
- Different users need different access to same object type
- Users can share objects with specific permissions
- Building a multi-tenant application

**Don't use ACLs when:**
- Simple role-based access is sufficient
- All users with a role have same permissions
- Performance is critical and caching isn't enough
- Application has simple security requirements

## See Also

- [Authorization Architecture](01-AUTHORIZATION-ARCHITECTURE.md)
- [Method Security](03-METHOD-SECURITY.md)
- [ACL Configuration](../src/main/java/com/example/security/acl/AclConfig.java)
- [ACL Service](../src/main/java/com/example/security/service/AclDocumentService.java)
