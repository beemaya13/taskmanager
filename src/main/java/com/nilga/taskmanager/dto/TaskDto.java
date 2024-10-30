package com.nilga.taskmanager.dto;

import com.nilga.taskmanager.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for representing a task.
 * Used to transfer task data between different layers of the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    /**
     * The unique identifier of the task.
     */
    private Long id;

    /**
     * The title of the task.
     * This field is mandatory and must contain between 3 and 100 characters.
     */
    @NotNull(message = "Title cannot be null")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    /**
     * The description of the task.
     * This field is optional and can contain up to 250 characters.
     */
    @Size(max = 250, message = "Description must be up to 250 characters")
    private String description;

    /**
     * The status of the task, represented by {@link TaskStatus}.
     */
    private TaskStatus status;
}
