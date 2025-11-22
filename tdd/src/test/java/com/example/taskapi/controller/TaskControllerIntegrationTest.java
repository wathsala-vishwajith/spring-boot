package com.example.taskapi.controller;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.model.Task;
import com.example.taskapi.model.TaskStatus;
import com.example.taskapi.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full integration tests for TaskController using TestContainers
 * Tests the complete REST API with real database
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Task Controller Integration Tests")
class TaskControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create new task via POST /api/tasks")
    void shouldCreateTask() throws Exception {
        // Given
        TaskRequest request = TaskRequest.builder()
                .title("New Task")
                .description("Task Description")
                .build();

        // When & Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Task Description"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("Should return validation error for invalid task")
    void shouldReturnValidationError() throws Exception {
        // Given
        TaskRequest request = TaskRequest.builder()
                .title("AB") // Too short
                .build();

        // When & Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    @DisplayName("Should get all tasks via GET /api/tasks")
    void shouldGetAllTasks() throws Exception {
        // Given
        taskRepository.save(Task.builder()
                .title("Task 1")
                .description("Description 1")
                .status(TaskStatus.TODO)
                .build());

        taskRepository.save(Task.builder()
                .title("Task 2")
                .description("Description 2")
                .status(TaskStatus.IN_PROGRESS)
                .build());

        // When & Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Task 1")))
                .andExpect(jsonPath("$[1].title", is("Task 2")));
    }

    @Test
    @DisplayName("Should get tasks by status via GET /api/tasks?status=TODO")
    void shouldGetTasksByStatus() throws Exception {
        // Given
        taskRepository.save(Task.builder()
                .title("Task 1")
                .status(TaskStatus.TODO)
                .build());

        taskRepository.save(Task.builder()
                .title("Task 2")
                .status(TaskStatus.DONE)
                .build());

        taskRepository.save(Task.builder()
                .title("Task 3")
                .status(TaskStatus.TODO)
                .build());

        // When & Then
        mockMvc.perform(get("/api/tasks")
                        .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].status", everyItem(is("TODO"))));
    }

    @Test
    @DisplayName("Should get task by ID via GET /api/tasks/{id}")
    void shouldGetTaskById() throws Exception {
        // Given
        Task task = taskRepository.save(Task.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .build());

        // When & Then
        mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @DisplayName("Should return 404 when task not found")
    void shouldReturn404WhenTaskNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tasks/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Task not found with id: 999"));
    }

    @Test
    @DisplayName("Should update task via PUT /api/tasks/{id}")
    void shouldUpdateTask() throws Exception {
        // Given
        Task task = taskRepository.save(Task.builder()
                .title("Original Title")
                .description("Original Description")
                .status(TaskStatus.TODO)
                .build());

        TaskRequest updateRequest = TaskRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .status(TaskStatus.DONE)
                .build();

        // When & Then
        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @DisplayName("Should delete task via DELETE /api/tasks/{id}")
    void shouldDeleteTask() throws Exception {
        // Given
        Task task = taskRepository.save(Task.builder()
                .title("Task to Delete")
                .status(TaskStatus.TODO)
                .build());

        // When & Then
        mockMvc.perform(delete("/api/tasks/{id}", task.getId()))
                .andExpect(status().isNoContent());

        // Verify task is deleted
        mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle complete CRUD lifecycle")
    void shouldHandleCompleteCRUDLifecycle() throws Exception {
        // Create
        TaskRequest createRequest = TaskRequest.builder()
                .title("Lifecycle Task")
                .description("Testing CRUD")
                .build();

        String createResponse = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        TaskResponse created = objectMapper.readValue(createResponse, TaskResponse.class);

        // Read
        mockMvc.perform(get("/api/tasks/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Lifecycle Task"));

        // Update
        TaskRequest updateRequest = TaskRequest.builder()
                .title("Updated Lifecycle Task")
                .description("Updated CRUD")
                .status(TaskStatus.DONE)
                .build();

        mockMvc.perform(put("/api/tasks/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Lifecycle Task"));

        // Delete
        mockMvc.perform(delete("/api/tasks/{id}", created.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/tasks/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }
}
