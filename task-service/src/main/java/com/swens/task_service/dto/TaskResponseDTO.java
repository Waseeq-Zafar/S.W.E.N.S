package com.swens.task_service.dto;

import java.util.List;

public class TaskResponseDTO {

    private String taskId;
    private String description;
    private String status;
    private String dueDate;      // changed from Instant to String
    private List<AssignedUserDTO> assignedUsers;
    private String createdAt;    // changed from Instant to String
    private String updatedAt;    // changed from Instant to String
    private String workflowId;  // <-- Added this field
    private List<String> unavailableUsers;
    private String taskName;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }



    // Getters
    public String getStatus() {
        return status;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<AssignedUserDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setUnavailableUsers(List<String> unavailableUsers) {
        this.unavailableUsers = unavailableUsers;
    }



    // Setters
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setAssignedUsers(List<AssignedUserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public List<String> getUnavailableUsers() {
        return unavailableUsers;
    }



}
