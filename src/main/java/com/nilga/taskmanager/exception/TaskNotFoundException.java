package com.nilga.taskmanager.exception;

/**
 * Exception thrown when a requested task is not found.
 */
public class TaskNotFoundException extends RuntimeException {

    /**
     * Constructs a new TaskNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public TaskNotFoundException(String message) {
        super(message);
    }
}
