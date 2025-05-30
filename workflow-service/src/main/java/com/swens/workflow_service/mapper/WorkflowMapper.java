package com.swens.workflow_service.mapper;

import com.swens.workflow_service.dto.TaskEventDto;
import com.swens.workflow_service.dto.WorkflowResponseDTO;
import com.swens.workflow_service.model.Task;
import com.swens.workflow_service.model.Workflow;
import org.springframework.stereotype.Component;



@Component
public class WorkflowMapper {
    public WorkflowResponseDTO toResponseDTO(Workflow workflow) {
        WorkflowResponseDTO dto = new WorkflowResponseDTO();
        dto.setWorkflowId(workflow.getWorkflowId());
        dto.setCreatedAt(workflow.getCreatedAt());
        dto.setUpdatedAt(workflow.getUpdatedAt());
        return dto;
    }

    public Task mapDtoToTask(TaskEventDto taskEventDto) {
        Task task = new Task();
        task.setTaskId(taskEventDto.getTaskId());
        task.setTaskName(taskEventDto.getTaskName());
        task.setAssignedUserId(taskEventDto.getAssignedUserId());
        task.setEventType(taskEventDto.getEventType());
        task.setTaskStatus(taskEventDto.getTaskStatus());
        task.setTimestamp(taskEventDto.getTimestamp());
        task.setWorkflowId(taskEventDto.getWorkflowId());
        return task;
    }
}