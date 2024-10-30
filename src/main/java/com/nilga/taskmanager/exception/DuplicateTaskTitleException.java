package com.nilga.taskmanager.exception;

/**
 * Exception thrown when attempting to create or update a task with a title
 * that already exists in the system.
 */
public class DuplicateTaskTitleException extends RuntimeException {

    /**
     * Constructs a new DuplicateTaskTitleException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public DuplicateTaskTitleException(String message) {
        super(message);
    }
}
