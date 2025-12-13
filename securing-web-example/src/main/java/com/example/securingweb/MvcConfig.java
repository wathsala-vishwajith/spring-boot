package com.example.securingweb;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC Configuration - Configuring Simple View Controllers
 *
 * LESSON 2: Understanding Spring MVC Configuration
 * =================================================
 *
 * @Configuration: Marks this class as a source of bean definitions. Spring will process this class
 *                 and register beans defined within it.
 *
 * WebMvcConfigurer: This is an interface that allows us to customize Spring MVC configuration.
 *                   It provides callback methods (hooks) to customize the Java-based configuration
 *                   for Spring MVC.
 *
 * WHY USE VIEW CONTROLLERS?
 * ==========================
 * View controllers are a shortcut for creating simple controllers that just return a view name.
 * Instead of creating a full controller class with a method like:
 *
 *     @Controller
 *     public class HomeController {
 *         @GetMapping("/")
 *         public String home() {
 *             return "home";
 *         }
 *     }
 *
 * We can register a view controller that does the same thing with less code.
 *
 * WHEN TO USE VIEW CONTROLLERS VS REGULAR CONTROLLERS?
 * =====================================================
 * Use View Controllers when:
 * - You just need to show a static page
 * - No business logic is required
 * - No model data needs to be prepared
 *
 * Use Regular Controllers when:
 * - You need to process form data
 * - You need to fetch data from database
 * - You need to perform business logic
 * - You need to manipulate the model
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Register View Controllers
     *
     * This method is called by Spring during initialization. We use the ViewControllerRegistry
     * to register simple URL-to-view mappings.
     *
     * @param registry The registry to add view controllers to
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        /*
         * MAPPING 1: Home Page
         * URL: "/"
         * View: "home"
         * Effect: When user visits http://localhost:8080/, Spring will render the "home" template.
         *
         * Behind the scenes:
         * 1. User requests "/"
         * 2. Spring MVC's DispatcherServlet receives the request
         * 3. Finds this view controller mapping
         * 4. Looks for a template named "home" (thanks to Thymeleaf, it looks for home.html)
         * 5. Thymeleaf processes the template and returns HTML
         */
        registry.addViewController("/").setViewName("home");

        /*
         * MAPPING 2: Hello Page
         * URL: "/hello"
         * View: "hello"
         * Effect: When user visits http://localhost:8080/hello, Spring will render the "hello" template.
         *
         * This page will be SECURED by Spring Security configuration.
         * Users must log in before they can access this page.
         */
        registry.addViewController("/hello").setViewName("hello");

        /*
         * MAPPING 3: Login Page
         * URL: "/login"
         * View: "login"
         * Effect: When user visits http://localhost:8080/login, Spring will render our custom login template.
         *
         * NOTE: Spring Security automatically provides a /login endpoint, but by registering
         * this view controller, we're telling Spring to use our custom login page instead of
         * the default one.
         */
        registry.addViewController("/login").setViewName("login");
    }

    /*
     * TEACHING NOTE: Other WebMvcConfigurer Methods
     * ==============================================
     *
     * WebMvcConfigurer provides many other customization options. Here are a few examples:
     *
     * addResourceHandlers() - Configure resource handlers for serving static resources
     *     Example: Serving files from /static, /public, /resources, /META-INF/resources
     *
     * addInterceptors() - Add Spring MVC interceptors for pre/post-processing of requests
     *     Example: Logging interceptor, authentication interceptor
     *
     * configureMessageConverters() - Configure message converters for reading/writing HTTP requests/responses
     *     Example: JSON conversion, XML conversion
     *
     * addCorsMappings() - Configure Cross-Origin Resource Sharing (CORS)
     *     Example: Allow requests from specific domains
     *
     * We don't need these for this simple example, but they're commonly used in real applications.
     */
}
