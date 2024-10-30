package com.nilga.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler that intercepts and handles exceptions thrown by controllers.
 * Provides standardized responses for various types of exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles TaskNotFoundException, which is thrown when a requested task is not found.
     *
     * @param ex the TaskNotFoundException
     * @return a 404 Not Found response with the exception message
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFound(TaskNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles DuplicateTaskTitleException, which is thrown when a task with a duplicate title is created.
     *
     * @param ex the DuplicateTaskTitleException
     * @return a 400 Bad Request response with the exception message
     */
    @ExceptionHandler(DuplicateTaskTitleException.class)
    public ResponseEntity<String> handleDuplicateTaskTitle(DuplicateTaskTitleException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles TaskLimitReachedException, which is thrown when the maximum allowed tasks limit is reached.
     *
     * @param ex the TaskLimitReachedException
     * @return a 409 Conflict response with the exception message
     */
    @ExceptionHandler(TaskLimitReachedException.class)
    public ResponseEntity<String> handleTaskLimitReached(TaskLimitReachedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Handles validation errors thrown when request arguments are invalid, such as failing @Valid checks.
     *
     * @param ex the MethodArgumentNotValidException
     * @return a 400 Bad Request response with a summary of the validation error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationError(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation failed: " + ex.getMessage());
    }

    /**
     * Handles all other exceptions not specifically handled by other exception handlers.
     *
     * @param ex the Exception
     * @return a 500 Internal Server Error response with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }
}
