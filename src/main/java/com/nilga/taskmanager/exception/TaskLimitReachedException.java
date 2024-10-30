package com.nilga.taskmanager.exception;

/**
 * Exception thrown when the maximum limit of tasks is reached.
 */
public class TaskLimitReachedException extends RuntimeException {

    /**
     * Constructs a new TaskLimitReachedException with the specified detail message.
     *
     * @param message the detail message
     */
    public TaskLimitReachedException(String message) {
        super(message);
    }
}
