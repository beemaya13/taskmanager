package com.nilga.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nilga.taskmanager.dto.TaskDto;
import com.nilga.taskmanager.exception.DuplicateTaskTitleException;
import com.nilga.taskmanager.exception.TaskLimitReachedException;
import com.nilga.taskmanager.exception.TaskNotFoundException;
import com.nilga.taskmanager.model.TaskStatus;
import com.nilga.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskDto validTaskDto;

    @BeforeEach
    void setUp() {
        validTaskDto = TaskDto.builder()
                .title("Valid Task")
                .description("A valid task description")
                .status(TaskStatus.NEW)
                .build();
    }

    @Test
    void createTask_success() throws Exception {
        when(taskService.createTask(any(TaskDto.class))).thenReturn(1L);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(taskService, times(1)).createTask(any(TaskDto.class));
    }

    @Test
    void createTask_taskLimitReached() throws Exception {
        doThrow(new TaskLimitReachedException("Task limit reached")).when(taskService).createTask(any(TaskDto.class));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskDto)))
                .andExpect(status().isConflict()) // Ожидаем 409 вместо 400
                .andExpect(content().string("Task limit reached")); // Проверяем текстовый ответ вместо JSON

        verify(taskService, times(1)).createTask(any(TaskDto.class));
    }

    @Test
    void createTask_duplicateTitle() throws Exception {
        doThrow(new DuplicateTaskTitleException("Duplicate task title")).when(taskService).createTask(any(TaskDto.class));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskDto)))
                .andExpect(status().isBadRequest()) // Ожидаем 400 вместо 409
                .andExpect(content().string("Duplicate task title")); // Проверяем текстовый ответ вместо JSON

        verify(taskService, times(1)).createTask(any(TaskDto.class));
    }

    @Test
    void deleteTask_success() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(anyLong());
    }

    @Test
    void deleteTask_notFound() throws Exception {
        doThrow(new TaskNotFoundException("Task not found")).when(taskService).deleteTask(anyLong());

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task not found")); // Проверяем текстовый ответ вместо JSON

        verify(taskService, times(1)).deleteTask(anyLong());
    }

    @Test
    void updateTaskStatus_success() throws Exception {
        mockMvc.perform(put("/api/tasks/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk());

        verify(taskService, times(1)).updateTaskStatus(eq(1L), eq(TaskStatus.IN_PROGRESS));
    }

    @Test
    void updateTaskStatus_notFound() throws Exception {
        doThrow(new TaskNotFoundException("Task not found")).when(taskService).updateTaskStatus(anyLong(), any(TaskStatus.class));

        mockMvc.perform(put("/api/tasks/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task not found")); // Проверяем текстовый ответ вместо JSON

        verify(taskService, times(1)).updateTaskStatus(anyLong(), any(TaskStatus.class));
    }

    @Test
    void updateTaskFields_success() throws Exception {
        mockMvc.perform(patch("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskDto)))
                .andExpect(status().isOk());

        verify(taskService, times(1)).updateTaskFields(eq(1L), any(TaskDto.class));
    }

    @Test
    void updateTaskFields_notFound() throws Exception {
        doThrow(new TaskNotFoundException("Task not found")).when(taskService).updateTaskFields(anyLong(), any(TaskDto.class));

        mockMvc.perform(patch("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task not found")); // Проверяем текстовый ответ вместо JSON

        verify(taskService, times(1)).updateTaskFields(anyLong(), any(TaskDto.class));
    }

    @Test
    void getAllTasks_success() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(validTaskDto));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is(validTaskDto.getTitle())));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void getAllTasks_empty() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskService, times(1)).getAllTasks();
    }
}
