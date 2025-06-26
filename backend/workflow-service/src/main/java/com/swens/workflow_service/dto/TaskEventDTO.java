package com.swens.workflow_service.dto;

import org.apache.kafka.common.protocol.types.Field;

import java.util.List;

public class TaskEventDTO {

    private String taskId;
    private String taskName;
    private List<AssignedUserDTO> assignedUsers;
    private String workflowId;
    private String eventType;
    private String taskStatus;
    private long timestamp;
    private String adminEmail;

    public TaskEventDTO() {
    }

    // Getters and setters

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<AssignedUserDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<AssignedUserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdminEmail() { return adminEmail; }

    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }


    // Inner class for assigned user details
    public static class AssignedUserDTO {
        private String userId;
        private String userName;
        private String email;

        public AssignedUserDTO() {
        }

        // Getters and setters
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
}
