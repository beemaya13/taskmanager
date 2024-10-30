package com.nilga.taskmanager.messaging;


import static org.awaitility.Awaitility.await;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import com.nilga.taskmanager.dto.TaskDto;
import com.nilga.taskmanager.model.TaskStatus;
import com.nilga.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TaskServiceIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private TaskService taskService;

    @Test
    public void testSendMessageToQueue() {
        rabbitAdmin.purgeQueue("task_queue", true);

        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Test Task");
        taskDto.setDescription("This is a test task");
        taskDto.setStatus(TaskStatus.NEW);

        taskService.createTask(taskDto);

        TaskDto receivedTaskDto = await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> (TaskDto) rabbitTemplate.receiveAndConvert("task_queue"), Objects::nonNull);

        assertNotNull(receivedTaskDto, "Message was not received in the queue within the expected time");
    }
}
