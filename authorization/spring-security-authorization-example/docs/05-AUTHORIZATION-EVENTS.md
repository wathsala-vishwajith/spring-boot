# Authorization Events

## Overview

Spring Security publishes authorization events that you can listen to for auditing, monitoring, security analysis, and custom business logic. These events provide visibility into authorization decisions throughout your application.

## Event Types

### 1. AuthorizationGrantedEvent

Published when authorization is **granted** (access allowed).

**Contains:**
- Authentication object
- Secured object (request, method invocation, etc.)
- AuthorizationDecision

### 2. AuthorizationDeniedEvent

Published when authorization is **denied** (access rejected).

**Contains:**
- Authentication object
- Secured object
- AuthorizationDecision

## Basic Event Listener

```java
@Component
@Slf4j
public class AuthorizationEventListener {

    @EventListener
    public void onAuthorizationGranted(AuthorizationGrantedEvent<?> event) {
        log.info("Authorization GRANTED - User: {}, Resource: {}",
            event.getAuthentication().get().getName(),
            event.getSource());
    }

    @EventListener
    public void onAuthorizationDenied(AuthorizationDeniedEvent<?> event) {
        log.warn("Authorization DENIED - User: {}, Resource: {}",
            event.getAuthentication().get().getName(),
            event.getSource());
    }
}
```

## Event Details

### Extracting Information from Events

```java
@EventListener
public void onAuthorizationEvent(AuthorizationGrantedEvent<?> event) {
    // Get authentication
    Authentication auth = event.getAuthentication().get();
    String username = auth.getName();
    Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

    // Get authorization decision
    AuthorizationDecision decision = event.getAuthorizationDecision();
    boolean granted = decision.isGranted();

    // Get the secured object
    Object source = event.getSource();

    // Log details
    log.info("User: {}, Granted: {}, Resource: {}, Authorities: {}",
        username, granted, source, authorities);
}
```

### Handling Different Event Sources

```java
@EventListener
public void onAuthorizationDenied(AuthorizationDeniedEvent<?> event) {
    Object source = event.getSource();

    if (source instanceof RequestAuthorizationContext) {
        handleHttpRequestDenial(event, (RequestAuthorizationContext) source);
    } else if (source instanceof MethodInvocation) {
        handleMethodInvocationDenial(event, (MethodInvocation) source);
    }
}

private void handleHttpRequestDenial(
    AuthorizationDeniedEvent<?> event,
    RequestAuthorizationContext context) {

    HttpServletRequest request = context.getRequest();
    String uri = request.getRequestURI();
    String method = request.getMethod();

    log.warn("HTTP access denied: {} {} by user {}",
        method, uri, event.getAuthentication().get().getName());
}

private void handleMethodInvocationDenial(
    AuthorizationDeniedEvent<?> event,
    MethodInvocation invocation) {

    String methodName = invocation.getMethod().getName();
    String className = invocation.getThis().getClass().getSimpleName();

    log.warn("Method access denied: {}.{} by user {}",
        className, methodName, event.getAuthentication().get().getName());
}
```

## Use Cases

### 1. Audit Logging

```java
@Component
@RequiredArgsConstructor
public class AuditLogger {

    private final AuditRepository auditRepository;

    @EventListener
    @Async
    public void logAuthorizationEvent(AuthorizationGrantedEvent<?> event) {
        AuditRecord record = new AuditRecord();
        record.setUsername(event.getAuthentication().get().getName());
        record.setAction("ACCESS_GRANTED");
        record.setResource(event.getSource().toString());
        record.setTimestamp(Instant.now());

        auditRepository.save(record);
    }

    @EventListener
    @Async
    public void logAuthorizationDenial(AuthorizationDeniedEvent<?> event) {
        AuditRecord record = new AuditRecord();
        record.setUsername(event.getAuthentication().get().getName());
        record.setAction("ACCESS_DENIED");
        record.setResource(event.getSource().toString());
        record.setTimestamp(Instant.now());

        auditRepository.save(record);
    }
}
```

### 2. Security Monitoring

