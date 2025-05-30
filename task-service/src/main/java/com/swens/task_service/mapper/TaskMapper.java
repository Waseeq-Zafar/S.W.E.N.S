package com.swens.task_service.mapper;

import com.swens.task_service.dto.AssignedUserDTO;
import com.swens.task_service.dto.TaskRequestDTO;
import com.swens.task_service.dto.TaskResponseDTO;
import com.swens.task_service.dto.TaskUpdateDTO;
import com.swens.task_service.model.Task;
import com.swens.task_service.util.TimeUtil;

import java.util.stream.Collectors;

public class TaskMapper {

    // DTO (with String dueDate) -> Entity (Instant dueDate)
    public static Task toEntity(TaskRequestDTO dto) {
        Task task = new Task();
        task.setStatus(dto.getStatus());
        task.setDescription(dto.getDescription());
        task.setAssignedUsers(
                dto.getAssignedUsers() != null
                        ? dto.getAssignedUsers().stream()
                        .map(user -> new Task.AssignedUser(
                                user.getUserId(),
                                user.getUserName(),
                                user.getEmail()
                                ))
                        .collect(Collectors.toList())
                        : null
        );
        task.setCreatedAt(TimeUtil.nowUTC());
        task.setUpdatedAt(TimeUtil.nowUTC());
        task.setWorkflowId(dto.getWorkflowId());

        if (dto.getDueDate() != null && !dto.getDueDate().isBlank()) {
            task.setDueDate(TimeUtil.parseISTToInstant(dto.getDueDate()));
        }

        return task;
    }

    // Entity -> DTO (Instant -> formatted String)
    public static TaskResponseDTO toDTO(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setTaskId(task.getTaskId());
        dto.setStatus(task.getStatus());
        dto.setDescription(task.getDescription());
        dto.setAssignedUsers(
                task.getAssignedUsers() != null
                        ? task.getAssignedUsers().stream()
                        .map(user -> new AssignedUserDTO(user.getUserId(),
                                user.getUserName(),
                                user.getEmail()))
                        .collect(Collectors.toList())
                        : null
        );
        dto.setCreatedAt(TimeUtil.formatInstantToIST(task.getCreatedAt()));
        dto.setUpdatedAt(TimeUtil.formatInstantToIST(task.getUpdatedAt()));
        dto.setDueDate(TimeUtil.formatInstantToIST(task.getDueDate()));
        dto.setWorkflowId(task.getWorkflowId());

        return dto;
    }

    // Update entity with update DTO (with String dueDate)
    public static void updateEntity(Task task, TaskUpdateDTO dto) {
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            task.setStatus(dto.getStatus());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            task.setDescription(dto.getDescription());
        }
        if (dto.getDueDate() != null && !dto.getDueDate().isBlank()) {
            task.setDueDate(TimeUtil.parseISTToInstant(dto.getDueDate()));
        }
        if (dto.getAssignedUsers() != null && !dto.getAssignedUsers().isEmpty()) {
            task.setAssignedUsers(
                    dto.getAssignedUsers().stream()
                            .map(user -> new Task.AssignedUser(
                                    user.getUserId(),
                                    user.getUserName(),
                                    user.getEmail()
                                    ))
                            .collect(Collectors.toList())
            );
        }
        task.setUpdatedAt(TimeUtil.nowUTC());
    }
}
