package com.swens.workflow_service.dto;

import java.time.Instant;
import java.util.List;

public class WorkFlowUpdatedDTO {

    private String taskId;
    private String status;
    private List<AssignedUser> assignedUsers;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant dueDate;
    private String workflowId;
    private String taskName;
    private String adminEmail;


    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public static class AssignedUser {
        private String userId;
        private String userName;
        private String email;

        public AssignedUser() {}

        public AssignedUser(String userId, String userName, String email) {
            this.userId = userId;
            this.userName = userName;
            this.email = email;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    // Getters & Setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public List<AssignedUser> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<AssignedUser> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
}
