package com.swens.task_service.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "tasks")
public class Task {

    @Id
    private String taskId;

    @NotNull(message = "Status is required")
    private String status; // e.g., PENDING, COMPLETED

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @Valid
    @Size(min = 1, message = "At least one user must be assigned")
    private List<AssignedUser> assignedUsers;

    @NotNull(message = "CreatedAt cannot be null")
    private Instant createdAt;

    @NotNull(message = "UpdatedAt cannot be null")
    private Instant updatedAt;

    @NotNull(message = "Due date cannot be null")
    @FutureOrPresent(message = "Due date must be present or future")
    private Instant dueDate;

    @NotBlank(message = "Workflow ID is required")
    private String workflowId;  // ✅ NEW FIELD

    @NotNull(message = "Task Name is required")
    private String taskName;

    @NotNull(message = "Admin Email ID is required")
    private String adminEmail;



    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }




    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public static class AssignedUser {
        public AssignedUser(String userId, String userName, String email) {
            this.userId = userId;
            this.userName = userName;
            this.email = email;
        }

        @NotBlank(message = "User ID is required")
        private String userId;

        @NotBlank(message = "User name is required")
        private String userName;

        @NotBlank(message = "Email is required")
        private String email;


        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    // Getters
    public String getTaskId() { return taskId; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public List<AssignedUser> getAssignedUsers() { return assignedUsers; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getDueDate() { return dueDate; }
    public String getWorkflowId() { return workflowId; }  // ✅ Getter

    // Setters
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setAssignedUsers(List<AssignedUser> assignedUsers) { this.assignedUsers = assignedUsers; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setDueDate(Instant dueDate) { this.dueDate = dueDate; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }  // ✅ Setter
}
