package com.swens.notification_service.model;

import org.apache.kafka.common.protocol.types.Field;

import java.util.List;

public class TaskEventModel {
    private String taskId;
    private String taskName;
    private List<AssignedUser> assignedUsers;
    private String workflowId;
    private String eventType;
    private String taskStatus;
    private long timestamp;
    private String adminEmail;

    public TaskEventModel() {
    }

    public TaskEventModel(String taskId, String taskName, List<AssignedUser> assignedUsers,
                          String workflowId, String eventType, String taskStatus, long timestamp, String adminEmail) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.assignedUsers = assignedUsers;
        this.workflowId = workflowId;
        this.eventType = eventType;
        this.taskStatus = taskStatus;
        this.timestamp = timestamp;
        this.adminEmail = adminEmail;
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

    public List<AssignedUser> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<AssignedUser> assignedUsers) {
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

    @Override
    public String toString() {
        return "TaskEventModel{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", assignedUsers=" + assignedUsers +
                ", workflowId='" + workflowId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", timestamp=" + timestamp +
                ", adminEmail='" + adminEmail + '\'' +
                '}';
    }

    // ✅ Inner static class for AssignedUser
    public static class AssignedUser {
        private String userId;
        private String userName;
        private String email;

        public AssignedUser() {
        }

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

        @Override
        public String toString() {
            return "AssignedUser{" +
                    "userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }
}
