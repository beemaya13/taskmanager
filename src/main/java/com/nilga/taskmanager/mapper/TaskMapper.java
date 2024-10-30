package com.nilga.taskmanager.mapper;

import com.nilga.taskmanager.dto.TaskDto;
import com.nilga.taskmanager.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between Task and TaskDto objects.
 * This interface provides mapping methods to transform Task entities
 * to TaskDto and vice versa.
 */
@Mapper
public interface TaskMapper {

    /**
     * Instance of the TaskMapper to be used for mapping operations.
     */
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    /**
     * Converts a Task entity to a TaskDto.
     *
     * @param task the Task entity to be converted
     * @return the converted TaskDto
     */
    TaskDto toDto(Task task);

    /**
     * Converts a TaskDto to a Task entity.
     *
     * @param taskDto the TaskDto to be converted
     * @return the converted Task entity
     */
    Task toEntity(TaskDto taskDto);
}
