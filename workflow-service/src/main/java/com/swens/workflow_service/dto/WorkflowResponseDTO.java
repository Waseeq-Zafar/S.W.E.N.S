package com.swens.workflow_service.dto;

public class WorkflowResponseDTO {


    private String workflowId;
    private long createdAt;
    private long updatedAt;
    private int completionPercentage;




    public WorkflowResponseDTO() {
    }

    public WorkflowResponseDTO(String workflowId, long createdAt, long updatedAt, int completionPercentage) {
        this.workflowId = workflowId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completionPercentage = completionPercentage;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCompletionPercentage() { return completionPercentage; }

    public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }


}
