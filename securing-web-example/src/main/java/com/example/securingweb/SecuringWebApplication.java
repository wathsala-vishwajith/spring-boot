package com.example.securingweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class - The Entry Point
 *
 * LESSON 1: Understanding @SpringBootApplication
 * ================================================
 *
 * The @SpringBootApplication annotation is a convenience annotation that combines three important annotations:
 *
 * 1. @Configuration: Indicates that this class can be used by the Spring IoC container as a source of bean definitions.
 *
 * 2. @EnableAutoConfiguration: Tells Spring Boot to automatically configure your application based on the dependencies
 *    you have added. For example:
 *    - Since we have spring-boot-starter-web, it will auto-configure Tomcat and Spring MVC
 *    - Since we have spring-boot-starter-security, it will auto-configure Spring Security
 *
 * 3. @ComponentScan: Tells Spring to scan this package and all sub-packages for components, services,
 *    configurations, controllers, etc.
 *
 * WHAT HAPPENS WHEN THIS APPLICATION STARTS?
 * ===========================================
 * 1. Spring Boot creates an ApplicationContext (container for all beans)
 * 2. Scans for components in com.example.securingweb and sub-packages
 * 3. Auto-configures embedded Tomcat server
 * 4. Auto-configures Spring Security with default settings
 * 5. Registers all our controllers, configurations, and services as beans
 * 6. Starts the embedded Tomcat server (default port: 8080)
 *
 * HOW TO RUN THIS APPLICATION:
 * ============================
 * From command line: mvn spring-boot:run
 * From IDE: Right-click and run as Java Application
 * As JAR: mvn package && java -jar target/securing-web-0.0.1-SNAPSHOT.jar
 */
@SpringBootApplication
public class SecuringWebApplication {

    /**
     * The main method - where Java applications start
     *
     * SpringApplication.run() does the following:
     * 1. Creates an ApplicationContext appropriate for your application
     * 2. Registers a CommandLinePropertySource to expose command line arguments as Spring properties
     * 3. Refreshes the ApplicationContext, loading all singleton beans
     * 4. Triggers any CommandLineRunner beans
     */
    public static void main(String[] args) {
        SpringApplication.run(SecuringWebApplication.class, args);
    }
}
