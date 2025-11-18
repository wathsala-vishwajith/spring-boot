package com.example.security.authentication.saml;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for SAML 2.0 Login examples
 */
@Controller
@RequestMapping("/saml")
public class SamlLoginController {

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal, Model model) {
        if (principal != null) {
            model.addAttribute("name", principal.getName());
            model.addAttribute("attributes", principal.getAttributes());
            model.addAttribute("authorities", principal.getAuthorities());
        }
        return "saml/home";
    }
}
