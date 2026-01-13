package com.example.taskapi.service;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.model.Task;
import com.example.taskapi.model.TaskStatus;
import com.example.taskapi.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TDD PHASE 2: GREEN - Implement minimal code to make tests pass
 * Service layer for Task operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Create a new task
     */
    public TaskResponse createTask(TaskRequest request) {
        log.debug("Creating new task with title: {}", request.getTitle());

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with id: {}", savedTask.getId());

        return mapToResponse(savedTask);
    }

    /**
     * Get all tasks
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        log.debug("Fetching all tasks");

        List<Task> tasks = taskRepository.findAll();
        log.info("Found {} tasks", tasks.size());

        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get task by ID
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        log.debug("Fetching task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        return mapToResponse(task);
    }

    /**
     * Update an existing task
     */
    public TaskResponse updateTask(Long id, TaskRequest request) {
        log.debug("Updating task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully with id: {}", id);

        return mapToResponse(updatedTask);
    }

    /**
     * Delete a task
     */
    public void deleteTask(Long id) {
        log.debug("Deleting task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        taskRepository.delete(task);
        log.info("Task deleted successfully with id: {}", id);
    }

    /**
     * Get tasks by status
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        log.debug("Fetching tasks with status: {}", status);

        List<Task> tasks = taskRepository.findByStatus(status);
        log.info("Found {} tasks with status: {}", tasks.size(), status);

        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map Task entity to TaskResponse DTO
     */
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
