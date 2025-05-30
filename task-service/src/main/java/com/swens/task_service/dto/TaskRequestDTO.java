package com.swens.task_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class TaskRequestDTO {

    @NotBlank(message = "Status must not be blank")
    private String status;

    @NotNull(message = "Due date is required")
    private String dueDate;

    @NotEmpty(message = "At least one assigned user is required")
    private List<@NotNull AssignedUserDTO> assignedUsers;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotBlank(message = "Workflow ID is required")
    private String workflowId; // ✅ NEW FIELD

    @NotNull(message = "Task Name is required")
    private String taskName;


    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    // Getters
    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public List<AssignedUserDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setAssignedUsers(List<AssignedUserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
}
