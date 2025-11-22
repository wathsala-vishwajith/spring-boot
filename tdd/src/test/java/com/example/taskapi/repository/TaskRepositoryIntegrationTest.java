package com.example.taskapi.repository;

import com.example.taskapi.model.Task;
import com.example.taskapi.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for TaskRepository using TestContainers
 * Tests run against a real PostgreSQL database in Docker container
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("Task Repository Integration Tests with TestContainers")
class TaskRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TaskRepository taskRepository;

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        task1 = Task.builder()
                .title("Task 1")
                .description("Description 1")
                .status(TaskStatus.TODO)
                .build();

        task2 = Task.builder()
                .title("Task 2")
                .description("Description 2")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        task3 = Task.builder()
                .title("Task 3")
                .description("Description 3")
                .status(TaskStatus.TODO)
                .build();
    }

    @Test
    @DisplayName("Should establish connection to PostgreSQL container")
    void shouldEstablishConnection() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    @DisplayName("Should save task to database")
    void shouldSaveTask() {
        // When
        Task savedTask = taskRepository.save(task1);

        // Then
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Task 1");
        assertThat(savedTask.getDescription()).isEqualTo("Description 1");
        assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(savedTask.getCreatedAt()).isNotNull();
        assertThat(savedTask.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find task by ID")
    void shouldFindTaskById() {
        // Given
        Task savedTask = taskRepository.save(task1);

        // When
        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());

        // Then
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Task 1");
    }

    @Test
    @DisplayName("Should return empty when task not found")
    void shouldReturnEmptyWhenTaskNotFound() {
        // When
        Optional<Task> foundTask = taskRepository.findById(999L);

        // Then
        assertThat(foundTask).isEmpty();
    }

    @Test
    @DisplayName("Should find all tasks")
    void shouldFindAllTasks() {
        // Given
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);

        // When
        List<Task> tasks = taskRepository.findAll();

        // Then
        assertThat(tasks).hasSize(3);
        assertThat(tasks).extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Task 1", "Task 2", "Task 3");
    }

    @Test
    @DisplayName("Should find tasks by status")
    void shouldFindTasksByStatus() {
        // Given
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);

        // When
        List<Task> todoTasks = taskRepository.findByStatus(TaskStatus.TODO);
        List<Task> inProgressTasks = taskRepository.findByStatus(TaskStatus.IN_PROGRESS);

        // Then
        assertThat(todoTasks).hasSize(2);
        assertThat(todoTasks).extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Task 1", "Task 3");

        assertThat(inProgressTasks).hasSize(1);
        assertThat(inProgressTasks.get(0).getTitle()).isEqualTo("Task 2");
    }

    @Test
    @DisplayName("Should update task")
    void shouldUpdateTask() {
        // Given
        Task savedTask = taskRepository.save(task1);
        Long taskId = savedTask.getId();

        // When
        savedTask.setTitle("Updated Title");
        savedTask.setStatus(TaskStatus.DONE);
        taskRepository.save(savedTask);

        // Then
        Optional<Task> updatedTask = taskRepository.findById(taskId);
        assertThat(updatedTask).isPresent();
        assertThat(updatedTask.get().getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTask.get().getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(updatedTask.get().getUpdatedAt()).isAfter(updatedTask.get().getCreatedAt());
    }

    @Test
    @DisplayName("Should delete task")
    void shouldDeleteTask() {
        // Given
        Task savedTask = taskRepository.save(task1);
        Long taskId = savedTask.getId();

        // When
        taskRepository.delete(savedTask);

        // Then
        Optional<Task> deletedTask = taskRepository.findById(taskId);
        assertThat(deletedTask).isEmpty();
    }

    @Test
    @DisplayName("Should count tasks correctly")
    void shouldCountTasks() {
        // Given
        taskRepository.save(task1);
        taskRepository.save(task2);

        // When
        long count = taskRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }
}
