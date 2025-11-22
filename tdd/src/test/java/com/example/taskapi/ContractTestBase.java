package com.example.taskapi;

import com.example.taskapi.controller.TaskController;
import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.model.TaskStatus;
import com.example.taskapi.service.ResourceNotFoundException;
import com.example.taskapi.service.TaskService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Base class for Spring Cloud Contract tests
 * Contract tests verify the API contract between producer and consumer
 */
@WebMvcTest(TaskController.class)
public abstract class ContractTestBase {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private TaskService taskService;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);

        // Mock responses for contract tests
        TaskResponse sampleTask = TaskResponse.builder()
                .id(1L)
                .title("Sample Task")
                .description("Sample Description")
                .status(TaskStatus.TODO)
                .createdAt(LocalDateTime.parse("2024-01-01T10:00:00"))
                .updatedAt(LocalDateTime.parse("2024-01-01T10:00:00"))
                .build();

        // GET /api/tasks
        when(taskService.getAllTasks())
                .thenReturn(Collections.singletonList(sampleTask));

        // POST /api/tasks
        TaskResponse newTask = TaskResponse.builder()
                .id(1L)
                .title("New Task")
                .description("New Description")
                .status(TaskStatus.TODO)
                .createdAt(LocalDateTime.parse("2024-01-01T10:00:00"))
                .updatedAt(LocalDateTime.parse("2024-01-01T10:00:00"))
                .build();

        when(taskService.createTask(any(TaskRequest.class)))
                .thenReturn(newTask);

        // GET /api/tasks/1
        when(taskService.getTaskById(1L))
                .thenReturn(sampleTask);

        // GET /api/tasks/999 (not found)
        when(taskService.getTaskById(999L))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 999"));
    }
}
