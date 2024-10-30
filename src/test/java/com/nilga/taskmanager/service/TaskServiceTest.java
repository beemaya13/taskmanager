package com.nilga.taskmanager.service;

import com.nilga.taskmanager.dto.TaskDto;
import com.nilga.taskmanager.exception.DuplicateTaskTitleException;
import com.nilga.taskmanager.exception.TaskLimitReachedException;
import com.nilga.taskmanager.exception.TaskNotFoundException;
import com.nilga.taskmanager.model.Task;
import com.nilga.taskmanager.model.TaskStatus;
import com.nilga.taskmanager.repository.TaskRepository;
import com.nilga.taskmanager.mapper.TaskMapper;
import com.nilga.taskmanager.telegram.TelegramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private TelegramService telegramService;

    @InjectMocks
    private TaskService taskService;

    private TaskDto sampleTaskDto;
    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTaskDto = TaskDto.builder()
                .title("New Task")
                .description("Description")
                .status(TaskStatus.NEW)
                .build();

        sampleTask = TaskMapper.INSTANCE.toEntity(sampleTaskDto);
        sampleTask.setId(1L);
    }

    @Test
    void createTask_Success() {
        when(taskRepository.count()).thenReturn(5L);
        when(taskRepository.existsByTitle(sampleTaskDto.getTitle())).thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Long createdTaskId = taskService.createTask(sampleTaskDto);

        assertNotNull(createdTaskId);
        assertEquals(sampleTask.getId(), createdTaskId);
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), any(TaskDto.class));
        verify(telegramService, times(1)).sendMessageToMultipleChats(anyString());
    }

    @Test
    void createTask_TaskLimitReached() {
        when(taskRepository.count()).thenReturn(10L);

        assertThrows(TaskLimitReachedException.class, () -> taskService.createTask(sampleTaskDto));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(TaskDto.class));
        verify(telegramService, never()).sendMessageToMultipleChats(anyString());
    }

    @Test
    void createTask_DuplicateTaskTitle() {
        when(taskRepository.count()).thenReturn(5L);
        when(taskRepository.existsByTitle(sampleTaskDto.getTitle())).thenReturn(true);

        assertThrows(DuplicateTaskTitleException.class, () -> taskService.createTask(sampleTaskDto));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(TaskDto.class));
        verify(telegramService, never()).sendMessageToMultipleChats(anyString());
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.findById(sampleTask.getId())).thenReturn(Optional.of(sampleTask));

        taskService.deleteTask(sampleTask.getId());

        verify(taskRepository, times(1)).deleteById(sampleTask.getId());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), any(TaskDto.class));
    }

    @Test
    void deleteTask_TaskNotFound() {
        when(taskRepository.findById(sampleTask.getId())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(sampleTask.getId()));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(TaskDto.class));
    }

    @Test
    void updateTaskStatus_Success() {
        when(taskRepository.findById(sampleTask.getId())).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        taskService.updateTaskStatus(sampleTask.getId(), TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, sampleTask.getStatus());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), any(TaskDto.class));
    }

    @Test
    void updateTaskStatus_TaskNotFound() {
        when(taskRepository.findById(sampleTask.getId())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskStatus(sampleTask.getId(), TaskStatus.IN_PROGRESS));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(TaskDto.class));
    }

    @Test
    void updateTaskFields_Success() {
        TaskDto updatedTaskDto = TaskDto.builder()
                .title("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.COMPLETED)
                .build();

        when(taskRepository.findById(sampleTask.getId())).thenReturn(Optional.of(sampleTask));
        when(taskRepository.existsByTitle(updatedTaskDto.getTitle())).thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        taskService.updateTaskFields(sampleTask.getId(), updatedTaskDto);

        assertEquals("Updated Task", sampleTask.getTitle());
        assertEquals("Updated Description", sampleTask.getDescription());
        assertEquals(TaskStatus.COMPLETED, sampleTask.getStatus());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), any(TaskDto.class));
    }

    @Test
    void updateTaskFields_TaskNotFound() {
        when(taskRepository.findById(sampleTask.getId())).thenReturn(Optional.empty());

        TaskDto updatedTaskDto = TaskDto.builder()
                .title("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.COMPLETED)
                .build();

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskFields(sampleTask.getId(), updatedTaskDto));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(TaskDto.class));
    }

    @Test
    void updateTaskFields_DuplicateTitle() {
        TaskDto updatedTaskDto = TaskDto.builder()
                .title("Updated Task")
                .build();

        when(taskRepository.findById(sampleTask.getId())).thenReturn(Optional.of(sampleTask));
        when(taskRepository.existsByTitle(updatedTaskDto.getTitle())).thenReturn(true);

        assertThrows(DuplicateTaskTitleException.class, () -> taskService.updateTaskFields(sampleTask.getId(), updatedTaskDto));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(TaskDto.class));
    }

    @Test
    void getAllTasks_ReturnsTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        List<TaskDto> tasks = taskService.getAllTasks();

        assertEquals(1, tasks.size());
        assertEquals(sampleTask.getTitle(), tasks.get(0).getTitle());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(TaskDto.class));
    }
}
