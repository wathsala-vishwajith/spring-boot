package com.example.oauth2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for PublicController endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PublicControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPublicHelloEndpoint() throws Exception {
        mockMvc.perform(get("/api/public/hello"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Hello! This is a public endpoint."));
    }

    @Test
    public void testPublicInfoEndpoint() throws Exception {
        mockMvc.perform(get("/api/public/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Spring Boot OAuth2 JWT Example"))
            .andExpect(jsonPath("$.version").value("1.0.0"));
    }
}