```java
@Component
public class SecurityMonitor {

    private final Map<String, Integer> failureCount = new ConcurrentHashMap<>();

    @EventListener
    public void onAuthorizationDenied(AuthorizationDeniedEvent<?> event) {
        String username = event.getAuthentication().get().getName();

        // Track failures
        int count = failureCount.merge(username, 1, Integer::sum);

        // Alert on suspicious activity
        if (count > 5) {
            alertSecurityTeam(username, count);
        }
    }

    @Scheduled(fixedRate = 3600000)  // Reset every hour
    public void resetCounters() {
        failureCount.clear();
    }

    private void alertSecurityTeam(String username, int failures) {
        log.error("SECURITY ALERT: User {} has {} failed authorization attempts",
            username, failures);
        // Send email, Slack message, etc.
    }
}
```

### 3. Metrics Collection

```java
@Component
@RequiredArgsConstructor
public class AuthorizationMetrics {

    private final MeterRegistry meterRegistry;

    @EventListener
    public void onAuthorizationGranted(AuthorizationGrantedEvent<?> event) {
        meterRegistry.counter("authorization.granted",
            "user", event.getAuthentication().get().getName(),
            "type", getResourceType(event.getSource())
        ).increment();
    }

    @EventListener
    public void onAuthorizationDenied(AuthorizationDeniedEvent<?> event) {
        meterRegistry.counter("authorization.denied",
            "user", event.getAuthentication().get().getName(),
            "type", getResourceType(event.getSource())
        ).increment();
    }

    private String getResourceType(Object source) {
        if (source instanceof RequestAuthorizationContext) {
            return "http";
        } else if (source instanceof MethodInvocation) {
            return "method";
        }
        return "unknown";
    }
}
```

### 4. Rate Limiting

```java
@Component
public class RateLimiter {

    private final LoadingCache<String, AtomicInteger> requestCounts;

    public RateLimiter() {
        this.requestCounts = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, AtomicInteger>() {
                @Override
                public AtomicInteger load(String key) {
                    return new AtomicInteger(0);
                }
            });
    }

    @EventListener
    public void onAuthorizationEvent(AuthorizationGrantedEvent<?> event) {
        String username = event.getAuthentication().get().getName();

        try {
            int count = requestCounts.get(username).incrementAndGet();

            if (count > 100) {  // 100 requests per minute
                log.warn("Rate limit exceeded for user: {}", username);
                // Take action: temporary ban, alert, etc.
            }
        } catch (ExecutionException e) {
            log.error("Error tracking rate limit", e);
        }
    }
}
```

## Publishing Custom Events

### Custom Event Publisher

```java
@Component
@RequiredArgsConstructor
public class CustomAuthorizationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishAuthorizationEvent(
        Authentication authentication,
        Object object,
        AuthorizationDecision decision) {

        if (decision.isGranted()) {
            AuthorizationGrantedEvent<?> event =
                new AuthorizationGrantedEvent<>(
                    () -> authentication, object, decision);
            eventPublisher.publishEvent(event);
        } else {
            AuthorizationDeniedEvent<?> event =
                new AuthorizationDeniedEvent<>(
                    () -> authentication, object, decision);
            eventPublisher.publishEvent(event);
        }
    }
}
```

### Using Custom Publisher

```java
@Service
@RequiredArgsConstructor
public class CustomSecurityService {

    private final CustomAuthorizationEventPublisher eventPublisher;

    public void performSecuredOperation(String userId) {
        Authentication auth = SecurityContextHolder.getContext()
            .getAuthentication();

        // Custom authorization logic
        boolean authorized = checkCustomAuthorization(userId);

        AuthorizationDecision decision =
            new AuthorizationDecision(authorized);

        // Publish event
        eventPublisher.publishAuthorizationEvent(
            auth, "CustomOperation:" + userId, decision);

        if (!authorized) {
            throw new AccessDeniedException("Not authorized");
        }

        // Perform operation
    }
}
```

## Filtering Events

### Listen to Specific Event Types Only

```java
@Component
public class HttpAuthorizationListener {

    @EventListener(condition = "#event.source instanceof T(org.springframework.security.web.access.intercept.RequestAuthorizationContext)")
    public void onHttpAuthorizationGranted(AuthorizationGrantedEvent<?> event) {
        // Only handles HTTP request events
        RequestAuthorizationContext context =
            (RequestAuthorizationContext) event.getSource();
        HttpServletRequest request = context.getRequest();

        log.info("HTTP access granted: {} {}",
            request.getMethod(), request.getRequestURI());
    }
}
```

