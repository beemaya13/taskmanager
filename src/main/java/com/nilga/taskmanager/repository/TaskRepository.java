package com.nilga.taskmanager.repository;

import com.nilga.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Task entities in the database.
 * Extends JpaRepository to provide CRUD operations and additional query methods.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Checks if a task with the specified title already exists in the database.
     *
     * @param title the title of the task to check for existence
     * @return true if a task with the specified title exists, false otherwise
     */
    boolean existsByTitle(String title);
}
