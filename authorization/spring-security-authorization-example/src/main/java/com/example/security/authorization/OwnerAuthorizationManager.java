package com.example.security.authorization;

import com.example.security.model.Document;
import com.example.security.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Custom AuthorizationManager that checks if the authenticated user is the owner
 * of a document. This demonstrates object-level authorization.
 *
 * This pattern is useful when you need to make authorization decisions based on
 * the relationship between the user and a specific resource.
 */
@Component
@RequiredArgsConstructor
public class OwnerAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final DocumentRepository documentRepository;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
                                       RequestAuthorizationContext context) {
        Authentication auth = authentication.get();

        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        // Extract document ID from path variables
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>)
            context.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables == null || !pathVariables.containsKey("id")) {
            return new AuthorizationDecision(false);
        }

        Long documentId = Long.parseLong(pathVariables.get("id"));

        // Check if user is the owner of the document
        return documentRepository.findById(documentId)
            .map(document -> new AuthorizationDecision(
                document.getOwner().equals(auth.getName())
            ))
            .orElse(new AuthorizationDecision(false));
    }
}
