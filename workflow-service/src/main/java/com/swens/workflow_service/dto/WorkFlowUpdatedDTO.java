package com.swens.workflow_service.dto;

public class WorkFlowUpdatedDTO {

    private String workflowId;
    private String taskId;
    private String userId;
    private String userName;   // Added
    private String userEmail;  // Added

    public WorkFlowUpdatedDTO() {
    }

    public WorkFlowUpdatedDTO(String workflowId, String taskId, String userId, String userName, String userEmail) {
        this.workflowId = workflowId;
        this.taskId = taskId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "WorkFlowUpdatedDTO{" +
                "workflowId='" + workflowId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
