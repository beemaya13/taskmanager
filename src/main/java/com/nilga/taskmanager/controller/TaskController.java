package com.nilga.taskmanager.controller;

import com.nilga.taskmanager.dto.TaskDto;
import com.nilga.taskmanager.model.TaskStatus;
import com.nilga.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing tasks in the Task Manager application.
 * Provides endpoints for creating, deleting, updating, and retrieving tasks.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "API for managing tasks")
public class TaskController {

    private final TaskService taskService;

    /**
     * Constructor for {@link TaskController}.
     *
     * @param taskService the {@link TaskService} instance used to handle task-related operations
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Creates a new task with the provided details.
     *
     * @param taskDto the task details
     * @return a {@link ResponseEntity} containing the ID of the created task and HTTP status 201 (Created)
     */
    @Operation(summary = "Create a new task", description = "Creates a new task with provided details")
    @PostMapping
    public ResponseEntity<Long> createTask(@Valid @RequestBody TaskDto taskDto) {
        Long taskId = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskId);
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to be deleted
     * @return a {@link ResponseEntity} with HTTP status 204 (No Content)
     */
    @Operation(summary = "Delete a task", description = "Deletes a task by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "ID of the task to be deleted") @PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the status of a task by its ID.
     *
     * @param id     the ID of the task
     * @param status the new status of the task
     * @return a {@link ResponseEntity} with HTTP status 200 (OK)
     */
    @Operation(summary = "Update task status", description = "Updates the status of a task by its ID")
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateTaskStatus(
            @Parameter(description = "ID of the task") @PathVariable Long id,
            @Parameter(description = "New status of the task") @RequestParam TaskStatus status) {
        taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates specific fields of a task by its ID.
     *
     * @param id      the ID of the task
     * @param taskDto the updated task details
     * @return a {@link ResponseEntity} with HTTP status 200 (OK)
     */
    @Operation(summary = "Update task fields", description = "Updates specific fields of a task by its ID")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateTaskFields(
            @Parameter(description = "ID of the task") @PathVariable Long id,
            @Valid @RequestBody TaskDto taskDto) {
        taskService.updateTaskFields(id, taskDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves a list of all tasks.
     *
     * @return a {@link ResponseEntity} containing the list of all tasks and HTTP status 200 (OK)
     */
    @Operation(summary = "Get all tasks", description = "Retrieves a list of all tasks")
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
}
