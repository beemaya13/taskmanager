package com.nilga.taskmanager.service;

import com.nilga.taskmanager.messaging.RabbitMQConfig;
import com.nilga.taskmanager.dto.TaskDto;
import com.nilga.taskmanager.exception.DuplicateTaskTitleException;
import com.nilga.taskmanager.exception.TaskLimitReachedException;
import com.nilga.taskmanager.exception.TaskNotFoundException;
import com.nilga.taskmanager.model.Task;
import com.nilga.taskmanager.model.TaskStatus;
import com.nilga.taskmanager.repository.TaskRepository;
import com.nilga.taskmanager.mapper.TaskMapper;
import com.nilga.taskmanager.telegram.TelegramService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing tasks. Provides functionality for creating, updating,
 * deleting, and retrieving tasks, as well as interacting with external services such as
 * RabbitMQ and Telegram.
 */
@Service
public class TaskService {

    private static final Logger logger = LogManager.getLogger(TaskService.class);
    private static final int MAX_TASKS = 10;

    private final TaskRepository taskRepository;
    private final RabbitTemplate rabbitTemplate;
    private final TelegramService telegramService;
    private final TaskMapper taskMapper = TaskMapper.INSTANCE;

    /**
     * Constructs a new TaskService with the required dependencies.
     *
     * @param taskRepository the repository for accessing task data
     * @param rabbitTemplate the template for interacting with RabbitMQ
     * @param telegramService the service for sending messages to Telegram
     */
    @Autowired
    public TaskService(TaskRepository taskRepository, RabbitTemplate rabbitTemplate, TelegramService telegramService) {
        this.taskRepository = taskRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.telegramService = telegramService;
    }

    /**
     * Creates a new task if it meets certain conditions.
     *
     * @param taskDto the task details
     * @return the ID of the created task
     * @throws TaskLimitReachedException if the maximum number of tasks is reached
     * @throws DuplicateTaskTitleException if a task with the same title already exists
     */
    public Long createTask(TaskDto taskDto) {
        if (taskRepository.count() >= MAX_TASKS) {
            logger.warn("Limit of tasks reached");
            throw new TaskLimitReachedException("Task limit reached. You cannot create more than " + MAX_TASKS + " tasks.");
        }
        if (taskRepository.existsByTitle(taskDto.getTitle())) {
            logger.warn("Duplicate task title detected: {}", taskDto.getTitle());
            throw new DuplicateTaskTitleException("A task with the same title already exists.");
        }

        Task task = taskMapper.toEntity(taskDto);
        Task savedTask = taskRepository.save(task);
        logger.info("Task created with ID: {}", savedTask.getId());

        telegramService.sendMessageToMultipleChats("New task created: " + taskDto.getTitle() + "\nDescription: " + taskDto.getDescription());
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, taskMapper.toDto(savedTask));

        return savedTask.getId();
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to delete
     * @throws TaskNotFoundException if the task is not found
     */
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));

        taskRepository.deleteById(id);
        logger.info("Task deleted with ID: {}", id);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, taskMapper.toDto(task));
    }

    /**
     * Updates the status of a task by its ID.
     *
     * @param id     the ID of the task
     * @param status the new status for the task
     * @throws TaskNotFoundException if the task is not found
     */
    public void updateTaskStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        logger.info("Task status updated for ID: {}", id);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, taskMapper.toDto(updatedTask));
    }

    /**
     * Updates specific fields of a task by its ID.
     *
     * @param id      the ID of the task
     * @param taskDto the task details to update
     * @throws TaskNotFoundException         if the task is not found
     * @throws DuplicateTaskTitleException   if the updated title already exists
     */
    public void updateTaskFields(Long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));

        if (taskDto.getTitle() != null && !taskDto.getTitle().equals(task.getTitle())) {
            if (taskRepository.existsByTitle(taskDto.getTitle())) {
                logger.warn("Duplicate task title detected: {}", taskDto.getTitle());
                throw new DuplicateTaskTitleException("A task with the same title already exists.");
            }
            task.setTitle(taskDto.getTitle());
        }

        if (taskDto.getDescription() != null) {
            task.setDescription(taskDto.getDescription());
        }
        if (taskDto.getStatus() != null) {
            task.setStatus(taskDto.getStatus());
        }

        Task updatedTask = taskRepository.save(task);
        logger.info("Task fields updated for ID: {}", id);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, taskMapper.toDto(updatedTask));
    }

    /**
     * Retrieves a list of all tasks.
     *
     * @return a list of TaskDto representing all tasks
     */
    public List<TaskDto> getAllTasks() {
        List<TaskDto> tasks = taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Retrieved {} tasks", tasks.size());
        return tasks;
    }
}
