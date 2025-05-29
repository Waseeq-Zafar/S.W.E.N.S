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

    public static class AssignedUser {
        public AssignedUser(String userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        @NotBlank(message = "User ID is required")
        private String userId;

        @NotBlank(message = "User name is required")
        private String userName;
    }


    public String getStatus() {
        return status;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public List<AssignedUser> getAssignedUsers() {
        return assignedUsers;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDueDate() {
        return dueDate;
    }


    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssignedUsers(List<AssignedUser> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }


}
