package com.example.security.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main controller for application home page
 */
@Controller
public class MainController {

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }
}
