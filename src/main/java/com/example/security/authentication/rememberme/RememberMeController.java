package com.example.security.authentication.rememberme;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Remember Me examples
 */
@Controller
@RequestMapping("/rememberme")
public class RememberMeController {

    @GetMapping("/login")
    public String login() {
        return "rememberme/login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());
            model.addAttribute("rememberMe",
                auth.getClass().getSimpleName().contains("RememberMe"));
        }
        return "rememberme/home";
    }
}
