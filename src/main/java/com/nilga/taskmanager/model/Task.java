package com.nilga.taskmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a task in the Task Manager application.
 * Each task has an ID, title, description, and status.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    /**
     * Unique identifier for the task, auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Title of the task. Cannot be null.
     */
    @NotNull
    private String title;

    /**
     * Optional description of the task.
     */
    private String description;

    /**
     * Status of the task, represented as a string enumeration.
     */
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

}
