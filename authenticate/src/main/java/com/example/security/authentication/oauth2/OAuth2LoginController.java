package com.example.security.authentication.oauth2;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Controller for OAuth2 Login examples
 */
@Controller
@RequestMapping("/oauth2")
public class OAuth2LoginController {

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
        if (oauth2User != null) {
            Map<String, Object> attributes = oauth2User.getAttributes();
            model.addAttribute("name", oauth2User.getName());
            model.addAttribute("attributes", attributes);
            model.addAttribute("authorities", oauth2User.getAuthorities());
        }
        return "oauth2/home";
    }

    @GetMapping("/user-info")
    public String userInfo(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
        if (oauth2User != null) {
            model.addAttribute("userAttributes", oauth2User.getAttributes());
        }
        return "oauth2/user-info";
    }
}
