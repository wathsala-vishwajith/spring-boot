package com.example.security.acl;

import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

/**
 * ACL (Access Control Lists) Configuration.
 *
 * ACLs provide instance-level (object-level) security, allowing you to define
 * fine-grained permissions for individual domain objects.
 *
 * Key concepts:
 * - ACL: Access Control List for a specific domain object
 * - ACE: Access Control Entry - a single permission for a principal
 * - SID: Security Identity (user or role)
 * - Permissions: READ, WRITE, CREATE, DELETE, ADMINISTRATION
 *
 * ACLs are stored in database tables:
 * - acl_class: Domain object types
 * - acl_sid: Security identities (users/roles)
 * - acl_object_identity: Individual domain objects
 * - acl_entry: Permissions for each object
 */
@Configuration
public class AclConfig {

    /**
     * ACL Service for managing ACLs.
     * Uses JDBC to store ACL data in database.
     */
    @Bean
    public JdbcMutableAclService aclService(
        DataSource dataSource,
        LookupStrategy lookupStrategy,
        AclCache aclCache) {

        JdbcMutableAclService aclService = new JdbcMutableAclService(
            dataSource, lookupStrategy, aclCache);

        // Optional: Set class identity query for custom object identity retrieval
        aclService.setClassIdentityQuery("SELECT @@IDENTITY");
        aclService.setSidIdentityQuery("SELECT @@IDENTITY");

        return aclService;
    }

    /**
     * Lookup strategy for retrieving ACLs from database.
     */
    @Bean
    public LookupStrategy lookupStrategy(
        DataSource dataSource,
        AclCache aclCache,
        AclAuthorizationStrategy aclAuthorizationStrategy,
        PermissionGrantingStrategy permissionGrantingStrategy) {

        return new BasicLookupStrategy(
            dataSource,
            aclCache,
            aclAuthorizationStrategy,
            permissionGrantingStrategy
        );
    }

    /**
     * ACL Cache using EhCache for performance.
     */
    @Bean
    public AclCache aclCache(
        EhCacheFactoryBean aclEhCacheFactoryBean,
        PermissionGrantingStrategy permissionGrantingStrategy,
        AclAuthorizationStrategy aclAuthorizationStrategy) {

        return new EhCacheBasedAclCache(
            aclEhCacheFactoryBean.getObject(),
            permissionGrantingStrategy,
            aclAuthorizationStrategy
        );
    }

    /**
     * EhCache factory for ACL cache.
     */
    @Bean
    public EhCacheFactoryBean aclEhCacheFactoryBean(EhCacheManagerFactoryBean ehCacheManagerFactoryBean) {
        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        ehCacheFactoryBean.setCacheManager(ehCacheManagerFactoryBean.getObject());
        ehCacheFactoryBean.setCacheName("aclCache");
        return ehCacheFactoryBean;
    }

    /**
     * EhCache manager.
     */
    @Bean
    public EhCacheManagerFactoryBean aclCacheManager() {
        return new EhCacheManagerFactoryBean();
    }

    /**
     * Permission granting strategy - decides if access should be granted.
     */
    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        // ConsoleAuditLogger logs ACL decisions to console
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    /**
     * ACL authorization strategy - controls who can modify ACLs.
     */
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        // Only users with ROLE_ADMIN can change ACL ownership, audit, or permissions
        return new AclAuthorizationStrategyImpl(
            new SimpleGrantedAuthority("ROLE_ADMIN"),
            new SimpleGrantedAuthority("ROLE_ADMIN"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
    }

    /**
     * Method security expression handler with ACL support.
     * This enables hasPermission() expressions in @PreAuthorize.
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
        JdbcMutableAclService aclService) {

        DefaultMethodSecurityExpressionHandler expressionHandler =
            new DefaultMethodSecurityExpressionHandler();

        AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService);
        expressionHandler.setPermissionEvaluator(permissionEvaluator);

        return expressionHandler;
    }
}