### Filter by User or Resource

```java
@Component
public class AdminActivityListener {

    @EventListener(condition = "#event.authentication.get().authorities.?[authority == 'ROLE_ADMIN'].size() > 0")
    public void onAdminActivity(AuthorizationGrantedEvent<?> event) {
        // Only triggered for admin users
        log.info("Admin activity: {}", event.getSource());
    }
}
```

## Asynchronous Event Processing

For performance-critical applications, process events asynchronously.

```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("auth-event-");
        executor.initialize();
        return executor;
    }
}

@Component
public class AsyncEventListener {

    @EventListener
    @Async
    public void onAuthorizationGranted(AuthorizationGrantedEvent<?> event) {
        // Processed asynchronously
        // Doesn't block the main request
        processEvent(event);
    }
}
```

## Configuration

### Enable Event Publishing

In Spring Boot 3.x, event publishing is enabled by default. To ensure it's on:

```properties
# application.properties
spring.security.authorization.observation.enabled=true
```

### Custom Event Publisher Configuration

```java
@Configuration
public class SecurityConfig {

    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher(
        ApplicationEventPublisher applicationEventPublisher) {

        return new SpringAuthorizationEventPublisher(
            applicationEventPublisher);
    }
}
```

## Best Practices

1. **Async Processing**: Use @Async for non-critical event handling
2. **Selective Logging**: Don't log every authorization event in production
3. **Security**: Don't log sensitive information (passwords, tokens)
4. **Performance**: Monitor event processing impact
5. **Storage**: Implement log rotation for audit records
6. **Alerting**: Set up alerts for suspicious patterns
7. **Testing**: Test event listeners separately

## Common Patterns

### Pattern 1: Audit Trail

```java
@EventListener
@Async
public void createAuditTrail(AuthorizationGrantedEvent<?> event) {
    // Store in database for compliance
    auditService.recordAccess(
        event.getAuthentication().get().getName(),
        event.getSource(),
        Instant.now()
    );
}
```

### Pattern 2: Real-time Dashboard

```java
@EventListener
public void updateDashboard(AuthorizationDeniedEvent<?> event) {
    // Push to WebSocket for real-time monitoring
    dashboardService.pushSecurityEvent(
        new SecurityEventDTO(event)
    );
}
```

### Pattern 3: Automated Response

```java
@EventListener
public void respondToThreat(AuthorizationDeniedEvent<?> event) {
    if (isSuspiciousActivity(event)) {
        // Automatic response
        lockUserAccount(event.getAuthentication().get().getName());
        notifySecurityTeam(event);
    }
}
```

## Debugging

Enable debug logging for authorization events:

```properties
logging.level.org.springframework.security.authorization.event=DEBUG
logging.level.com.example.security.event=DEBUG
```

## Performance Considerations

1. **Event Volume**: Consider the number of events generated
2. **Processing Time**: Keep listeners lightweight
3. **Async Processing**: Use for expensive operations
4. **Sampling**: Consider sampling in high-traffic scenarios
5. **Storage**: Implement data retention policies

## Testing Event Listeners

```java
@SpringBootTest
class AuthorizationEventListenerTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private AuditRepository auditRepository;

    @Test
    void shouldLogGrantedEvent() {
        Authentication auth = new TestingAuthenticationToken(
            "user", "password", "ROLE_USER");

        AuthorizationGrantedEvent<?> event =
            new AuthorizationGrantedEvent<>(
                () -> auth,
                "testResource",
                new AuthorizationDecision(true)
            );

        eventPublisher.publishEvent(event);

        // Verify audit was saved
        verify(auditRepository, timeout(1000))
            .save(any(AuditRecord.class));
    }
}
```

## See Also

- [Authorization Architecture](01-AUTHORIZATION-ARCHITECTURE.md)
- [Event Listener Implementation](../src/main/java/com/example/security/event/AuthorizationEventListener.java)
- [Custom Event Publisher](../src/main/java/com/example/security/event/CustomAuthorizationEventPublisher.java)
