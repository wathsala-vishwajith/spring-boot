package com.example.taskapi.repository;

import com.example.taskapi.model.Task;
import com.example.taskapi.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Task entity
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find tasks by status
     * @param status the task status to filter by
     * @return list of tasks with the given status
     */
    List<Task> findByStatus(TaskStatus status);
}
