package com.swens.task_service.dto;


import java.time.Instant;
import java.util.List;


public class TaskUpdateDTO {

    // Optional fields for update
    private String status;

    private String description;

    private List<AssignedUserDTO> assignedUsers;

    private String dueDate;


    public void setStatus(String status) {
        this.status = status;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssignedUsers(List<AssignedUserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }


    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public List<AssignedUserDTO> getAssignedUsers() {
        return assignedUsers;
    }

}
