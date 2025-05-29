package com.swens.workflow_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "workflows")
public class Workflow {

    @Id
    private String id;  // MongoDB internal ID (ObjectId)

    private String workflowId;  // Custom ID like "workflow-001"
    private long createdAt;
    private long updatedAt;
    private List<Task> tasks;

    public Workflow() {
    }

    public Workflow(String workflowId) {
        this.workflowId = workflowId;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Workflow(String workflowId, List<Task> tasks, long createdAt, long updatedAt) {
        this.workflowId = workflowId;
        this.tasks = tasks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
