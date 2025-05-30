package com.swens.workflow_service.model;

public class Task {

    private String taskId;
    private String taskName;
    private String assignedUserId;
    private String eventType;   // e.g., TASK_CREATED, TASK_UPDATED
    private String taskStatus;
    private long timestamp;     // epoch millis
    private String workflowId;  // <-- Added field

    public Task() {
    }

    public Task(String taskId, String taskName, String assignedUserId, String eventType, long timestamp, String workflowId) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.assignedUserId = assignedUserId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.workflowId = workflowId;
    }

    // Getters
    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getAssignedUserId() {
        return assignedUserId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    // Setters
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setAssignedUserId(String assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
}
