package com.swens.workflow_service.mapper;

import com.swens.workflow_service.dto.TaskEventDTO;
import com.swens.workflow_service.dto.WorkFlowUpdatedDTO;
import com.swens.workflow_service.dto.WorkflowResponseDTO;
import com.swens.workflow_service.model.Task;
import com.swens.workflow_service.model.Workflow;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class WorkflowMapper {
    public WorkflowResponseDTO toResponseDTO(Workflow workflow) {
        WorkflowResponseDTO dto = new WorkflowResponseDTO();
        dto.setWorkflowId(workflow.getWorkflowId());
        dto.setCreatedAt(workflow.getCreatedAt());
        dto.setUpdatedAt(workflow.getUpdatedAt());
        return dto;
    }

    public Task mapDtoToTask(TaskEventDTO dto) {
        Task task = new Task();
        task.setTaskId(dto.getTaskId());
        task.setTaskStatus(dto.getTaskStatus());
        task.setWorkflowId(dto.getWorkflowId());
        task.setTaskName(dto.getTaskName());
        List<Task.AssignedUser> assignedUsers = dto.getAssignedUsers().stream().map(userDto -> {
            Task.AssignedUser user = new Task.AssignedUser();
            user.setUserId(userDto.getUserId());
            user.setUserName(userDto.getUserName());
            user.setEmail(userDto.getEmail());
            return user;
        }).collect(Collectors.toList());
        task.setAssignedUsers(assignedUsers);

        return task;
    }

    public List<WorkFlowUpdatedDTO> toWorkFlowUpdatedDTO(Task task) {
        WorkFlowUpdatedDTO dto = new WorkFlowUpdatedDTO();
        dto.setTaskId(task.getTaskId());
        dto.setWorkflowId(task.getWorkflowId());
        dto.setStatus(task.getTaskStatus());
        dto.setCreatedAt(Instant.ofEpochMilli(task.getTimestamp()));
        dto.setUpdatedAt(Instant.ofEpochMilli(task.getTimestamp()));
        dto.setDueDate(Instant.ofEpochMilli(task.getTimestamp()));
        dto.setTaskName(task.getTaskName());

        // Convert assigned users
        List<WorkFlowUpdatedDTO.AssignedUser> assignedUsers = task.getAssignedUsers().stream().map(user ->
                new WorkFlowUpdatedDTO.AssignedUser(
                        user.getUserId(),
                        user.getUserName(),
                        user.getEmail()
                )
        ).collect(Collectors.toList());

        dto.setAssignedUsers(assignedUsers);

        return List.of(dto); // Return a single DTO in a List
    }


}