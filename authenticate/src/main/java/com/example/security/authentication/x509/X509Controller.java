package com.example.security.authentication.x509;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.cert.X509Certificate;

/**
 * Controller for X.509 Authentication examples
 */
@Controller
@RequestMapping("/x509")
public class X509Controller {

    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());

            // Get certificate details if available
            if (auth.getCredentials() instanceof X509Certificate) {
                X509Certificate cert = (X509Certificate) auth.getCredentials();
                model.addAttribute("certificateSubject", cert.getSubjectX500Principal().getName());
                model.addAttribute("certificateIssuer", cert.getIssuerX500Principal().getName());
                model.addAttribute("certificateSerial", cert.getSerialNumber());
            }
        }
        return "x509/home";
    }
}
